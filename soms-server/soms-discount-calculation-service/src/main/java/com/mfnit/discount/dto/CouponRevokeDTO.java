package com.mfnit.discount.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:01
 * @Description SOMS - Coupon Revoke DTO 券回收DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class CouponRevokeDTO {
    /** 券ID列表 */
    private List<Long> couponIds;

    /** 按模板回收（作废该模板下所有未使用的券） */
    private Long templateId;

    /** 按会员回收（作废该会员所有未使用的券） */
    private Long customerId;

    @NotBlank(message = "回收原因不能为空")
    private String reason;
}
