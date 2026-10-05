package com.mfnit.admin.service;

import com.mfnit.admin.vo.PermissionTreeVO;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 03:20
 * @Description SOMS Admin 系统权限服务接口
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface SysPermissionService {

    /** 权限树 */
    List<PermissionTreeVO> getPermissionTree();

    /** 某角色已分配的权限ID */
    List<Long> getPermissionIdsByRoleId(Long roleId);
}
