package com.mfnit.common.core.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 04:00
 * @Description SOMS JWT 工具类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
/**
 * JWT 工具类
 */
public class JwtUtils {

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_USER_TYPE = "userType";
    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_TOKEN_VERSION = "tokenVersion";

    /**
     * 生成 token
     *
     * @param secret       密钥，至少 32 字节
     * @param issuer       签发者
     * @param userId       用户ID
     * @param userType     用户类型
     * @param roles        角色编码列表
     * @param tokenVersion token版本
     * @param expireSeconds 有效期（秒）
     */
    public static String createToken(String secret, String issuer,
                                     Long userId, Integer userType,
                                     List<String> roles, Integer tokenVersion,
                                     long expireSeconds) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .issuer(issuer)
                .subject(String.valueOf(userId))
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_USER_TYPE, userType)
                .claim(CLAIM_ROLES, roles)
                .claim(CLAIM_TOKEN_VERSION, tokenVersion)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expireSeconds * 1000))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 token，失败返回 null
     */
    public static Claims parseToken(String secret, String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 校验 token 是否有效
     */
    public static boolean isValid(String secret, String token) {
        return parseToken(secret, token) != null;
    }

    public static Long getUserId(Claims claims) {
        return claims.get(CLAIM_USER_ID, Long.class);
    }

    public static Integer getUserType(Claims claims) {
        return claims.get(CLAIM_USER_TYPE, Integer.class);
    }

    @SuppressWarnings("unchecked")
    public static List<String> getRoles(Claims claims) {
        return claims.get(CLAIM_ROLES, List.class);
    }

    public static Integer getTokenVersion(Claims claims) {
        return claims.get(CLAIM_TOKEN_VERSION, Integer.class);
    }

    /**
     * 生成 refresh token（随机字符串）
     */
    public static String createRefreshToken() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}
