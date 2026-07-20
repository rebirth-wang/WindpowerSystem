package com.fastbee.icc.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织业务事件推送
 * 参考 文档-附录-事件列表-基础事件-卡片实时消息
 */
public enum CardBusinessEnum {

    card_add("card.add","卡片新增"),
    card_update("card.update","卡片更新"),
    card_delete("card.delete","卡片删除"),
    card_move("card.batch_update","卡片批量更新"),
    card_batch_add("card.batch_add","卡片批量新增"),
    card_replace("card.replace","补卡/换卡"),

    ;
    public String code;
    public String msg;
    CardBusinessEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static List<String> getBusinessTypes(){
        List<String> list = new ArrayList<>();
        for(CardBusinessEnum item : CardBusinessEnum.values()){
            list.add(item.code);
        }
        return list;
    }
}
