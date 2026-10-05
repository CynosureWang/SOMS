# SOMS 支付服务接口文档

> **服务名称**：`service-pay`
> **服务端口**：`8040`
> **基础路径**：`http://127.0.0.1:8140`
> **网关路径**：`http://localhost/api/v1/pay`
> **版本**：`1.1.0`
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

除回调接口外，所有接口必须携带 token：

```
Authorization: Bearer {accessToken}
```

### 1.4 数据字典

**支付方式（payType）**

| 值  | 含义     |
|-----|----------|
| `1` | 现金     |
| `2` | 微信     |
| `3` | 支付宝   |
| `4` | 银行卡   |
| `5` | 会员余额 |

**支付场景（payScene）**

| 值  | 含义     | 说明                   |
|-----|----------|------------------------|
| `1` | 付款码   | POS 扫顾客码，同步返回 |
| `2` | Native   | PC 生成二维码，异步回调 |
| `3` | JSAPI    | 小程序/公众号，异步回调 |
| `4` | APP      | APP 支付，异步回调     |

**支付单状态（status）**

| 值  | 含义     |
|-----|----------|
| `0` | 待支付   |
| `1` | 支付中   |
| `2` | 成功     |
| `3` | 失败     |
| `4` | 已关闭   |
| `5` | 已退款   |

**支付流水类型（flowType）**

| 值  | 含义     |
|-----|----------|
| `1` | 创建     |
| `2` | 发起支付 |
| `3` | 支付成功 |
| `4` | 支付失败 |
| `5` | 关闭     |
| `6` | 退款     |
| `7` | 回调     |

**支付渠道（channel）**

| 值  | 含义 |
|-----|------|
| `1` | 微信 |
| `2` | 支付宝 |

**退款状态（refundStatus）**

| 值  | 含义     |
|-----|----------|
| `0` | 待退款   |
| `1` | 退款中   |
| `2` | 成功     |
| `3` | 失败     |

---

## 2. 数据模型

### 2.1 PayOrder（支付单）

| 字段               | 类型            | 说明                                      |
| ------------------ | --------------- | ----------------------------------------- |
| `payId`            | `Long`          | 支付单ID（雪花）                          |
| `payNo`            | `String`        | 支付单号，`MFSOMSP + yyyyMMdd + 7位序列`  |
| `orderId`          | `Long`          | 关联订单ID                                |
| `orderNo`          | `String`        | 关联订单号                                |
| `storeId`          | `Long`          | 门店ID                                    |
| `customerId`       | `Long`          | 会员ID                                    |
| `payType`          | `Integer`       | 支付方式                                  |
| `payScene`         | `Integer`       | 支付场景                                  |
| `payAmount`        | `BigDecimal`    | 应付金额                                  |
| `paidAmount`       | `BigDecimal`    | 实付金额                                  |
| `status`           | `Integer`       | 状态                                      |
| `channelOrderNo`   | `String`        | 第三方订单号                              |
| `channelUserId`    | `String`        | 第三方用户ID（openid）                    |
| `channelTradeNo`   | `String`        | 第三方交易号                              |
| `payTime`          | `LocalDateTime` | 支付成功时间                              |
| `expireTime`       | `LocalDateTime` | 过期时间                                  |
| `closeTime`        | `LocalDateTime` | 关闭时间                                  |
| `failReason`       | `String`        | 失败原因                                  |
| `attach`           | `String`        | 附加数据                                  |
| `remark`           | `String`        | 备注                                      |

### 2.2 PayRefund（退款单）

| 字段               | 类型            | 说明                                      |
| ------------------ | --------------- | ----------------------------------------- |
| `refundId`         | `Long`          | 退款ID（雪花）                            |
| `refundNo`         | `String`        | 退款单号，`MFSOMSR + yyyyMMdd + 7位序列`  |
| `payId`            | `Long`          | 原支付单ID                                |
| `payNo`            | `String`        | 原支付单号                                |
| `orderId`          | `Long`          | 订单ID                                    |
| `orderNo`          | `String`        | 订单号                                    |
| `refundAmount`     | `BigDecimal`    | 退款金额                                  |
| `reason`           | `String`        | 退款原因                                  |
| `status`           | `Integer`       | 退款状态                                  |
| `channelRefundNo`  | `String`        | 第三方退款单号                            |
| `refundTime`       | `LocalDateTime` | 退款成功时间                              |
| `failReason`       | `String`        | 失败原因                                  |

