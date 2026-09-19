# SOMS 内部服务契约文档

> **版本**：`1.0.0`
> **首次版本日期**：2026-09-20
> **适用范围**：仅限 SOMS 内部服务间调用

---

## 1. 说明

本文档描述 SOMS 各微服务之间的**内部调用接口**，这些接口**不对外暴露**，仅供内部服务通过 Feign 调用。

**安全约定**：

- 网关**不路由** `/internal/**` 路径。
- 内部接口所在服务的端口**只允许内网指定机器访问**。
- 内部接口**不加 `@PreAuthorize`**，因为网关层已隔离。

---

## 2. service-admin 提供的内部接口

### 2.1 查询用户角色和权限

**GET** `/api/v1/admin/internal/user-auth`

**调用方**：`service-auth`（登录时调用）

#### 查询参数

| 参数       | 类型      | 必填 | 说明                              |
|------------|-----------|------|-----------------------------------|
| `userId`   | `Long`    | 是   | 业务用户ID（admin_id 或 employee_id） |
| `userType` | `Integer` | 是   | 2管理员 3员工                     |

#### 请求示例

```
GET /api/v1/admin/internal/user-auth?userId=1&userType=2
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "userId": 1,
    "userType": 2,
    "roles": ["SUPER_ADMIN"],
    "permissions": ["system", "system:role:list", "system:permission:list"],
    "dataScope": 1,
    "storeIds": []
  }
}
```

#### 字段说明

| 字段          | 类型       | 说明                                   |
|---------------|------------|----------------------------------------|
| `userId`      | `Long`     | 业务用户ID                             |
| `userType`    | `Integer`  | 用户类型                               |
| `roles`       | `String[]` | 角色编码列表                           |
| `permissions` | `String[]` | 权限编码列表                           |
| `dataScope`   | `Integer`  | 数据范围：1全部 2本区域 3本门店 4本人  |
| `storeIds`    | `Long[]`   | 员工能管的门店ID，管理员为空           |

#### 特殊逻辑

- **`SUPER_ADMIN`**：返回 `sys_permission` 表全部权限。
- **`userType=1`（会员）**：返回空 roles 和空 permissions，`dataScope=4`。
- **`userType=4`（供应商）**：同上。
- **无角色的用户**：返回空 roles 和空 permissions，`dataScope=4`。

---

## 3. Feign 客户端声明

Feign 客户端统一放在 `com.mfnit.common.api.client` 包下。

```java
@FeignClient(value = "service-admin", path = "/api/v1/admin/internal")
public interface AdminUserFeignClient {

    @GetMapping("/user-auth")
    Result<UserAuthDTO> getUserAuth(@RequestParam("userId") Long userId,
                                     @RequestParam("userType") Integer userType);
}
```

对应 DTO：`com.mfnit.common.api.dto.auth.UserAuthDTO`

---

## 4. 调用关系图

```
service-auth
    │
    │ 登录时调用
    ↓
service-admin  /api/v1/admin/internal/user-auth
    │
    │ 返回 roles / permissions / dataScope / storeIds
    ↓
service-auth 写入 Redis: auth:perms:{userId}
    │
    ↓
gateway 透传 X-User-Id 到下游
    │
    ↓
各业务服务从 Redis 读权限
```