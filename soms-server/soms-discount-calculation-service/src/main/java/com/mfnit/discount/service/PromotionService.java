package com.mfnit.discount.service;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.discount.dto.PromotionCreateDTO;
import com.mfnit.discount.vo.PromotionDetailVO;
import com.mfnit.discount.vo.PromotionVO;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:40
 * @Description SOMS Discount Calculation Service Interface 折扣计算服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface PromotionService {

    Long createPromotion(PromotionCreateDTO dto);

    void updatePromotion(Long promotionId, Integer status);

    void deletePromotion(Long promotionId);

    PromotionDetailVO getDetail(Long promotionId);

    PageResult<PromotionVO> pagePromotion(Integer pageNum, Integer pageSize, Integer status, Integer type);
}