### 2.3 PayFlow（支付流水）

| 字段            | 类型      | 说明                          |
| --------------- | --------- | ----------------------------- |
| `flowId`        | `Long`    | 流水ID                        |
| `payId`         | `Long`    | 支付单ID                      |
| `payNo`         | `String`  | 支付单号                      |
| `flowType`      | `Integer` | 流水类型                      |
| `channel`       | `Integer` | 支付渠道                      |
| `requestData`   | `String`  | 请求报文                      |
| `responseData`  | `String`  | 响应报文                      |
| `remark`        | `String`  | 备注                          |

---

## 3. 支付发起接口

### 3.1 创建支付单（现金 / 会员余额）

**POST** `/api/v1/pay/create`

**所需权限**：`pay:create`

**说明**：用于现金支付和会员余额支付，**同步返回支付结果**。

#### 请求头

```
Authorization: Bearer {accessToken}
Content-Type: application/json
```

#### 请求体

| 字段         | 类型         | 必填 | 说明                          |
| ------------ | ------------ | ---- | ----------------------------- |
| `orderId`    | `Long`       | 是   | 订单ID                        |
| `orderNo`    | `String`     | 是   | 订单号                        |
| `storeId`    | `Long`       | 是   | 门店ID                        |
| `customerId` | `Long`       | 否   | 会员ID                        |
| `payType`    | `Integer`    | 是   | 1现金 5会员余额               |
| `payScene`   | `Integer`    | 是   | 场景：现金通常为 1            |
| `payAmount`  | `BigDecimal` | 是   | 应付金额                      |
| `attach`     | `String`     | 否   | 附加数据                      |

#### 请求体示例

```json
{
  "orderId": 2106985371171164162,
  "orderNo": "SO2026100500010000034",
  "storeId": 1,
  "customerId": null,
  "payType": 1,
  "payScene": 1,
  "payAmount": 8.20,
  "attach": "现金收款"
}
```

#### 成功响应

**HTTP 200**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "payId": 2106985371780235265,
    "payNo": "MFSOMSP202610050000001",
    "status": 2,
    "statusName": "成功",
    "channelTradeNo": null,
    "codeUrl": null,
    "jsapiParams": null,
    "errMsg": null
  }
}
```

| 字段             | 类型     | 说明                          |
| ---------------- | -------- | ----------------------------- |
| `payId`          | `Long`   | 支付单ID                      |
| `payNo`          | `String` | 支付单号                      |
| `status`         | `Integer`| 支付状态                      |
| `statusName`     | `String` | 状态名称                      |
| `channelTradeNo` | `String` | 第三方交易号                  |
| `codeUrl`        | `String` | Native 二维码链接             |
| `jsapiParams`    | `Map`    | JSAPI 支付参数                |
| `errMsg`         | `String` | 付款码支付错误信息            |

#### 业务异常

| 场景                    | code | message                     |
| ----------------------- | ---- | --------------------------- |
| 该支付方式请用对应接口  | 400  | `"该支付方式请使用对应接口"` |

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/pay/create" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 2106985371171164162,
    "orderNo": "SO2026100500010000034",
    "storeId": 1,
    "payType": 1,
    "payScene": 1,
    "payAmount": 8.20
  }'
```

---

### 3.2 付款码支付（微信 / 支付宝）

**POST** `/api/v1/pay/barcode`

**所需权限**：`pay:create`

**说明**：POS 扫顾客付款码，**同步返回支付结果**。

#### 请求体

| 字段         | 类型         | 必填 | 说明                          |
| ------------ | ------------ | ---- | ----------------------------- |
| `orderId`    | `Long`       | 是   | 订单ID                        |
| `orderNo`    | `String`     | 是   | 订单号                        |
| `storeId`    | `Long`       | 是   | 门店ID                        |
| `customerId` | `Long`       | 否   | 会员ID                        |
| `payType`    | `Integer`    | 是   | 2微信 3支付宝                 |
| `authCode`   | `String`     | 是   | 顾客付款码                    |
| `payAmount`  | `BigDecimal` | 是   | 应付金额                      |

#### 请求体示例

```json
{
  "orderId": 2106985371171164162,
  "orderNo": "SO2026100500010000034",
  "storeId": 1,
  "customerId": null,
  "payType": 2,
  "authCode": "134567890123456789",
  "payAmount": 8.20
}
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "payId": 2106985371780235266,
    "payNo": "MFSOMSP202610050000002",
    "status": 2,
    "statusName": "成功",
    "channelTradeNo": "MOCK_TN_1732000000000"
  }
}
```

