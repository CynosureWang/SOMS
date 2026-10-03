package com.mfnit.stock.dto;

import lombok.Data;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 12:36
 * @Description SOMS 库存DTO 库存查询
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class StockQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private Long storeId;
    private String keyword;      // 商品名/编码
    private Long productId;
    private Integer warnOnly;    // 1仅查预警
}
