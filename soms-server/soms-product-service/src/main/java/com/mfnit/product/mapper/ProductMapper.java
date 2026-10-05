package com.mfnit.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mfnit.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 15:58
 * @Description SOMS Product Mapper 商品Mapper
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
