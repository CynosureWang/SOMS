# SOMS 优惠服务接口文档

> **服务名称**：`service-discount`
> **服务端口**：`8020`
> **基础路径**：`http://127.0.0.1:8150`
> **网关路径**：`http://localhost/api/v1/discount`
> **版本**：`2.0.0`
> **首次版本日期**：2026-10-05

---

## 1. 通用说明

### 1.1 统一响应格式

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

### 1.4 数据字典

**促销类型（promotionType）**

| 值  | 含义     |
|-----|----------|
| `1` | 限时特价 |
| `2` | 连扫     |
| `3` | 会员折扣 |
| `4` | 优惠券   |
| `5` | 买赠     |

**动作类型（actionType）**

| 值  | 含义       | actionValue | actionExt |
|-----|------------|-------------|-----------|
| `1` | 打折       | 0.8（8折）  | —         |
| `2` | 减钱       | 20.00       | —         |
| `3` | 一口价     | 9.90        | —         |
| `4` | 第N件优惠  | 0.5         | `{"n":2}` |
| `5` | 买赠       | —           | `{"buyQty":2,"giftProductId":1002,"giftQty":1}` |
| `6` | 阶梯满减   | —           | `{"levels":[{"threshold":100,"reduce":20}]}` |

**范围类型（scopeType）**

| 值  | 含义 |
|-----|------|
| `1` | 全场 |
| `2` | 分类 |
| `3` | 品牌 |
| `4` | 商品 |
| `5` | 条码 |

**条件类型（conditionType）**

| 值  | 含义     |
|-----|----------|
| `1` | 数量     |
| `2` | 金额     |
| `3` | 时间     |
| `4` | 会员等级 |

**折扣规则类型（ruleType）**

| 值  | 含义     |
|-----|----------|
| `1` | 时段折扣 |
| `2` | 临期折扣 |

**券类型（couponType）**

| 值  | 含义   |
|-----|--------|
| `1` | 满减   |
| `2` | 折扣   |
| `3` | 单品   |

**券状态（couponStatus）**

| 值  | 含义   |
|-----|--------|
| `0` | 未使用 |
| `1` | 锁定中 |
| `2` | 已使用 |
| `3` | 已过期 |
| `4` | 已作废 |

**价签状态（priceTagStatus）**

| 值  | 含义   |
|-----|--------|
| `0` | 未售   |
| `1` | 已售   |
| `2` | 作废   |
| `3` | 过期   |

**自打价签条码类型（barcodeType）**

| 值  | 含义         |
|-----|--------------|
| `3` | 称重自打价签 |
| `4` | 成品自打价签 |

---

## 2. 数据模型

### 2.1 Promotion（促销）

| 字段               | 类型        | 说明                          |
| ------------------ | ----------- | ----------------------------- |
| `promotionId`      | `Long`      | 促销ID                        |
| `promotionCode`    | `String`    | 促销编码，全局唯一            |
| `promotionName`    | `String`    | 促销名称                      |
| `promotionType`    | `Integer`   | 促销类型                      |
| `startTime`        | `LocalDateTime` | 开始时间                  |
| `endTime`          | `LocalDateTime` | 结束时间                  |
| `priority`         | `Integer`   | 优先级，越大越先命中          |
| `stackable`        | `Integer`   | 是否可叠加 0否 1是            |
| `excludeGroup`     | `String`    | 互斥组                        |
| `stackWithMember`  | `Integer`   | 是否叠加会员折扣 0否 1是      |
| `stackWithCoupon`  | `Integer`   | 是否叠加券 0否 1是            |
| `scopeRelation`    | `Integer`   | 范围关系 1任一 2全部          |
| `status`           | `Integer`   | 1启用 2停用                   |

### 2.2 CouponTemplate（券模板）

| 字段                 | 类型            | 说明                          |
| -------------------- | --------------- | ----------------------------- |
| `templateId`         | `Long`          | 模板ID                        |
| `templateCode`       | `String`        | 模板编码，全局唯一            |
| `templateName`       | `String`        | 券名称                        |
| `couponType`         | `Integer`       | 券类型                        |
| `threshold`          | `BigDecimal`    | 使用门槛                      |
| `amount`             | `BigDecimal`    | 减免金额                      |
| `discountRate`       | `BigDecimal`    | 折扣率                        |
| `totalCount`         | `Integer`       | 发行总量                      |
| `issuedCount`        | `Integer`       | 已发放数量                    |
| `perLimit`           | `Integer`       | 每人限领                      |
| `validType`          | `Integer`       | 1固定日期 2领取后N天          |
| `validStart`         | `LocalDateTime` | 固定起始时间                  |
| `validEnd`           | `LocalDateTime` | 固定结束时间                  |
| `validDays`          | `Integer`       | 领取后N天                     |
| `scopeType`          | `Integer`       | 适用范围类型                  |
| `scopeValue`         | `String`        | 范围值                        |
| `stackWithPromotion` | `Integer`       | 是否叠加促销 0否 1是          |
| `status`             | `Integer`       | 1启用 2停用                   |

### 2.3 Coupon（用户券）

