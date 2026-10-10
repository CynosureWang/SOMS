package com.mfnit.customer.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/22
 * @Description 修改客户请求对象（除会员ID外均为可选，传了才校验、才更新）
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class CustomerUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会员ID
     */
    @NotNull(message = "会员ID不能为空")
    private Long customerId;

    /**
     * 会员姓名（可选，传了才更新）
     */
    @Size(max = 64, message = "会员姓名长度不能超过64个字符")
    private String customerName;

    /**
     * 手机号（可选，传了才更新并校验格式）
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    /**
     * 会员等级：1普通会员，2银卡，3金卡（可选）
     */
    @Min(value = 1, message = "会员等级最小为1")
    @Max(value = 3, message = "会员等级最大为3")
    private Integer customerLevel;

    /**
     * 累计消费金额（可选）
     */
    private BigDecimal totalConsume;

    /**
     * 账户余额（可选）
     */
    private BigDecimal balance;
}
