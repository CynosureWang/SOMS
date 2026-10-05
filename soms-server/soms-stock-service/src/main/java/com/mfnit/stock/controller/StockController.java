package com.mfnit.stock.controller;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.stock.dto.*;
import com.mfnit.stock.service.StockService;
import com.mfnit.stock.vo.StockFlowVO;
import com.mfnit.stock.vo.StoreStockVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 12:40
 * @Description SOMS 库存模块控制器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    // ===== 后台管理 =====

    /** 分页查询门店库存 */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('stock:list')")
    public Result<PageResult<StoreStockVO>> list(StockQueryDTO dto) {
        return ResultGenerator.genSuccessResult(stockService.pageStock(dto));
    }

    /** 入库 */
    @PostMapping("/in")
    @PreAuthorize("hasAuthority('stock:in')")
    public Result<Void> stockIn(@Valid @RequestBody StockInDTO dto) {
        stockService.stockIn(dto);
        return ResultGenerator.genSuccessResult();
    }

    /** 出库 */
    @PostMapping("/out")
    @PreAuthorize("hasAuthority('stock:out')")
    public Result<Void> stockOut(@Valid @RequestBody StockOutDTO dto) {
        stockService.stockOut(dto);
        return ResultGenerator.genSuccessResult();
    }

    /** 库存流水 */
    @GetMapping("/flow/list")
    @PreAuthorize("hasAuthority('stock:list')")
    public Result<PageResult<StockFlowVO>> flowList(StockQueryDTO dto,
                                                    @RequestParam(required = false) Integer flowType) {
        return ResultGenerator.genSuccessResult(stockService.pageFlow(dto, flowType));
    }

    // ===== 服务间调用（order 调） =====

    /** 查询单商品库存 */
    @GetMapping("/get")
    public Result<StoreStockVO> get(@RequestParam Long storeId, @RequestParam Long productId) {
        return ResultGenerator.genSuccessResult(stockService.getStock(storeId, productId));
    }

    /** 批量查询 */
    @PostMapping("/list-by-products")
    public Result<List<StoreStockVO>> listByProducts(@RequestParam Long storeId,
                                                     @RequestBody List<Long> productIds) {
        return ResultGenerator.genSuccessResult(stockService.listStock(storeId, productIds));
    }

    /** 锁定库存 */
    @PostMapping("/lock")
    public Result<Void> lock(@Valid @RequestBody StockLockDTO dto) {
        log.info("stock 收到锁定请求：storeId={}, orderId={}, items={}",
                dto.getStoreId(), dto.getOrderId(), dto.getItems());
        stockService.lockStock(dto);
        return ResultGenerator.genSuccessResult();
    }

    /** 扣减库存 */
    @PostMapping("/deduct")
    public Result<Void> deduct(@Valid @RequestBody StockDeductDTO dto) {
        stockService.deductStock(dto);
        return ResultGenerator.genSuccessResult();
    }

    /** 释放锁定 */
    @PostMapping("/release")
    public Result<Void> release(@Valid @RequestBody StockReleaseDTO dto) {
        stockService.releaseStock(dto);
        return ResultGenerator.genSuccessResult();
    }
}
