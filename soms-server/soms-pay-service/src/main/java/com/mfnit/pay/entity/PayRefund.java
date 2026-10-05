package com.mfnit.pay.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:42
 * @Description SOMS PayRefund 支付退款实体类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("pay_refund")
public class PayRefund implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "refund_id", type = IdType.ASSIGN_ID)
    private Long refundId;

    private String refundNo;
    private Long payId;
    private String payNo;
    private Long orderId;
    private String orderNo;

    private BigDecimal refundAmount;
    private String reason;

    private Integer status;
    private String channelRefundNo;
    private LocalDateTime refundTime;
    private String failReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;

    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}