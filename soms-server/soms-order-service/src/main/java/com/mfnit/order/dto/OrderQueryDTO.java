package com.mfnit.order.dto;


import lombok.Data;

import java.time.LocalDate;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:09
 * @Description SOMS 订单查询数据传输对象
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class OrderQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String orderNo;
    private Long storeId;
    private Long customerId;
    private Integer status;
    private Integer orderType;
    private LocalDate startDate;
    private LocalDate endDate;
}
