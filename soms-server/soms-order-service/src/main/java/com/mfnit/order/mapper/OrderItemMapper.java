package com.mfnit.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mfnit.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:06
 * @Description SOMS 订单项Mapper
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
