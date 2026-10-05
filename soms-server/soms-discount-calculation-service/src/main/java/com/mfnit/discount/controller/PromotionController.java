package com.mfnit.discount.controller;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.discount.dto.PromotionCreateDTO;
import com.mfnit.discount.service.PromotionService;
import com.mfnit.discount.vo.PromotionDetailVO;
import com.mfnit.discount.vo.PromotionVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:45
 * @Description SOMS 优惠券服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Validated
@RestController
@RequestMapping("/api/v1/discount/promotion")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('discount:promotion:list')")
    public Result<PageResult<PromotionVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type) {
        return ResultGenerator.genSuccessResult(
                promotionService.pagePromotion(pageNum, pageSize, status, type));
    }

    @GetMapping("/{promotionId}")
    @PreAuthorize("hasAuthority('discount:promotion:list')")
    public Result<PromotionDetailVO> detail(@PathVariable Long promotionId) {
        return ResultGenerator.genSuccessResult(promotionService.getDetail(promotionId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('discount:promotion:add')")
    public Result<Long> create(@Valid @RequestBody PromotionCreateDTO dto) {
        return ResultGenerator.genSuccessResult(promotionService.createPromotion(dto));
    }

    @PutMapping("/{promotionId}/status")
    @PreAuthorize("hasAuthority('discount:promotion:edit')")
    public Result<Void> updateStatus(@PathVariable Long promotionId,
                                     @RequestParam Integer status) {
        promotionService.updatePromotion(promotionId, status);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{promotionId}")
    @PreAuthorize("hasAuthority('discount:promotion:delete')")
    public Result<Void> delete(@PathVariable Long promotionId) {
        promotionService.deletePromotion(promotionId);
        return ResultGenerator.genSuccessResult();
    }
}
