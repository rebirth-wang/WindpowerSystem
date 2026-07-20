package com.fastbee.icc.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织业务事件推送
 * 参考 文档-附录-事件列表-基础事件-部门实时消息
 */
public enum DepartmentBusinessEnum {

    department_add("department.add","部门新增"),
    department_update("department.update","部门更新"),
    department_delete("department.delete","部门删除"),
    department_move("department.batch_update","部门批量更新"),
    department_batch_add("department.batch_add","部门批量新增"),

    ;
    public String code;
    public String msg;
    DepartmentBusinessEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static List<String> getBusinessTypes(){
        List<String> list = new ArrayList<>();
        for(DepartmentBusinessEnum item : DepartmentBusinessEnum.values()){
            list.add(item.code);
        }
        return list;
    }
}
