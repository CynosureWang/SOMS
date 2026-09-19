package com.mfnit.discount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/16 00:49
 * @Description SOMS Discount Calculation Application
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.mfnit"})
public class DiscountCalculationApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscountCalculationApplication.class, args);
    }
}
