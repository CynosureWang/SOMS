# SOMS 客户管理服务接口文档

> **服务名称**：`service-customer`  
> **服务端口**：`8010`  
> **基础路径**：`http://127.0.0.1:8010`  
> **版本**：`1.0.0`  
> **更新日期**：2026-09-16

---

## 1. 通用说明

### 1.1 统一响应格式

所有接口均返回统一的 JSON 结构：

```json
{
  "code": 200,
  "message": "SUCCESS",
  "data": {}
}
```

| 字段      | 类型     | 说明                                         |
|-----------|----------|----------------------------------------------|
| `code`    | `int`    | 状态码，`200` 表示成功，其他表示失败          |
| `message` | `String` | 响应消息                                      |
| `data`    | `Object` | 响应数据（部分接口为 `null`）                  |

### 1.2 状态码说明

| 状态码 | 说明                           |
|--------|--------------------------------|
| `200`  | 成功                           |
| `400`  | 失败（业务异常）               |
| `401`  | 参数错误（校验未通过）         |
| `404`  | 资源不存在                     |
| `500`  | 系统异常                       |
| `501`  | 未知错误                       |
| `502`  | TOKEN 为空或错误               |

### 1.3 数据字典 — 会员等级

| 值  | 含义     |
|-----|----------|
| `1` | 普通会员 |
| `2` | 银卡会员 |
| `3` | 金卡会员 |

### 1.4 数据字典 — 逻辑删除

| 值  | 含义   |
|-----|--------|
| `0` | 未删除 |
| `1` | 已删除 |

---

## 2. 数据模型 — Customer（客户）

| 字段           | 类型           | 必填 | 校验规则                            | 说明                                  |
|----------------|----------------|------|-------------------------------------|---------------------------------------|
| `customerId`   | `Long`         | 否   | 自增主键                            | 会员ID，新增时自动生成                |
| `customerName` | `String`       | 是   | 非空，最大 64 字符                  | 会员姓名                              |
| `mobile`       | `String`       | 是   | 非空，正则 `^1[3-9]\d{9}$`         | 手机号，唯一                           |
| `customerLevel`| `Integer`      | 否   | 范围 1 ~ 3，默认 1                  | 会员等级                              |
| `totalConsume` | `BigDecimal`   | 否   | —                                   | 累计消费金额，默认 0.00               |
| `balance`      | `BigDecimal`   | 否   | —                                   | 账户余额，默认 0.00                   |
| `gmtCreate`    | `LocalDateTime`| 否   | 自动填充                            | 创建时间                              |
| `gmtModified`  | `LocalDateTime`| 否   | 自动填充                            | 更新时间                              |
| `isDeleted`    | `Integer`      | 否   | 默认 0                              | 逻辑删除标志                          |

---

## 3. 接口详情

---

### 3.1 新增客户

**POST** `/customer`

新增一个客户记录，`customerId` 由系统自动生成。

#### 请求头

| Header         | 值             | 必填 |
|----------------|----------------|------|
| Content-Type   | application/json | 是  |

#### 请求体

```json
{
  "customerName": "张三",
  "mobile": "13800138000",
  "customerLevel": 1,
  "totalConsume": 0.00,
  "balance": 100.00
}
```

#### 成功响应

**HTTP 200**

```json
{
  "code": 200,
  "message": "新增客户成功",
  "data": {
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
}
```

#### 参数校验失败响应

**HTTP 400**

```json
{
  "code": 401,
  "message": "会员姓名不能为空; 手机号格式不正确",
  "data": null
}
```

---

### 3.2 删除客户（逻辑删除）

**DELETE** `/customer/{customerId}`

根据 ID 逻辑删除客户，`is_deleted` 字段置为 `1`。

#### 路径参数

| 参数         | 类型   | 必填 | 说明   |
|--------------|--------|------|--------|
| `customerId` | `Long` | 是   | 会员ID |

#### 请求示例

```
DELETE /customer/1
```

#### 成功响应

**HTTP 200**

```json
{
  "code": 200,
  "message": "删除客户成功",
  "data": {
    "customerId": 1,
    "customerName": "张三",
    "mobile": "13800138000",
    "customerLevel": 1,
    "totalConsume": 0.00,
    "balance": 100.00,
    "gmtCreate": "2026-09-16T10:30:00",
    "gmtModified": "2026-09-16T11:00:00",
    "isDeleted": 1
  }
}
```

---

### 3.3 修改客户信息

**PUT** `/customer`

更新客户信息，需携带 `customerId`。

