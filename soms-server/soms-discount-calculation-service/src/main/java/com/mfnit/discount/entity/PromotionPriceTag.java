package com.mfnit.discount.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 15:13
 * @Description SOMS 优惠券价格标签实体类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("promotion_price_tag")
public class PromotionPriceTag implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long promotionId;
    private Long ruleId;
    private String barcode;
    private Integer barcodeType;
    private String sourceBarcode;
    private Long productId;
    private String productName;
    private BigDecimal originalPrice;
    private BigDecimal promotionPrice;
    private BigDecimal weight;
    private BigDecimal amount;
    private Long storeId;
    private LocalDate validDate;
    private Integer invalidSource;
    private Long operatorId;
    private Integer status;
    private Long orderId;
    private String orderNo;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    private LocalDateTime gmtSold;
    private LocalDateTime gmtExpire;
}