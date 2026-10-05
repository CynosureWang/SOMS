# SOMS 后台管理服务接口文档

> **服务名称**：`service-admin`
> **服务端口**：`8000`
> **基础路径**：`http://127.0.0.1:8000`
> **版本**：`1.1.0`
> **首次版本日期**：2026-09-18
> **更新日期**：2026-09-20

---

## 1. 通用说明

### 1.1 统一响应格式

所有接口均返回统一的 JSON 结构：

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {}
}
```

| 字段      | 类型     | 说明                               |
| --------- | -------- | ---------------------------------- |
| `code`    | `int`    | 状态码，`0` 表示成功，其他表示失败 |
| `message` | `String` | 响应消息                           |
| `data`    | `Object` | 响应数据（部分接口为 `null`）      |

### 1.2 状态码说明

| 状态码 | 说明                   |
| ------ | ---------------------- |
| `0`    | 成功                   |
| `400`  | 失败（业务异常）       |
| `401`  | 参数错误 / 未登录      |
| `403`  | 无权限                 |
| `404`  | 资源不存在             |
| `500`  | 系统异常               |
| `501`  | 未知错误               |
| `502`  | TOKEN 为空或错误       |

### 1.3 请求头要求

除白名单接口外，所有接口必须携带 token：

```
Authorization: Bearer {accessToken}
```

> 请求经网关转发时，网关会额外注入 `X-User-Id`、`X-User-Type`、`X-Roles`、`X-Gateway-Timestamp`、`X-Gateway-Sign` 等内部头，业务方无需关心。

### 1.4 数据字典 — 用户类型（userType）

| 值  | 含义   |
|-----|--------|
| `1` | 会员   |
| `2` | 管理员 |
| `3` | 员工   |
| `4` | 供应商 |

### 1.5 数据字典 — 会员等级

| 值  | 含义     |
|-----|----------|
| `1` | 普通会员 |
| `2` | 银卡会员 |
| `3` | 金卡会员 |

### 1.6 数据字典 — 逻辑删除

| 值  | 含义   |
|-----|--------|
| `0` | 未删除 |
| `1` | 已删除 |

### 1.7 数据字典 — 数据范围（dataScope）

| 值  | 含义       |
|-----|------------|
| `1` | 全部数据   |
| `2` | 本区域数据 |
| `3` | 本门店数据 |
| `4` | 仅本人数据 |

### 1.8 数据字典 — 角色状态（status）

| 值  | 含义 |
|-----|------|
| `1` | 启用 |
| `2` | 禁用 |

### 1.9 数据字典 — 权限类型（permissionType）

| 值  | 含义 |
|-----|------|
| `1` | 菜单 |
| `2` | 按钮 |
| `3` | 接口 |

---

## 2. 数据模型 — Customer（客户）

| 字段           | 类型           | 必填 | 校验规则                            | 说明                                  |
|----------------|----------------|------|-------------------------------------|---------------------------------------|
| `customerId`   | `Long`         | 否   | 雪花主键                            | 会员ID，新增时自动生成                |
| `customerName` | `String`       | 是   | 非空，最大 64 字符                  | 会员姓名                              |
| `mobile`       | `String`       | 是   | 非空，正则 `^1[3-9]\d{9}$`         | 手机号，唯一                          |
| `customerLevel`| `Integer`      | 否   | 范围 1 ~ 3，默认 1                  | 会员等级                              |
| `totalConsume` | `BigDecimal`   | 否   | —                                   | 累计消费金额，默认 0.00               |
| `balance`      | `BigDecimal`   | 否   | —                                   | 账户余额，默认 0.00                   |
| `gmtCreate`    | `LocalDateTime`| 否   | 自动填充                            | 创建时间                              |
| `gmtModified`  | `LocalDateTime`| 否   | 自动填充                            | 更新时间                              |
| `isDeleted`    | `Integer`      | 否   | 默认 0                              | 逻辑删除标志                          |

---

## 3. 数据模型 — Role（角色）

| 字段          | 类型            | 必填 | 校验规则           | 说明                          |
|---------------|-----------------|------|--------------------|-------------------------------|
| `roleId`      | `Long`          | 否   | 雪花主键           | 角色ID，新增时自动生成        |
| `roleCode`    | `String`        | 是   | 非空，全局唯一     | 角色编码，如 `STORE_MANAGER`  |
| `roleName`    | `String`        | 是   | 非空，最大 64 字符 | 角色名称，如 `店长`           |
| `userType`    | `Integer`       | 是   | `2` 或 `3`         | 适用用户类型                  |
| `dataScope`   | `Integer`       | 否   | 1~4，默认 1        | 数据范围                      |
| `sort`        | `Integer`       | 否   | 默认 0             | 排序                          |
| `status`      | `Integer`       | 否   | 1 或 2，默认 1     | 状态                          |
| `remark`      | `String`        | 否   | 最大 255 字符      | 备注                          |
| `gmtCreate`   | `LocalDateTime` | 否   | 自动填充           | 创建时间                      |
| `gmtModified` | `LocalDateTime` | 否   | 自动填充           | 更新时间                      |
| `isDeleted`   | `Integer`       | 否   | 默认 0             | 逻辑删除标志                  |

---

## 4. 数据模型 — Permission（权限）

| 字段             | 类型      | 说明                            |
|------------------|-----------|---------------------------------|
| `permissionId`   | `Long`    | 权限ID                          |
| `permissionCode` | `String`  | 权限编码，如 `system:role:add`  |
| `permissionName` | `String`  | 权限名称，如 `新增角色`         |
| `permissionType` | `Integer` | 权限类型：1菜单 2按钮 3接口     |
| `parentId`       | `Long`    | 上级权限ID，根节点为 `null`     |
| `path`           | `String`  | 前端路由或接口路径              |
| `icon`           | `String`  | 图标                            |
| `sort`           | `Integer` | 排序                            |
| `status`         | `Integer` | 1启用 2禁用                     |
| `children`       | `Array`   | 子权限列表（树形返回时才有）    |

---

## 5. 接口详情 — 微服务状态

### 5.1 获取微服务状态

**GET** `/api/v1/admin/nacos/server/status`

从 Nacos 中获取微服务服务状态。

#### 请求示例

```
GET /api/v1/admin/nacos/server/status
```

#### 成功响应

**HTTP 200**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "services": [
      {
        "serviceName": "service-customer",
        "instances": [
          {
            "ip": "169.254.213.131",
            "port": 8010,
            "group": "DEFAULT_GROUP",
            "serviceName": "service-customer"
          }
        ]
      }
    ]
  }
}
```

