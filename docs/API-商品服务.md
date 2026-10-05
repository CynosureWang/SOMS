# SOMS 商品服务接口文档

> **服务名称**：`service-product`
> **服务端口**：`8090`
> **基础路径**：`http://127.0.0.1:8090`
> **网关路径**：`http://localhost/api/v1/product`
> **版本**：`1.1.1`
> **首次版本日期**：2026-10-03

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

### 1.2 状态码说明

| 状态码 | 说明     |
| ------ | -------- |
| `0`    | 成功     |
| `400`  | 业务异常 |
| `401`  | 未登录   |
| `403`  | 无权限   |
| `500`  | 系统异常 |

### 1.3 请求头

除内部接口外，所有接口需携带：

```
Authorization: Bearer {accessToken}
```

### 1.4 数据字典

**商品状态**

| 值   | 含义 |
| ---- | ---- |
| 1    | 上架 |
| 2    | 下架 |

**库存模式**

| 值   | 含义                  |
| ---- | --------------------- |
| 1    | 严格（库存0拒绝销售） |
| 2    | 宽松（允许负库存）    |

**条码类型**

| 值   | 含义     |
| ---- | -------- |
| 1    | 国标条码 |
| 2    | 店内码   |
| 3    | 秤码     |
| 4    | 促销价签 |

**价签状态**

| 值   | 含义 |
| ---- | ---- |
| 0    | 未售 |
| 1    | 已售 |
| 2    | 作废 |
| 3    | 过期 |

---

## 2. 数据模型

### 2.1 ProductCategory（分类）

| 字段         | 类型    | 说明              |
| ------------ | ------- | ----------------- |
| categoryId   | Long    | 分类ID            |
| categoryCode | String  | 分类编码          |
| categoryName | String  | 分类名称          |
| parentId     | Long    | 上级分类ID，0为根 |
| level        | Integer | 层级：1/2/3       |
| path         | String  | 路径 /1/10/100/   |
| icon         | String  | 图标              |
| sort         | Integer | 排序              |
| status       | Integer | 1启用 2禁用       |

### 2.2 ProductBrand（品牌）

| 字段        | 类型    | 说明        |
| ----------- | ------- | ----------- |
| brandId     | Long    | 品牌ID      |
| brandCode   | String  | 品牌编码    |
| brandName   | String  | 品牌名称    |
| brandLogo   | String  | LOGO        |
| firstLetter | String  | 首字母      |
| sort        | Integer | 排序        |
| status      | Integer | 1启用 2禁用 |

### 2.3 Product（商品）

| 字段          | 类型       | 说明                |
| ------------- | ---------- | ------------------- |
| productId     | Long       | 商品ID（雪花）      |
| productCode   | String     | 商品编码            |
| spuCode       | String     | SPU编码（多规格用） |
| productName   | String     | 商品名称            |
| shortName     | String     | 简称（POS显示）     |
| pinyin        | String     | 拼音（检索用）      |
| categoryId    | Long       | 分类ID              |
| brandId       | Long       | 品牌ID              |
| specJson      | String     | 规格JSON            |
| specText      | String     | 规格文本            |
| barcode       | String     | 主条码              |
| scaleCode     | String     | 秤码PLU（称重商品） |
| unit          | String     | 单位                |
| isWeight      | Integer    | 是否称重 0/1        |
| price         | BigDecimal | 售价                |
| costPrice     | BigDecimal | 成本价              |
| stockWarn     | Integer    | 库存预警值          |
| shelfLifeDays | Integer    | 保质期天数          |
| mainImage     | String     | 主图                |
| description   | String     | 描述                |
| status        | Integer    | 1上架 2下架         |
| allowDiscount | Integer    | 是否允许促销 0/1    |
| stockMode     | Integer    | 1严格 2宽松         |

### 2.4 ScaleLabel（称重价签）

| 字段        | 类型       | 说明                    |
| ----------- | ---------- | ----------------------- |
| labelId     | Long       | 价签ID                  |
| barcode     | String     | 价签条码（25开头13位）  |
| productId   | Long       | 商品ID                  |
| productName | String     | 商品名快照              |
| specText    | String     | 规格快照                |
| price       | BigDecimal | 单价快照                |
| weight      | BigDecimal | 重量（kg）              |
| amount      | BigDecimal | 金额                    |
| storeId     | Long       | 门店ID                  |
| scaleCode   | String     | 秤编号                  |
| operatorId  | Long       | 操作员ID                |
| status      | Integer    | 0未售 1已售 2作废 3过期 |
| orderId     | Long       | 关联订单ID              |
| orderNo     | String     | 关联订单号              |
| gmtExpire   | DateTime   | 过期时间                |

---

## 3. 分类接口

### 3.1 查询分类树

**GET** `/api/v1/product/category/tree`

**所需权限**：`product:category:list`

**响应**：

```json
{
  "code": 0,
  "data": [
    {
      "categoryId": 1,
      "categoryCode": "FOOD",
      "categoryName": "食品饮料",
      "parentId": 0,
      "level": 1,
      "icon": "food",
      "sort": 1,
      "children": [
        {
          "categoryId": 10,
          "categoryCode": "FOOD_SNACK",
          "categoryName": "休闲零食",
          "parentId": 1,
          "level": 2,
          "children": []
        }
      ]
    }
  ]
}
```

### 3.2 新增分类

