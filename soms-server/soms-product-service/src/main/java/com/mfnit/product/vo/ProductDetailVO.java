package com.mfnit.product.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:09
 * @Description SOMS 产品详情VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class ProductDetailVO {
    private Long productId;
    private String productCode;
    private String spuCode;
    private String productName;
    private String shortName;
    private String pinyin;
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
    private String specJson;
    private String specText;
    private String barcode;
    private String scaleCode;
    private String unit;
    private Integer isWeight;
    private BigDecimal price;
    private BigDecimal costPrice;
    private Integer stockWarn;
    private Integer shelfLifeDays;
    private String mainImage;
    private String description;
    private Integer status;
    private Integer allowDiscount;
    private Integer stockMode;
    private List<String> images;
}