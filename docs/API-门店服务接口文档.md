# SOMS 门店服务接口文档

> **服务名称**：`service-store`
> **服务端口**：`8200`
> **基础路径**：`http://127.0.0.1:8160`
> **网关路径**：`http://localhost/api/v1/store`
> **版本**：`1.1.0`
> **首次版本日期**：2026-10-05

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

> 请求经网关转发时，网关会额外注入 `X-User-Id`、`X-User-Type`、`X-Roles`、`X-Gateway-Timestamp`、`X-Gateway-Sign` 等内部头，业务方无需关心。

### 1.4 数据字典

**区域类型（regionType）**

| 值  | 含义 |
|-----|------|
| `1` | 大区 |
| `2` | 城市 |
| `3` | 片区 |

**门店类型（storeType）**

| 值  | 含义 |
|-----|------|
| `1` | 直营 |
| `2` | 加盟 |
| `3` | 联营 |

**经营状态（businessStatus）**

| 值  | 含义 |
|-----|------|
| `1` | 筹备 |
| `2` | 营业 |
| `3` | 暂停 |
| `4` | 闭店 |

**营业时间日期类型（dayType）**

| 值  | 含义   |
|-----|--------|
| `1` | 工作日 |
| `2` | 周末   |
| `3` | 节假日 |

**联系人角色（contactRole）**

| 值  | 含义 |
|-----|------|
| `1` | 店长 |
| `2` | 财务 |
| `3` | 收货 |
| `4` | 客服 |

---

## 2. 数据模型

### 2.1 StoreRegion（区域）

| 字段         | 类型      | 说明                          |
| ------------ | --------- | ----------------------------- |
| `regionId`   | `Long`    | 区域ID（雪花）                |
| `regionCode` | `String`  | 区域编码，全局唯一            |
| `regionName` | `String`  | 区域名称                      |
| `regionType` | `Integer` | 1大区 2城市 3片区             |
| `parentId`   | `Long`    | 上级区域ID，根节点为 0        |
| `level`      | `Integer` | 层级，1~3                     |
| `path`       | `String`  | 路径，如 `/1/10/100/`         |
| `sort`       | `Integer` | 排序                          |
| `status`     | `Integer` | 1启用 2禁用                   |
| `remark`     | `String`  | 备注                          |

### 2.2 Store（门店）

| 字段             | 类型           | 说明                          |
| ---------------- | -------------- | ----------------------------- |
| `storeId`        | `Long`         | 门店ID（雪花）                |
| `storeCode`      | `String`       | 门店编码，全局唯一            |
| `storeName`      | `String`       | 门店名称                      |
| `shortName`      | `String`       | 简称                          |
| `storeType`      | `Integer`      | 1直营 2加盟 3联营             |
| `businessStatus` | `Integer`      | 1筹备 2营业 3暂停 4闭店       |
| `regionId`       | `Long`         | 大区ID                        |
| `cityId`         | `Long`         | 城市ID                        |
| `areaId`         | `Long`         | 片区ID                        |
| `parentStoreId`  | `Long`         | 上级门店ID                    |
| `address`        | `String`       | 详细地址                      |
| `longitude`      | `BigDecimal`   | 经度                          |
| `latitude`       | `BigDecimal`   | 纬度                          |
| `areaSize`       | `BigDecimal`   | 营业面积（㎡）                |
| `openDate`       | `LocalDate`    | 开业日期                      |
| `closeDate`      | `LocalDate`    | 闭店日期                      |
| `managerId`      | `Long`         | 店长ID                        |
| `managerPhone`   | `String`       | 店长电话                      |
| `servicePhone`   | `String`       | 客服电话                      |
| `remark`         | `String`       | 备注                          |

### 2.3 StoreBusinessHours（营业时间）

| 字段        | 类型        | 说明                    |
| ----------- | ----------- | ----------------------- |
| `id`        | `Long`      | 主键                    |
| `storeId`   | `Long`      | 门店ID                  |
| `dayType`   | `Integer`   | 1工作日 2周末 3节假日   |
| `openTime`  | `LocalTime` | 开始营业时间            |
| `closeTime` | `LocalTime` | 结束营业时间            |
| `is24h`     | `Integer`   | 是否24小时 0否 1是      |

### 2.4 StoreContact（联系人）

