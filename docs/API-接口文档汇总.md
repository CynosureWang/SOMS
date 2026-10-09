# SOMS 接口文档汇总（含接口测试实例）

> **适用范围**：已实现并可测试的全部接口（网关 / 认证 / 后台管理 / 客户管理 / 内部契约）
> **文档日期**：2026-09-20
> **本文档以源码为准**，与旧版各服务文档存在差异处已在文中标注。

---

## 1. 通用约定

### 1.1 统一入口（网关）

所有外部请求**只走网关**：

```
Base URL: http://localhost
```

| 服务 | 路由前缀 | 直连端口（仅内网调试） |
|---|---|---|
| service-auth | `/api/v1/auth/**` | 8100 |
| service-admin | `/api/v1/admin/**` | 8000 |
| service-customer | `/api/v1/customer/**` | 8010 |
| service-store | `/api/v1/store/**` | 8070（暂无接口） |

> ⚠️ 与旧文档差异：auth 服务实际端口为 **8100**（旧文档写 8090，且 8090 已被 order/product 占用，属冲突）。

### 1.2 统一响应格式

```json
{ "code": 0, "message": "SUCCESS", "data": {} }
```

| code | 含义 | HTTP 状态码 |
|---|---|---|
| 0 | 成功 | 200 |
| 400 | 业务异常 | 200（业务失败，HTTP 200） |
| 401 | 参数错误 / 未登录 / token 失效 | 400 / 401 |
| 403 | 无权限 | 200 |
| 404 | 资源不存在 | 200（customer 服务） |
| 500 | 系统异常 | 500 |
| 501 | 未知错误 | 500 |
| 502 | TOKEN 为空或错误 | — |

### 1.3 鉴权方式

- 除白名单外，所有接口请求头必须携带：`Authorization: Bearer {accessToken}`
- accessToken 有效期：管理员/员工 **8 小时**，会员 **7 天**；refreshToken **30 天**。
- 网关校验失败统一返回：HTTP 401 + `{"code":401,"message":"未登录 / token 无效或已过期 / token 已失效","data":null}`。

### 1.4 数据字典

**用户类型 userType**：1 会员 / 2 管理员 / 3 员工 / 4 供应商
**会员等级**：1 普通会员 / 2 银卡会员 / 3 金卡会员
**数据范围 dataScope**：1 全部 / 2 本区域 / 3 本门店 / 4 本人
**角色状态**：1 启用 / 2 禁用
**权限类型**：1 菜单 / 2 按钮 / 3 接口

---

## 2. 测试环境准备（必须先完成）

### 2.1 基础设施清单

| 组件 | 地址 | 状态（2026-09-20） |
|---|---|---|
| Nacos | 127.0.0.1:8848（namespace=soms） | ❌ 未启动，**必须先启动** |
| Redis | 127.0.0.1:6379 | ❌ 未启动，**必须先启动** |
| MySQL | 127.0.0.1:3306 | ✅ 运行中 |

### 2.2 Nacos 需要导入的配置（模板）

各服务通过 `spring.config.import` 从 Nacos 拉取以下配置，**缺失则服务无法正确启动**：

| data-id | 关键内容 |
|---|---|
| `soms-common.yml` | 数据源（url/username/password）、JWT（secret/issuer/过期时间）、gateway.sign.secret 等 |
| `soms-redis.yml` | Redis host/port/database |
| `service-gateway.yml` | 网关路由、白名单、签名密钥（可先用本地兜底） |
| `service-auth.yml` / `service-admin.yml` / `service-customer.yml` | 各自数据源等 |

```yaml
# soms-common.yml 模板（脱敏）
jwt:
  secret: <32字节以上随机串>      # 网关与 auth 必须一致
  issuer: soms
  admin-expire: 28800            # 8 小时
  member-expire: 604800          # 7 天
  refresh-expire: 2592000        # 30 天

gateway:
  sign:
    secret: <与网关一致>

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/soms_admin?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: <你的密码>
```

