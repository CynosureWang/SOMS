package com.mfnit.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.product.dto.ProductCreateDTO;
import com.mfnit.product.dto.ProductQueryDTO;
import com.mfnit.product.dto.ProductUpdateDTO;
import com.mfnit.product.entity.Product;
import com.mfnit.product.entity.ProductBarcode;
import com.mfnit.product.entity.ProductBrand;
import com.mfnit.product.entity.ProductCategory;
import com.mfnit.product.entity.ProductImage;
import com.mfnit.product.mapper.*;
import com.mfnit.product.service.ProductService;
import com.mfnit.product.vo.ProductDetailVO;
import com.mfnit.product.vo.ProductSimpleVO;
import com.mfnit.product.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 17:16
 * @Description SOMS Product Service Implementation
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductCategoryMapper categoryMapper;
    private final ProductBrandMapper brandMapper;
    private final ProductImageMapper imageMapper;
    private final ProductBarcodeMapper barcodeMapper;

    @Override
    public PageResult<ProductVO> pageProduct(ProductQueryDTO dto) {
        Page<Product> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getKeyword())) {
            String kw = dto.getKeyword();
            wrapper.and(w -> w.like(Product::getProductName, kw)
                    .or().like(Product::getProductCode, kw)
                    .or().like(Product::getBarcode, kw)
                    .or().like(Product::getPinyin, kw));
        }
        wrapper.eq(dto.getCategoryId() != null, Product::getCategoryId, dto.getCategoryId())
                .eq(dto.getBrandId() != null, Product::getBrandId, dto.getBrandId())
                .eq(dto.getStatus() != null, Product::getStatus, dto.getStatus())
                .eq(dto.getIsWeight() != null, Product::getIsWeight, dto.getIsWeight())
                .orderByDesc(Product::getGmtCreate);

        Page<Product> result = productMapper.selectPage(page, wrapper);

        // 批量查分类名、品牌名
        List<Long> categoryIds = result.getRecords().stream().map(Product::getCategoryId).distinct().toList();
        List<Long> brandIds = result.getRecords().stream().map(Product::getBrandId).filter(x -> x != null).distinct().toList();

        Map<Long, String> categoryMap = categoryIds.isEmpty() ? Map.of() :
                categoryMapper.selectBatchIds(categoryIds).stream()
                        .collect(Collectors.toMap(ProductCategory::getCategoryId, ProductCategory::getCategoryName));
        Map<Long, String> brandMap = brandIds.isEmpty() ? Map.of() :
                brandMapper.selectBatchIds(brandIds).stream()
                        .collect(Collectors.toMap(ProductBrand::getBrandId, ProductBrand::getBrandName));

        List<ProductVO> records = result.getRecords().stream().map(p -> {
            ProductVO vo = new ProductVO();
            BeanUtils.copyProperties(p, vo);
            vo.setCategoryName(categoryMap.get(p.getCategoryId()));
            vo.setBrandName(p.getBrandId() == null ? null : brandMap.get(p.getBrandId()));
            return vo;
        }).toList();

        return new PageResult<ProductVO>()
                .setRecords(records)
                .setTotal(result.getTotal())
                .setSize(result.getSize())
                .setCurrent(result.getCurrent())
                .setPages(result.getPages());
    }

    @Override
    public ProductDetailVO getDetail(Long productId) {
        Product p = productMapper.selectById(productId);
        if (p == null) {
            throw new BusinessException("商品不存在");
        }
        ProductDetailVO vo = new ProductDetailVO();
        BeanUtils.copyProperties(p, vo);

        ProductCategory category = categoryMapper.selectById(p.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getCategoryName());
        }
        if (p.getBrandId() != null) {
            ProductBrand brand = brandMapper.selectById(p.getBrandId());
            if (brand != null) {
                vo.setBrandName(brand.getBrandName());
            }
        }
        List<String> images = imageMapper.selectList(
                        new LambdaQueryWrapper<ProductImage>()
                                .eq(ProductImage::getProductId, productId)
                                .orderByAsc(ProductImage::getSort))
                .stream().map(ProductImage::getImageUrl).toList();
        vo.setImages(images);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProduct(ProductCreateDTO dto) {
        Long count = productMapper.selectCount(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getProductCode, dto.getProductCode()));
        if (count > 0) {
            throw new BusinessException("商品编码已存在");
        }

        Product p = new Product();
        BeanUtils.copyProperties(dto, p);
        p.setStatus(1);
        productMapper.insert(p);

        // 写条码表
        if (StringUtils.hasText(dto.getBarcode())) {
            ProductBarcode pb = new ProductBarcode()
                    .setProductId(p.getProductId())
                    .setBarcode(dto.getBarcode())
                    .setBarcodeType(1)
                    .setIsPrimary(1);
            barcodeMapper.insert(pb);
        }
        return p.getProductId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(ProductUpdateDTO dto) {
        Product p = productMapper.selectById(dto.getProductId());
        if (p == null) {
            throw new BusinessException("商品不存在");
        }
        BeanUtils.copyProperties(dto, p);
        productMapper.updateById(p);
    }

    @Override
    public void deleteProduct(Long productId) {
        productMapper.deleteById(productId);
    }

    @Override
    public void changeStatus(Long productId, Integer status) {
        Product p = productMapper.selectById(productId);
        if (p == null) {
            throw new BusinessException("商品不存在");
        }
        p.setStatus(status);
        productMapper.updateById(p);
    }

    @Override
    public ProductSimpleVO getByBarcode(String barcode) {
        // 先查条码表
        ProductBarcode pb = barcodeMapper.selectOne(
                new LambdaQueryWrapper<ProductBarcode>()
                        .eq(ProductBarcode::getBarcode, barcode));
        if (pb == null) {
            throw new BusinessException("商品不存在：" + barcode);
        }
        return toSimpleVO(productMapper.selectById(pb.getProductId()));
    }

    @Override
    public ProductSimpleVO getByScaleCode(String scaleCode) {
        Product p = productMapper.selectOne(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getScaleCode, scaleCode));
        if (p == null) {
            throw new BusinessException("称重商品不存在：" + scaleCode);
        }
        return toSimpleVO(p);
    }

    @Override
    public List<ProductSimpleVO> listScaleProducts(Long storeId) {
        List<Product> list = productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getIsWeight, 1)
                        .eq(Product::getStatus, 1));
        return list.stream().map(this::toSimpleVO).toList();
    }

    private ProductSimpleVO toSimpleVO(Product p) {
        if (p == null) return null;
        ProductSimpleVO vo = new ProductSimpleVO();
        BeanUtils.copyProperties(p, vo);
        return vo;
    }
}
