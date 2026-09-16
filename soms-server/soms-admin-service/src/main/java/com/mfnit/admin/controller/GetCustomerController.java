package com.mfnit.admin.controller;

import com.mfnit.admin.service.GetCustomerService;
import com.mfnit.common.api.dto.customer.CustomerDTO;
import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/17 00:39
 * @Description SOMS Admin 获取Customer信息控制器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class GetCustomerController {

    // 注入Common模块中的Feign远程调用客户端
    private final GetCustomerService getCustomerService;

    /**
     * 分页查询客户列表
     */
    @GetMapping("/customer/list")
    public Result<PageResult<CustomerDTO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String mobile) {

        PageResult<CustomerDTO> page =
                getCustomerService.getCustomers(pageNum, pageSize, customerName, mobile);

        return ResultGenerator.genSuccessResult(page);
    }

}
