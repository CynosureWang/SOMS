package com.mfnit.discount.engine;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:31
 * @Description SOMS 优惠券计算引擎
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class InternalCartItem {
    private Long productId;
    private String barcode;
    private Integer barcodeType;
    private String productName;
    private Long categoryId;
    private Long brandId;
    private BigDecimal quantity;
    private BigDecimal price;
    /** 是否自打价签商品，直接按 price 结算，不打折 */
    private boolean priceTagItem;
    /** 命中的促销 */
    private Long promotionId;
    private String promotionName;
    /** 折后单价 */
    private BigDecimal discountedPrice;
    /** 小计 */
    private BigDecimal totalAmount;
    /** 优惠金额 */
    private BigDecimal discountAmount;
    /** 实付 */
    private BigDecimal payAmount;
}