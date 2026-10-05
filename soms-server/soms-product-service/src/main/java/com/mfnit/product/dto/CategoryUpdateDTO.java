package com.mfnit.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:04
 * @Description SOMS 商品分类更新DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class CategoryUpdateDTO {
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    private String categoryName;
    private String icon;
    private Integer sort;
    private Integer status;
    private String remark;
}