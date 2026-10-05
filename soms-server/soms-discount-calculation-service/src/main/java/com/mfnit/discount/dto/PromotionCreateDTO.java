package com.mfnit.discount.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 15:34
 * @Description SOMS 优惠券创建DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class PromotionCreateDTO {
    @NotBlank(message = "促销编码不能为空")
    private String promotionCode;
    @NotBlank(message = "促销名称不能为空")
    private String promotionName;
    @NotNull(message = "促销类型不能为空")
    private Integer promotionType;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    private Integer priority = 0;
    private Integer stackable = 0;
    private String excludeGroup;
    private Integer stackWithMember = 1;
    private Integer stackWithCoupon = 1;
    private Integer scopeRelation = 1;

    private List<PromotionScopeDTO> scopes;
    private List<PromotionConditionDTO> conditions;
    private List<PromotionActionDTO> actions;

    private String remark;
}
