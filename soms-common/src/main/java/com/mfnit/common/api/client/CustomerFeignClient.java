package com.mfnit.common.api.client;

import com.mfnit.common.api.dto.customer.CustomerDTO;
import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
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
}