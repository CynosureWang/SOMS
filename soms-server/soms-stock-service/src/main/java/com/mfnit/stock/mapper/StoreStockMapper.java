package com.mfnit.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mfnit.stock.entity.StoreStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 12:32
 * @Description SOMS 门店库存Mapper
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Mapper
public interface StoreStockMapper extends BaseMapper<StoreStock> {

    /**
     * 悲观锁查询：按 store + product 加行锁
     */
    @Select("SELECT * FROM store_stock WHERE store_id = #{storeId} AND product_id = #{productId} AND is_deleted = 0 FOR UPDATE")
    StoreStock selectForUpdate(@Param("storeId") Long storeId, @Param("productId") Long productId);

    /**
     * 批量悲观锁查询，按 product_id 排序，避免死锁
     */
    @Select("<script>" +
            "SELECT * FROM store_stock WHERE store_id = #{storeId} " +
            "AND product_id IN " +
            "<foreach collection='productIds' item='pid' open='(' separator=',' close=')'>#{pid}</foreach>" +
            " AND is_deleted = 0 ORDER BY product_id FOR UPDATE" +
            "</script>")
    List<StoreStock> selectListForUpdate(@Param("storeId") Long storeId, @Param("productIds") List<Long> productIds);
}
