# SOMS 内部服务契约文档

> **版本**：`2.0.0`
> **更新日期**：2026-10-05
> **适用范围**：仅限 SOMS 内部服务间调用

---

## 1. 说明

本文档描述 SOMS 各微服务之间的**内部调用接口**，这些接口**不对外暴露**，仅供内部服务通过 Feign 调用。

**安全约定**：

- 网关**不路由** `/internal/**` 路径。
- 内部接口所在服务的端口**只允许内网指定机器访问**。
- 内部接口**不加 `@PreAuthorize`**，因为网关层已隔离。

**Feign 客户端原则**：

- **Feign 客户端放调用方**，不放被调方，不放 common。
- 例：`order` 调 `product`，`ProductFeignClient` 放 `com.mfnit.order.client`。
- 服务间不共享 jar，DTO 在调用方定义镜像。

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

## 3. service-product 提供的内部接口

### 3.1 查询商品

**GET** `/api/v1/product/internal/{productId}`

**调用方**：`service-stock`、`service-order`

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "productId": 2106052145707646978,
    "productCode": "P0001",
    "productName": "可口可乐500ml",
    "specText": null,
    "unit": "瓶",
    "mainImage": null,
    "categoryId": 100,
    "brandId": null,
    "price": 3.50,
    "stockMode": 1,
    "isWeight": 0,
    "status": 1,
    "stockWarn": 50
  }
}
```

---

## 4. service-stock 提供的内部接口

### 4.1 锁定库存

**POST** `/api/v1/stock/lock`

**调用方**：`service-order`

```json
{
  "storeId": 1,
  "orderId": 2106985371171164162,
  "orderNo": "SO2026100500010000034",
  "items": [
    {"productId": 2106052145707646978, "productName": "可口可乐500ml", "quantity": 4}
  ]
}
```

### 4.2 扣减库存

**POST** `/api/v1/stock/deduct`

**调用方**：`service-order`（支付成功时）

请求体同 `lock`。

### 4.3 释放库存

**POST** `/api/v1/stock/release`

**调用方**：`service-order`（取消订单时）

请求体同 `lock`。

### 4.4 查询库存

**GET** `/api/v1/stock/get?storeId=1&productId=xxx`

**调用方**：`service-order`

---

## 5. service-order 提供的内部接口

### 5.1 支付成功通知

**POST** `/api/v1/order/internal/pay-success`

**调用方**：`service-pay`（支付成功时）

```json
{
  "orderNo": "SO2026100500010000034",
  "payType": 1,
  "paidAmount": 8.20
}
```

---

## 6. service-discount 提供的内部接口

### 6.1 计算优惠

**POST** `/api/v1/discount/internal/calculate`

**调用方**：`service-order`（下单时）

请求体和响应见 `API-优惠服务.md` 第 7.1 节。

### 6.2 锁定券

**POST** `/api/v1/discount/coupon/internal/lock?couponId=x&orderId=x&orderNo=x`

**调用方**：`service-order`

### 6.3 核销券

**POST** `/api/v1/discount/coupon/internal/use?couponId=x`

**调用方**：`service-order`（支付成功时）

### 6.4 释放券

**POST** `/api/v1/discount/coupon/internal/release?couponId=x`

**调用方**：`service-order`（取消订单时）

---

## 7. service-store 提供的内部接口

### 7.1 查询门店

**GET** `/api/v1/store/internal/{storeId}`

**调用方**：`service-order`、`service-stock`

### 7.2 批量查询门店

**POST** `/api/v1/store/internal/list-by-ids`

**调用方**：`service-order`、`service-stock`

请求体：`[1, 2, 3]`

### 7.3 校验门店是否可营业

**GET** `/api/v1/store/internal/{storeId}/can-trade`

**调用方**：`service-order`

返回 `data: true/false`。

---

## 8. 服务调用关系图

```
service-auth
    │ 登录时调
    ↓
service-admin  /internal/user-auth

service-order
    │ 下单时调
    ├──→ service-product  /internal/{productId}
    ├──→ service-store    /internal/{storeId}
    │                     /internal/{storeId}/can-trade
    ├──→ service-discount /internal/calculate
    │                     /coupon/internal/lock
    └──→ service-stock    /lock
    │ 支付成功时调
    ├──→ service-stock    /deduct
    └──→ service-discount /coupon/internal/use
    │ 取消时调
    ├──→ service-stock    /release
    └──→ service-discount /coupon/internal/release

service-pay
    │ 支付成功时调
    ↓
service-order  /internal/pay-success

service-stock
    │ 查询商品信息时调
    ↓
service-product  /internal/{productId}
```