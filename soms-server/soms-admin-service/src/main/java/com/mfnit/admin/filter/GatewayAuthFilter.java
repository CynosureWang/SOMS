package com.mfnit.admin.filter;

import com.mfnit.admin.service.PermissionCacheService;
import com.mfnit.common.core.context.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 23:29
 * @Description SOMS Admin Gateway Auth Filter
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayAuthFilter extends OncePerRequestFilter {

    /** 时间戳有效窗口：5 分钟 */
    private static final long MAX_TIMESTAMP_DIFF = 5 * 60 * 1000L;

    private final PermissionCacheService permissionCacheService;

    @Value("${gateway.sign.secret}")
    private String gatewaySignSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            chain.doFilter(request, response);
            return;
        }

        // 1. 校验网关签名，失败不设置 Authentication
        if (!verifyGatewaySign(request, userId)) {
            log.warn("网关签名校验失败，userId={}, uri={}", userId, request.getRequestURI());
            chain.doFilter(request, response);
            return;
        }

        // 2. 签名通过，设置 Authentication
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        List<String> permissions = permissionCacheService.getPermissions(userId);
        for (String perm : permissions) {
            authorities.add(new SimpleGrantedAuthority(perm));
        }

        String roles = UserContext.getRoles();
        if (roles != null && !roles.isEmpty()) {
            for (String role : roles.split(",")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.trim()));
            }
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }

    private boolean verifyGatewaySign(HttpServletRequest request, Long userId) {
        String timestampStr = request.getHeader("X-Gateway-Timestamp");
        String sign = request.getHeader("X-Gateway-Sign");
        String userType = request.getHeader("X-User-Type");
        String roles = request.getHeader("X-Roles");

        if (timestampStr == null || sign == null) {
            return false;
        }

        long timestamp;
        try {
            timestamp = Long.parseLong(timestampStr);
        } catch (NumberFormatException e) {
            return false;
        }

        // 时间戳超窗，拒绝
        if (Math.abs(System.currentTimeMillis() - timestamp) > MAX_TIMESTAMP_DIFF) {
            return false;
        }

        String signContent = userId + "|" + userType + "|"
                + (roles == null ? "" : roles) + "|" + timestamp;
        String expected = hmacSha256(gatewaySignSecret, signContent);

        // 常量时间比较，防时序攻击
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                sign.getBytes(StandardCharsets.UTF_8));
    }

    private String hmacSha256(String secret, String content) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] bytes = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException("HMAC 签名失败", e);
        }
    }
}