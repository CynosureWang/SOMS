# SOMS 客户会员模块接口文档

> **服务名称**
> ：
> `service-customer`
> **服务端口**
> ：
> `8010`
> **基础路径**
> ：
> `http://127.0.0.1:8010`
> **版本**
> ：
> `1.1.0`
> **首次版本日期**
> ：2026-09-22
> **说明**
> ：本文档记录客户会员模块（余额 / 积分 / 等级 / 状态 / 内部契约）新增能力，客户基础 CRUD 见《API - 客户管理服务.md》



***

## 1. 数据库变更

### 1.1 customer 表新增字段



| 字段       | 类型        | 默认值 | 说明           |
| -------- | --------- | --- | ------------ |
| `points` | `INT`     | `0` | 积分余额         |
| `status` | `TINYINT` | `1` | 状态：0 禁用，1 正常 |

### 1.2 新增表：customer\_balance\_log（会员余额流水）



| 字段                            | 类型              | 说明                                  |
| ----------------------------- | --------------- | ----------------------------------- |
| `log_id`                      | `BIGINT`        | 流水 ID（自增主键）                         |
| `customer_id`                 | `BIGINT`        | 会员 ID                               |
| `change_type`                 | `TINYINT`       | 变动类型：1 充值，2 消费扣减，3 退款，4 人工调整        |
| `change_amount`               | `DECIMAL(10,2)` | 变动金额（正数为增加，负数为扣减）                   |
| `balance_before`              | `DECIMAL(10,2)` | 变动前余额                               |
| `balance_after`               | `DECIMAL(10,2)` | 变动后余额                               |
| `biz_type`                    | `VARCHAR(32)`   | 业务类型，如 RECHARGE/ORDER/REFUND/ADJUST |
| `biz_id`                      | `VARCHAR(64)`   | 业务单号（幂等键）                           |
| `remark`                      | `VARCHAR(255)`  | 备注                                  |
| `gmt_create` / `gmt_modified` | `DATETIME`      | 创建 / 更新时间                           |

**唯一约束**：`uk_biz (biz_type, biz_id)` —— 同一业务单号只允许产生一条流水，用于幂等。

### 1.3 新增表：customer\_points\_log（会员积分流水）



| 字段                            | 类型             | 说明                        |
| ----------------------------- | -------------- | ------------------------- |
| `log_id`                      | `BIGINT`       | 流水 ID（自增主键）               |
| `customer_id`                 | `BIGINT`       | 会员 ID                     |
| `change_type`                 | `TINYINT`      | 变动类型：1 消费获得，2 积分使用，3 人工调整 |
| `change_points`               | `INT`          | 变动积分（正数为增加，负数为扣减）         |
| `points_before`               | `INT`          | 变动前积分                     |
| `points_after`                | `INT`          | 变动后积分                     |
| `biz_type`                    | `VARCHAR(32)`  | 业务类型，如 CONSUME/USE/ADJUST |
| `biz_id`                      | `VARCHAR(64)`  | 业务单号（幂等键）                 |
| `remark`                      | `VARCHAR(255)` | 备注                        |
| `gmt_create` / `gmt_modified` | `DATETIME`     | 创建 / 更新时间                 |

**唯一约束**：`uk_biz (biz_type, biz_id)`。

### 1.4 存量库升级脚本

已在本地建过 `customer` 表的执行：



```
ALTER TABLE \\\`customer\\\`

\&#x20;   ADD COLUMN \\\`points\\\` INT NOT NULL DEFAULT 0 COMMENT '积分余额' AFTER \\\`balance\\\`,

\&#x20;   ADD COLUMN \\\`status\\\` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用，1正常' AFTER \\\`points\\\`;
```

两张流水表的完整 DDL 见 `soms-server/soms-customer-service/src/main/resources/db/schema.sql`。全新部署直接执行 `schema.sql` 即可。



***

## 2. 业务规则（当前为代码内常量，后续可迁移至 Nacos 配置）



