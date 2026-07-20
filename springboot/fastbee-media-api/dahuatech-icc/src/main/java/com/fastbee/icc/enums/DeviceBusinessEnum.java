package com.fastbee.icc.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织业务事件推送
 * 参考 文档-附录-事件列表-基础事件-设备实时消息
 */
public enum DeviceBusinessEnum {

    device_add("device.add","设备新增"),
    device_update("device.update","设备更新"),
    device_delete("device.delete","设备删除"),
    device_move("device.batch_update","设备批量更新"),
    device_batch_add("device.batch_add","设备批量新增"),

    ;
    public String code;
    public String msg;
    DeviceBusinessEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static List<String> getBusinessTypes(){
        List<String> list = new ArrayList<>();
        for(DeviceBusinessEnum item : DeviceBusinessEnum.values()){
            list.add(item.code);
        }
        return list;
    }
}
