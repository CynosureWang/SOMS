package com.mfnit.discount.dto;

import lombok.Data;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 15:46
 * @Description SOMS 优惠券条件DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class PromotionConditionDTO {
    private Integer conditionType;
    private String operator;
    private java.math.BigDecimal threshold;
}
