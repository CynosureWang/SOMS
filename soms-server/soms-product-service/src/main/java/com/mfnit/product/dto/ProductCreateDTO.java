package com.mfnit.product.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:05
 * @Description SOMS 商品服务商品创建DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class ProductCreateDTO {
    @NotBlank(message = "商品编码不能为空")
    private String productCode;
    private String spuCode;
    @NotBlank(message = "商品名称不能为空")
    private String productName;
    private String shortName;
    private String pinyin;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    private Long brandId;

    private String specJson;
    private String specText;

    private String barcode;
    private String scaleCode;

    private String unit = "个";
    private Integer isWeight = 0;

    @NotNull(message = "售价不能为空")
    @DecimalMin(value = "0.00", message = "售价不能为负")
    private BigDecimal price;
    private BigDecimal costPrice;

    private Integer stockWarn;
    private Integer shelfLifeDays;

    private String mainImage;
    private String description;
    private Integer allowDiscount = 1;
    private Integer stockMode = 1;
}