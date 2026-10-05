package com.mfnit.discount.entity;

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
 * @CreateTime 2026/10/4 15:14
 * @Description SOMS 优惠券模板实体类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("coupon_template")
public class CouponTemplate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "template_id", type = IdType.ASSIGN_ID)
    private Long templateId;

    private String templateCode;
    private String templateName;
    private Integer couponType;

    private BigDecimal threshold;
    private BigDecimal amount;
    private BigDecimal discountRate;

    private Integer totalCount;
    private Integer issuedCount;
    private Integer perLimit;

    private Integer validType;
    private LocalDateTime validStart;
    private LocalDateTime validEnd;
    private Integer validDays;

    private Integer scopeType;
    private String scopeValue;
    /** 是否允许和促销叠加：0否 1是 */
    private Integer stackWithPromotion;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;

    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}
