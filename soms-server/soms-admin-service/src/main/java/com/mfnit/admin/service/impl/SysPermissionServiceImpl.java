package com.mfnit.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mfnit.admin.entity.SysPermission;
import com.mfnit.admin.entity.SysRolePermission;
import com.mfnit.admin.mapper.SysPermissionMapper;
import com.mfnit.admin.mapper.SysRolePermissionMapper;
import com.mfnit.admin.service.SysPermissionService;
import com.mfnit.admin.vo.PermissionTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 03:20
 * @Description SOMS Admin 系统权限服务实现类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl implements SysPermissionService {

    private final SysPermissionMapper sysPermissionMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public List<PermissionTreeVO> getPermissionTree() {
        List<SysPermission> all = sysPermissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>()
                        .eq(SysPermission::getStatus, 1)
                        .orderByAsc(SysPermission::getSort)
        );

        // 按 parentId 分组，根节点的 parentId 为 null 或 0
        Map<Long, List<SysPermission>> childrenMap = all.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getParentId() == null ? 0L : p.getParentId()
                ));

        List<SysPermission> roots = childrenMap.getOrDefault(0L, Collections.emptyList());

        return roots.stream()
                .map(r -> buildTree(r, childrenMap))
                .toList();
    }

    private PermissionTreeVO buildTree(SysPermission node,
                                       Map<Long, List<SysPermission>> childrenMap) {
        PermissionTreeVO vo = new PermissionTreeVO()
                .setPermissionId(node.getPermissionId())
                .setPermissionCode(node.getPermissionCode())
                .setPermissionName(node.getPermissionName())
                .setPermissionType(node.getPermissionType())
                .setParentId(node.getParentId())
                .setPath(node.getPath())
                .setIcon(node.getIcon())
                .setSort(node.getSort());

        List<SysPermission> children = childrenMap.getOrDefault(node.getPermissionId(),
                Collections.emptyList());
        if (!children.isEmpty()) {
            vo.setChildren(children.stream()
                    .map(c -> buildTree(c, childrenMap))
                    .toList());
        }
        return vo;
    }

    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        return sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, roleId)
        ).stream().map(SysRolePermission::getPermissionId).toList();
    }
}
