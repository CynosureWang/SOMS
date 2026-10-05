package com.mfnit.discount.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mfnit.discount.constant.CouponStatus;
import com.mfnit.discount.entity.Coupon;
import com.mfnit.discount.mapper.CouponMapper;
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
 * @Description SOMS 折扣计算服务 - 券过期任务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CouponExpireTask {

    private final CouponMapper couponMapper;

    /** 每小时扫描一次，把过期的未使用券改成已过期 */
    @Scheduled(cron = "0 0 * * * ?")
    public void expireCoupons() {
        LocalDateTime now = LocalDateTime.now();

        LambdaUpdateWrapper<Coupon> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Coupon::getStatus, CouponStatus.UNUSED.getCode())
                .lt(Coupon::getValidEnd, now)
                .set(Coupon::getStatus, CouponStatus.EXPIRED.getCode());

        int count = couponMapper.update(null, wrapper);
        if (count > 0) {
            log.info("过期券自动作废 {} 张", count);
        }
    }
}
