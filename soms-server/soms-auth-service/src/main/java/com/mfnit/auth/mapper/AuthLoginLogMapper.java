package com.mfnit.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mfnit.auth.entity.AuthLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 03:56
 * @Description SOMS Auth Login Log Mapper
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Mapper
public interface AuthLoginLogMapper extends BaseMapper<AuthLoginLog> {
}
