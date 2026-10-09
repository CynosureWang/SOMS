package com.mfnit.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfnit.common.api.dto.customer.BalanceDeductDTO;
import com.mfnit.common.api.dto.customer.ConsumeCompleteDTO;
import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.ResultCodeMessage;
import com.mfnit.common.core.exception.BusinessException;
import com.mfnit.customer.dto.RechargeDTO;
import com.mfnit.customer.entity.Customer;
import com.mfnit.customer.entity.CustomerBalanceLog;
import com.mfnit.customer.entity.CustomerPointsLog;
import com.mfnit.customer.mapper.CustomerBalanceLogMapper;
import com.mfnit.customer.mapper.CustomerMapper;
import com.mfnit.customer.mapper.CustomerPointsLogMapper;
import com.mfnit.customer.service.CustomerAccountService;
import com.mfnit.customer.vo.AccountVO;
import com.mfnit.customer.vo.BalanceLogVO;
import com.mfnit.customer.vo.PointsLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/22
 * @Description 客户账户服务实现类（余额/积分/等级/累计消费）
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Service
@RequiredArgsConstructor
public class CustomerAccountServiceImpl implements CustomerAccountService {

    /** 银卡升级门槛（累计消费金额，元），后续可迁移至 Nacos 配置 */
    private static final BigDecimal LEVEL2_THRESHOLD = new BigDecimal("1000");
    /** 金卡升级门槛（累计消费金额，元），后续可迁移至 Nacos 配置 */
    private static final BigDecimal LEVEL3_THRESHOLD = new BigDecimal("5000");
    /** 每消费 1 元获得的积分数，后续可迁移至 Nacos 配置 */
    private static final int POINTS_PER_YUAN = 1;

    /** 余额变动类型 */
    private static final int CHANGE_TYPE_RECHARGE = 1;
    private static final int CHANGE_TYPE_CONSUME = 2;

    /** 积分变动类型 */
    private static final int POINTS_TYPE_CONSUME = 1;

