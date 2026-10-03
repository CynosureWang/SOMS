package com.mfnit.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.product.dto.BrandCreateDTO;
import com.mfnit.product.dto.BrandUpdateDTO;
import com.mfnit.product.entity.ProductBrand;
import com.mfnit.product.mapper.ProductBrandMapper;
import com.mfnit.product.service.ProductBrandService;
import com.mfnit.product.vo.BrandVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:59
 * @Description SOMS Product Brand Service Implementation
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Service
@RequiredArgsConstructor
public class ProductBrandServiceImpl implements ProductBrandService {

    private final ProductBrandMapper brandMapper;

    @Override
    public PageResult<BrandVO> pageBrand(Integer pageNum, Integer pageSize, String keyword) {
        Page<ProductBrand> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ProductBrand> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(ProductBrand::getBrandName, keyword)
                    .or().like(ProductBrand::getBrandCode, keyword));
        }
        wrapper.orderByAsc(ProductBrand::getSort);
        Page<ProductBrand> result = brandMapper.selectPage(page, wrapper);

        List<BrandVO> records = result.getRecords().stream().map(b -> {
            BrandVO vo = new BrandVO();
            BeanUtils.copyProperties(b, vo);
            return vo;
        }).toList();

        return new PageResult<BrandVO>()
                .setRecords(records)
                .setTotal(result.getTotal())
                .setSize(result.getSize())
                .setCurrent(result.getCurrent())
                .setPages(result.getPages());
    }

    @Override
    public Long createBrand(BrandCreateDTO dto) {
        Long count = brandMapper.selectCount(
                new LambdaQueryWrapper<ProductBrand>()
                        .eq(ProductBrand::getBrandCode, dto.getBrandCode()));
        if (count > 0) {
            throw new BusinessException("品牌编码已存在");
        }
        ProductBrand brand = new ProductBrand()
                .setBrandCode(dto.getBrandCode())
                .setBrandName(dto.getBrandName())
                .setBrandLogo(dto.getBrandLogo())
                .setFirstLetter(dto.getFirstLetter())
                .setSort(dto.getSort())
                .setRemark(dto.getRemark())
                .setStatus(1);
        brandMapper.insert(brand);
        return brand.getBrandId();
    }

    @Override
    public void updateBrand(BrandUpdateDTO dto) {
        ProductBrand brand = brandMapper.selectById(dto.getBrandId());
        if (brand == null) {
            throw new BusinessException("品牌不存在");
        }
        brand.setBrandName(dto.getBrandName())
                .setBrandLogo(dto.getBrandLogo())
                .setFirstLetter(dto.getFirstLetter())
                .setSort(dto.getSort())
                .setStatus(dto.getStatus())
                .setRemark(dto.getRemark());
        brandMapper.updateById(brand);
    }

    @Override
    public void deleteBrand(Long brandId) {
        brandMapper.deleteById(brandId);
    }
}
