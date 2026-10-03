package com.mfnit.product.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:08
 * @Description SOMS 品牌VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class BrandVO {
    private Long brandId;
    private String brandCode;
    private String brandName;
    private String brandLogo;
    private String firstLetter;
    private Integer sort;
    private Integer status;
    private String remark;
    private LocalDateTime gmtCreate;
}