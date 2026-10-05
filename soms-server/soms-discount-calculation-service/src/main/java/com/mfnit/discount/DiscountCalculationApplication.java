package com.mfnit.discount;

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
 * @CreateTime 2026/9/16 00:49
 * @Description SOMS Discount Calculation Application
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@EnableScheduling
@EnableMethodSecurity(prePostEnabled = true)
@EnableFeignClients(basePackages = "com.mfnit.discount.client")
@EnableDiscoveryClient
@MapperScan("com.mfnit.discount.mapper")
@SpringBootApplication(scanBasePackages = {"com.mfnit.discount", "com.mfnit.common"})
public class DiscountCalculationApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscountCalculationApplication.class, args);
    }
}
