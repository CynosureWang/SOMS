package com.mfnit.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:03
 * @Description SOMS 商品分类创建DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class CategoryCreateDTO {
    @NotBlank(message = "分类编码不能为空")
    private String categoryCode;
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;
    private Long parentId = 0L;
    private String icon;
    private Integer sort = 0;
    private String remark;
}
