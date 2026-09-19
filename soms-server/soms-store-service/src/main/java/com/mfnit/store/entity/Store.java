package com.mfnit.store.entity;

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
 * @CreateTime 2026/9/19 02:41
 * @Description SOMS Store 实体类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("store")
public class Store implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "store_id", type = IdType.ASSIGN_ID)
    private Long storeId;

    private String storeCode;
    private String storeName;
    private String shortName;
    private Integer storeType;
    private Integer businessStatus;
    private Long regionId;
    private Long parentStoreId;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private BigDecimal areaSize;
    private LocalDate openDate;
    private LocalDate closeDate;
    private Long managerId;
    private String managerPhone;
    private String servicePhone;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;

    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}
