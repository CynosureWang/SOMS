package com.mfnit.pay.constant;

import lombok.Getter;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:43
 * @Description SOMS 支付场景枚举类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Getter
public enum PayScene {
    BARCODE(1, "付款码"),
    NATIVE(2, "扫码支付"),
    JSAPI(3, "JSAPI"),
    APP(4, "APP支付"),
    ;

    private final int code;
    private final String text;

    PayScene(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
