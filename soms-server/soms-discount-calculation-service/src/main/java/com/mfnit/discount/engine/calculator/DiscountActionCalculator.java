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
 * @CreateTime 2026/10/4 16:32
 * @Description SOMS 折扣计算服务 折扣
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Component
public class DiscountActionCalculator implements ActionCalculator {

    @Override
    public boolean support(Integer actionType) {
        return actionType == ActionType.DISCOUNT.getCode();
    }

    @Override
    public BigDecimal calculate(Promotion promotion, PromotionAction action,
                                List<InternalCartItem> items) {
        BigDecimal rate = action.getActionValue();  // 0.8 = 8折
        BigDecimal total = items.stream()
                .map(i -> i.getPrice().multiply(i.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 优惠 = 总额 × (1 - 折扣率)
        return total.multiply(BigDecimal.ONE.subtract(rate))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
