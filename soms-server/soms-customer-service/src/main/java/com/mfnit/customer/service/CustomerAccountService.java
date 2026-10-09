package com.mfnit.customer.service;

import com.mfnit.common.api.dto.customer.BalanceDeductDTO;
import com.mfnit.common.api.dto.customer.ConsumeCompleteDTO;
import com.mfnit.common.api.result.PageResult;
import com.mfnit.customer.dto.RechargeDTO;
import com.mfnit.customer.vo.AccountVO;
import com.mfnit.customer.vo.BalanceLogVO;
import com.mfnit.customer.vo.PointsLogVO;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/22
 * @Description 客户账户服务接口（余额/积分/等级/累计消费）
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public interface CustomerAccountService {

    /**
     * 余额充值（会员端）
     * @param customerId 会员ID
     * @param dto        充值请求
     * @return 充值后账户概览
     */
    AccountVO recharge(Long customerId, RechargeDTO dto);

    /**
     * 余额扣减（内部接口，幂等，业务单号唯一）
     * @param dto 扣减请求
     * @return 扣减后账户概览
     */
    AccountVO deductBalance(BalanceDeductDTO dto);

    /**
     * 消费完成回调（内部接口，幂等）：累加累计消费、赠送积分、重算会员等级
     * @param dto 消费回调请求
     * @return 回调后账户概览
     */
    AccountVO completeConsume(ConsumeCompleteDTO dto);

    /**
     * 账户概览（余额/积分/等级/累计消费）
     * @param customerId 会员ID
     * @return 账户概览
     */
    AccountVO getAccount(Long customerId);

    /**
     * 启用/禁用会员
     * @param customerId 会员ID
     * @param status     状态：0禁用，1正常
     * @return 更新后账户概览
     */
    AccountVO changeStatus(Long customerId, Integer status);

    /**
     * 余额流水分页查询（会员端"我的钱包"）
     * @param customerId 会员ID
     * @param pageNum    页码
     * @param pageSize   每页数量
     * @return 余额流水分页结果
     */
    PageResult<BalanceLogVO> pageBalanceLogs(Long customerId, Integer pageNum, Integer pageSize);

    /**
     * 积分流水分页查询
     * @param customerId 会员ID
     * @param pageNum    页码
     * @param pageSize   每页数量
     * @return 积分流水分页结果
     */
    PageResult<PointsLogVO> pagePointsLogs(Long customerId, Integer pageNum, Integer pageSize);

}
