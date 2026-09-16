package com.mfnit.admin.service.impl;

import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.alibaba.nacos.api.exception.NacosException;
import com.mfnit.admin.entity.NacosInstance;
import com.mfnit.admin.entity.NacosService;
import com.mfnit.admin.entity.NacosServiceList;
import com.mfnit.admin.service.GetNacosServerStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/16 23:52
 * @Description SOMS Admin 获取Nacos服务器状态服务实现类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Service
@RequiredArgsConstructor
public class GetNacosServerStatusServiceImpl implements GetNacosServerStatusService {

    private final NacosServiceDiscovery nacosServerDiscovery;

    /**
     * 从 Nacos 拉取所有服务及其实例，组装成 NacosServiceList
     */
    public NacosServiceList nacosServiceDiscovery() throws NacosException {
        List<NacosService> serviceList = new ArrayList<>();

        for (String server : nacosServerDiscovery.getServices()) {
            System.out.println("Service: " + server);

            NacosService nacosService = new NacosService()
                    .setServiceName(server)
                    .setInstances(new ArrayList<>());

            List<ServiceInstance> instances = nacosServerDiscovery.getInstances(server);
            for (ServiceInstance instance : instances) {
                System.out.println("Instance: " + instance.getInstanceId()
                        + ", " + instance.getHost() + ", " + instance.getPort());

                NacosInstance nacosInstance = new NacosInstance()
                        .setIp(instance.getHost())
                        .setPort(instance.getPort())
                        .setGroup(resolveGroup(instance.getInstanceId()))
                        .setServiceName(server);

                nacosService.getInstances().add(nacosInstance);
            }

            serviceList.add(nacosService);
        }

        return new NacosServiceList().setServices(serviceList);
    }

    /**
     * 从 instanceId 中解析 group。
     * instanceId Example：169.254.213.131#8070##DEFAULT_GROUP@@service-store
     */
    private String resolveGroup(String instanceId) {
        if (instanceId == null || instanceId.isEmpty()) {
            return null;
        }
        String[] groupAndService = instanceId.split("@@", 2);
        if (groupAndService.length < 2) {
            return null;
        }
        String[] hashParts = groupAndService[0].split("#");
        if (hashParts.length >= 4) {
            return hashParts[3].trim();
        }
        return null;
    }
}
