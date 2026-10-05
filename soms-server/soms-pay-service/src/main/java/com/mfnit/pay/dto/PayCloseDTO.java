package com.mfnit.pay.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:51
 * @Description SOMS 付款关闭DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class PayCloseDTO {
    @NotNull(message = "支付单ID不能为空")
    private Long payId;
    private String reason;
}
