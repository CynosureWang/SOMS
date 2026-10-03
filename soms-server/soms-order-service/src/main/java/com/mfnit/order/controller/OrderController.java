package com.mfnit.order.controller;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.order.dto.OrderCancelDTO;
import com.mfnit.order.dto.OrderCreateDTO;
import com.mfnit.order.dto.OrderPaySuccessDTO;
import com.mfnit.order.dto.OrderQueryDTO;
import com.mfnit.order.service.OrderService;
import com.mfnit.order.vo.OrderDetailVO;
import com.mfnit.order.vo.OrderVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:16
 * @Description SOMS Order Service
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Validated
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /** 创建订单 */
    @PostMapping
    @PreAuthorize("hasAuthority('order:add')")
    public Result<OrderDetailVO> create(@Valid @RequestBody OrderCreateDTO dto) {
        return ResultGenerator.genSuccessResult(orderService.createOrder(dto));
    }

    /** 分页查询 */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('order:list')")
    public Result<PageResult<OrderVO>> list(OrderQueryDTO dto) {
        return ResultGenerator.genSuccessResult(orderService.pageOrder(dto));
    }

    /** 订单详情 */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAuthority('order:detail')")
    public Result<OrderDetailVO> detail(@PathVariable Long orderId) {
        return ResultGenerator.genSuccessResult(orderService.getDetail(orderId));
    }

    /** 按订单号查询 */
    @GetMapping("/no/{orderNo}")
    @PreAuthorize("hasAuthority('order:detail')")
    public Result<OrderDetailVO> getByOrderNo(@PathVariable String orderNo) {
        return ResultGenerator.genSuccessResult(orderService.getByOrderNo(orderNo));
    }

    /** 取消订单 */
    @PostMapping("/cancel")
    @PreAuthorize("hasAuthority('order:cancel')")
    public Result<Void> cancel(@Valid @RequestBody OrderCancelDTO dto) {
        orderService.cancelOrder(dto);
        return ResultGenerator.genSuccessResult();
    }

    /** 完成订单 */
    @PostMapping("/finish/{orderId}")
    @PreAuthorize("hasAuthority('order:edit')")
    public Result<Void> finish(@PathVariable Long orderId) {
        orderService.finishOrder(orderId);
        return ResultGenerator.genSuccessResult();
    }

    // ===== 内部接口：pay 回调时用，不加 @PreAuthorize =====

    /** 支付成功通知 */
    @PostMapping("/internal/pay-success")
    public Result<Void> paySuccess(@Valid @RequestBody OrderPaySuccessDTO dto) {
        orderService.paySuccess(dto);
        return ResultGenerator.genSuccessResult();
    }
}
