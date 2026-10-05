package com.mfnit.product.controller;

import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.product.dto.CategoryCreateDTO;
import com.mfnit.product.dto.CategoryUpdateDTO;
import com.mfnit.product.service.ProductCategoryService;
import com.mfnit.product.vo.CategoryTreeVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 17:19
 * @Description SOMS 商品分类控制器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Validated
@RestController
@RequestMapping("/api/v1/product/category")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('product:category:list')")
    public Result<List<CategoryTreeVO>> tree() {
        return ResultGenerator.genSuccessResult(categoryService.getTree());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('product:category:add')")
    public Result<Long> create(@Valid @RequestBody CategoryCreateDTO dto) {
        return ResultGenerator.genSuccessResult(categoryService.createCategory(dto));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('product:category:edit')")
    public Result<Void> update(@Valid @RequestBody CategoryUpdateDTO dto) {
        categoryService.updateCategory(dto);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('product:category:delete')")
    public Result<Void> delete(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResultGenerator.genSuccessResult();
    }
}
