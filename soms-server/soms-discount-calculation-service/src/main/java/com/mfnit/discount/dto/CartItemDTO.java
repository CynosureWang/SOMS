package com.mfnit.discount.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 15:34
 * @Description SOMS 购物车商品DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class CartItemDTO {
    private Long productId;
    private String productName;
    private String barcode;
    private Integer barcodeType;
    private Long categoryId;
    private Long brandId;
    private BigDecimal quantity;
    private BigDecimal price;
}