package com.mfnit.discount.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.discount.constant.CouponStatus;
import com.mfnit.discount.engine.DiscountEngine;
import com.mfnit.discount.entity.Coupon;
import com.mfnit.discount.entity.CouponTemplate;
import com.mfnit.discount.entity.Promotion;
import com.mfnit.discount.mapper.CouponMapper;
import com.mfnit.discount.mapper.CouponTemplateMapper;
import com.mfnit.discount.dto.DiscountContextDTO;
import com.mfnit.discount.mapper.PromotionMapper;
import com.mfnit.discount.service.CouponService;
import com.mfnit.discount.service.DiscountCalculateService;
import com.mfnit.discount.vo.AppliedPromotionVO;
import com.mfnit.discount.vo.DiscountResultVO;
import com.mfnit.discount.vo.ItemDiscountVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:44
 * @Description SOMS - Discount Calculate Service 优惠计算服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiscountCalculateServiceImpl implements DiscountCalculateService {

    private final DiscountEngine discountEngine;
    private final CouponMapper couponMapper;
    private final CouponTemplateMapper templateMapper;
    private final PromotionMapper promotionMapper;

    @Override
    public DiscountResultVO calculate(DiscountContextDTO ctx) {
        // 1. 先算商品促销
        DiscountResultVO result = discountEngine.calculate(ctx);

        // 2. 再算券
        if (ctx.getCouponIds() != null && !ctx.getCouponIds().isEmpty()) {
            applyCoupons(result, ctx.getCouponIds(), ctx.getCustomerId());
        }

        return result;
    }

    /**
     * 应用优惠券
     * 规则：券基于折后金额（payAmount）计算，且券与券之间不叠加
     */
    private void applyCoupons(DiscountResultVO result, List<Long> couponIds, Long customerId) {
        if (couponIds == null || couponIds.isEmpty()) return;
        if (couponIds.size() > 1) {
            throw new BusinessException("暂不支持叠加多张券");
        }

        Long couponId = couponIds.get(0);
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new BusinessException("券不存在");
        }
        if (customerId != null && !customerId.equals(coupon.getCustomerId())) {
            throw new BusinessException("券不属于当前会员");
        }
        if (coupon.getStatus() != CouponStatus.UNUSED.getCode()) {
            throw new BusinessException("券当前不可用");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getValidStart()) || now.isAfter(coupon.getValidEnd())) {
            throw new BusinessException("券不在有效期内");
        }

        CouponTemplate t = templateMapper.selectById(coupon.getTemplateId());
        if (t == null) {
            throw new BusinessException("券模板不存在");
        }

        // === 检查叠加规则 ===
        boolean hasPromotion = result.getAppliedPromotions() != null
                && !result.getAppliedPromotions().isEmpty();

        if (hasPromotion) {
            // 1. 券侧：不允许和促销叠加
            if (t.getStackWithPromotion() != null && t.getStackWithPromotion() == 0) {
                throw new BusinessException("该券不可与促销活动叠加使用");
            }

            // 2. 促销侧：已应用的促销里，有不允许和券叠加的
            for (AppliedPromotionVO ap : result.getAppliedPromotions()) {
                // 只检查真正的促销（promotionType != 4，类型4是券本身）
                if (ap.getPromotionType() != null && ap.getPromotionType() == 4) {
                    continue;
                }
                Promotion p = promotionMapper.selectById(ap.getPromotionId());
                if (p != null && p.getStackWithCoupon() != null
                        && p.getStackWithCoupon() == 0) {
                    throw new BusinessException(
                            "促销【" + p.getPromotionName() + "】不支持叠加优惠券");
                }
            }
        }

        // === 后续计算不变 ===
        BigDecimal currentPay = result.getPayAmount();
        if (t.getThreshold() != null && currentPay.compareTo(t.getThreshold()) < 0) {
            throw new BusinessException("未达到券使用门槛：" + t.getThreshold());
        }

        BigDecimal couponDiscount = BigDecimal.ZERO;
        if (t.getCouponType() == 1 && t.getAmount() != null) {
            couponDiscount = t.getAmount().min(currentPay);
        } else if (t.getCouponType() == 2 && t.getDiscountRate() != null) {
            couponDiscount = currentPay.multiply(BigDecimal.ONE.subtract(t.getDiscountRate()))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        if (couponDiscount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        allocateCouponDiscount(result.getItems(), couponDiscount);
        result.setDiscountAmount(result.getDiscountAmount().add(couponDiscount));
        result.setPayAmount(result.getOriginalAmount().subtract(result.getDiscountAmount()));

        if (result.getAppliedPromotions() == null) {
            result.setAppliedPromotions(new ArrayList<>());
        }
        result.getAppliedPromotions().add(new AppliedPromotionVO()
                .setPromotionId(coupon.getCouponId())
                .setPromotionName(t.getTemplateName())
                .setPromotionType(4)
                .setDiscountAmount(couponDiscount));
    }

    /**
     * 券优惠按明细金额比例分摊
     */
    private void allocateCouponDiscount(List<ItemDiscountVO> items, BigDecimal totalDiscount) {
        BigDecimal totalPay = items.stream()
                .map(ItemDiscountVO::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPay.compareTo(BigDecimal.ZERO) == 0) return;

        BigDecimal allocated = BigDecimal.ZERO;
        for (int i = 0; i < items.size(); i++) {
            ItemDiscountVO item = items.get(i);
            BigDecimal share;
            if (i == items.size() - 1) {
                share = totalDiscount.subtract(allocated);
            } else {
                share = totalDiscount.multiply(item.getPayAmount())
                        .divide(totalPay, 2, RoundingMode.HALF_UP);
            }
            item.setDiscountAmount(item.getDiscountAmount().add(share));
            item.setPayAmount(item.getPayAmount().subtract(share));
            allocated = allocated.add(share);
        }
    }
}
