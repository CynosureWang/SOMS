package com.mfnit.product.dto;

import lombok.Data;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:06
 * @Description SOMS 商品查询DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class ProductQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;      // 名称/编码/条码/拼音 模糊
    private Long categoryId;
    private Long brandId;
    private Integer status;
    private Integer isWeight;
}
