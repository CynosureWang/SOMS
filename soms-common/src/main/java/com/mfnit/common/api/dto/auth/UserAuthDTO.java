package com.mfnit.common.api.dto.auth;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 13:41
 * @Description SOMS 系统用户服务Feign客户端
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class UserAuthDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private Integer userType;
    /** 角色编码 */
    private List<String> roles;
    /** 权限编码 */
    private List<String> permissions;
    /** 数据范围：1全部 2本区域 3本门店 4本人 */
    private Integer dataScope;
    /** 员工能管的门店ID */
    private List<Long> storeIds;
}
