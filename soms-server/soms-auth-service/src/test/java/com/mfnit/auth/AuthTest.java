package com.mfnit.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 04:07
 * @Description SOMS Auth 测试
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@SpringBootTest
public class AuthTest {
    @Test
    void test(){
        System.out.println(BCrypt.hashpw("admin123", BCrypt.gensalt()));
    }
}
