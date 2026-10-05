package com.mfnit.order.service;

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
 * @CreateTime 2026/10/3 14:08
 * @Description SOMS 订单号生成器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Component
@RequiredArgsConstructor
public class OrderNoGenerator {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String PREFIX = "SO";

    private final StringRedisTemplate redisTemplate;

    public String generate(Long storeId) {
        String dateStr = LocalDate.now().format(DATE_FMT);
        String storePart = String.format("%04d", storeId % 10000);
        String seqKey = "order:no:seq:" + dateStr + ":" + storePart;

        Long seq = redisTemplate.opsForValue().increment(seqKey);
        if (seq == null) {
            throw new RuntimeException("生成订单号失败");
        }
        if (seq == 1) {
            redisTemplate.expire(seqKey, 25, TimeUnit.HOURS);
        }
        if (seq > 999999L) {
            throw new RuntimeException("当日订单号已用完");
        }

        String body = PREFIX + dateStr + storePart + String.format("%06d", seq);
        return body + calcCheckDigit(body);
    }

    /**
     * 校验位：加权和取模 36
     */
    private char calcCheckDigit(String body) {
        int sum = 0;
        for (int i = 0; i < body.length(); i++) {
            sum += body.charAt(i) * (i % 7 + 1);
        }
        int mod = sum % 36;
        return "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(mod);
    }
}