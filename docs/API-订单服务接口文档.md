# SOMS 订单服务接口文档

> **服务名称**：`service-order`
> **服务端口**：`8070`
> **基础路径**：`http://127.0.0.1:8070`
> **网关路径**：`http://localhost/api/v1/order`
> **版本**：`1.1.1`
> **首次版本日期**：2026-10-03
> **更新日期**：2026-10-05

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

| 状态码 | 说明              |
| ------ | ----------------- |
| `0`    | 成功              |
| `400`  | 业务异常          |
| `401`  | 未登录 / 参数错误 |
| `403`  | 无权限            |
| `500`  | 系统异常          |

### 1.3 请求头要求

除内部接口外，所有接口必须携带 token：

```
Authorization: Bearer {accessToken}
```

---

## 2. 数据字典

### 2.1 订单状态（status）

| 值  | 含义     |
| --- | -------- |
| `1` | 待支付   |
| `2` | 已支付   |
| `3` | 已完成   |
| `4` | 已取消   |
| `5` | 退款中   |
| `6` | 已退款   |

### 2.2 支付状态（payStatus）

| 值  | 含义     |
| --- | -------- |
| `0` | 未支付   |
| `1` | 已支付   |
| `2` | 部分退款 |
| `3` | 已退款   |

### 2.3 订单类型（orderType）

| 值  | 含义     |
| --- | -------- |
| `1` | POS      |
| `2` | 线上     |
| `3` | 自助收银 |
| `4` | 外卖     |
| `5` | 预订     |

### 2.4 支付类型（payType）

| 值  | 含义     |
| --- | -------- |
| `1` | 现金     |
| `2` | 微信     |
| `3` | 支付宝   |
| `4` | 银行卡   |
| `5` | 会员余额 |

### 2.5 条码类型（barcodeType）

| 值  | 含义     |
| --- | -------- |
| `1` | 国标条码 |
| `2` | 店内码   |
| `3` | 秤码     |
| `4` | 自打价签 |

### 2.6 操作人类型（operatorType）

| 值  | 含义   |
| --- | ------ |
| `1` | 用户   |
| `2` | 系统   |
| `3` | 管理员 |

---

## 3. 订单号规则

```
SO + yyyyMMdd + 门店ID后4位 + 6位序列 + 1位校验位
```

**总长 21 位。**

**示例：**

```
SO202610050001000001X
```

| 段       | 值         | 说明       |
| -------- | ---------- | ---------- |
| `SO`     | SO         | 固定前缀   |
| `20261005` | 20261005 | 日期       |
| `0001`   | 0001       | 门店ID后4位 |
| `000001` | 000001     | 当日序列   |
| `X`      | X          | 校验位     |

**Redis key**：`order:no:seq:{date}:{storePart}`，每天自动重置，25 小时过期。

**校验位算法**：加权和取模 36，字符集 `0-9A-Z`。

---

## 4. 数据模型

### 4.1 Order（订单主表）

| 字段           | 类型            | 说明                       |
| -------------- | --------------- | -------------------------- |
| `orderId`      | `Long`          | 订单ID（雪花，预生成）     |
| `orderNo`      | `String`        | 订单号                     |
| `storeId`      | `Long`          | 门店ID                     |
| `customerId`   | `Long`          | 会员ID，散客为空           |
| `couponId`     | `Long`          | 使用的券ID                 |
| `orderType`    | `Integer`       | 订单类型                   |
| `totalAmount`  | `BigDecimal`    | 商品总额                   |
| `discountAmount` | `BigDecimal`  | 优惠金额                   |
| `payAmount`    | `BigDecimal`    | 应付金额                   |
| `paidAmount`   | `BigDecimal`    | 实付金额                   |
| `itemCount`    | `Integer`       | 商品件数                   |
| `status`       | `Integer`       | 订单状态                   |
| `payStatus`    | `Integer`       | 支付状态                   |
| `payType`      | `Integer`       | 支付类型                   |
| `payTime`      | `LocalDateTime` | 支付时间                   |
| `finishTime`   | `LocalDateTime` | 完成时间                   |
| `cancelTime`   | `LocalDateTime` | 取消时间                   |
| `cancelReason` | `String`        | 取消原因                   |
| `remark`       | `String`        | 备注                       |

### 4.2 OrderItem（订单明细）

