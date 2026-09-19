package com.mfnit.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 13:17
 * @Description SOMS Gateway Whitelist Properties 白名单配置类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Component
@ConfigurationProperties(prefix = "auth")
public class WhitelistProperties {
    private List<String> whitelist = new ArrayList<>();
}