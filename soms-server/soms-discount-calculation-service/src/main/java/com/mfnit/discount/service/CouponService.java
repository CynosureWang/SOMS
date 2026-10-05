package com.mfnit.discount.service;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.discount.dto.CouponIssueDTO;
import com.mfnit.discount.dto.CouponRevokeDTO;
import com.mfnit.discount.dto.CouponTemplateCreateDTO;
import com.mfnit.discount.vo.CouponTemplateVO;
import com.mfnit.discount.vo.CouponVO;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:39
 * @Description SOMS 优惠券服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface CouponService {

    // === 券模板 ===
    Long createTemplate(CouponTemplateCreateDTO dto);

    void updateTemplate(Long templateId, Integer status);

    void deleteTemplate(Long templateId);

    CouponTemplateVO getTemplate(Long templateId);

    PageResult<CouponTemplateVO> pageTemplate(Integer pageNum, Integer pageSize);

    // === 发券 ===
    int issueCoupon(CouponIssueDTO dto);

    CouponVO receiveCoupon(Long templateId, Long customerId);

    // === 查询 ===
    List<CouponVO> listAvailableCoupon(Long customerId);

    PageResult<CouponVO> pageCoupon(Integer pageNum, Integer pageSize, Long customerId, Integer status);

    // === 使用 ===
    void lockCoupon(Long couponId, Long orderId, String orderNo);

    void useCoupon(Long couponId);

    void releaseCoupon(Long couponId);

    int revokeCoupon(CouponRevokeDTO dto);
}