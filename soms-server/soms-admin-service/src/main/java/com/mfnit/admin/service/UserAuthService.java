package com.mfnit.admin.service;

import com.mfnit.common.api.dto.auth.UserAuthDTO;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 13:43
 * @Description SOMS Admin 系统用户获取权限服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface UserAuthService {
    UserAuthDTO getUserAuth(Long userId, Integer userType);
}
