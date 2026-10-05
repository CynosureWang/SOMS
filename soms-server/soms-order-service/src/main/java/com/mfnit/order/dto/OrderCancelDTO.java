package com.mfnit.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:09
 * @Description SOMS 订单取消数据传输对象
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class OrderCancelDTO {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    private String reason;
}
