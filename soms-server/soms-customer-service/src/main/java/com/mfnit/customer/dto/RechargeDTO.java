package com.mfnit.customer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/22
 * @Description 会员余额充值请求对象
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class RechargeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 充值金额（元）
     */
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额必须大于0")
    private BigDecimal amount;

    /**
     * 业务单号（可选，支付回调幂等键；不传则每次直接充值）
     */
    private String bizId;

    /**
     * 备注
     */
    private String remark;
}
