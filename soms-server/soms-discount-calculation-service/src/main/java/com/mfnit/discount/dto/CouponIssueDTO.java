package com.mfnit.discount.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:10
 * @Description SOMS 券模板 / 发放 DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class CouponIssueDTO {
    @NotNull(message = "模板ID不能为空")
    private Long templateId;
    @NotNull(message = "会员列表不能为空")
    private List<Long> customerIds;
}
