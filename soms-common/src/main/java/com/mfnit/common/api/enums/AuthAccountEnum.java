package com.mfnit.common.api.enums;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 22:45
 * @Description SOMS 认证账户状态枚举
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public enum AuthAccountEnum {

    NORMAL(1,"正常"),
    LOCKED(2,"已锁定"),
    DISABLED(3,"已禁用"),
    ;

    AuthAccountEnum(Integer code,String text){
        this.code = code;
        this.text = text;
    }

    private Integer code;
    private String text;

    /**
     * 代码
     */
    public Integer code(){
        return code;
    }

    /**
     * 名称
     */
    public String text(){
        return text;
    }

    public static String getText(Integer code){

        for(AuthAccountEnum item : AuthAccountEnum.values()){
            if(item.code().equals(code)){
                return item.text();
            }
        }

        return "";
    }
}
