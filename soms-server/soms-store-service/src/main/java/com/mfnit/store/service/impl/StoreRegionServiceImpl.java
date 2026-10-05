package com.mfnit.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.store.dto.RegionCreateDTO;
import com.mfnit.store.dto.RegionUpdateDTO;
import com.mfnit.store.entity.StoreRegion;
import com.mfnit.store.mapper.StoreRegionMapper;
import com.mfnit.store.service.StoreRegionService;
import com.mfnit.store.vo.RegionTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:27
 * @Description SOMS 门店区域服务实现
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Service
@RequiredArgsConstructor
public class StoreRegionServiceImpl implements StoreRegionService {

    private final StoreRegionMapper regionMapper;

    @Override
    public List<RegionTreeVO> getTree(Integer regionType) {
        LambdaQueryWrapper<StoreRegion> wrapper = new LambdaQueryWrapper<>();
        if (regionType != null) {
            wrapper.le(StoreRegion::getRegionType, regionType);
        }
        wrapper.orderByAsc(StoreRegion::getSort);
        List<StoreRegion> all = regionMapper.selectList(wrapper);

        Map<Long, List<StoreRegion>> childrenMap = all.stream()
                .collect(Collectors.groupingBy(StoreRegion::getParentId));

        List<StoreRegion> roots = childrenMap.getOrDefault(0L, Collections.emptyList());
        return roots.stream().map(r -> buildTree(r, childrenMap)).toList();
    }

    private RegionTreeVO buildTree(StoreRegion node, Map<Long, List<StoreRegion>> childrenMap) {
        RegionTreeVO vo = new RegionTreeVO()
                .setRegionId(node.getRegionId())
                .setRegionCode(node.getRegionCode())
                .setRegionName(node.getRegionName())
                .setRegionType(node.getRegionType())
                .setParentId(node.getParentId())
                .setLevel(node.getLevel())
                .setSort(node.getSort())
                .setStatus(node.getStatus());

        List<StoreRegion> children = childrenMap.get(node.getRegionId());
        if (children != null && !children.isEmpty()) {
            vo.setChildren(children.stream().map(c -> buildTree(c, childrenMap)).toList());
        }
        return vo;
    }

    @Override
    public Long createRegion(RegionCreateDTO dto) {
        Long count = regionMapper.selectCount(
                new LambdaQueryWrapper<StoreRegion>()
                        .eq(StoreRegion::getRegionCode, dto.getRegionCode()));
        if (count > 0) {
            throw new BusinessException("区域编码已存在");
        }

        StoreRegion region = new StoreRegion()
                .setRegionCode(dto.getRegionCode())
                .setRegionName(dto.getRegionName())
                .setRegionType(dto.getRegionType())
                .setParentId(dto.getParentId())
                .setSort(dto.getSort())
                .setRemark(dto.getRemark())
                .setStatus(1);

        if (dto.getParentId() == null || dto.getParentId() == 0L) {
            region.setLevel(1).setParentId(0L);
        } else {
            StoreRegion parent = regionMapper.selectById(dto.getParentId());
            if (parent == null) {
                throw new BusinessException("上级区域不存在");
            }
            region.setLevel(parent.getLevel() + 1);
        }

        regionMapper.insert(region);

        // 回填 path
        String path = region.getParentId() == 0L
                ? "/" + region.getRegionId() + "/"
                : regionMapper.selectById(region.getParentId()).getPath() + region.getRegionId() + "/";
        region.setPath(path);
        regionMapper.updateById(region);

        return region.getRegionId();
    }

    @Override
    public void updateRegion(RegionUpdateDTO dto) {
        StoreRegion region = regionMapper.selectById(dto.getRegionId());
        if (region == null) {
            throw new BusinessException("区域不存在");
        }
        region.setRegionName(dto.getRegionName())
                .setSort(dto.getSort())
                .setStatus(dto.getStatus())
                .setRemark(dto.getRemark());
        regionMapper.updateById(region);
    }

    @Override
    public void deleteRegion(Long regionId) {
        Long childCount = regionMapper.selectCount(
                new LambdaQueryWrapper<StoreRegion>()
                        .eq(StoreRegion::getParentId, regionId));
        if (childCount > 0) {
            throw new BusinessException("存在子区域，不能删除");
        }
        regionMapper.deleteById(regionId);
    }
}
