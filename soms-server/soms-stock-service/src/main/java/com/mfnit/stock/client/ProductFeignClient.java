package com.mfnit.stock.client;

import com.mfnit.common.api.result.Result;
import com.mfnit.stock.client.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 13:06
 * @Description SOMS ProductFeignClient
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@FeignClient(value = "service-product", path = "/api/v1/product")
public interface ProductFeignClient {

    @GetMapping("/internal/{productId}")
    Result<ProductDTO> getProduct(@PathVariable("productId") Long productId);
}
