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
 * @CreateTime 2026/10/4 16:34
 * @Description SOMS 阶梯优惠计算器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@Component
public class LadderReduceActionCalculator implements ActionCalculator {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean support(Integer actionType) {
        return actionType == ActionType.LADDER_REDUCE.getCode();
    }

    @Override
    public BigDecimal calculate(Promotion promotion, PromotionAction action,
                                List<InternalCartItem> items) {
        try {
            BigDecimal total = items.stream()
                    .map(i -> i.getPrice().multiply(i.getQuantity()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            JsonNode ext = MAPPER.readTree(action.getActionExt());
            JsonNode levels = ext.get("levels");

            // 找满足条件的最高档
            BigDecimal reduce = BigDecimal.ZERO;
            for (JsonNode level : levels) {
                BigDecimal threshold = level.get("threshold").decimalValue();
                BigDecimal amount = level.get("reduce").decimalValue();
                if (total.compareTo(threshold) >= 0 && amount.compareTo(reduce) > 0) {
                    reduce = amount;
                }
            }
            return reduce.min(total).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.error("阶梯满减计算失败", e);
            return BigDecimal.ZERO;
        }
    }
}
