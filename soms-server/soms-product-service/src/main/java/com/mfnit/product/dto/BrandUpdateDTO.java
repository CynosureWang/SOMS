package com.mfnit.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:05
 * @Description SOMS 品牌更新DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class BrandUpdateDTO {
    @NotNull(message = "品牌ID不能为空")
    private Long brandId;
    private String brandName;
    private String brandLogo;
    private String firstLetter;
    private Integer sort;
    private Integer status;
    private String remark;
}
