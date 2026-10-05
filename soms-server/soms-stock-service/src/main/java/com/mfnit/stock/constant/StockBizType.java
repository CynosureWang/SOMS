package com.mfnit.stock.constant;

import lombok.Getter;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 13:14
 * @Description SOMS 库存业务类型
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Getter
public enum StockBizType {
    PURCHASE(1, "采购"),
    ORDER(2, "订单"),
    CHECK(3, "盘点"),
    TRANSFER(4, "调拨"),
    RETURN(5, "退货"),
    MANUAL(6, "手工"),
    ;

    private final int code;
    private final String text;

    StockBizType(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
