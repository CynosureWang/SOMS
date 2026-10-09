package com.mfnit.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mfnit.customer.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/16
 * @Description SOMS Customer Mapper
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