**POST** `/api/v1/product/category`

**所需权限**：`product:category:add`

**请求体**：

```json
{
  "categoryCode": "FOOD_DRINK",
  "categoryName": "饮料冲调",
  "parentId": 1,
  "icon": "drink",
  "sort": 2
}
```

### 3.3 修改分类

**PUT** `/api/v1/product/category`

**所需权限**：`product:category:edit`

```json
{
  "categoryId": 11,
  "categoryName": "饮料",
  "sort": 2,
  "status": 1
}
```

### 3.4 删除分类

**DELETE** `/api/v1/product/category/{categoryId}`

**所需权限**：`product:category:delete`

> 有子分类时不能删除。

---

## 4. 品牌接口

### 4.1 分页查询品牌

**GET** `/api/v1/product/brand/list`

**所需权限**：`product:brand:list`

**参数**：`pageNum`、`pageSize`、`keyword`

### 4.2 新增品牌

**POST** `/api/v1/product/brand`

**所需权限**：`product:brand:add`

```json
{
  "brandCode": "COCACOLA",
  "brandName": "可口可乐",
  "firstLetter": "K",
  "sort": 1
}
```

### 4.3 修改品牌

**PUT** `/api/v1/product/brand`

**所需权限**：`product:brand:edit`

### 4.4 删除品牌

**DELETE** `/api/v1/product/brand/{brandId}`

**所需权限**：`product:brand:delete`

---

## 5. 商品接口

### 5.1 分页查询商品

**GET** `/api/v1/product/list`

**所需权限**：`product:list`

**参数**：

| 参数       | 类型    | 说明                |
| ---------- | ------- | ------------------- |
| pageNum    | Integer | 页码                |
| pageSize   | Integer | 每页数量            |
| keyword    | String  | 名称/编码/条码/拼音 |
| categoryId | Long    | 分类ID              |
| brandId    | Long    | 品牌ID              |
| status     | Integer | 状态                |
| isWeight   | Integer | 是否称重            |

### 5.2 商品详情

**GET** `/api/v1/product/{productId}`

**所需权限**：`product:detail`

### 5.3 新增商品

**POST** `/api/v1/product`

**所需权限**：`product:add`

**请求体**：

```json
{
  "productCode": "P0001",
  "productName": "可口可乐500ml",
  "categoryId": 100,
  "brandId": 1,
  "barcode": "6901234567890",
  "unit": "瓶",
  "isWeight": 0,
  "price": 3.50,
  "costPrice": 2.50,
  "stockWarn": 50,
  "stockMode": 1,
  "allowDiscount": 1
}
```

### 5.4 修改商品

**PUT** `/api/v1/product`

**所需权限**：`product:edit`

### 5.5 删除商品

**DELETE** `/api/v1/product/{productId}`

**所需权限**：`product:delete`

### 5.6 上下架

**PUT** `/api/v1/product/{productId}/status?status=1`

**所需权限**：`product:edit`

---

## 6. 条码查询接口（收银用）

### 6.1 按条码查询

**GET** `/api/v1/product/barcode/{barcode}`

**无需权限，要求登录**

### 6.2 按秤码查询

**GET** `/api/v1/product/scale/{scaleCode}`

**无需权限，要求登录**

### 6.3 批量拉本店称重商品

**GET** `/api/v1/product/scale/list?storeId=1`

**无需权限，要求登录**

> 用于收银机本地缓存。

---

## 7. 称重价签接口

### 7.1 生成价签（秤端调用）

**POST** `/api/v1/product/scale/label/generate`

**请求体**：

```json
{
  "scaleCode": "00001",
  "weight": 1.234,
  "storeId": 1,
  "operatorId": null
}
```

**响应**：

```json
{
  "code": 0,
  "data": {
    "labelId": 2106...,
    "barcode": "2500000000001X",
    "productId": 1001,
    "productName": "散装苹果",
    "price": 7.90,
    "weight": 1.234,
    "amount": 9.75,
    "unit": "kg",
    "status": 0
  }
}
```

### 7.2 按条码查价签（收银调用）

**GET** `/api/v1/product/scale/label/{barcode}`

### 7.3 标记已售（结算时调用）

**POST** `/api/v1/product/scale/label/mark-sold`

**请求体**：

```json
{
  "barcodes": ["2500000000001X"],
  "orderId": 123456,
  "orderNo": "SO202610030001000001X"
}
```

---

## 8. 内部接口（服务间调用）

### 8.1 查询商品（stock/order 调）

**GET** `/api/v1/product/internal/{productId}`

**不加 `@PreAuthorize`，网关不路由 `/internal/**`。**

**响应**：

```json
{
  "code": 0,
  "data": {
    "productId": 1001,
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

## 9. 权限编码

| 编码                      | 名称     |
| ------------------------- | -------- |
| `product:list`            | 商品列表 |
| `product:detail`          | 商品详情 |
| `product:add`             | 新增商品 |
| `product:edit`            | 修改商品 |
| `product:delete`          | 删除商品 |
| `product:category:list`   | 分类列表 |
| `product:category:add`    | 新增分类 |
| `product:category:edit`   | 修改分类 |
| `product:category:delete` | 删除分类 |
| `product:brand:list`      | 品牌列表 |
| `product:brand:add`       | 新增品牌 |
| `product:brand:edit`      | 修改品牌 |
| `product:brand:delete`    | 删除品牌 |