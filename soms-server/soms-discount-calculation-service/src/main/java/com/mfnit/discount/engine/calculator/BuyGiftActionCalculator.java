package com.mfnit.discount.engine.calculator;

import com.mfnit.discount.constant.ActionType;
import com.mfnit.discount.engine.ActionCalculator;
import com.mfnit.discount.engine.InternalCartItem;
import com.mfnit.discount.entity.Promotion;
import com.mfnit.discount.entity.PromotionAction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:34
 * @Description SOMS Buy Gift Action Calculator 购买送赠品
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Component
public class BuyGiftActionCalculator implements ActionCalculator {

    @Override
    public boolean support(Integer actionType) {
        return actionType == ActionType.BUY_GIFT.getCode();
    }

    @Override
    public BigDecimal calculate(Promotion promotion, PromotionAction action,
                                List<InternalCartItem> items) {
        // 买赠的优惠金额 = 赠品价值，需要后续扩展
        // 这里返回 0，赠品在订单明细里以 0 元记录
        return BigDecimal.ZERO;
    }
}