package com.mfnit.pay.constant;

import lombok.Getter;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:44
 * @Description SOMS 支付渠道枚举类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Getter
public enum PayChannel {
    WECHAT(1, "微信"),
    ALIPAY(2, "支付宝"),
    ;

    private final int code;
    private final String text;

    PayChannel(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
