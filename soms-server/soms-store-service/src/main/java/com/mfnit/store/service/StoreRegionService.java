package com.mfnit.store.service;

import com.mfnit.store.dto.RegionCreateDTO;
import com.mfnit.store.dto.RegionUpdateDTO;
import com.mfnit.store.vo.RegionTreeVO;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:26
 * @Description SOMS 门店区域服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface StoreRegionService {
    List<RegionTreeVO> getTree(Integer regionType);
    Long createRegion(RegionCreateDTO dto);
    void updateRegion(RegionUpdateDTO dto);
    void deleteRegion(Long regionId);
}