    private final CustomerMapper customerMapper;
    private final CustomerBalanceLogMapper balanceLogMapper;
    private final CustomerPointsLogMapper pointsLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountVO recharge(Long customerId, RechargeDTO dto) {
        Customer customer = getActiveCustomer(customerId);
        // 幂等：传入业务单号时，已处理过则直接返回当前账户
        if (StringUtils.hasText(dto.getBizId())) {
            Long exists = balanceLogMapper.selectCount(new LambdaQueryWrapper<CustomerBalanceLog>()
                    .eq(CustomerBalanceLog::getBizType, "RECHARGE")
                    .eq(CustomerBalanceLog::getBizId, dto.getBizId()));
            if (exists > 0) {
                return getAccount(customerId);
            }
        }
        BigDecimal before = customer.getBalance() == null ? BigDecimal.ZERO : customer.getBalance();
        BigDecimal amount = dto.getAmount();
        customerMapper.update(null, new LambdaUpdateWrapper<Customer>()
                .setSql("balance = balance + {0}", amount)
                .eq(Customer::getCustomerId, customerId));
        balanceLogMapper.insert(new CustomerBalanceLog()
                .setCustomerId(customerId)
                .setChangeType(CHANGE_TYPE_RECHARGE)
                .setChangeAmount(amount)
                .setBalanceBefore(before)
                .setBalanceAfter(before.add(amount))
                .setBizType("RECHARGE")
                .setBizId(dto.getBizId())
                .setRemark(dto.getRemark()));
        return getAccount(customerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountVO deductBalance(BalanceDeductDTO dto) {
        Customer customer = getActiveCustomer(dto.getCustomerId());
        // 幂等：同一业务单号已扣减过则直接返回当前账户
        Long exists = balanceLogMapper.selectCount(new LambdaQueryWrapper<CustomerBalanceLog>()
                .eq(CustomerBalanceLog::getBizType, dto.getBizType())
                .eq(CustomerBalanceLog::getBizId, dto.getBizId()));
        if (exists > 0) {
            return getAccount(dto.getCustomerId());
        }
        BigDecimal before = customer.getBalance() == null ? BigDecimal.ZERO : customer.getBalance();
        BigDecimal amount = dto.getAmount();
        // 原子扣减并校验余额充足
        boolean ok = customerMapper.update(null, new LambdaUpdateWrapper<Customer>()
                .setSql("balance = balance - {0}", amount)
                .eq(Customer::getCustomerId, dto.getCustomerId())
                .ge(Customer::getBalance, amount)) > 0;
        if (!ok) {
            throw new BusinessException(ResultCodeMessage.PARAM_ERROR.getCode(), "余额不足");
        }
        balanceLogMapper.insert(new CustomerBalanceLog()
                .setCustomerId(dto.getCustomerId())
                .setChangeType(CHANGE_TYPE_CONSUME)
                .setChangeAmount(amount.negate())
                .setBalanceBefore(before)
                .setBalanceAfter(before.subtract(amount))
                .setBizType(dto.getBizType())
                .setBizId(dto.getBizId())
                .setRemark(dto.getRemark()));
        return getAccount(dto.getCustomerId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountVO completeConsume(ConsumeCompleteDTO dto) {
        Customer customer = getActiveCustomer(dto.getCustomerId());
        // 幂等：同一业务单号已回调过则直接返回当前账户
        Long exists = pointsLogMapper.selectCount(new LambdaQueryWrapper<CustomerPointsLog>()
                .eq(CustomerPointsLog::getBizType, dto.getBizType())
                .eq(CustomerPointsLog::getBizId, dto.getBizId()));
        if (exists > 0) {
            return getAccount(dto.getCustomerId());
        }
        // 1. 累加累计消费
        customerMapper.update(null, new LambdaUpdateWrapper<Customer>()
                .setSql("total_consume = total_consume + {0}", dto.getConsumeAmount())
                .eq(Customer::getCustomerId, dto.getCustomerId()));
        // 2. 按消费金额赠送积分（1元=1积分）
        int points = dto.getConsumeAmount()
                .multiply(BigDecimal.valueOf(POINTS_PER_YUAN))
                .setScale(0, java.math.RoundingMode.DOWN)
                .intValue();
        if (points > 0) {
            int before = customer.getPoints() == null ? 0 : customer.getPoints();
            customerMapper.update(null, new LambdaUpdateWrapper<Customer>()
                    .setSql("points = points + {0}", points)
                    .eq(Customer::getCustomerId, dto.getCustomerId()));
            pointsLogMapper.insert(new CustomerPointsLog()
                    .setCustomerId(dto.getCustomerId())
                    .setChangeType(POINTS_TYPE_CONSUME)
                    .setChangePoints(points)
                    .setPointsBefore(before)
                    .setPointsAfter(before + points)
                    .setBizType(dto.getBizType())
                    .setBizId(dto.getBizId())
                    .setRemark("消费返积分"));
        }
        // 3. 重算会员等级（累计消费达到门槛自动升级）
        Customer latest = customerMapper.selectById(dto.getCustomerId());
        int newLevel = calcLevel(latest.getTotalConsume());
        if (newLevel != latest.getCustomerLevel()) {
            customerMapper.update(null, new LambdaUpdateWrapper<Customer>()
                    .set(Customer::getCustomerLevel, newLevel)
                    .eq(Customer::getCustomerId, dto.getCustomerId()));
        }
        return getAccount(dto.getCustomerId());
    }

    @Override
    public AccountVO getAccount(Long customerId) {
        // 账户概览不做启用状态校验（禁用会员也可查询，用于管理端/内部查看）
        Customer customer = requireCustomer(customerId);
        return toAccountVO(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountVO changeStatus(Long customerId, Integer status) {
        Customer customer = requireCustomer(customerId);
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ResultCodeMessage.PARAM_ERROR.getCode(), "状态参数错误，仅支持0禁用/1正常");
        }
        customerMapper.update(null, new LambdaUpdateWrapper<Customer>()
                .set(Customer::getStatus, status)
                .eq(Customer::getCustomerId, customerId));
        return toAccountVO(customerMapper.selectById(customerId));
    }

    @Override
    public PageResult<BalanceLogVO> pageBalanceLogs(Long customerId, Integer pageNum, Integer pageSize) {
        requireCustomer(customerId);
        Page<CustomerBalanceLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CustomerBalanceLog> wrapper = new LambdaQueryWrapper<CustomerBalanceLog>()
                .eq(CustomerBalanceLog::getCustomerId, customerId)
                .orderByDesc(CustomerBalanceLog::getLogId);
        Page<CustomerBalanceLog> result = balanceLogMapper.selectPage(page, wrapper);
        List<BalanceLogVO> records = result.getRecords().stream()
                .map(this::toBalanceLogVO)
                .toList();
        return new PageResult<BalanceLogVO>()
                .setRecords(records)
                .setTotal(result.getTotal())
                .setSize(result.getSize())
                .setCurrent(result.getCurrent())
                .setPages(result.getPages());
    }

    @Override
    public PageResult<PointsLogVO> pagePointsLogs(Long customerId, Integer pageNum, Integer pageSize) {
        requireCustomer(customerId);
        Page<CustomerPointsLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CustomerPointsLog> wrapper = new LambdaQueryWrapper<CustomerPointsLog>()
                .eq(CustomerPointsLog::getCustomerId, customerId)
                .orderByDesc(CustomerPointsLog::getLogId);
        Page<CustomerPointsLog> result = pointsLogMapper.selectPage(page, wrapper);
        List<PointsLogVO> records = result.getRecords().stream()
                .map(this::toPointsLogVO)
                .toList();
        return new PageResult<PointsLogVO>()
                .setRecords(records)
                .setTotal(result.getTotal())
                .setSize(result.getSize())
                .setCurrent(result.getCurrent())
                .setPages(result.getPages());
    }

    /**
     * 获取正常状态的会员，不存在或已禁用则抛异常
     */
    private Customer getActiveCustomer(Long customerId) {
        Customer customer = requireCustomer(customerId);
        if (customer.getStatus() != null && customer.getStatus() == 0) {
            throw new BusinessException(ResultCodeMessage.UNAUTHORIZED.getCode(),
                    "账号已锁定或禁用");
        }
        return customer;
    }

    private Customer requireCustomer(Long customerId) {
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException(ResultCodeMessage.NOT_FOUND.getCode(), "客户不存在");
        }
        return customer;
    }

    /**
     * 按累计消费金额计算会员等级：普通1 / 银卡2 / 金卡3
     */
    private int calcLevel(BigDecimal totalConsume) {
        BigDecimal consume = totalConsume == null ? BigDecimal.ZERO : totalConsume;
        if (consume.compareTo(LEVEL3_THRESHOLD) >= 0) {
            return 3;
        }
        if (consume.compareTo(LEVEL2_THRESHOLD) >= 0) {
            return 2;
        }
        return 1;
    }

    private AccountVO toAccountVO(Customer customer) {
        return new AccountVO()
                .setCustomerId(customer.getCustomerId())
                .setCustomerName(customer.getCustomerName())
                .setMobile(customer.getMobile())
                .setCustomerLevel(customer.getCustomerLevel())
                .setTotalConsume(customer.getTotalConsume())
                .setBalance(customer.getBalance())
                .setPoints(customer.getPoints())
                .setStatus(customer.getStatus());
    }

    private BalanceLogVO toBalanceLogVO(CustomerBalanceLog log) {
        return new BalanceLogVO()
                .setLogId(log.getLogId())
                .setCustomerId(log.getCustomerId())
                .setChangeType(log.getChangeType())
                .setChangeAmount(log.getChangeAmount())
                .setBalanceBefore(log.getBalanceBefore())
                .setBalanceAfter(log.getBalanceAfter())
                .setBizType(log.getBizType())
                .setBizId(log.getBizId())
                .setRemark(log.getRemark())
                .setGmtCreate(log.getGmtCreate());
    }

    private PointsLogVO toPointsLogVO(CustomerPointsLog log) {
        return new PointsLogVO()
                .setLogId(log.getLogId())
                .setCustomerId(log.getCustomerId())
                .setChangeType(log.getChangeType())
                .setChangePoints(log.getChangePoints())
                .setPointsBefore(log.getPointsBefore())
                .setPointsAfter(log.getPointsAfter())
                .setBizType(log.getBizType())
                .setBizId(log.getBizId())
                .setRemark(log.getRemark())
                .setGmtCreate(log.getGmtCreate());
    }
}
