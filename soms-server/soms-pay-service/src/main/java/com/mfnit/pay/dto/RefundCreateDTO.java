package com.mfnit.pay.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:52
 * @Description SOMS 退款创建DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class RefundCreateDTO {
    @NotNull(message = "支付单ID不能为空")
    private Long payId;
    @NotNull(message = "退款金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal refundAmount;
    private String reason;
}
