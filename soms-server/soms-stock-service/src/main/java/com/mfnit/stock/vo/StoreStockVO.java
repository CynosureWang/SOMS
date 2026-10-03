package com.mfnit.stock.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 12:36
 * @Description SOMS 库存vo 库存信息
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class StoreStockVO {
    private Long id;
    private Long storeId;
    private Long productId;
    private String productCode;
    private String productName;
    private BigDecimal quantity;
    private BigDecimal lockedQuantity;
    private BigDecimal availableQuantity;
    private BigDecimal costPrice;
    private Integer stockWarn;
    private Integer warnStatus;   // 1正常 2预警 3缺货
    private LocalDateTime gmtModified;
}
