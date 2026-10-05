package com.mfnit.pay.client.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:54
 * @Description SOMS 订单支付成功DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class OrderPaySuccessDTO {
    private String orderNo;
    private Integer payType;
    private BigDecimal paidAmount;
}
