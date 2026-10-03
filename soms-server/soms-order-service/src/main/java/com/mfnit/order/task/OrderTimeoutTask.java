package com.mfnit.order.task;

import com.mfnit.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:16
 * @Description SOMS Order Service 定时超时订单取消任务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutTask {

    private final OrderService orderService;

    /** 每分钟扫描一次超时未支付订单 */
    @Scheduled(cron = "0 * * * * ?")
    public void cancelTimeoutOrders() {
        int count = orderService.cancelTimeoutOrders();
        if (count > 0) {
            log.info("自动取消超时订单 {} 个", count);
        }
    }
}
