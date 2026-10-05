package com.mfnit.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.pay.client.OrderFeignClient;
import com.mfnit.pay.client.dto.OrderPaySuccessDTO;
import com.mfnit.pay.constant.*;
import com.mfnit.pay.dto.*;
import com.mfnit.pay.entity.PayFlow;
import com.mfnit.pay.entity.PayOrder;
import com.mfnit.pay.entity.PayRefund;
import com.mfnit.pay.mapper.PayFlowMapper;
import com.mfnit.pay.mapper.PayOrderMapper;
import com.mfnit.pay.mapper.PayRefundMapper;
import com.mfnit.pay.service.PayNoGenerator;
import com.mfnit.pay.service.PayService;
import com.mfnit.pay.vo.PayOrderVO;
import com.mfnit.pay.vo.PayResultVO;
import com.mfnit.pay.vo.RefundVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:55
 * @Description SOMS 支付服务实现
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {

    private final PayOrderMapper payOrderMapper;
    private final PayFlowMapper payFlowMapper;
    private final PayRefundMapper payRefundMapper;
    private final PayNoGenerator payNoGenerator;
    private final OrderFeignClient orderFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayResultVO createPay(PayCreateDTO dto) {
        PayOrder pay = new PayOrder()
                .setPayNo(payNoGenerator.generatePayNo())
                .setOrderId(dto.getOrderId())
                .setOrderNo(dto.getOrderNo())
                .setStoreId(dto.getStoreId())
                .setCustomerId(dto.getCustomerId())
                .setPayType(dto.getPayType())
                .setPayScene(dto.getPayScene())
                .setPayAmount(dto.getPayAmount())
                .setPaidAmount(BigDecimal.ZERO)
                .setStatus(PayStatus.PENDING.getCode())
                .setExpireTime(LocalDateTime.now().plusMinutes(30))
                .setAttach(dto.getAttach());
        payOrderMapper.insert(pay);

        writeFlow(pay, PayFlowType.CREATE, null, null, "创建支付单");

        // 现金 / 会员余额：直接成功
        if (dto.getPayType() == PayType.CASH.getCode()
                || dto.getPayType() == PayType.MEMBER_BALANCE.getCode()) {
            //TODO 调用扣减会员余额/现金账户接口，调soms-customer-service取balance后，判断余额是否充足，现用模拟
            doPaySuccess(pay, dto.getPayAmount());
            return buildResult(pay, null, null);
        }

        throw new BusinessException("该支付方式请使用对应接口");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayResultVO barcodePay(BarcodePayDTO dto) {
        PayOrder pay = createInternal(dto.getOrderId(), dto.getOrderNo(), dto.getStoreId(),
                dto.getCustomerId(), dto.getPayType(), PayScene.BARCODE.getCode(),
                dto.getPayAmount(), null);
        //TODO 对接微信/支付宝付款码接口
        String channelTradeNo = mockBarcodePay(dto.getAuthCode(), dto.getPayAmount());

        if (channelTradeNo != null) {
            doPaySuccess(pay, dto.getPayAmount());
            pay.setChannelTradeNo(channelTradeNo);
            payOrderMapper.updateById(pay);
            return buildResult(pay, null, null);
        } else {
            pay.setStatus(PayStatus.FAILED.getCode()).setFailReason("付款码支付失败");
            payOrderMapper.updateById(pay);
            writeFlow(pay, PayFlowType.FAILED, null, null, "付款码支付失败");
            throw new BusinessException("支付失败，请重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayResultVO nativePay(NativePayDTO dto) {
        PayOrder pay = createInternal(dto.getOrderId(), dto.getOrderNo(), dto.getStoreId(),
                null, dto.getPayType(), PayScene.NATIVE.getCode(),
                dto.getPayAmount(), null);

        // 调渠道生成二维码
        String codeUrl = mockNativePay(pay.getPayNo(), dto.getPayAmount());
        pay.setStatus(PayStatus.PAYING.getCode());
        payOrderMapper.updateById(pay);

        writeFlow(pay, PayFlowType.INITIATE, codeUrl, null, "发起Native支付");

        return buildResult(pay, codeUrl, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayResultVO jsapiPay(JsapiPayDTO dto) {
        PayOrder pay = createInternal(dto.getOrderId(), dto.getOrderNo(), dto.getStoreId(),
                null, dto.getPayType(), PayScene.JSAPI.getCode(),
                dto.getPayAmount(), null);

        pay.setChannelUserId(dto.getOpenid());
        pay.setStatus(PayStatus.PAYING.getCode());
        payOrderMapper.updateById(pay);

        // 调渠道生成 JSAPI 支付参数
        java.util.Map<String, String> jsapiParams = mockJsapiPay(pay.getPayNo(), dto.getPayAmount(), dto.getOpenid());

        writeFlow(pay, PayFlowType.INITIATE, null, null, "发起JSAPI支付");

        return buildResult(pay, null, jsapiParams);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handleCallback(Integer channel, String body, String signature) {
        // 1. 验签
        // 2. 解析报文，拿 out_trade_no（我们的 pay_no）、transaction_id、total_fee
        // 3. 查支付单
        // 4. 幂等：已成功的直接返回
        // 5. 更新状态、写流水、通知 order

        log.info("收到支付回调：channel={}, body={}", channel, body);

        // 模拟解析
        String payNo = parseOutTradeNo(body);
        PayOrder pay = payOrderMapper.selectOne(
                new LambdaQueryWrapper<PayOrder>().eq(PayOrder::getPayNo, payNo));
        if (pay == null) {
            return "FAIL";
        }
        if (pay.getStatus() == PayStatus.SUCCESS.getCode()) {
            return "SUCCESS"; // 幂等
        }

        BigDecimal paidAmount = parseAmount(body);
        String channelTradeNo = parseTransactionId(body);

        pay.setChannelTradeNo(channelTradeNo);
        doPaySuccess(pay, paidAmount);

        writeFlow(pay, PayFlowType.CALLBACK, body, null, "异步回调");

        return "SUCCESS";
    }

    @Override
    public PayResultVO queryPay(String payNo) {
        PayOrder pay = payOrderMapper.selectOne(
                new LambdaQueryWrapper<PayOrder>().eq(PayOrder::getPayNo, payNo));
        if (pay == null) {
            throw new BusinessException("支付单不存在");
        }
        return buildResult(pay, null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closePay(PayCloseDTO dto) {
        PayOrder pay = payOrderMapper.selectById(dto.getPayId());
        if (pay == null) {
            throw new BusinessException("支付单不存在");
        }
        if (pay.getStatus() == PayStatus.SUCCESS.getCode()) {
            throw new BusinessException("已支付成功，不能关闭");
        }
        if (pay.getStatus() == PayStatus.CLOSED.getCode()) {
            return;
        }
        pay.setStatus(PayStatus.CLOSED.getCode())
                .setCloseTime(LocalDateTime.now())
                .setFailReason(dto.getReason());
        payOrderMapper.updateById(pay);
        writeFlow(pay, PayFlowType.CLOSED, null, null, dto.getReason());
    }

    @Override
    public PayOrderVO getDetail(Long payId) {
        PayOrder pay = payOrderMapper.selectById(payId);
        if (pay == null) {
            throw new BusinessException("支付单不存在");
        }
        return toVO(pay);
    }

    @Override
    public PageResult<PayOrderVO> pagePay(PayQueryDTO dto) {
        Page<PayOrder> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<PayOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getStoreId() != null, PayOrder::getStoreId, dto.getStoreId())
                .eq(dto.getPayType() != null, PayOrder::getPayType, dto.getPayType())
                .eq(dto.getStatus() != null, PayOrder::getStatus, dto.getStatus())
                .like(StringUtils.hasText(dto.getPayNo()), PayOrder::getPayNo, dto.getPayNo())
                .like(StringUtils.hasText(dto.getOrderNo()), PayOrder::getOrderNo, dto.getOrderNo())
                .orderByDesc(PayOrder::getGmtCreate);

        Page<PayOrder> result = payOrderMapper.selectPage(page, wrapper);
        List<PayOrderVO> records = result.getRecords().stream().map(this::toVO).toList();

        return new PageResult<PayOrderVO>()
                .setRecords(records)
                .setTotal(result.getTotal())
                .setSize(result.getSize())
                .setCurrent(result.getCurrent())
                .setPages(result.getPages());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RefundVO createRefund(RefundCreateDTO dto) {
        PayOrder pay = payOrderMapper.selectById(dto.getPayId());
        if (pay == null) {
            throw new BusinessException("支付单不存在");
        }
        if (pay.getStatus() != PayStatus.SUCCESS.getCode()) {
            throw new BusinessException("只有支付成功的订单可以退款");
        }
        if (dto.getRefundAmount().compareTo(pay.getPaidAmount()) > 0) {
            throw new BusinessException("退款金额不能超过实付金额");
        }

        PayRefund refund = new PayRefund()
                .setRefundNo(payNoGenerator.generateRefundNo())
                .setPayId(pay.getPayId())
                .setPayNo(pay.getPayNo())
                .setOrderId(pay.getOrderId())
                .setOrderNo(pay.getOrderNo())
                .setRefundAmount(dto.getRefundAmount())
                .setReason(dto.getReason())
                .setStatus(0);
        payRefundMapper.insert(refund);

        // 调第三方退款（模拟）
        String channelRefundNo = mockRefund(pay.getPayNo(), dto.getRefundAmount());
        refund.setChannelRefundNo(channelRefundNo)
                .setStatus(2)
                .setRefundTime(LocalDateTime.now());
        payRefundMapper.updateById(refund);

        // 更新支付单状态
        if (dto.getRefundAmount().compareTo(pay.getPaidAmount()) == 0) {
            pay.setStatus(PayStatus.REFUNDED.getCode());
            payOrderMapper.updateById(pay);
        }

        writeFlow(pay, PayFlowType.REFUND, null, null, "退款：" + dto.getRefundAmount());

        return toRefundVO(refund);
    }

    @Override
    public RefundVO getRefund(String refundNo) {
        PayRefund refund = payRefundMapper.selectOne(
                new LambdaQueryWrapper<PayRefund>().eq(PayRefund::getRefundNo, refundNo));
        if (refund == null) {
            throw new BusinessException("退款单不存在");
        }
        return toRefundVO(refund);
    }

    // ==================== 私有方法 ====================

    private PayOrder createInternal(Long orderId, String orderNo, Long storeId,
                                    Long customerId, Integer payType, Integer payScene,
                                    BigDecimal payAmount, String attach) {
        PayOrder pay = new PayOrder()
                .setPayNo(payNoGenerator.generatePayNo())
                .setOrderId(orderId)
                .setOrderNo(orderNo)
                .setStoreId(storeId)
                .setCustomerId(customerId)
                .setPayType(payType)
                .setPayScene(payScene)
                .setPayAmount(payAmount)
                .setPaidAmount(BigDecimal.ZERO)
                .setStatus(PayStatus.PENDING.getCode())
                .setExpireTime(LocalDateTime.now().plusMinutes(30))
                .setAttach(attach);
        payOrderMapper.insert(pay);
        writeFlow(pay, PayFlowType.CREATE, null, null, "创建支付单");
        return pay;
    }

    /** 支付成功：更新支付单 + 通知 order */
    private void doPaySuccess(PayOrder pay, BigDecimal paidAmount) {
        pay.setStatus(PayStatus.SUCCESS.getCode())
                .setPaidAmount(paidAmount)
                .setPayTime(LocalDateTime.now());
        payOrderMapper.updateById(pay);

        writeFlow(pay, PayFlowType.SUCCESS, null, null, "支付成功");

        // 通知 order
        try {
            OrderPaySuccessDTO dto = new OrderPaySuccessDTO();
            dto.setOrderNo(pay.getOrderNo());
            dto.setPayType(pay.getPayType());
            dto.setPaidAmount(paidAmount);
            Result<Void> result = orderFeignClient.paySuccess(dto);
            if (result.getCode() != 0) {
                log.error("通知订单支付成功失败：payNo={}, msg={}", pay.getPayNo(), result.getMessage());
                throw new BusinessException("通知订单失败：" + result.getMessage());
            }
        } catch (Exception e) {
            log.error("通知订单异常：payNo={}", pay.getPayNo(), e);
            throw new BusinessException("通知订单异常：" + e.getMessage());
        }
    }

    private void writeFlow(PayOrder pay, PayFlowType type, String req, String resp, String remark) {
        PayFlow flow = new PayFlow()
                .setPayId(pay.getPayId())
                .setPayNo(pay.getPayNo())
                .setFlowType(type.getCode())
                .setChannel(getChannelByPayType(pay.getPayType()))
                .setRequestData(req)
                .setResponseData(resp)
                .setRemark(remark);
        payFlowMapper.insert(flow);
    }

    private Integer getChannelByPayType(Integer payType) {
        if (payType == PayType.WECHAT.getCode()) return PayChannel.WECHAT.getCode();
        if (payType == PayType.ALIPAY.getCode()) return PayChannel.ALIPAY.getCode();
        return null;
    }

    private PayOrderVO toVO(PayOrder pay) {
        PayOrderVO vo = new PayOrderVO();
        BeanUtils.copyProperties(pay, vo);
        vo.setPayTypeName(PayType.values()[pay.getPayType() - 1].getText());
        vo.setStatusName(PayStatus.values()[pay.getStatus()].getText());
        return vo;
    }

    private RefundVO toRefundVO(PayRefund refund) {
        RefundVO vo = new RefundVO();
        BeanUtils.copyProperties(refund, vo);
        vo.setStatusName(switch (refund.getStatus()) {
            case 0 -> "待退款";
            case 1 -> "退款中";
            case 2 -> "成功";
            case 3 -> "失败";
            default -> "";
        });
        return vo;
    }

    private PayResultVO buildResult(PayOrder pay, String codeUrl, java.util.Map<String, String> jsapiParams) {
        PayResultVO vo = new PayResultVO()
                .setPayId(pay.getPayId())
                .setPayNo(pay.getPayNo())
                .setStatus(pay.getStatus())
                .setStatusName(PayStatus.values()[pay.getStatus()].getText())
                .setChannelTradeNo(pay.getChannelTradeNo());
        if (codeUrl != null) vo.setCodeUrl(codeUrl);
        if (jsapiParams != null) vo.setJsapiParams(jsapiParams);
        return vo;
    }

    // ==================== 模拟第三方（后续替换真实渠道） ====================

    private String mockBarcodePay(String authCode, BigDecimal amount) {
        // 模拟：authCode 以 1 开头成功，其他失败
        return authCode.startsWith("1") ? "MOCK_TN_" + System.currentTimeMillis() : null;
    }

    private String mockNativePay(String payNo, BigDecimal amount) {
        return "weixin://wxpay/bizpayurl?pr=" + payNo;
    }

    private java.util.Map<String, String> mockJsapiPay(String payNo, BigDecimal amount, String openid) {
        java.util.Map<String, String> params = new java.util.HashMap<>();
        params.put("appId", "mock_appid");
        params.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        params.put("nonceStr", java.util.UUID.randomUUID().toString().replace("-", ""));
        params.put("package", "prepay_id=mock_" + payNo);
        params.put("signType", "RSA");
        params.put("paySign", "mock_sign");
        return params;
    }

    private String mockRefund(String payNo, BigDecimal amount) {
        return "MOCK_REFUND_" + System.currentTimeMillis();
    }

    private String parseOutTradeNo(String body) {
        // 实际应从 JSON 里取 out_trade_no
        return "MFSOMSP202610040000001";
    }

    private BigDecimal parseAmount(String body) {
        return new BigDecimal("0.01");
    }

    private String parseTransactionId(String body) {
        return "MOCK_TN_" + System.currentTimeMillis();
    }
}