| 字段         | 类型            | 说明                          |
| ------------ | --------------- | ----------------------------- |
| `couponId`   | `Long`          | 券ID                          |
| `couponNo`   | `String`        | 券编号                        |
| `templateId` | `Long`          | 模板ID                        |
| `customerId` | `Long`          | 会员ID                        |
| `status`     | `Integer`       | 0未使用 1锁定中 2已使用 3已过期 4已作废 |
| `validStart` | `LocalDateTime` | 生效时间                      |
| `validEnd`   | `LocalDateTime` | 过期时间                      |
| `orderId`    | `Long`          | 使用订单ID                    |
| `orderNo`    | `String`        | 使用订单号                    |
| `usedTime`   | `LocalDateTime` | 使用时间                      |

### 2.4 DiscountRule（折扣规则）

| 字段             | 类型         | 说明                          |
| ---------------- | ------------ | ----------------------------- |
| `ruleId`         | `Long`       | 规则ID                        |
| `ruleName`       | `String`     | 规则名称                      |
| `ruleType`       | `Integer`    | 1时段折扣 2临期折扣           |
| `scopeType`      | `Integer`    | 范围类型                      |
| `scopeValue`     | `String`     | 范围值                        |
| `storeId`        | `Long`       | 门店ID，空为全部              |
| `timeStart`      | `LocalTime`  | 时段开始                      |
| `timeEnd`        | `LocalTime`  | 时段结束                      |
| `shelfLifeMin`   | `Integer`    | 剩余保质期最小天数            |
| `shelfLifeMax`   | `Integer`    | 剩余保质期最大天数            |
| `discountRate`   | `BigDecimal` | 折扣率，0.7=7折               |
| `reduceAmount`   | `BigDecimal` | 减钱                          |
| `fixedPrice`     | `BigDecimal` | 一口价                        |
| `validStart`     | `LocalDate`  | 规则生效日期                  |
| `validEnd`       | `LocalDate`  | 规则结束日期                  |
| `priority`       | `Integer`    | 优先级                        |
| `status`         | `Integer`    | 1启用 2停用                   |

### 2.5 PromotionPriceTag（自打价签）

| 字段              | 类型         | 说明                          |
| ----------------- | ------------ | ----------------------------- |
| `id`              | `Long`       | 主键                          |
| `barcode`         | `String`     | 价签条码（26开头）            |
| `barcodeType`     | `Integer`    | 3称重 4成品                   |
| `sourceBarcode`   | `String`     | 原条码                        |
| `productId`       | `Long`       | 商品ID                        |
| `productName`     | `String`     | 商品名快照                    |
| `originalPrice`   | `BigDecimal` | 原价                          |
| `promotionPrice`  | `BigDecimal` | 促销价                        |
| `weight`          | `BigDecimal` | 重量（称重商品）              |
| `amount`          | `BigDecimal` | 金额                          |
| `storeId`         | `Long`       | 门店ID                        |
| `invalidSource`   | `Integer`    | 原条码是否失效 0否 1是        |
| `status`          | `Integer`    | 0未售 1已售 2作废 3过期       |
| `gmtExpire`       | `LocalDateTime` | 过期时间                   |

---

## 3. 促销管理接口

### 3.1 分页查询促销

**GET** `/api/v1/discount/promotion/list`

**所需权限**：`discount:promotion:list`

#### 查询参数

| 参数       | 类型      | 必填 | 默认值 | 说明          |
| ---------- | --------- | ---- | ------ | ------------- |
| `pageNum`  | `Integer` | 否   | `1`    | 页码          |
| `pageSize` | `Integer` | 否   | `10`   | 每页数量      |
| `status`   | `Integer` | 否   | —      | 1启用 2停用   |
| `type`     | `Integer` | 否   | —      | 促销类型      |

#### 请求示例

```
GET /api/v1/discount/promotion/list?pageNum=1&pageSize=10&status=1
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
        "promotionId": 2106746910958862338,
        "promotionCode": "PROMO_COKE_2OFF",
        "promotionName": "可口可乐满2件8折",
        "promotionType": 2,
        "promotionTypeName": "连扫",
        "startTime": "2026-10-01T00:00:00",
        "endTime": "2026-12-31T23:59:59",
        "priority": 20,
        "stackable": 0,
        "excludeGroup": "PRICE_OFF",
        "status": 1,
        "gmtCreate": "2026-10-04T22:03:00"
      }
    ],
    "total": 2,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

#### cURL 示例

```bash
curl "http://localhost/api/v1/discount/promotion/list?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 3.2 促销详情

**GET** `/api/v1/discount/promotion/{promotionId}`

**所需权限**：`discount:promotion:list`

#### 路径参数

| 参数          | 类型   | 说明   |
| ------------- | ------ | ------ |
| `promotionId` | `Long` | 促销ID |

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "promotionId": 2106746910958862338,
    "promotionCode": "PROMO_COKE_2OFF",
    "promotionName": "可口可乐满2件8折",
    "promotionType": 2,
    "startTime": "2026-10-01T00:00:00",
    "endTime": "2026-12-31T23:59:59",
    "priority": 20,
    "stackable": 0,
    "excludeGroup": "PRICE_OFF",
    "stackWithMember": 1,
    "stackWithCoupon": 1,
    "status": 1,
    "scopes": [
      {
        "scopeType": 4,
        "scopeValue": "2106052145707646978",
        "storeId": null,
        "memberLevel": null
      }
    ],
    "conditions": [
      {
        "conditionType": 1,
        "operator": ">=",
        "threshold": 2.000
      }
    ],
    "actions": [
      {
        "actionType": 1,
        "actionValue": 0.800,
        "actionExt": null
      }
    ],
    "gmtCreate": "2026-10-04T22:03:00"
  }
}
```

#### cURL 示例

```bash
curl "http://localhost/api/v1/discount/promotion/2106746910958862338" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 3.3 新增促销

