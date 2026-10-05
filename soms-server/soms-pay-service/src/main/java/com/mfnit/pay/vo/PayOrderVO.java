package com.mfnit.pay.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:52
 * @Description SOMS 付款订单VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class PayOrderVO {
    private Long payId;
    private String payNo;
    private Long orderId;
    private String orderNo;
    private Long storeId;
    private Long customerId;
    private Integer payType;
    private String payTypeName;
    private Integer payScene;
    private BigDecimal payAmount;
    private BigDecimal paidAmount;
    private Integer status;
    private String statusName;
    private String channelOrderNo;
    private LocalDateTime payTime;
    private LocalDateTime gmtCreate;
}
