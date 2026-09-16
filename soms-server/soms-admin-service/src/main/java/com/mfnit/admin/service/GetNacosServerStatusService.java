package com.mfnit.admin.service;

import com.alibaba.nacos.api.exception.NacosException;
import com.mfnit.admin.entity.NacosServiceList;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/16 23:51
 * @Description SOMS Admin 获取Nacos服务器状态服务声明类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface GetNacosServerStatusService {
    NacosServiceList nacosServiceDiscovery() throws NacosException;
}
