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
public enum ActionType {
    DISCOUNT(1, "打折"),
    REDUCE(2, "减钱"),
    FIXED_PRICE(3, "一口价"),
    NTH_DISCOUNT(4, "第N件优惠"),
    BUY_GIFT(5, "买赠"),
    LADDER_REDUCE(6, "阶梯满减"),
    ;

    private final int code;
    private final String text;

    ActionType(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