| 规则     | 默认值           | 说明                         |
| ------ | ------------- | -------------------------- |
| 银卡升级门槛 | 累计消费 ≥ 1000 元 | 消费完成回调后自动重算等级              |
| 金卡升级门槛 | 累计消费 ≥ 5000 元 | 同上                         |
| 积分比例   | 消费 1 元 = 1 积分 | 向下取整（199.99 元 → 199 积分）    |
| 会员状态限制 | 状态为 0（禁用）     | 不能充值 / 扣减 / 消费回调，但可查询账户与流水 |

会员等级：`1` 普通会员，`2` 银卡会员，`3` 金卡会员。



***

## 3. 会员端接口

### 3.1 余额充值

**POST** `/api/v1/customer/{customerId}/recharge`



| 参数       | 类型           | 必填 | 说明                         |
| -------- | ------------ | -- | -------------------------- |
| `amount` | `BigDecimal` | 是  | 充值金额（元），必须大于 0             |
| `bizId`  | `String`     | 否  | 业务单号（支付回调幂等键，传入则同一单号只生效一次） |
| `remark` | `String`     | 否  | 备注                         |

**请求示例**



```
{

\&#x20; "amount": 100.00,

\&#x20; "bizId": "PAY20260922001",

\&#x20; "remark": "微信充值"

}
```

**成功响应**



```
{

\&#x20; "code": 0,

\&#x20; "message": "充值成功",

\&#x20; "data": {

\&#x20;   "customerId": 1,

\&#x20;   "customerName": "张三",

\&#x20;   "mobile": "13800138000",

\&#x20;   "customerLevel": 1,

\&#x20;   "totalConsume": 0.00,

\&#x20;   "balance": 100.00,

\&#x20;   "points": 0,

\&#x20;   "status": 1

\&#x20; }

}
```

### 3.2 账户概览

**GET** `/api/v1/customer/{customerId}/account`

返回字段：`customerId` / `customerName` / `mobile` / `customerLevel` / `totalConsume` / `balance` / `points` / `status`。

**成功响应**



```
{

\&#x20; "code": 0,

\&#x20; "message": "SUCCESS",

\&#x20; "data": {

\&#x20;   "customerId": 1,

\&#x20;   "customerName": "张三",

\&#x20;   "mobile": "13800138000",

\&#x20;   "customerLevel": 2,

\&#x20;   "totalConsume": 2000.00,

\&#x20;   "balance": 150.00,

\&#x20;   "points": 2000,

\&#x20;   "status": 1

\&#x20; }

}
```

### 3.3 余额流水分页查询

**GET** `/api/v1/customer/{customerId}/balance-logs?pageNum=1&pageSize=10`



| 查询参数       | 类型        | 必填 | 默认值  | 说明   |
| ---------- | --------- | -- | ---- | ---- |
| `pageNum`  | `Integer` | 否  | `1`  | 页码   |
| `pageSize` | `Integer` | 否  | `10` | 每页数量 |

返回分页结构 `PageResult`，`records` 元素字段：



| 字段                               | 类型              | 说明                           |
| -------------------------------- | --------------- | ---------------------------- |
| `logId`                          | `Long`          | 流水 ID                        |
| `customerId`                     | `Long`          | 会员 ID                        |
| `changeType`                     | `Integer`       | 变动类型：1 充值，2 消费扣减，3 退款，4 人工调整 |
| `changeAmount`                   | `BigDecimal`    | 变动金额（正增负减）                   |
| `balanceBefore` / `balanceAfter` | `BigDecimal`    | 变动前 / 后余额                    |
| `bizType` / `bizId`              | `String`        | 业务类型 / 业务单号                  |
| `remark`                         | `String`        | 备注                           |
| `gmtCreate`                      | `LocalDateTime` | 变动时间                         |

**成功响应**



