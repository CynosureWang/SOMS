package com.mfnit.customer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.customer.entity.Customer;
import com.mfnit.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/16
 * @Description 客户管理控制器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Validated
@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 新增客户
     * @param customer 客户信息
     * @return 新增后的客户信息
     */
    @PostMapping
    public Result addCustomer(@Valid @RequestBody Customer customer) {
        Customer saved = customerService.addCustomer(customer);
        return ResultGenerator.genSuccessMsgDataResult("新增客户成功", saved);
    }

    /**
     * 根据ID删除客户（逻辑删除）
     * @param customerId 会员ID
     * @return 被删除的客户信息
     */
    @DeleteMapping("/{customerId}")
    public Result deleteCustomer(@PathVariable Long customerId) {
        Customer deleted = customerService.deleteCustomer(customerId);
        return ResultGenerator.genSuccessMsgDataResult("删除客户成功", deleted);
    }

    /**
     * 修改客户信息
     * @param customer 客户信息
     * @return 更新后的客户信息
     */
    @PutMapping
    public Result updateCustomer(@Valid @RequestBody Customer customer) {
        Customer updated = customerService.updateCustomer(customer);
        return ResultGenerator.genSuccessMsgDataResult("修改客户信息成功", updated);
    }

    /**
     * 根据ID查询客户
     * @param customerId 会员ID
     * @return 客户信息
     */
    @GetMapping("/{customerId}")
    public Result getCustomerById(@PathVariable Long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        return ResultGenerator.genSuccessResult(customer);
    }

    /**
     * 根据手机号查询客户
     * @param mobile 手机号
     * @return 客户信息
     */
    @GetMapping("/mobile/{mobile}")
    public Result getCustomerByMobile(@PathVariable String mobile) {
        Customer customer = customerService.getCustomerByMobile(mobile);
        return ResultGenerator.genSuccessResult(customer);
    }

    /**
     * 分页查询客户列表（支持按姓名、手机号模糊搜索）
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param customerName 会员姓名（模糊）
     * @param mobile   手机号（模糊）
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result getCustomerList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String mobile) {

        IPage<Customer> page = customerService.getCustomerPage(pageNum, pageSize, customerName, mobile);
        return ResultGenerator.genSuccessResult(page);
    }

}
