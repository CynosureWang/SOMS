package com.mfnit.admin.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 03:23
 * @Description SOMS Admin 系统角色详情视图对象
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class RoleDetailVO {
    private Long roleId;
    private String roleCode;
    private String roleName;
    private Integer userType;
    private Integer dataScope;
    private Integer sort;
    private Integer status;
    private String remark;
    /** 已分配的权限ID列表 */
    private List<Long> permissionIds;
}
