package com.mfnit.store.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:26
 * @Description SOMS 联系人VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class ContactVO {
    private Long id;
    private String contactName;
    private Integer contactRole;
    private String phone;
    private String email;
    private Integer isPrimary;
}