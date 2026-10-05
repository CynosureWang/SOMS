package com.mfnit.product.service;

import com.mfnit.product.dto.CategoryCreateDTO;
import com.mfnit.product.dto.CategoryUpdateDTO;
import com.mfnit.product.vo.CategoryTreeVO;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:10
 * @Description SOMS 产品分类服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface ProductCategoryService {
    List<CategoryTreeVO> getTree();
    Long createCategory(CategoryCreateDTO dto);
    void updateCategory(CategoryUpdateDTO dto);
    void deleteCategory(Long categoryId);
}