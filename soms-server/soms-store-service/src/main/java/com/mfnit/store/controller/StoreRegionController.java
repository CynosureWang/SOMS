package com.mfnit.store.controller;

import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.store.dto.RegionCreateDTO;
import com.mfnit.store.dto.RegionUpdateDTO;
import com.mfnit.store.service.StoreRegionService;
import com.mfnit.store.vo.RegionTreeVO;
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
 * @CreateTime 2026/10/5 14:28
 * @Description SOMS Store Region Controller 门店区域控制器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Validated
@RestController
@RequestMapping("/api/v1/store/region")
@RequiredArgsConstructor
public class StoreRegionController {

    private final StoreRegionService regionService;

    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('store:region:list')")
    public Result<List<RegionTreeVO>> tree(@RequestParam(required = false) Integer regionType) {
        return ResultGenerator.genSuccessResult(regionService.getTree(regionType));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('store:region:add')")
    public Result<Long> create(@Valid @RequestBody RegionCreateDTO dto) {
        return ResultGenerator.genSuccessResult(regionService.createRegion(dto));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('store:region:edit')")
    public Result<Void> update(@Valid @RequestBody RegionUpdateDTO dto) {
        regionService.updateRegion(dto);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{regionId}")
    @PreAuthorize("hasAuthority('store:region:delete')")
    public Result<Void> delete(@PathVariable Long regionId) {
        regionService.deleteRegion(regionId);
        return ResultGenerator.genSuccessResult();
    }
}
