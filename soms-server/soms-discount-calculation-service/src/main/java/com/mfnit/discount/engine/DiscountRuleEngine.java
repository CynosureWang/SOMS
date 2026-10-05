package com.mfnit.discount.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mfnit.discount.constant.DiscountRuleType;
import com.mfnit.discount.constant.ScopeType;
import com.mfnit.discount.entity.DiscountRule;
import com.mfnit.discount.mapper.DiscountRuleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:35
 * @Description SOMS 折扣规则引擎
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
/**
 * 折扣规则引擎：根据商品、门店、时间、剩余保质期算折扣价
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DiscountRuleEngine {

    private final DiscountRuleMapper ruleMapper;

    /**
     * 计算商品当前的折扣价
     *
     * @param productId             商品ID
     * @param categoryId            分类ID
     * @param brandId               品牌ID
     * @param storeId               门店ID
     * @param originalPrice         原价
     * @param remainingShelfLifeDays 剩余保质期天数（成品临期用，没有传 null）
     * @return 折扣价，无匹配规则返回 null
     */
    public BigDecimal calculateDiscountPrice(Long productId, Long categoryId, Long brandId,
                                             Long storeId, BigDecimal originalPrice,
                                             Integer remainingShelfLifeDays) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        List<DiscountRule> rules = ruleMapper.selectList(
                new LambdaQueryWrapper<DiscountRule>()
                        .eq(DiscountRule::getStatus, 1)
                        .orderByDesc(DiscountRule::getPriority));

        // 过滤生效的规则
        List<DiscountRule> activeRules = rules.stream()
                .filter(r -> matchStore(r, storeId))
                .filter(r -> matchValidDate(r, today))
                .filter(r -> matchScope(r, productId, categoryId, brandId))
                .filter(r -> matchRuleType(r, currentTime, remainingShelfLifeDays))
                .sorted(Comparator.comparing(DiscountRule::getPriority).reversed())
                .toList();

        if (activeRules.isEmpty()) return null;

        // 取优先级最高的一条
        DiscountRule rule = activeRules.get(0);
        return applyRule(rule, originalPrice);
    }

    private boolean matchStore(DiscountRule rule, Long storeId) {
        return rule.getStoreId() == null || rule.getStoreId().equals(storeId);
    }

    private boolean matchValidDate(DiscountRule rule, LocalDate today) {
        if (rule.getValidStart() != null && today.isBefore(rule.getValidStart())) return false;
        if (rule.getValidEnd() != null && today.isAfter(rule.getValidEnd())) return false;
        return true;
    }

    private boolean matchScope(DiscountRule rule, Long productId, Long categoryId, Long brandId) {
        int type = rule.getScopeType();
        String value = rule.getScopeValue();
        if (type == ScopeType.ALL.getCode()) return true;
        if (type == ScopeType.CATEGORY.getCode()) {
            return categoryId != null && String.valueOf(categoryId).equals(value);
        }
        if (type == ScopeType.BRAND.getCode()) {
            return brandId != null && String.valueOf(brandId).equals(value);
        }
        if (type == ScopeType.PRODUCT.getCode()) {
            return String.valueOf(productId).equals(value);
        }
        return false;
    }

    private boolean matchRuleType(DiscountRule rule, LocalTime now, Integer shelfLifeDays) {
        if (rule.getRuleType() == DiscountRuleType.TIME_DISCOUNT.getCode()) {
            LocalTime start = rule.getTimeStart();
            LocalTime end = rule.getTimeEnd();
            if (start == null || end == null) return false;
            if (start.isBefore(end)) {
                return !now.isBefore(start) && !now.isAfter(end);
            } else {
                // 跨天
                return !now.isBefore(start) || !now.isAfter(end);
            }
        }
        if (rule.getRuleType() == DiscountRuleType.EXPIRE_DISCOUNT.getCode()) {
            if (shelfLifeDays == null) return false;
            Integer min = rule.getShelfLifeMin();
            Integer max = rule.getShelfLifeMax();
            if (min != null && shelfLifeDays < min) return false;
            if (max != null && shelfLifeDays > max) return false;
            return true;
        }
        return false;
    }

    private BigDecimal applyRule(DiscountRule rule, BigDecimal originalPrice) {
        if (rule.getDiscountRate() != null) {
            return originalPrice.multiply(rule.getDiscountRate())
                    .setScale(2, RoundingMode.HALF_UP);
        }
        if (rule.getReduceAmount() != null) {
            BigDecimal result = originalPrice.subtract(rule.getReduceAmount());
            return result.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        }
        if (rule.getFixedPrice() != null) {
            return rule.getFixedPrice();
        }
        return null;
    }
}
