package com.mfnit.pay.controller;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.pay.dto.*;
import com.mfnit.pay.service.PayService;
import com.mfnit.pay.vo.PayOrderVO;
import com.mfnit.pay.vo.PayResultVO;
import com.mfnit.pay.vo.RefundVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:55
 * @Description SOMS Pay Service Controller
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Validated
@RestController
@RequestMapping("/api/v1/pay")
@RequiredArgsConstructor
public class PayController {

    private final PayService payService;

    /** 创建支付单（现金/会员余额） */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('pay:create')")
    public Result<PayResultVO> create(@Valid @RequestBody PayCreateDTO dto) {
        return ResultGenerator.genSuccessResult(payService.createPay(dto));
    }

    /** 付款码支付 */
    @PostMapping("/barcode")
    @PreAuthorize("hasAuthority('pay:create')")
    public Result<PayResultVO> barcode(@Valid @RequestBody BarcodePayDTO dto) {
        return ResultGenerator.genSuccessResult(payService.barcodePay(dto));
    }

    /** Native 支付 */
    @PostMapping("/native")
    @PreAuthorize("hasAuthority('pay:create')")
    public Result<PayResultVO> nativePay(@Valid @RequestBody NativePayDTO dto) {
        return ResultGenerator.genSuccessResult(payService.nativePay(dto));
    }

    /** JSAPI 支付 */
    @PostMapping("/jsapi")
    @PreAuthorize("hasAuthority('pay:create')")
    public Result<PayResultVO> jsapi(@Valid @RequestBody JsapiPayDTO dto) {
        return ResultGenerator.genSuccessResult(payService.jsapiPay(dto));
    }

    /** 查询支付结果 */
    @GetMapping("/query/{payNo}")
    @PreAuthorize("hasAuthority('pay:list')")
    public Result<PayResultVO> query(@PathVariable String payNo) {
        return ResultGenerator.genSuccessResult(payService.queryPay(payNo));
    }

    /** 支付单详情 */
    @GetMapping("/{payId}")
    @PreAuthorize("hasAuthority('pay:list')")
    public Result<PayOrderVO> detail(@PathVariable Long payId) {
        return ResultGenerator.genSuccessResult(payService.getDetail(payId));
    }

    /** 分页查询 */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pay:list')")
    public Result<PageResult<PayOrderVO>> list(PayQueryDTO dto) {
        return ResultGenerator.genSuccessResult(payService.pagePay(dto));
    }

    /** 关闭支付单 */
    @PostMapping("/close")
    @PreAuthorize("hasAuthority('pay:close')")
    public Result<Void> close(@Valid @RequestBody PayCloseDTO dto) {
        payService.closePay(dto);
        return ResultGenerator.genSuccessResult();
    }

    /** 退款 */
    @PostMapping("/refund")
    @PreAuthorize("hasAuthority('pay:refund')")
    public Result<RefundVO> refund(@Valid @RequestBody RefundCreateDTO dto) {
        return ResultGenerator.genSuccessResult(payService.createRefund(dto));
    }

    /** 退款查询 */
    @GetMapping("/refund/{refundNo}")
    @PreAuthorize("hasAuthority('pay:refund')")
    public Result<RefundVO> getRefund(@PathVariable String refundNo) {
        return ResultGenerator.genSuccessResult(payService.getRefund(refundNo));
    }

    // ===== 回调接口（不加权限，第三方调） =====

    /** 微信回调 */
    @PostMapping("/callback/wechat")
    public String wechatCallback(@RequestBody String body,
                                 @RequestHeader(value = "Wechatpay-Signature", required = false) String signature) {
        return payService.handleCallback(1, body, signature);
    }

    /** 支付宝回调 */
    @PostMapping("/callback/alipay")
    public String alipayCallback(@RequestParam java.util.Map<String, String> params) {
        return payService.handleCallback(2, params.toString(), null);
    }
}
