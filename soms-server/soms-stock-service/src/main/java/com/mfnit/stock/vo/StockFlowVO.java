package com.mfnit.stock.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 12:37
 * @Description SOMS 库存流水VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class StockFlowVO {
    private Long flowId;
    private Long storeId;
    private Long productId;
    private String productName;
    private Integer flowType;
    private String flowTypeName;
    private BigDecimal changeQuantity;
    private BigDecimal changeLocked;
    private BigDecimal beforeQuantity;
    private BigDecimal afterQuantity;
    private BigDecimal beforeLocked;
    private BigDecimal afterLocked;
    private String bizNo;
    private Long operatorId;
    private String remark;
    private LocalDateTime gmtCreate;
}
