package com.mfnit.admin.handler;

import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 14:18
 * @Description SOMS 订单服务异常处理
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@RestControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(AuthorizationDeniedException.class)
    public Result<Void> handleAuthzDenied(AuthorizationDeniedException e) {
        log.warn("权限不足：{}", e.getMessage());
        return ResultGenerator.genCodeMsgResult(403, "无权限");
    }

    @ExceptionHandler(AuthenticationException.class)
    public Result<Void> handleAuthentication(AuthenticationException e) {
        log.warn("未认证：{}", e.getMessage());
        return ResultGenerator.genCodeMsgResult(401, "未登录");
    }
}