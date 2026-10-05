package com.mfnit.store.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:25
 * @Description SOMS 门店VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class StoreVO {
    private Long storeId;
    private String storeCode;
    private String storeName;
    private String shortName;
    private Integer storeType;
    private Integer businessStatus;
    private String businessStatusName;

    private Long regionId;
    private String regionName;
    private Long cityId;
    private String cityName;
    private Long areaId;
    private String areaName;

    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private BigDecimal areaSize;
    private LocalDate openDate;
    private String managerPhone;
    private String servicePhone;
    private LocalDateTime gmtCreate;
}