```
{

\&#x20; "code": 0,

\&#x20; "message": "SUCCESS",

\&#x20; "data": {

\&#x20;   "records": \\\[

\&#x20;     {

\&#x20;       "logId": 1,

\&#x20;       "customerId": 1,

\&#x20;       "changeType": 1,

\&#x20;       "changeAmount": 100.00,

\&#x20;       "balanceBefore": 0.00,

\&#x20;       "balanceAfter": 100.00,

\&#x20;       "bizType": "RECHARGE",

\&#x20;       "bizId": "PAY20260922001",

\&#x20;       "remark": "微信充值",

\&#x20;       "gmtCreate": "2026-09-22T10:30:00"

\&#x20;     }

\&#x20;   ],

\&#x20;   "total": 1,

\&#x20;   "size": 10,

\&#x20;   "current": 1,

\&#x20;   "pages": 1

\&#x20; }

}
```

### 3.4 积分流水分页查询

**GET** `/api/v1/customer/{customerId}/points-logs?pageNum=1&pageSize=10`

参数与分页结构与余额流水一致，`records` 元素字段：



| 字段                             | 类型              | 说明                        |
| ------------------------------ | --------------- | ------------------------- |
| `logId`                        | `Long`          | 流水 ID                     |
| `customerId`                   | `Long`          | 会员 ID                     |
| `changeType`                   | `Integer`       | 变动类型：1 消费获得，2 积分使用，3 人工调整 |
| `changePoints`                 | `Integer`       | 变动积分（正增负减）                |
| `pointsBefore` / `pointsAfter` | `Integer`       | 变动前 / 后积分                 |
| `bizType` / `bizId`            | `String`        | 业务类型 / 业务单号               |
| `remark`                       | `String`        | 备注                        |
| `gmtCreate`                    | `LocalDateTime` | 变动时间                      |

### 3.5 启用 / 禁用会员

**PUT** `/api/v1/customer/{customerId}/status?status=0|1`



| 参数       | 类型        | 必填 | 说明            |
| -------- | --------- | -- | ------------- |
| `status` | `Integer` | 是  | `0` 禁用，`1` 正常 |

**成功响应**：返回更新后的账户概览，`message` 为 "状态更新成功"。

> 说明：被禁用的会员无法充值、扣减余额、消费回调；账户与流水查询不受影响。



***

## 4. 内部接口（仅供内部服务经 Feign 调用）

> 约定：
> `/internal/**`
> 不对外暴露，网关应排除路由；调用方使用
> `com.mfnit.common.api.client.CustomerFeignClient`
> 。

### 4.1 批量查询会员

**GET** `/api/v1/customer/internal/batch?customerIds=1,2,3`



| 参数            | 类型           | 必填 | 说明             |
| ------------- | ------------ | -- | -------------- |
| `customerIds` | `List<Long>` | 是  | 会员 ID 集合（逗号分隔） |

**成功响应**



```
{

\&#x20; "code": 0,

\&#x20; "message": "SUCCESS",

\&#x20; "data": \\\[

\&#x20;   {

\&#x20;     "customerId": 1,

\&#x20;     "customerName": "张三",

\&#x20;     "mobile": "13800138000",

\&#x20;     "customerLevel": 2

\&#x20;   }

\&#x20; ]

}
```

### 4.2 余额扣减（幂等）

**POST** `/api/v1/customer/internal/balance/deduct`



| 参数           | 类型           | 必填 | 说明             |
| ------------ | ------------ | -- | -------------- |
| `customerId` | `Long`       | 是  | 会员 ID          |
| `amount`     | `BigDecimal` | 是  | 扣减金额（元），必须大于 0 |
| `bizType`    | `String`     | 是  | 业务类型，如 ORDER   |
| `bizId`      | `String`     | 是  | 业务单号（幂等键）      |
| `remark`     | `String`     | 否  | 备注             |

**请求示例**



```
{

\&#x20; "customerId": 1,

\&#x20; "amount": 50.00,

\&#x20; "bizType": "ORDER",

\&#x20; "bizId": "SO20260922001001",

\&#x20; "remark": "订单支付"

}
```

