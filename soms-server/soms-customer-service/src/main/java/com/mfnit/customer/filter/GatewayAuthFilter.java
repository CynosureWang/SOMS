package com.mfnit.customer.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/10/12
 * @Description SOMS 客户服务网关认证过滤器（校验网关签名，防止绕过网关直连服务端口访问对外接口）
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayAuthFilter extends OncePerRequestFilter {

    /** 网关签名允许的时间窗口：±5 分钟，防重放 */
    private static final long MAX_TIMESTAMP_DIFF = 5 * 60 * 1000L;

    /** 内部契约接口路径前缀：服务间经 Feign 直连调用（不走网关），不做签名校验 */
    private static final String INTERNAL_PATH_PREFIX = "/api/v1/customer/internal";

    @Value("${gateway.sign.secret}")
    private String gatewaySignSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        // 内部接口：pay/order 等服务经 Nacos 直连调用，不经过网关、不携带网关签名，直接放行
        // （外部经网关访问 /internal/** 已由网关 AuthGlobalFilter 统一 403 拦截）
        if (uri.startsWith(INTERNAL_PATH_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        // 对外接口：必须携带合法的网关签名头，否则拒绝（防止绕过网关直连 8010 端口调用）
        if (!verifyGatewaySign(request)) {
            log.warn("网关签名校验失败或缺失，uri={}, remote={}", uri, request.getRemoteAddr());
            reject(response);
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * 校验网关注入的签名头：HMAC-SHA256(userId|userType|roles|timestamp) 与
     * 网关侧算法一致，时间窗口 ±5 分钟，常量时间比较
     */
    private boolean verifyGatewaySign(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        String userType = request.getHeader("X-User-Type");
        String roles = request.getHeader("X-Roles");
        String timestampStr = request.getHeader("X-Gateway-Timestamp");
        String sign = request.getHeader("X-Gateway-Sign");

        if (userId == null || sign == null || timestampStr == null) {
            return false;
        }

        long timestamp;
        try {
            timestamp = Long.parseLong(timestampStr);
        } catch (NumberFormatException e) {
            return false;
        }

        if (Math.abs(System.currentTimeMillis() - timestamp) > MAX_TIMESTAMP_DIFF) {
            return false;
        }

        String signContent = userId + "|" + userType + "|"
                + (roles == null ? "" : roles) + "|" + timestamp;
        String expected = hmacSha256(gatewaySignSecret, signContent);

        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                sign.getBytes(StandardCharsets.UTF_8));
    }

    /** HMAC-SHA256 签名 */
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

    /** 校验失败统一返回 401，与网关未认证响应格式一致 */
    private void reject(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"code\":401,\"message\":\"请求未经网关认证，禁止直接访问\",\"data\":null}");
    }
}
