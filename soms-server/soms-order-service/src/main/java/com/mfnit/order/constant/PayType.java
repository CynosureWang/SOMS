package com.mfnit.order.constant;

import lombok.Getter;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:07
 * @Description SOMS 支付类型
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Getter
public enum PayType {
    CASH(1, "现金"),
    WECHAT(2, "微信"),
    ALIPAY(3, "支付宝"),
    BANK_CARD(4, "银行卡"),
    MEMBER_BALANCE(5, "会员余额"),
    ;

    private final int code;
    private final String text;

    PayType(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
