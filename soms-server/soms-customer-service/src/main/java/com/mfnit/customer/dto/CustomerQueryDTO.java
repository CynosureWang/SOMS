package com.mfnit.customer.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/22
 * @Description 客户分页查询请求对象
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class CustomerQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 会员姓名（模糊）
     */
    private String customerName;

    /**
     * 手机号（模糊）
     */
    private String mobile;
}
