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
 * @Description SOMS 固定价格计算服务 一口价
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Component
public class FixedPriceActionCalculator implements ActionCalculator {

    @Override
    public boolean support(Integer actionType) {
        return actionType == ActionType.FIXED_PRICE.getCode();
    }

    @Override
    public BigDecimal calculate(Promotion promotion, PromotionAction action,
                                List<InternalCartItem> items) {
        BigDecimal fixedPrice = action.getActionValue();
        BigDecimal discount = BigDecimal.ZERO;
        for (InternalCartItem item : items) {
            BigDecimal orig = item.getPrice().multiply(item.getQuantity());
            BigDecimal now = fixedPrice.multiply(item.getQuantity());
            if (orig.compareTo(now) > 0) {
                discount = discount.add(orig.subtract(now));
            }
        }
        return discount.setScale(2, RoundingMode.HALF_UP);
    }
}