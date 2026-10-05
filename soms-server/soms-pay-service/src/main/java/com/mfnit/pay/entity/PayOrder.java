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
 * @CreateTime 2026/10/4 13:41
 * @Description SOMS PayOrder 实体类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("pay_order")
public class PayOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "pay_id", type = IdType.ASSIGN_ID)
    private Long payId;

    private String payNo;
    private Long orderId;
    private String orderNo;
    private Long storeId;
    private Long customerId;

    private Integer payType;
    private Integer payScene;
    private BigDecimal payAmount;
    private BigDecimal paidAmount;

    private Integer status;
    private String channelOrderNo;
    private String channelUserId;
    private String channelTradeNo;

    private LocalDateTime payTime;
    private LocalDateTime expireTime;
    private LocalDateTime closeTime;
    private String failReason;

    private String attach;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;

    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}