| 字段             | 类型         | 说明                          |
| ---------------- | ------------ | ----------------------------- |
| `itemId`         | `Long`       | 明细ID                        |
| `orderId`        | `Long`       | 订单ID                        |
| `orderNo`        | `String`     | 订单号                        |
| `productId`      | `Long`       | 商品ID                        |
| `barcode`        | `String`     | 条码快照                      |
| `scaleLabelId`   | `Long`       | 称重价签ID                    |
| `productName`    | `String`     | 商品名快照                    |
| `specText`       | `String`     | 规格快照                      |
| `unit`           | `String`     | 单位快照                      |
| `mainImage`      | `String`     | 图片快照                      |
| `categoryId`     | `Long`       | 分类ID快照                    |
| `isWeight`       | `Integer`    | 是否称重 0否 1是              |
| `price`          | `BigDecimal` | 单价快照                      |
| `quantity`       | `BigDecimal` | 数量                          |
| `totalAmount`    | `BigDecimal` | 小计                          |
| `discountAmount` | `BigDecimal` | 分摊优惠                      |
| `payAmount`      | `BigDecimal` | 实付                          |
| `promotionId`    | `Long`       | 促销ID                        |
| `promotionName`  | `String`     | 促销名快照                    |
| `refundQuantity` | `BigDecimal` | 已退数量                      |
| `refundAmount`   | `BigDecimal` | 已退金额                      |

### 4.3 OrderStatusLog（订单状态日志）

| 字段           | 类型            | 说明                          |
| -------------- | --------------- | ----------------------------- |
| `id`           | `Long`          | 主键                          |
| `orderId`      | `Long`          | 订单ID                        |
| `orderNo`      | `String`        | 订单号                        |
| `fromStatus`   | `Integer`       | 变更前状态                    |
| `toStatus`     | `Integer`       | 变更后状态                    |
| `operatorId`   | `Long`          | 操作人ID                      |
| `operatorType` | `Integer`       | 1用户 2系统 3管理员           |
| `remark`       | `String`        | 备注                          |
| `gmtCreate`    | `LocalDateTime` | 创建时间                      |

---

## 5. 状态机

```
待支付(1) ──支付成功──→ 已支付(2) ──完成──→ 已完成(3)
    │                       │
    │取消                   │退款
    ↓                       ↓
已取消(4)              退款中(5) → 已退款(6)
```

**POS/自助收银**：支付成功直接进入已完成。

**线上/外卖**：支付成功进入已支付，收货后完成。

**状态流转规则：**

| 从状态 | 可流转到 |
| ------ | -------- |
| 待支付(1) | 已支付(2)、已取消(4) |
| 已支付(2) | 已完成(3)、退款中(5) |
| 已完成(3) | 退款中(5) |
| 退款中(5) | 已退款(6) |

---

## 6. 接口详情

### 6.1 创建订单

**POST** `/api/v1/order`

**所需权限**：`order:add`

**说明**：创建订单并锁定库存与券。

#### 请求头

```
Authorization: Bearer {accessToken}
Content-Type: application/json
```

#### 请求体

| 字段         | 类型        | 必填 | 说明                          |
| ------------ | ----------- | ---- | ----------------------------- |
| `storeId`    | `Long`      | 是   | 门店ID                        |
| `customerId` | `Long`      | 否   | 会员ID                        |
| `orderType`  | `Integer`   | 是   | 订单类型                      |
| `couponIds`  | `Long[]`    | 否   | 使用的券ID列表，暂支持一张    |
| `items`      | `Array`     | 是   | 商品列表                      |
| `remark`     | `String`    | 否   | 备注                          |

**items 元素：**

| 字段           | 类型         | 必填 | 说明                                 |
| -------------- | ------------ | ---- | ------------------------------------ |
| `productId`    | `Long`       | 是   | 商品ID                               |
| `barcode`      | `String`     | 否   | 条码                                 |
| `barcodeType`  | `Integer`    | 否   | 1国标 2店内码 3秤码 4自打价签，默认 1 |
| `scaleLabelId` | `Long`       | 否   | 称重价签ID                           |
| `quantity`     | `BigDecimal` | 是   | 数量，必须大于 0                     |

#### 请求体示例

```json
{
  "storeId": 1,
  "customerId": 1,
  "orderType": 1,
  "couponIds": [2106747678999876097],
  "items": [
    {
      "productId": 2106052145707646978,
      "barcode": "6901234567890",
      "barcodeType": 1,
      "quantity": 4
    }
  ],
  "remark": "测试订单"
}
```

#### 成功响应

