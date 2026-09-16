package com.mfnit.customer.exception;

import com.mfnit.common.api.result.ResultCodeMessage;
import lombok.Getter;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/16
 * @Description 自定义业务异常
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCodeMessage.FAIL.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResultCodeMessage resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

}
