package com.mfnit.discount.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:01
 * @Description SOMS 优惠券规则创建DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class DiscountRuleCreateDTO {
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;
    @NotNull(message = "规则类型不能为空")
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

    private Integer priority = 0;
}
