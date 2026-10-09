package com.mfnit.common.api.client;

import com.mfnit.common.api.dto.customer.BalanceDeductDTO;
import com.mfnit.common.api.dto.customer.ConsumeCompleteDTO;
import com.mfnit.common.api.dto.customer.CustomerAccountDTO;
import com.mfnit.common.api.dto.customer.CustomerDTO;
import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/17 00:27
 * @Description SOMS Customer服务Feign客户端
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@FeignClient(value = "service-customer", path = "/api/v1/customer")
public interface CustomerFeignClient {

    /**
     * 分页查询客户列表
     * @param pageNum
     * @param pageSize
     * @param customerName
     * @param mobile
     * @return Result<PageResult<CustomerDTO>>
     */
    @GetMapping("/list")
    Result<PageResult<CustomerDTO>> getAllCustomers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String mobile);

    /**
     * 批量查询会员（内部接口）
     * @param customerIds 会员ID集合（逗号分隔）
     * @return Result<List<CustomerDTO>>
     */
    @GetMapping("/internal/batch")
    Result<List<CustomerDTO>> batchCustomers(@RequestParam("customerIds") List<Long> customerIds);

    /**
     * 余额扣减（内部接口，幂等，bizType+bizId 唯一）
     * @param dto 扣减请求
     * @return Result<CustomerAccountDTO>
     */
    @PostMapping("/internal/balance/deduct")
    Result<CustomerAccountDTO> deductBalance(@RequestBody BalanceDeductDTO dto);

    /**
     * 消费完成回调（内部接口，幂等）：累加累计消费、赠送积分、重算会员等级
     * @param dto 消费回调请求
     * @return Result<CustomerAccountDTO>
     */
    @PostMapping("/internal/consume/complete")
    Result<CustomerAccountDTO> completeConsume(@RequestBody ConsumeCompleteDTO dto);
}
