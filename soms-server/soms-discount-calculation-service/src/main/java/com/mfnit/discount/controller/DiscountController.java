package com.mfnit.discount.controller;

import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.discount.dto.DiscountContextDTO;
import com.mfnit.discount.service.DiscountCalculateService;
import com.mfnit.discount.vo.DiscountResultVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:44
 * @Description SOMS 折扣计算服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Validated
@RestController
@RequestMapping("/api/v1/discount")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountCalculateService calculateService;

    /**
     * 计算购物车优惠（内部接口，order 和收银端调用）
     * 不加 @PreAuthorize，网关不路由 /internal/**
     */
    @PostMapping("/internal/calculate")
    public Result<DiscountResultVO> calculate(@Valid @RequestBody DiscountContextDTO ctx) {
        return ResultGenerator.genSuccessResult(calculateService.calculate(ctx));
    }
}
