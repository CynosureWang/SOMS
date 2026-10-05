package com.mfnit.pay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/16 00:51
 * @Description SOMS Pay Application
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@EnableScheduling
@EnableMethodSecurity(prePostEnabled = true)
@EnableFeignClients(basePackages = "com.mfnit.pay.client")
@EnableDiscoveryClient
@MapperScan("com.mfnit.pay.mapper")
@SpringBootApplication(scanBasePackages = {"com.mfnit.pay", "com.mfnit.common"})
public class PayApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class, args);
    }
}