**行为说明**



* 原子扣减并校验余额，余额不足返回业务异常（code=401，"余额不足"）

* 同一 `bizType + bizId` 重复调用不重复扣款，直接返回当前账户

* 会员不存在返回 404；会员禁用返回 403（"账号已锁定或禁用"）

**成功响应**：返回 `CustomerAccountDTO`（字段同账户概览），`message` 为 "扣减成功"。

### 4.3 消费完成回调（幂等）

**POST** `/api/v1/customer/internal/consume/complete`



| 参数              | 类型           | 必填 | 说明           |
| --------------- | ------------ | -- | ------------ |
| `customerId`    | `Long`       | 是  | 会员 ID        |
| `consumeAmount` | `BigDecimal` | 是  | 消费金额（元），不能为负 |
| `bizType`       | `String`     | 是  | 业务类型，如 ORDER |
| `bizId`         | `String`     | 是  | 业务单号（幂等键）    |

**请求示例**



```
{

\&#x20; "customerId": 1,

\&#x20; "consumeAmount": 2000.00,

\&#x20; "bizType": "ORDER",

\&#x20; "bizId": "SO20260922001001"

}
```

**行为说明**



1. 累加 `total_consume`（累计消费）

2. 按消费金额赠送积分（1 元 = 1 积分，向下取整），写积分流水

3. 重算会员等级：累计消费 ≥1000 升银卡，≥5000 升金卡

4. 同一 `bizType + bizId` 重复调用不重复累计 / 赠送，直接返回当前账户

**成功响应**：返回 `CustomerAccountDTO`，`message` 为 "消费回调成功"。



***

## 5. Feign 契约（soms-common）

`com.mfnit.common.api.client.CustomerFeignClient`（`path = /api/v1/customer`）本次扩展方法：



| 方法                                    | HTTP | 路径                           |
| ------------------------------------- | ---- | ---------------------------- |
| `batchCustomers(List<Long>)`          | GET  | `/internal/batch`            |
| `deductBalance(BalanceDeductDTO)`     | POST | `/internal/balance/deduct`   |
| `completeConsume(ConsumeCompleteDTO)` | POST | `/internal/consume/complete` |

新增 DTO（`com.mfnit.common.api.dto.customer`）：



| DTO                  | 用途                             |
| -------------------- | ------------------------------ |
| `CustomerAccountDTO` | 账户信息（余额 / 积分 / 等级 / 累计消费 / 状态） |
| `BalanceDeductDTO`   | 余额扣减请求                         |
| `ConsumeCompleteDTO` | 消费完成回调请求                       |



***



***

## 6. cURL 测试示例（可直接导入 Postman）

> 以下 14 条命令覆盖「客户管理 → 会员账户 → 内部接口」完整链路，
>
> `{{customerId}}`
>
>  由「新增客户」的响应自动填充；也可在 Postman 集合变量中手动修改。
> 默认直连 
>
> `8010`
>
>  端口（customer 服务无鉴权，可直接调试）。