#### 业务异常

| 场景         | code | message             |
| ------------ | ---- | ------------------- |
| 付款码支付失败 | 400  | `"支付失败，请重试"` |

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/pay/barcode" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 2106985371171164162,
    "orderNo": "SO2026100500010000034",
    "storeId": 1,
    "payType": 2,
    "authCode": "134567890123456789",
    "payAmount": 8.20
  }'
```

---

### 3.3 Native 支付（扫码支付）

**POST** `/api/v1/pay/native`

**所需权限**：`pay:create`

**说明**：PC 端生成二维码，**异步回调**通知支付结果。

#### 请求体

| 字段         | 类型         | 必填 | 说明                          |
| ------------ | ------------ | ---- | ----------------------------- |
| `orderId`    | `Long`       | 是   | 订单ID                        |
| `orderNo`    | `String`     | 否   | 订单号                        |
| `storeId`    | `Long`       | 否   | 门店ID                        |
| `payType`    | `Integer`    | 否   | 2微信 3支付宝                 |
| `payAmount`  | `BigDecimal` | 是   | 应付金额                      |

#### 请求体示例

```json
{
  "orderId": 2106985371171164162,
  "orderNo": "SO2026100500010000034",
  "storeId": 1,
  "payType": 2,
  "payAmount": 8.20
}
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "payId": 2106985371780235267,
    "payNo": "MFSOMSP202610050000003",
    "status": 1,
    "statusName": "支付中",
    "codeUrl": "weixin://wxpay/bizpayurl?pr=MFSOMSP202610050000003"
  }
}
```

**前端把 `codeUrl` 生成二维码展示给顾客扫。**

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/pay/native" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 2106985371171164162,
    "orderNo": "SO2026100500010000034",
    "storeId": 1,
    "payType": 2,
    "payAmount": 8.20
  }'
```

---

### 3.4 JSAPI 支付（小程序 / 公众号）

**POST** `/api/v1/pay/jsapi`

**所需权限**：`pay:create`

#### 请求体

| 字段         | 类型         | 必填 | 说明                          |
| ------------ | ------------ | ---- | ----------------------------- |
| `orderId`    | `Long`       | 是   | 订单ID                        |
| `orderNo`    | `String`     | 否   | 订单号                        |
| `storeId`    | `Long`       | 否   | 门店ID                        |
| `payType`    | `Integer`    | 否   | 2微信 3支付宝                 |
| `payAmount`  | `BigDecimal` | 是   | 应付金额                      |
| `openid`     | `String`     | 是   | 用户在支付渠道的 openid       |

#### 请求体示例

```json
{
  "orderId": 2106985371171164162,
  "orderNo": "SO2026100500010000034",
  "storeId": 1,
  "payType": 2,
  "payAmount": 8.20,
  "openid": "o1234567890abcdefghijklmn"
}
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "payId": 2106985371780235268,
    "payNo": "MFSOMSP202610050000004",
    "status": 1,
    "statusName": "支付中",
    "jsapiParams": {
      "appId": "mock_appid",
      "timeStamp": "1732000000",
      "nonceStr": "a1b2c3d4e5f6",
      "package": "prepay_id=mock_MFSOMSP202610050000004",
      "signType": "RSA",
      "paySign": "mock_sign"
    }
  }
}
```

**前端把 `jsapiParams` 传给微信 JSAPI `chooseWXPay`。**

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/pay/jsapi" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 2106985371171164162,
    "orderNo": "SO2026100500010000034",
    "storeId": 1,
    "payType": 2,
    "payAmount": 8.20,
    "openid": "o1234567890abcdefghijklmn"
  }'
```

---

## 4. 支付查询接口

### 4.1 查询支付结果

**GET** `/api/v1/pay/query/{payNo}`

**所需权限**：`pay:list`

#### 路径参数

| 参数    | 类型     | 说明       |
| ------- | -------- | ---------- |
| `payNo` | `String` | 支付单号   |

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "payId": 2106985371780235265,
    "payNo": "MFSOMSP202610050000001",
    "status": 2,
    "statusName": "成功",
    "channelTradeNo": null
  }
}
```

#### 业务异常