| 字段          | 类型      | 说明                    |
| ------------- | --------- | ----------------------- |
| `id`          | `Long`    | 主键                    |
| `storeId`     | `Long`    | 门店ID                  |
| `contactName` | `String`  | 姓名                    |
| `contactRole` | `Integer` | 1店长 2财务 3收货 4客服 |
| `phone`       | `String`  | 电话                    |
| `email`       | `String`  | 邮箱                    |
| `isPrimary`   | `Integer` | 是否主联系人 0否 1是    |

---

## 3. 区域管理接口

### 3.1 查询区域树

**GET** `/api/v1/store/region/tree`

**所需权限**：`store:region:list`

**说明**：查询区域树形结构，可通过 `regionType` 过滤到指定层级。

#### 请求头

```
Authorization: Bearer {accessToken}
```

#### 查询参数

| 参数         | 类型      | 必填 | 默认值 | 说明                             |
| ------------ | --------- | ---- | ------ | -------------------------------- |
| `regionType` | `Integer` | 否   | —      | 过滤到指定层级：1大区 2城市 3片区 |

#### 请求示例

```
GET /api/v1/store/region/tree
GET /api/v1/store/region/tree?regionType=2
```

#### 成功响应

**HTTP 200**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": [
    {
      "regionId": 2106...,
      "regionCode": "HD",
      "regionName": "华东大区",
      "regionType": 1,
      "parentId": 0,
      "level": 1,
      "sort": 1,
      "status": 1,
      "children": [
        {
          "regionId": 2106...,
          "regionCode": "SH",
          "regionName": "上海市",
          "regionType": 2,
          "parentId": 2106...,
          "level": 2,
          "sort": 1,
          "status": 1,
          "children": [
            {
              "regionId": 2106...,
              "regionCode": "PD",
              "regionName": "浦东片区",
              "regionType": 3,
              "parentId": 2106...,
              "level": 3,
              "sort": 1,
              "status": 1
            }
          ]
        }
      ]
    }
  ]
}
```

#### cURL 示例

```bash
curl "http://localhost/api/v1/store/region/tree" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 3.2 新增区域

**POST** `/api/v1/store/region`

**所需权限**：`store:region:add`

#### 请求头

```
Authorization: Bearer {accessToken}
Content-Type: application/json
```

#### 请求体

| 字段         | 类型      | 必填 | 说明                      |
| ------------ | --------- | ---- | ------------------------- |
| `regionCode` | `String`  | 是   | 区域编码，全局唯一        |
| `regionName` | `String`  | 是   | 区域名称                  |
| `regionType` | `Integer` | 是   | 1大区 2城市 3片区         |
| `parentId`   | `Long`    | 否   | 上级区域ID，不传默认为 0  |
| `sort`       | `Integer` | 否   | 排序，默认 0              |
| `remark`     | `String`  | 否   | 备注                      |

#### 请求体示例

```json
{
  "regionCode": "HD",
  "regionName": "华东大区",
  "regionType": 1,
  "parentId": 0,
  "sort": 1,
  "remark": "华东区域"
}
```

#### 成功响应

**HTTP 200**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": 2106985301998723074
}
```

`data` 为新增区域的 `regionId`。

#### 业务异常

| 场景           | code | message                 |
| -------------- | ---- | ----------------------- |
| 区域编码已存在 | 400  | `"区域编码已存在"`      |
| 上级区域不存在 | 400  | `"上级区域不存在"`      |

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/store/region" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "regionCode": "HD",
    "regionName": "华东大区",
    "regionType": 1,
    "parentId": 0,
    "sort": 1
  }'
```

---

### 3.3 修改区域

**PUT** `/api/v1/store/region`

**所需权限**：`store:region:edit`

> `regionCode`、`regionType`、`parentId` 不可修改。

#### 请求体

| 字段         | 类型      | 必填 | 说明         |
| ------------ | --------- | ---- | ------------ |
| `regionId`   | `Long`    | 是   | 区域ID       |
| `regionName` | `String`  | 否   | 区域名称     |
| `sort`       | `Integer` | 否   | 排序         |
| `status`     | `Integer` | 否   | 1启用 2禁用  |
| `remark`     | `String`  | 否   | 备注         |

#### 请求体示例

```json
{
  "regionId": 2106985301998723074,
  "regionName": "华东大区（改）",
  "sort": 2,
  "status": 1
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

| 场景       | code | message         |
| ---------- | ---- | --------------- |
| 区域不存在 | 400  | `"区域不存在"`  |

#### cURL 示例

```bash
curl -X PUT "http://localhost/api/v1/store/region" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "regionId": 2106985301998723074,
    "regionName": "华东大区（改）",
    "status": 1
  }'
