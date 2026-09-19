package com.mfnit.store.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 02:45
 * @Description 门店组织层级实体类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("store_region")
public class StoreRegion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 区域ID
     */
    @TableId(value = "region_id", type = IdType.ASSIGN_ID)
    private Long regionId;

    /**
     * 区域编码
     */
    private String regionCode;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 区域类型：1大区 2城市 3片区
     */
    private Integer regionType;

    /**
     * 上级区域ID
     */
    private Long parentId;

    /**
     * 层级：1大区 2城市 3片区
     */
    private Integer level;

    /**
     * 路径，如 /1/10/100/
     */
    private String path;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;

    /**
     * 逻辑删除：0未删除，1已删除
     */
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}
