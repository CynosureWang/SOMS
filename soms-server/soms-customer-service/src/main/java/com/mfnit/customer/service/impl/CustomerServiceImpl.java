package com.mfnit.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mfnit.common.api.dto.customer.CustomerDTO;
import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.ResultCodeMessage;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.customer.dto.CustomerCreateDTO;
import com.mfnit.customer.dto.CustomerQueryDTO;
import com.mfnit.customer.dto.CustomerUpdateDTO;
import com.mfnit.customer.entity.Customer;
import com.mfnit.customer.mapper.CustomerMapper;
import com.mfnit.customer.service.CustomerService;
import com.mfnit.customer.vo.CustomerVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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
    public CustomerVO addCustomer(CustomerCreateDTO dto) {
        // 校验手机号是否已存在
        Long count = lambdaQuery().eq(Customer::getMobile, dto.getMobile()).count();
        if (count > 0) {
            throw new BusinessException(ResultCodeMessage.PARAM_ERROR.getCode(),
                    "手机号已存在，请勿重复添加");
        }
        // 组装实体并设置默认值
        Customer customer = new Customer()
                .setCustomerName(dto.getCustomerName())
                .setMobile(dto.getMobile())
                .setCustomerLevel(dto.getCustomerLevel() == null ? 1 : dto.getCustomerLevel())
                .setTotalConsume(dto.getTotalConsume() == null ? BigDecimal.ZERO : dto.getTotalConsume())
                .setBalance(dto.getBalance() == null ? BigDecimal.ZERO : dto.getBalance())
                .setPoints(0)
                .setStatus(1);
        save(customer);
        return toVO(customer);
    }

    @Override
    public CustomerVO deleteCustomer(Long customerId) {
        // 校验客户是否存在（getById 自动带逻辑删除过滤）
        Customer customer = getById(customerId);
        if (customer == null) {
            throw new BusinessException(ResultCodeMessage.NOT_FOUND.getCode(),
                    "客户不存在或已被删除");
        }
        // 逻辑删除（@TableLogic 自动转 UPDATE is_deleted = 1）
        removeById(customerId);
        return toVO(customer);
    }

    @Override
    public CustomerVO updateCustomer(CustomerUpdateDTO dto) {
        // 校验客户是否存在
        Customer existing = getById(dto.getCustomerId());
        if (existing == null) {
            throw new BusinessException(ResultCodeMessage.NOT_FOUND.getCode(),
                    "客户不存在，无法修改");
        }
        // 如果修改了手机号，校验新手机号是否与其他客户重复
        if (StringUtils.hasText(dto.getMobile()) && !dto.getMobile().equals(existing.getMobile())) {
            Long count = lambdaQuery().eq(Customer::getMobile, dto.getMobile()).count();
            if (count > 0) {
                throw new BusinessException(ResultCodeMessage.PARAM_ERROR.getCode(),
                        "手机号已被其他客户使用");
            }
        }
        // 仅更新非空字段（空字符串也不写入），gmtModified 由自动填充处理器更新
        Customer update = new Customer()
                .setCustomerId(dto.getCustomerId());
        if (StringUtils.hasText(dto.getCustomerName())) {
            update.setCustomerName(dto.getCustomerName());
        }
        if (StringUtils.hasText(dto.getMobile())) {
            update.setMobile(dto.getMobile());
        }
        update.setCustomerLevel(dto.getCustomerLevel())
                .setTotalConsume(dto.getTotalConsume())
                .setBalance(dto.getBalance());
        updateById(update);
        // 返回更新后的完整数据
        return toVO(getById(dto.getCustomerId()));
    }

    @Override
    public CustomerVO getCustomerById(Long customerId) {
        Customer customer = getById(customerId);
        if (customer == null) {
            throw new BusinessException(ResultCodeMessage.NOT_FOUND.getCode(),
                    "客户不存在");
        }
        return toVO(customer);
    }

    @Override
    public CustomerVO getCustomerByMobile(String mobile) {
        Customer customer = lambdaQuery().eq(Customer::getMobile, mobile).one();
        if (customer == null) {
            throw new BusinessException(ResultCodeMessage.NOT_FOUND.getCode(),
                    "未找到该手机号对应的客户");
        }
        return toVO(customer);
    }

    @Override
    public PageResult<CustomerVO> getCustomerPage(CustomerQueryDTO dto) {
        Page<Customer> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<Customer>()
                .like(StringUtils.hasText(dto.getCustomerName()),
                        Customer::getCustomerName, dto.getCustomerName())
                .like(StringUtils.hasText(dto.getMobile()),
                        Customer::getMobile, dto.getMobile())
                .orderByDesc(Customer::getGmtCreate);

        Page<Customer> result = page(page, wrapper);

        List<CustomerVO> records = result.getRecords().stream()
                .map(this::toVO)
                .toList();

        return new PageResult<CustomerVO>()
                .setRecords(records)
                .setTotal(result.getTotal())
                .setSize(result.getSize())
                .setCurrent(result.getCurrent())
                .setPages(result.getPages());
    }

    @Override
    public List<CustomerDTO> listCustomerDTOs(List<Long> customerIds) {
        if (customerIds == null || customerIds.isEmpty()) {
            return List.of();
        }
        return listByIds(customerIds).stream()
                .map(c -> new CustomerDTO()
                        .setCustomerId(c.getCustomerId())
                        .setCustomerName(c.getCustomerName())
                        .setMobile(c.getMobile())
                        .setCustomerLevel(c.getCustomerLevel()))
                .toList();
    }

    /**
     * 实体转视图对象
     */
    private CustomerVO toVO(Customer customer) {
        CustomerVO vo = new CustomerVO();
        BeanUtils.copyProperties(customer, vo);
        return vo;
    }
}
