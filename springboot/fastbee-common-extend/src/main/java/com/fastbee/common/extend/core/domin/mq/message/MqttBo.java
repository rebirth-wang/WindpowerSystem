package com.fastbee.common.extend.core.domin.mq.message;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author bill
 */
@Data
public class MqttBo {

    /*主题*/
    private String topic;
    /*数据*/
    private String data;
    /*消息质量*/
    private int qos = 1;
    /*发送方向*/
    private String direction;
    /*时间*/
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ts;
}

