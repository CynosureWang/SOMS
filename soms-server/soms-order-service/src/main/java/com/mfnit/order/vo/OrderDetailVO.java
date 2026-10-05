package com.mfnit.order.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:11
 * @Description SOMS 订单详情VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class OrderDetailVO {
    private Long orderId;
    private String orderNo;
    private Long storeId;
    private Long customerId;
    private Integer orderType;
    private String orderTypeName;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;
    private BigDecimal paidAmount;
    private Integer itemCount;
    private Integer status;
    private String statusName;
    private Integer payStatus;
    private Integer payType;
    private LocalDateTime payTime;
    private LocalDateTime finishTime;
    private LocalDateTime cancelTime;
    private String cancelReason;
    private String remark;
    private LocalDateTime gmtCreate;
    private List<OrderItemVO> items;
}
