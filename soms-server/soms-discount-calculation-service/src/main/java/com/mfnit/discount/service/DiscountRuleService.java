package com.mfnit.discount.service;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.discount.dto.DiscountRuleCreateDTO;
import com.mfnit.discount.vo.DiscountRuleVO;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:42
 * @Description SOMS - Discount Rule Service 优惠规则服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface DiscountRuleService {

    Long createRule(DiscountRuleCreateDTO dto);

    void updateRule(Long ruleId, Integer status);

    void deleteRule(Long ruleId);

    DiscountRuleVO getDetail(Long ruleId);

    PageResult<DiscountRuleVO> pageRule(Integer pageNum, Integer pageSize,
                                        Integer ruleType, Integer status);
}