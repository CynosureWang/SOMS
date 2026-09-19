package com.mfnit.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mfnit.admin.entity.*;
import com.mfnit.admin.mapper.*;
import com.mfnit.admin.service.UserAuthService;
import com.mfnit.common.api.dto.auth.UserAuthDTO;
import com.mfnit.common.api.enums.AuthAccountTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 13:43
 * @Description SOMS Admin 系统用户获取权限信息服务实现类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final SysAdminRoleMapper sysAdminRoleMapper;
    private final SysEmployeeRoleMapper sysEmployeeRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysEmployeeStoreMapper sysEmployeeStoreMapper;

    @Override
    public UserAuthDTO getUserAuth(Long userId, Integer userType) {
        // 1. 查角色ID列表
        List<Long> roleIds;
        List<Long> storeIds = Collections.emptyList();

        if (userType == AuthAccountTypeEnum.ADMINISTRATOR.code()) {
            // 管理员
            roleIds = sysAdminRoleMapper.selectList(
                    new LambdaQueryWrapper<SysAdminRole>()
                            .eq(SysAdminRole::getAdminId, userId)
            ).stream().map(SysAdminRole::getRoleId).toList();
        } else if (userType == AuthAccountTypeEnum.EMPLOYEE.code()) {
            // 员工
            roleIds = sysEmployeeRoleMapper.selectList(
                    new LambdaQueryWrapper<SysEmployeeRole>()
                            .eq(SysEmployeeRole::getEmployeeId, userId)
            ).stream().map(SysEmployeeRole::getRoleId).toList();

            // 员工能管的门店
            storeIds = sysEmployeeStoreMapper.selectList(
                    new LambdaQueryWrapper<SysEmployeeStore>()
                            .eq(SysEmployeeStore::getEmployeeId, userId)
            ).stream().map(SysEmployeeStore::getStoreId).toList();
        } else {
            // 会员不参与 RBAC
            return new UserAuthDTO()
                    .setUserId(userId)
                    .setUserType(userType)
                    .setRoles(Collections.emptyList())
                    .setPermissions(Collections.emptyList())
                    .setDataScope(4);
        }

        if (roleIds.isEmpty()) {
            return new UserAuthDTO()
                    .setUserId(userId)
                    .setUserType(userType)
                    .setRoles(Collections.emptyList())
                    .setPermissions(Collections.emptyList())
                    .setDataScope(4)
                    .setStoreIds(storeIds);
        }

        // 2. 查角色编码
        List<SysRole> roles = sysRoleMapper.selectBatchIds(roleIds);
        List<String> roleCodes = roles.stream().map(SysRole::getRoleCode).toList();

        // 3. 超级管理员拥有所有权限
        if (roleCodes.contains("SUPER_ADMIN")) {
            List<String> allPerms = sysPermissionMapper.selectList(null)
                    .stream().map(SysPermission::getPermissionCode).toList();
            return new UserAuthDTO()
                    .setUserId(userId)
                    .setUserType(userType)
                    .setRoles(roleCodes)
                    .setPermissions(allPerms)
                    .setDataScope(1)
                    .setStoreIds(storeIds);
        }

        // 4. 按角色查权限
        List<Long> permIds = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .in(SysRolePermission::getRoleId, roleIds)
        ).stream().map(SysRolePermission::getPermissionId).distinct().toList();

        List<String> permissionCodes = permIds.isEmpty()
                ? Collections.emptyList()
                : sysPermissionMapper.selectBatchIds(permIds)
                .stream().map(SysPermission::getPermissionCode).toList();

        // 5. 数据范围取角色里最小的 data_scope（数字越小范围越大）
        Integer dataScope = roles.stream()
                .map(SysRole::getDataScope)
                .min(Integer::compareTo)
                .orElse(4);

        return new UserAuthDTO()
                .setUserId(userId)
                .setUserType(userType)
                .setRoles(roleCodes)
                .setPermissions(permissionCodes)
                .setDataScope(dataScope)
                .setStoreIds(storeIds);
    }
}
