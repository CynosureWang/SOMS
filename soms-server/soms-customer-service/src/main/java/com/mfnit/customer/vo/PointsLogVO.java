package com.mfnit.customer.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/22
 * @Description 会员积分流水视图对象
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class PointsLogVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 流水ID
     */
    private Long logId;

    /**
     * 会员ID
     */
    private Long customerId;

    /**
     * 变动类型：1消费获得，2积分使用，3人工调整
     */
    private Integer changeType;

    /**
     * 变动积分（正数为增加，负数为扣减）
     */
    private Integer changePoints;

    /**
     * 变动前积分
     */
    private Integer pointsBefore;

    /**
     * 变动后积分
     */
    private Integer pointsAfter;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务单号
     */
    private String bizId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;
}
