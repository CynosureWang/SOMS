package com.mfnit.stock.constant;

import lombok.Getter;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 13:12
 * @Description SOMS 库存模式
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Getter
public enum StockMode {

    STRICT(1, "严格（库存0拒绝）"),
    LOOSE(2, "宽松（允许负库存）"),
    ;

    private final int code;
    private final String text;

    StockMode(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public static StockMode getByCode(Integer code) {
        if (code == null) return null;
        for (StockMode item : values()) {
            if (item.code == code) return item;
        }
        return null;
    }
}
