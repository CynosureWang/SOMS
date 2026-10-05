package com.mfnit.discount.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mfnit.discount.entity.PromotionPriceTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 15:18
 * @Description SOMS 优惠券价格标签Mapper接口
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Mapper
public interface PromotionPriceTagMapper extends BaseMapper<PromotionPriceTag> {
    @Select("SELECT * FROM promotion_price_tag " +
            "WHERE source_barcode = #{sourceBarcode} " +
            "AND invalid_source = 1 AND status = 0 " +
            "ORDER BY gmt_create DESC LIMIT 1")
    PromotionPriceTag selectInvalidBySourceBarcode(@Param("sourceBarcode") String sourceBarcode);
}