---

## 6. 接口详情 — 客户管理

### 6.1 查询客户列表

**GET** `/api/v1/admin/customer/list`

通过远程调用客户管理服务（service-customer）分页获取客户列表。

**所需权限**：`customer:list`

#### 查询参数

| 参数           | 类型      | 必填 | 默认值 | 说明                 |
| -------------- | --------- | ---- | ------ | -------------------- |
| `pageNum`      | `Integer` | 否   | `1`    | 页码                 |
| `pageSize`     | `Integer` | 否   | `10`   | 每页数量             |
| `customerName` | `String`  | 否   | —      | 会员姓名（模糊匹配） |
| `mobile`       | `String`  | 否   | —      | 手机号（模糊匹配）   |

#### 请求示例

```
GET /api/v1/admin/customer/list?pageNum=1&pageSize=10&customerName=张&mobile=138
```

#### 成功响应

**HTTP 200**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "records": [
      {
        "customerId": 1,
        "customerName": "张三",
        "mobile": "13800138000",
        "customerLevel": 1,
        "totalConsume": 0.00,
        "balance": 100.00,
        "gmtCreate": "2026-09-16T10:30:00",
        "gmtModified": "2026-09-16T10:30:00",
        "isDeleted": 0
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

#### 分页字段说明

| 字段      | 类型      | 说明           |
| --------- | --------- | -------------- |
| `records` | `Array`   | 当前页数据列表 |
| `total`   | `Long`    | 总记录数       |
| `size`    | `Integer` | 每页条数       |
| `current` | `Integer` | 当前页码       |
| `pages`   | `Integer` | 总页数         |

---

## 7. 接口详情 — 角色管理

### 7.1 分页查询角色

**GET** `/api/v1/admin/role/list`

**所需权限**：`system:role:list`

#### 查询参数

| 参数       | 类型      | 必填 | 默认值 | 说明             |
|------------|-----------|------|--------|------------------|
| `pageNum`  | `Integer` | 否   | `1`    | 页码             |
| `pageSize` | `Integer` | 否   | `10`   | 每页数量         |
| `roleName` | `String`  | 否   | —      | 角色名称（模糊） |
| `userType` | `Integer` | 否   | —      | 适用用户类型     |
| `status`   | `Integer` | 否   | —      | 状态             |

#### 请求示例

```
GET /api/v1/admin/role/list?pageNum=1&pageSize=10&userType=3
```

#### 成功响应

**HTTP 200**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "records": [
      {
        "roleId": 4,
        "roleCode": "STORE_MANAGER",
        "roleName": "店长",
        "userType": 3,
        "dataScope": 3,
        "sort": 4,
        "status": 1,
        "remark": "管理本门店",
        "gmtCreate": "2026-09-19T00:00:00"
      }
    ],
    "total": 5,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

---

### 7.2 查询角色详情

**GET** `/api/v1/admin/role/{roleId}`

**所需权限**：`system:role:list`

#### 路径参数

| 参数     | 类型   | 说明   |
|----------|--------|--------|
| `roleId` | `Long` | 角色ID |

#### 成功响应

**HTTP 200**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "roleId": 4,
    "roleCode": "STORE_MANAGER",
    "roleName": "店长",
    "userType": 3,
    "dataScope": 3,
    "sort": 4,
    "status": 1,
    "remark": "管理本门店",
    "permissionIds": [2, 1101, 5, 1401, 1402]
  }
}
```

| 字段            | 类型       | 说明                               |
| --------------- | ---------- | ---------------------------------- |
| `permissionIds` | `Long数组` | 该角色已分配的权限ID，用于前端回显 |

---

### 7.3 新增角色

**POST** `/api/v1/admin/role`

**所需权限**：`system:role:add`

#### 请求体

| 字段        | 类型      | 必填 | 校验规则           | 说明         |
|-------------|-----------|------|--------------------|--------------|
| `roleCode`  | `String`  | 是   | 非空，全局唯一     | 角色编码     |
| `roleName`  | `String`  | 是   | 非空，最大 64 字符 | 角色名称     |
| `userType`  | `Integer` | 是   | `2` 或 `3`         | 适用用户类型 |
| `dataScope` | `Integer` | 否   | 1~4，默认 1        | 数据范围     |
| `sort`      | `Integer` | 否   | 默认 0             | 排序         |
| `remark`    | `String`  | 否   | 最大 255 字符      | 备注         |

#### 请求体示例

```json
{
  "roleCode": "STORE_MANAGER",
  "roleName": "店长",
  "userType": 3,
  "dataScope": 3,
  "sort": 4,
  "remark": "管理本门店"
}
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": 2101032941673771010
}
```

`data` 为新角色的 `roleId`。

#### 业务异常

| 场景           | code | message                           |
|----------------|------|-----------------------------------|
| 角色编码已存在 | 400  | `"角色编码已存在：STORE_MANAGER"` |

---

### 7.4 修改角色

**PUT** `/api/v1/admin/role`

**所需权限**：`system:role:edit`

> `roleCode` 不可修改。

#### 请求体

| 字段        | 类型      | 必填 | 说明     |
|-------------|-----------|------|----------|
| `roleId`    | `Long`    | 是   | 角色ID   |
| `roleName`  | `String`  | 否   | 角色名称 |
| `dataScope` | `Integer` | 否   | 数据范围 |
| `sort`      | `Integer` | 否   | 排序     |
| `remark`    | `String`  | 否   | 备注     |
| `status`    | `Integer` | 否   | 状态     |

#### 请求体示例

```json
{
  "roleId": 4,
  "roleName": "店长（改）",
  "dataScope": 3,
  "status": 1
}
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": null
}
```

#### 业务异常

| 场景       | code | message       |
|------------|------|---------------|
| 角色不存在 | 400  | `"角色不存在"` |

---

### 7.5 删除角色

**DELETE** `/api/v1/admin/role/{roleId}`

**所需权限**：`system:role:delete`

#### 路径参数

| 参数     | 类型   | 说明   |
|----------|--------|--------|
| `roleId` | `Long` | 角色ID |

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": null
}
```

