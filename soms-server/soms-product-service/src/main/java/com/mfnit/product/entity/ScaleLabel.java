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
 * @CreateTime 2026/10/2 15:57
 * @Description SOMS Scale Label Entity 称重价签实体类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("scale_label")
public class ScaleLabel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "label_id", type = IdType.ASSIGN_ID)
    private Long labelId;

    private String barcode;
    private Long productId;
    private String productName;
    private String specText;

    private BigDecimal price;
    private BigDecimal weight;
    private BigDecimal amount;

    private Long storeId;
    private String scaleCode;
    private Long operatorId;

    private Integer status;
    private Long orderId;
    private String orderNo;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    private LocalDateTime gmtSold;
    private LocalDateTime gmtExpire;
}
