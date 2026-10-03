# SOMS 订单服务接口文档

> **服务名称**：`service-order`
> **服务端口**：`8070`
> **基础路径**：`http://127.0.0.1:8070`
> **网关路径**：`http://localhost/api/v1/order`
> **版本**：`1.0.0`
> **首次版本日期**：2026-10-03

---

## 1. 数据字典

### 1.1 订单状态（status）

| 值 | 含义 |
|---|---|
| 1 | 待支付 |
| 2 | 已支付 |
| 3 | 已完成 |
| 4 | 已取消 |
| 5 | 退款中 |
| 6 | 已退款 |

### 1.2 支付状态（payStatus）

| 值 | 含义 |
|---|---|
| 0 | 未支付 |
| 1 | 已支付 |
| 2 | 部分退款 |
| 3 | 已退款 |

### 1.3 订单类型（orderType）

| 值 | 含义 |
|---|---|
| 1 | POS |
| 2 | 线上 |
| 3 | 自助收银 |
| 4 | 外卖 |
| 5 | 预订 |

### 1.4 支付类型（payType）

| 值 | 含义 |
|---|---|
| 1 | 现金 |
| 2 | 微信 |
| 3 | 支付宝 |
| 4 | 银行卡 |
| 5 | 会员余额 |

---

## 2. 订单号规则

SO + yyyyMMdd + 门店ID后4位 + 6位序列 + 1位校验位

```
**示例**：SO202610030001000010L
```

| 段 | 值 | 说明 |
|---|---|---|
| SO | SO | 固定前缀 |
| 20261003 | 20261003 | 日期 |
| 0001 | 0001 | 门店ID后4位 |
| 000010 | 000010 | 当日序列 |
| L | L | 校验位 |

**Redis key**：`order:no:seq:{date}:{storePart}`，每天自动重置，25 小时过期。

---

## 3. 数据模型

### 3.1 Order（订单主表）

| 字段 | 类型 | 说明 |
|---|---|---|
| orderId | Long | 订单ID（雪花，预生成） |
| orderNo | String | 订单号 |
| storeId | Long | 门店ID |
| customerId | Long | 会员ID |
| orderType | Integer | 订单类型 |
| totalAmount | BigDecimal | 商品总额 |
| discountAmount | BigDecimal | 优惠金额 |
| payAmount | BigDecimal | 应付金额 |
| paidAmount | BigDecimal | 实付金额 |
| itemCount | Integer | 商品件数 |
| status | Integer | 订单状态 |
| payStatus | Integer | 支付状态 |
| payType | Integer | 支付类型 |
| payTime | DateTime | 支付时间 |
| finishTime | DateTime | 完成时间 |
| cancelTime | DateTime | 取消时间 |
| cancelReason | String | 取消原因 |
| remark | String | 备注 |

### 3.2 OrderItem（订单明细）

| 字段 | 类型 | 说明 |
|---|---|---|
| itemId | Long | 明细ID |
| orderId | Long | 订单ID |
| orderNo | String | 订单号 |
| productId | Long | 商品ID |
| barcode | String | 条码快照 |
| scaleLabelId | Long | 称重价签ID |
| productName | String | 商品名快照 |
| specText | String | 规格快照 |
| unit | String | 单位快照 |
| mainImage | String | 图片快照 |
| categoryId | Long | 分类ID快照 |
| isWeight | Integer | 是否称重 |
| price | BigDecimal | 单价快照 |
| quantity | BigDecimal | 数量 |
| totalAmount | BigDecimal | 小计 |
| discountAmount | BigDecimal | 分摊优惠 |
| payAmount | BigDecimal | 实付 |
| promotionId | Long | 促销ID |
| promotionName | String | 促销名快照 |
| refundQuantity | BigDecimal | 已退数量 |
| refundAmount | BigDecimal | 已退金额 |

### 3.3 OrderStatusLog（状态日志）

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| orderId | Long | 订单ID |
| orderNo | String | 订单号 |
| fromStatus | Integer | 变更前状态 |
| toStatus | Integer | 变更后状态 |
| operatorId | Long | 操作人ID |
| operatorType | Integer | 1用户 2系统 3管理员 |
| remark | String | 备注 |

---

## 4. 状态机

待支付(1) ──支付成功──→ 已支付(2) ──完成──→ 已完成(3)
    │                       │
    │取消                │退款
    ↓                       ↓
已取消(4)              退款中(5) → 已退款(6)
**POS/自助收银**：支付成功直接进入已完成。

**线上/外卖**：支付成功进入已支付，收货后完成。

---

## 5. 接口详情

### 5.1 创建订单