```

---

### 3.4 删除区域

**DELETE** `/api/v1/store/region/{regionId}`

**所需权限**：`store:region:delete`

#### 路径参数

| 参数       | 类型   | 说明   |
| ---------- | ------ | ------ |
| `regionId` | `Long` | 区域ID |

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
| 存在子区域 | 400  | `"存在子区域，不能删除"` |

#### cURL 示例

```bash
curl -X DELETE "http://localhost/api/v1/store/region/2106985301998723074" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 4. 门店管理接口

### 4.1 分页查询门店

**GET** `/api/v1/store/list`

**所需权限**：`store:list`

#### 查询参数

| 参数             | 类型      | 必填 | 默认值 | 说明                        |
| ---------------- | --------- | ---- | ------ | --------------------------- |
| `pageNum`        | `Integer` | 否   | `1`    | 页码                        |
| `pageSize`       | `Integer` | 否   | `10`   | 每页数量                    |
| `keyword`        | `String`  | 否   | —      | 门店名称/编码（模糊）       |
| `regionId`       | `Long`    | 否   | —      | 大区ID                      |
| `cityId`         | `Long`    | 否   | —      | 城市ID                      |
| `areaId`         | `Long`    | 否   | —      | 片区ID                      |
| `storeType`      | `Integer` | 否   | —      | 门店类型                    |
| `businessStatus` | `Integer` | 否   | —      | 经营状态                    |

#### 请求示例

```
GET /api/v1/store/list?pageNum=1&pageSize=10&keyword=浦东
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
        "storeId": 1,
        "storeCode": "SH001",
        "storeName": "上海浦东店",
        "shortName": "浦东店",
        "storeType": 1,
        "businessStatus": 2,
        "businessStatusName": "营业",
        "regionId": 2106...,
        "regionName": "华东大区",
        "cityId": 2106...,
        "cityName": "上海市",
        "areaId": 2106...,
        "areaName": "浦东片区",
        "address": "上海市浦东新区xxx路100号",
        "longitude": 121.4737,
        "latitude": 31.2304,
        "areaSize": 2000.00,
        "openDate": "2026-01-01",
        "managerPhone": "13800000000",
        "servicePhone": "021-12345678",
        "gmtCreate": "2026-10-05T10:00:00"
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
curl "http://localhost/api/v1/store/list?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 4.2 门店详情

**GET** `/api/v1/store/{storeId}`

**所需权限**：`store:detail`

#### 路径参数

| 参数      | 类型   | 说明   |
| --------- | ------ | ------ |
| `storeId` | `Long` | 门店ID |

#### 成功响应

**HTTP 200**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "storeId": 1,
    "storeCode": "SH001",
    "storeName": "上海浦东店",
    "shortName": "浦东店",
    "storeType": 1,
    "businessStatus": 2,
    "regionId": 2106...,
    "regionName": "华东大区",
    "cityId": 2106...,
    "cityName": "上海市",
    "areaId": 2106...,
    "areaName": "浦东片区",
    "address": "上海市浦东新区xxx路100号",
    "longitude": 121.4737,
    "latitude": 31.2304,
    "areaSize": 2000.00,
    "openDate": "2026-01-01",
    "closeDate": null,
    "managerId": null,
    "managerPhone": "13800000000",
    "servicePhone": "021-12345678",
    "remark": null,
    "businessHours": [
      {
        "id": 2106...,
        "dayType": 1,
        "openTime": "08:00:00",
        "closeTime": "22:00:00",
        "is24h": 0
      },
      {
        "id": 2106...,
        "dayType": 2,
        "openTime": "09:00:00",
        "closeTime": "22:30:00",
        "is24h": 0
      }
    ],
    "contacts": [
      {
        "id": 2106...,
        "contactName": "张三",
        "contactRole": 1,
        "phone": "13800000000",
        "email": "zhangsan@example.com",
        "isPrimary": 1
      }
    ]
  }
}
```

#### 业务异常

| 场景       | code | message         |
| ---------- | ---- | --------------- |
| 门店不存在 | 400  | `"门店不存在"`  |

#### cURL 示例

