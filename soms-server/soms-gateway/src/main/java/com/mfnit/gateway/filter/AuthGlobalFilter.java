package com.mfnit.gateway.filter;

import com.mfnit.common.core.util.JwtUtils;
import com.mfnit.gateway.config.WhitelistProperties;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 13:17
 * @Description SOMS 网关全局认证过滤器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final WhitelistProperties whitelistProperties;
    private final ReactiveStringRedisTemplate redisTemplate;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${gateway.sign.secret}")
    private String gatewaySignSecret;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (isWhitelist(path)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "未登录");
        }
        String token = authHeader.substring(7);

        Claims claims = JwtUtils.parseToken(jwtSecret, token);
        if (claims == null) {
            return unauthorized(exchange, "token 无效或已过期");
        }

        return redisTemplate.hasKey("auth:blacklist:" + token)
                .flatMap(blacklisted -> {
                    if (Boolean.TRUE.equals(blacklisted)) {
                        return unauthorized(exchange, "token 已失效");
                    }

                    Long userId = JwtUtils.getUserId(claims);
                    Integer userType = JwtUtils.getUserType(claims);
                    List<String> roles = JwtUtils.getRoles(claims);
                    String rolesStr = roles == null ? "" : String.join(",", roles);

                    // === 生成网关签名 ===
                    long timestamp = System.currentTimeMillis();
                    String signContent = userId + "|" + userType + "|" + rolesStr + "|" + timestamp;
                    String sign = hmacSha256(gatewaySignSecret, signContent);

                    ServerHttpRequest mutated = request.mutate()
                            .header("X-User-Id", String.valueOf(userId))
                            .header("X-User-Type", String.valueOf(userType))
                            .header("X-Roles", rolesStr)
                            .header("X-Gateway-Timestamp", String.valueOf(timestamp))
                            .header("X-Gateway-Sign", sign)
                            .build();

                    return chain.filter(exchange.mutate().request(mutated).build());
                });
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

    private boolean isWhitelist(String path) {
        return whitelistProperties.getWhitelist().stream()
                .anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String msg) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"code\":401,\"message\":\"" + msg + "\",\"data\":null}";
        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}