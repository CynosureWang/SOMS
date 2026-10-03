package com.mfnit.product.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:04
 * @Description SOMS 商品品牌创建DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class BrandCreateDTO {
    @NotBlank(message = "品牌编码不能为空")
    private String brandCode;
    @NotBlank(message = "品牌名称不能为空")
    private String brandName;
    private String brandLogo;
    private String firstLetter;
    private Integer sort = 0;
    private String remark;
}