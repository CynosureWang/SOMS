package com.mfnit.common.api.dto.auth;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 04:02
 * @Description SOMS - TokenDTO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class TokenDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    /** 有效期（秒） */
    private Long expiresIn;
    private Long userId;
    private Integer userType;
    private List<String> roles;
}
