package com.mfnit.product.controller;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.product.dto.BrandCreateDTO;
import com.mfnit.product.dto.BrandUpdateDTO;
import com.mfnit.product.service.ProductBrandService;
import com.mfnit.product.vo.BrandVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 17:42
 * @Description SOMS - Product Brand Controller
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Validated
@RestController
@RequestMapping("/api/v1/product/brand")
@RequiredArgsConstructor
public class ProductBrandController {

    private final ProductBrandService brandService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('product:brand:list')")
    public Result<PageResult<BrandVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return ResultGenerator.genSuccessResult(brandService.pageBrand(pageNum, pageSize, keyword));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('product:brand:add')")
    public Result<Long> create(@Valid @RequestBody BrandCreateDTO dto) {
        return ResultGenerator.genSuccessResult(brandService.createBrand(dto));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('product:brand:edit')")
    public Result<Void> update(@Valid @RequestBody BrandUpdateDTO dto) {
        brandService.updateBrand(dto);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{brandId}")
    @PreAuthorize("hasAuthority('product:brand:delete')")
    public Result<Void> delete(@PathVariable Long brandId) {
        brandService.deleteBrand(brandId);
        return ResultGenerator.genSuccessResult();
    }
}
