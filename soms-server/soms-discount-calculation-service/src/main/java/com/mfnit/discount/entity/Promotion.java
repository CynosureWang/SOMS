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
 * @CreateTime 2026/10/4 15:12
 * @Description SOMS 优惠券实体类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("promotion")
public class Promotion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "promotion_id", type = IdType.ASSIGN_ID)
    private Long promotionId;

    private String promotionCode;
    private String promotionName;
    private Integer promotionType;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Integer priority;
    private Integer stackable;
    private String excludeGroup;
    private Integer stackWithMember;
    /** 是否允许和券叠加：0否 1是 */
    private Integer stackWithCoupon;

    private Integer scopeRelation;

    private Integer status;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;

    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}
