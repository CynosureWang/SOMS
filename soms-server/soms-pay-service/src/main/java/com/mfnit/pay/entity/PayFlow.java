package com.mfnit.pay.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:41
 * @Description SOMS PayFlow 支付流水实体类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("pay_flow")
public class PayFlow implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "flow_id", type = IdType.ASSIGN_ID)
    private Long flowId;

    private Long payId;
    private String payNo;
    private Integer flowType;
    private Integer channel;
    private String requestData;
    private String responseData;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;
}
