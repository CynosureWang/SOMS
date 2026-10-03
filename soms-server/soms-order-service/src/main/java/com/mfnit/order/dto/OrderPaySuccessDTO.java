package com.mfnit.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:10
 * @Description SOMS 内部订单回调DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class OrderPaySuccessDTO {
    @NotNull(message = "订单号不能为空")
    private String orderNo;
    @NotNull(message = "支付类型不能为空")
    private Integer payType;
    @NotNull(message = "实付金额不能为空")
    private BigDecimal paidAmount;
}
