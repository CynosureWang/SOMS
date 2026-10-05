package com.mfnit.discount.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:01
 * @Description SOMS 生成价格标签DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class GeneratePriceTagDTO {
    @NotBlank(message = "原条码不能为空")
    private String sourceBarcode;
    @NotNull(message = "门店ID不能为空")
    private Long storeId;
    private Long operatorId;

    /** 称重商品必传：重量kg */
    private BigDecimal weight;

    /** 员工手输的剩余保质期天数（成品临期用） */
    private Integer remainingShelfLifeDays;
}