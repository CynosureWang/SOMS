package com.mfnit.product.controller;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.product.dto.ProductCreateDTO;
import com.mfnit.product.dto.ProductDTO;
import com.mfnit.product.dto.ProductQueryDTO;
import com.mfnit.product.dto.ProductUpdateDTO;
import com.mfnit.product.service.ProductService;
import com.mfnit.product.vo.ProductDetailVO;
import com.mfnit.product.vo.ProductSimpleVO;
import com.mfnit.product.vo.ProductVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 17:43
 * @Description SOMS - Product Controller
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Validated
@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ===== 后台管理 =====

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('product:list')")
    public Result<PageResult<ProductVO>> list(ProductQueryDTO dto) {
        return ResultGenerator.genSuccessResult(productService.pageProduct(dto));
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasAuthority('product:detail')")
    public Result<ProductDetailVO> detail(@PathVariable Long productId) {
        return ResultGenerator.genSuccessResult(productService.getDetail(productId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('product:add')")
    public Result<Long> create(@Valid @RequestBody ProductCreateDTO dto) {
        return ResultGenerator.genSuccessResult(productService.createProduct(dto));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('product:edit')")
    public Result<Void> update(@Valid @RequestBody ProductUpdateDTO dto) {
        productService.updateProduct(dto);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAuthority('product:delete')")
    public Result<Void> delete(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResultGenerator.genSuccessResult();
    }

    @PutMapping("/{productId}/status")
    @PreAuthorize("hasAuthority('product:edit')")
    public Result<Void> changeStatus(@PathVariable Long productId,
                                     @RequestParam Integer status) {
        productService.changeStatus(productId, status);
        return ResultGenerator.genSuccessResult();
    }

    // ===== 收银端（无需细粒度权限，只要求登录） =====

    @GetMapping("/barcode/{barcode}")
    public Result<ProductSimpleVO> getByBarcode(@PathVariable String barcode) {
        return ResultGenerator.genSuccessResult(productService.getByBarcode(barcode));
    }

    @GetMapping("/scale/{scaleCode}")
    public Result<ProductSimpleVO> getByScaleCode(@PathVariable String scaleCode) {
        return ResultGenerator.genSuccessResult(productService.getByScaleCode(scaleCode));
    }

    @GetMapping("/scale/list")
    public Result<List<ProductSimpleVO>> listScaleProducts(@RequestParam Long storeId) {
        return ResultGenerator.genSuccessResult(productService.listScaleProducts(storeId));
    }

    /** 内部调用：查商品（stock 调） */
    @GetMapping("/internal/{productId}")
    public Result<ProductDTO> getProductInternal(@PathVariable Long productId) {
        ProductDetailVO detail = productService.getDetail(productId);
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(detail, dto);
        return ResultGenerator.genSuccessResult(dto);
    }
}
