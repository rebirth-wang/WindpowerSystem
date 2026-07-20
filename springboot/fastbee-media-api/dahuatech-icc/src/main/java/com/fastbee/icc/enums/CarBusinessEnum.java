package com.fastbee.icc.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织业务事件推送
 * 参考 文档-附录-事件列表-基础事件-车辆实时消息
 */
public enum CarBusinessEnum {

    car_add("car.add","车辆新增"),
    car_update("car.update","车辆更新"),
    car_delete("car.delete","车辆删除"),
    car_batch_update("car.batch_update","车辆批量更新"),
    car_batch_add("car.batch_add","车辆批量新增"),

    ;
    public String code;
    public String msg;
    CarBusinessEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static List<String> getBusinessTypes(){
        List<String> list = new ArrayList<>();
        for(CarBusinessEnum item : CarBusinessEnum.values()){
            list.add(item.code);
        }
        return list;
    }
}
