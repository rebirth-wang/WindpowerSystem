package com.fastbee.icc.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织业务事件推送
 * 参考 文档-附录-事件列表-基础事件-组织实时消息
 */
public enum OrganizationBusinessEnum {

    org_add("org.add","组织新增"),
    org_update("org.update","组织更新"),
    org_delete("org.delete","组织删除"),
    org_move("org.move","组织移动"),
    org_batch_add("org.batch_add","组织批量新增"),

    ;
    public String code;
    public String msg;
    OrganizationBusinessEnum(String code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public static List<String> getBusinessTypes(){
        List<String> list = new ArrayList<>();
        for(OrganizationBusinessEnum item : OrganizationBusinessEnum.values()){
            list.add(item.code);
        }
        return list;
    }
}