**POST** `/api/v1/discount/promotion`

**所需权限**：`discount:promotion:add`

#### 请求体

| 字段              | 类型                     | 必填 | 说明                    |
| ----------------- | ------------------------ | ---- | ----------------------- |
| `promotionCode`   | `String`                 | 是   | 促销编码                |
| `promotionName`   | `String`                 | 是   | 促销名称                |
| `promotionType`   | `Integer`                | 是   | 促销类型                |
| `startTime`       | `LocalDateTime`          | 是   | 开始时间                |
| `endTime`         | `LocalDateTime`          | 是   | 结束时间                |
| `priority`        | `Integer`                | 否   | 优先级，默认 0          |
| `stackable`       | `Integer`                | 否   | 是否可叠加，默认 0      |
| `excludeGroup`    | `String`                 | 否   | 互斥组                  |
| `stackWithMember` | `Integer`                | 否   | 叠加会员折扣，默认 1    |
| `stackWithCoupon` | `Integer`                | 否   | 叠加券，默认 1          |
| `scopeRelation`   | `Integer`                | 否   | 范围关系，默认 1        |
| `scopes`          | `Array<PromotionScope>`  | 否   | 范围列表                |
| `conditions`      | `Array<PromotionCondition>` | 否 | 条件列表                |
| `actions`         | `Array<PromotionAction>` | 否   | 动作列表                |
| `remark`          | `String`                 | 否   | 备注                    |

**Scope 结构：** `scopeType`、`scopeValue`、`storeId`、`memberLevel`

**Condition 结构：** `conditionType`、`operator`、`threshold`

**Action 结构：** `actionType`、`actionValue`、`actionExt`

#### 请求体示例

```json
{
  "promotionCode": "PROMO_COKE_PRICE",
  "promotionName": "可口可乐一口价",
  "promotionType": 1,
  "startTime": "2026-10-01T00:00:00",
  "endTime": "2026-12-31T23:59:59",
  "priority": 10,
  "stackable": 0,
  "excludeGroup": "PRICE_OFF",
  "stackWithMember": 1,
  "stackWithCoupon": 1,
  "scopeRelation": 1,
  "scopes": [
    {
      "scopeType": 4,
      "scopeValue": "2106052145707646978"
    }
  ],
  "conditions": [
    {
      "conditionType": 1,
      "operator": ">=",
      "threshold": 1
    }
  ],
  "actions": [
    {
      "actionType": 3,
      "actionValue": 2.50
    }
  ],
  "remark": "一口价2.50"
}
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": 2106746731140661250
}
```

`data` 为新增促销的 `promotionId`。

#### 业务异常

| 场景           | code | message               |
| -------------- | ---- | --------------------- |
| 促销编码已存在 | 400  | `"促销编码已存在"`    |

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/discount/promotion" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "promotionCode": "PROMO_COKE_PRICE",
    "promotionName": "可口可乐一口价",
    "promotionType": 1,
    "startTime": "2026-10-01T00:00:00",
    "endTime": "2026-12-31T23:59:59",
    "priority": 10,
    "scopes": [{"scopeType": 4, "scopeValue": "2106052145707646978"}],
    "conditions": [{"conditionType": 1, "operator": ">=", "threshold": 1}],
    "actions": [{"actionType": 3, "actionValue": 2.50}]
  }'
```

---

### 3.4 修改促销状态

**PUT** `/api/v1/discount/promotion/{promotionId}/status`

**所需权限**：`discount:promotion:edit`

#### 路径参数

| 参数          | 类型   | 说明   |
| ------------- | ------ | ------ |
| `promotionId` | `Long` | 促销ID |

#### 查询参数

| 参数     | 类型      | 必填 | 说明          |
| -------- | --------- | ---- | ------------- |
| `status` | `Integer` | 是   | 1启用 2停用   |

#### 请求示例

```
PUT /api/v1/discount/promotion/2106746910958862338/status?status=2
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": null
}
```

#### cURL 示例

```bash
curl -X PUT "http://localhost/api/v1/discount/promotion/2106746910958862338/status?status=2" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 3.5 删除促销

**DELETE** `/api/v1/discount/promotion/{promotionId}`

**所需权限**：`discount:promotion:delete`

> 逻辑删除，同时清空促销的范围、条件、动作关联。

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": null
}
```

#### cURL 示例

```bash
curl -X DELETE "http://localhost/api/v1/discount/promotion/2106746910958862338" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 4. 券管理接口

### 4.1 分页查询券模板

**GET** `/api/v1/discount/coupon/template/list`

**所需权限**：`discount:coupon:list`

#### 查询参数

