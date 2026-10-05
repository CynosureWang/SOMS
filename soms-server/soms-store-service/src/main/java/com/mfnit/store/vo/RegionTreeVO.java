package com.mfnit.store.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/5 14:25
 * @Description SOMS 区域树VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class RegionTreeVO {
    private Long regionId;
    private String regionCode;
    private String regionName;
    private Integer regionType;
    private Long parentId;
    private Integer level;
    private Integer sort;
    private Integer status;
    private List<RegionTreeVO> children;
}
