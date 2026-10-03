package com.mfnit.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mfnit.order.entity.OrderStatusLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:06
 * @Description SOMS 订单状态日志Mapper
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Mapper
public interface OrderStatusLogMapper extends BaseMapper<OrderStatusLog> {
}
