package com.mfnit.order.client.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:13
 * @Description SOMS 订单服务商品DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class ProductDTO {
    private Long productId;
    private Long brandId;
    private String productCode;
    private String productName;
    private String specText;
    private String unit;
    private String mainImage;
    private Long categoryId;
    private BigDecimal price;
    private Integer stockMode;
    private Integer isWeight;
    private Integer status;
    private Integer stockWarn;
}