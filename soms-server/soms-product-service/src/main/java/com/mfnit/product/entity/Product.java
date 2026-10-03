package com.mfnit.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 15:55
 * @Description SOMS Product Entity 商品实体类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("product")
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "product_id", type = IdType.ASSIGN_ID)
    private Long productId;

    private String productCode;
    private String spuCode;
    private String productName;
    private String shortName;
    private String pinyin;
    private Long categoryId;
    private Long brandId;

    private String specJson;
    private String specText;

    private String barcode;
    private String scaleCode;

    private String unit;
    private Integer isWeight;

    private BigDecimal price;
    private BigDecimal costPrice;

    private Integer stockWarn;
    private Integer shelfLifeDays;

    private String mainImage;
    private String description;

    private Integer status;
    private Integer allowDiscount;
    /** 库存模式：1严格 2宽松 */
    private Integer stockMode;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;

    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}
