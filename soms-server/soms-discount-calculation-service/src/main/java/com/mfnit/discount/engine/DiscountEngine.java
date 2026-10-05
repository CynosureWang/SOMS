package com.mfnit.discount.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mfnit.discount.constant.ScopeType;
import com.mfnit.discount.dto.CartItemDTO;
import com.mfnit.discount.dto.DiscountContextDTO;
import com.mfnit.discount.entity.Promotion;
import com.mfnit.discount.entity.PromotionAction;
import com.mfnit.discount.entity.PromotionCondition;
import com.mfnit.discount.entity.PromotionScope;
import com.mfnit.discount.mapper.PromotionActionMapper;
import com.mfnit.discount.mapper.PromotionConditionMapper;
import com.mfnit.discount.mapper.PromotionMapper;
import com.mfnit.discount.mapper.PromotionScopeMapper;
import com.mfnit.discount.vo.AppliedPromotionVO;
import com.mfnit.discount.vo.DiscountResultVO;
import com.mfnit.discount.vo.ItemDiscountVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:31
 * @Description SOMS 优惠券计算引擎
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DiscountEngine {

    private final PromotionMapper promotionMapper;
    private final PromotionScopeMapper scopeMapper;
    private final PromotionConditionMapper conditionMapper;
    private final PromotionActionMapper actionMapper;
    private final List<ActionCalculator> calculators;

    public DiscountResultVO calculate(DiscountContextDTO ctx) {
        // 1. 转换为内部模型
        List<InternalCartItem> items = ctx.getItems().stream().map(this::toInternalItem).toList();

        // 2. 初始化每条明细的金额（关键：必须在促销计算前初始化）
        for (InternalCartItem item : items) {
            BigDecimal total = item.getPrice().multiply(item.getQuantity())
                    .setScale(2, RoundingMode.HALF_UP);
            item.setTotalAmount(total);
            item.setDiscountAmount(BigDecimal.ZERO);
            item.setPayAmount(total);
        }

        // 3. 拆分：自打价签商品 和 正常商品
        List<InternalCartItem> normalItems = items.stream()
                .filter(i -> !i.isPriceTagItem())
                .collect(Collectors.toList());

        // 4. 计算正常商品的促销
        List<AppliedPromotionVO> applied = new ArrayList<>();
        if (!normalItems.isEmpty()) {
            applied = applyPromotions(ctx, normalItems);
        }

        // 5. 汇总
        BigDecimal originalAmount = items.stream()
                .map(InternalCartItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discountAmount = items.stream()
                .map(InternalCartItem::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 6. 组装 VO
        DiscountResultVO result = new DiscountResultVO()
                .setOriginalAmount(originalAmount)
                .setDiscountAmount(discountAmount)
                .setPayAmount(originalAmount.subtract(discountAmount))
                .setAppliedPromotions(applied);

        result.setItems(items.stream().map(this::toItemVO).toList());
        return result;
    }

    private InternalCartItem toInternalItem(CartItemDTO dto) {
        InternalCartItem item = new InternalCartItem();
        item.setProductId(dto.getProductId());
        item.setProductName(dto.getProductName());
        item.setBarcode(dto.getBarcode());
        item.setBarcodeType(dto.getBarcodeType());
        item.setCategoryId(dto.getCategoryId());
        item.setBrandId(dto.getBrandId());
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
        // barcode_type = 4 表示成品自打价签，不打折
        item.setPriceTagItem(dto.getBarcodeType() != null && dto.getBarcodeType() == 4);
        return item;
    }

    /**
     * 应用促销
     */
    private List<AppliedPromotionVO> applyPromotions(DiscountContextDTO ctx,
                                                     List<InternalCartItem> items) {
        // 1. 查当前时间生效的促销
        LocalDateTime now = LocalDateTime.now();
        List<Promotion> promotions = promotionMapper.selectList(
                new LambdaQueryWrapper<Promotion>()
                        .eq(Promotion::getStatus, 1)
                        .le(Promotion::getStartTime, now)
                        .ge(Promotion::getEndTime, now)
                        .orderByDesc(Promotion::getPriority));

        List<AppliedPromotionVO> applied = new ArrayList<>();
        Set<String> usedExcludeGroups = new HashSet<>();
        boolean memberDiscountApplied = false;

        // 2. 逐个尝试
        for (Promotion p : promotions) {
            // 2.1 加载范围/条件/动作
            List<PromotionScope> scopes = scopeMapper.selectList(
                    new LambdaQueryWrapper<PromotionScope>()
                            .eq(PromotionScope::getPromotionId, p.getPromotionId()));
            List<PromotionCondition> conditions = conditionMapper.selectList(
                    new LambdaQueryWrapper<PromotionCondition>()
                            .eq(PromotionCondition::getPromotionId, p.getPromotionId()));
            List<PromotionAction> actions = actionMapper.selectList(
                    new LambdaQueryWrapper<PromotionAction>()
                            .eq(PromotionAction::getPromotionId, p.getPromotionId()));

            if (actions.isEmpty()) continue;

            // 2.2 匹配范围
            List<InternalCartItem> matched = matchScope(items, scopes, ctx);
            if (matched.isEmpty()) continue;

            // 2.3 检查条件
            if (!checkConditions(conditions, matched)) continue;

            // 2.4 检查互斥
            if (p.getExcludeGroup() != null
                    && usedExcludeGroups.contains(p.getExcludeGroup())) {
                continue;
            }

            // 2.5 检查叠加
            if (p.getStackable() == 0 && !applied.isEmpty()) {
                continue;
            }

            // 2.6 检查会员折扣叠加
            if (p.getPromotionType() == 3) {
                // 会员折扣
                if (memberDiscountApplied) continue;
                memberDiscountApplied = true;
            } else {
                if (p.getStackWithMember() == 0 && memberDiscountApplied) {
                    continue;
                }
            }

            // 2.7 应用动作
            BigDecimal totalDiscount = BigDecimal.ZERO;
            for (PromotionAction action : actions) {
                ActionCalculator calculator = findCalculator(action.getActionType());
                if (calculator == null) continue;
                BigDecimal d = calculator.calculate(p, action, matched);
                totalDiscount = totalDiscount.add(d);
            }

            if (totalDiscount.compareTo(BigDecimal.ZERO) > 0) {
                // 分摊到匹配的商品
                allocateDiscount(matched, totalDiscount, p.getPromotionId(), p.getPromotionName());

                applied.add(new AppliedPromotionVO()
                        .setPromotionId(p.getPromotionId())
                        .setPromotionName(p.getPromotionName())
                        .setPromotionType(p.getPromotionType())
                        .setDiscountAmount(totalDiscount));

                if (p.getExcludeGroup() != null) {
                    usedExcludeGroups.add(p.getExcludeGroup());
                }
            }
        }

        return applied;
    }

    /**
     * 匹配范围
     */
    private List<InternalCartItem> matchScope(List<InternalCartItem> items,
                                              List<PromotionScope> scopes,
                                              DiscountContextDTO ctx) {
        if (scopes.isEmpty()) return Collections.emptyList();

        List<InternalCartItem> matched = new ArrayList<>();
        for (InternalCartItem item : items) {
            for (PromotionScope scope : scopes) {
                // 门店过滤
                if (scope.getStoreId() != null && !scope.getStoreId().equals(ctx.getStoreId())) {
                    continue;
                }
                // 会员等级过滤
                if (scope.getMemberLevel() != null
                        && !scope.getMemberLevel().equals(ctx.getMemberLevel())) {
                    continue;
                }
                // 范围类型匹配
                if (matchScopeType(scope, item)) {
                    matched.add(item);
                    break;
                }
            }
        }
        return matched;
    }

    private boolean matchScopeType(PromotionScope scope, InternalCartItem item) {
        int type = scope.getScopeType();
        String value = scope.getScopeValue();
        if (type == ScopeType.ALL.getCode()) return true;
        if (type == ScopeType.CATEGORY.getCode()) {
            return item.getCategoryId() != null
                    && String.valueOf(item.getCategoryId()).equals(value);
        }
        if (type == ScopeType.BRAND.getCode()) {
            return item.getBrandId() != null
                    && String.valueOf(item.getBrandId()).equals(value);
        }
        if (type == ScopeType.PRODUCT.getCode()) {
            return String.valueOf(item.getProductId()).equals(value);
        }
        if (type == ScopeType.BARCODE.getCode()) {
            return value != null && value.equals(item.getBarcode());
        }
        return false;
    }

    /**
     * 检查条件
     */
    private boolean checkConditions(List<PromotionCondition> conditions,
                                    List<InternalCartItem> items) {
        for (PromotionCondition c : conditions) {
            if (c.getConditionType() == 1) {
                // 数量
                BigDecimal totalQty = items.stream()
                        .map(InternalCartItem::getQuantity)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (!compare(totalQty, c.getOperator(), c.getThreshold())) {
                    return false;
                }
            } else if (c.getConditionType() == 2) {
                // 金额
                BigDecimal totalAmt = items.stream()
                        .map(i -> i.getPrice().multiply(i.getQuantity()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (!compare(totalAmt, c.getOperator(), c.getThreshold())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean compare(BigDecimal a, String op, BigDecimal b) {
        if (b == null) return true;
        int cmp = a.compareTo(b);
        return switch (op) {
            case ">=" -> cmp >= 0;
            case ">" -> cmp > 0;
            case "<=" -> cmp <= 0;
            case "<" -> cmp < 0;
            case "=" -> cmp == 0;
            default -> true;
        };
    }

    private ActionCalculator findCalculator(Integer actionType) {
        for (ActionCalculator c : calculators) {
            if (c.support(actionType)) return c;
        }
        return null;
    }

    /**
     * 优惠分摊到明细
     */
    private void allocateDiscount(List<InternalCartItem> items,
                                  BigDecimal totalDiscount,
                                  Long promotionId,
                                  String promotionName) {
        BigDecimal totalAmount = items.stream()
                .map(i -> i.getPrice().multiply(i.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) return;

        BigDecimal allocated = BigDecimal.ZERO;
        for (int i = 0; i < items.size(); i++) {
            InternalCartItem item = items.get(i);
            BigDecimal itemAmount = item.getPrice().multiply(item.getQuantity());

            BigDecimal share;
            if (i == items.size() - 1) {
                // 最后一条补差
                share = totalDiscount.subtract(allocated);
            } else {
                share = totalDiscount.multiply(itemAmount)
                        .divide(totalAmount, 2, RoundingMode.HALF_UP);
            }

            item.setDiscountAmount(item.getDiscountAmount().add(share));
            item.setPayAmount(item.getTotalAmount().subtract(item.getDiscountAmount()));
            if (item.getPromotionId() == null) {
                item.setPromotionId(promotionId);
                item.setPromotionName(promotionName);
            }
            allocated = allocated.add(share);
        }
    }

    private ItemDiscountVO toItemVO(InternalCartItem item) {
        return new ItemDiscountVO()
                .setProductId(item.getProductId())
                .setBarcode(item.getBarcode())
                .setBarcodeType(item.getBarcodeType())
                .setProductName(item.getProductName())
                .setOriginalPrice(item.getPrice())
                .setPrice(item.getPrice())
                .setQuantity(item.getQuantity())
                .setTotalAmount(item.getTotalAmount())
                .setDiscountAmount(item.getDiscountAmount())
                .setPayAmount(item.getPayAmount())
                .setPromotionId(item.getPromotionId())
                .setPromotionName(item.getPromotionName());
    }
}