| 参数       | 类型      | 必填 | 默认值 | 说明     |
| ---------- | --------- | ---- | ------ | -------- |
| `pageNum`  | `Integer` | 否   | `1`    | 页码     |
| `pageSize` | `Integer` | 否   | `10`   | 每页数量 |

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "records": [
      {
        "templateId": 2106747678684266497,
        "templateCode": "CP_FULL10_3",
        "templateName": "满10减3券",
        "couponType": 1,
        "threshold": 10.00,
        "amount": 3.00,
        "discountRate": null,
        "totalCount": 1000,
        "issuedCount": 3,
        "perLimit": 1,
        "validType": 2,
        "validStart": null,
        "validEnd": null,
        "validDays": 30,
        "scopeType": 1,
        "scopeValue": null,
        "status": 1
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
curl "http://localhost/api/v1/discount/coupon/template/list?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 4.2 券模板详情

**GET** `/api/v1/discount/coupon/template/{templateId}`

**所需权限**：`discount:coupon:list`

#### cURL 示例

```bash
curl "http://localhost/api/v1/discount/coupon/template/2106747678684266497" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 4.3 新增券模板

**POST** `/api/v1/discount/coupon/template`

**所需权限**：`discount:coupon:add`

#### 请求体

| 字段                 | 类型            | 必填 | 说明                          |
| -------------------- | --------------- | ---- | ----------------------------- |
| `templateCode`       | `String`        | 是   | 模板编码，全局唯一            |
| `templateName`       | `String`        | 是   | 券名称                        |
| `couponType`         | `Integer`       | 是   | 1满减 2折扣 3单品             |
| `threshold`          | `BigDecimal`    | 否   | 使用门槛，默认 0              |
| `amount`             | `BigDecimal`    | 否   | 满减金额                      |
| `discountRate`       | `BigDecimal`    | 否   | 折扣率                        |
| `totalCount`         | `Integer`       | 否   | 发行总量                      |
| `perLimit`           | `Integer`       | 否   | 每人限领，默认 1              |
| `validType`          | `Integer`       | 否   | 1固定日期 2领取后N天，默认 1  |
| `validStart`         | `LocalDateTime` | 否   | 固定起始                      |
| `validEnd`           | `LocalDateTime` | 否   | 固定结束                      |
| `validDays`          | `Integer`       | 否   | 领取后N天                     |
| `scopeType`          | `Integer`       | 否   | 范围类型                      |
| `scopeValue`         | `String`        | 否   | 范围值                        |
| `stackWithPromotion` | `Integer`       | 否   | 是否叠加促销，默认 1          |

#### 请求体示例

```json
{
  "templateCode": "CP_FULL10_3",
  "templateName": "满10减3券",
  "couponType": 1,
  "threshold": 10.00,
  "amount": 3.00,
  "totalCount": 1000,
  "perLimit": 1,
  "validType": 2,
  "validDays": 30,
  "scopeType": 1,
  "stackWithPromotion": 1
}
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": 2106747678684266497
}
```

#### 业务异常

| 场景           | code | message             |
| -------------- | ---- | ------------------- |
| 模板编码已存在 | 400  | `"模板编码已存在"`  |

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/discount/coupon/template" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "templateCode": "CP_FULL10_3",
    "templateName": "满10减3券",
    "couponType": 1,
    "threshold": 10.00,
    "amount": 3.00,
    "totalCount": 1000,
    "perLimit": 1,
    "validType": 2,
    "validDays": 30,
    "scopeType": 1
  }'
```

---

### 4.4 修改券模板状态

**PUT** `/api/v1/discount/coupon/template/{templateId}/status`

**所需权限**：`discount:coupon:edit`

**说明**：停用模板（`status=2`）时，会**连带作废该模板下所有未使用的券**。

#### 查询参数

| 参数     | 类型      | 必填 | 说明          |
| -------- | --------- | ---- | ------------- |
| `status` | `Integer` | 是   | 1启用 2停用   |

#### 请求示例

```
PUT /api/v1/discount/coupon/template/2106747678684266497/status?status=2
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": null
}
```

#### cURL 示例

```bash
curl -X PUT "http://localhost/api/v1/discount/coupon/template/2106747678684266497/status?status=2" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 4.5 删除券模板

**DELETE** `/api/v1/discount/coupon/template/{templateId}`

**所需权限**：`discount:coupon:delete`

> 逻辑删除，已发的券不受影响。

#### cURL 示例

```bash
curl -X DELETE "http://localhost/api/v1/discount/coupon/template/2106747678684266497" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 4.6 批量发券

**POST** `/api/v1/discount/coupon/issue`

**所需权限**：`discount:coupon:issue`

#### 请求体

| 字段          | 类型        | 必填 | 说明       |
| ------------- | ----------- | ---- | ---------- |
| `templateId`  | `Long`      | 是   | 模板ID     |
| `customerIds` | `Array<Long>` | 是 | 会员ID列表 |

#### 请求体示例

```json
{
  "templateId": 2106747678684266497,
  "customerIds": [1, 2, 3]
}
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": 3
}
```

`data` 为成功发放的数量。

#### 业务异常

| 场景             | code | message                       |
| ---------------- | ---- | ----------------------------- |
| 模板不存在/停用  | 400  | `"模板不存在或已停用"`        |
| 发行数量不足     | 400  | `"发行数量不足，剩余：xx"`    |

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/discount/coupon/issue" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"templateId": 2106747678684266497, "customerIds": [1, 2, 3]}'
```

---

### 4.7 会员领取券

**POST** `/api/v1/discount/coupon/receive/{templateId}`

**无需权限（会员端调用）**

#### 路径参数

| 参数         | 类型   | 说明   |
| ------------ | ------ | ------ |
| `templateId` | `Long` | 模板ID |

#### 查询参数

| 参数         | 类型   | 必填 | 说明   |
| ------------ | ------ | ---- | ------ |
| `customerId` | `Long` | 是   | 会员ID |

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "couponId": 2106747678999876097,
    "couponNo": "CP20261004ABCD123456",
    "templateId": 2106747678684266497,
    "templateName": "满10减3券",
    "couponType": 1,
    "threshold": 10.00,
    "amount": 3.00,
    "status": 0,
    "statusName": "未使用",
    "validStart": "2026-10-04T22:08:00",
    "validEnd": "2026-11-03T22:08:00"
  }
}
```

#### 业务异常

| 场景           | code | message               |
| -------------- | ---- | --------------------- |
| 已被领完       | 400  | `"已被领完"`          |
| 已达领取上限   | 400  | `"已达到领取上限"`    |

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/discount/coupon/receive/2106747678684266497?customerId=1"
```

---

### 4.8 查询会员可用券

**GET** `/api/v1/discount/coupon/available`

**无需权限（会员端调用）**

#### 查询参数

| 参数         | 类型   | 必填 | 说明   |
| ------------ | ------ | ---- | ------ |
| `customerId` | `Long` | 是   | 会员ID |

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": [
    {
      "couponId": 2106747678999876097,
      "couponNo": "CP20261004ABCD123456",
      "templateId": 2106747678684266497,
      "templateName": "满10减3券",
      "couponType": 1,
      "threshold": 10.00,
      "amount": 3.00,
      "status": 0,
      "statusName": "未使用",
      "validStart": "2026-10-04T22:08:00",
      "validEnd": "2026-11-03T22:08:00"
    }
  ]
}
```

#### cURL 示例

```bash
curl "http://localhost/api/v1/discount/coupon/available?customerId=1" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 4.9 分页查询用户券

**GET** `/api/v1/discount/coupon/list`

**所需权限**：`discount:coupon:list`

#### 查询参数

| 参数         | 类型      | 必填 | 说明          |
| ------------ | --------- | ---- | ------------- |
| `pageNum`    | `Integer` | 否   | 页码          |
| `pageSize`   | `Integer` | 否   | 每页数量      |
| `customerId` | `Long`    | 否   | 会员ID        |
| `status`     | `Integer` | 否   | 券状态        |

#### cURL 示例

```bash
curl "http://localhost/api/v1/discount/coupon/list?pageNum=1&pageSize=10&status=0" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 4.10 回收券

**POST** `/api/v1/discount/coupon/revoke`

**所需权限**：`discount:coupon:revoke`

**说明**：作废未使用的券，支持按券ID、模板ID、会员ID三种方式。

#### 请求体

| 字段          | 类型        | 必填 | 说明                          |
| ------------- | ----------- | ---- | ----------------------------- |
| `couponIds`   | `Array<Long>` | 否 | 券ID列表                      |
| `templateId`  | `Long`      | 否   | 按模板回收                    |
| `customerId`  | `Long`      | 否   | 按会员回收                    |
| `reason`      | `String`    | 是   | 回收原因                      |

**三个筛选字段至少传一个。**

#### 请求体示例（按券ID）

```json
{
  "couponIds": [2106747678999876097],
  "reason": "活动取消"
}
```

#### 请求体示例（按模板）

```json
{
  "templateId": 2106747678684266497,
  "reason": "模板下线"
}
```

#### 请求体示例（按会员）

```json
{
  "customerId": 1,
  "reason": "会员违规"
}
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": 3
}
```

`data` 为成功作废的数量。

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/discount/coupon/revoke" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"couponIds": [2106747678999876097], "reason": "活动取消"}'
```

---

## 5. 折扣规则接口

### 5.1 分页查询规则

**GET** `/api/v1/discount/rule/list`

**所需权限**：`discount:rule:list`

#### 查询参数

| 参数       | 类型      | 必填 | 说明          |
| ---------- | --------- | ---- | ------------- |
| `pageNum`  | `Integer` | 否   | 页码          |
| `pageSize` | `Integer` | 否   | 每页数量      |
| `ruleType` | `Integer` | 否   | 1时段 2临期   |
| `status`   | `Integer` | 否   | 1启用 2停用   |

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "records": [
      {
        "ruleId": 2106747000000000001,
        "ruleName": "临期折扣7折",
        "ruleType": 2,
        "scopeType": 1,
        "scopeValue": null,
        "storeId": 1,
        "timeStart": null,
        "timeEnd": null,
        "shelfLifeMin": 16,
        "shelfLifeMax": 30,
        "discountRate": 0.7000,
        "reduceAmount": null,
        "fixedPrice": null,
        "validStart": "2026-10-01",
        "validEnd": "2026-12-31",
        "status": 1,
        "priority": 100,
        "gmtCreate": "2026-10-04T22:02:00"
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
curl "http://localhost/api/v1/discount/rule/list?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 5.2 规则详情

**GET** `/api/v1/discount/rule/{ruleId}`

**所需权限**：`discount:rule:list`

#### cURL 示例

```bash
curl "http://localhost/api/v1/discount/rule/2106747000000000001" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 5.3 新增规则

**POST** `/api/v1/discount/rule`

**所需权限**：`discount:rule:add`

#### 请求体（时段折扣）

```json
{
  "ruleName": "晚间生鲜8折",
  "ruleType": 1,
  "scopeType": 2,
  "scopeValue": "3",
  "storeId": 1,
  "timeStart": "20:00:00",
  "timeEnd": "23:59:59",
  "discountRate": 0.8,
  "validStart": "2026-10-01",
  "validEnd": "2026-12-31",
  "priority": 50
}
```

#### 请求体（临期折扣）

```json
{
  "ruleName": "临期折扣7折",
  "ruleType": 2,
  "scopeType": 1,
  "storeId": 1,
  "shelfLifeMin": 16,
  "shelfLifeMax": 30,
  "discountRate": 0.7,
  "validStart": "2026-10-01",
  "validEnd": "2026-12-31",
  "priority": 100
}
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": 2106747000000000001
}
```

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/discount/rule" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "ruleName": "临期折扣7折",
    "ruleType": 2,
    "scopeType": 1,
    "storeId": 1,
    "shelfLifeMin": 16,
    "shelfLifeMax": 30,
    "discountRate": 0.7,
    "validStart": "2026-10-01",
    "validEnd": "2026-12-31",
    "priority": 100
  }'
