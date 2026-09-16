package com.mfnit.admin.controller;

import com.alibaba.nacos.api.exception.NacosException;
import com.mfnit.admin.service.GetNacosServerStatusService;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultCodeMessage;
import com.mfnit.common.api.result.ResultGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.hypermedia.DiscoveredResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/16 18:10
 * @Description SOMS Admin 获取Nacos中微服务状态控制器
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class GetNacosServerStatusController {

    private final GetNacosServerStatusService getNacosServerStatusService;

    @GetMapping("/nacos/server/status")
    public Result getNacosServerStatus() throws NacosException {
        return ResultGenerator.genSuccessResult(getNacosServerStatusService.nacosServiceDiscovery());
    }
}
