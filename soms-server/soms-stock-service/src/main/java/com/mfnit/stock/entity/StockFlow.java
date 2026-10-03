package com.mfnit.stock.entity;

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
 * @CreateTime 2026/10/3 12:32
 * @Description SOMS 库存流动实体类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("stock_flow")
public class StockFlow implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "flow_id", type = IdType.ASSIGN_ID)
    private Long flowId;

    private Long storeId;
    private Long productId;
    private String productName;

    private Integer flowType;
    private BigDecimal changeQuantity;
    private BigDecimal changeLocked;
    private BigDecimal beforeQuantity;
    private BigDecimal afterQuantity;
    private BigDecimal beforeLocked;
    private BigDecimal afterLocked;

    private Integer bizType;
    private Long bizId;
    private String bizNo;

    private Long operatorId;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;
}