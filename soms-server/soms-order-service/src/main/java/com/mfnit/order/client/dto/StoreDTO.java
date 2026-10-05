package com.mfnit.order.client.dto;

import lombok.Data;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:45
 * @Description SOMS 下单校验门店是否营业
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class StoreDTO {
    private Long storeId;
    private String storeCode;
    private String storeName;
    private String shortName;
    private Integer businessStatus;
    private String address;
    private String servicePhone;
}