| 场景         | code | message             |
| ------------ | ---- | ------------------- |
| 支付单不存在 | 400  | `"支付单不存在"`    |

#### cURL 示例

```bash
curl "http://localhost/api/v1/pay/query/MFSOMSP202610050000001" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 4.2 支付单详情

**GET** `/api/v1/pay/detail/{payId}`

**所需权限**：`pay:list`

#### 路径参数

| 参数    | 类型   | 说明       |
| ------- | ------ | ---------- |
| `payId` | `Long` | 支付单ID   |

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "payId": 2106985371780235265,
    "payNo": "MFSOMSP202610050000001",
    "orderId": 2106985371171164162,
    "orderNo": "SO2026100500010000034",
    "storeId": 1,
    "customerId": null,
    "payType": 1,
    "payTypeName": "现金",
    "payScene": 1,
    "payAmount": 8.20,
    "paidAmount": 8.20,
    "status": 2,
    "statusName": "成功",
    "channelOrderNo": null,
    "payTime": "2026-10-05T13:50:33",
    "gmtCreate": "2026-10-05T13:50:32"
  }
}
```

#### cURL 示例

```bash
curl "http://localhost/api/v1/pay/detail/2106985371780235265" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 4.3 分页查询支付单

**GET** `/api/v1/pay/list`

**所需权限**：`pay:list`

#### 查询参数

| 参数       | 类型      | 必填 | 默认值 | 说明                       |
| ---------- | --------- | ---- | ------ | -------------------------- |
| `pageNum`  | `Integer` | 否   | `1`    | 页码                       |
| `pageSize` | `Integer` | 否   | `10`   | 每页数量                   |
| `payNo`    | `String`  | 否   | —      | 支付单号（模糊）           |
| `orderNo`  | `String`  | 否   | —      | 订单号（模糊）             |
| `storeId`  | `Long`    | 否   | —      | 门店ID                     |
| `payType`  | `Integer` | 否   | —      | 支付方式                   |
| `status`   | `Integer` | 否   | —      | 支付状态                   |
| `startDate`| `Date`    | 否   | —      | 开始日期                   |
| `endDate`  | `Date`    | 否   | —      | 结束日期                   |

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "records": [
      {
        "payId": 2106985371780235265,
        "payNo": "MFSOMSP202610050000001",
        "orderId": 2106985371171164162,
        "orderNo": "SO2026100500010000034",
        "storeId": 1,
        "payType": 1,
        "payTypeName": "现金",
        "payAmount": 8.20,
        "paidAmount": 8.20,
        "status": 2,
        "statusName": "成功",
        "payTime": "2026-10-05T13:50:33",
        "gmtCreate": "2026-10-05T13:50:32"
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
curl "http://localhost/api/v1/pay/list?pageNum=1&pageSize=10&storeId=1" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 5. 支付操作接口

### 5.1 关闭支付单

**POST** `/api/v1/pay/close`

**所需权限**：`pay:close`

#### 请求体

| 字段      | 类型     | 必填 | 说明       |
| --------- | -------- | ---- | ---------- |
| `payId`   | `Long`   | 是   | 支付单ID   |
| `reason`  | `String` | 否   | 关闭原因   |

#### 请求体示例

```json
{
  "payId": 2106985371780235267,
  "reason": "用户取消支付"
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

| 场景             | code | message                 |
| ---------------- | ---- | ----------------------- |
| 支付单不存在     | 400  | `"支付单不存在"`        |
| 已支付成功不能关闭 | 400  | `"已支付成功，不能关闭"` |

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/pay/close" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"payId": 2106985371780235267, "reason": "用户取消支付"}'
```

---

## 6. 退款接口

### 6.1 申请退款

**POST** `/api/v1/pay/refund`

**所需权限**：`pay:refund`

#### 请求体

| 字段           | 类型         | 必填 | 说明                          |
| -------------- | ------------ | ---- | ----------------------------- |
| `payId`        | `Long`       | 是   | 原支付单ID                    |
| `refundAmount` | `BigDecimal` | 是   | 退款金额，不能超过实付金额    |
| `reason`       | `String`     | 否   | 退款原因                      |

#### 请求体示例（全额退款）

```json
{
  "payId": 2106985371780235265,
  "refundAmount": 8.20,
  "reason": "顾客不满意"
}
```

#### 请求体示例（部分退款）

```json
{
  "payId": 2106985371780235265,
  "refundAmount": 3.00,
  "reason": "部分退款"
}
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "refundId": 2106985371780235300,
    "refundNo": "MFSOMSR202610050000001",
    "payNo": "MFSOMSP202610050000001",
    "orderNo": "SO2026100500010000034",
    "refundAmount": 8.20,
    "status": 2,
    "statusName": "成功",
    "refundTime": "2026-10-05T14:00:00",
    "gmtCreate": "2026-10-05T14:00:00"
  }
}
```

#### 业务异常

| 场景                       | code | message                     |
| -------------------------- | ---- | --------------------------- |
| 支付单不存在               | 400  | `"支付单不存在"`            |
| 只有成功订单可退款         | 400  | `"只有支付成功的订单可以退款"` |
| 退款金额超过实付           | 400  | `"退款金额不能超过实付金额"` |

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/pay/refund" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"payId": 2106985371780235265, "refundAmount": 8.20, "reason": "顾客不满意"}'
```

