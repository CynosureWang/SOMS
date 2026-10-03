package com.mfnit.stock.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 12:36
 * @Description SOMS 库存DTO 库存释放
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class StockReleaseDTO {
    @NotNull(message = "门店ID不能为空")
    private Long storeId;
    @NotEmpty(message = "商品列表不能为空")
    private List<StockItem> items;
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    private String orderNo;
}
