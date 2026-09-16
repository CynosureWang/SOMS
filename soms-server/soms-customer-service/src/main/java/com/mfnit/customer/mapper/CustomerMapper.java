package com.mfnit.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfnit.customer.entity.Customer;
import org.apache.ibatis.annotations.Param;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/16
 * @Description 客户 Mapper 接口
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface CustomerMapper extends BaseMapper<Customer> {

    /**
     * 新增客户
     * @param customer 客户实体
     * @return 影响行数
     */
    int insertCustomer(Customer customer);

    /**
     * 根据ID逻辑删除客户
     * @param customerId 会员ID
     * @return 影响行数
     */
    int deleteCustomerById(@Param("customerId") Long customerId);

    /**
     * 根据ID更新客户信息
     * @param customer 客户实体
     * @return 影响行数
     */
    int updateCustomerById(Customer customer);

    /**
     * 根据ID查询客户
     * @param customerId 会员ID
     * @return 客户实体
     */
    Customer selectCustomerById(@Param("customerId") Long customerId);

    /**
     * 根据手机号查询客户
     * @param mobile 手机号
     * @return 客户实体
     */
    Customer selectCustomerByMobile(@Param("mobile") String mobile);

    /**
     * 分页查询客户列表（支持按姓名、手机号模糊搜索）
     * @param page         分页参数
     * @param customerName 会员姓名（模糊）
     * @param mobile       手机号（模糊）
     * @return 分页结果
     */
    IPage<Customer> selectCustomerPage(Page<Customer> page,
                                       @Param("customerName") String customerName,
                                       @Param("mobile") String mobile);

    /**
     * 统计客户总数
     * @param customerName 会员姓名（模糊）
     * @param mobile       手机号（模糊）
     * @return 总数
     */
    long countCustomer(@Param("customerName") String customerName,
                       @Param("mobile") String mobile);

}
