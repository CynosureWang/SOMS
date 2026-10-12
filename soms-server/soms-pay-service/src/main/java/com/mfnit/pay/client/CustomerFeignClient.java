package com.mfnit.pay.client;

import com.mfnit.common.api.dto.customer.BalanceDeductDTO;
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
 * @Description SOMS Customer服务Feign客户端（余额扣减/退款，仅供支付链路调用）
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@FeignClient(value = "service-customer", path = "/api/v1/customer/internal")
public interface CustomerFeignClient {

    /**
     * 余额扣减（幂等，bizType+bizId 唯一）
     * @param dto 扣减请求
     * @return 扣减后账户概览
     */
    @PostMapping("/balance/deduct")
    Result<CustomerAccountDTO> deductBalance(@RequestBody BalanceDeductDTO dto);

    /**
     * 余额退款（幂等）：退款原路退回会员余额
     * @param dto 退款请求
     * @return 退款后账户概览
     */
    @PostMapping("/balance/refund")
    Result<CustomerAccountDTO> refundBalance(@RequestBody BalanceDeductDTO dto);
}
