package com.mfnit.admin.controller;

import com.mfnit.admin.service.UserAuthService;
import com.mfnit.common.api.dto.auth.UserAuthDTO;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 13:42
 * @Description SOMS Admin 系统用户服务Feign客户端
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@RestController
@RequestMapping("/api/v1/admin/internal")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserAuthService userAuthService;

    @GetMapping("/user-auth")
    public Result<UserAuthDTO> getUserAuth(@RequestParam Long userId,
                                           @RequestParam Integer userType) {
        return ResultGenerator.genSuccessResult(
                userAuthService.getUserAuth(userId, userType));
    }
}
