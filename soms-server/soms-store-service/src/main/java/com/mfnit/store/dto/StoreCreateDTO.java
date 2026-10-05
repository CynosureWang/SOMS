package com.mfnit.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:23
 * @Description SOMS 门店创建DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class StoreCreateDTO {
    @NotBlank(message = "门店编码不能为空")
    private String storeCode;
    @NotBlank(message = "门店名称不能为空")
    private String storeName;
    private String shortName;
    private Integer storeType = 1;

    private Long regionId;
    private Long cityId;
    private Long areaId;
    private Long parentStoreId;

    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private BigDecimal areaSize;

    private LocalDate openDate;
    private Long managerId;
    private String managerPhone;
    private String servicePhone;
    private String remark;
}