package com.mfnit.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.order.client.ProductFeignClient;
import com.mfnit.order.client.StockFeignClient;
import com.mfnit.order.client.dto.ProductDTO;
import com.mfnit.order.client.dto.StockDeductDTO;
import com.mfnit.order.client.dto.StockItem;
import com.mfnit.order.client.dto.StockLockDTO;
import com.mfnit.order.client.dto.StockReleaseDTO;
import com.mfnit.order.constant.OrderStatus;
import com.mfnit.order.constant.OrderType;
import com.mfnit.order.constant.PayStatus;
import com.mfnit.order.dto.OrderCancelDTO;
import com.mfnit.order.dto.OrderCreateDTO;
import com.mfnit.order.dto.OrderItemDTO;
import com.mfnit.order.dto.OrderPaySuccessDTO;
import com.mfnit.order.dto.OrderQueryDTO;
import com.mfnit.order.entity.Order;
import com.mfnit.order.entity.OrderItem;
import com.mfnit.order.entity.OrderStatusLog;
import com.mfnit.order.mapper.OrderItemMapper;
import com.mfnit.order.mapper.OrderMapper;
import com.mfnit.order.mapper.OrderStatusLogMapper;
import com.mfnit.order.service.OrderNoGenerator;
import com.mfnit.order.service.OrderService;
import com.mfnit.order.vo.OrderDetailVO;
import com.mfnit.order.vo.OrderItemVO;
import com.mfnit.order.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/3 14:15
 * @Description SOMS 订单服务实现类
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    /** 订单超时时间：15 分钟 */
    private static final int TIMEOUT_MINUTES = 15;

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderStatusLogMapper statusLogMapper;
    private final OrderNoGenerator orderNoGenerator;
    private final ProductFeignClient productFeignClient;
    private final StockFeignClient stockFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDetailVO createOrder(OrderCreateDTO dto) {
        // 1. 查商品，构建明细（不变）
        List<OrderItem> items = new ArrayList<>();
        List<StockItem> stockItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        int itemCount = 0;

        for (OrderItemDTO itemDTO : dto.getItems()) {
            Result<ProductDTO> result = productFeignClient.getProduct(itemDTO.getProductId());
            if (result.getCode() != 0 || result.getData() == null) {
                throw new BusinessException("商品不存在：" + itemDTO.getProductId());
            }
            ProductDTO product = result.getData();
            if (product.getStatus() != 1) {
                throw new BusinessException("商品已下架：" + product.getProductName());
            }

            BigDecimal qty = itemDTO.getQuantity();
            BigDecimal subtotal = product.getPrice().multiply(qty)
                    .setScale(2, RoundingMode.HALF_UP);

            // 构建订单明细
            OrderItem item = new OrderItem()
                    .setProductId(product.getProductId())
                    .setBarcode(itemDTO.getBarcode())
                    .setScaleLabelId(itemDTO.getScaleLabelId())
                    .setProductName(product.getProductName())
                    .setSpecText(product.getSpecText())
                    .setUnit(product.getUnit())
                    .setMainImage(product.getMainImage())
                    .setCategoryId(product.getCategoryId())
                    .setIsWeight(product.getIsWeight())
                    .setPrice(product.getPrice())
                    .setQuantity(qty)
                    .setTotalAmount(subtotal)
                    .setDiscountAmount(BigDecimal.ZERO)
                    .setPayAmount(subtotal)
                    .setRefundQuantity(BigDecimal.ZERO)
                    .setRefundAmount(BigDecimal.ZERO);
            items.add(item);

            // 构建库存项
            StockItem si = new StockItem();
            si.setProductId(product.getProductId());
            si.setProductName(product.getProductName());
            si.setQuantity(qty);
            stockItems.add(si);   // ← 关键，之前漏了

            totalAmount = totalAmount.add(subtotal);
            itemCount += qty.intValue();
        }

        // 2. 预生成订单ID和订单号
        Long orderId = IdWorker.getId();
        String orderNo = orderNoGenerator.generate(dto.getStoreId());

        log.info("准备锁定库存：storeId={}, orderId={}, orderNo={}, items={}",
                dto.getStoreId(), orderId, orderNo, stockItems);

        // 3. 调 stock 锁库存（用预生成的 orderId）
        StockLockDTO lockDTO = new StockLockDTO();
        lockDTO.setStoreId(dto.getStoreId());
        lockDTO.setItems(stockItems);
        lockDTO.setOrderId(orderId);       // ← 关键，传预生成的 ID
        lockDTO.setOrderNo(orderNo);

        Result<Void> lockResult = stockFeignClient.lock(lockDTO);
        if (lockResult.getCode() != 0) {
            throw new BusinessException("锁定库存失败：" + lockResult.getMessage());
        }

        // 4. 保存订单（用同一个 orderId）
        try {
            Order order = new Order()
                    .setOrderId(orderId)        // ← 显式设置
                    .setOrderNo(orderNo)
                    .setStoreId(dto.getStoreId())
                    .setCustomerId(dto.getCustomerId())
                    .setOrderType(dto.getOrderType())
                    .setTotalAmount(totalAmount)
                    .setDiscountAmount(BigDecimal.ZERO)
                    .setPayAmount(totalAmount)
                    .setPaidAmount(BigDecimal.ZERO)
                    .setItemCount(itemCount)
                    .setStatus(OrderStatus.PENDING_PAY.getCode())
                    .setPayStatus(PayStatus.UNPAID.getCode())
                    .setRemark(dto.getRemark());
            orderMapper.insert(order);

            for (OrderItem item : items) {
                item.setOrderId(orderId).setOrderNo(orderNo);
                orderItemMapper.insert(item);
            }

            writeStatusLog(orderId, orderNo, null,
                    OrderStatus.PENDING_PAY.getCode(), null, 2, "创建订单");

            return buildDetail(order, items);
        } catch (Exception e) {
            log.error("保存订单失败，释放库存：{}", orderNo, e);
            try {
                StockReleaseDTO releaseDTO = new StockReleaseDTO();
                releaseDTO.setStoreId(dto.getStoreId());
                releaseDTO.setItems(stockItems);
                releaseDTO.setOrderId(orderId);   // ← 传进去
                releaseDTO.setOrderNo(orderNo);
                stockFeignClient.release(releaseDTO);
            } catch (Exception ex) {
                log.error("释放库存也失败：{}", orderNo, ex);
            }
            throw e;
        }
    }

    @Override
    public OrderDetailVO getDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, orderId));
        return buildDetail(order, items);
    }

    @Override
    public OrderDetailVO getByOrderNo(String orderNo) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo));
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, order.getOrderId()));
        return buildDetail(order, items);
    }

    @Override
    public PageResult<OrderVO> pageOrder(OrderQueryDTO dto) {
        Page<Order> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getStoreId() != null, Order::getStoreId, dto.getStoreId())
                .eq(dto.getCustomerId() != null, Order::getCustomerId, dto.getCustomerId())
                .eq(dto.getStatus() != null, Order::getStatus, dto.getStatus())
                .eq(dto.getOrderType() != null, Order::getOrderType, dto.getOrderType())
                .like(StringUtils.hasText(dto.getOrderNo()), Order::getOrderNo, dto.getOrderNo())
                .ge(dto.getStartDate() != null, Order::getGmtCreate, dto.getStartDate() == null ? null : dto.getStartDate().atStartOfDay())
                .le(dto.getEndDate() != null, Order::getGmtCreate, dto.getEndDate() == null ? null : dto.getEndDate().atTime(23, 59, 59))
                .orderByDesc(Order::getGmtCreate);

        Page<Order> result = orderMapper.selectPage(page, wrapper);
        List<OrderVO> records = result.getRecords().stream().map(this::toVO).toList();

        return new PageResult<OrderVO>()
                .setRecords(records)
                .setTotal(result.getTotal())
                .setSize(result.getSize())
                .setCurrent(result.getCurrent())
                .setPages(result.getPages());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(OrderCancelDTO dto) {
        Order order = orderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAY.getCode()) {
            throw new BusinessException("只有待支付订单可以取消");
        }
        doCancel(order, dto.getReason(), null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void paySuccess(OrderPaySuccessDTO dto) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, dto.getOrderNo()));
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAY.getCode()) {
            throw new BusinessException("订单状态不允许支付");
        }

        // 1. 扣库存
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, order.getOrderId()));
        List<StockItem> stockItems = items.stream().map(i -> {
            StockItem si = new StockItem();
            si.setProductId(i.getProductId());
            si.setProductName(i.getProductName());
            si.setQuantity(i.getQuantity());
            return si;
        }).toList();

        StockDeductDTO deductDTO = new StockDeductDTO();
        deductDTO.setStoreId(order.getStoreId());
        deductDTO.setItems(stockItems);
        deductDTO.setOrderId(order.getOrderId());
        deductDTO.setOrderNo(order.getOrderNo());

        Result<Void> deductResult = stockFeignClient.deduct(deductDTO);
        if (deductResult.getCode() != 0) {
            throw new BusinessException("扣减库存失败：" + deductResult.getMessage());
        }

        // 2. 更新订单
        Integer fromStatus = order.getStatus();
        order.setStatus(OrderStatus.PAID.getCode())
                .setPayStatus(PayStatus.PAID.getCode())
                .setPayType(dto.getPayType())
                .setPaidAmount(dto.getPaidAmount())
                .setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);

        writeStatusLog(order.getOrderId(), order.getOrderNo(), fromStatus,
                OrderStatus.PAID.getCode(), null, 2, "支付成功");

        // 3. POS 即时完成
        if (order.getOrderType() == OrderType.POS.getCode()
                || order.getOrderType() == OrderType.SELF_CHECKOUT.getCode()) {
            finishOrderInternal(order);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.PAID.getCode()) {
            throw new BusinessException("只有已支付订单可以完成");
        }
        finishOrderInternal(order);
    }

    @Override
    public int cancelTimeoutOrders() {
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(TIMEOUT_MINUTES);
        List<Order> orders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getStatus, OrderStatus.PENDING_PAY.getCode())
                        .lt(Order::getGmtCreate, timeout));

        int count = 0;
        for (Order order : orders) {
            try {
                doCancel(order, "超时未支付，系统自动取消", null);
                count++;
            } catch (Exception e) {
                log.error("取消超时订单失败：{}", order.getOrderNo(), e);
            }
        }
        return count;
    }

    // ==================== 私有方法 ====================

    private void doCancel(Order order, String reason, Long operatorId) {
        // 释放库存
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, order.getOrderId()));
        List<StockItem> stockItems = items.stream().map(i -> {
            StockItem si = new StockItem();
            si.setProductId(i.getProductId());
            si.setProductName(i.getProductName());
            si.setQuantity(i.getQuantity());
            return si;
        }).toList();

        StockReleaseDTO releaseDTO = new StockReleaseDTO();
        releaseDTO.setStoreId(order.getStoreId());
        releaseDTO.setItems(stockItems);
        releaseDTO.setOrderId(order.getOrderId());
        releaseDTO.setOrderNo(order.getOrderNo());

        Result<Void> releaseResult = stockFeignClient.release(releaseDTO);
        if (releaseResult.getCode() != 0) {
            throw new BusinessException("释放库存失败：" + releaseResult.getMessage());
        }

        Integer fromStatus = order.getStatus();
        order.setStatus(OrderStatus.CANCELLED.getCode())
                .setCancelTime(LocalDateTime.now())
                .setCancelReason(reason);
        orderMapper.updateById(order);

        writeStatusLog(order.getOrderId(), order.getOrderNo(), fromStatus,
                OrderStatus.CANCELLED.getCode(), operatorId, operatorId == null ? 2 : 3, reason);
    }

    private void finishOrderInternal(Order order) {
        Integer fromStatus = order.getStatus();
        order.setStatus(OrderStatus.FINISHED.getCode())
                .setFinishTime(LocalDateTime.now());
        orderMapper.updateById(order);
        writeStatusLog(order.getOrderId(), order.getOrderNo(), fromStatus,
                OrderStatus.FINISHED.getCode(), null, 2, "订单完成");
    }

    private void writeStatusLog(Long orderId, String orderNo,
                                Integer fromStatus, Integer toStatus,
                                Long operatorId, Integer operatorType, String remark) {
        OrderStatusLog log = new OrderStatusLog()
                .setOrderId(orderId)
                .setOrderNo(orderNo)
                .setFromStatus(fromStatus)
                .setToStatus(toStatus)
                .setOperatorId(operatorId)
                .setOperatorType(operatorType)
                .setRemark(remark);
        statusLogMapper.insert(log);
    }

    private OrderVO toVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setStatusName(OrderStatus.getText(order.getStatus()));
        vo.setOrderTypeName(OrderType.values()[order.getOrderType() - 1].getText());
        return vo;
    }

    private OrderDetailVO buildDetail(Order order, List<OrderItem> items) {
        OrderDetailVO vo = new OrderDetailVO();
        BeanUtils.copyProperties(order, vo);
        vo.setStatusName(OrderStatus.getText(order.getStatus()));
        vo.setOrderTypeName(OrderType.values()[order.getOrderType() - 1].getText());

        List<OrderItemVO> itemVOs = items.stream().map(i -> {
            OrderItemVO iv = new OrderItemVO();
            BeanUtils.copyProperties(i, iv);
            return iv;
        }).toList();
        vo.setItems(itemVOs);
        return vo;
    }
}