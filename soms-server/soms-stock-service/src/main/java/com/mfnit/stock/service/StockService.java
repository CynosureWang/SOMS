package com.mfnit.stock.service;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.stock.dto.*;
import com.mfnit.stock.vo.StockFlowVO;
import com.mfnit.stock.vo.StoreStockVO;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 12:38
 * @Description SOMS 库存服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface StockService {

    /** 入库 */
    void stockIn(StockInDTO dto);

    /** 出库 */
    void stockOut(StockOutDTO dto);

    /** 锁定库存（下单时调用） */
    void lockStock(StockLockDTO dto);

    /** 扣减库存（支付成功调用） */
    void deductStock(StockDeductDTO dto);

    /** 释放锁定（取消订单调用） */
    void releaseStock(StockReleaseDTO dto);

    /** 查询单商品库存 */
    StoreStockVO getStock(Long storeId, Long productId);

    /** 批量查询 */
    List<StoreStockVO> listStock(Long storeId, List<Long> productIds);

    /** 分页查询门店库存 */
    PageResult<StoreStockVO> pageStock(StockQueryDTO dto);

    /** 分页查询库存流水 */
    PageResult<StockFlowVO> pageFlow(StockQueryDTO dto, Integer flowType);
}