---

### 6.2 退款查询

**GET** `/api/v1/pay/refund/query/{refundNo}`

**所需权限**：`pay:refund`

#### 路径参数

| 参数       | 类型     | 说明       |
| ---------- | -------- | ---------- |
| `refundNo` | `String` | 退款单号   |

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "refundId": 2106985371780235300,
    "refundNo": "MFSOMSR202610050000001",
    "payNo": "MFSOMSP202610050000001",
    "orderNo": "SO2026100500010000034",
    "refundAmount": 8.20,
    "status": 2,
    "statusName": "成功",
    "refundTime": "2026-10-05T14:00:00"
  }
}
```

#### 业务异常

| 场景         | code | message             |
| ------------ | ---- | ------------------- |
| 退款单不存在 | 400  | `"退款单不存在"`    |

#### cURL 示例

```bash
curl "http://localhost/api/v1/pay/refund/query/MFSOMSR202610050000001" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 7. 支付回调接口

### 7.1 微信支付回调

**POST** `/api/v1/pay/callback/wechat`

**无需权限，第三方调。**

#### 请求头

```
Content-Type: application/json
Wechatpay-Signature: xxx
Wechatpay-Timestamp: xxx
Wechatpay-Nonce: xxx
Wechatpay-Serial: xxx
```

#### 请求体（微信支付回调报文）

```json
{
  "id": "evt_1234567890",
  "create_time": "2026-10-05T14:00:00+08:00",
  "resource_type": "encrypt-resource",
  "event_type": "TRANSACTION.SUCCESS",
  "summary": "支付成功",
  "resource": {
    "original_type": "transaction",
    "algorithm": "AEAD_AES_256_GCM",
    "ciphertext": "encrypted_data",
    "associated_data": "transaction",
    "nonce": "nonce_string"
  }
}
```

#### 成功响应

```
SUCCESS
```

**注意：这是纯字符串，不是 JSON。**

**失败返回 `FAIL`，微信会重试。**

---

### 7.2 支付宝支付回调

**POST** `/api/v1/pay/callback/alipay`

**无需权限，第三方调。**

#### 请求体（表单参数）

```
out_trade_no=MFSOMSP202610050000004
trade_no=2026100522001400000000000001
trade_status=TRADE_SUCCESS
total_amount=8.20
...
sign=xxx
sign_type=RSA2
```

#### 成功响应

```
SUCCESS
```

---

## 8. 支付单号规则

**支付单号：**

```
MFSOMSP + yyyyMMdd + 7位序列
```

**示例：**

```
MFSOMSP202610050000001
MFSOMSP202610050000002
...
MFSOMSP202610059999999
```

**退款单号：**

```
MFSOMSR + yyyyMMdd + 7位序列
```

**示例：**

```
MFSOMSR202610050000001
MFSOMSR202610050000002
```

| 项         | 说明                              |
| ---------- | --------------------------------- |
| 前缀       | `MFSOMSP`（支付）/ `MFSOMSR`（退款） |
| 日期       | `yyyyMMdd`                        |
| 序列       | 7 位，每天重置                    |
| 单日上限   | 9,999,999 笔                      |
| Redis key  | `pay:no:seq:{prefix}:{date}`      |

---

## 9. 支付流程

### 9.1 付款码支付（同步）

```
1. 收银员扫顾客付款码
2. 调 /pay/barcode
3. pay 调微信/支付宝付款码接口
4. 同步拿到支付结果
5. pay 更新支付单状态 + 通知 order
6. 返回给收银端
```

