package com.mfnit.order.constant;

import lombok.Getter;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:07
 * @Description SOMS 订单类型
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Getter
public enum OrderType {
    POS(1, "POS"),
    ONLINE(2, "线上"),
    SELF_CHECKOUT(3, "自助收银"),
    DELIVERY(4, "外卖"),
    BOOKING(5, "预订"),
    ;

    private final int code;
    private final String text;

    OrderType(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
