package com.mfnit.common.api.result;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/16 00:57
 * @Description SOMS Result Code Message
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public enum ResultCodeMessage {
    /**
     * 系统异常
     */
    SYSTEM_EXCEPTION(500,"系统异常，请联系管理员"),
    UNKNOWN_EXCEPTION(501,"未知错误，请联系开发人员"),
    TOKEN_EMPTY(502,"TOKEN为空或错误"),
    SUCCESS(0,"SUCCESS"),
    FAIL(400,"FAIL"),
    PARAM_ERROR(401,"参数错误"),
    UNAUTHORIZED(403,"账号已锁定或禁用"),
    NOT_FOUND(404,"资源不存在"),
    ;

    private final int code;
    private final String message;

    ResultCodeMessage(int code, String message){
        this.code = code;
        this.message = message;
    }

    public static String getMessage(int code){
        for(ResultCodeMessage responseMessage : ResultCodeMessage.values()){
            if(responseMessage.getCode() == code){
                return responseMessage.getMessage();
            }
        }
        return null;
    }

    public int getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }
}
