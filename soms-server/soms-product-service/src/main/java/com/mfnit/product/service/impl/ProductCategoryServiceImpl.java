package com.mfnit.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.product.dto.CategoryCreateDTO;
import com.mfnit.product.dto.CategoryUpdateDTO;
import com.mfnit.product.entity.ProductCategory;
import com.mfnit.product.mapper.ProductCategoryMapper;
import com.mfnit.product.service.ProductCategoryService;
import com.mfnit.product.vo.CategoryTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:11
 * @Description SOMS 产品分类服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryMapper categoryMapper;

    @Override
    public List<CategoryTreeVO> getTree() {
        List<ProductCategory> all = categoryMapper.selectList(
                new LambdaQueryWrapper<ProductCategory>()
                        .orderByAsc(ProductCategory::getSort));

        Map<Long, List<ProductCategory>> childrenMap = all.stream()
                .collect(Collectors.groupingBy(ProductCategory::getParentId));

        List<ProductCategory> roots = childrenMap.getOrDefault(0L, Collections.emptyList());
        return roots.stream().map(r -> buildTree(r, childrenMap)).collect(Collectors.toList());
    }

    private CategoryTreeVO buildTree(ProductCategory node,
                                     Map<Long, List<ProductCategory>> childrenMap) {
        CategoryTreeVO vo = new CategoryTreeVO()
                .setCategoryId(node.getCategoryId())
                .setCategoryCode(node.getCategoryCode())
                .setCategoryName(node.getCategoryName())
                .setParentId(node.getParentId())
                .setLevel(node.getLevel())
                .setIcon(node.getIcon())
                .setSort(node.getSort())
                .setStatus(node.getStatus());

        List<ProductCategory> children = childrenMap.get(node.getCategoryId());
        if (children != null && !children.isEmpty()) {
            vo.setChildren(children.stream().map(c -> buildTree(c, childrenMap)).collect(Collectors.toList()));
        }
        return vo;
    }

    @Override
    public Long createCategory(CategoryCreateDTO dto) {
        Long count = categoryMapper.selectCount(
                new LambdaQueryWrapper<ProductCategory>()
                        .eq(ProductCategory::getCategoryCode, dto.getCategoryCode()));
        if (count > 0) {
            throw new BusinessException("分类编码已存在：" + dto.getCategoryCode());
        }

        ProductCategory category = new ProductCategory()
                .setCategoryCode(dto.getCategoryCode())
                .setCategoryName(dto.getCategoryName())
                .setParentId(dto.getParentId())
                .setIcon(dto.getIcon())
                .setSort(dto.getSort())
                .setRemark(dto.getRemark())
                .setStatus(1);

        // 计算 level 和 path
        if (dto.getParentId() == null || dto.getParentId() == 0L) {
            category.setLevel(1);
            category.setParentId(0L);
        } else {
            ProductCategory parent = categoryMapper.selectById(dto.getParentId());
            if (parent == null) {
                throw new BusinessException("上级分类不存在");
            }
            category.setLevel(parent.getLevel() + 1);
        }

        categoryMapper.insert(category);

        // 回填 path
        String path;
        if (category.getParentId() == 0L) {
            path = "/" + category.getCategoryId() + "/";
        } else {
            ProductCategory parent = categoryMapper.selectById(category.getParentId());
            path = parent.getPath() + category.getCategoryId() + "/";
        }
        category.setPath(path);
        categoryMapper.updateById(category);

        return category.getCategoryId();
    }

    @Override
    public void updateCategory(CategoryUpdateDTO dto) {
        ProductCategory category = categoryMapper.selectById(dto.getCategoryId());
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        category.setCategoryName(dto.getCategoryName())
                .setIcon(dto.getIcon())
                .setSort(dto.getSort())
                .setStatus(dto.getStatus())
                .setRemark(dto.getRemark());
        categoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Long childCount = categoryMapper.selectCount(
                new LambdaQueryWrapper<ProductCategory>()
                        .eq(ProductCategory::getParentId, categoryId));
        if (childCount > 0) {
            throw new BusinessException("存在子分类，不能删除");
        }
        categoryMapper.deleteById(categoryId);
    }
}
