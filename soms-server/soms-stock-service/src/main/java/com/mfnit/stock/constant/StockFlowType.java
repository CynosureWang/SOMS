package com.mfnit.stock.constant;

import lombok.Getter;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 12:37
 * @Description SOMS 库存流水类型
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Getter
public enum StockFlowType {

    IN(1, "入库"),
    OUT(2, "出库"),
    CHECK_ADJUST(3, "盘点调整"),
    LOCK(4, "销售锁定"),
    UNLOCK(5, "销售解锁"),
    DEDUCT(6, "销售扣减"),
    RETURN_IN(7, "退货入库"),
    LOSS_OUT(8, "报损出库"),
    ;

    private final int code;
    private final String text;

    StockFlowType(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public static String getText(Integer code) {
        if (code == null) {
            return "";
        }
        for (StockFlowType item : values()) {
            if (item.code == code) {
                return item.text;
            }
        }
        return "";
    }

    public static StockFlowType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (StockFlowType item : values()) {
            if (item.code == code) {
                return item;
            }
        }
        return null;
    }
}
