package com.mfnit.discount.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 15:28
 * @Description SOMS 优惠券计算服务DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class DiscountContextDTO {
    private Long storeId;
    private Long customerId;
    private Integer memberLevel;
    private List<CartItemDTO> items;
    private List<Long> couponIds;
}

