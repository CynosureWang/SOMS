package com.mfnit.customer.controller;

import com.mfnit.common.api.result.PageResult;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.customer.dto.RechargeDTO;
import com.mfnit.customer.service.CustomerAccountService;
import com.mfnit.customer.vo.AccountVO;
import com.mfnit.customer.vo.BalanceLogVO;
import com.mfnit.customer.vo.PointsLogVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/22
 * @Description 客户账户控制器（会员端：充值、账户概览、余额/积分流水、状态）
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Validated
@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerAccountController {

    private final CustomerAccountService customerAccountService;

    /**
     * 余额充值
     * @param customerId 会员ID
     * @param dto        充值请求（amount 必填，bizId 可选幂等键）
     * @return 充值后账户概览
     */
    @PostMapping("/{customerId}/recharge")
    public Result<AccountVO> recharge(@PathVariable Long customerId,
                                      @Valid @RequestBody RechargeDTO dto) {
        AccountVO account = customerAccountService.recharge(customerId, dto);
        return ResultGenerator.genSuccessMsgDataResult("充值成功", account);
    }

    /**
     * 账户概览（余额/积分/等级/累计消费）
     * @param customerId 会员ID
     * @return 账户概览
     */
    @GetMapping("/{customerId}/account")
    public Result<AccountVO> account(@PathVariable Long customerId) {
        return ResultGenerator.genSuccessResult(customerAccountService.getAccount(customerId));
    }

    /**
     * 余额流水分页查询
     * @param customerId 会员ID
     * @param pageNum    页码
     * @param pageSize   每页数量
     * @return 余额流水分页结果
     */
    @GetMapping("/{customerId}/balance-logs")
    public Result<PageResult<BalanceLogVO>> balanceLogs(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResultGenerator.genSuccessResult(
                customerAccountService.pageBalanceLogs(customerId, pageNum, pageSize));
    }

    /**
     * 积分流水分页查询
     * @param customerId 会员ID
     * @param pageNum    页码
     * @param pageSize   每页数量
     * @return 积分流水分页结果
     */
    @GetMapping("/{customerId}/points-logs")
    public Result<PageResult<PointsLogVO>> pointsLogs(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResultGenerator.genSuccessResult(
                customerAccountService.pagePointsLogs(customerId, pageNum, pageSize));
    }

    /**
     * 启用/禁用会员
     * @param customerId 会员ID
     * @param status     状态：0禁用，1正常
     * @return 更新后账户概览
     */
    @PutMapping("/{customerId}/status")
    public Result<AccountVO> changeStatus(@PathVariable Long customerId,
                                          @RequestParam Integer status) {
        AccountVO account = customerAccountService.changeStatus(customerId, status);
        return ResultGenerator.genSuccessMsgDataResult("状态更新成功", account);
    }

}
