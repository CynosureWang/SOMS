package com.mfnit.discount.engine;

import lombok.Data;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:30
 * @Description SOMS 优惠券计算引擎
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class DiscountInternalContext {
    private Long storeId;
    private Long customerId;
    private Integer memberLevel;
    private java.time.LocalDateTime currentTime;
    private List<InternalCartItem> items;
    private List<Long> couponIds;
}