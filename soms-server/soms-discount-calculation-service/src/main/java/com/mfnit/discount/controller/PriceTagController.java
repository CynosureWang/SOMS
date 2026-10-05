package com.mfnit.discount.controller;

import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.discount.dto.GeneratePriceTagDTO;
import com.mfnit.discount.service.PriceTagService;
import com.mfnit.discount.vo.PriceTagVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:46
 * @Description SOMS - Price Tag Controller 价签控制器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Validated
@RestController
@RequestMapping("/api/v1/discount/price-tag")
@RequiredArgsConstructor
public class PriceTagController {

    private final PriceTagService priceTagService;

    /**
     * 生成自打价签（PDA 调用）
     * 需要传商品信息，因为 discount 服务不查 product 库
     * 建议 PDA 先调 product 拿商品信息，再调这个接口
     */
    @PostMapping("/generate")
    public Result<PriceTagVO> generate(@Valid @RequestBody GeneratePriceTagDTO dto,
                                       @RequestParam Long productId,
                                       @RequestParam String productName,
                                       @RequestParam Long categoryId,
                                       @RequestParam(required = false) Long brandId,
                                       @RequestParam java.math.BigDecimal originalPrice,
                                       @RequestParam(required = false) Integer shelfLifeDays) {
        return ResultGenerator.genSuccessResult(
                priceTagService.generate(dto, productId, productName,
                        categoryId, brandId, originalPrice, shelfLifeDays));
    }

    /**
     * 扫码查询价签（收银端调用）
     * 返回 null 表示不是自打价签，走正常商品流程
     */
    @GetMapping("/scan/{barcode}")
    public Result<PriceTagVO> scan(@PathVariable String barcode) {
        return ResultGenerator.genSuccessResult(priceTagService.scan(barcode));
    }
}
