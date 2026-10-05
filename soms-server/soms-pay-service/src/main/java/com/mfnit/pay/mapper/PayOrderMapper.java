package com.mfnit.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mfnit.pay.entity.PayOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:47
 * @Description SOMS 支付订单Mapper
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Mapper
public interface PayOrderMapper extends BaseMapper<PayOrder> {
}
