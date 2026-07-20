package com.fastbee.common.extend.core.domin.mq.message;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 设备消息
 * @author bill
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceMessage {

    /** 下发的数据*/
    private Object message;

    /** 下发的topic*/
    private String topicName;

    /** 设备编号*/
    private String serialNumber;

    private String dataType;

    /** 时间 */
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss:SSS")
    private Date time;
}