> 逻辑删除，同时会清空该角色下的 `sys_role_permission` 关联。

---

### 7.6 给角色分配权限

**POST** `/api/v1/admin/role/assign-permission`

**所需权限**：`system:role:assign`

#### 请求体

| 字段            | 类型       | 必填 | 说明                     |
|-----------------|------------|------|--------------------------|
| `roleId`        | `Long`     | 是   | 角色ID                   |
| `permissionIds` | `Long数组` | 否   | 权限ID集合，传空表示清空 |

#### 请求体示例

```json
{
  "roleId": 4,
  "permissionIds": [2, 1101, 5, 1401, 1402]
}
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": null
}
```

> 先清空该角色所有权限，再按 `permissionIds` 重新插入。

#### 业务异常

| 场景       | code | message       |
|------------|------|---------------|
| 角色不存在 | 400  | `"角色不存在"` |

---

## 8. 接口详情 — 权限管理

### 8.1 查询权限树

**GET** `/api/v1/admin/permission/tree`

**所需权限**：`system:permission:list`

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": [
    {
      "permissionId": 1,
      "permissionCode": "system",
      "permissionName": "系统管理",
      "permissionType": 1,
      "parentId": null,
      "path": "/system",
      "icon": "setting",
      "sort": 1,
      "children": [
        {
          "permissionId": 1009,
          "permissionCode": "system:role:list",
          "permissionName": "角色列表",
          "permissionType": 2,
          "parentId": 1,
          "path": null,
          "icon": null,
          "sort": 9,
          "children": []
        }
      ]
    }
  ]
}
```

> 返回结构为树形，`children` 递归嵌套。根节点 `parentId` 为 `null`。

---

### 8.2 查询角色已分配权限ID

**GET** `/api/v1/admin/permission/role/{roleId}`

**所需权限**：`system:permission:list`

#### 路径参数

| 参数     | 类型   | 说明   |
|----------|--------|--------|
| `roleId` | `Long` | 角色ID |

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": [1, 1009, 1014]
}
```

