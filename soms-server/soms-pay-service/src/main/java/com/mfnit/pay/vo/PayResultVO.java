package com.mfnit.pay.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/10/4 13:52
 * @Description SOMS 付款结果VO
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
@Data
@Accessors(chain = true)
public class PayResultVO {
    private Long payId;
    private String payNo;
    private Integer status;
    private String statusName;
    /** Native 返回二维码链接 */
    private String codeUrl;
    /** JSAPI 返回小程序支付参数 */
    private Map<String, String> jsapiParams;
    /** 第三方交易号 */
    private String channelTradeNo;
    /** 付款码支付时的错误信息 */
    private String errMsg;
}
