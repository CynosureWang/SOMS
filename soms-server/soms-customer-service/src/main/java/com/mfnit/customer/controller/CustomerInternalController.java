package com.mfnit.customer.controller;

import com.mfnit.common.api.dto.customer.BalanceDeductDTO;
import com.mfnit.common.api.dto.customer.ConsumeCompleteDTO;
import com.mfnit.common.api.dto.customer.CustomerDTO;
import com.mfnit.common.api.result.Result;
import com.mfnit.common.api.result.ResultGenerator;
import com.mfnit.customer.service.CustomerAccountService;
import com.mfnit.customer.service.CustomerService;
import com.mfnit.customer.vo.AccountVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Project SOMS
 * @Author Lris
 * @Version 1.0.0
 * @CreateTime 2026/9/22
 * @Description 客户服务内部接口控制器（仅供 order/pay/self-checkout 等内部服务经 Feign 调用，网关不对外暴露）
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@RestController
@RequestMapping("/api/v1/customer/internal")
@RequiredArgsConstructor
public class CustomerInternalController {

    private final CustomerService customerService;
    private final CustomerAccountService customerAccountService;

    /**
     * 批量查询会员
     * @param customerIds 会员ID集合（逗号分隔）
     * @return 会员DTO列表
     */
    @GetMapping("/batch")
    public Result<List<CustomerDTO>> batch(@RequestParam("customerIds") List<Long> customerIds) {
        return ResultGenerator.genSuccessResult(customerService.listCustomerDTOs(customerIds));
    }

    /**
     * 余额扣减（幂等，bizType+bizId 唯一）
     * @param dto 扣减请求
     * @return 扣减后账户概览
     */
    @PostMapping("/balance/deduct")
    public Result<AccountVO> deductBalance(@Valid @RequestBody BalanceDeductDTO dto) {
        AccountVO account = customerAccountService.deductBalance(dto);
        return ResultGenerator.genSuccessMsgDataResult("扣减成功", account);
    }

    /**
     * 消费完成回调（幂等）：累加累计消费、赠送积分、重算会员等级
     * @param dto 消费回调请求
     * @return 回调后账户概览
     */
    @PostMapping("/consume/complete")
    public Result<AccountVO> completeConsume(@Valid @RequestBody ConsumeCompleteDTO dto) {
        AccountVO account = customerAccountService.completeConsume(dto);
        return ResultGenerator.genSuccessMsgDataResult("消费回调成功", account);
    }

}
