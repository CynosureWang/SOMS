package com.mfnit.common.api.client;

import com.mfnit.common.api.dto.auth.UserAuthDTO;
import com.mfnit.common.api.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 13:40
 * @Description SOMS 系统用户服务Feign客户端
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@FeignClient(value = "service-admin", path = "/api/v1/admin/internal")
public interface AdminUserFeignClient {

    /**
     * 按用户ID和类型查角色和权限
     */
    @GetMapping("/user-auth")
    Result<UserAuthDTO> getUserAuth(@RequestParam("userId") Long userId,
                                    @RequestParam("userType") Integer userType);
}
