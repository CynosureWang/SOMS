package com.mfnit.admin.controller;

import com.mfnit.admin.dto.RoleAssignPermissionDTO;
import com.mfnit.admin.dto.RoleCreateDTO;
import com.mfnit.admin.dto.RoleQueryDTO;
import com.mfnit.admin.dto.RoleUpdateDTO;
import com.mfnit.admin.service.SysRoleService;
import com.mfnit.admin.vo.RoleVO;
import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 03:20
 * @Description SOMS Admin 系统角色管理控制器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@RestController
@RequestMapping("/api/v1/admin/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<PageResult<RoleVO>> list(RoleQueryDTO dto) {
        return ResultGenerator.genSuccessResult(sysRoleService.pageRole(dto));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public Result<Long> create(@Valid @RequestBody RoleCreateDTO dto) {
        return ResultGenerator.genSuccessResult(sysRoleService.createRole(dto));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> update(@Valid @RequestBody RoleUpdateDTO dto) {
        sysRoleService.updateRole(dto);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<Void> delete(@PathVariable Long roleId) {
        sysRoleService.deleteRole(roleId);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/assign-permission")
    @PreAuthorize("hasAuthority('system:role:assign')")
    public Result<Void> assignPermission(@Valid @RequestBody RoleAssignPermissionDTO dto) {
        sysRoleService.assignPermissions(dto);
        return ResultGenerator.genSuccessResult();
    }
}
