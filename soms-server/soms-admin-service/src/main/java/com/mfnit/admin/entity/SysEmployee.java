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
 * @CreateTime 2026/9/19
 * @Description SOMS 员工业务信息实体类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("sys_employee")
public class SysEmployee implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 员工ID，与 auth_account.user_id 一致
     */
    @TableId(value = "employee_id", type = IdType.INPUT)
    private Long employeeId;

    /**
     * 工号
     */
    private String employeeNo;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 所属门店ID
     */
    private Long storeId;

    /**
     * 岗位
     */
    private String position;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;

    /**
     * 逻辑删除：0未删除，1已删除
     */
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}