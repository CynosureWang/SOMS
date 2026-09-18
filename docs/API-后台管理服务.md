# SOMS 后台管理服务接口文档

> **服务名称**：`service-admin`  
> **服务端口**：`8000`  
> **基础路径**：`http://127.0.0.1:8000`  
> **版本**：`1.0.0`  
> **首次版本日期**：2026-09-18  
> **更新日期**：2026-09-18

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

| 状态码 | 说明                   |
| ------ | ---------------------- |
| `0`    | 成功                   |
| `400`  | 失败（业务异常）       |
| `401`  | 参数错误（校验未通过） |
| `404`  | 资源不存在             |
| `500`  | 系统异常               |
| `501`  | 未知错误               |
| `502`  | TOKEN 为空或错误       |

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

### 3.1 获取微服务状态

**GET** `/api/v1/admin/nacos/server/status`

从Nacos中获取微服务服务状态。

#### 请求体示例

```json
GET http://127.0.0.1:8000/api/v1/admin/nacos/server/status
```

#### 成功响应

**HTTP 200**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "services": [
      {
        "serviceName": "service-customer",
        "instances": [
          {
            "ip": "169.254.213.131",
            "port": 8010,
            "group": "DEFAULT_GROUP",
            "serviceName": "service-customer"
          }
        ]
      },
      {
        "serviceName": "service-admin",
        "instances": [
          {
            "ip": "169.254.213.131",
            "port": 8000,
            "group": "DEFAULT_GROUP",
            "serviceName": "service-admin"
          }
        ]
      }
    ]
  }
}
```

---

### 3.2 查询客户列表

**GET** `/api/v1/admin/customer/list`

通过远程调用客户管理服务 ( service-customer ) 分页获取客户列表，支持分页及按姓名、手机号模糊搜索。

#### 查询参数

| 参数           | 类型      | 必填 | 默认值 | 说明                 |
| -------------- | --------- | ---- | ------ | -------------------- |
| `pageNum`      | `Integer` | 否   | `1`    | 页码                 |
| `pageSize`     | `Integer` | 否   | `10`   | 每页数量             |
| `customerName` | `String`  | 否   | —      | 会员姓名（模糊匹配） |
| `mobile`       | `String`  | 否   | —      | 手机号（模糊匹配）   |

#### 请求示例

```
GET /api/v1/admin/customer/list?pageNum=1&pageSize=10&customerName=张&mobile=138
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

| 字段      | 类型      | 说明           |
| --------- | --------- | -------------- |
| `records` | `Array`   | 当前页数据列表 |
| `total`   | `Long`    | 总记录数       |
| `size`    | `Integer` | 每页条数       |
| `current` | `Integer` | 当前页码       |
| `pages`   | `Integer` | 总页数         |

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
# 获取nacos微服务状态
curl -X GET http://127.0.0.1:8000/api/v1/admin/nacos/server/status

# admin分页查询客户列表
curl -X GET "http://127.0.0.1:8000/api/v1/admin/customer/list?pageNum=1&pageSize=10&customerName=张"
```
