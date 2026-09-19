package com.mfnit.auth.service;

import com.mfnit.common.api.dto.auth.LoginDTO;
import com.mfnit.common.api.dto.auth.TokenDTO;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 04:05
 * @Description SOMS Auth 认证服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface AuthService {

    TokenDTO login(LoginDTO dto, String ip, String userAgent);

    void logout(String token);

    TokenDTO refresh(String refreshToken);
}
