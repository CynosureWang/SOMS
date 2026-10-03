package com.mfnit.product.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:08
 * @Description SOMS 分类树形结构VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class CategoryTreeVO {
    private Long categoryId;
    private String categoryCode;
    private String categoryName;
    private Long parentId;
    private Integer level;
    private String icon;
    private Integer sort;
    private Integer status;
    private List<CategoryTreeVO> children;
}
