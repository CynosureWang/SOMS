package com.mfnit.discount.engine.calculator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mfnit.discount.constant.ActionType;
import com.mfnit.discount.engine.ActionCalculator;
import com.mfnit.discount.engine.InternalCartItem;
import com.mfnit.discount.entity.Promotion;
import com.mfnit.discount.entity.PromotionAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:33
 * @Description SOMS Nth Discount Action Calculator 第N件优惠
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@Component
public class NthDiscountActionCalculator implements ActionCalculator {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean support(Integer actionType) {
        return actionType == ActionType.NTH_DISCOUNT.getCode();
    }

    @Override
    public BigDecimal calculate(Promotion promotion, PromotionAction action,
                                List<InternalCartItem> items) {
        try {
            BigDecimal rate = action.getActionValue();  // 第N件的折扣率 0.5=半价
            JsonNode ext = MAPPER.readTree(action.getActionExt());
            int n = ext.get("n").asInt();

            BigDecimal discount = BigDecimal.ZERO;
            for (InternalCartItem item : items) {
                int qty = item.getQuantity().intValue();
                int nthCount = qty / n;
                if (nthCount > 0) {
                    BigDecimal unitDiscount = item.getPrice()
                            .multiply(BigDecimal.ONE.subtract(rate));
                    discount = discount.add(unitDiscount.multiply(
                            BigDecimal.valueOf(nthCount)));
                }
            }
            return discount.setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.error("第N件优惠计算失败", e);
            return BigDecimal.ZERO;
        }
    }
}