package com.fastbee.common.extend.core.protocol;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * 基础消息
 *
 * @author bill
 */
@Data
public class Message implements Serializable {

    /*获取客户端id*/
   public String serialNumber;
    /*消息类型*/
    public String mgId;
    /*消息流水号*/
    public String serNo;


    @JsonIgnore
     protected transient ByteBuf payload;

    /**
     * 是否数据和注册包都封装到一起
     */
    private Boolean isPackage = false;

    private Object body;
}
