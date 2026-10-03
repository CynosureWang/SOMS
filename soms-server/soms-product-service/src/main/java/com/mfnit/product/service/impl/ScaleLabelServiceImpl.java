package com.mfnit.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.product.dto.ScaleLabelGenerateDTO;
import com.mfnit.product.dto.ScaleLabelMarkDTO;
import com.mfnit.product.entity.Product;
import com.mfnit.product.entity.ScaleLabel;
import com.mfnit.product.mapper.ProductMapper;
import com.mfnit.product.mapper.ScaleLabelMapper;
import com.mfnit.product.service.ScaleLabelService;
import com.mfnit.product.vo.ScaleLabelVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 17:17
 * @Description SOMS - ScaleLabel Service Implementation
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Service
@RequiredArgsConstructor
public class ScaleLabelServiceImpl implements ScaleLabelService {

    private static final String SCALE_BARCODE_PREFIX = "25";
    private static final String SEQ_KEY = "product:scale:label:seq";
    private static final int EXPIRE_HOURS = 24;

    private final ScaleLabelMapper labelMapper;
    private final ProductMapper productMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScaleLabelVO generateLabel(ScaleLabelGenerateDTO dto) {
        // 1. 查商品
        Product p = productMapper.selectOne(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getScaleCode, dto.getScaleCode())
                        .eq(Product::getStatus, 1));
        if (p == null) {
            throw new BusinessException("称重商品不存在：" + dto.getScaleCode());
        }
        if (p.getIsWeight() != 1) {
            throw new BusinessException("该商品不是称重商品");
        }

        // 2. 生成条码 25 + 11位序列 + 1位校验
        String barcode = generateBarcode();

        // 3. 计算金额
        BigDecimal amount = p.getPrice().multiply(dto.getWeight())
                .setScale(2, RoundingMode.HALF_UP);

        // 4. 写库
        ScaleLabel label = new ScaleLabel()
                .setBarcode(barcode)
                .setProductId(p.getProductId())
                .setProductName(p.getProductName())
                .setSpecText(p.getSpecText())
                .setPrice(p.getPrice())
                .setWeight(dto.getWeight())
                .setAmount(amount)
                .setStoreId(dto.getStoreId())
                .setScaleCode(dto.getScaleCode())
                .setOperatorId(dto.getOperatorId())
                .setStatus(0)
                .setGmtExpire(LocalDateTime.now().plusHours(EXPIRE_HOURS));
        labelMapper.insert(label);

        return toVO(label, p.getUnit());
    }

    @Override
    public ScaleLabelVO getLabel(String barcode) {
        ScaleLabel label = labelMapper.selectOne(
                new LambdaQueryWrapper<ScaleLabel>()
                        .eq(ScaleLabel::getBarcode, barcode));
        if (label == null) {
            throw new BusinessException("价签不存在");
        }
        if (label.getStatus() == 1) {
            throw new BusinessException("该价签已售出");
        }
        if (label.getStatus() == 2 || label.getStatus() == 3) {
            throw new BusinessException("该价签已失效");
        }
        if (label.getGmtExpire().isBefore(LocalDateTime.now())) {
            throw new BusinessException("该价签已过期");
        }
        Product p = productMapper.selectById(label.getProductId());
        return toVO(label, p == null ? "kg" : p.getUnit());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markSold(ScaleLabelMarkDTO dto) {
        List<ScaleLabel> labels = labelMapper.selectList(
                new LambdaQueryWrapper<ScaleLabel>()
                        .in(ScaleLabel::getBarcode, dto.getBarcodes()));
        if (labels.isEmpty()) {
            return;
        }
        for (ScaleLabel label : labels) {
            if (label.getStatus() != 0) {
                continue; // 幂等
            }
            label.setStatus(1)
                    .setOrderId(dto.getOrderId())
                    .setOrderNo(dto.getOrderNo())
                    .setGmtSold(LocalDateTime.now());
            labelMapper.updateById(label);
        }
    }

    private String generateBarcode() {
        Long seq = redisTemplate.opsForValue().increment(SEQ_KEY);
        if (seq == null) {
            throw new BusinessException("生成条码失败");
        }
        // 重置序列防止溢出
        if (seq > 99999999999L) {
            redisTemplate.opsForValue().set(SEQ_KEY, "0");
            seq = 1L;
        }
        String seqStr = String.format("%011d", seq);
        String body = SCALE_BARCODE_PREFIX + seqStr;
        return body + calcCheckDigit(body);
    }

    /** EAN-13 校验位 */
    private String calcCheckDigit(String body12) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = body12.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int check = (10 - (sum % 10)) % 10;
        return String.valueOf(check);
    }

    private ScaleLabelVO toVO(ScaleLabel label, String unit) {
        return new ScaleLabelVO()
                .setLabelId(label.getLabelId())
                .setBarcode(label.getBarcode())
                .setProductId(label.getProductId())
                .setProductName(label.getProductName())
                .setSpecText(label.getSpecText())
                .setPrice(label.getPrice())
                .setWeight(label.getWeight())
                .setAmount(label.getAmount())
                .setUnit(unit)
                .setStatus(label.getStatus())
                .setGmtCreate(label.getGmtCreate());
    }
}
