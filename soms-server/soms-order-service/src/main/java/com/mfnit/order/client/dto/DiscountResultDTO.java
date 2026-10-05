package com.mfnit.order.client.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 21:43
 * @Description SOMS 订单服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class DiscountResultDTO {
    private BigDecimal originalAmount;      // 原价总额
    private BigDecimal discountAmount;      // 优惠总额
    private BigDecimal payAmount;           // 应付
    private List<ItemDiscountDTO> items;    // 明细
    private List<AppliedPromotionDTO> appliedPromotions;  // 应用的促销
}