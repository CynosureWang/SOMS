package com.mfnit.order.client;

import com.mfnit.common.api.result.Result;
import com.mfnit.order.client.dto.StoreDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:44
 * @Description SOMS 下单校验门店是否营业
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@FeignClient(value = "service-store", path = "/api/v1/store")
public interface StoreFeignClient {

    @GetMapping("/internal/{storeId}")
    Result<StoreDTO> getStore(@PathVariable("storeId") Long storeId);

    @GetMapping("/internal/{storeId}/can-trade")
    Result<Boolean> canTrade(@PathVariable("storeId") Long storeId);
}