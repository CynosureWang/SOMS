package com.mfnit.store.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:26
 * @Description SOMS 门店营业时间VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class BusinessHoursVO {
    private Long id;
    private Integer dayType;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Integer is24h;
}
