package com.fastbee.icc.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织业务事件推送
 * 参考 文档-附录-事件列表-基础事件-角色实时消息
 */
public enum RoleBusinessEnum {

    role_add("role.add","角色新增"),
    role_update("role.update","角色更新"),
    role_delete("role.delete","角色删除"),

    ;
    public String code;
    public String msg;
    RoleBusinessEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static List<String> getBusinessTypes(){
        List<String> list = new ArrayList<>();
        for(RoleBusinessEnum item : RoleBusinessEnum.values()){
            list.add(item.code);
        }
        return list;
    }
}
