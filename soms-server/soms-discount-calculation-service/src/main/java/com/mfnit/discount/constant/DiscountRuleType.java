package com.mfnit.discount.constant;

import lombok.Getter;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 15:10
 * @Description SOMS 折扣规则类型
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Getter
public enum DiscountRuleType {
    TIME_DISCOUNT(1, "时段折扣"),
    EXPIRE_DISCOUNT(2, "临期折扣"),
    ;

    private final int code;
    private final String text;

    DiscountRuleType(int code, String text) {
        this.code = code;
        this.text = text;
    }
}