package com.mfnit.discount.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mfnit.discount.constant.PriceTagStatus;
import com.mfnit.discount.entity.PromotionPriceTag;
import com.mfnit.discount.mapper.PromotionPriceTagMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:04
 * @Description SOMS 折扣计算服务 - 价签过期任务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PriceTagExpireTask {

    private final PromotionPriceTagMapper priceTagMapper;

    /** 每天凌晨 2 点，把过期的价签作废 */
    @Scheduled(cron = "0 0 2 * * ?")
    public void expirePriceTags() {
        LocalDateTime now = LocalDateTime.now();

        LambdaUpdateWrapper<PromotionPriceTag> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PromotionPriceTag::getStatus, PriceTagStatus.UNSOLD.getCode())
                .lt(PromotionPriceTag::getGmtExpire, now)
                .set(PromotionPriceTag::getStatus, PriceTagStatus.EXPIRED.getCode());

        int count = priceTagMapper.update(null, wrapper);
        if (count > 0) {
            log.info("过期价签自动作废 {} 张", count);
        }
    }
}