```bash
curl "http://localhost/api/v1/store/1" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 4.3 新增门店

**POST** `/api/v1/store`

**所需权限**：`store:add`

#### 请求体

| 字段            | 类型         | 必填 | 说明                          |
| --------------- | ------------ | ---- | ----------------------------- |
| `storeCode`     | `String`     | 是   | 门店编码，全局唯一            |
| `storeName`     | `String`     | 是   | 门店名称                      |
| `shortName`     | `String`     | 否   | 简称                          |
| `storeType`     | `Integer`    | 否   | 1直营 2加盟 3联营，默认 1     |
| `regionId`      | `Long`       | 否   | 大区ID                        |
| `cityId`        | `Long`       | 否   | 城市ID                        |
| `areaId`        | `Long`       | 否   | 片区ID                        |
| `parentStoreId` | `Long`       | 否   | 上级门店ID                    |
| `address`       | `String`     | 否   | 详细地址                      |
| `longitude`     | `BigDecimal` | 否   | 经度                          |
| `latitude`      | `BigDecimal` | 否   | 纬度                          |
| `areaSize`      | `BigDecimal` | 否   | 营业面积（㎡）                |
| `openDate`      | `LocalDate`  | 否   | 开业日期                      |
| `managerId`     | `Long`       | 否   | 店长ID                        |
| `managerPhone`  | `String`     | 否   | 店长电话                      |
| `servicePhone`  | `String`     | 否   | 客服电话                      |
| `remark`        | `String`     | 否   | 备注                          |

#### 请求体示例

```json
{
  "storeCode": "SH001",
  "storeName": "上海浦东店",
  "shortName": "浦东店",
  "storeType": 1,
  "regionId": 2106985301998723074,
  "cityId": 2106985301998723075,
  "areaId": 2106985301998723076,
  "address": "上海市浦东新区xxx路100号",
  "longitude": 121.4737,
  "latitude": 31.2304,
  "areaSize": 2000.00,
  "openDate": "2026-01-01",
  "managerPhone": "13800000000",
  "servicePhone": "021-12345678"
}
```

#### 成功响应

**HTTP 200**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": 2106985301998723077
}
```

`data` 为新增门店的 `storeId`。

#### 业务异常

| 场景           | code | message               |
| -------------- | ---- | --------------------- |
| 门店编码已存在 | 400  | `"门店编码已存在"`    |

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/store" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "storeCode": "SH001",
    "storeName": "上海浦东店",
    "storeType": 1,
    "address": "上海市浦东新区xxx路100号",
    "openDate": "2026-01-01"
  }'
```

---

### 4.4 修改门店

**PUT** `/api/v1/store`

**所需权限**：`store:edit`

> `storeCode` 不可修改。

#### 请求体

| 字段             | 类型         | 必填 | 说明             |
| ---------------- | ------------ | ---- | ---------------- |
| `storeId`        | `Long`       | 是   | 门店ID           |
| `storeName`      | `String`     | 否   | 门店名称         |
| `shortName`      | `String`     | 否   | 简称             |
| `storeType`      | `Integer`    | 否   | 门店类型         |
| `businessStatus` | `Integer`    | 否   | 经营状态         |
| `regionId`       | `Long`       | 否   | 大区ID           |
| `cityId`         | `Long`       | 否   | 城市ID           |
| `areaId`         | `Long`       | 否   | 片区ID           |
| `address`        | `String`     | 否   | 详细地址         |
| `longitude`      | `BigDecimal` | 否   | 经度             |
| `latitude`       | `BigDecimal` | 否   | 纬度             |
| `areaSize`       | `BigDecimal` | 否   | 营业面积         |
| `managerId`      | `Long`       | 否   | 店长ID           |
| `managerPhone`   | `String`     | 否   | 店长电话         |
| `servicePhone`   | `String`     | 否   | 客服电话         |
| `remark`         | `String`     | 否   | 备注             |

#### 请求体示例

```json
{
  "storeId": 1,
  "storeName": "上海浦东店（改）",
  "businessStatus": 2,
  "servicePhone": "021-87654321"
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

#### cURL 示例

```bash
curl -X PUT "http://localhost/api/v1/store" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "storeId": 1,
    "storeName": "上海浦东店（改）",
    "businessStatus": 2
  }'
```

---

### 4.5 删除门店

**DELETE** `/api/v1/store/{storeId}`

**所需权限**：`store:delete`

#### 路径参数

| 参数      | 类型   | 说明   |
| --------- | ------ | ------ |
| `storeId` | `Long` | 门店ID |

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": null
}
```

> 逻辑删除，`is_deleted` 置为 1。

#### cURL 示例

```bash
curl -X DELETE "http://localhost/api/v1/store/1" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 4.6 修改门店经营状态

