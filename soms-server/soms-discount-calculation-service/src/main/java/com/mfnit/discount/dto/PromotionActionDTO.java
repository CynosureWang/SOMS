package com.mfnit.discount.dto;

import lombok.Data;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 15:46
 * @Description SOMS 优惠券动作DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class PromotionActionDTO {
    private Integer actionType;
    private java.math.BigDecimal actionValue;
    private String actionExt;
}