**HTTP 200**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "orderId": 2106985371171164162,
    "orderNo": "SO2026100500010000034",
    "storeId": 1,
    "customerId": 1,
    "couponId": 2106747678999876097,
    "orderType": 1,
    "orderTypeName": "POS",
    "totalAmount": 14.00,
    "discountAmount": 5.80,
    "payAmount": 8.20,
    "paidAmount": 0,
    "itemCount": 4,
    "status": 1,
    "statusName": "待支付",
    "payStatus": 0,
    "payType": null,
    "payTime": null,
    "finishTime": null,
    "cancelTime": null,
    "cancelReason": null,
    "remark": "测试订单",
    "gmtCreate": "2026-10-05T13:50:33",
    "items": [
      {
        "itemId": 2106985374623076354,
        "productId": 2106052145707646978,
        "productName": "可口可乐500ml",
        "specText": null,
        "unit": "瓶",
        "mainImage": null,
        "isWeight": 0,
        "price": 3.50,
        "quantity": 4.000,
        "totalAmount": 14.00,
        "discountAmount": 5.80,
        "payAmount": 8.20,
        "promotionId": 2106746910958862338,
        "promotionName": "可口可乐满2件8折",
        "refundQuantity": 0,
        "refundAmount": 0
      }
    ]
  }
}
```

#### 业务异常

| 场景                    | code | message                                |
| ----------------------- | ---- | -------------------------------------- |
| 门店不存在              | 400  | `"门店不存在"`                         |
| 门店未营业              | 400  | `"门店未营业，无法下单"`               |
| 商品不存在              | 400  | `"商品不存在：2106052145707646978"`    |
| 商品已下架              | 400  | `"商品已下架：可口可乐500ml"`          |
| 计算优惠失败            | 400  | `"计算优惠失败：xxx"`                  |
| 锁定库存失败            | 400  | `"锁定库存失败：xxx"`                  |
| 锁定券失败              | 400  | `"锁定券失败：xxx"`                    |
| 促销不支持叠加券        | 400  | `"促销【xxx】不支持叠加优惠券"`        |
| 券不可叠加促销          | 400  | `"该券不可与促销活动叠加使用"`         |
| 未达到券使用门槛        | 400  | `"未达到券使用门槛：10.00"`            |
| 券不存在 / 不可用       | 400  | `"券不存在" / "券当前不可用"`          |

#### 流程说明

```
1. Feign 调 store 校验门店状态（非营业拒绝）
2. Feign 调 product 查商品信息
3. Feign 调 discount 计算优惠
4. 预生成 orderId（雪花）和 orderNo
5. Feign 调 stock 锁定库存
6. Feign 调 discount 锁定券
7. 保存 order + order_item
8. 写状态日志
9. 任一步失败，依次回滚：释放券 → 释放库存
```

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/order" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "storeId": 1,
    "customerId": 1,
    "orderType": 1,
    "couponIds": [2106747678999876097],
    "items": [
      {
        "productId": 2106052145707646978,
        "barcode": "6901234567890",
        "barcodeType": 1,
        "quantity": 4
      }
    ],
    "remark": "测试订单"
  }'
```

---

### 6.2 分页查询订单

**GET** `/api/v1/order/list`

**所需权限**：`order:list`

#### 请求头

```
Authorization: Bearer {accessToken}
```

#### 查询参数

| 参数         | 类型      | 必填 | 默认值 | 说明                  |
| ------------ | --------- | ---- | ------ | --------------------- |
| `pageNum`    | `Integer` | 否   | `1`    | 页码                  |
| `pageSize`   | `Integer` | 否   | `10`   | 每页数量              |
| `orderNo`    | `String`  | 否   | —      | 订单号（模糊）        |
| `storeId`    | `Long`    | 否   | —      | 门店ID                |
| `customerId` | `Long`    | 否   | —      | 会员ID                |
| `status`     | `Integer` | 否   | —      | 订单状态              |
| `orderType`  | `Integer` | 否   | —      | 订单类型              |
| `startDate`  | `Date`    | 否   | —      | 开始日期 `yyyy-MM-dd` |
| `endDate`    | `Date`    | 否   | —      | 结束日期 `yyyy-MM-dd` |

#### 请求示例

```
GET /api/v1/order/list?pageNum=1&pageSize=10&storeId=1&status=1
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
        "orderId": 2106985371171164162,
        "orderNo": "SO2026100500010000034",
        "storeId": 1,
        "customerId": 1,
        "orderType": 1,
        "orderTypeName": "POS",
        "totalAmount": 14.00,
        "discountAmount": 5.80,
        "payAmount": 8.20,
        "paidAmount": 0,
        "itemCount": 4,
        "status": 1,
        "statusName": "待支付",
        "payStatus": 0,
        "payType": null,
        "payTime": null,
        "gmtCreate": "2026-10-05T13:50:33"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

#### cURL 示例

```bash
curl "http://localhost/api/v1/order/list?pageNum=1&pageSize=10&storeId=1" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 6.3 订单详情

