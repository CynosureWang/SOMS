package com.mfnit.admin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/17 00:00
 * @Description SOMS Admin Nacos实例信息实体类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class NacosInstance {
    /** IP 地址 */
    private String ip;
    /** 端口 */
    private Integer port;
    /** 分组，如 DEFAULT_GROUP */
    private String group;
    /** 所属服务名 */
    private String serviceName;
}
