package com.mfnit.customer.exception;

import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultCodeMessage;
import com.mfnit.common.api.result.ResultGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/16
 * @Description 全局异常处理器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ResultGenerator.genCodeMsgResult(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid 触发）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", message);
        return ResultGenerator.genCodeMsgResult(
                ResultCodeMessage.PARAM_ERROR.getCode(), message);
    }

    /**
     * 处理请求体不可读异常（如 JSON 格式错误）
     */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleHttpMessageNotReadableException(
            org.springframework.http.converter.HttpMessageNotReadableException e) {
        log.warn("请求体格式错误: {}", e.getMessage());
        return ResultGenerator.genCodeMsgResult(
                ResultCodeMessage.PARAM_ERROR.getCode(), "请求体格式错误，请检查JSON格式");
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleException(Exception e) {
        log.error("系统异常: ", e);
        return ResultGenerator.genCodeMsgResult(
                ResultCodeMessage.SYSTEM_EXCEPTION.getCode(),
                ResultCodeMessage.SYSTEM_EXCEPTION.getMessage());
    }

}
