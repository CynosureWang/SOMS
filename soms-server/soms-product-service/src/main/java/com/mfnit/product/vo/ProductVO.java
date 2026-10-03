package com.mfnit.product.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:08
 * @Description SOMS 产品VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class ProductVO {
    private Long productId;
    private String productCode;
    private String spuCode;
    private String productName;
    private String shortName;
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
    private String specText;
    private String barcode;
    private String scaleCode;
    private String unit;
    private Integer isWeight;
    private BigDecimal price;
    private BigDecimal costPrice;
    private String mainImage;
    private Integer status;
    private Integer allowDiscount;
    private Integer stockMode;
    private LocalDateTime gmtCreate;
}
