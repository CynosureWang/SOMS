package com.mfnit.discount.controller;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.discount.dto.DiscountRuleCreateDTO;
import com.mfnit.discount.service.DiscountRuleService;
import com.mfnit.discount.vo.DiscountRuleVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:46
 * @Description SOMS - Discount Rule Controller 折扣规则控制器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Validated
@RestController
@RequestMapping("/api/v1/discount/rule")
@RequiredArgsConstructor
public class DiscountRuleController {

    private final DiscountRuleService ruleService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('discount:rule:list')")
    public Result<PageResult<DiscountRuleVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer ruleType,
            @RequestParam(required = false) Integer status) {
        return ResultGenerator.genSuccessResult(
                ruleService.pageRule(pageNum, pageSize, ruleType, status));
    }

    @GetMapping("/{ruleId}")
    @PreAuthorize("hasAuthority('discount:rule:list')")
    public Result<DiscountRuleVO> detail(@PathVariable Long ruleId) {
        return ResultGenerator.genSuccessResult(ruleService.getDetail(ruleId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('discount:rule:add')")
    public Result<Long> create(@Valid @RequestBody DiscountRuleCreateDTO dto) {
        return ResultGenerator.genSuccessResult(ruleService.createRule(dto));
    }

    @PutMapping("/{ruleId}/status")
    @PreAuthorize("hasAuthority('discount:rule:edit')")
    public Result<Void> updateStatus(@PathVariable Long ruleId,
                                     @RequestParam Integer status) {
        ruleService.updateRule(ruleId, status);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{ruleId}")
    @PreAuthorize("hasAuthority('discount:rule:delete')")
    public Result<Void> delete(@PathVariable Long ruleId) {
        ruleService.deleteRule(ruleId);
        return ResultGenerator.genSuccessResult();
    }
}
