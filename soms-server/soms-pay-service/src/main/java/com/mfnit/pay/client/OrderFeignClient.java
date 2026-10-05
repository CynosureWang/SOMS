package com.mfnit.pay.client;

import com.mfnit.common.api.result.Result;
import com.mfnit.pay.client.dto.OrderPaySuccessDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:53
 * @Description SOMS 订单Feign客户端
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@FeignClient(value = "service-order", path = "/api/v1/order")
public interface OrderFeignClient {

    @PostMapping("/internal/pay-success")
    Result<Void> paySuccess(@RequestBody OrderPaySuccessDTO dto);
}
