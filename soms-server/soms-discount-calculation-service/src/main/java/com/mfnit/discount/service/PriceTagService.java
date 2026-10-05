package com.mfnit.discount.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.discount.constant.PriceTagBarcodeType;
import com.mfnit.discount.constant.PriceTagStatus;
import com.mfnit.discount.dto.GeneratePriceTagDTO;
import com.mfnit.discount.engine.DiscountRuleEngine;
import com.mfnit.discount.entity.PromotionPriceTag;
import com.mfnit.discount.mapper.PromotionPriceTagMapper;
import com.mfnit.discount.vo.PriceTagVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:35
 * @Description SOMS 价签服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PriceTagService {

    /** 自打价签前缀 */
    private static final String TAG_PREFIX = "26";
    private static final String SEQ_KEY = "discount:tag:seq";

    private final PromotionPriceTagMapper priceTagMapper;
    private final DiscountRuleEngine ruleEngine;
    private final StringRedisTemplate redisTemplate;

    /**
     * 生成自打价签
     * 员工 PDA 扫原条码后调用
     */
    @Transactional(rollbackFor = Exception.class)
    public PriceTagVO generate(GeneratePriceTagDTO dto,
                               Long productId, String productName,
                               Long categoryId, Long brandId,
                               BigDecimal originalPrice, Integer shelfLifeDays) {

        // 1. 判断是否称重
        boolean isWeight = dto.getWeight() != null && dto.getWeight().compareTo(BigDecimal.ZERO) > 0;

        // 2. 计算折扣价
        BigDecimal promoPrice = ruleEngine.calculateDiscountPrice(
                productId, categoryId, brandId, dto.getStoreId(),
                originalPrice, dto.getRemainingShelfLifeDays());

        if (promoPrice == null) {
            throw new BusinessException("当前没有匹配的折扣规则");
        }
        if (promoPrice.compareTo(originalPrice) >= 0) {
            throw new BusinessException("折扣价必须低于原价");
        }

        // 3. 生成条码
        String barcode = generateBarcode();

        // 4. 计算金额
        BigDecimal amount = null;
        if (isWeight) {
            amount = promoPrice.multiply(dto.getWeight())
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            amount = promoPrice;
        }

        // 5. 把同一商品的所有有效自打价签作废（临期折扣档位变更时）
        invalidateOldTags(productId, dto.getStoreId());

        // 6. 写库
        PromotionPriceTag tag = new PromotionPriceTag()
                .setBarcode(barcode)
                .setBarcodeType(isWeight ? PriceTagBarcodeType.SCALE.getCode()
                        : PriceTagBarcodeType.PRODUCT.getCode())
                .setSourceBarcode(dto.getSourceBarcode())
                .setProductId(productId)
                .setProductName(productName)
                .setOriginalPrice(originalPrice)
                .setPromotionPrice(promoPrice)
                .setWeight(dto.getWeight())
                .setAmount(amount)
                .setStoreId(dto.getStoreId())
                .setValidDate(LocalDate.now())
                .setInvalidSource(isWeight ? 1 : 0)  // 称重原条码失效
                .setOperatorId(dto.getOperatorId())
                .setStatus(PriceTagStatus.UNSOLD.getCode())
                .setGmtExpire(isWeight
                        ? LocalDateTime.of(LocalDate.now(), LocalTime.MAX)  // 称重当天闭店
                        : LocalDateTime.now().plusDays(7));                 // 成品 7 天
        priceTagMapper.insert(tag);

        return toVO(tag);
    }

    /**
     * 收银扫到条码时的处理
     */
    public PriceTagVO scan(String barcode) {
        // 1. 先按自打价签条码查
        PromotionPriceTag tag = priceTagMapper.selectOne(
                new LambdaQueryWrapper<PromotionPriceTag>()
                        .eq(PromotionPriceTag::getBarcode, barcode));

        if (tag != null) {
            checkTagValid(tag);
            return toVO(tag);
        }

        // 2. 按原条码查，判断是否被标记失效
        PromotionPriceTag invalid = priceTagMapper.selectInvalidBySourceBarcode(barcode);
        if (invalid != null) {
            throw new BusinessException("该商品禁止销售");
        }

        // 3. 返回 null，让收银走正常商品流程
        return null;
    }

    private void checkTagValid(PromotionPriceTag tag) {
        if (tag.getStatus() == PriceTagStatus.SOLD.getCode()) {
            throw new BusinessException("该价签已售出");
        }
        if (tag.getStatus() == PriceTagStatus.VOID.getCode()) {
            throw new BusinessException("该价签已作废");
        }
        if (tag.getGmtExpire() != null && tag.getGmtExpire().isBefore(LocalDateTime.now())) {
            throw new BusinessException("该价签已过期");
        }
    }

    /**
     * 作废旧价签
     */
    private void invalidateOldTags(Long productId, Long storeId) {
        priceTagMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<PromotionPriceTag>()
                        .eq(PromotionPriceTag::getProductId, productId)
                        .eq(PromotionPriceTag::getStoreId, storeId)
                        .eq(PromotionPriceTag::getStatus, PriceTagStatus.UNSOLD.getCode())
                        .set(PromotionPriceTag::getStatus, PriceTagStatus.VOID.getCode()));
    }

    private String generateBarcode() {
        Long seq = redisTemplate.opsForValue().increment(SEQ_KEY);
        if (seq == null) throw new BusinessException("生成条码失败");
        if (seq > 99999999999L) {
            redisTemplate.opsForValue().set(SEQ_KEY, "0");
            seq = 1L;
        }
        String body = TAG_PREFIX + String.format("%011d", seq);
        return body + calcCheckDigit(body);
    }

    private String calcCheckDigit(String body12) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int d = body12.charAt(i) - '0';
            sum += (i % 2 == 0) ? d : d * 3;
        }
        return String.valueOf((10 - (sum % 10)) % 10);
    }

    private PriceTagVO toVO(PromotionPriceTag t) {
        return new PriceTagVO()
                .setId(t.getId())
                .setBarcode(t.getBarcode())
                .setBarcodeType(t.getBarcodeType())
                .setSourceBarcode(t.getSourceBarcode())
                .setProductId(t.getProductId())
                .setProductName(t.getProductName())
                .setOriginalPrice(t.getOriginalPrice())
                .setPromotionPrice(t.getPromotionPrice())
                .setWeight(t.getWeight())
                .setAmount(t.getAmount())
                .setStatus(t.getStatus())
                .setStatusName(PriceTagStatus.values()[t.getStatus()].getText());
    }
}
