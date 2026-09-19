package com.mfnit.admin;

import com.mfnit.common.api.client.CustomerFeignClient;
import com.mfnit.common.config.JwtProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/16 00:37
 * @Description SOMS Admin Service
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@EnableMethodSecurity(prePostEnabled = true)
@EnableFeignClients(basePackageClasses = {CustomerFeignClient.class})
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.mfnit"})
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
