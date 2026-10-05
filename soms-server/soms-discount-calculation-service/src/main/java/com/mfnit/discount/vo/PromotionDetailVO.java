package com.mfnit.discount.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:41
 * @Description SOMS - Discount Calculation Service 折扣计算服务VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class PromotionDetailVO {
    private Long promotionId;
    private String promotionCode;
    private String promotionName;
    private Integer promotionType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer priority;
    private Integer stackable;
    private String excludeGroup;
    private Integer stackWithMember;
    private Integer status;
    private java.util.List<ScopeVO> scopes;
    private java.util.List<ConditionVO> conditions;
    private java.util.List<ActionVO> actions;
    private LocalDateTime gmtCreate;

    @Data
    @Accessors(chain = true)
    public static class ScopeVO {
        private Integer scopeType;
        private String scopeValue;
        private Long storeId;
        private Integer memberLevel;
    }

    @Data
    @Accessors(chain = true)
    public static class ConditionVO {
        private Integer conditionType;
        private String operator;
        private java.math.BigDecimal threshold;
    }

    @Data
    @Accessors(chain = true)
    public static class ActionVO {
        private Integer actionType;
        private java.math.BigDecimal actionValue;
        private String actionExt;
    }
}
