package com.mfnit.auth.controller;

import com.mfnit.auth.service.AuthService;
import com.mfnit.common.api.dto.auth.LoginDTO;
import com.mfnit.common.api.dto.auth.TokenDTO;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 04:06
 * @Description SOMS Auth 认证服务控制器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<TokenDTO> login(@Valid @RequestBody LoginDTO dto,
                                  HttpServletRequest request) {
        String ip = getIp(request);
        String userAgent = request.getHeader("User-Agent");
        return ResultGenerator.genSuccessResult(authService.login(dto, ip, userAgent));
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String auth) {
        if (auth != null && auth.startsWith("Bearer ")) {
            authService.logout(auth.substring(7));
        }
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/refresh")
    public Result<TokenDTO> refresh(@RequestParam String refreshToken) {
        return ResultGenerator.genSuccessResult(authService.refresh(refreshToken));
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
