package com.mfnit.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 02:33
 * @Description SOMS Order 订单服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.mfnit"})
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