**GET** `/api/v1/order/{orderId}`

**所需权限**：`order:detail`

#### 路径参数

| 参数      | 类型   | 说明   |
| --------- | ------ | ------ |
| `orderId` | `Long` | 订单ID |

#### 成功响应

**HTTP 200**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "orderId": 2106985371171164162,
    "orderNo": "SO2026100500010000034",
    "storeId": 1,
    "customerId": 1,
    "couponId": 2106747678999876097,
    "orderType": 1,
    "orderTypeName": "POS",
    "totalAmount": 14.00,
    "discountAmount": 5.80,
    "payAmount": 8.20,
    "paidAmount": 0,
    "itemCount": 4,
    "status": 1,
    "statusName": "待支付",
    "payStatus": 0,
    "payType": null,
    "payTime": null,
    "finishTime": null,
    "cancelTime": null,
    "cancelReason": null,
    "remark": "测试订单",
    "gmtCreate": "2026-10-05T13:50:33",
    "items": [
      {
        "itemId": 2106985374623076354,
        "productId": 2106052145707646978,
        "productName": "可口可乐500ml",
        "specText": null,
        "unit": "瓶",
        "mainImage": null,
        "isWeight": 0,
        "price": 3.50,
        "quantity": 4.000,
        "totalAmount": 14.00,
        "discountAmount": 5.80,
        "payAmount": 8.20,
        "promotionId": 2106746910958862338,
        "promotionName": "可口可乐满2件8折",
        "refundQuantity": 0,
        "refundAmount": 0
      }
    ]
  }
}
```

#### 业务异常

| 场景       | code | message         |
| ---------- | ---- | --------------- |
| 订单不存在 | 400  | `"订单不存在"`  |

#### cURL 示例

```bash
curl "http://localhost/api/v1/order/2106985371171164162" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 6.4 按订单号查询

**GET** `/api/v1/order/no/{orderNo}`

**所需权限**：`order:detail`

#### 路径参数

| 参数      | 类型     | 说明   |
| --------- | -------- | ------ |
| `orderNo` | `String` | 订单号 |

#### 成功响应

同 6.3。

#### cURL 示例

