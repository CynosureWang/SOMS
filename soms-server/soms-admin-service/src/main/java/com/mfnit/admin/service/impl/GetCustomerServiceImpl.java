package com.mfnit.admin.service.impl;

import com.mfnit.admin.service.GetCustomerService;
import com.mfnit.common.api.client.CustomerFeignClient;
import com.mfnit.common.api.dto.customer.CustomerDTO;
import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultCodeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/17 00:41
 * @Description SOMS Admin 获取Customer信息服务实现
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Service
@RequiredArgsConstructor
public class GetCustomerServiceImpl implements GetCustomerService {

    private final CustomerFeignClient customerFeignClient;

    @Override
    public PageResult<CustomerDTO> getCustomers(Integer pageNum, Integer pageSize,
                                                String customerName, String mobile) {
        Result<PageResult<CustomerDTO>> result =
                customerFeignClient.getAllCustomers(pageNum, pageSize, customerName, mobile);

        if (result.getCode() != ResultCodeMessage.SUCCESS.getCode()) {
            throw new RuntimeException("查询客户失败：" + result.getMessage());
        }
        return result.getData();
    }
}