**PUT** `/api/v1/store/{storeId}/status`

**所需权限**：`store:edit`

#### 路径参数

| 参数      | 类型   | 说明   |
| --------- | ------ | ------ |
| `storeId` | `Long` | 门店ID |

#### 查询参数

| 参数             | 类型      | 必填 | 说明                            |
| ---------------- | --------- | ---- | ------------------------------- |
| `businessStatus` | `Integer` | 是   | 1筹备 2营业 3暂停 4闭店         |

#### 请求示例

```
PUT /api/v1/store/1/status?businessStatus=3
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
curl -X PUT "http://localhost/api/v1/store/1/status?businessStatus=3" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 4.7 更新门店详情（营业时间 + 联系人）

**PUT** `/api/v1/store/detail`

**所需权限**：`store:edit`

**说明**：整体替换门店的营业时间和联系人列表（先删后插）。

#### 请求体

| 字段            | 类型                    | 必填 | 说明     |
| --------------- | ----------------------- | ---- | -------- |
| `storeId`       | `Long`                  | 是   | 门店ID   |
| `businessHours` | `Array<BusinessHours>`  | 否   | 营业时间 |
| `contacts`      | `Array<Contact>`        | 否   | 联系人   |

**BusinessHours 结构：**

| 字段        | 类型        | 必填 | 说明                    |
| ----------- | ----------- | ---- | ----------------------- |
| `dayType`   | `Integer`   | 是   | 1工作日 2周末 3节假日   |
| `openTime`  | `LocalTime` | 是   | 开始营业时间            |
| `closeTime` | `LocalTime` | 是   | 结束营业时间            |
| `is24h`     | `Integer`   | 否   | 是否24小时，默认 0      |

**Contact 结构：**

| 字段          | 类型      | 必填 | 说明                    |
| ------------- | --------- | ---- | ----------------------- |
| `contactName` | `String`  | 是   | 姓名                    |
| `contactRole` | `Integer` | 否   | 1店长 2财务 3收货 4客服 |
| `phone`       | `String`  | 否   | 电话                    |
| `email`       | `String`  | 否   | 邮箱                    |
| `isPrimary`   | `Integer` | 否   | 是否主联系人            |

#### 请求体示例

```json
{
  "storeId": 1,
  "businessHours": [
    {
      "dayType": 1,
      "openTime": "08:00:00",
      "closeTime": "22:00:00",
      "is24h": 0
    },
    {
      "dayType": 2,
      "openTime": "09:00:00",
      "closeTime": "22:30:00",
      "is24h": 0
    }
  ],
  "contacts": [
    {
      "contactName": "张三",
      "contactRole": 1,
      "phone": "13800000000",
      "email": "zhangsan@example.com",
      "isPrimary": 1
    },
    {
      "contactName": "李四",
      "contactRole": 4,
      "phone": "13900000000",
      "isPrimary": 0
    }
  ]
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

#### cURL 示例

```bash
curl -X PUT "http://localhost/api/v1/store/detail" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "storeId": 1,
    "businessHours": [
      {"dayType": 1, "openTime": "08:00:00", "closeTime": "22:00:00"}
    ],
    "contacts": [
      {"contactName": "张三", "contactRole": 1, "phone": "13800000000", "isPrimary": 1}
    ]
  }'
```

---

## 5. 内部接口（服务间调用）

### 5.1 查询门店基础信息

**GET** `/api/v1/store/internal/{storeId}`

**调用方**：`service-order`、`service-stock` 等

**不加 `@PreAuthorize`，网关不路由 `/internal/**`。**

#### 路径参数

| 参数      | 类型   | 说明   |
| --------- | ------ | ------ |
| `storeId` | `Long` | 门店ID |

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "storeId": 1,
    "storeCode": "SH001",
    "storeName": "上海浦东店",
    "shortName": "浦东店",
    "storeType": 1,
    "businessStatus": 2,
    "businessStatusName": "营业",
    "regionId": 2106...,
    "regionName": "华东大区",
    "cityId": 2106...,
    "cityName": "上海市",
    "areaId": 2106...,
    "areaName": "浦东片区",
    "address": "上海市浦东新区xxx路100号",
    "longitude": 121.4737,
    "latitude": 31.2304,
    "areaSize": 2000.00,
    "openDate": "2026-01-01",
    "managerPhone": "13800000000",
    "servicePhone": "021-12345678"
  }
}
```

#### cURL 示例

```bash
curl "http://localhost/api/v1/store/internal/1"
```

---

### 5.2 批量查询门店

**POST** `/api/v1/store/internal/list-by-ids`

**不加 `@PreAuthorize`。**

#### 请求体

```json
[1, 2, 3]
```

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": [
    {
      "storeId": 1,
      "storeCode": "SH001",
      "storeName": "上海浦东店",
      "businessStatus": 2
    },
    {
      "storeId": 2,
      "storeCode": "SH002",
      "storeName": "上海徐汇店",
      "businessStatus": 2
    }
  ]
}
```

