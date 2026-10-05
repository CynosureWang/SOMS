package com.mfnit.discount.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:09
 * @Description SOMS 券模板 / 发放 DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class CouponTemplateCreateDTO {
    @NotBlank(message = "模板编码不能为空")
    private String templateCode;
    @NotBlank(message = "券名称不能为空")
    private String templateName;
    @NotNull(message = "券类型不能为空")
    private Integer couponType;

    private BigDecimal threshold = BigDecimal.ZERO;
    private BigDecimal amount;
    private BigDecimal discountRate;

    private Integer totalCount;
    private Integer perLimit = 1;

    private Integer validType = 1;
    private LocalDateTime validStart;
    private LocalDateTime validEnd;
    private Integer validDays;

    private Integer scopeType;
    private String scopeValue;
    private Integer stackWithPromotion = 1;
}