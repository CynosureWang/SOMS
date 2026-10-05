package com.mfnit.pay.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:44
 * @Description SOMS 支付单号生成器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Component
@RequiredArgsConstructor
public class PayNoGenerator {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String PAY_PREFIX = "MFSOMSP";
    private static final String REFUND_PREFIX = "MFSOMSR";

    private final StringRedisTemplate redisTemplate;

    public String generatePayNo() {
        return generate(PAY_PREFIX);
    }

    public String generateRefundNo() {
        return generate(REFUND_PREFIX);
    }

    private String generate(String prefix) {
        String dateStr = LocalDate.now().format(DATE_FMT);
        String key = "pay:no:seq:" + prefix + ":" + dateStr;

        Long seq = redisTemplate.opsForValue().increment(key);
        if (seq == null) {
            throw new RuntimeException("生成单号失败");
        }
        if (seq == 1) {
            redisTemplate.expire(key, 25, TimeUnit.HOURS);
        }
        if (seq > 9999999L) {
            throw new RuntimeException("当日单号已用完");
        }

        return prefix + dateStr + String.format("%07d", seq);
    }
}