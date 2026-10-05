package com.mfnit.order.constant;

import lombok.Getter;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:07
 * @Description SOMS 支付状态
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Getter
public enum PayStatus {
    UNPAID(0, "未支付"),
    PAID(1, "已支付"),
    PARTIAL_REFUND(2, "部分退款"),
    REFUNDED(3, "已退款"),
    ;

    private final int code;
    private final String text;

    PayStatus(int code, String text) {
        this.code = code;
        this.text = text;
    }
}