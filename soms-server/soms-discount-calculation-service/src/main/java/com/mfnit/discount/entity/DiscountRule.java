package com.mfnit.discount.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 15:14
 * @Description SOMS 优惠券规则实体类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("discount_rule")
public class DiscountRule implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "rule_id", type = IdType.ASSIGN_ID)
    private Long ruleId;

    private String ruleName;
    private Integer ruleType;

    private Integer scopeType;
    private String scopeValue;
    private Long storeId;

    private LocalTime timeStart;
    private LocalTime timeEnd;

    private Integer shelfLifeMin;
    private Integer shelfLifeMax;

    private BigDecimal discountRate;
    private BigDecimal reduceAmount;
    private BigDecimal fixedPrice;

    private LocalDate validStart;
    private LocalDate validEnd;

    private Integer status;
    private Integer priority;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;

    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}