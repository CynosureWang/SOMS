package com.mfnit.order.client.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 21:43
 * @Description SOMS 订单服务 - 商品优惠信息数据传输对象
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class ItemDiscountDTO {
    private Long productId;
    private String barcode;
    private Integer barcodeType;
    private String productName;
    private BigDecimal originalPrice;
    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;
    private Long promotionId;
    private String promotionName;
}