package com.mfnit.product.controller;

import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.product.dto.ScaleLabelGenerateDTO;
import com.mfnit.product.dto.ScaleLabelMarkDTO;
import com.mfnit.product.service.ScaleLabelService;
import com.mfnit.product.vo.ScaleLabelVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 17:43
 * @Description SOMS - Scale Label Controller
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Validated
@RestController
@RequestMapping("/api/v1/product/scale/label")
@RequiredArgsConstructor
public class ScaleLabelController {

    private final ScaleLabelService scaleLabelService;

    /** 秤端：生成价签 */
    @PostMapping("/generate")
    public Result<ScaleLabelVO> generate(@Valid @RequestBody ScaleLabelGenerateDTO dto) {
        return ResultGenerator.genSuccessResult(scaleLabelService.generateLabel(dto));
    }

    /** 收银端：按条码查价签 */
    @GetMapping("/{barcode}")
    public Result<ScaleLabelVO> get(@PathVariable String barcode) {
        return ResultGenerator.genSuccessResult(scaleLabelService.getLabel(barcode));
    }

    /** 结算时：标记已售 */
    @PostMapping("/mark-sold")
    public Result<Void> markSold(@Valid @RequestBody ScaleLabelMarkDTO dto) {
        scaleLabelService.markSold(dto);
        return ResultGenerator.genSuccessResult();
    }
}
