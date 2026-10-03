package com.mfnit.common.core.handler;

import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultCodeMessage;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.common.core.exception.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 00:18
 * @Description SOMS 全局异常处理器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ForbiddenException.class)
    public Result<Void> handleForbidden(ForbiddenException e) {
        log.warn("无权限：{}", e.getMessage());
        return ResultGenerator.genCodeMsgResult(403, e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return ResultGenerator.genCodeMsgResult(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValid(MethodArgumentNotValidException e) {
        FieldError fe = e.getBindingResult().getFieldError();
        String msg = fe == null ? "参数错误" : fe.getDefaultMessage();
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
}
