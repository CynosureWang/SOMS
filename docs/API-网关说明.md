# SOMS 网关说明文档

> **服务名称**：`service-gateway`
> **服务端口**：`80`
> **版本**：`1.0.0`
> **首次版本日期**：2026-09-20

---

## 1. 网关职责

- 统一入口，所有前端/外部请求都经网关进入。
- JWT 校验。
- 白名单放行。
- 用户信息透传给下游。
- 网关签名（防伪造）。
- 跨域处理。

---

## 2. 路由配置

| 路径前缀               | 转发到             |
|------------------------|--------------------|
| `/api/v1/auth/**`      | `service-auth`     |
| `/api/v1/admin/**`     | `service-admin`    |
| `/api/v1/customer/**`  | `service-customer` |
| `/api/v1/store/**`     | `service-store`    |
| `/api/v1/product/**`   | `service-product`  |
| ...                    | ...                |

> 各业务服务统一使用 `/api/v1/{服务名}/**` 前缀。

---

## 3. 白名单

以下路径**不校验 token**，直接放行：

| 路径                             | 说明             |
|----------------------------------|------------------|
| `/api/v1/auth/login`             | 登录             |
| `/api/v1/auth/refresh`           | 刷新 token       |
| `/api/v1/auth/sms/send`          | 发送验证码       |
| `/api/v1/customer/auth/**`       | 会员认证相关     |
| `/actuator/**`                   | 健康检查         |
| `/favicon.ico`                   | 图标             |

> 白名单配置在网关的 `application.yml` 的 `auth.whitelist` 下。

---

## 4. 透传的 Header

网关校验 token 通过后，会在转发请求时注入以下 header：

| Header              | 说明                          |
|---------------------|-------------------------------|
| `X-User-Id`         | 业务用户ID                    |
| `X-User-Type`       | 用户类型：1会员 2管理员 3员工 |
| `X-Roles`           | 角色编码列表，逗号分隔        |
| `X-Gateway-Timestamp` | 签名时间戳（毫秒）          |
| `X-Gateway-Sign`    | HMAC-SHA256 签名              |

### 签名规则

```
signContent = userId + "|" + userType + "|" + roles + "|" + timestamp
sign = HMAC-SHA256(secret, signContent) 的十六进制小写
```

**下游服务必须校验签名**，通过才信任 header。

### 时间戳窗口

±5 分钟。超窗拒绝。

---

## 5. 未认证响应

网关校验失败时返回：

```json
{
  "code": 401,
  "message": "未登录 / token 无效或已过期 / token 已失效",
  "data": null
}
```

**HTTP 状态码固定 401**，业务码也是 401。

---

## 6. Token 黑名单

登出时，`service-auth` 会把 accessToken 加入 Redis：

```
key:   auth:blacklist:{token}
value: 1
ttl:   剩余有效期
```

网关每次校验都查一次 Redis，命中即拒绝。

---

## 7. 配置示例

```yaml
server:
  port: 80

spring:
  cloud:
    gateway:
      server:
        webflux:
          routes:
            - id: auth
              uri: lb://service-auth
              predicates:
                - Path=/api/v1/auth/**
            - id: admin
              uri: lb://service-admin
              predicates:
                - Path=/api/v1/admin/**

jwt:
  secret: ${JWT_SECRET}
  issuer: soms

gateway:
  sign:
    secret: ${GATEWAY_SIGN_SECRET}

auth:
  whitelist:
    - /api/v1/auth/login
    - /api/v1/auth/refresh
    - /actuator/**
```

---

## 8. 安全提醒

- **生产环境只暴露网关端口**，其他服务端口只允许内网访问。
- **`jwt.secret` 和 `gateway.sign.secret` 放 Nacos 配置中心**，不要提交 Git。
- **网关和各服务的签名密钥必须一致**。
- **保证 NTP 时间同步**，否则签名时间戳校验会失败。