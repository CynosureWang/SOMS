package com.mfnit.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:24
 * @Description SOMS 联系人DTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
public class ContactDTO {
    private Long id;
    @NotBlank(message = "姓名不能为空")
    private String contactName;
    private Integer contactRole = 1;
    private String phone;
    private String email;
    private Integer isPrimary = 0;
}
