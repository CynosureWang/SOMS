package com.mfnit.common.core.exception;

import lombok.Getter;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 14:02
 * @Description SOMS 业务异常
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
