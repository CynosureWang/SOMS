package com.mfnit.admin.controller;

import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 00:42
 * @Description SOMS Admin Store门店控制器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@RestController
@RequestMapping("/api/v1/admin")
public class StoreController {
    @GetMapping()
    public Result getStoreList() {
        return ResultGenerator.genSuccessResult();
    }
}
