package com.mfnit.discount.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 15:15
 * @Description SOMS 优惠券实体类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("coupon")
public class Coupon implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "coupon_id", type = IdType.ASSIGN_ID)
    private Long couponId;

    private String couponNo;
    private Long templateId;
    private Long customerId;

    private Integer status;
    private LocalDateTime validStart;
    private LocalDateTime validEnd;

    private Long orderId;
    private String orderNo;
    private LocalDateTime usedTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;
}