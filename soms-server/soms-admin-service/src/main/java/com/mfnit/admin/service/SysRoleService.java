package com.mfnit.admin.service;

import com.mfnit.admin.dto.RoleAssignPermissionDTO;
import com.mfnit.admin.dto.RoleCreateDTO;
import com.mfnit.admin.dto.RoleQueryDTO;
import com.mfnit.admin.dto.RoleUpdateDTO;
import com.mfnit.admin.vo.RoleDetailVO;
import com.mfnit.admin.vo.RoleVO;
import com.mfnit.common.api.result.PageResult;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 03:20
 * @Description SOMS Admin 系统角色服务接口
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface SysRoleService {

    Long createRole(RoleCreateDTO dto);

    void updateRole(RoleUpdateDTO dto);

    void deleteRole(Long roleId);

    PageResult<RoleVO> pageRole(RoleQueryDTO dto);

    RoleDetailVO getRoleDetail(Long roleId);

    void assignPermissions(RoleAssignPermissionDTO dto);
}
