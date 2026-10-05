package com.mfnit.store.service;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.store.dto.*;
import com.mfnit.store.vo.StoreDetailVO;
import com.mfnit.store.vo.StoreVO;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:27
 * @Description SOMS 门店服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface StoreService {
    PageResult<StoreVO> pageStore(StoreQueryDTO dto);
    StoreDetailVO getDetail(Long storeId);
    Long createStore(StoreCreateDTO dto);
    void updateStore(StoreUpdateDTO dto);
    void deleteStore(Long storeId);
    void changeStatus(Long storeId, Integer businessStatus);
    void updateDetail(StoreDetailUpdateDTO dto);

    /** 内部接口：单门店基础信息 */
    StoreVO getSimple(Long storeId);
    /** 内部接口：批量 */
    List<StoreVO> listByIds(List<Long> storeIds);

    /** 内部接口：判断门店是否可交易 */
    boolean canTrade(Long storeId);
}