```bash
curl "http://localhost/api/v1/order/no/SO2026100500010000034" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 6.5 取消订单

**POST** `/api/v1/order/cancel`

**所需权限**：`order:cancel`

**说明**：只有"待支付"状态可取消。取消时释放库存、释放券。

#### 请求体

| 字段      | 类型     | 必填 | 说明     |
| --------- | -------- | ---- | -------- |
| `orderId` | `Long`   | 是   | 订单ID   |
| `reason`  | `String` | 否   | 取消原因 |

#### 请求体示例

```json
{
  "orderId": 2106985371171164162,
  "reason": "用户取消"
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

| 场景                   | code | message                     |
| ---------------------- | ---- | --------------------------- |
| 订单不存在             | 400  | `"订单不存在"`              |
| 状态不是待支付         | 400  | `"只有待支付订单可以取消"`  |
| 释放库存失败           | 400  | `"释放库存失败：xxx"`       |

#### 流程说明

```
1. 校验订单存在且状态为"待支付"
2. Feign 调 stock 释放库存
3. Feign 调 discount 释放券（如果有 couponId）
4. 更新状态为"已取消"
5. 写状态日志
```

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/order/cancel" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"orderId": 2106985371171164162, "reason": "用户取消"}'
```

---

### 6.6 完成订单

**POST** `/api/v1/order/finish/{orderId}`

**所需权限**：`order:edit`

**说明**：只对"已支付"状态有效。

#### 路径参数

| 参数      | 类型   | 说明   |
| --------- | ------ | ------ |
| `orderId` | `Long` | 订单ID |

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": null
}
```

#### 业务异常

| 场景               | code | message                     |
| ------------------ | ---- | --------------------------- |
| 订单不存在         | 400  | `"订单不存在"`              |
| 状态不是已支付     | 400  | `"只有已支付订单可以完成"`  |

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/order/finish/2106985371171164162" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 7. 内部接口

### 7.1 支付成功通知（pay 调）

**POST** `/api/v1/order/internal/pay-success`

**不加 `@PreAuthorize`，网关不路由 `/internal/**`。**

#### 请求头

```
Content-Type: application/json
```

#### 请求体

| 字段         | 类型         | 必填 | 说明     |
| ------------ | ------------ | ---- | -------- |
| `orderNo`    | `String`     | 是   | 订单号   |
| `payType`    | `Integer`    | 是   | 支付类型 |
| `paidAmount` | `BigDecimal` | 是   | 实付金额 |

#### 请求体示例

```json
{
  "orderNo": "SO2026100500010000034",
  "payType": 1,
  "paidAmount": 8.20
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

| 场景             | code | message                     |
| ---------------- | ---- | --------------------------- |
| 订单不存在       | 400  | `"订单不存在"`              |
| 状态不允许支付   | 400  | `"订单状态不允许支付"`      |
| 扣减库存失败     | 400  | `"扣减库存失败：xxx"`       |

#### 流程说明

```
1. 校验订单存在且状态为"待支付"
2. Feign 调 stock 扣减库存
3. Feign 调 discount 核销券（如果有 couponId）
4. 更新订单为"已支付"，记录 payTime、payType、paidAmount
5. 写状态日志
6. POS/自助收银订单直接进入"已完成"
```

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/order/internal/pay-success" \
  -H "Content-Type: application/json" \
  -d '{
    "orderNo": "SO2026100500010000034",
    "payType": 1,
    "paidAmount": 8.20
  }'
```

---

## 8. 定时任务

### 8.1 超时未支付订单自动取消

**cron**：`0 * * * * ?`（每分钟）

**逻辑**：

1. 扫描 `status = 1` 且 `gmtCreate < now - 15min` 的订单
2. 逐个调 stock 释放库存
3. 调 discount 释放券
4. 更新状态为"已取消"，`cancelReason = "超时未支付，系统自动取消"`
5. 写状态日志

**超时时间**：15 分钟（硬编码 `TIMEOUT_MINUTES`）

---

## 9. Feign 调用关系

```
order ──→ product
  │         GET /api/v1/product/internal/{productId}
  │
  ├──→ store
  │         GET /api/v1/store/internal/{storeId}
  │         GET /api/v1/store/internal/{storeId}/can-trade
  │
  ├──→ stock
  │         POST /api/v1/stock/lock
  │         POST /api/v1/stock/deduct
  │         POST /api/v1/stock/release
  │
  └──→ discount
            POST /api/v1/discount/internal/calculate
            POST /api/v1/discount/coupon/internal/lock
            POST /api/v1/discount/coupon/internal/use
            POST /api/v1/discount/coupon/internal/release
```

**被调方：**

```
service-pay ──→ order  POST /api/v1/order/internal/pay-success
```

---

## 10. 权限编码

| 编码           | 名称     |
| -------------- | -------- |
| `order:list`   | 订单列表 |
| `order:detail` | 订单详情 |
| `order:add`    | 创建订单 |
| `order:cancel` | 取消订单 |
| `order:edit`   | 修改订单 |
| `order:refund` | 订单退款 |

---

## 11. cURL 完整测试流程

```bash
# 0. 登录拿 token
curl -X POST http://localhost/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123","userType":2}'

TOKEN=eyJhbGciOiJIUzI1NiJ9...

# 1. 创建订单（不带券）
curl -X POST "http://localhost/api/v1/order" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "storeId": 1,
    "orderType": 1,
    "items": [
      {"productId": 2106052145707646978, "barcodeType": 1, "quantity": 1}
    ]
  }'

# 2. 创建订单（带券）
curl -X POST "http://localhost/api/v1/order" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "storeId": 1,
    "customerId": 1,
    "orderType": 1,
    "couponIds": [2106747678999876097],
    "items": [
      {"productId": 2106052145707646978, "barcodeType": 1, "quantity": 4}
    ]
  }'

# 3. 分页查询
curl "http://localhost/api/v1/order/list?pageNum=1&pageSize=10&storeId=1" \
  -H "Authorization: Bearer $TOKEN"

# 4. 订单详情
curl "http://localhost/api/v1/order/2106985371171164162" \
  -H "Authorization: Bearer $TOKEN"

# 5. 按订单号查询
curl "http://localhost/api/v1/order/no/SO2026100500010000034" \
  -H "Authorization: Bearer $TOKEN"

# 6. 支付成功通知（内部接口）
curl -X POST "http://localhost/api/v1/order/internal/pay-success" \
  -H "Content-Type: application/json" \
  -d '{"orderNo":"SO2026100500010000034","payType":1,"paidAmount":8.20}'

# 7. 取消订单
curl -X POST "http://localhost/api/v1/order/cancel" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"orderId":2106985371171164162,"reason":"用户取消"}'

# 8. 完成订单
curl -X POST "http://localhost/api/v1/order/finish/2106985371171164162" \
  -H "Authorization: Bearer $TOKEN"
```