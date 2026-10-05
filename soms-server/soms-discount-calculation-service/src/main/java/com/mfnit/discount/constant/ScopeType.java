package com.mfnit.discount.constant;

import lombok.Getter;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 15:11
 * @Description SOMS 折扣规则作用范围类型
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Getter
public enum ScopeType {
    ALL(1, "全场"),
    CATEGORY(2, "分类"),
    BRAND(3, "品牌"),
    PRODUCT(4, "商品"),
    BARCODE(5, "条码"),
    ;

    private final int code;
    private final String text;

    ScopeType(int code, String text) {
        this.code = code;
        this.text = text;
    }
}