```

---

### 5.4 修改规则状态

**PUT** `/api/v1/discount/rule/{ruleId}/status`

**所需权限**：`discount:rule:edit`

#### cURL 示例

```bash
curl -X PUT "http://localhost/api/v1/discount/rule/2106747000000000001/status?status=2" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 5.5 删除规则

**DELETE** `/api/v1/discount/rule/{ruleId}`

**所需权限**：`discount:rule:delete`

#### cURL 示例

```bash
curl -X DELETE "http://localhost/api/v1/discount/rule/2106747000000000001" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 6. 自打价签接口

### 6.1 生成自打价签

**POST** `/api/v1/discount/price-tag/generate`

**无需权限（PDA 调用）**

**说明**：员工 PDA 扫原条码后调用，系统按规则自动算促销价并生成新价签，同时把同商品的旧有效价签置为作废。

#### 查询参数

| 参数             | 类型         | 必填 | 说明                          |
| ---------------- | ------------ | ---- | ----------------------------- |
| `productId`      | `Long`       | 是   | 商品ID                        |
| `productName`    | `String`     | 是   | 商品名称                      |
| `categoryId`     | `Long`       | 是   | 分类ID                        |
| `brandId`        | `Long`       | 否   | 品牌ID                        |
| `originalPrice`  | `BigDecimal` | 是   | 原价                          |
| `shelfLifeDays`  | `Integer`    | 否   | 保质期天数                    |

#### 请求体

| 字段                      | 类型         | 必填 | 说明                          |
| ------------------------- | ------------ | ---- | ----------------------------- |
| `sourceBarcode`           | `String`     | 是   | 原条码                        |
| `storeId`                 | `Long`       | 是   | 门店ID                        |
| `operatorId`              | `Long`       | 否   | 操作员ID                      |
| `weight`                  | `BigDecimal` | 否   | 重量（称重商品必传）          |
| `remainingShelfLifeDays`  | `Integer`    | 否   | 剩余保质期天数（临期必传）    |

#### 请求示例

```
POST /api/v1/discount/price-tag/generate?productId=2106052145707646978&productName=可口可乐500ml&categoryId=100&originalPrice=3.50&shelfLifeDays=25
```

```json
{
  "sourceBarcode": "6901234567890",
  "storeId": 1,
  "operatorId": 1,
  "remainingShelfLifeDays": 25
}
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "id": 2106985301998723078,
    "barcode": "2600000000001X",
    "barcodeType": 4,
    "sourceBarcode": "6901234567890",
    "productId": 2106052145707646978,
    "productName": "可口可乐500ml",
    "originalPrice": 3.50,
    "promotionPrice": 2.45,
    "weight": null,
    "amount": 2.45,
    "status": 0,
    "statusName": "未售"
  }
}
```

#### 业务异常

| 场景                   | code | message                     |
| ---------------------- | ---- | --------------------------- |
| 没有匹配的折扣规则     | 400  | `"当前没有匹配的折扣规则"`  |
| 折扣价不低于原价       | 400  | `"折扣价必须低于原价"`      |

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/discount/price-tag/generate?productId=2106052145707646978&productName=可口可乐500ml&categoryId=100&originalPrice=3.50&shelfLifeDays=25" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sourceBarcode": "6901234567890",
    "storeId": 1,
    "operatorId": 1,
    "remainingShelfLifeDays": 25
  }'
```

