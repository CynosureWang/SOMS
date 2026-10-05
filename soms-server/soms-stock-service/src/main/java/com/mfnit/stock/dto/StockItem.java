package com.mfnit.stock.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 12:34
 * @Description SOMS 库存DTO 锁定/扣减/解锁
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class StockItem {
    private Long productId;
    private String productName;
    private BigDecimal quantity;
}
