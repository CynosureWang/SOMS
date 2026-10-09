package com.mfnit.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mfnit.admin.dto.RoleCreateDTO;
import com.mfnit.admin.entity.SysAdmin;
import com.mfnit.admin.entity.SysRole;
import com.mfnit.admin.mapper.SysAdminMapper;
import com.mfnit.admin.mapper.SysRoleMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author lris
 * @Version 1.0.0
 * @CreateTime 2026/09/21
 */
@SpringBootTest
public class QueryTest {
    @Autowired
    private SysAdminMapper sysAdminMapper;

    @Test
    public void createRole() {

        QueryWrapper<SysAdmin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", "0");
        System.out.println(sysAdminMapper.selectList(queryWrapper));
    }
}
