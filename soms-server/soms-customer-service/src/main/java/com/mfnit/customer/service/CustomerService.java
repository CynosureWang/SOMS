package com.mfnit.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mfnit.customer.entity.Customer;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/16
 * @Description 客户服务接口
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface CustomerService extends IService<Customer> {

    /**
     * 新增客户
     * @param customer 客户信息
     * @return 新增后的客户信息（包含生成的ID）
     */
    Customer addCustomer(Customer customer);

    /**
     * 根据ID删除客户（逻辑删除）
     * @param customerId 会员ID
     * @return 被删除的客户信息
     */
    Customer deleteCustomer(Long customerId);

    /**
     * 修改客户信息
     * @param customer 客户信息
     * @return 更新后的客户信息
     */
    Customer updateCustomer(Customer customer);

    /**
     * 根据ID查询客户
     * @param customerId 会员ID
     * @return 客户信息
     */
    Customer getCustomerById(Long customerId);

    /**
     * 根据手机号查询客户
     * @param mobile 手机号
     * @return 客户信息
     */
    Customer getCustomerByMobile(String mobile);

    /**
     * 分页查询客户列表
     * @param pageNum      页码
     * @param pageSize     每页数量
     * @param customerName 会员姓名（模糊）
     * @param mobile       手机号（模糊）
     * @return 分页结果
     */
    IPage<Customer> getCustomerPage(Integer pageNum, Integer pageSize,
                                    String customerName, String mobile);

}
