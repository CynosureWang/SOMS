package com.mfnit.admin;

import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.alibaba.nacos.api.exception.NacosException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/16 18:18
 * @Description Nacos 微服务发现测试
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@SpringBootTest
public class NacosServerDiscoveryTest {
    @Autowired
    DiscoveryClient discoveryClient;
    @Autowired
    NacosServiceDiscovery nacosServerDiscovery;
    @Test
    void discoveryNacosServer(){
        for(String server : discoveryClient.getServices()){
            System.out.println("Service: " + server);
            List<ServiceInstance> instances = discoveryClient.getInstances(server);
            for(ServiceInstance instance : instances){
                System.out.println("Instance: " + instance.getInstanceId() + ", " + instance.getHost() + ", " + instance.getPort());
            }
        }
    }
    @Test
    void nacosServiceDiscovery() throws NacosException {
        for(String server : nacosServerDiscovery.getServices()){
            System.out.println("Service: " + server);
            List<ServiceInstance> instances = nacosServerDiscovery.getInstances(server);
            for(ServiceInstance instance : instances){
                System.out.println("Instance: " + instance.getInstanceId() + ", " + instance.getHost() + ", " + instance.getPort());
            }
        }
    }
}
