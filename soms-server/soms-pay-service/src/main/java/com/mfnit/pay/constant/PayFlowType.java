package com.mfnit.pay.constant;

import lombok.Getter;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:43
 * @Description SOMS 支付流程类型枚举类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Getter
public enum PayFlowType {
    CREATE(1, "创建"),
    INITIATE(2, "发起支付"),
    SUCCESS(3, "支付成功"),
    FAILED(4, "支付失败"),
    CLOSED(5, "关闭"),
    REFUND(6, "退款"),
    CALLBACK(7, "回调"),
    ;

    private final int code;
    private final String text;

    PayFlowType(int code, String text) {
        this.code = code;
        this.text = text;
    }
}