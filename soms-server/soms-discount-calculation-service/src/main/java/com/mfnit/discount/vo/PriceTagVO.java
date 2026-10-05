package com.mfnit.discount.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:29
 * @Description SOMS 价签 VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class PriceTagVO {
    private Long id;
    private String barcode;
    private Integer barcodeType;
    private String sourceBarcode;
    private Long productId;
    private String productName;
    private BigDecimal originalPrice;
    private BigDecimal promotionPrice;
    private BigDecimal weight;
    private BigDecimal amount;
    private Integer status;
    private String statusName;
    private Long ruleId;
    private String ruleName;
}
