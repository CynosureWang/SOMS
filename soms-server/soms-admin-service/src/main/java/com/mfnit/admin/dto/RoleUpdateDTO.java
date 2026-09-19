package com.mfnit.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 03:19
 * @Description SOMS Admin 系统角色更新数据传输对象
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class RoleUpdateDTO {

    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    private String roleName;

    private Integer dataScope;

    private Integer sort;

    private String remark;

    private Integer status;
}