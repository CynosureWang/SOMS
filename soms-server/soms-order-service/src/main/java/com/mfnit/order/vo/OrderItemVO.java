package com.mfnit.order.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:11
 * @Description SOMS 订单项VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class OrderItemVO {
    private Long itemId;
    private Long productId;
    private String productName;
    private String specText;
    private String unit;
    private String mainImage;
    private Integer isWeight;
    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;
    private BigDecimal refundQuantity;
    private BigDecimal refundAmount;
}
