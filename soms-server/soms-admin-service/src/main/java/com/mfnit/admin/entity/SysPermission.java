package com.mfnit.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 03:05
 * @Description SOMS Admin Permission Entity 权限实体类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("sys_permission")
public class SysPermission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "permission_id", type = IdType.ASSIGN_ID)
    private Long permissionId;

    private String permissionCode;
    private String permissionName;
    private Integer permissionType;
    private Long parentId;
    private String path;
    private String icon;
    private Integer sort;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;

    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}
