package com.mfnit.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.store.dto.*;
import com.mfnit.store.entity.Store;
import com.mfnit.store.entity.StoreBusinessHours;
import com.mfnit.store.entity.StoreContact;
import com.mfnit.store.entity.StoreRegion;
import com.mfnit.store.mapper.StoreBusinessHoursMapper;
import com.mfnit.store.mapper.StoreContactMapper;
import com.mfnit.store.mapper.StoreMapper;
import com.mfnit.store.mapper.StoreRegionMapper;
import com.mfnit.store.service.StoreService;
import com.mfnit.store.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:27
 * @Description SOMS 门店服务实现类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreMapper storeMapper;
    private final StoreRegionMapper regionMapper;
    private final StoreBusinessHoursMapper hoursMapper;
    private final StoreContactMapper contactMapper;

    @Override
    public PageResult<StoreVO> pageStore(StoreQueryDTO dto) {
        Page<Store> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w.like(Store::getStoreName, dto.getKeyword())
                    .or().like(Store::getStoreCode, dto.getKeyword()));
        }
        wrapper.eq(dto.getRegionId() != null, Store::getRegionId, dto.getRegionId())
                .eq(dto.getCityId() != null, Store::getCityId, dto.getCityId())
                .eq(dto.getAreaId() != null, Store::getAreaId, dto.getAreaId())
                .eq(dto.getStoreType() != null, Store::getStoreType, dto.getStoreType())
                .eq(dto.getBusinessStatus() != null, Store::getBusinessStatus, dto.getBusinessStatus())
                .orderByDesc(Store::getGmtCreate);

        Page<Store> result = storeMapper.selectPage(page, wrapper);
        List<StoreVO> records = result.getRecords().stream()
                .map(this::toVO).toList();

        return new PageResult<StoreVO>()
                .setRecords(records)
                .setTotal(result.getTotal())
                .setSize(result.getSize())
                .setCurrent(result.getCurrent())
                .setPages(result.getPages());
    }

    @Override
    public StoreDetailVO getDetail(Long storeId) {
        Store s = storeMapper.selectById(storeId);
        if (s == null) {
            throw new BusinessException("门店不存在");
        }
        StoreDetailVO vo = new StoreDetailVO();
        BeanUtils.copyProperties(s, vo);
        fillRegionNames(vo, s);

        // 营业时间
        List<StoreBusinessHours> hours = hoursMapper.selectList(
                new LambdaQueryWrapper<StoreBusinessHours>()
                        .eq(StoreBusinessHours::getStoreId, storeId));
        vo.setBusinessHours(hours.stream().map(h -> {
            BusinessHoursVO hv = new BusinessHoursVO();
            BeanUtils.copyProperties(h, hv);
            return hv;
        }).toList());

        // 联系人
        List<StoreContact> contacts = contactMapper.selectList(
                new LambdaQueryWrapper<StoreContact>()
                        .eq(StoreContact::getStoreId, storeId));
        vo.setContacts(contacts.stream().map(c -> {
            ContactVO cv = new ContactVO();
            BeanUtils.copyProperties(c, cv);
            return cv;
        }).toList());

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStore(StoreCreateDTO dto) {
        Long count = storeMapper.selectCount(
                new LambdaQueryWrapper<Store>()
                        .eq(Store::getStoreCode, dto.getStoreCode()));
        if (count > 0) {
            throw new BusinessException("门店编码已存在");
        }

        Store store = new Store();
        BeanUtils.copyProperties(dto, store);
        store.setBusinessStatus(1);
        storeMapper.insert(store);
        return store.getStoreId();
    }

    @Override
    public void updateStore(StoreUpdateDTO dto) {
        Store store = storeMapper.selectById(dto.getStoreId());
        if (store == null) {
            throw new BusinessException("门店不存在");
        }
        BeanUtils.copyProperties(dto, store);
        storeMapper.updateById(store);
    }

    @Override
    public void deleteStore(Long storeId) {
        storeMapper.deleteById(storeId);
    }

    @Override
    public void changeStatus(Long storeId, Integer businessStatus) {
        Store store = storeMapper.selectById(storeId);
        if (store == null) {
            throw new BusinessException("门店不存在");
        }
        store.setBusinessStatus(businessStatus);
        storeMapper.updateById(store);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDetail(StoreDetailUpdateDTO dto) {
        Store store = storeMapper.selectById(dto.getStoreId());
        if (store == null) {
            throw new BusinessException("门店不存在");
        }

        // 先删旧的营业时间和联系人
        hoursMapper.delete(new LambdaQueryWrapper<StoreBusinessHours>()
                .eq(StoreBusinessHours::getStoreId, dto.getStoreId()));
        contactMapper.delete(new LambdaQueryWrapper<StoreContact>()
                .eq(StoreContact::getStoreId, dto.getStoreId()));

        // 插新的
        if (dto.getBusinessHours() != null) {
            for (BusinessHoursDTO h : dto.getBusinessHours()) {
                StoreBusinessHours entity = new StoreBusinessHours()
                        .setStoreId(dto.getStoreId())
                        .setDayType(h.getDayType())
                        .setOpenTime(h.getOpenTime())
                        .setCloseTime(h.getCloseTime())
                        .setIs24h(h.getIs24h());
                hoursMapper.insert(entity);
            }
        }

        if (dto.getContacts() != null) {
            for (ContactDTO c : dto.getContacts()) {
                StoreContact entity = new StoreContact()
                        .setStoreId(dto.getStoreId())
                        .setContactName(c.getContactName())
                        .setContactRole(c.getContactRole())
                        .setPhone(c.getPhone())
                        .setEmail(c.getEmail())
                        .setIsPrimary(c.getIsPrimary());
                contactMapper.insert(entity);
            }
        }
    }

    @Override
    public StoreVO getSimple(Long storeId) {
        Store store = storeMapper.selectById(storeId);
        return store == null ? null : toVO(store);
    }

    @Override
    public List<StoreVO> listByIds(List<Long> storeIds) {
        if (storeIds == null || storeIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Store> list = storeMapper.selectBatchIds(storeIds);
        return list.stream().map(this::toVO).toList();
    }

    @Override
    public boolean canTrade(Long storeId) {
        Store store = storeMapper.selectById(storeId);
        return store != null && store.getBusinessStatus() == 2;
    }

    // === 私有 ===

    private StoreVO toVO(Store s) {
        StoreVO vo = new StoreVO();
        BeanUtils.copyProperties(s, vo);
        vo.setBusinessStatusName(switch (s.getBusinessStatus()) {
            case 1 -> "筹备";
            case 2 -> "营业";
            case 3 -> "暂停";
            case 4 -> "闭店";
            default -> "";
        });
        fillRegionNames(vo, s);
        return vo;
    }

    private void fillRegionNames(StoreVO vo, Store s) {
        if (s.getRegionId() != null) {
            StoreRegion r = regionMapper.selectById(s.getRegionId());
            if (r != null) vo.setRegionName(r.getRegionName());
        }
        if (s.getCityId() != null) {
            StoreRegion r = regionMapper.selectById(s.getCityId());
            if (r != null) vo.setCityName(r.getRegionName());
        }
        if (s.getAreaId() != null) {
            StoreRegion r = regionMapper.selectById(s.getAreaId());
            if (r != null) vo.setAreaName(r.getRegionName());
        }
    }

    private void fillRegionNames(StoreDetailVO vo, Store s) {
        if (s.getRegionId() != null) {
            StoreRegion r = regionMapper.selectById(s.getRegionId());
            if (r != null) vo.setRegionName(r.getRegionName());
        }
        if (s.getCityId() != null) {
            StoreRegion r = regionMapper.selectById(s.getCityId());
            if (r != null) vo.setCityName(r.getRegionName());
        }
        if (s.getAreaId() != null) {
            StoreRegion r = regionMapper.selectById(s.getAreaId());
            if (r != null) vo.setAreaName(r.getRegionName());
        }
    }
}
