package com.mfnit.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:06
 * @Description SOMS 秤码标签生成DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class ScaleLabelGenerateDTO {
    @NotBlank(message = "秤码不能为空")
    private String scaleCode;
    @NotNull(message = "重量不能为空")
    @DecimalMin(value = "0.001", message = "重量必须大于0")
    private BigDecimal weight;
    @NotNull(message = "门店ID不能为空")
    private Long storeId;
    private Long operatorId;
}
