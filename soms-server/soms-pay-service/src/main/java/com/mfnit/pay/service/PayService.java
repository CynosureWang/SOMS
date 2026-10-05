package com.mfnit.pay.service;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.pay.dto.*;
import com.mfnit.pay.vo.PayOrderVO;
import com.mfnit.pay.vo.PayResultVO;
import com.mfnit.pay.vo.RefundVO;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:54
 * @Description SOMS 支付服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface PayService {

    /** 创建支付单（现金/会员余额直接成功） */
    PayResultVO createPay(PayCreateDTO dto);

    /** 付款码支付（微信/支付宝，同步返回） */
    PayResultVO barcodePay(BarcodePayDTO dto);

    /** Native 支付（生成二维码） */
    PayResultVO nativePay(NativePayDTO dto);

    /** JSAPI 支付（小程序） */
    PayResultVO jsapiPay(JsapiPayDTO dto);

    /** 微信/支付宝异步回调 */
    String handleCallback(Integer channel, String body, String signature);

    /** 主动查询支付结果 */
    PayResultVO queryPay(String payNo);

    /** 关闭支付单 */
    void closePay(PayCloseDTO dto);

    /** 支付单详情 */
    PayOrderVO getDetail(Long payId);

    /** 分页查询 */
    PageResult<PayOrderVO> pagePay(PayQueryDTO dto);

    /** 申请退款 */
    RefundVO createRefund(RefundCreateDTO dto);

    /** 退款查询 */
    RefundVO getRefund(String refundNo);
}
