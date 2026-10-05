package com.mfnit.order.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:08
 * @Description SOMS 订单项数据传输对象
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class OrderItemDTO {
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    private Integer barcodeType = 1;
    private String barcode;
    private Long scaleLabelId;
    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "0.001", message = "数量必须大于0")
    private BigDecimal quantity;
}
