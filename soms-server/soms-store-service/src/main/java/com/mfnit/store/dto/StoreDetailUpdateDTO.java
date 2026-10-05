package com.mfnit.store.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:24
 * @Description SOMS 门店详情更新DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class StoreDetailUpdateDTO {
    @NotNull(message = "门店ID不能为空")
    private Long storeId;
    private List<BusinessHoursDTO> businessHours;
    private List<ContactDTO> contacts;
}