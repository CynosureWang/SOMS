# SOMS 认证服务接口文档

> **服务名称**：`service-auth`
> **服务端口**：`8090`
> **基础路径**：`http://127.0.0.1:8090`
> **网关路径**：`http://localhost/api/v1/auth`
> **版本**：`1.0.0`
> **首次版本日期**：2026-09-19

---

## 1. 通用说明

### 1.1 统一响应格式

与后台管理服务一致：

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {}
}
```

### 1.2 状态码说明

| 状态码 | 说明               |
| ------ | ------------------ |
| `0`    | 成功               |
| `400`  | 业务异常           |
| `401`  | 参数错误 / 未登录  |
| `500`  | 系统异常           |

### 1.3 Token 说明

- **accessToken**：用于访问受保护接口，放在 `Authorization: Bearer {accessToken}`。
- **refreshToken**：用于刷新 accessToken，只调 `/refresh` 用。
- **有效期**：
  - 管理员 / 员工：8 小时
  - 会员：7 天
  - refreshToken：30 天

---

## 2. 数据字典 — 用户类型

| 值  | 含义   |
|-----|--------|
| `1` | 会员   |
| `2` | 管理员 |
| `3` | 员工   |
| `4` | 供应商 |

---

## 3. 接口详情

### 3.1 账号密码登录

**POST** `/api/v1/auth/login`

#### 请求体

| 字段       | 类型      | 必填 | 说明         |
|------------|-----------|------|--------------|
| `username` | `String`  | 是   | 登录账号     |
| `password` | `String`  | 是   | 密码（明文） |
| `userType` | `Integer` | 是   | 用户类型     |

#### 请求体示例

```json
{
  "username": "admin",
  "password": "admin123",
  "userType": 2
}
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "3f2a8b...",
    "tokenType": "Bearer",
    "expiresIn": 28800,
    "userId": 1,
    "userType": 2,
    "roles": ["SUPER_ADMIN"]
  }
}
```

| 字段           | 类型       | 说明                               |
| -------------- | ---------- | ---------------------------------- |
| `accessToken`  | `String`   | 访问令牌                           |
| `refreshToken` | `String`   | 刷新令牌                           |
| `tokenType`    | `String`   | 固定为 `Bearer`                    |
| `expiresIn`    | `Long`     | accessToken 剩余有效秒数           |
| `userId`       | `Long`     | 业务用户ID                         |
| `userType`     | `Integer`  | 用户类型                           |
| `roles`        | `String[]` | 角色编码列表，前端可用于菜单控制   |

#### 业务异常

| 场景           | code | message              |
|----------------|------|----------------------|
| 账号不存在     | 400  | `"账号或密码错误"`   |
| 密码错误       | 400  | `"账号或密码错误"`   |
| 账号已锁定/禁用 | 400 | `"账号已锁定或禁用"` |

> 登录失败会写入 `auth_login_log`，用于审计。

---

### 3.2 登出

**POST** `/api/v1/auth/logout`

将当前 accessToken 加入黑名单，直到其自然过期。

#### 请求头

```
Authorization: Bearer {accessToken}
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": null
}
```

---

### 3.3 刷新 Token

**POST** `/api/v1/auth/refresh`

#### 查询参数

| 参数           | 类型     | 必填 | 说明              |
|----------------|----------|------|-------------------|
| `refreshToken` | `String` | 是   | 登录时返回的刷新令牌 |

#### 请求示例

```
POST /api/v1/auth/refresh?refreshToken=3f2a8b...
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "3f2a8b...",
    "expiresIn": 28800,
    "userId": 1,
    "userType": 2
  }
}
```

#### 业务异常

| 场景                    | code | message                    |
|-------------------------|------|----------------------------|
| refreshToken 无效或过期 | 400  | `"refresh token 无效或已过期"` |
| 账号不可用              | 400  | `"账号不可用"`             |

---

## 4. cURL 测试示例

```bash
# 1. 登录
curl -X POST http://localhost/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123","userType":2}'

# 2. 登出
curl -X POST http://localhost/api/v1/auth/logout \
  -H "Authorization: Bearer $TOKEN"

# 3. 刷新
curl -X POST "http://localhost/api/v1/auth/refresh?refreshToken=$REFRESH_TOKEN"
```