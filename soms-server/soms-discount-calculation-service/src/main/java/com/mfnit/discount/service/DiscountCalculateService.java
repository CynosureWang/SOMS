package com.mfnit.discount.service;

import com.mfnit.discount.dto.DiscountContextDTO;
import com.mfnit.discount.vo.DiscountResultVO;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 16:43
 * @Description SOMS - Discount Calculate Service 优惠计算服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface DiscountCalculateService {

    /** 计算购物车优惠 */
    DiscountResultVO calculate(DiscountContextDTO ctx);
}
