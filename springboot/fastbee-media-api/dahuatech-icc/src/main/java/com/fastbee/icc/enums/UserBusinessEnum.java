package com.fastbee.icc.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织业务事件推送
 * 参考 文档-附录-事件列表-基础事件-用户实时消息
 */
public enum UserBusinessEnum {

    user_add("user.add","用户新增"),
    user_update("user.update","用户更新"),
    user_delete("user.delete","用户删除"),

    ;
    public String code;
    public String msg;
    UserBusinessEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static List<String> getBusinessTypes(){
        List<String> list = new ArrayList<>();
        for(UserBusinessEnum item : UserBusinessEnum.values()){
            list.add(item.code);
        }
        return list;
    }
}