#### 请求头

| Header         | 值             | 必填 |
|----------------|----------------|------|
| Content-Type   | application/json | 是  |

#### 请求体

```json
{
  "customerId": 1,
  "customerName": "张三（已更新）",
  "mobile": "13800138000",
  "customerLevel": 2,
  "totalConsume": 500.00,
  "balance": 200.00
}
```

#### 成功响应

**HTTP 200**

```json
{
  "code": 200,
  "message": "修改客户信息成功",
  "data": {
    "customerId": 1,
    "customerName": "张三（已更新）",
    "mobile": "13800138000",
    "customerLevel": 2,
    "totalConsume": 500.00,
    "balance": 200.00,
    "gmtCreate": "2026-09-16T10:30:00",
    "gmtModified": "2026-09-16T11:30:00",
    "isDeleted": 0
  }
}
```

---

### 3.4 根据 ID 查询客户

**GET** `/customer/{customerId}`

根据会员 ID 查询单个客户信息。

#### 路径参数

| 参数         | 类型   | 必填 | 说明   |
|--------------|--------|------|--------|
| `customerId` | `Long` | 是   | 会员ID |

#### 请求示例

```
GET /customer/1
```

#### 成功响应

**HTTP 200**

```json
{
  "code": 200,
  "message": "SUCCESS",
  "data": {
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
}
```

---

### 3.5 根据手机号查询客户

**GET** `/customer/mobile/{mobile}`

根据手机号精确查询客户信息。

#### 路径参数

| 参数     | 类型     | 必填 | 说明                           |
|----------|----------|------|--------------------------------|
| `mobile` | `String` | 是   | 手机号，格式：`1[3-9]xxxxxxxxx` |

#### 请求示例

```
GET /customer/mobile/13800138000
```

#### 成功响应

**HTTP 200**

```json
{
  "code": 200,
  "message": "SUCCESS",
  "data": {
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
}
```

---

### 3.6 分页查询客户列表

**GET** `/customer/list`

支持分页及按姓名、手机号模糊搜索。

#### 查询参数

| 参数           | 类型      | 必填 | 默认值 | 说明                       |
|----------------|-----------|------|--------|----------------------------|
| `pageNum`      | `Integer` | 否   | `1`    | 页码                       |
| `pageSize`     | `Integer` | 否   | `10`   | 每页数量                   |
| `customerName` | `String`  | 否   | —      | 会员姓名（模糊匹配）       |
| `mobile`       | `String`  | 否   | —      | 手机号（模糊匹配）         |

#### 请求示例

```
GET /customer/list?pageNum=1&pageSize=10&customerName=张&mobile=138
```

#### 成功响应

**HTTP 200**

```json
{
  "code": 200,
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

| 字段      | 类型       | 说明                   |
|-----------|------------|------------------------|
| `records` | `Array`    | 当前页数据列表          |
| `total`   | `Long`     | 总记录数               |
| `size`    | `Integer`  | 每页条数               |
| `current` | `Integer`  | 当前页码               |
| `pages`   | `Integer`  | 总页数                 |

---

## 4. 错误响应汇总

| 场景             | HTTP 状态码 | code | 示例 message                                  |
|------------------|-------------|------|-----------------------------------------------|
| 参数校验失败     | 400         | 401  | `"会员姓名不能为空; 手机号格式不正确"`          |
| 请求体 JSON 错误 | 400         | 401  | `"请求体格式错误，请检查JSON格式"`              |
| 业务异常         | 200         | 400  | `"客户不存在"` 等自定义业务消息                 |
| 系统异常         | 500         | 500  | `"系统异常，请联系管理员"`                      |
| 未知错误         | 500         | 501  | `"未知错误，请联系开发人员"`                    |

---

## 5. cURL 测试示例

```bash
# 新增客户
curl -X POST http://127.0.0.1:8010/customer \
  -H "Content-Type: application/json" \
  -d '{"customerName":"张三","mobile":"13800138000","customerLevel":1,"balance":100.00}'

# 查询客户
curl -X GET http://127.0.0.1:8010/customer/1

# 按手机号查询
curl -X GET http://127.0.0.1:8010/customer/mobile/13800138000

# 分页查询
curl -X GET "http://127.0.0.1:8010/customer/list?pageNum=1&pageSize=10&customerName=张"

# 修改客户
curl -X PUT http://127.0.0.1:8010/customer \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"customerName":"张三（已更新）","customerLevel":2}'

# 删除客户
curl -X DELETE http://127.0.0.1:8010/customer/1
```
