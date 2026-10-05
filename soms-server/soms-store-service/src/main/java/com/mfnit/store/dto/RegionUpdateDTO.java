package com.mfnit.store.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:22
 * @Description SOMS 区域更新DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class RegionUpdateDTO {
    @NotNull(message = "区域ID不能为空")
    private Long regionId;
    private String regionName;
    private Integer sort;
    private Integer status;
    private String remark;
}
