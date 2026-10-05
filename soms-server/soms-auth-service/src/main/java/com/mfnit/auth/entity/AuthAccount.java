package com.mfnit.auth.entity;

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
 * @CreateTime 2026/9/19 03:55
 * @Description SOMS Auth Account Entity
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("auth_account")
public class AuthAccount implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "auth_id", type = IdType.ASSIGN_ID)
    private Long authId;

    private Long userId;
    private Integer userType;
    private String username;
    private String passwordHash;
    private String mobile;
    private String openId;
    private String unionId;
    private Integer status;
    private Integer tokenVersion;
    private LocalDateTime lastLogin;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;

    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}