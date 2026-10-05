package com.mfnit.store;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/16 00:54
 * @Description SOMS Store Application
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@EnableMethodSecurity(prePostEnabled = true)
@EnableFeignClients(basePackages = "com.mfnit.store.client")
@EnableDiscoveryClient
@MapperScan("com.mfnit.store.mapper")
@SpringBootApplication(scanBasePackages = {"com.mfnit.store", "com.mfnit.common"})
public class StoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(StoreApplication.class, args);
    }
}
