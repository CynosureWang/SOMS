package com.mfnit.order.entity;

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
 * @CreateTime 2026/10/3 14:04
 * @Description SOMS 订单项实体类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("order_item")
public class OrderItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "item_id", type = IdType.ASSIGN_ID)
    private Long itemId;

    private Long orderId;
    private String orderNo;

    private Long productId;
    private String barcode;
    private Long scaleLabelId;

    private String productName;
    private String specText;
    private String unit;
    private String mainImage;
    private Long categoryId;

    private Integer isWeight;
    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal totalAmount;

    private BigDecimal discountAmount;
    private BigDecimal payAmount;
    private Long promotionId;
    private String promotionName;

    private BigDecimal refundQuantity;
    private BigDecimal refundAmount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;
}
