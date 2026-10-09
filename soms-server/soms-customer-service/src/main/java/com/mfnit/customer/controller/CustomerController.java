package com.mfnit.customer.controller;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.customer.dto.CustomerCreateDTO;
import com.mfnit.customer.dto.CustomerQueryDTO;
import com.mfnit.customer.dto.CustomerUpdateDTO;
import com.mfnit.customer.service.CustomerService;
import com.mfnit.customer.vo.CustomerVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @param dto 新增客户请求
     * @return 新增后的客户信息
     */
    @PostMapping
    public Result<CustomerVO> addCustomer(@Valid @RequestBody CustomerCreateDTO dto) {
        CustomerVO saved = customerService.addCustomer(dto);
        return ResultGenerator.genSuccessMsgDataResult("新增客户成功", saved);
    }

    /**
     * 根据ID删除客户（逻辑删除）
     * @param customerId 会员ID
     * @return 被删除的客户信息
     */
    @DeleteMapping("/{customerId}")
    public Result<CustomerVO> deleteCustomer(@PathVariable Long customerId) {
        CustomerVO deleted = customerService.deleteCustomer(customerId);
        return ResultGenerator.genSuccessMsgDataResult("删除客户成功", deleted);
    }

    /**
     * 修改客户信息
     * @param dto 修改客户请求
     * @return 更新后的客户信息
     */
    @PutMapping
    public Result<CustomerVO> updateCustomer(@Valid @RequestBody CustomerUpdateDTO dto) {
        CustomerVO updated = customerService.updateCustomer(dto);
        return ResultGenerator.genSuccessMsgDataResult("修改客户信息成功", updated);
    }

    /**
     * 根据ID查询客户
     * @param customerId 会员ID
     * @return 客户信息
     */
    @GetMapping("/{customerId}")
    public Result<CustomerVO> getCustomerById(@PathVariable Long customerId) {
        CustomerVO customer = customerService.getCustomerById(customerId);
        return ResultGenerator.genSuccessResult(customer);
    }

    /**
     * 根据手机号查询客户
     * @param mobile 手机号
     * @return 客户信息
     */
    @GetMapping("/mobile/{mobile}")
    public Result<CustomerVO> getCustomerByMobile(@PathVariable String mobile) {
        CustomerVO customer = customerService.getCustomerByMobile(mobile);
        return ResultGenerator.genSuccessResult(customer);
    }

    /**
     * 分页查询客户列表（支持按姓名、手机号模糊搜索）
     * @param dto 分页查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<PageResult<CustomerVO>> getCustomerList(CustomerQueryDTO dto) {
        return ResultGenerator.genSuccessResult(customerService.getCustomerPage(dto));
    }

}
