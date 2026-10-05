package com.mfnit.discount.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:42
 * @Description SOMS - Discount Rule VO 优惠规则VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class DiscountRuleVO {
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
    private LocalDateTime gmtCreate;
}
