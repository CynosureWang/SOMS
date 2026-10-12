package com.mfnit.customer.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mfnit.common.api.dto.customer.CustomerDTO;
import com.mfnit.common.api.result.ResultCodeMessage;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.customer.entity.Customer;
import com.mfnit.customer.mapper.CustomerMapper;
import com.mfnit.customer.service.CustomerService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/16
 * @Description 客户服务实现类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Override
    public Customer addCustomer(Customer customer) {
        // 校验手机号是否已存在
        Customer existing = baseMapper.selectCustomerByMobile(customer.getMobile());
        if (existing != null) {
            throw new BusinessException(ResultCodeMessage.PARAM_ERROR.getCode(),
                    "手机号已存在，请勿重复添加");
        }
        // 设置默认值
        if (customer.getCustomerLevel() == null) {
            customer.setCustomerLevel(1);
        }
        if (customer.getTotalConsume() == null) {
            customer.setTotalConsume(BigDecimal.ZERO);
        }
        if (customer.getBalance() == null) {
            customer.setBalance(BigDecimal.ZERO);
        }
        if (customer.getPoints() == null) {
            customer.setPoints(0);
        }
        if (customer.getStatus() == null) {
            customer.setStatus(1);
        }
        baseMapper.insertCustomer(customer);
        return customer;
    }

    @Override
    public Customer deleteCustomer(Long customerId) {
        // 校验客户是否存在
        Customer customer = baseMapper.selectCustomerById(customerId);
        if (customer == null) {
            throw new BusinessException(ResultCodeMessage.NOT_FOUND.getCode(),
                    "客户不存在或已被删除");
        }
        baseMapper.deleteCustomerById(customerId);
        return customer;
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        // 校验客户是否存在
        Customer existing = baseMapper.selectCustomerById(customer.getCustomerId());
        if (existing == null) {
            throw new BusinessException(ResultCodeMessage.NOT_FOUND.getCode(),
                    "客户不存在，无法修改");
        }
        // 如果修改了手机号，校验新手机号是否与其他客户重复
        if (customer.getMobile() != null && !customer.getMobile().equals(existing.getMobile())) {
            Customer byMobile = baseMapper.selectCustomerByMobile(customer.getMobile());
            if (byMobile != null) {
                throw new BusinessException(ResultCodeMessage.PARAM_ERROR.getCode(),
                        "手机号已被其他客户使用");
            }
        }
        baseMapper.updateCustomerById(customer);
        // 返回更新后的完整数据
        return baseMapper.selectCustomerById(customer.getCustomerId());
    }

    @Override
    public Customer getCustomerById(Long customerId) {
        Customer customer = baseMapper.selectCustomerById(customerId);
        if (customer == null) {
            throw new BusinessException(ResultCodeMessage.NOT_FOUND.getCode(),
                    "客户不存在");
        }
        return customer;
    }

    @Override
    public Customer getCustomerByMobile(String mobile) {
        Customer customer = baseMapper.selectCustomerByMobile(mobile);
        if (customer == null) {
            throw new BusinessException(ResultCodeMessage.NOT_FOUND.getCode(),
                    "未找到该手机号对应的客户");
        }
        return customer;
    }

    @Override
    public IPage<Customer> getCustomerPage(Integer pageNum, Integer pageSize,
                                           String customerName, String mobile) {
        Page<Customer> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectCustomerPage(page, customerName, mobile);
    }

    /**
     * 批量查询客户简要信息（供内部服务 Feign 调用）
     * <p>只返回 DTO 中的必要字段（ID/姓名/手机号/会员等级），避免客户完整实体（余额、积分等）外泄给调用方
     *
     * @param customerIds 客户 ID 列表，允许为 null 或空
     * @return 客户 DTO 列表；传入为空时返回空列表，不存在的 ID 会被静默跳过
     */
    @Override
    public List<CustomerDTO> listCustomerDTOs(List<Long> customerIds) {
        // 空入参直接返回空列表，避免 selectBatchIds 拼出非法 IN () 条件
        if (customerIds == null || customerIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 批量查询后转换为 DTO，仅映射对外暴露的四个字段
        return baseMapper.selectBatchIds(customerIds).stream()
                .map(customer -> new CustomerDTO()
                        .setCustomerId(customer.getCustomerId())
                        .setCustomerName(customer.getCustomerName())
                        .setMobile(customer.getMobile())
                        .setCustomerLevel(customer.getCustomerLevel()))
                .toList();
    }

}
