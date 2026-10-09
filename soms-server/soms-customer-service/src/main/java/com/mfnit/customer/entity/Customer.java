package com.mfnit.customer.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/16
 * @Description 客户实体类（数据库表映射，接口出入参请使用 dto / vo）
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
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
     * 积分余额
     */
    private Integer points;

    /**
     * 状态：0禁用，1正常
     */
    private Integer status;

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
