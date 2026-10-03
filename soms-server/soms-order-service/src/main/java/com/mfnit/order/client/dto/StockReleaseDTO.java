package com.mfnit.order.client.dto;

import lombok.Data;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:14
 * @Description SOMS 库存释放DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class StockReleaseDTO {
    private Long storeId;
    private List<StockItem> items;
    private Long orderId;
    private String orderNo;
}