package com.mfnit.order.client;

import com.mfnit.common.api.result.Result;
import com.mfnit.order.client.dto.StockDeductDTO;
import com.mfnit.order.client.dto.StockLockDTO;
import com.mfnit.order.client.dto.StockReleaseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:12
 * @Description SOMS 库存服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@FeignClient(value = "service-stock", path = "/api/v1/stock")
public interface StockFeignClient {

    @PostMapping("/lock")
    Result<Void> lock(@RequestBody StockLockDTO dto);

    @PostMapping("/deduct")
    Result<Void> deduct(@RequestBody StockDeductDTO dto);

    @PostMapping("/release")
    Result<Void> release(@RequestBody StockReleaseDTO dto);
}
