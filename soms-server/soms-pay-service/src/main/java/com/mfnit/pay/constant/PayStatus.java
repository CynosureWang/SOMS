package com.mfnit.pay.constant;

import lombok.Getter;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:43
 * @Description SOMS 支付状态枚举类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Getter
public enum PayStatus {
    PENDING(0, "待支付"),
    PAYING(1, "支付中"),
    SUCCESS(2, "成功"),
    FAILED(3, "失败"),
    CLOSED(4, "已关闭"),
    REFUNDED(5, "已退款"),
    ;

    private final int code;
    private final String text;

    PayStatus(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
