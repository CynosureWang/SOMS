package com.mfnit.pay.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:53
 * @Description SOMS 退款VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class RefundVO {
    private Long refundId;
    private String refundNo;
    private String payNo;
    private String orderNo;
    private BigDecimal refundAmount;
    private Integer status;
    private String statusName;
    private LocalDateTime refundTime;
    private LocalDateTime gmtCreate;
}