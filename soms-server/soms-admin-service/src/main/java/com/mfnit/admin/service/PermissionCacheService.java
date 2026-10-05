package com.mfnit.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 13:48
 * @Description SOMS Admin 系统权限缓存服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Service
@RequiredArgsConstructor
public class PermissionCacheService {

    private final StringRedisTemplate redisTemplate;

    public List<String> getPermissions(Long userId) {
        Object val = redisTemplate.opsForHash().get("auth:perms:" + userId, "permissions");
        if (val == null) {
            return Collections.emptyList();
        }
        String str = val.toString();
        return str.isEmpty() ? Collections.emptyList() : Arrays.asList(str.split(","));
    }

    public Integer getDataScope(Long userId) {
        Object val = redisTemplate.opsForHash().get("auth:perms:" + userId, "dataScope");
        return val == null ? 4 : Integer.valueOf(val.toString());
    }

    public List<Long> getStoreIds(Long userId) {
        Object val = redisTemplate.opsForHash().get("auth:perms:" + userId, "storeIds");
        if (val == null || val.toString().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(val.toString().split(","))
                .map(Long::valueOf).toList();
    }
}
