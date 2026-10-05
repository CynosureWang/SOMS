# SOMS 库存服务接口文档

> **服务名称**：`service-stock`
> **服务端口**：`8060`
> **基础路径**：`http://127.0.0.1:8060`
> **网关路径**：`http://localhost/api/v1/stock`
> **版本**：`1.1.0`
> **首次版本日期**：2026-10-03

---

## 1. 数据字典

### 1.1 流水类型（flowType）

| 值 | 含义 |
|---|---|
| 1 | 入库 |
| 2 | 出库 |
| 3 | 盘点调整 |
| 4 | 销售锁定 |
| 5 | 销售解锁 |
| 6 | 销售扣减 |
| 7 | 退货入库 |
| 8 | 报损出库 |

### 1.2 业务类型（bizType）

| 值 | 含义 |
|---|---|
| 1 | 采购 |
| 2 | 订单 |
| 3 | 盘点 |
| 4 | 调拨 |
| 5 | 退货 |
| 6 | 手工 |

### 1.3 预警状态（warnStatus）

| 值 | 含义 |
|---|---|
| 1 | 正常 |
| 2 | 预警 |
| 3 | 缺货 |

---

## 2. 数据模型

### 2.1 StoreStock（门店库存）

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| storeId | Long | 门店ID |
| productId | Long | 商品ID |
| productCode | String | 商品编码快照 |
| productName | String | 商品名快照 |
| quantity | BigDecimal | 总库存（含锁定） |
| lockedQuantity | BigDecimal | 锁定数量 |
| availableQuantity | BigDecimal | 可用 = quantity - locked（生成列） |
| costPrice | BigDecimal | 成本价 |
| stockWarn | Integer | 库存预警值 |

### 2.2 StockFlow（库存流水）

| 字段 | 类型 | 说明 |
|---|---|---|
| flowId | Long | 流水ID |
| storeId | Long | 门店ID |
| productId | Long | 商品ID |
| productName | String | 商品名快照 |
| flowType | Integer | 流水类型 |
| changeQuantity | BigDecimal | 库存变动 |
| changeLocked | BigDecimal | 锁定变动 |
| beforeQuantity | BigDecimal | 变动前总库存 |
| afterQuantity | BigDecimal | 变动后总库存 |
| beforeLocked | BigDecimal | 变动前锁定 |
| afterLocked | BigDecimal | 变动后锁定 |
| bizType | Integer | 业务类型 |
| bizId | Long | 业务ID |
| bizNo | String | 业务单号 |
| operatorId | Long | 操作员ID |
| remark | String | 备注 |

---

## 3. 后台接口

### 3.1 分页查询库存

**GET** `/api/v1/stock/list`

**所需权限**：`stock:list`

**参数**：`pageNum`、`pageSize`、`storeId`、`productId`、`keyword`、`warnOnly`

**响应**：

```json
{
  "code": 0,
  "data": {
    "records": [
      {
        "id": 2106...,
        "storeId": 1,
        "productId": 1001,
        "productCode": "P0001",
        "productName": "可口可乐500ml",
        "quantity": 100.000,
        "lockedQuantity": 5.000,
        "availableQuantity": 95.000,
        "costPrice": 2.50,
        "stockWarn": 50,
        "warnStatus": 1,
        "gmtModified": "2026-10-03T13:43:08"
      }
    ],
    "total": 1
  }
}
```

### 3.2 入库

**POST** `/api/v1/stock/in`

**所需权限**：`stock:in`

**请求体**：

```json
{
  "storeId": 1,
  "productId": 1001,
  "quantity": 100,
  "costPrice": 2.50,
  "bizType": 6,
  "bizId": null,
  "bizNo": null,
  "operatorId": 1,
  "remark": "初始入库"
}
```

### 3.3 出库

**POST** `/api/v1/stock/out`

**所需权限**：`stock:out`

```json
{
  "storeId": 1,
  "productId": 1001,
  "quantity": 5,
  "bizType": 6,
  "remark": "报损"
}
```

> 出库严格校验，不允许扣到负库存。

### 3.4 查询流水

**GET** `/api/v1/stock/flow/list`

**所需权限**：`stock:list`

**参数**：`storeId`、`productId`、`flowType`、`pageNum`、`pageSize`

**响应**：

```json
{
  "code": 0,
  "data": {
    "records": [
      {
        "flowId": 2106...,
        "storeId": 1,
        "productId": 1001,
        "productName": "可口可乐500ml",
        "flowType": 4,
        "flowTypeName": "销售锁定",
        "changeQuantity": 0,
        "changeLocked": 5,
        "beforeQuantity": 100,
        "afterQuantity": 100,
        "beforeLocked": 0,
        "afterLocked": 5,
        "bizNo": "SO202610030001000010L",
        "gmtCreate": "2026-10-03T14:51:48"
      }
    ]
  }
}
```

---

## 4. 服务间调用接口

### 4.1 查询单商品库存

**GET** `/api/v1/stock/get?storeId=1&productId=1001`

**无需权限（内部调用）**

### 4.2 批量查询

**POST** `/api/v1/stock/list-by-products?storeId=1`

**请求体**：`[1001, 1002]`

### 4.3 锁定库存

**POST** `/api/v1/stock/lock`

**请求体**：

```json
{
  "storeId": 1,
  "orderId": 2106...,
  "orderNo": "SO202610030001000010L",
  "items": [
    {
      "productId": 1001,
      "productName": "可口可乐500ml",
      "quantity": 2
    }
  ]
}
```

**逻辑**：

1. 按 `productId` 排序加锁（防死锁）
2. 严格模式库存不足抛 `BusinessException`
3. 宽松模式允许负库存，打 warn 日志
4. 更新 `lockedQuantity`，写流水

### 4.4 扣减库存（支付成功调用）

**POST** `/api/v1/stock/deduct`

```json
{
  "storeId": 1,
  "orderId": 2106...,
  "orderNo": "SO202610030001000010L",
  "items": [
    {"productId": 1001, "productName": "可口可乐500ml", "quantity": 2}
  ]
}
```

**逻辑**：

1. `quantity -= x`
2. `lockedQuantity -= x`（解锁）

### 4.5 释放锁定（取消订单调用）

**POST** `/api/v1/stock/release`

```json
{
  "storeId": 1,
  "orderId": 2106...,
  "orderNo": "SO202610030001000010L",
  "items": [
    {"productId": 1001, "productName": "可口可乐500ml", "quantity": 2}
  ]
}
```

---

## 5. 库存模式说明

**严格模式（`stockMode=1`）**

- 下单锁库存时，可用库存不足直接拒绝
- 报错：`商品【xxx】库存不足，剩余：xx`

**宽松模式（`stockMode=2`）**

- 允许负库存销售
- 库存不足只打 warn 日志，不阻止交易
- 日志：`商品【xxx】库存不足，可用：xx，需求：xx，允许负库存销售`

---

## 6. 权限编码

| 编码         | 名称     |
| ------------ | -------- |
| `stock:list` | 库存查询 |
| `stock:in`   | 入库     |
| `stock:out`  | 出库     |

