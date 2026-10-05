package com.mfnit.discount.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mfnit.discount.entity.Promotion;
import com.mfnit.discount.mapper.PromotionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:03
 * @Description SOMS 折扣计算服务 - 促销过期任务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionExpireTask {

    private final PromotionMapper promotionMapper;

    /** 每天凌晨 1 点，把已过期的促销停用 */
    @Scheduled(cron = "0 0 1 * * ?")
    public void expirePromotions() {
        LocalDateTime now = LocalDateTime.now();

        LambdaUpdateWrapper<Promotion> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Promotion::getStatus, 1)
                .lt(Promotion::getEndTime, now)
                .set(Promotion::getStatus, 2);

        int count = promotionMapper.update(null, wrapper);
        if (count > 0) {
            log.info("过期促销自动停用 {} 个", count);
        }
    }
}
