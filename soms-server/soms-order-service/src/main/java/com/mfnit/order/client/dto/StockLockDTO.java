package com.mfnit.order.client.dto;

import lombok.Data;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:13
 * @Description SOMS 订单服务库存锁定DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class StockLockDTO {
    private Long storeId;
    private List<StockItem> items;
    private Long orderId;
    private String orderNo;
}