#### cURL 示例

```bash
curl -X POST "http://localhost/api/v1/store/internal/list-by-ids" \
  -H "Content-Type: application/json" \
  -d '[1, 2, 3]'
```

---

### 5.3 校验门店是否可营业

**GET** `/api/v1/store/internal/{storeId}/can-trade`

**调用方**：`service-order`（下单时校验）

**不加 `@PreAuthorize`。**

#### 路径参数

| 参数      | 类型   | 说明   |
| --------- | ------ | ------ |
| `storeId` | `Long` | 门店ID |

#### 成功响应

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": true
}
```

`data = true` 表示营业中（`businessStatus = 2`），可以交易；`false` 表示不能交易。

#### cURL 示例

```bash
curl "http://localhost/api/v1/store/internal/1/can-trade"
```

---

## 6. Feign 调用关系

```
service-order
    │
    │ 下单时调用
    ↓
service-store  /api/v1/store/internal/{storeId}
               /api/v1/store/internal/{storeId}/can-trade

service-stock
    │
    │ 查询库存展示门店信息时调用
    ↓
service-store  /api/v1/store/internal/{storeId}
```

---

## 7. 权限编码

| 编码                      | 名称     |
| ------------------------- | -------- |
| `store:list`              | 门店列表 |
| `store:detail`            | 门店详情 |
| `store:add`               | 新增门店 |
| `store:edit`              | 修改门店 |
| `store:delete`            | 删除门店 |
| `store:region:list`       | 区域列表 |
| `store:region:add`        | 新增区域 |
| `store:region:edit`       | 修改区域 |
| `store:region:delete`     | 删除区域 |

---

## 8. cURL 完整测试流程

```bash
# 0. 登录拿 token
curl -X POST http://localhost/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123","userType":2}'

TOKEN=eyJhbGciOiJIUzI1NiJ9...

# 1. 建大区
curl -X POST http://localhost/api/v1/store/region \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"regionCode":"HD","regionName":"华东大区","regionType":1,"parentId":0,"sort":1}'

# 2. 建城市（parentId 用上一步返回的 regionId）
curl -X POST http://localhost/api/v1/store/region \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"regionCode":"SH","regionName":"上海市","regionType":2,"parentId":REGION_ID,"sort":1}'

# 3. 建片区
curl -X POST http://localhost/api/v1/store/region \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"regionCode":"PD","regionName":"浦东片区","regionType":3,"parentId":CITY_ID,"sort":1}'

# 4. 查区域树
curl "http://localhost/api/v1/store/region/tree" \
  -H "Authorization: Bearer $TOKEN"

# 5. 建门店
curl -X POST http://localhost/api/v1/store \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"storeCode":"SH001","storeName":"上海浦东店","storeType":1,"regionId":REGION_ID,"cityId":CITY_ID,"areaId":AREA_ID,"address":"上海市浦东新区xxx路100号"}'

# 6. 查门店列表
curl "http://localhost/api/v1/store/list?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN"

# 7. 门店详情
curl "http://localhost/api/v1/store/STORE_ID" \
  -H "Authorization: Bearer $TOKEN"

# 8. 更新营业时间和联系人
curl -X PUT http://localhost/api/v1/store/detail \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"storeId":STORE_ID,"businessHours":[{"dayType":1,"openTime":"08:00:00","closeTime":"22:00:00"}],"contacts":[{"contactName":"张三","contactRole":1,"phone":"13800000000","isPrimary":1}]}'

# 9. 内部接口：查门店
curl "http://localhost/api/v1/store/internal/STORE_ID"

# 10. 内部接口：校验是否营业
curl "http://localhost/api/v1/store/internal/STORE_ID/can-trade"

# 11. 改经营状态为暂停
curl -X PUT "http://localhost/api/v1/store/STORE_ID/status?businessStatus=3" \
  -H "Authorization: Bearer $TOKEN"
```