---

### 6.2 扫码查价签

**GET** `/api/v1/discount/price-tag/scan/{barcode}`

**无需权限（收银端调用）**

**说明**：

- 扫到自打价签条码 → 返回价签信息
- 扫到原条码且被标记失效（称重商品）→ 报错"该商品禁止销售"
- 扫到普通条码 → 返回 `null`，走正常商品流程

#### 路径参数

| 参数      | 类型     | 说明       |
| --------- | -------- | ---------- |
| `barcode` | `String` | 扫码得到的条码 |

#### 成功响应（命中自打价签）

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "id": 2106985301998723078,
    "barcode": "2600000000001X",
    "barcodeType": 4,
    "sourceBarcode": "6901234567890",
    "productId": 2106052145707646978,
    "productName": "可口可乐500ml",
    "originalPrice": 3.50,
    "promotionPrice": 2.45,
    "weight": null,
    "amount": 2.45,
    "status": 0,
    "statusName": "未售"
  }
}
```

#### 成功响应（普通条码）

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": null
}
```

#### 业务异常

| 场景                     | code | message                     |
| ------------------------ | ---- | --------------------------- |
| 价签已售出               | 400  | `"该价签已售出"`            |
| 价签已作废               | 400  | `"该价签已作废"`            |
| 价签已过期               | 400  | `"该价签已过期"`            |
| 原条码失效（称重商品）   | 400  | `"该商品禁止销售"`          |

