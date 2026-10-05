package com.mfnit.pay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:50
 * @Description SOMS 付款码支付DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class BarcodePayDTO {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
    @NotNull(message = "门店ID不能为空")
    private Long storeId;
    private Long customerId;
    @NotNull(message = "支付方式不能为空")
    private Integer payType;      // 2微信 3支付宝
    @NotBlank(message = "付款码不能为空")
    private String authCode;      // 顾客付款码
    @NotNull(message = "金额不能为空")
    private BigDecimal payAmount;
}
