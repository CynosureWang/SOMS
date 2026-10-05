package com.mfnit.store.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:23
 * @Description SOMS 门店更新DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class StoreUpdateDTO {
    @NotNull(message = "门店ID不能为空")
    private Long storeId;
    private String storeName;
    private String shortName;
    private Integer storeType;
    private Integer businessStatus;

    private Long regionId;
    private Long cityId;
    private Long areaId;

    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private BigDecimal areaSize;

    private Long managerId;
    private String managerPhone;
    private String servicePhone;
    private String remark;
}
