package com.mfnit.common.api.dto.customer;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
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
 * @Description SOMS Customer服务消费完成回调数据传输对象（内部契约，幂等）
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class ConsumeCompleteDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会员ID
     */
    @NotNull(message = "会员ID不能为空")
    private Long customerId;

    /**
     * 消费金额（元，用于累计消费与积分计算）
     */
    @NotNull(message = "消费金额不能为空")
    @DecimalMin(value = "0.00", message = "消费金额不能为负数")
    private BigDecimal consumeAmount;

    /**
     * 业务类型，如 ORDER
     */
    @NotBlank(message = "业务类型不能为空")
    private String bizType;

    /**
     * 业务单号（幂等键）
     */
    @NotBlank(message = "业务单号不能为空")
    private String bizId;
}
