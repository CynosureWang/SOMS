package com.mfnit.stock.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 12:34
 * @Description SOMS 出库DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class StockOutDTO {
    @NotNull(message = "门店ID不能为空")
    private Long storeId;
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    @NotNull(message = "出库数量不能为空")
    @DecimalMin(value = "0.001", message = "数量必须大于0")
    private BigDecimal quantity;
    private Integer bizType;   // 5退货 8报损 6手工
    private Long bizId;
    private String bizNo;
    private Long operatorId;
    private String remark;
}
