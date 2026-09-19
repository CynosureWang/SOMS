package com.mfnit.admin.controller;

import com.mfnit.admin.service.SysPermissionService;
import com.mfnit.admin.vo.PermissionTreeVO;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 03:20
 * @Description SOMS Admin 系统权限管理控制器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@RestController
@RequestMapping("/api/v1/admin/permission")
@RequiredArgsConstructor
public class SysPermissionController {

    private final SysPermissionService sysPermissionService;

    /** 权限树 */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:permission:list')")
    public Result<List<PermissionTreeVO>> tree() {
        return ResultGenerator.genSuccessResult(sysPermissionService.getPermissionTree());
    }

    /** 某角色已分配的权限ID */
    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasAuthority('system:permission:list')")
    public Result<List<Long>> getByRole(@PathVariable Long roleId) {
        return ResultGenerator.genSuccessResult(
                sysPermissionService.getPermissionIdsByRoleId(roleId));
    }
}
