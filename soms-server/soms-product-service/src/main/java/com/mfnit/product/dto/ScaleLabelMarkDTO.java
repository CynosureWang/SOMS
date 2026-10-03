package com.mfnit.product.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:07
 * @Description SOMS 秤码标签标记DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class ScaleLabelMarkDTO {
    @NotEmpty(message = "条码列表不能为空")
    private List<String> barcodes;
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    private String orderNo;
}
