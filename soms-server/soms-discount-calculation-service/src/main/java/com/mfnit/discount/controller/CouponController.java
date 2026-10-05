package com.mfnit.discount.controller;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.discount.dto.CouponIssueDTO;
import com.mfnit.discount.dto.CouponRevokeDTO;
import com.mfnit.discount.dto.CouponTemplateCreateDTO;
import com.mfnit.discount.service.CouponService;
import com.mfnit.discount.vo.CouponTemplateVO;
import com.mfnit.discount.vo.CouponVO;
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
 * @CreateTime 2026/10/4 16:45
 * @Description SOMS Discount Calculation Service Coupon Controller 优惠券控制器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Validated
@RestController
@RequestMapping("/api/v1/discount/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // ===== 券模板 =====

    @GetMapping("/template/list")
    @PreAuthorize("hasAuthority('discount:coupon:list')")
    public Result<PageResult<CouponTemplateVO>> templateList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResultGenerator.genSuccessResult(
                couponService.pageTemplate(pageNum, pageSize));
    }

    @GetMapping("/template/{templateId}")
    @PreAuthorize("hasAuthority('discount:coupon:list')")
    public Result<CouponTemplateVO> templateDetail(@PathVariable Long templateId) {
        return ResultGenerator.genSuccessResult(couponService.getTemplate(templateId));
    }

    @PostMapping("/template")
    @PreAuthorize("hasAuthority('discount:coupon:add')")
    public Result<Long> createTemplate(@Valid @RequestBody CouponTemplateCreateDTO dto) {
        return ResultGenerator.genSuccessResult(couponService.createTemplate(dto));
    }

    @PutMapping("/template/{templateId}/status")
    @PreAuthorize("hasAuthority('discount:coupon:edit')")
    public Result<Void> updateTemplateStatus(@PathVariable Long templateId,
                                             @RequestParam Integer status) {
        couponService.updateTemplate(templateId, status);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/template/{templateId}")
    @PreAuthorize("hasAuthority('discount:coupon:delete')")
    public Result<Void> deleteTemplate(@PathVariable Long templateId) {
        couponService.deleteTemplate(templateId);
        return ResultGenerator.genSuccessResult();
    }

    // ===== 发券 =====

    @PostMapping("/issue")
    @PreAuthorize("hasAuthority('discount:coupon:issue')")
    public Result<Integer> issue(@Valid @RequestBody CouponIssueDTO dto) {
        return ResultGenerator.genSuccessResult(couponService.issueCoupon(dto));
    }

    @PostMapping("/receive/{templateId}")
    public Result<CouponVO> receive(@PathVariable Long templateId,
                                    @RequestParam Long customerId) {
        return ResultGenerator.genSuccessResult(
                couponService.receiveCoupon(templateId, customerId));
    }

    // ===== 用户券 =====

    @GetMapping("/available")
    public Result<List<CouponVO>> available(@RequestParam Long customerId) {
        return ResultGenerator.genSuccessResult(
                couponService.listAvailableCoupon(customerId));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('discount:coupon:list')")
    public Result<PageResult<CouponVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer status) {
        return ResultGenerator.genSuccessResult(
                couponService.pageCoupon(pageNum, pageSize, customerId, status));
    }

    /**
     * 锁定券（下单时调用，内部接口）
     */
    @PostMapping("/internal/lock")
    public Result<Void> lock(@RequestParam Long couponId,
                             @RequestParam Long orderId,
                             @RequestParam String orderNo) {
        couponService.lockCoupon(couponId, orderId, orderNo);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 核销券（支付成功调用，内部接口）
     */
    @PostMapping("/internal/use")
    public Result<Void> use(@RequestParam Long couponId) {
        couponService.useCoupon(couponId);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 释放券（取消订单调用，内部接口）
     */
    @PostMapping("/internal/release")
    public Result<Void> release(@RequestParam Long couponId) {
        couponService.releaseCoupon(couponId);
        return ResultGenerator.genSuccessResult();
    }

    // ===== 券回收 =====
    @PostMapping("/revoke")
    @PreAuthorize("hasAuthority('discount:coupon:revoke')")
    public Result<Integer> revoke(@Valid @RequestBody CouponRevokeDTO dto) {
        return ResultGenerator.genSuccessResult(couponService.revokeCoupon(dto));
    }
}
