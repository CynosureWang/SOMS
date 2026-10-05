package com.mfnit.pay.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:51
 * @Description SOMS 付款查询DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class PayQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String payNo;
    private String orderNo;
    private Long storeId;
    private Integer payType;
    private Integer status;
    private LocalDate startDate;
    private LocalDate endDate;
}
