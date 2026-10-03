package com.mfnit.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/17 12:04
 * @Description SOMS Product Service
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
/**
 * @Project SOMS
 * @Description SOMS 商品服务
 */
@EnableMethodSecurity(prePostEnabled = true)
@EnableFeignClients(basePackages = "com.mfnit.common.api.client")
@EnableDiscoveryClient
@MapperScan("com.mfnit.product.mapper")
@SpringBootApplication(scanBasePackages = {"com.mfnit"})
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