---

## 9. 错误响应汇总

| 场景             | HTTP 状态码 | code | 示例 message                      |
|------------------|-------------|------|-----------------------------------|
| 参数校验失败     | 400         | 401  | `"会员姓名不能为空; 手机号格式不正确"` |
| 请求体 JSON 错误 | 400         | 401  | `"请求体格式错误，请检查JSON格式"` |
| 业务异常         | 200         | 400  | `"客户不存在"` 等自定义业务消息    |
| 无 token         | 401         | 401  | `"未登录"`                        |
| token 无效       | 401         | 401  | `"token 无效或已过期"`            |
| token 已登出     | 401         | 401  | `"token 已失效"`                  |
| 缺少功能权限     | 200         | 403  | `"无权限"`                        |
| 系统异常         | 500         | 500  | `"系统异常，请联系管理员"`        |
| 未知错误         | 500         | 501  | `"未知错误，请联系开发人员"`      |

---

## 10. 权限编码规范

所有权限编码遵循 `模块:资源:操作` 或 `模块:操作` 的命名规范。

### 10.1 系统管理

| 权限编码                 | 权限名称   |
| ------------------------ | ---------- |
| `system:admin:list`      | 管理员列表 |
| `system:admin:add`       | 新增管理员 |
| `system:admin:edit`      | 修改管理员 |
| `system:admin:delete`    | 删除管理员 |
| `system:employee:list`   | 员工列表   |
| `system:employee:add`    | 新增员工   |
| `system:employee:edit`   | 修改员工   |
| `system:employee:delete` | 删除员工   |
| `system:role:list`       | 角色列表   |
| `system:role:add`        | 新增角色   |
| `system:role:edit`       | 修改角色   |
| `system:role:delete`     | 删除角色   |
| `system:role:assign`     | 分配权限   |
| `system:permission:list` | 权限列表   |

