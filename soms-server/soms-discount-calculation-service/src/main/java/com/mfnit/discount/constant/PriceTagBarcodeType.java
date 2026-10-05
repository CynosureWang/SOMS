package com.mfnit.discount.constant;

import lombok.Getter;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 15:11
 * @Description SOMS 价签条码类型
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Getter
public enum PriceTagBarcodeType {
    SCALE(3, "称重自打价签"),
    PRODUCT(4, "成品自打价签"),
    ;

    private final int code;
    private final String text;

    PriceTagBarcodeType(int code, String text) {
        this.code = code;
        this.text = text;
    }
}