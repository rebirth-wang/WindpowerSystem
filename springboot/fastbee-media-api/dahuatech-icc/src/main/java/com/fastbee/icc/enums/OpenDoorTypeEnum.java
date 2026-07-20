package com.fastbee.icc.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-01 13:51
 * @Description: 门禁开门类型枚举
 */
public enum OpenDoorTypeEnum {

    LEGAL_PASSWORD_OPEN_DOOR("42","合法密码开门"),
    ILLEGAL_PASSWORD_CLOSE_DOOR("43","合法密码关门"),
    LEGAL_FINGERPRINT_OPEN_DOOR("45","合法指纹开门"),
    ILLEGAL_FINGERPRINT_CLOSE_DOOR("46","合法指纹关门"),
    REMOTE_OPEN_DOOR("48","远程开门"),
    BUTTON_OPEN_DOOR("49","按钮开门"),
    KEY_OPEN_DOOR("50","钥匙开门"),
    LEGAL_CARD_OPEN_DOOR("51","合法刷卡开门"),
    ILLEGAL_CARD_CLOSE_DOOR("52","非法刷卡开门"),
    DOOR_SENSOR("53","门磁"),
    ABNORMAL_OPEN_DOOR("54","异常开门"),
    ABNORMAL_CLOSE_DOOR("55","异常关门"),
    NORMAL_OPEN_DOOR("56","正常开门"),
    NORMAL_CLOSE_DOOR("57","正常关门"),
    ALARM_ACCESS_CONTROL("59","报警门禁对讲请求报警"),
    FACE_OPEN_DOOR("61","人像刷门"),
    ILLEGAL_OPEN_DOOR("62","人像非法刷门"),
    LEGAL_FACE_CARD_OPEN_DOOR("1436","人证合法开门"),
    ILLEGAL_FACE_CARD_OPEN_DOOR("1437","人证非法开门"),
    ILEGAL_FACE_CARD_ID_OPEN_DOOR("1438","人证和身份证非法开门"),
    LEGAL_FACE_CARD_ID_OPEN_DOOR("1439","人证和身份证合法开门"),
    RFID_ALARM("1448","RFID感应报警"),
    ILLEGAL_REID_ALARM("1449","RFID非法感应报警"),
    RFID_OUTDOOR_ALARM("1450","RFID外部报警"),

    ;
    public String code;
    public String msg;
    OpenDoorTypeEnum(String code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public static List<String> getOpenDoorTypes(){
        List<String> list = new ArrayList<>();
        for(OpenDoorTypeEnum item : OpenDoorTypeEnum.values()){
            list.add(item.code);
        }
        return list;
    }
}
