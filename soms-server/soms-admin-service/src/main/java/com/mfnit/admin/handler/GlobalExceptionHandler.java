package com.mfnit.admin.handler;

import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultCodeMessage;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.common.core.exception.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 14:02
 * @Description SOMS 全局异常处理器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ForbiddenException.class)
    public Result<Void> handleForbidden(ForbiddenException e) {
        log.warn("无权限：{}", e.getMessage());
        return ResultGenerator.genCodeMsgResult(
                ResultCodeMessage.FAIL.getCode(), e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return ResultGenerator.genCodeMsgResult(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValid(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String msg = fieldError == null ? "参数错误" : fieldError.getDefaultMessage();
        return ResultGenerator.genCodeMsgResult(
                ResultCodeMessage.PARAM_ERROR.getCode(), msg);
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBind(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String msg = fieldError == null ? "参数错误" : fieldError.getDefaultMessage();
        return ResultGenerator.genCodeMsgResult(
                ResultCodeMessage.PARAM_ERROR.getCode(), msg);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return ResultGenerator.genCodeMsgResult(
                ResultCodeMessage.SYSTEM_EXCEPTION.getCode(),
                ResultCodeMessage.SYSTEM_EXCEPTION.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDenied(AccessDeniedException e) {
        log.warn("权限不足：{}", e.getMessage());
        return ResultGenerator.genCodeMsgResult(403, "无权限");
    }

    @ExceptionHandler(AuthenticationException.class)
    public Result<Void> handleAuthentication(AuthenticationException e) {
        log.warn("未认证：{}", e.getMessage());
        return ResultGenerator.genCodeMsgResult(401, "未登录");
    }
}
