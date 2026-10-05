package com.mfnit.discount.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:40
 * @Description SOMS Discount Calculation Service Interface 折扣计算服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class PromotionVO {
    private Long promotionId;
    private String promotionCode;
    private String promotionName;
    private Integer promotionType;
    private String promotionTypeName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer priority;
    private Integer stackable;
    private String excludeGroup;
    private Integer status;
    private LocalDateTime gmtCreate;
}