### 2.3 数据库建表与种子数据

项目内**只有 `soms_customer` 库的建表脚本**（`soms-customer-service/src/main/resources/db/schema.sql`）。auth 与 admin 涉及的 9 张表需要手动创建，以下 DDL 依据实体类推导：

```sql
-- 库：soms_auth
CREATE DATABASE IF NOT EXISTS `soms_auth` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `soms_auth`;

CREATE TABLE `auth_account` (
  `auth_id` BIGINT NOT NULL COMMENT '主键（雪花）',
  `user_id` BIGINT DEFAULT NULL COMMENT '业务用户ID（admin_id/employee_id/customer_id）',
  `user_type` TINYINT NOT NULL COMMENT '1会员 2管理员 3员工 4供应商',
  `username` VARCHAR(64) NOT NULL,
  `password_hash` VARCHAR(128) NOT NULL,
  `mobile` VARCHAR(20) DEFAULT NULL,
  `open_id` VARCHAR(128) DEFAULT NULL,
  `union_id` VARCHAR(128) DEFAULT NULL,
  `status` TINYINT DEFAULT 1 COMMENT '1正常 2锁定 3禁用',
  `token_version` INT DEFAULT 0,
  `last_login` DATETIME DEFAULT NULL,
  `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT DEFAULT 0,
  PRIMARY KEY (`auth_id`),
  UNIQUE KEY `uk_user_type_username` (`user_type`,`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认证账号表';

CREATE TABLE `auth_login_log` (
  `id` BIGINT NOT NULL,
  `auth_id` BIGINT DEFAULT NULL,
  `user_type` TINYINT DEFAULT NULL,
  `login_type` TINYINT DEFAULT 1,
  `ip` VARCHAR(64) DEFAULT NULL,
  `user_agent` VARCHAR(512) DEFAULT NULL,
  `success` TINYINT DEFAULT NULL COMMENT '1成功 0失败',
  `fail_reason` VARCHAR(128) DEFAULT NULL,
  `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_auth_id` (`auth_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

CREATE TABLE `auth_refresh_token` (
  `id` BIGINT NOT NULL,
  `auth_id` BIGINT NOT NULL,
  `token` VARCHAR(64) NOT NULL,
  `expire_time` DATETIME NOT NULL,
  `status` TINYINT DEFAULT 1,
  `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_token` (`token`),
  KEY `idx_auth_id` (`auth_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='刷新令牌表';
```

```sql
-- 库：soms_admin
CREATE DATABASE IF NOT EXISTS `soms_admin` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `soms_admin`;

CREATE TABLE `sys_admin` (
  `admin_id` BIGINT NOT NULL,
  `real_name` VARCHAR(64) DEFAULT NULL,
  `mobile` VARCHAR(20) DEFAULT NULL,
  `email` VARCHAR(128) DEFAULT NULL,
  `avatar` VARCHAR(255) DEFAULT NULL,
  `remark` VARCHAR(255) DEFAULT NULL,
  `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT DEFAULT 0,
  PRIMARY KEY (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统管理员表';

CREATE TABLE `sys_role` (
  `role_id` BIGINT NOT NULL,
  `role_code` VARCHAR(64) NOT NULL,
  `role_name` VARCHAR(64) NOT NULL,
  `user_type` TINYINT NOT NULL COMMENT '2管理员 3员工',
  `data_scope` TINYINT DEFAULT 1,
  `sort` INT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `remark` VARCHAR(255) DEFAULT NULL,
  `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT DEFAULT 0,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE `sys_permission` (
  `permission_id` BIGINT NOT NULL,
  `permission_code` VARCHAR(128) NOT NULL,
  `permission_name` VARCHAR(64) NOT NULL,
  `permission_type` TINYINT DEFAULT NULL COMMENT '1菜单 2按钮 3接口',
  `parent_id` BIGINT DEFAULT NULL,
  `path` VARCHAR(255) DEFAULT NULL,
  `icon` VARCHAR(64) DEFAULT NULL,
  `sort` INT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT DEFAULT 0,
  PRIMARY KEY (`permission_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

CREATE TABLE `sys_admin_role` (
  `id` BIGINT NOT NULL,
  `admin_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_admin_id` (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员-角色关联表';

CREATE TABLE `sys_employee` (
  `employee_id` BIGINT NOT NULL,
  `employee_no` VARCHAR(64) DEFAULT NULL,
  `real_name` VARCHAR(64) DEFAULT NULL,
  `mobile` VARCHAR(20) DEFAULT NULL,
  `store_id` BIGINT DEFAULT NULL,
  `position` VARCHAR(64) DEFAULT NULL,
  `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT DEFAULT 0,
  PRIMARY KEY (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

CREATE TABLE `sys_employee_role` (
  `id` BIGINT NOT NULL,
  `employee_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工-角色关联表';

CREATE TABLE `sys_employee_store` (
  `id` BIGINT NOT NULL,
  `employee_id` BIGINT NOT NULL,
  `store_id` BIGINT NOT NULL,
  `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工-门店关联表';

CREATE TABLE `sys_role_permission` (
  `id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关联表';
```

**种子数据（最小可登录集合）**：

```sql
USE `soms_auth`;
-- admin / admin123（BCrypt 哈希由 AuthTest 生成，此处为示例哈希，请用真实哈希替换）
INSERT INTO `auth_account` (`auth_id`,`user_id`,`user_type`,`username`,`password_hash`,`status`,`token_version`)
VALUES (1, 1, 2, 'admin', '<BCrypt(admin123)>', 1, 0);

USE `soms_admin`;
INSERT INTO `sys_admin` (`admin_id`,`real_name`) VALUES (1, '系统管理员');

-- 超级管理员角色
INSERT INTO `sys_role` (`role_id`,`role_code`,`role_name`,`user_type`,`data_scope`,`sort`,`status`)
VALUES (1, 'SUPER_ADMIN', '超级管理员', 2, 1, 1, 1);
INSERT INTO `sys_admin_role` (`id`,`admin_id`,`role_id`) VALUES (1, 1, 1);

-- 权限字典（按需插入，示例：系统管理根节点 + 角色/权限/客户权限）
INSERT INTO `sys_permission` (`permission_id`,`permission_code`,`permission_name`,`permission_type`,`parent_id`,`sort`,`status`) VALUES
(1, 'system', '系统管理', 1, NULL, 1, 1),
(1001, 'system:role:list', '角色列表', 2, 1, 1, 1),
(1002, 'system:role:add', '新增角色', 2, 1, 2, 1),
(1003, 'system:role:edit', '修改角色', 2, 1, 3, 1),
(1004, 'system:role:delete', '删除角色', 2, 1, 4, 1),
(1005, 'system:role:assign', '分配权限', 2, 1, 5, 1),
(1006, 'system:permission:list', '权限列表', 2, 1, 6, 1),
(2001, 'customer:list', '会员列表', 2, 1, 7, 1);
```

> 说明：`SUPER_ADMIN` 角色在登录时会返回**全部**权限（代码逻辑），故上面权限字典为普通角色分配所用。

### 2.4 服务启动顺序

```
1. 启动 Nacos（standalone，并创建 namespace=soms）
2. 启动 Redis
3. 在 Nacos 中导入配置（2.2）
4. 执行建表 SQL（2.3）
5. 依次启动：gateway → auth → admin → customer
   （或 IDE 中分别运行各 Application 类）
6. 验证：curl http://localhost/actuator/health 或登录接口
```

---

## 3. 认证服务接口（service-auth）

### 3.1 账号密码登录

**POST** `/api/v1/auth/login`（白名单，无需 token）

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| username | String | 是 | 登录账号 |
| password | String | 是 | 明文密码 |
| userType | Integer | 是 | 1会员 2管理员 3员工 |

**测试实例 ① 登录成功**：

```bash
curl -X POST http://localhost/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123","userType":2}'
```

```json
{
  "code": 0, "message": "SUCCESS",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "3f2a8b...",
    "tokenType": "Bearer",
    "expiresIn": 28800,
    "userId": 1, "userType": 2, "roles": ["SUPER_ADMIN"]
  }
}
```

**测试实例 ② 密码错误**：

```bash
curl -X POST http://localhost/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"wrong","userType":2}'
```

```json
{ "code": 400, "message": "账号或密码错误", "data": null }
```

**测试实例 ③ 账号被禁用**：将 `auth_account.status` 改为 3 后登录 → `{"code":400,"message":"账号已锁定或禁用","data":null}`
**测试实例 ④ 参数缺失**：不传 userType → HTTP 400 `{"code":401,"message":"用户类型不能为空","data":null}`
**测试实例 ⑤ 账号不存在** → `{"code":400,"message":"账号或密码错误","data":null}`（防枚举）

> 登录失败会写入 `auth_login_log`。

### 3.2 登出

**POST** `/api/v1/auth/logout`

请求头：`Authorization: Bearer {accessToken}`

**测试实例 ⑥ 登出**：

```bash
curl -X POST http://localhost/api/v1/auth/logout -H "Authorization: Bearer $TOKEN"
```

```json
{ "code": 0, "message": "SUCCESS", "data": null }
```

**测试实例 ⑦ 登出后用旧 token 访问受保护接口**（如 `GET /api/v1/admin/role/list`）→ HTTP 401 `{"code":401,"message":"token 已失效","data":null}`

### 3.3 刷新 Token

**POST** `/api/v1/auth/refresh?refreshToken={refreshToken}`（白名单）

**测试实例 ⑧ 刷新**：

```bash
curl -X POST "http://localhost/api/v1/auth/refresh?refreshToken=$REFRESH_TOKEN"
```

```json
{
  "code": 0, "message": "SUCCESS",
  "data": { "accessToken": "新token...", "refreshToken": "3f2a8b...", "expiresIn": 28800, "userId": 1, "userType": 2 }
}
```

**测试实例 ⑨ 无效 refreshToken** → `{"code":400,"message":"refresh token 无效或已过期","data":null}`
**测试实例 ⑩ 账号禁用后刷新** → `{"code":400,"message":"账号不可用","data":null}`

---

## 4. 后台管理接口（service-admin）

> 以下接口均需 `Authorization: Bearer {accessToken}`，并需要对应权限。无权限时返回：`{"code":403,"message":"无权限","data":null}`（HTTP 200）。

### 4.1 获取微服务状态

**GET** `/api/v1/admin/nacos/server/status`（无需权限注解）

**测试实例 ⑪**：

```bash
curl http://localhost/api/v1/admin/nacos/server/status -H "Authorization: Bearer $TOKEN"
```

```json
{
  "code": 0, "message": "SUCCESS",
  "data": { "services": [ { "serviceName": "service-customer", "instances": [ { "ip": "192.168.x.x", "port": 8010, "group": "DEFAULT_GROUP", "serviceName": "service-customer" } ] } ] }
}
```

### 4.2 分页查询客户列表（跨服务 Feign）

**GET** `/api/v1/admin/customer/list`（权限：`customer:list`）

| 参数 | 类型 | 必填 | 默认 | 说明 |
|---|---|---|---|---|
| pageNum | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |
| customerName | String | 否 | — | 姓名模糊 |
| mobile | String | 否 | — | 手机号模糊 |

**测试实例 ⑫ 分页查询**：

```bash
curl "http://localhost/api/v1/admin/customer/list?pageNum=1&pageSize=10&customerName=张" \
  -H "Authorization: Bearer $TOKEN"
```

```json
{
  "code": 0, "message": "SUCCESS",
  "data": {
    "records": [ { "customerId": 1, "customerName": "张三", "mobile": "13800138000", "customerLevel": 1 } ],
    "total": 1, "size": 10, "current": 1, "pages": 1
  }
}
```

### 4.3 角色管理

#### 4.3.1 分页查询角色 — **GET** `/api/v1/admin/role/list`（权限 `system:role:list`）

参数：pageNum/pageSize/roleName(模糊)/userType/status

**测试实例 ⑬**：

```bash
curl "http://localhost/api/v1/admin/role/list?pageNum=1&pageSize=10&userType=3" \
  -H "Authorization: Bearer $TOKEN"
```

```json
{
  "code": 0, "message": "SUCCESS",
  "data": { "records": [ { "roleId": 4, "roleCode": "STORE_MANAGER", "roleName": "店长", "userType": 3, "dataScope": 3, "sort": 4, "status": 1, "remark": "管理本门店" } ], "total": 5, "size": 10, "current": 1, "pages": 1 }
}
```

#### 4.3.2 查询角色详情 — **GET** `/api/v1/admin/role/{roleId}`（权限 `system:role:list`）

**测试实例 ⑭**：

```bash
curl http://localhost/api/v1/admin/role/4 -H "Authorization: Bearer $TOKEN"
```

```json
{ "code": 0, "message": "SUCCESS", "data": { "roleId": 4, "roleCode": "STORE_MANAGER", "roleName": "店长", "userType": 3, "dataScope": 3, "sort": 4, "status": 1, "remark": "管理本门店", "permissionIds": [2, 1101, 5] } }
```

#### 4.3.3 新增角色 — **POST** `/api/v1/admin/role`（权限 `system:role:add`）

请求体：roleCode(必填,唯一)/roleName(必填)/userType(必填 2|3)/dataScope(默认1)/sort(默认0)/remark

**测试实例 ⑮ 新增成功**：

```bash
curl -X POST http://localhost/api/v1/admin/role \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"roleCode":"TEST_ROLE","roleName":"测试角色","userType":3,"dataScope":3,"sort":9,"remark":"接口测试"}'
```

```json
{ "code": 0, "message": "SUCCESS", "data": 2101032941673771010 }
```

**测试实例 ⑯ 编码重复** → `{"code":500,"message":"系统异常，请联系管理员","data":null}`
> ⚠️ 已知缺陷：当前抛 `RuntimeException` 被兜底为 500，预期应为 400“角色编码已存在：xxx”（见分析报告问题 #5）。

**测试实例 ⑰ 参数校验失败**（roleName 为空）→ HTTP 400 `{"code":401,"message":"角色名称不能为空","data":null}`

#### 4.3.4 修改角色 — **PUT** `/api/v1/admin/role`（权限 `system:role:edit`，roleCode 不可改）

**测试实例 ⑱**：

```bash
curl -X PUT http://localhost/api/v1/admin/role \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"roleId":4,"roleName":"店长（改）","dataScope":3,"status":1}'
```

```json
{ "code": 0, "message": "SUCCESS", "data": null }
```

**测试实例 ⑲ 角色不存在** → `{"code":500,"message":"系统异常，请联系管理员","data":null}`（同为 #5 缺陷，预期 400“角色不存在”）

#### 4.3.5 删除角色 — **DELETE** `/api/v1/admin/role/{roleId}`（权限 `system:role:delete`，逻辑删除 + 清关联）

**测试实例 ⑳**：

```bash
curl -X DELETE http://localhost/api/v1/admin/role/4 -H "Authorization: Bearer $TOKEN"
```

```json
{ "code": 0, "message": "SUCCESS", "data": null }
```

#### 4.3.6 给角色分配权限 — **POST** `/api/v1/admin/role/assign-permission`（权限 `system:role:assign`）

请求体：roleId(必填)/permissionIds(数组，传空=清空)

**测试实例 ㉑**：

```bash
curl -X POST http://localhost/api/v1/admin/role/assign-permission \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"roleId":4,"permissionIds":[1001,1002,2001]}'
```

```json
{ "code": 0, "message": "SUCCESS", "data": null }
```

### 4.4 权限管理

#### 4.4.1 查询权限树 — **GET** `/api/v1/admin/permission/tree`（权限 `system:permission:list`）

**测试实例 ㉒**：

```bash
curl http://localhost/api/v1/admin/permission/tree -H "Authorization: Bearer $TOKEN"
```

```json
{
  "code": 0, "message": "SUCCESS",
  "data": [ { "permissionId": 1, "permissionCode": "system", "permissionName": "系统管理", "permissionType": 1, "parentId": null, "path": "/system", "icon": "setting", "sort": 1, "children": [ { "permissionId": 1001, "permissionCode": "system:role:list", "permissionName": "角色列表", "permissionType": 2, "parentId": 1, "path": null, "icon": null, "sort": 1, "children": [] } ] } ]
}
```

#### 4.4.2 查询角色已分配权限 ID — **GET** `/api/v1/admin/permission/role/{roleId}`（权限 `system:permission:list`）

**测试实例 ㉓**：

```bash
curl http://localhost/api/v1/admin/permission/role/4 -H "Authorization: Bearer $TOKEN"
```

```json
{ "code": 0, "message": "SUCCESS", "data": [1001, 1002, 2001] }
```

---

## 5. 客户管理接口（service-customer）

> ⚠️ 注意：客户服务**自身无权限校验**，经网关后任何已登录用户均可调用（见分析报告问题 #7）。
> 测试数据准备：先执行 `soms-customer-service/src/main/resources/db/schema.sql`。

### 5.1 新增客户 — **POST** `/api/v1/customer`

请求体：customerName(必填≤64)/mobile(必填，`^1[3-9]\d{9}$`)/customerLevel(1~3，默认1)/totalConsume(默认0)/balance(默认0)

**测试实例 ㉔ 新增成功**：

```bash
curl -X POST http://localhost/api/v1/customer \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"customerName":"张三","mobile":"13800138000","customerLevel":1,"balance":100.00}'
```

```json
{ "code": 0, "message": "新增客户成功", "data": { "customerId": 1, "customerName": "张三", "mobile": "13800138000", "customerLevel": 1, "totalConsume": 0.00, "balance": 100.00 } }
```

**测试实例 ㉕ 手机号重复** → `{"code":401,"message":"手机号已存在，请勿重复添加","data":null}`
**测试实例 ㉖ 参数校验失败**（手机号非法）→ HTTP 400 `{"code":401,"message":"会员姓名不能为空; 手机号格式不正确","data":null}`

### 5.2 删除客户（逻辑删除）— **DELETE** `/api/v1/customer/{customerId}`

**测试实例 ㉗**：

```bash
curl -X DELETE http://localhost/api/v1/customer/1 -H "Authorization: Bearer $TOKEN"
```

```json
{ "code": 0, "message": "删除客户成功", "data": { "customerId": 1, "isDeleted": 1 } }
```

**测试实例 ㉘ 客户不存在** → `{"code":404,"message":"客户不存在或已被删除","data":null}`

### 5.3 修改客户 — **PUT** `/api/v1/customer`

**测试实例 ㉙**：

```bash
curl -X PUT http://localhost/api/v1/customer \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"customerId":1,"customerName":"张三（改）","customerLevel":2,"balance":200.00}'
```

```json
{ "code": 0, "message": "修改客户信息成功", "data": { "customerId": 1, "customerName": "张三（改）", "customerLevel": 2, "balance": 200.00 } }
```

**测试实例 ㉚ 手机号被他人占用** → `{"code":401,"message":"手机号已被其他客户使用","data":null}`

### 5.4 根据 ID 查询 — **GET** `/api/v1/customer/{customerId}`

**测试实例 ㉛**：

```bash
curl http://localhost/api/v1/customer/1 -H "Authorization: Bearer $TOKEN"
```

```json
{ "code": 0, "message": "SUCCESS", "data": { "customerId": 1, "customerName": "张三", "mobile": "13800138000", "customerLevel": 1, "totalConsume": 0.00, "balance": 100.00 } }
```

### 5.5 根据手机号查询 — **GET** `/api/v1/customer/mobile/{mobile}`

**测试实例 ㉜**：

```bash
curl http://localhost/api/v1/customer/mobile/13800138000 -H "Authorization: Bearer $TOKEN"
```

```json
{ "code": 0, "message": "SUCCESS", "data": { "customerId": 1, "mobile": "13800138000" } }
```

### 5.6 分页查询客户列表 — **GET** `/api/v1/customer/list`

参数同 4.2。**测试实例 ㉝**：

```bash
curl "http://localhost/api/v1/customer/list?pageNum=1&pageSize=10&customerName=张" \
  -H "Authorization: Bearer $TOKEN"
```

```json
{ "code": 0, "message": "SUCCESS", "data": { "records": [ { "customerId": 1, "customerName": "张三", "mobile": "13800138000", "customerLevel": 1, "totalConsume": 0.00, "balance": 100.00, "gmtCreate": "2026-09-16T10:30:00" } ], "total": 1, "size": 10, "current": 1, "pages": 1 } }
```

---

## 6. 内部接口（仅供服务间 Feign 调用，不建议直测）

### 6.1 查询用户角色和权限 — **GET** `/api/v1/admin/internal/user-auth?userId={id}&userType={type}`

调用方：service-auth 登录时。

**测试实例 ㉞**（直连 admin 8000 或经网关）：

```bash
curl "http://localhost/api/v1/admin/internal/user-auth?userId=1&userType=2" \
  -H "Authorization: Bearer $TOKEN"
```

```json
{
  "code": 0, "message": "SUCCESS",
  "data": { "userId": 1, "userType": 2, "roles": ["SUPER_ADMIN"], "permissions": ["system","system:role:list",...], "dataScope": 1, "storeIds": [] }
}
```

> ⚠️ 已知缺陷：网关路由 `/api/v1/admin/**` 会匹配 internal 路径，未排除（见分析报告问题 #9）。

---

## 7. 错误码速查

| 场景 | HTTP | code | message |
|---|---|---|---|
| 成功 | 200 | 0 | SUCCESS / 自定义成功消息 |
| 业务失败 | 200 | 400 | 如“账号或密码错误”“客户不存在” |
| 参数校验失败 | 400 | 401 | 校验消息拼接 |
| 请求体 JSON 错误 | 400 | 401 | 请求体格式错误，请检查JSON格式 |
| 未登录 | 401 | 401 | 未登录 |
| token 无效/过期 | 401 | 401 | token 无效或已过期 |
| token 已登出 | 401 | 401 | token 已失效 |
| 无权限 | 200 | 403 | 无权限 |
| 资源不存在 | 200 | 404 | 客户不存在 等 |
| 系统异常 | 500 | 500 | 系统异常，请联系管理员 |
| 未知错误 | 500 | 501 | 未知错误，请联系开发人员 |

---

## 8. 建议测试流程（端到端冒烟）

```
① 登录 admin → 拿 TOKEN / REFRESH_TOKEN          （3.1）
② 用 TOKEN 调角色分页 / 权限树                    （4.3.1 / 4.4.1）
③ 新增角色 → 分配权限 → 查详情 → 修改 → 删除       （4.3.3~4.3.6）
④ 新增客户 → 按 ID 查 → 按手机号查 → 分页查 → 改 → 删 （5.x）
⑤ 登出 → 用旧 TOKEN 访问 → 断言 401               （3.2 / ⑦）
⑥ 用 REFRESH_TOKEN 刷新 → 新 TOKEN 访问 → 断言 200 （3.3）
⑦ 反向用例：错密码 / 重复手机号 / 无权限账号        （3.1② / 5.1㉕ / 4.3③ 权限不足）
```

### 完整可复制脚本（Linux/macOS bash）

```bash
BASE=http://localhost

# 1. 登录
LOGIN=$(curl -s -X POST $BASE/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123","userType":2}')
echo "$LOGIN"
TOKEN=$(echo "$LOGIN" | sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p')
REFRESH=$(echo "$LOGIN" | sed -n 's/.*"refreshToken":"\([^"]*\)".*/\1/p')
echo "TOKEN=$TOKEN"

# 2. 角色列表
curl -s "$BASE/api/v1/admin/role/list?pageNum=1&pageSize=10" -H "Authorization: Bearer $TOKEN"

# 3. 新增角色
curl -s -X POST $BASE/api/v1/admin/role \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"roleCode":"TEST_ROLE","roleName":"测试角色","userType":3,"dataScope":3}'

# 4. 新增客户
curl -s -X POST $BASE/api/v1/customer \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"customerName":"张三","mobile":"13800138000","customerLevel":1}'

# 5. 登出
curl -s -X POST $BASE/api/v1/auth/logout -H "Authorization: Bearer $TOKEN"

# 6. 刷新
curl -s -X POST "$BASE/api/v1/auth/refresh?refreshToken=$REFRESH"
```

### Windows PowerShell 测试脚本

```powershell
$BASE = "http://localhost"

# 登录
$login = Invoke-RestMethod -Method Post -Uri "$BASE/api/v1/auth/login" -ContentType "application/json" `
  -Body '{"username":"admin","password":"admin123","userType":2}'
$token = $login.data.accessToken
Write-Host "TOKEN=$token"

# 角色列表
Invoke-RestMethod -Uri "$BASE/api/v1/admin/role/list?pageNum=1&pageSize=10" `
  -Headers @{ Authorization = "Bearer $token" } | ConvertTo-Json -Depth 5
```

---

## 9. 测试用例汇总表（建议覆盖）

| 编号 | 接口 | 用例 | 预期 |
|---|---|---|---|
| ① | POST /auth/login | 正确凭据 | 200 code=0，返回双 token |
| ② | POST /auth/login | 密码错误 | code=400 “账号或密码错误” |
| ③ | POST /auth/login | 账号禁用 | code=400 “账号已锁定或禁用” |
| ④ | POST /auth/login | 缺 userType | HTTP 400 code=401 |
| ⑥ | POST /auth/logout | 正常登出 | code=0 |
| ⑦ | GET /admin/role/list | 登出后旧 token | HTTP 401 “token 已失效” |
| ⑧ | POST /auth/refresh | 有效 refreshToken | code=0 新 accessToken |
| ⑨ | POST /auth/refresh | 无效 refreshToken | code=400 |
| ⑬ | GET /admin/role/list | 有权限 | code=0 分页 |
| ⑮ | POST /admin/role | 合法请求体 | code=0 返回 roleId |
| ⑯ | POST /admin/role | 编码重复 | code=500（缺陷，预期400） |
| ⑰ | POST /admin/role | roleName 空 | HTTP 400 code=401 |
| ⑳ | DELETE /admin/role/{id} | 合法 | code=0 |
| ㉒ | GET /admin/permission/tree | 有权限 | code=0 树形结构 |
| ㉔ | POST /customer | 合法 | code=0 “新增客户成功” |
| ㉕ | POST /customer | 手机号重复 | code=401 |
| ㉖ | POST /customer | 手机号非法 | HTTP 400 code=401 |
| ㉘ | DELETE /customer/{id} | 不存在 | code=404 |
| ㉜ | GET /customer/mobile/{m} | 精确查询 | code=0 |
| ㉝ | GET /customer/list | 模糊+分页 | code=0 分页结构 |
| — | 全部 admin 接口 | 无 token | HTTP 401 |
| — | 全部 admin 接口 | 无权限角色 | code=403 “无权限” |

---

*本文档接口信息与源码一致；标注“⚠️已知缺陷”处为代码现状，测试时请注意区分预期结果与缺陷结果。*