### 10.2 门店管理

| 权限编码            | 权限名称 |
| ------------------- | -------- |
| `store:list`        | 门店列表 |
| `store:detail`      | 门店详情 |
| `store:add`         | 新增门店 |
| `store:edit`        | 修改门店 |
| `store:delete`      | 删除门店 |
| `store:region:list` | 区域列表 |
| `store:region:add`  | 新增区域 |
| `store:region:edit` | 修改区域 |

### 10.3 商品管理

| 权限编码                  | 权限名称 |
| ------------------------- | -------- |
| `product:list`            | 商品列表 |
| `product:detail`          | 商品详情 |
| `product:add`             | 新增商品 |
| `product:edit`            | 修改商品 |
| `product:delete`          | 删除商品 |
| `product:category:list`   | 分类列表 |
| `product:category:add`    | 新增分类 |
| `product:category:edit`   | 修改分类 |
| `product:category:delete` | 删除分类 |
| `product:brand:list`      | 品牌列表 |
| `product:brand:add`       | 新增品牌 |
| `product:brand:edit`      | 修改品牌 |
| `product:brand:delete`    | 删除品牌 |

### 10.4 库存管理
| 权限编码       | 权限名称 |
| -------------- | -------- |
| `stock:list`   | 库存查询 |
| `stock:in`     | 入库     |
| `stock:out`    | 出库     |
| `stock:adjust` | 库存调整 |

### 10.5 订单管理
| 权限编码       | 权限名称 |
| -------------- | -------- |
| `order:list`   | 订单列表 |
| `order:detail` | 订单详情 |
| `order:add`    | 创建订单 |
| `order:cancel` | 取消订单 |
| `order:edit`   | 修改订单 |
| `order:refund` | 订单退款 |

### 10.6 会员管理
| 权限编码          | 权限名称 |
| ----------------- | -------- |
| `customer:list`   | 会员列表 |
| `customer:detail` | 会员详情 |
| `customer:edit`   | 修改会员 |

### 10.7 营销管理
| 权限编码                    | 权限名称     |
| --------------------------- | ------------ |
| `discount:promotion:list`   | 促销列表     |
| `discount:promotion:add`    | 新增促销     |
| `discount:promotion:edit`   | 修改促销     |
| `discount:promotion:delete` | 删除促销     |
| `discount:coupon:list`      | 券列表       |
| `discount:coupon:add`       | 新增券       |
| `discount:coupon:edit`      | 修改券       |
| `discount:coupon:delete`    | 删除券       |
| `discount:coupon:issue`     | 发券         |
| `discount:coupon:revoke`    | 回收券       |
| `discount:rule:list`        | 折扣规则列表 |
| `discount:rule:add`         | 新增折扣规则 |
| `discount:rule:edit`        | 修改折扣规则 |
| `discount:rule:delete`      | 删除折扣规则 |

