package com.mfnit.product.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:09
 * @Description SOMS 产品简单信息VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class ProductSimpleVO {
    private Long productId;
    private String productCode;
    private String productName;
    private String specText;
    private String barcode;
    private String scaleCode;
    private String unit;
    private Integer isWeight;
    private BigDecimal price;
    private String mainImage;
}
