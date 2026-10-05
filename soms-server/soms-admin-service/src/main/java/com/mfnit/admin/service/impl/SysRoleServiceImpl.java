package com.mfnit.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfnit.admin.dto.RoleAssignPermissionDTO;
import com.mfnit.admin.dto.RoleCreateDTO;
import com.mfnit.admin.dto.RoleQueryDTO;
import com.mfnit.admin.dto.RoleUpdateDTO;
import com.mfnit.admin.entity.SysRole;
import com.mfnit.admin.entity.SysRolePermission;
import com.mfnit.admin.mapper.SysRoleMapper;
import com.mfnit.admin.mapper.SysRolePermissionMapper;
import com.mfnit.admin.service.SysRoleService;
import com.mfnit.admin.vo.RoleDetailVO;
import com.mfnit.admin.vo.RoleVO;
import com.mfnit.common.api.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 03:20
 * @Description SOMS Admin 系统角色服务实现类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public Long createRole(RoleCreateDTO dto) {
        // 校验角色编码唯一
        Long count = sysRoleMapper.selectCount(
                new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getRoleCode, dto.getRoleCode())
        );
        if (count > 0) {
            throw new RuntimeException("角色编码已存在：" + dto.getRoleCode());
        }

        SysRole role = new SysRole()
                .setRoleCode(dto.getRoleCode())
                .setRoleName(dto.getRoleName())
                .setUserType(dto.getUserType())
                .setDataScope(dto.getDataScope())
                .setSort(dto.getSort())
                .setRemark(dto.getRemark())
                .setStatus(1);

        sysRoleMapper.insert(role);
        return role.getRoleId();
    }

    @Override
    public void updateRole(RoleUpdateDTO dto) {
        SysRole role = sysRoleMapper.selectById(dto.getRoleId());
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        role.setRoleName(dto.getRoleName())
                .setDataScope(dto.getDataScope())
                .setSort(dto.getSort())
                .setRemark(dto.getRemark())
                .setStatus(dto.getStatus());
        sysRoleMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        // 删除角色
        sysRoleMapper.deleteById(roleId);
        // 删除角色-权限关联
        sysRolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, roleId)
        );
    }

    @Override
    public PageResult<RoleVO> pageRole(RoleQueryDTO dto) {
        Page<SysRole> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .like(dto.getRoleName() != null, SysRole::getRoleName, dto.getRoleName())
                .eq(dto.getUserType() != null, SysRole::getUserType, dto.getUserType())
                .eq(dto.getStatus() != null, SysRole::getStatus, dto.getStatus())
                .orderByAsc(SysRole::getSort);

        Page<SysRole> result = sysRoleMapper.selectPage(page, wrapper);

        List<RoleVO> records = result.getRecords().stream()
                .map(this::toVO)
                .toList();

        return new PageResult<RoleVO>()
                .setRecords(records)
                .setTotal(result.getTotal())
                .setSize(result.getSize())
                .setCurrent(result.getCurrent())
                .setPages(result.getPages());
    }

    @Override
    public RoleDetailVO getRoleDetail(Long roleId) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        List<Long> permissionIds = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, roleId)
        ).stream().map(SysRolePermission::getPermissionId).toList();

        RoleDetailVO vo = new RoleDetailVO();
        BeanUtils.copyProperties(role, vo);
        vo.setPermissionIds(permissionIds);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(RoleAssignPermissionDTO dto) {
        SysRole role = sysRoleMapper.selectById(dto.getRoleId());
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        // 先清空
        sysRolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, dto.getRoleId())
        );
        // 再插入
        if (!CollectionUtils.isEmpty(dto.getPermissionIds())) {
            for (Long permissionId : dto.getPermissionIds()) {
                SysRolePermission rp = new SysRolePermission()
                        .setRoleId(dto.getRoleId())
                        .setPermissionId(permissionId);
                sysRolePermissionMapper.insert(rp);
            }
        }
    }

    private RoleVO toVO(SysRole role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }
}