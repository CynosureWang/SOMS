package com.mfnit.store.dto;

import lombok.Data;

import java.time.LocalTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:24
 * @Description SOMS 门店营业时间DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class BusinessHoursDTO {
    private Integer dayType;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Integer is24h = 0;
}