### 10.8 支付管理
| 权限编码     | 权限名称 |
| ------------ | -------- |
| `pay:list`   | 支付记录 |
| `pay:create` | 发起支付 |
| `pay:close`  | 关闭支付 |
| `pay:refund` | 退款管理 |

### 10.9 财务管理
| 权限编码                 | 权限名称 |
| ------------------------ | -------- |
| `finance:reconciliation` | 对账     |
| `finance:report`         | 财务报表 |

### 10.10 报表统计
| 权限编码        | 权限名称 |
| --------------- | -------- |
| `report:sales`  | 销售报表 |
| `report:stock`  | 库存报表 |
| `report:member` | 会员报表 |

### 10.11 一级菜单与权限对应
| 菜单     | permission_id | permission_code          |
| -------- | ------------- | ------------------------ |
| 系统管理 | 1             | `system`                 |
| 门店管理 | 2             | `store`                  |
| 商品管理 | 3             | `product`                |
| 库存管理 | 4             | `stock`                  |
| 订单管理 | 5             | `order`                  |
| 会员管理 | 6             | `customer`               |
| 营销管理 | 7             | `discount` / `promotion` |
| 支付管理 | 8             | `pay`                    |
| 财务管理 | 9             | `finance`                |
| 报表统计 | 10            | `report`                 |

## 10.12 权限初始化 SQL 参考

**新增模块权限时，用 `ON DUPLICATE KEY UPDATE` 保证幂等：**

```
-- 门店模块
INSERT INTO sys_permission 
(permission_id, permission_code, permission_name, permission_type, parent_id, sort, status, is_deleted) 
VALUES
(2001, 'store:list',              '门店列表', 2, 2, 1, 1, 0),
(2002, 'store:detail',            '门店详情', 2, 2, 2, 1, 0),
(2003, 'store:add',               '新增门店', 2, 2, 3, 1, 0),
(2004, 'store:edit',              '修改门店', 2, 2, 4, 1, 0),
(2005, 'store:delete',            '删除门店', 2, 2, 5, 1, 0),
(2006, 'store:region:list',       '区域列表', 2, 2, 6, 1, 0),
(2007, 'store:region:add',        '新增区域', 2, 2, 7, 1, 0),
(2008, 'store:region:edit',       '修改区域', 2, 2, 8, 1, 0),
(2009, 'store:region:delete',     '删除区域', 2, 2, 9, 1, 0)
ON DUPLICATE KEY UPDATE
    permission_name = VALUES(permission_name),
    parent_id = VALUES(parent_id),
    sort = VALUES(sort);

-- 支付模块
INSERT INTO sys_permission 
(permission_id, permission_code, permission_name, permission_type, parent_id, sort, status, is_deleted) 
VALUES
(2101, 'pay:list',    '支付记录', 2, 8, 1, 1, 0),
(2102, 'pay:create',  '发起支付', 2, 8, 2, 1, 0),
(2103, 'pay:close',   '关闭支付', 2, 8, 3, 1, 0),
(2104, 'pay:refund',  '退款管理', 2, 8, 4, 1, 0)
ON DUPLICATE KEY UPDATE
    permission_name = VALUES(permission_name),
    parent_id = VALUES(parent_id),
    sort = VALUES(sort);

-- 营销模块
INSERT INTO sys_permission 
(permission_id, permission_code, permission_name, permission_type, parent_id, sort, status, is_deleted) 
VALUES
(2201, 'discount:promotion:list',    '促销列表',     2, 7, 1,  1, 0),
(2202, 'discount:promotion:add',     '新增促销',     2, 7, 2,  1, 0),
(2203, 'discount:promotion:edit',    '修改促销',     2, 7, 3,  1, 0),
(2204, 'discount:promotion:delete',  '删除促销',     2, 7, 4,  1, 0),
(2205, 'discount:coupon:list',       '券列表',       2, 7, 5,  1, 0),
(2206, 'discount:coupon:add',        '新增券',       2, 7, 6,  1, 0),
(2207, 'discount:coupon:edit',       '修改券',       2, 7, 7,  1, 0),
(2208, 'discount:coupon:delete',     '删除券',       2, 7, 8,  1, 0),
(2209, 'discount:coupon:issue',      '发券',         2, 7, 9,  1, 0),
(2210, 'discount:coupon:revoke',     '回收券',       2, 7, 10, 1, 0),
(2211, 'discount:rule:list',         '折扣规则列表', 2, 7, 11, 1, 0),
(2212, 'discount:rule:add',          '新增折扣规则', 2, 7, 12, 1, 0),
(2213, 'discount:rule:edit',         '修改折扣规则', 2, 7, 13, 1, 0),
(2214, 'discount:rule:delete',       '删除折扣规则', 2, 7, 14, 1, 0)
ON DUPLICATE KEY UPDATE
    permission_name = VALUES(permission_name),
    parent_id = VALUES(parent_id),
    sort = VALUES(sort);
```

