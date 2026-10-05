package com.mfnit.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 03:22
 * @Description SOMS Admin 系统角色分配权限数据传输对象
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class RoleAssignPermissionDTO {

    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    /** 权限ID集合，传空表示清空权限 */
    private List<Long> permissionIds;
}
