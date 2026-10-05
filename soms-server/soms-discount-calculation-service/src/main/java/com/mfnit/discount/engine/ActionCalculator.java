package com.mfnit.discount.engine;

import com.mfnit.discount.entity.Promotion;
import com.mfnit.discount.entity.PromotionAction;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:32
 * @Description SOMS 折扣计算服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface ActionCalculator {

    /** 支持的动作类型 */
    boolean support(Integer actionType);

    /** 计算优惠金额 */
    BigDecimal calculate(Promotion promotion, PromotionAction action, List<InternalCartItem> items);
}