#### cURL 示例

```bash
curl "http://localhost/api/v1/discount/price-tag/scan/2600000000001X" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 7. 计算优惠（内部接口）

### 7.1 计算购物车优惠

**POST** `/api/v1/discount/internal/calculate`

**调用方**：`service-order`（下单时调用）

**不加 `@PreAuthorize`，网关不路由 `/internal/**`。**

#### 请求体

| 字段         | 类型               | 必填 | 说明                          |
| ------------ | ------------------ | ---- | ----------------------------- |
| `storeId`    | `Long`             | 是   | 门店ID                        |
| `customerId` | `Long`             | 否   | 会员ID                        |
| `memberLevel`| `Integer`          | 否   | 会员等级                      |
| `items`      | `Array<CartItem>`  | 是   | 购物车列表                    |
| `couponIds`  | `Array<Long>`      | 否   | 使用的券ID列表（暂支持一张）  |

**CartItem 结构：**

| 字段          | 类型         | 必填 | 说明                          |
| ------------- | ------------ | ---- | ----------------------------- |
| `productId`   | `Long`       | 是   | 商品ID                        |
| `barcode`     | `String`     | 否   | 条码                          |
| `barcodeType` | `Integer`    | 否   | 条码类型：1国标 3秤码 4自打价签 |
| `categoryId`  | `Long`       | 否   | 分类ID                        |
| `brandId`     | `Long`       | 否   | 品牌ID                        |
| `quantity`    | `BigDecimal` | 是   | 数量                          |
| `price`       | `BigDecimal` | 是   | 原价                          |

#### 请求体示例

```json
{
  "storeId": 1,
  "customerId": 1,
  "memberLevel": null,
  "couponIds": [2106747678999876097],
  "items": [
    {
      "productId": 2106052145707646978,
      "barcode": "6901234567890",
      "barcodeType": 1,
      "categoryId": 100,
      "price": 3.50,
      "quantity": 4
    }
  ]
}
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "originalAmount": 14.00,
    "discountAmount": 5.80,
    "payAmount": 8.20,
    "items": [
      {
        "productId": 2106052145707646978,
        "barcode": "6901234567890",
        "barcodeType": 1,
        "productName": null,
        "originalPrice": 3.50,
        "price": 3.50,
        "quantity": 4,
        "totalAmount": 14.00,
        "discountAmount": 5.80,
        "payAmount": 8.20,
        "promotionId": 2106746910958862338,
        "promotionName": "可口可乐满2件8折"
      }
    ],
    "appliedPromotions": [
      {
        "promotionId": 2106746910958862338,
        "promotionName": "可口可乐满2件8折",
        "promotionType": 2,
        "discountAmount": 2.80
      },
      {
        "promotionId": 2106747678999876097,
        "promotionName": "满10减3券",
        "promotionType": 4,
        "discountAmount": 3.00
      }
    ]
  }
}
```

#### 业务异常

| 场景                            | code | message                                  |
| ------------------------------- | ---- | ---------------------------------------- |
| 券不存在                        | 400  | `"券不存在"`                             |
| 券不属于当前会员                | 400  | `"券不属于当前会员"`                     |
| 券不在有效期内                  | 400  | `"券不在有效期内"`                       |
| 未达到券使用门槛                | 400  | `"未达到券使用门槛：10.00"`              |
| 促销不支持叠加券                | 400  | `"促销【xxx】不支持叠加优惠券"`          |
| 券不可与促销叠加                | 400  | `"该券不可与促销活动叠加使用"`           |

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/discount/internal/calculate" \
  -H "Content-Type: application/json" \
  -d '{
    "storeId": 1,
    "customerId": 1,
    "couponIds": [2106747678999876097],
    "items": [
      {"productId": 2106052145707646978, "barcodeType": 1, "price": 3.50, "quantity": 4}
    ]
  }'
```

---

### 7.2 锁定券

**POST** `/api/v1/discount/coupon/internal/lock`

**调用方**：`service-order`（下单时调用）

#### 查询参数

| 参数       | 类型     | 必填 | 说明     |
| ---------- | -------- | ---- | -------- |
| `couponId` | `Long`   | 是   | 券ID     |
| `orderId`  | `Long`   | 是   | 订单ID   |
| `orderNo`  | `String` | 是   | 订单号   |

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": null
}
```

#### 业务异常

| 场景         | code | message             |
| ------------ | ---- | ------------------- |
| 券不存在     | 400  | `"券不存在"`        |
| 券当前不可用 | 400  | `"券当前不可用"`    |
| 券不在有效期 | 400  | `"券不在有效期内"`  |

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/discount/coupon/internal/lock?couponId=2106747678999876097&orderId=2106985371171164162&orderNo=SO2026100500010000034"
```

---

### 7.3 核销券

**POST** `/api/v1/discount/coupon/internal/use`

**调用方**：`service-order`（支付成功时调用）

#### 查询参数

| 参数       | 类型   | 必填 | 说明   |
| ---------- | ------ | ---- | ------ |
| `couponId` | `Long` | 是   | 券ID   |

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": null
}
```

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/discount/coupon/internal/use?couponId=2106747678999876097"
```

---

### 7.4 释放券

**POST** `/api/v1/discount/coupon/internal/release`

**调用方**：`service-order`（取消订单时调用）

#### 查询参数

| 参数       | 类型   | 必填 | 说明   |
| ---------- | ------ | ---- | ------ |
| `couponId` | `Long` | 是   | 券ID   |

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": null
}
```

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/discount/coupon/internal/release?couponId=2106747678999876097"
```

---

## 8. 定时任务

| 任务 | cron | 说明 |
| ---- | ---- | ---- |
| 券过期作废 | `0 0 * * * ?` | 每小时，把过期的未使用券改成已过期 |
| 促销过期停用 | `0 0 1 * * ?` | 每天凌晨 1 点，停用已过期的促销 |
| 价签过期作废 | `0 0 2 * * ?` | 每天凌晨 2 点，作废已过期的价签 |

---

## 9. Feign 调用关系

```
service-order
    │
    │ 下单时调用
    ↓
service-discount  /api/v1/discount/internal/calculate
                  /api/v1/discount/coupon/internal/lock
                  /api/v1/discount/coupon/internal/use
                  /api/v1/discount/coupon/internal/release
```

---

## 10. 权限编码

| 编码                          | 名称       |
| ----------------------------- | ---------- |
| `discount:promotion:list`     | 促销列表   |
| `discount:promotion:add`      | 新增促销   |
| `discount:promotion:edit`     | 修改促销   |
| `discount:promotion:delete`   | 删除促销   |
| `discount:coupon:list`        | 券列表     |
| `discount:coupon:add`         | 新增券     |
| `discount:coupon:edit`        | 修改券     |
| `discount:coupon:delete`      | 删除券     |
| `discount:coupon:issue`       | 发券       |
| `discount:coupon:revoke`      | 回收券     |
| `discount:rule:list`          | 规则列表   |
| `discount:rule:add`           | 新增规则   |
| `discount:rule:edit`          | 修改规则   |
| `discount:rule:delete`        | 删除规则   |

---

## 11. cURL 完整测试流程

```bash
# 0. 登录拿 token
curl -X POST http://localhost/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123","userType":2}'

TOKEN=eyJhbGciOiJIUzI1NiJ9...

# 1. 建折扣规则（临期7折）
curl -X POST http://localhost/api/v1/discount/rule \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"ruleName":"临期折扣7折","ruleType":2,"scopeType":1,"storeId":1,"shelfLifeMin":16,"shelfLifeMax":30,"discountRate":0.7,"validStart":"2026-10-01","validEnd":"2026-12-31","priority":100}'

# 2. 建促销（一口价2.50）
curl -X POST http://localhost/api/v1/discount/promotion \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"promotionCode":"PROMO_COKE_PRICE","promotionName":"可口可乐一口价","promotionType":1,"startTime":"2026-10-01T00:00:00","endTime":"2026-12-31T23:59:59","priority":10,"scopes":[{"scopeType":4,"scopeValue":"2106052145707646978"}],"conditions":[{"conditionType":1,"operator":">=","threshold":1}],"actions":[{"actionType":3,"actionValue":2.50}]}'

# 3. 建券模板（满10减3）
curl -X POST http://localhost/api/v1/discount/coupon/template \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"templateCode":"CP_FULL10_3","templateName":"满10减3券","couponType":1,"threshold":10.00,"amount":3.00,"totalCount":1000,"perLimit":1,"validType":2,"validDays":30,"scopeType":1}'

# 4. 发券给会员1
curl -X POST http://localhost/api/v1/discount/coupon/issue \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"templateId":TEMPLATE_ID,"customerIds":[1]}'

# 5. 查会员1可用券，拿 couponId
curl "http://localhost/api/v1/discount/coupon/available?customerId=1" \
  -H "Authorization: Bearer $TOKEN"

# 6. 计算优惠（4件可乐+券）
curl -X POST http://localhost/api/v1/discount/internal/calculate \
  -H "Content-Type: application/json" \
  -d '{"storeId":1,"customerId":1,"couponIds":[COUPON_ID],"items":[{"productId":2106052145707646978,"barcodeType":1,"price":3.50,"quantity":4}]}'

# 7. 生成自打价签（临期25天，7折）
curl -X POST "http://localhost/api/v1/discount/price-tag/generate?productId=2106052145707646978&productName=可口可乐500ml&categoryId=100&originalPrice=3.50&shelfLifeDays=25" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"sourceBarcode":"6901234567890","storeId":1,"operatorId":1,"remainingShelfLifeDays":25}'

# 8. 扫码查价签
curl "http://localhost/api/v1/discount/price-tag/scan/BARCODE" \
  -H "Authorization: Bearer $TOKEN"

# 9. 回收券
curl -X POST http://localhost/api/v1/discount/coupon/revoke \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"couponIds":[COUPON_ID],"reason":"活动取消"}'
```