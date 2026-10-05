package com.mfnit.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.stock.client.ProductFeignClient;
import com.mfnit.stock.client.dto.ProductDTO;
import com.mfnit.stock.constant.StockBizType;
import com.mfnit.stock.constant.StockFlowType;
import com.mfnit.stock.constant.StockMode;
import com.mfnit.stock.dto.*;
import com.mfnit.stock.entity.StockFlow;
import com.mfnit.stock.entity.StoreStock;
import com.mfnit.stock.mapper.StockFlowMapper;
import com.mfnit.stock.mapper.StoreStockMapper;
import com.mfnit.stock.service.StockService;
import com.mfnit.stock.vo.StockFlowVO;
import com.mfnit.stock.vo.StoreStockVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 12:39
 * @Description SOMS 库存服务实现类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StoreStockMapper stockMapper;
    private final StockFlowMapper flowMapper;
    private final ProductFeignClient productFeignClient;
    // ==================== 入库 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockIn(StockInDTO dto) {
        if (dto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("入库数量必须大于0");
        }

        StoreStock stock = getOrCreateStock(dto.getStoreId(), dto.getProductId());

        BigDecimal before = stock.getQuantity();
        BigDecimal after = before.add(dto.getQuantity());

        stock.setQuantity(after);
        if (dto.getCostPrice() != null) {
            stock.setCostPrice(dto.getCostPrice());
        }
        stockMapper.updateById(stock);

        writeFlow(stock, StockFlowType.IN.getCode(), dto.getQuantity(), BigDecimal.ZERO,
                before, after, stock.getLockedQuantity(), stock.getLockedQuantity(),
                dto.getBizType(), dto.getBizId(), dto.getBizNo(),
                dto.getOperatorId(), dto.getRemark());
    }

    // ==================== 出库 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockOut(StockOutDTO dto) {
        if (dto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("出库数量必须大于0");
        }

        StoreStock stock = stockMapper.selectForUpdate(dto.getStoreId(), dto.getProductId());
        if (stock == null) {
            throw new BusinessException("库存记录不存在");
        }

        BigDecimal before = stock.getQuantity();
        BigDecimal after = before.subtract(dto.getQuantity());

        // 出库不能扣到负库存（手工出库严格校验）
        if (after.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("库存不足，当前：" + before);
        }

        stock.setQuantity(after);
        stockMapper.updateById(stock);

        writeFlow(stock, StockFlowType.OUT.getCode(), dto.getQuantity().negate(), BigDecimal.ZERO,
                before, after, stock.getLockedQuantity(), stock.getLockedQuantity(),
                dto.getBizType(), dto.getBizId(), dto.getBizNo(),
                dto.getOperatorId(), dto.getRemark());
    }

    // ==================== 锁定 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockStock(StockLockDTO dto) {
        // 按 productId 排序，避免死锁
        List<StockItem> items = dto.getItems().stream()
                .sorted(Comparator.comparing(StockItem::getProductId))
                .toList();
        List<Long> productIds = items.stream().map(StockItem::getProductId).toList();

        List<StoreStock> stocks = stockMapper.selectListForUpdate(dto.getStoreId(), productIds);
        Map<Long, StoreStock> stockMap = stocks.stream()
                .collect(Collectors.toMap(StoreStock::getProductId, Function.identity()));

        // 先检查
        for (StockItem item : items) {
            StoreStock stock = stockMap.get(item.getProductId());
            if (stock == null) {
                throw new BusinessException("库存记录不存在：" + item.getProductName());
            }
            Result<ProductDTO> result = productFeignClient.getProduct(item.getProductId());
            if (result.getCode() != 0 || result.getData() == null) {
                throw new BusinessException("商品不存在：" + item.getProductId());
            }
            ProductDTO product = result.getData();
            if (product == null) {
                throw new BusinessException("商品不存在");
            }
            BigDecimal available = stock.getQuantity().subtract(stock.getLockedQuantity());
            if (product.getStockMode() != null
                    && product.getStockMode() == StockMode.STRICT.getCode()) {
                // 严格模式
                if (available.compareTo(item.getQuantity()) < 0) {
                    throw new BusinessException("商品【" + item.getProductName() + "】库存不足，剩余：" + available);
                }
            } else {
                // 宽松模式
                if (available.compareTo(item.getQuantity()) < 0) {
                    log.warn("商品【{}】库存不足，可用：{}，需求：{}，允许负库存销售",
                            item.getProductName(), available, item.getQuantity());
                }
            }
        }

        // 执行锁定
        for (StockItem item : items) {
            StoreStock stock = stockMap.get(item.getProductId());
            BigDecimal beforeLocked = stock.getLockedQuantity();
            BigDecimal afterLocked = beforeLocked.add(item.getQuantity());
            stock.setLockedQuantity(afterLocked);
            stockMapper.updateById(stock);

            writeFlow(stock, StockFlowType.LOCK.getCode(), BigDecimal.ZERO, item.getQuantity(),
                    stock.getQuantity(), stock.getQuantity(),
                    beforeLocked, afterLocked,
                    2, dto.getOrderId(), dto.getOrderNo(), null, "订单锁定");
        }
    }

    // ==================== 扣减 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(StockDeductDTO dto) {
        List<StockItem> items = dto.getItems().stream()
                .sorted(Comparator.comparing(StockItem::getProductId))
                .toList();
        List<Long> productIds = items.stream().map(StockItem::getProductId).toList();

        List<StoreStock> stocks = stockMapper.selectListForUpdate(dto.getStoreId(), productIds);
        Map<Long, StoreStock> stockMap = stocks.stream()
                .collect(Collectors.toMap(StoreStock::getProductId, Function.identity()));

        for (StockItem item : items) {
            StoreStock stock = stockMap.get(item.getProductId());
            if (stock == null) {
                throw new BusinessException("库存记录不存在：" + item.getProductName());
            }

            BigDecimal beforeQty = stock.getQuantity();
            BigDecimal beforeLocked = stock.getLockedQuantity();
            BigDecimal afterQty = beforeQty.subtract(item.getQuantity());
            BigDecimal afterLocked = beforeLocked.subtract(item.getQuantity());

            if (afterLocked.compareTo(BigDecimal.ZERO) < 0) {
                log.warn("解锁数量超过锁定数量，商品：{}，锁定：{}，扣减：{}",
                        item.getProductName(), beforeLocked, item.getQuantity());
                afterLocked = BigDecimal.ZERO;
            }

            stock.setQuantity(afterQty);
            stock.setLockedQuantity(afterLocked);
            stockMapper.updateById(stock);

            writeFlow(stock, StockFlowType.DEDUCT.getCode(),
                    item.getQuantity().negate(), item.getQuantity().negate(),
                    beforeQty, afterQty, beforeLocked, afterLocked,
                    StockBizType.ORDER.getCode(), dto.getOrderId(), dto.getOrderNo(), null, "订单支付扣减");
        }
    }

    // ==================== 解锁 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseStock(StockReleaseDTO dto) {
        List<StockItem> items = dto.getItems().stream()
                .sorted(Comparator.comparing(StockItem::getProductId))
                .toList();
        List<Long> productIds = items.stream().map(StockItem::getProductId).toList();

        List<StoreStock> stocks = stockMapper.selectListForUpdate(dto.getStoreId(), productIds);
        Map<Long, StoreStock> stockMap = stocks.stream()
                .collect(Collectors.toMap(StoreStock::getProductId, Function.identity()));

        for (StockItem item : items) {
            StoreStock stock = stockMap.get(item.getProductId());
            if (stock == null) {
                continue;
            }
            BigDecimal beforeLocked = stock.getLockedQuantity();
            BigDecimal afterLocked = beforeLocked.subtract(item.getQuantity());
            if (afterLocked.compareTo(BigDecimal.ZERO) < 0) {
                afterLocked = BigDecimal.ZERO;
            }
            stock.setLockedQuantity(afterLocked);
            stockMapper.updateById(stock);

            writeFlow(stock, StockFlowType.UNLOCK.getCode(), BigDecimal.ZERO, item.getQuantity().negate(),
                    stock.getQuantity(), stock.getQuantity(),
                    beforeLocked, afterLocked,
                    2, dto.getOrderId(), dto.getOrderNo(), null, "订单取消解锁");
        }
    }

    // ==================== 查询 ====================

    @Override
    public StoreStockVO getStock(Long storeId, Long productId) {
        StoreStock stock = stockMapper.selectOne(
                new LambdaQueryWrapper<StoreStock>()
                        .eq(StoreStock::getStoreId, storeId)
                        .eq(StoreStock::getProductId, productId));
        return toStockVO(stock);
    }

    @Override
    public List<StoreStockVO> listStock(Long storeId, List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<StoreStock> list = stockMapper.selectList(
                new LambdaQueryWrapper<StoreStock>()
                        .eq(StoreStock::getStoreId, storeId)
                        .in(StoreStock::getProductId, productIds));
        return list.stream().map(this::toStockVO).toList();
    }

    @Override
    public PageResult<StoreStockVO> pageStock(StockQueryDTO dto) {
        Page<StoreStock> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<StoreStock> wrapper = new LambdaQueryWrapper<>();
        if (dto.getStoreId() != null) {
            wrapper.eq(StoreStock::getStoreId, dto.getStoreId());
        }
        if (dto.getProductId() != null) {
            wrapper.eq(StoreStock::getProductId, dto.getProductId());
        }
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w.like(StoreStock::getProductName, dto.getKeyword())
                    .or().like(StoreStock::getProductCode, dto.getKeyword()));
        }
        if (dto.getWarnOnly() != null && dto.getWarnOnly() == 1) {
            wrapper.apply("quantity <= stock_warn");
        }
        wrapper.orderByDesc(StoreStock::getGmtModified);

        Page<StoreStock> result = stockMapper.selectPage(page, wrapper);
        List<StoreStockVO> records = result.getRecords().stream().map(this::toStockVO).toList();

        return new PageResult<StoreStockVO>()
                .setRecords(records)
                .setTotal(result.getTotal())
                .setSize(result.getSize())
                .setCurrent(result.getCurrent())
                .setPages(result.getPages());
    }

    @Override
    public PageResult<StockFlowVO> pageFlow(StockQueryDTO dto, Integer flowType) {
        Page<StockFlow> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<StockFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getStoreId() != null, StockFlow::getStoreId, dto.getStoreId())
                .eq(dto.getProductId() != null, StockFlow::getProductId, dto.getProductId())
                .eq(flowType != null, StockFlow::getFlowType, flowType)
                .orderByDesc(StockFlow::getGmtCreate);

        Page<StockFlow> result = flowMapper.selectPage(page, wrapper);
        List<StockFlowVO> records = result.getRecords().stream().map(f -> {
            StockFlowVO vo = new StockFlowVO();
            BeanUtils.copyProperties(f, vo);
            vo.setFlowTypeName(StockFlowType.getText(f.getFlowType()));
            return vo;
        }).toList();

        return new PageResult<StockFlowVO>()
                .setRecords(records)
                .setTotal(result.getTotal())
                .setSize(result.getSize())
                .setCurrent(result.getCurrent())
                .setPages(result.getPages());
    }

    // ==================== 私有方法 ====================

    private StoreStock getOrCreateStock(Long storeId, Long productId) {
        StoreStock stock = stockMapper.selectOne(
                new LambdaQueryWrapper<StoreStock>()
                        .eq(StoreStock::getStoreId, storeId)
                        .eq(StoreStock::getProductId, productId));
        if (stock == null) {
            Result<ProductDTO> result = productFeignClient.getProduct(productId);
            if (result.getCode() != 0 || result.getData() == null) {
                throw new BusinessException("商品不存在：" + productId);
            }
            ProductDTO product = result.getData();
            stock = new StoreStock()
                    .setStoreId(storeId)
                    .setProductId(productId)
                    .setProductCode(product.getProductCode())
                    .setProductName(product.getProductName())
                    .setQuantity(BigDecimal.ZERO)
                    .setLockedQuantity(BigDecimal.ZERO)
                    .setStockWarn(product.getStockWarn());
            stockMapper.insert(stock);
        }
        return stock;
    }

    private void writeFlow(StoreStock stock, int flowType,
                           BigDecimal changeQty, BigDecimal changeLocked,
                           BigDecimal beforeQty, BigDecimal afterQty,
                           BigDecimal beforeLocked, BigDecimal afterLocked,
                           Integer bizType, Long bizId, String bizNo,
                           Long operatorId, String remark) {
        StockFlow flow = new StockFlow()
                .setStoreId(stock.getStoreId())
                .setProductId(stock.getProductId())
                .setProductName(stock.getProductName())
                .setFlowType(flowType)
                .setChangeQuantity(changeQty)
                .setChangeLocked(changeLocked)
                .setBeforeQuantity(beforeQty)
                .setAfterQuantity(afterQty)
                .setBeforeLocked(beforeLocked)
                .setAfterLocked(afterLocked)
                .setBizType(bizType)
                .setBizId(bizId)
                .setBizNo(bizNo)
                .setOperatorId(operatorId)
                .setRemark(remark);
        flowMapper.insert(flow);
    }

    private StoreStockVO toStockVO(StoreStock stock) {
        if (stock == null) return null;
        StoreStockVO vo = new StoreStockVO();
        BeanUtils.copyProperties(stock, vo);

        // 预警状态
        BigDecimal available = stock.getQuantity().subtract(stock.getLockedQuantity());
        if (stock.getStockWarn() != null) {
            if (available.compareTo(BigDecimal.ZERO) <= 0) {
                vo.setWarnStatus(3);
            } else if (available.compareTo(new BigDecimal(stock.getStockWarn())) <= 0) {
                vo.setWarnStatus(2);
            } else {
                vo.setWarnStatus(1);
            }
        } else {
            vo.setWarnStatus(available.compareTo(BigDecimal.ZERO) <= 0 ? 3 : 1);
        }
        return vo;
    }
}