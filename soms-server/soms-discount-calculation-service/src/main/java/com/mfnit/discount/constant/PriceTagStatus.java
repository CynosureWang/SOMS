package com.mfnit.discount.constant;

import lombok.Getter;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 15:11
 * @Description SOMS 价签状态
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Getter
public enum PriceTagStatus {
    UNSOLD(0, "未售"),
    SOLD(1, "已售"),
    VOID(2, "作废"),
    EXPIRED(3, "过期"),
    ;

    private final int code;
    private final String text;

    PriceTagStatus(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
