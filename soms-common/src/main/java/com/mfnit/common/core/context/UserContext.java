package com.mfnit.common.core.context;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 04:03
 * @Description TODO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public class UserContext {

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_TYPE = "X-User-Type";
    public static final String HEADER_ROLES = "X-Roles";

    public static Long getUserId() {
        String val = getHeader(HEADER_USER_ID);
        return val == null ? null : Long.valueOf(val);
    }

    public static Integer getUserType() {
        String val = getHeader(HEADER_USER_TYPE);
        return val == null ? null : Integer.valueOf(val);
    }

    public static String getRoles() {
        return getHeader(HEADER_ROLES);
    }

    private static String getHeader(String name) {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        return request.getHeader(name);
    }
}
