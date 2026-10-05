package com.mfnit.discount.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:30
 * @Description SOMS 优惠券 VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class CouponVO {
    private Long couponId;
    private String couponNo;
    private Long templateId;
    private String templateName;
    private Integer couponType;
    private BigDecimal threshold;
    private BigDecimal amount;
    private BigDecimal discountRate;
    private Integer status;
    private String statusName;
    private LocalDateTime validStart;
    private LocalDateTime validEnd;
}
