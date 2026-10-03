package com.mfnit.product.service;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.product.dto.BrandCreateDTO;
import com.mfnit.product.dto.BrandUpdateDTO;
import com.mfnit.product.vo.BrandVO;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:10
 * @Description SOMS 产品品牌服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface ProductBrandService {
    PageResult<BrandVO> pageBrand(Integer pageNum, Integer pageSize, String keyword);
    Long createBrand(BrandCreateDTO dto);
    void updateBrand(BrandUpdateDTO dto);
    void deleteBrand(Long brandId);
}
