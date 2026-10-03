package com.mfnit.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:06
 * @Description SOMS 商品更新DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class ProductUpdateDTO {
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    private String productName;
    private String shortName;
    private String pinyin;
    private Long categoryId;
    private Long brandId;
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
}
