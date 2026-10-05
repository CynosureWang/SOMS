package com.mfnit.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:22
 * @Description SOMS 区域创建DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class RegionCreateDTO {
    @NotBlank(message = "区域编码不能为空")
    private String regionCode;
    @NotBlank(message = "区域名称不能为空")
    private String regionName;
    @NotNull(message = "区域类型不能为空")
    private Integer regionType;
    private Long parentId = 0L;
    private Integer sort = 0;
    private String remark;
}
