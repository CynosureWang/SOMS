package com.mfnit.discount.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.discount.constant.PromotionType;
import com.mfnit.discount.dto.PromotionActionDTO;
import com.mfnit.discount.dto.PromotionConditionDTO;
import com.mfnit.discount.dto.PromotionCreateDTO;
import com.mfnit.discount.dto.PromotionScopeDTO;
import com.mfnit.discount.entity.Promotion;
import com.mfnit.discount.entity.PromotionAction;
import com.mfnit.discount.entity.PromotionCondition;
import com.mfnit.discount.entity.PromotionScope;
import com.mfnit.discount.mapper.*;
import com.mfnit.discount.service.PromotionService;
import com.mfnit.discount.vo.PromotionDetailVO;
import com.mfnit.discount.vo.PromotionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:41
 * @Description SOMS 优惠券服务实现类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionMapper promotionMapper;
    private final PromotionScopeMapper scopeMapper;
    private final PromotionConditionMapper conditionMapper;
    private final PromotionActionMapper actionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPromotion(PromotionCreateDTO dto) {
        Long count = promotionMapper.selectCount(
                new LambdaQueryWrapper<Promotion>()
                        .eq(Promotion::getPromotionCode, dto.getPromotionCode()));
        if (count > 0) {
            throw new BusinessException("促销编码已存在");
        }

        Promotion p = new Promotion();
        BeanUtils.copyProperties(dto, p);
        p.setStatus(1);
        promotionMapper.insert(p);

        // 范围
        if (dto.getScopes() != null) {
            for (PromotionScopeDTO s : dto.getScopes()) {
                PromotionScope scope = new PromotionScope()
                        .setPromotionId(p.getPromotionId())
                        .setScopeType(s.getScopeType())
                        .setScopeValue(s.getScopeValue())
                        .setStoreId(s.getStoreId())
                        .setMemberLevel(s.getMemberLevel());
                scopeMapper.insert(scope);
            }
        }

        // 条件
        if (dto.getConditions() != null) {
            for (PromotionConditionDTO c : dto.getConditions()) {
                PromotionCondition cond = new PromotionCondition()
                        .setPromotionId(p.getPromotionId())
                        .setConditionType(c.getConditionType())
                        .setOperator(c.getOperator())
                        .setThreshold(c.getThreshold());
                conditionMapper.insert(cond);
            }
        }

        // 动作
        if (dto.getActions() != null) {
            for (PromotionActionDTO a : dto.getActions()) {
                PromotionAction action = new PromotionAction()
                        .setPromotionId(p.getPromotionId())
                        .setActionType(a.getActionType())
                        .setActionValue(a.getActionValue())
                        .setActionExt(a.getActionExt());
                actionMapper.insert(action);
            }
        }

        return p.getPromotionId();
    }

    @Override
    public void updatePromotion(Long promotionId, Integer status) {
        Promotion p = promotionMapper.selectById(promotionId);
        if (p == null) {
            throw new BusinessException("促销不存在");
        }
        p.setStatus(status);
        promotionMapper.updateById(p);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePromotion(Long promotionId) {
        promotionMapper.deleteById(promotionId);
        scopeMapper.delete(new LambdaQueryWrapper<PromotionScope>()
                .eq(PromotionScope::getPromotionId, promotionId));
        conditionMapper.delete(new LambdaQueryWrapper<PromotionCondition>()
                .eq(PromotionCondition::getPromotionId, promotionId));
        actionMapper.delete(new LambdaQueryWrapper<PromotionAction>()
                .eq(PromotionAction::getPromotionId, promotionId));
    }

    @Override
    public PromotionDetailVO getDetail(Long promotionId) {
        Promotion p = promotionMapper.selectById(promotionId);
        if (p == null) {
            throw new BusinessException("促销不存在");
        }
        PromotionDetailVO vo = new PromotionDetailVO();
        BeanUtils.copyProperties(p, vo);

        List<PromotionScope> scopes = scopeMapper.selectList(
                new LambdaQueryWrapper<PromotionScope>()
                        .eq(PromotionScope::getPromotionId, promotionId));
        vo.setScopes(scopes.stream().map(s -> {
            PromotionDetailVO.ScopeVO sv = new PromotionDetailVO.ScopeVO();
            BeanUtils.copyProperties(s, sv);
            return sv;
        }).toList());

        List<PromotionCondition> conditions = conditionMapper.selectList(
                new LambdaQueryWrapper<PromotionCondition>()
                        .eq(PromotionCondition::getPromotionId, promotionId));
        vo.setConditions(conditions.stream().map(c -> {
            PromotionDetailVO.ConditionVO cv = new PromotionDetailVO.ConditionVO();
            BeanUtils.copyProperties(c, cv);
            return cv;
        }).toList());

        List<PromotionAction> actions = actionMapper.selectList(
                new LambdaQueryWrapper<PromotionAction>()
                        .eq(PromotionAction::getPromotionId, promotionId));
        vo.setActions(actions.stream().map(a -> {
            PromotionDetailVO.ActionVO av = new PromotionDetailVO.ActionVO();
            BeanUtils.copyProperties(a, av);
            return av;
        }).toList());

        return vo;
    }

    @Override
    public PageResult<PromotionVO> pagePromotion(Integer pageNum, Integer pageSize,
                                                 Integer status, Integer type) {
        Page<Promotion> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Promotion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, Promotion::getStatus, status)
                .eq(type != null, Promotion::getPromotionType, type)
                .orderByDesc(Promotion::getGmtCreate);
        Page<Promotion> result = promotionMapper.selectPage(page, wrapper);
        List<PromotionVO> records = result.getRecords().stream().map(p -> {
            PromotionVO vo = new PromotionVO();
            BeanUtils.copyProperties(p, vo);
            for (PromotionType t : PromotionType.values()) {
                if (t.getCode() == p.getPromotionType()) {
                    vo.setPromotionTypeName(t.getText());
                    break;
                }
            }
            return vo;
        }).toList();
        return new PageResult<PromotionVO>()
                .setRecords(records)
                .setTotal(result.getTotal())
                .setSize(result.getSize())
                .setCurrent(result.getCurrent())
                .setPages(result.getPages());
    }
}
