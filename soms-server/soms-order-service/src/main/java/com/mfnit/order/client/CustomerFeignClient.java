package com.mfnit.order.client;

import com.mfnit.common.api.dto.customer.ConsumeCompleteDTO;
import com.mfnit.common.api.dto.customer.CustomerAccountDTO;
import com.mfnit.common.api.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/10/12
 * @Description SOMS Customer服务Feign客户端（消费完成回调：累计消费/积分/等级）
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@FeignClient(value = "service-customer", path = "/api/v1/customer/internal")
public interface CustomerFeignClient {

    /**
     * 消费完成回调（幂等，bizType+bizId 唯一）
     * @param dto 消费回调请求
     * @return 回调后账户概览
     */
    @PostMapping("/consume/complete")
    Result<CustomerAccountDTO> completeConsume(@RequestBody ConsumeCompleteDTO dto);
}
