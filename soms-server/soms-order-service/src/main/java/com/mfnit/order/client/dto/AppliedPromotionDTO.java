package com.mfnit.order.client.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 21:44
 * @Description SOMS 订单服务 - 应用的促销信息数据传输对象
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class AppliedPromotionDTO {
    private Long promotionId;
    private String promotionName;
    private Integer promotionType;
    private BigDecimal discountAmount;
}
