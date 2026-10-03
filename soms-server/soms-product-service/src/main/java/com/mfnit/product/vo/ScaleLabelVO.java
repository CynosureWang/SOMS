package com.mfnit.product.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:09
 * @Description SOMS 标签信息VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class ScaleLabelVO {
    private Long labelId;
    private String barcode;
    private Long productId;
    private String productName;
    private String specText;
    private BigDecimal price;
    private BigDecimal weight;
    private BigDecimal amount;
    private String unit;
    private Integer status;
    private LocalDateTime gmtCreate;
}