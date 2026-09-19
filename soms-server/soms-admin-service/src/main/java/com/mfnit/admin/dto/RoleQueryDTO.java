package com.mfnit.admin.dto;

import lombok.Data;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 03:19
 * @Description SOMS Admin 系统角色查询数据传输对象
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class RoleQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String roleName;
    private Integer userType;
    private Integer status;
}
