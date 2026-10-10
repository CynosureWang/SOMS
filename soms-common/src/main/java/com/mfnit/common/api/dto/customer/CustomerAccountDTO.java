package com.mfnit.common.api.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/22
 * @Description SOMS Customer服务账户数据传输对象（内部契约）
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CustomerAccountDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 会员ID */
    private Long customerId;

    /** 会员姓名 */
    private String customerName;

    /** 手机号 */
    private String mobile;

    /** 会员等级：1普通会员，2银卡，3金卡 */
    private Integer customerLevel;

    /** 累计消费金额 */
    private BigDecimal totalConsume;

    /** 账户余额 */
    private BigDecimal balance;

    /** 积分余额 */
    private Integer points;

    /** 状态：0禁用，1正常 */
    private Integer status;
}