**POST** `/api/v1/order`

**所需权限**：`order:add`

**请求体**：

```json
{
  "storeId": 1,
  "customerId": null,
  "orderType": 1,
  "items": [
    {
      "productId": 2106052145707646978,
      "barcode": null,
      "scaleLabelId": null,
      "quantity": 2
    }
  ],
  "remark": "测试订单"
}
```

**响应**：

```json
{
  "code": 0,
  "data": {
    "orderId": 2106276005405249538,
    "orderNo": "SO202610030001000010L",
    "storeId": 1,
    "orderType": 1,
    "orderTypeName": "POS",
    "totalAmount": 7.00,
    "discountAmount": 0,
    "payAmount": 7.00,
    "paidAmount": 0,
    "itemCount": 2,
    "status": 1,
    "statusName": "待支付",
    "items": [
      {
        "itemId": 2106276010824290306,
        "productId": 2106052145707646978,
        "productName": "可口可乐500ml",
        "unit": "瓶",
        "isWeight": 0,
        "price": 3.50,
        "quantity": 2,
        "totalAmount": 7.00,
        "discountAmount": 0,
        "payAmount": 7.00,
        "refundQuantity": 0,
        "refundAmount": 0
      }
    ]
  }
}
```

**流程**：

1. Feign 调 product 查商品信息，构建明细
2. 预生成 `orderId` 和 `orderNo`
3. Feign 调 stock **锁定库存**（用预生成的 orderId）
4. 插入 `order` + `order_item`
5. 写状态日志
6. 失败时调 stock 释放库存

### 5.2 分页查询

**GET** `/api/v1/order/list`

**所需权限**：`order:list`

**参数**：

| 参数       | 类型    | 说明     |
| ---------- | ------- | -------- |
| pageNum    | Integer | 页码     |
| pageSize   | Integer | 每页数量 |
| orderNo    | String  | 订单号   |
| storeId    | Long    | 门店ID   |
| customerId | Long    | 会员ID   |
| status     | Integer | 订单状态 |
| orderType  | Integer | 订单类型 |
| startDate  | Date    | 开始日期 |
| endDate    | Date    | 结束日期 |

### 5.3 订单详情

**GET** `/api/v1/order/{orderId}`

**所需权限**：`order:detail`

### 5.4 按订单号查询

**GET** `/api/v1/order/no/{orderNo}`

**所需权限**：`order:detail`

### 5.5 取消订单

**POST** `/api/v1/order/cancel`

**所需权限**：`order:cancel`

```json
{
  "orderId": 2106276005405249538,
  "reason": "用户取消"
}
```

**流程**：

1. 校验状态必须是"待支付"
2. Feign 调 stock 释放库存
3. 更新状态为"已取消"
4. 写状态日志

**异常**：`只有待支付订单可以取消`

### 5.6 完成订单

**POST** `/api/v1/order/finish/{orderId}`

**所需权限**：`order:edit`

> 只对"已支付"状态有效。

---

## 6. 内部接口

### 6.1 支付成功通知（pay 调）

**POST** `/api/v1/order/internal/pay-success`

**不加 `@PreAuthorize`**

**请求体**：

```json
{
  "orderNo": "SO202610030001000010L",
  "payType": 1,
  "paidAmount": 7.00
}
```

**流程**：

1. 校验订单存在且状态为"待支付"
2. Feign 调 stock **扣减库存**
3. 更新订单为"已支付"，记录 `payTime`、`payType`、`paidAmount`
4. 写状态日志
5. **POS/自助收银订单直接进入"已完成"**

**异常**：

- `订单不存在`
- `订单状态不允许支付`

---

## 7. 定时任务

### 7.1 超时未支付订单自动取消

**cron**：`0 * * * * ?`（每分钟）

**逻辑**：

1. 扫描 `status = 1` 且 `gmtCreate < now - 15min` 的订单
2. 逐个调 stock 释放库存
3. 更新状态为"已取消"
4. 写状态日志

**超时时间**：15 分钟（硬编码 `TIMEOUT_MINUTES`）

---

## 8. Feign 调用关系

```
order ──→ product
  │         GET /api/v1/product/internal/{productId}
  │
  └──→ stock
        POST /api/v1/stock/lock
        POST /api/v1/stock/deduct
        POST /api/v1/stock/release
```

---

## 9. 权限编码

| 编码           | 名称     |
| -------------- | -------- |
| `order:list`   | 订单列表 |
| `order:detail` | 订单详情 |
| `order:add`    | 创建订单 |
| `order:cancel` | 取消订单 |
| `order:edit`   | 修改订单 |
| `order:refund` | 订单退款 |