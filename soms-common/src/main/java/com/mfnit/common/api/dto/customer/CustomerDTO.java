package com.mfnit.common.api.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/17 00:31
 * @Description SOMS Customer服务数据传输对象
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CustomerDTO implements Serializable {

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
}
