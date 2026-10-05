package com.mfnit.discount.constant;

import lombok.Getter;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 15:11
 * @Description SOMS 优惠券状态
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Getter
public enum CouponStatus {
    UNUSED(0, "未使用"),
    LOCKED(1, "锁定中"),
    USED(2, "已使用"),
    EXPIRED(3, "已过期"),
    VOID(4, "已作废"),
    ;

    private final int code;
    private final String text;

    CouponStatus(int code, String text) {
        this.code = code;
        this.text = text;
    }
}