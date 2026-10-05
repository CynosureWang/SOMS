package com.mfnit.common.api.enums;

/**
 * @Project SOMS
 * @Author Cynosure.Wang
 * @Version 1.0.0
 * @CreateTime 2026/9/19 22:49
 * @Description SOMS 认证账户类型枚举
 * @Copyright Copyright © 2026 Zaozhuang Memorial Future Network Information Technology Co., Ltd. All rights reserved
 */
public enum AuthAccountTypeEnum {

    CUSTOMER(1,"客户"),
    ADMINISTRATOR(2,"管理员"),
    EMPLOYEE(3,"员工"),
    SUPPLIER(4,"供应商"),
    ;

    AuthAccountTypeEnum(Integer code,String text){
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

        for(AuthAccountTypeEnum item : AuthAccountTypeEnum.values()){
            if(item.code().equals(code)){
                return item.text();
            }
        }

        return "";
    }
}
