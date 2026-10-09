package com.mfnit.customer.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/22
 * @Description 客户视图对象（接口响应）
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class CustomerVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会员ID
     */
    private Long customerId;

    /**
     * 会员姓名
     */
    private String customerName;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 会员等级：1普通会员，2银卡，3金卡
     */
    private Integer customerLevel;

    /**
     * 累计消费金额
     */
    private BigDecimal totalConsume;

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 更新时间
     */
    private LocalDateTime gmtModified;

    /**
     * 逻辑删除：0未删除，1已删除
     */
    private Integer isDeleted;
}
