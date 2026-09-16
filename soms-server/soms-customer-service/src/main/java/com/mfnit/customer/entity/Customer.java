package com.mfnit.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/16
 * @Description 客户实体类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("customer")
public class Customer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会员ID
     */
    @TableId(value = "customer_id", type = IdType.AUTO)
    private Long customerId;

    /**
     * 会员姓名
     */
    @NotBlank(message = "会员姓名不能为空")
    @Size(max = 64, message = "会员姓名长度不能超过64个字符")
    private String customerName;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    /**
     * 会员等级：1普通会员，2银卡，3金卡
     */
    @Min(value = 1, message = "会员等级最小为1")
    @Max(value = 3, message = "会员等级最大为3")
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;

    /**
     * 逻辑删除：0未删除，1已删除
     */
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;

}
