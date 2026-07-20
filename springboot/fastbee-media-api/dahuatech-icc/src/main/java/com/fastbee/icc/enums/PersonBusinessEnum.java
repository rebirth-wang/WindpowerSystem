package com.fastbee.icc.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织业务事件推送
 * 参考 文档-附录-事件列表-基础事件-人员实时消息
 */
public enum PersonBusinessEnum {

    person_add("person.add","人员新增"),
    person_update("person.update","人员更新"),
    person_delete("person.delete","人员删除"),
    person_batch_update("person.batch_update","人员批量更新"),
    person_batch_add("person.batch_add","人员批量新增"),
    person_department_update("person.department_update","人员部门转移通知"),

    ;
    public String code;
    public String msg;
    PersonBusinessEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static List<String> getBusinessTypes(){
        List<String> list = new ArrayList<>();
        for(PersonBusinessEnum item : PersonBusinessEnum.values()){
            list.add(item.code);
        }
        return list;
    }
}
