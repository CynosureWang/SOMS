package com.mfnit.admin.service;

import com.mfnit.common.api.dto.customer.CustomerDTO;
import com.mfnit.common.api.result.PageResult;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/17 00:41
 * @Description SOMS Admin 获取Customer信息服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface GetCustomerService {
    PageResult<CustomerDTO> getCustomers(Integer pageNum, Integer pageSize,
                                         String customerName, String mobile);
}