**给 SUPER_ADMIN 分配：**

```
INSERT IGNORE INTO sys_role_permission (id, role_id, permission_id) 
SELECT (SELECT MAX(id) FROM sys_role_permission) + ROW_NUMBER() OVER (ORDER BY permission_id), 1, permission_id
FROM sys_permission
WHERE permission_code LIKE 'store:%' 
   OR permission_code LIKE 'pay:%' 
   OR permission_code LIKE 'discount:%';
```

## 10.13 权限编码命名规范总结

| 规范          | 示例                                        |
| :------------ | :------------------------------------------ |
| 模块前缀      | `store:`、`product:`、`order:`              |
| 资源 + 操作   | `store:add`、`product:edit`                 |
| 子资源 + 操作 | `store:region:add`、`discount:coupon:issue` |
| 全小写        | ✅ `store:list`                              |
| 驼峰或大写    | ❌ `store:List`、`Store:list`                |
| 分隔符用冒号  | ✅ `store:region:list`                       |

**`@PreAuthorize` 里用的字符串必须和数据库 `permission_code` 完全一致。**


---

## 11. 超级管理员说明

`SUPER_ADMIN` 角色拥有系统全部权限，**不需要在 `sys_role_permission` 表里逐个分配权限**。

后端在权限校验时，识别到当前用户的角色包含 `SUPER_ADMIN` 时，返回全部权限列表；`@PreAuthorize` 按正常逻辑校验即可通过。

---

## 12. cURL 测试示例

```bash
# 0. 登录拿 token（网关地址）
curl -X POST http://localhost/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123","userType":2}'

# 存 token
TOKEN=eyJhbGciOiJIUzI1NiJ9...

# 1. 分页查询角色
curl "http://localhost/api/v1/admin/role/list?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN"

# 2. 查询角色详情
curl http://localhost/api/v1/admin/role/4 \
  -H "Authorization: Bearer $TOKEN"

# 3. 新增角色
curl -X POST http://localhost/api/v1/admin/role \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"roleCode":"TEST_ROLE","roleName":"测试角色","userType":3,"dataScope":3}'

# 4. 修改角色
curl -X PUT http://localhost/api/v1/admin/role \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"roleId":4,"roleName":"店长（改）","status":1}'

# 5. 删除角色
curl -X DELETE http://localhost/api/v1/admin/role/4 \
  -H "Authorization: Bearer $TOKEN"

# 6. 给角色分配权限
curl -X POST http://localhost/api/v1/admin/role/assign-permission \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"roleId":4,"permissionIds":[2,1101,5,1401,1402]}'

# 7. 查询权限树
curl http://localhost/api/v1/admin/permission/tree \
  -H "Authorization: Bearer $TOKEN"

# 8. 查询角色已分配权限
curl http://localhost/api/v1/admin/permission/role/4 \
  -H "Authorization: Bearer $TOKEN"
```