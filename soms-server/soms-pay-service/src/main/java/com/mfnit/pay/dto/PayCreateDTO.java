package com.mfnit.pay.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:50
 * @Description SOMS 支付创建DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class PayCreateDTO {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
    @NotNull(message = "门店ID不能为空")
    private Long storeId;
    private Long customerId;
    @NotNull(message = "支付方式不能为空")
    private Integer payType;
    @NotNull(message = "支付场景不能为空")
    private Integer payScene;
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal payAmount;
    private String attach;
}