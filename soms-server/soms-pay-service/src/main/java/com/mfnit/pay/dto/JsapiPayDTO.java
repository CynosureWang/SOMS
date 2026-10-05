package com.mfnit.pay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:51
 * @Description SOMS JSAPI支付DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class JsapiPayDTO {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    private String orderNo;
    private Long storeId;
    private Integer payType;
    @NotNull(message = "金额不能为空")
    private BigDecimal payAmount;
    @NotBlank(message = "openid不能为空")
    private String openid;
}
