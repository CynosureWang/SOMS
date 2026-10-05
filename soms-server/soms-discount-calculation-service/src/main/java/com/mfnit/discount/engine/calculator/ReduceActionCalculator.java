package com.mfnit.discount.engine.calculator;

import com.mfnit.discount.constant.ActionType;
import com.mfnit.discount.engine.ActionCalculator;
import com.mfnit.discount.engine.InternalCartItem;
import com.mfnit.discount.entity.Promotion;
import com.mfnit.discount.entity.PromotionAction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:33
 * @Description SOMS 减免计算服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Component
public class ReduceActionCalculator implements ActionCalculator {

    @Override
    public boolean support(Integer actionType) {
        return actionType == ActionType.REDUCE.getCode();
    }

    @Override
    public BigDecimal calculate(Promotion promotion, PromotionAction action,
                                List<InternalCartItem> items) {
        BigDecimal reduce = action.getActionValue();
        BigDecimal total = items.stream()
                .map(i -> i.getPrice().multiply(i.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 不能减成负数
        return reduce.min(total).setScale(2, RoundingMode.HALF_UP);
    }
}
