package com.fastbee.icc.enums;

public enum XXXRetCodeEnums {

    xxx_error("xxx001","xxx错误");
    ;
    public String code;
    public String msg;

    XXXRetCodeEnums(String code, String msg){
        this.msg = msg;
        this.code = code;
    }
}
