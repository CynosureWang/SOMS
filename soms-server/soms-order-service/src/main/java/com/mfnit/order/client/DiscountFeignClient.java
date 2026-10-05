package com.mfnit.order.client;

import com.mfnit.common.api.result.Result;
import com.mfnit.order.client.dto.DiscountContextDTO;
import com.mfnit.order.client.dto.DiscountResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:47
 * @Description SOMS 折扣计算调用客户端
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@FeignClient(value = "service-discount", path = "/api/v1/discount")
public interface DiscountFeignClient {

    /** 计算购物车优惠 */
    @PostMapping("/internal/calculate")
    Result<DiscountResultDTO> calculate(@RequestBody DiscountContextDTO ctx);

    /** 锁定券 */
    @PostMapping("/coupon/internal/lock")
    Result<Void> lockCoupon(@RequestParam("couponId") Long couponId,
                            @RequestParam("orderId") Long orderId,
                            @RequestParam("orderNo") String orderNo);

    /** 核销券 */
    @PostMapping("/coupon/internal/use")
    Result<Void> useCoupon(@RequestParam("couponId") Long couponId);

    /** 释放券 */
    @PostMapping("/coupon/internal/release")
    Result<Void> releaseCoupon(@RequestParam("couponId") Long couponId);
}
