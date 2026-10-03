package com.mfnit.order.service;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.order.dto.OrderCancelDTO;
import com.mfnit.order.dto.OrderCreateDTO;
import com.mfnit.order.dto.OrderPaySuccessDTO;
import com.mfnit.order.dto.OrderQueryDTO;
import com.mfnit.order.vo.OrderDetailVO;
import com.mfnit.order.vo.OrderVO;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:15
 * @Description SOMS 订单服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface OrderService {

    /** 创建订单 */
    OrderDetailVO createOrder(OrderCreateDTO dto);

    /** 查询订单详情 */
    OrderDetailVO getDetail(Long orderId);

    /** 按订单号查询 */
    OrderDetailVO getByOrderNo(String orderNo);

    /** 分页查询 */
    PageResult<OrderVO> pageOrder(OrderQueryDTO dto);

    /** 取消订单 */
    void cancelOrder(OrderCancelDTO dto);

    /** 支付成功回调 */
    void paySuccess(OrderPaySuccessDTO dto);

    /** 完成订单 */
    void finishOrder(Long orderId);

    /** 超时未支付订单取消（定时任务调用） */
    int cancelTimeoutOrders();
}