```
\# 01 新增客户（返回 data.customerId 会写入集合变量，供后续接口使用）

curl -X POST http://127.0.0.1:8010/api/v1/customer \\

&#x20; -H "Content-Type: application/json" \\

&#x20; -d '{"customerName":"张三","mobile":"13800138000","customerLevel":1,"balance":100.00}'

\# 02 分页查询客户列表（姓名/手机号模糊搜索）

curl -X GET "http://127.0.0.1:8010/api/v1/customer/list?pageNum=1\&pageSize=10\&customerName=张"

\# 03 按ID查询客户

curl -X GET http://127.0.0.1:8010/api/v1/customer/1

\# 04 按手机号查询客户

curl -X GET http://127.0.0.1:8010/api/v1/customer/mobile/13800138000

\# 05 修改客户（部分字段：customerId 必填，其余传了才更新）

curl -X PUT http://127.0.0.1:8010/api/v1/customer \\

&#x20; -H "Content-Type: application/json" \\

&#x20; -d '{"customerId":1,"customerName":"张三（已更新）","customerLevel":2}'

\# 06 余额充值（bizId 可选，传了则同一单号幂等）

curl -X POST http://127.0.0.1:8010/api/v1/customer/1/recharge \\

&#x20; -H "Content-Type: application/json" \\

&#x20; -d '{"amount":500.00,"bizId":"PAY20260922001","remark":"微信充值"}'

\# 07 账户概览（余额/积分/等级/累计消费）

curl -X GET http://127.0.0.1:8010/api/v1/customer/1/account

\# 08 余额流水

curl -X GET "http://127.0.0.1:8010/api/v1/customer/1/balance-logs?pageNum=1\&pageSize=10"

\# 09 积分流水

curl -X GET "http://127.0.0.1:8010/api/v1/customer/1/points-logs?pageNum=1\&pageSize=10"

\# 10 启用/禁用会员（status：0禁用，1正常）

curl -X PUT "http://127.0.0.1:8010/api/v1/customer/1/status?status=1"

\# 11 内部-批量查询会员

curl -X GET "http://127.0.0.1:8010/api/v1/customer/internal/batch?customerIds=1,2"

\# 12 内部-余额扣减（幂等：同一 bizId 重复调用只生效一次）

curl -X POST http://127.0.0.1:8010/api/v1/customer/internal/balance/deduct \\

&#x20; -H "Content-Type: application/json" \\

&#x20; -d '{"customerId":1,"amount":50.00,"bizType":"ORDER","bizId":"SO20260922001001","remark":"订单支付"}'

\# 13 内部-消费完成回调（累加累计消费+返积分+重算等级，幂等）

curl -X POST http://127.0.0.1:8010/api/v1/customer/internal/consume/complete \\

&#x20; -H "Content-Type: application/json" \\

&#x20; -d '{"customerId":1,"consumeAmount":2000.00,"bizType":"ORDER","bizId":"SO20260922001001"}'

\# 14 删除客户（逻辑删除）

curl -X DELETE http://127.0.0.1:8010/api/v1/customer/1
```



***

## 7. Postman 一键导入

### 方式一：导入 Collection 文件（推荐）

Postman Collection 文件位置：`docs/postman/SOMS-客户服务.postman_collection.json`



1. 打开 Postman，点击左上角 **Import**（或 Collections 面板的 Import 按钮）

2. 选择 **Upload Files**，选中 `SOMS-客户服务.postman_collection.json`

3. 导入后生成集合，已按「01 - 客户管理（CRUD）/ 02 - 会员账户 / 03 - 内部接口」分组

集合变量：



| 变量           | 默认值                     | 说明                               |
| ------------ | ----------------------- | -------------------------------- |
| `baseUrl`    | `http://127.0.0.1:8010` | 服务地址，经网关改为 `http://127.0.0.1:80` |
| `customerId` | `1`                     | 测试会员 ID，「新增客户」成功返回后自动写入          |

### 方式二：粘贴 cURL 文本



1. 打开 Postman，点击 **Import**

2. 选择 **Paste Raw Text**，复制第 6 节全部 curl 命令粘贴

3. 点击 Import，Postman 会自动解析并生成请求集合



***

## 8. 错误码汇总



| 场景     | HTTP 状态码 | code | message                  |
| ------ | -------- | ---- | ------------------------ |
| 参数校验失败 | 400      | 401  | 如 "充值金额不能为空"" 手机号格式不正确 " |
| 余额不足   | 200      | 401  | "余额不足"                   |
| 会员不存在  | 200      | 404  | "客户不存在"                  |
| 会员已禁用  | 200      | 403  | "账号已锁定或禁用"               |
| 状态参数错误 | 200      | 401  | "状态参数错误，仅支持 0 禁用 / 1 正常" |
| 系统异常   | 500      | 500  | "系统异常，请联系管理员"            |