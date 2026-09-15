package com.mfnit.common.api.result;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/16 00:57
 * @Description SOMS ResultGenerator
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public class ResultGenerator {

	/**
	 * 生成无返回值的成功结果
	 * @return
	 */
	public static Result genSuccessResult() {
		return new Result()
				.setCode(ResultCodeMessage.SUCCESS.getCode())
				.setMessage(ResultCodeMessage.SUCCESS.getMessage());
	}

	/**
	 * 生成无返回值的成功结果(带消息)
	 * @param message
	 * @return
	 */
	public static Result genSuccessResult(String message) {
		return new Result()
				.setCode(ResultCodeMessage.SUCCESS.getCode())
				.setMessage(message);
	}

	/**
	 * 生成带返回值的成功结果
	 * @param data  返回数据
	 * @return
	 */
	public static Result genSuccessResult(Object data) {
		return new Result()
				.setCode(ResultCodeMessage.SUCCESS.getCode())
				.setMessage(ResultCodeMessage.SUCCESS.getMessage())
				.setData(data);
	}

    /**
     * 返回带message和data的结果
     * @param message
     * @param data
     * @return
     */
	public static Result genSuccessMsgDataResult(String message, Object data) {
		return new Result()
				.setCode(ResultCodeMessage.SUCCESS.getCode())
				.setMessage(message)
				.setData(data);
	}

	/**
	 * 生成失败结果
	 * @param message   失败信息
	 * @return
	 */
	public static Result genFailResult(String message) {
		return new Result()
				.setCode(ResultCodeMessage.FAIL.getCode())
				.setMessage(message);
	}


    /**
     * 传入code和message的结果
     * @param message
     * @return
     */
    public static Result genCodeMsgResult(int code, String message) {
        return new Result()
                .setCode(code)
                .setMessage(message);
    }


}
