package com.mfnit.discount.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:29
 * @Description SOMS 折扣结果 VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class ItemDiscountVO {
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
