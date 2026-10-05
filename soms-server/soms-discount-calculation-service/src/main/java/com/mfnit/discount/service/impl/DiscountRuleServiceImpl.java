package com.mfnit.discount.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.discount.dto.DiscountRuleCreateDTO;
import com.mfnit.discount.entity.DiscountRule;
import com.mfnit.discount.mapper.DiscountRuleMapper;
import com.mfnit.discount.service.DiscountRuleService;
import com.mfnit.discount.vo.DiscountRuleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:43
 * @Description SOMS - Discount Rule Service 优惠规则服务实现类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Service
@RequiredArgsConstructor
public class DiscountRuleServiceImpl implements DiscountRuleService {

    private final DiscountRuleMapper ruleMapper;

    @Override
    public Long createRule(DiscountRuleCreateDTO dto) {
        DiscountRule rule = new DiscountRule();
        BeanUtils.copyProperties(dto, rule);
        rule.setStatus(1);
        ruleMapper.insert(rule);
        return rule.getRuleId();
    }

    @Override
    public void updateRule(Long ruleId, Integer status) {
        DiscountRule rule = ruleMapper.selectById(ruleId);
        if (rule == null) {
            throw new BusinessException("规则不存在");
        }
        rule.setStatus(status);
        ruleMapper.updateById(rule);
    }

    @Override
    public void deleteRule(Long ruleId) {
        ruleMapper.deleteById(ruleId);
    }

    @Override
    public DiscountRuleVO getDetail(Long ruleId) {
        DiscountRule rule = ruleMapper.selectById(ruleId);
        if (rule == null) {
            throw new BusinessException("规则不存在");
        }
        DiscountRuleVO vo = new DiscountRuleVO();
        BeanUtils.copyProperties(rule, vo);
        return vo;
    }

    @Override
    public PageResult<DiscountRuleVO> pageRule(Integer pageNum, Integer pageSize,
                                               Integer ruleType, Integer status) {
        Page<DiscountRule> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DiscountRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ruleType != null, DiscountRule::getRuleType, ruleType)
                .eq(status != null, DiscountRule::getStatus, status)
                .orderByDesc(DiscountRule::getPriority);
        Page<DiscountRule> result = ruleMapper.selectPage(page, wrapper);
        List<DiscountRuleVO> records = result.getRecords().stream().map(r -> {
            DiscountRuleVO vo = new DiscountRuleVO();
            BeanUtils.copyProperties(r, vo);
            return vo;
        }).toList();
        return new PageResult<DiscountRuleVO>()
                .setRecords(records)
                .setTotal(result.getTotal())
                .setSize(result.getSize())
                .setCurrent(result.getCurrent())
                .setPages(result.getPages());
    }
}