package com.mfnit.discount.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.discount.constant.CouponStatus;
import com.mfnit.discount.dto.CouponIssueDTO;
import com.mfnit.discount.dto.CouponRevokeDTO;
import com.mfnit.discount.dto.CouponTemplateCreateDTO;
import com.mfnit.discount.entity.Coupon;
import com.mfnit.discount.entity.CouponTemplate;
import com.mfnit.discount.mapper.CouponMapper;
import com.mfnit.discount.mapper.CouponTemplateMapper;
import com.mfnit.discount.service.CouponService;
import com.mfnit.discount.vo.CouponTemplateVO;
import com.mfnit.discount.vo.CouponVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:40
 * @Description SOMS 优惠券服务实现类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final CouponTemplateMapper templateMapper;
    private final CouponMapper couponMapper;

    // === 券模板 ===

    @Override
    public Long createTemplate(CouponTemplateCreateDTO dto) {
        Long count = templateMapper.selectCount(
                new LambdaQueryWrapper<CouponTemplate>()
                        .eq(CouponTemplate::getTemplateCode, dto.getTemplateCode()));
        if (count > 0) {
            throw new BusinessException("模板编码已存在");
        }

        CouponTemplate t = new CouponTemplate();
        BeanUtils.copyProperties(dto, t);
        t.setIssuedCount(0).setStatus(1);
        templateMapper.insert(t);
        return t.getTemplateId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplate(Long templateId, Integer status) {
        CouponTemplate t = templateMapper.selectById(templateId);
        if (t == null) {
            throw new BusinessException("模板不存在");
        }
        if (t.getStatus().equals(status)) {
            return;  // 状态没变，直接返回
        }

        t.setStatus(status);
        templateMapper.updateById(t);

        // 停用模板 → 批量作废该模板下所有未使用的券
        if (status == 2) {
            com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Coupon> wrapper =
                    new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<>();
            wrapper.eq(Coupon::getTemplateId, templateId)
                    .eq(Coupon::getStatus, CouponStatus.UNUSED.getCode())
                    .set(Coupon::getStatus, CouponStatus.VOID.getCode());

            int count = couponMapper.update(null, wrapper);
            if (count > 0) {
                log.info("停用模板 {}，连带作废 {} 张未使用券", templateId, count);
            }
        }
    }

    @Override
    public void deleteTemplate(Long templateId) {
        templateMapper.deleteById(templateId);
    }

    @Override
    public CouponTemplateVO getTemplate(Long templateId) {
        CouponTemplate t = templateMapper.selectById(templateId);
        if (t == null) {
            throw new BusinessException("模板不存在");
        }
        return toTemplateVO(t);
    }

    @Override
    public PageResult<CouponTemplateVO> pageTemplate(Integer pageNum, Integer pageSize) {
        Page<CouponTemplate> page = new Page<>(pageNum, pageSize);
        Page<CouponTemplate> result = templateMapper.selectPage(page, null);
        List<CouponTemplateVO> records = result.getRecords().stream()
                .map(this::toTemplateVO).toList();
        return new PageResult<CouponTemplateVO>()
                .setRecords(records)
                .setTotal(result.getTotal())
                .setSize(result.getSize())
                .setCurrent(result.getCurrent())
                .setPages(result.getPages());
    }

    // === 发券 ===

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int issueCoupon(CouponIssueDTO dto) {
        CouponTemplate t = templateMapper.selectById(dto.getTemplateId());
        if (t == null || t.getStatus() != 1) {
            throw new BusinessException("模板不存在或已停用");
        }

        int total = dto.getCustomerIds().size();
        if (t.getTotalCount() > 0 && t.getIssuedCount() + total > t.getTotalCount()) {
            throw new BusinessException("发行数量不足，剩余：" + (t.getTotalCount() - t.getIssuedCount()));
        }

        LocalDateTime now = LocalDateTime.now();
        List<Coupon> coupons = new ArrayList<>();
        for (Long customerId : dto.getCustomerIds()) {
            Coupon c = buildCoupon(t, customerId, now);
            coupons.add(c);
        }

        for (Coupon c : coupons) {
            couponMapper.insert(c);
        }

        // 更新已发放数量
        t.setIssuedCount(t.getIssuedCount() + total);
        templateMapper.updateById(t);

        return total;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CouponVO receiveCoupon(Long templateId, Long customerId) {
        CouponTemplate t = templateMapper.selectById(templateId);
        if (t == null || t.getStatus() != 1) {
            throw new BusinessException("模板不存在或已停用");
        }
        if (t.getTotalCount() > 0 && t.getIssuedCount() >= t.getTotalCount()) {
            throw new BusinessException("已被领完");
        }

        // 每人限领
        Long received = couponMapper.selectCount(
                new LambdaQueryWrapper<Coupon>()
                        .eq(Coupon::getTemplateId, templateId)
                        .eq(Coupon::getCustomerId, customerId));
        if (received >= t.getPerLimit()) {
            throw new BusinessException("已达到领取上限");
        }

        Coupon c = buildCoupon(t, customerId, LocalDateTime.now());
        couponMapper.insert(c);

        t.setIssuedCount(t.getIssuedCount() + 1);
        templateMapper.updateById(t);

        return toVO(c, t.getTemplateName());
    }

    // === 查询 ===

    @Override
    public List<CouponVO> listAvailableCoupon(Long customerId) {
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> list = couponMapper.selectList(
                new LambdaQueryWrapper<Coupon>()
                        .eq(Coupon::getCustomerId, customerId)
                        .eq(Coupon::getStatus, CouponStatus.UNUSED.getCode())
                        .le(Coupon::getValidStart, now)
                        .ge(Coupon::getValidEnd, now)
                        .orderByAsc(Coupon::getValidEnd));
        return list.stream().map(c -> toVO(c, loadTemplateName(c.getTemplateId()))).toList();
    }

    @Override
    public PageResult<CouponVO> pageCoupon(Integer pageNum, Integer pageSize,
                                           Long customerId, Integer status) {
        Page<Coupon> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(customerId != null, Coupon::getCustomerId, customerId)
                .eq(status != null, Coupon::getStatus, status)
                .orderByDesc(Coupon::getGmtCreate);
        Page<Coupon> result = couponMapper.selectPage(page, wrapper);
        List<CouponVO> records = result.getRecords().stream()
                .map(c -> toVO(c, loadTemplateName(c.getTemplateId()))).toList();
        return new PageResult<CouponVO>()
                .setRecords(records)
                .setTotal(result.getTotal())
                .setSize(result.getSize())
                .setCurrent(result.getCurrent())
                .setPages(result.getPages());
    }

    // === 使用 ===

    @Override
    public void lockCoupon(Long couponId, Long orderId, String orderNo) {
        Coupon c = couponMapper.selectById(couponId);
        if (c == null) {
            throw new BusinessException("券不存在");
        }
        if (c.getStatus() != CouponStatus.UNUSED.getCode()) {
            throw new BusinessException("券当前不可用");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(c.getValidStart()) || now.isAfter(c.getValidEnd())) {
            throw new BusinessException("券不在有效期内");
        }
        c.setStatus(CouponStatus.LOCKED.getCode())
                .setOrderId(orderId)
                .setOrderNo(orderNo)
                .setUsedTime(now);
        couponMapper.updateById(c);
    }

    @Override
    public void useCoupon(Long couponId) {
        Coupon c = couponMapper.selectById(couponId);
        if (c == null || c.getStatus() != CouponStatus.LOCKED.getCode()) {
            return;
        }
        c.setStatus(CouponStatus.USED.getCode());
        couponMapper.updateById(c);
    }

    @Override
    public void releaseCoupon(Long couponId) {
        Coupon c = couponMapper.selectById(couponId);
        if (c == null || c.getStatus() != CouponStatus.LOCKED.getCode()) {
            return;
        }
        c.setStatus(CouponStatus.UNUSED.getCode())
                .setOrderId(null)
                .setOrderNo(null)
                .setUsedTime(null);
        couponMapper.updateById(c);
    }

    // === 私有方法 ===

    private Coupon buildCoupon(CouponTemplate t, Long customerId, LocalDateTime now) {
        LocalDateTime start, end;
        if (t.getValidType() == 2 && t.getValidDays() != null) {
            start = now;
            end = now.plusDays(t.getValidDays());
        } else {
            start = t.getValidStart();
            end = t.getValidEnd();
        }
        String couponNo = "CP" + now.format(DATE_FMT)
                + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();

        return new Coupon()
                .setCouponNo(couponNo)
                .setTemplateId(t.getTemplateId())
                .setCustomerId(customerId)
                .setStatus(CouponStatus.UNUSED.getCode())
                .setValidStart(start)
                .setValidEnd(end);
    }

    private String loadTemplateName(Long templateId) {
        CouponTemplate t = templateMapper.selectById(templateId);
        return t == null ? "" : t.getTemplateName();
    }

    private CouponTemplateVO toTemplateVO(CouponTemplate t) {
        CouponTemplateVO vo = new CouponTemplateVO();
        BeanUtils.copyProperties(t, vo);
        return vo;
    }

    private CouponVO toVO(Coupon c, String templateName) {
        CouponVO vo = new CouponVO()
                .setCouponId(c.getCouponId())
                .setCouponNo(c.getCouponNo())
                .setTemplateId(c.getTemplateId())
                .setTemplateName(templateName)
                .setStatus(c.getStatus())
                .setStatusName(CouponStatus.values()[c.getStatus()].getText())
                .setValidStart(c.getValidStart())
                .setValidEnd(c.getValidEnd());
        CouponTemplate t = templateMapper.selectById(c.getTemplateId());
        if (t != null) {
            vo.setCouponType(t.getCouponType())
                    .setThreshold(t.getThreshold())
                    .setAmount(t.getAmount())
                    .setDiscountRate(t.getDiscountRate());
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int revokeCoupon(CouponRevokeDTO dto) {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getStatus, CouponStatus.UNUSED.getCode());

        if (dto.getCouponIds() != null && !dto.getCouponIds().isEmpty()) {
            wrapper.in(Coupon::getCouponId, dto.getCouponIds());
        } else if (dto.getTemplateId() != null) {
            wrapper.eq(Coupon::getTemplateId, dto.getTemplateId());
        } else if (dto.getCustomerId() != null) {
            wrapper.eq(Coupon::getCustomerId, dto.getCustomerId());
        } else {
            throw new BusinessException("必须指定券ID、模板ID或会员ID");
        }

        List<Coupon> coupons = couponMapper.selectList(wrapper);
        if (coupons.isEmpty()) {
            return 0;
        }

        for (Coupon c : coupons) {
            c.setStatus(CouponStatus.VOID.getCode());
            couponMapper.updateById(c);
        }

        log.info("回收券 {} 张，原因：{}", coupons.size(), dto.getReason());
        return coupons.size();
    }
}