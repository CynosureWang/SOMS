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
public enum PromotionType {
    TIME_SPECIAL(1, "限时特价"),
    CONTINUOUS_SCAN(2, "连扫"),
    MEMBER_DISCOUNT(3, "会员折扣"),
    COUPON(4, "优惠券"),
    BUY_GIFT(5, "买赠"),
    ;

    private final int code;
    private final String text;

    PromotionType(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
