package com.mfnit.product.service;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.product.dto.ProductCreateDTO;
import com.mfnit.product.dto.ProductQueryDTO;
import com.mfnit.product.dto.ProductUpdateDTO;
import com.mfnit.product.vo.ProductDetailVO;
import com.mfnit.product.vo.ProductSimpleVO;
import com.mfnit.product.vo.ProductVO;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:10
 * @Description SOMS 产品服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface ProductService {
    PageResult<ProductVO> pageProduct(ProductQueryDTO dto);
    ProductDetailVO getDetail(Long productId);
    Long createProduct(ProductCreateDTO dto);
    void updateProduct(ProductUpdateDTO dto);
    void deleteProduct(Long productId);
    void changeStatus(Long productId, Integer status);

    /** 收银：按条码查 */
    ProductSimpleVO getByBarcode(String barcode);
    /** 收银：按秤码查 */
    ProductSimpleVO getByScaleCode(String scaleCode);
    /** 收银：批量拉本店称重商品，用于缓存 */
    List<ProductSimpleVO> listScaleProducts(Long storeId);
}