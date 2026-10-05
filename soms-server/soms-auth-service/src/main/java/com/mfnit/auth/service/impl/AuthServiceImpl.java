package com.mfnit.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mfnit.auth.entity.AuthAccount;
import com.mfnit.auth.entity.AuthLoginLog;
import com.mfnit.auth.entity.AuthRefreshToken;
import com.mfnit.auth.mapper.AuthAccountMapper;
import com.mfnit.auth.mapper.AuthLoginLogMapper;
import com.mfnit.auth.mapper.AuthRefreshTokenMapper;
import com.mfnit.auth.service.AuthService;
import com.mfnit.common.api.client.AdminUserFeignClient;
import com.mfnit.common.api.dto.auth.LoginDTO;
import com.mfnit.common.api.dto.auth.TokenDTO;
import com.mfnit.common.api.dto.auth.UserAuthDTO;
import com.mfnit.common.api.enums.AuthAccountEnum;
import com.mfnit.common.api.enums.AuthAccountTypeEnum;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.config.JwtProperties;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.common.core.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 04:05
 * @Description SOMS Auth 认证服务实现类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtProperties jwtProperties;

    private final AuthAccountMapper authAccountMapper;
    private final AuthLoginLogMapper authLoginLogMapper;
    private final AuthRefreshTokenMapper authRefreshTokenMapper;
    private final AdminUserFeignClient adminUserFeignClient;
    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Value("${jwt.admin-expire}")
    private long adminExpire;

    @Value("${jwt.member-expire}")
    private long memberExpire;

    @Value("${jwt.refresh-expire}")
    private long refreshExpire;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenDTO login(LoginDTO dto, String ip, String userAgent) {
        // 1. 查账号（按 userType + username）
        AuthAccount account = authAccountMapper.selectOne(
                new LambdaQueryWrapper<AuthAccount>()
                        .eq(AuthAccount::getUserType, dto.getUserType())
                        .eq(AuthAccount::getUsername, dto.getUsername())
        );
        if (account == null) {
            writeLog(null, dto.getUserType(), ip, userAgent, 0, "账号不存在");
            throw new BusinessException("账号或密码错误");
        }

        // 2. 校验状态
        if (account.getStatus() != AuthAccountEnum.NORMAL.code()) {
            writeLog(account.getAuthId(), dto.getUserType(), ip, userAgent, 0, "账号已锁定或禁用");
            throw new BusinessException("账号已锁定或禁用");
        }

        // 3. 校验密码
        if (!BCrypt.checkpw(dto.getPassword(), account.getPasswordHash())) {
            writeLog(account.getAuthId(), dto.getUserType(), ip, userAgent, 0, "密码错误");
            throw new BusinessException("账号或密码错误");
        }

        // 4. 有效期：管理员/员工 8 小时，会员 7 天
        long expire = (dto.getUserType() == AuthAccountTypeEnum.CUSTOMER.code()) ? memberExpire : adminExpire;

        // 5. 查角色和权限
        UserAuthDTO userAuth = null;
        if (dto.getUserType() == AuthAccountTypeEnum.ADMINISTRATOR.code() || dto.getUserType() == AuthAccountTypeEnum.EMPLOYEE.code()) {
            Result<UserAuthDTO> authResult = adminUserFeignClient.getUserAuth(account.getUserId(), account.getUserType());
            if (authResult.getCode() == 0 && authResult.getData() != null) {
                userAuth = authResult.getData();
            }
        }

        List<String> roles = userAuth != null ? userAuth.getRoles() : Collections.emptyList();
        List<String> permissions = userAuth != null ? userAuth.getPermissions() : Collections.emptyList();
        Integer dataScope = userAuth != null ? userAuth.getDataScope() : 4;
        List<Long> storeIds = userAuth != null ? userAuth.getStoreIds() : Collections.emptyList();

        // 6. 权限缓存到 Redis，key = auth:perms:{userId}
        String permKey = "auth:perms:" + account.getUserId();
        redisTemplate.opsForHash().put(permKey, "permissions", String.join(",", permissions));
        redisTemplate.opsForHash().put(permKey, "dataScope", String.valueOf(dataScope));
        redisTemplate.opsForHash().put(permKey, "storeIds",
                storeIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
        redisTemplate.expire(permKey, expire, TimeUnit.SECONDS);

        // 7. 生成 token（roles 放 token）
        String accessToken = JwtUtils.createToken(
                jwtProperties.getSecret(), jwtIssuer,
                account.getUserId(), account.getUserType(),
                roles, account.getTokenVersion(),
                expire
        );

        // 7. 生成 refresh token
        String refreshToken = JwtUtils.createRefreshToken();
        AuthRefreshToken rt = new AuthRefreshToken()
                .setAuthId(account.getAuthId())
                .setToken(refreshToken)
                .setExpireTime(LocalDateTime.now().plusSeconds(refreshExpire))
                .setStatus(1);
        authRefreshTokenMapper.insert(rt);

        // 8. 更新最后登录时间
        account.setLastLogin(LocalDateTime.now());
        authAccountMapper.updateById(account);

        // 9. 写登录日志
        writeLog(account.getAuthId(), dto.getUserType(), ip, userAgent, 1, null);

        return new TokenDTO()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .setExpiresIn(expire)
                .setUserId(account.getUserId())
                .setUserType(account.getUserType())
                .setRoles(roles);
    }

    @Override
    public void logout(String token) {
        // 把 token 加入 Redis 黑名单，剩余有效期即过期
        var claims = JwtUtils.parseToken(jwtProperties.getSecret(), token);
        if (claims == null) {
            return;
        }
        long ttl = claims.getExpiration().getTime() - System.currentTimeMillis();
        if (ttl > 0) {
            redisTemplate.opsForValue().set("auth:blacklist:" + token, "1", ttl, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public TokenDTO refresh(String refreshToken) {
        AuthRefreshToken rt = authRefreshTokenMapper.selectOne(
                new LambdaQueryWrapper<AuthRefreshToken>()
                        .eq(AuthRefreshToken::getToken, refreshToken)
                        .eq(AuthRefreshToken::getStatus, 1)
        );
        if (rt == null || rt.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("refresh token 无效或已过期");
        }
        AuthAccount account = authAccountMapper.selectById(rt.getAuthId());
        if (account == null || account.getStatus() != 1) {
            throw new BusinessException("账号不可用");
        }

        long expire = (account.getUserType() == AuthAccountTypeEnum.CUSTOMER.code()) ? memberExpire : adminExpire;
        String accessToken = JwtUtils.createToken(
                jwtProperties.getSecret(), jwtIssuer,
                account.getUserId(), account.getUserType(),
                Collections.emptyList(), account.getTokenVersion(),
                expire
        );

        return new TokenDTO()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .setExpiresIn(expire)
                .setUserId(account.getUserId())
                .setUserType(account.getUserType());
    }

    private void writeLog(Long authId, Integer userType, String ip, String userAgent,
                          int success, String failReason) {
        AuthLoginLog log = new AuthLoginLog()
                .setAuthId(authId)
                .setUserType(userType)
                .setLoginType(1)
                .setIp(ip)
                .setUserAgent(userAgent)
                .setSuccess(success)
                .setFailReason(failReason);
        authLoginLogMapper.insert(log);
    }
}
