package com.mfnit.product.service;

import com.mfnit.product.dto.ScaleLabelGenerateDTO;
import com.mfnit.product.dto.ScaleLabelMarkDTO;
import com.mfnit.product.vo.ScaleLabelVO;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/2 16:11
 * @Description SOMS 标签服务
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface ScaleLabelService {
    ScaleLabelVO generateLabel(ScaleLabelGenerateDTO dto);
    ScaleLabelVO getLabel(String barcode);
    void markSold(ScaleLabelMarkDTO dto);
}
