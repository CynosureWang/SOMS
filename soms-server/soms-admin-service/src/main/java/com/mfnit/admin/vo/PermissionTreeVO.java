package com.mfnit.admin.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 03:19
 * @Description SOMS Admin 系统权限树视图对象
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class PermissionTreeVO {
    private Long permissionId;
    private String permissionCode;
    private String permissionName;
    private Integer permissionType;
    private Long parentId;
    private String path;
    private String icon;
    private Integer sort;
    private List<PermissionTreeVO> children;
}
