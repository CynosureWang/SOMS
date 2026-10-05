package com.mfnit.store.controller;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.store.dto.*;
import com.mfnit.store.service.StoreService;
import com.mfnit.store.vo.StoreDetailVO;
import com.mfnit.store.vo.StoreVO;
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
 * @CreateTime 2026/10/5 14:29
 * @Description SOMS Store Controller 门店控制器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Validated
@RestController
@RequestMapping("/api/v1/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    // ===== 后台管理 =====

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('store:list')")
    public Result<PageResult<StoreVO>> list(StoreQueryDTO dto) {
        return ResultGenerator.genSuccessResult(storeService.pageStore(dto));
    }

    @GetMapping("/{storeId}")
    @PreAuthorize("hasAuthority('store:detail')")
    public Result<StoreDetailVO> detail(@PathVariable Long storeId) {
        return ResultGenerator.genSuccessResult(storeService.getDetail(storeId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('store:add')")
    public Result<Long> create(@Valid @RequestBody StoreCreateDTO dto) {
        return ResultGenerator.genSuccessResult(storeService.createStore(dto));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('store:edit')")
    public Result<Void> update(@Valid @RequestBody StoreUpdateDTO dto) {
        storeService.updateStore(dto);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{storeId}")
    @PreAuthorize("hasAuthority('store:delete')")
    public Result<Void> delete(@PathVariable Long storeId) {
        storeService.deleteStore(storeId);
        return ResultGenerator.genSuccessResult();
    }

    @PutMapping("/{storeId}/status")
    @PreAuthorize("hasAuthority('store:edit')")
    public Result<Void> changeStatus(@PathVariable Long storeId,
                                     @RequestParam Integer businessStatus) {
        storeService.changeStatus(storeId, businessStatus);
        return ResultGenerator.genSuccessResult();
    }

    @PutMapping("/detail")
    @PreAuthorize("hasAuthority('store:edit')")
    public Result<Void> updateDetail(@Valid @RequestBody StoreDetailUpdateDTO dto) {
        storeService.updateDetail(dto);
        return ResultGenerator.genSuccessResult();
    }

    // ===== 内部接口 =====

    @GetMapping("/internal/{storeId}")
    public Result<StoreVO> getSimple(@PathVariable Long storeId) {
        return ResultGenerator.genSuccessResult(storeService.getSimple(storeId));
    }

    @PostMapping("/internal/list-by-ids")
    public Result<List<StoreVO>> listByIds(@RequestBody List<Long> storeIds) {
        return ResultGenerator.genSuccessResult(storeService.listByIds(storeIds));
    }

    /** 内部接口：校验门店是否可营业 */
    @GetMapping("/internal/{storeId}/can-trade")
    public Result<Boolean> canTrade(@PathVariable Long storeId) {
        return ResultGenerator.genSuccessResult(storeService.canTrade(storeId));
    }
}