### 9.2 Native/JSAPI 支付（异步）

```
1. 调 /pay/native 或 /pay/jsapi
2. pay 调第三方下单接口
3. 返回二维码/JSAPI 参数给前端
4. 顾客扫码/支付
5. 第三方回调 /pay/callback/xxx
6. pay 更新支付单状态 + 通知 order
```

### 9.3 pay 通知 order

**支付成功后，pay 调 order 的内部接口：**

```
POST http://service-order/api/v1/order/internal/pay-success
{
  "orderNo": "SO...",
  "payType": 2,
  "paidAmount": 8.20
}
```

**order 收到后：扣库存、更新订单状态、核销券。**

---

## 10. Feign 调用关系

```
service-pay
    │
    │ 支付成功后调用
    ↓
service-order  /api/v1/order/internal/pay-success
```

---

## 11. 权限编码

| 编码          | 名称     |
| ------------- | -------- |
| `pay:list`    | 支付记录 |
| `pay:create`  | 发起支付 |
| `pay:close`   | 关闭支付 |
| `pay:refund`  | 退款管理 |

---

## 12. 当前实现说明

**支付渠道目前是模拟实现，业务流程已跑通。**

**替换成真实渠道时：**

| 模拟方法 | 替换为 |
| -------- | ------ |
| `mockBarcodePay` | 微信/支付宝付款码接口 |
| `mockNativePay` | 微信 Native 下单 |
| `mockJsapiPay` | 微信 JSAPI 下单 |
| `mockRefund` | 微信/支付宝退款 |
| `parseOutTradeNo` / `parseAmount` / `parseTransactionId` | 解析真实回调报文 |

**依赖 SDK：**

- 微信：`com.github.wechatpay-apiv3:wechatpay-java`
- 支付宝：`com.alipay.sdk:alipay-sdk-java`

---

## 13. cURL 完整测试流程

```bash
# 0. 登录拿 token
curl -X POST http://localhost/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123","userType":2}'

TOKEN=eyJhbGciOiJIUzI1NiJ9...

# 1. 先创建订单（假设订单已创建，拿到 orderId/orderNo/payAmount）
# POST /api/v1/order
ORDER_ID=2106985371171164162
ORDER_NO=SO2026100500010000034
PAY_AMOUNT=8.20

# 2. 现金支付
curl -X POST "http://localhost/api/v1/pay/create" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"orderId\":$ORDER_ID,\"orderNo\":\"$ORDER_NO\",\"storeId\":1,\"payType\":1,\"payScene\":1,\"payAmount\":$PAY_AMOUNT}"

# 3. 查询支付单
curl "http://localhost/api/v1/pay/query/MFSOMSP202610050000001" \
  -H "Authorization: Bearer $TOKEN"

# 4. 分页查询
curl "http://localhost/api/v1/pay/list?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN"

# 5. 付款码支付（模拟成功，authCode 以 1 开头）
curl -X POST "http://localhost/api/v1/pay/barcode" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"orderId\":$ORDER_ID,\"orderNo\":\"$ORDER_NO\",\"storeId\":1,\"payType\":2,\"authCode\":\"134567890123456789\",\"payAmount\":$PAY_AMOUNT}"

# 6. Native 支付
curl -X POST "http://localhost/api/v1/pay/native" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"orderId\":$ORDER_ID,\"orderNo\":\"$ORDER_NO\",\"storeId\":1,\"payType\":2,\"payAmount\":$PAY_AMOUNT}"

# 7. JSAPI 支付
curl -X POST "http://localhost/api/v1/pay/jsapi" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"orderId\":$ORDER_ID,\"orderNo\":\"$ORDER_NO\",\"storeId\":1,\"payType\":2,\"payAmount\":$PAY_AMOUNT,\"openid\":\"o1234567890abcdefghijklmn\"}"

# 8. 申请退款
curl -X POST "http://localhost/api/v1/pay/refund" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"payId\":PAY_ID,\"refundAmount\":$PAY_AMOUNT,\"reason\":\"顾客不满意\"}"

# 9. 退款查询
curl "http://localhost/api/v1/pay/refund/query/MFSOMSR202610050000001" \
  -H "Authorization: Bearer $TOKEN"

# 10. 关闭支付单（针对未支付的）
curl -X POST "http://localhost/api/v1/pay/close" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"payId": PAY_ID, "reason": "用户取消"}'
```