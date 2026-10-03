package com.mfnit.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:09
 * @Description SOMS 订单创建数据传输对象
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class OrderCreateDTO {
    @NotNull(message = "门店ID不能为空")
    private Long storeId;
    private Long customerId;
    @NotNull(message = "订单类型不能为空")
    private Integer orderType;
    @NotEmpty(message = "商品列表不能为空")
    private List<OrderItemDTO> items;
    private String remark;
}