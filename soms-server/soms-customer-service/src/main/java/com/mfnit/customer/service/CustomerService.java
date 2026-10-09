package com.mfnit.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mfnit.common.api.dto.customer.CustomerDTO;
import com.mfnit.common.api.result.PageResult;
import com.mfnit.customer.dto.CustomerCreateDTO;
import com.mfnit.customer.dto.CustomerQueryDTO;
import com.mfnit.customer.dto.CustomerUpdateDTO;
import com.mfnit.customer.entity.Customer;
import com.mfnit.customer.vo.CustomerVO;

import java.util.List;

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
     * @param dto 新增客户请求
     * @return 新增后的客户信息（包含生成的ID）
     */
    CustomerVO addCustomer(CustomerCreateDTO dto);

    /**
     * 根据ID删除客户（逻辑删除）
     * @param customerId 会员ID
     * @return 被删除的客户信息
     */
    CustomerVO deleteCustomer(Long customerId);

    /**
     * 修改客户信息
     * @param dto 修改客户请求
     * @return 更新后的客户信息
     */
    CustomerVO updateCustomer(CustomerUpdateDTO dto);

    /**
     * 根据ID查询客户
     * @param customerId 会员ID
     * @return 客户信息
     */
    CustomerVO getCustomerById(Long customerId);

    /**
     * 根据手机号查询客户
     * @param mobile 手机号
     * @return 客户信息
     */
    CustomerVO getCustomerByMobile(String mobile);

    /**
     * 分页查询客户列表
     * @param dto 分页查询条件
     * @return 分页结果
     */
    PageResult<CustomerVO> getCustomerPage(CustomerQueryDTO dto);

    /**
     * 批量查询会员（内部接口，供 order/self-checkout 等服务调用）
     * @param customerIds 会员ID集合
     * @return 会员DTO列表
     */
    List<CustomerDTO> listCustomerDTOs(List<Long> customerIds);

}
