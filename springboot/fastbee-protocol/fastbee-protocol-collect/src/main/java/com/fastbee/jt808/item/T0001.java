package com.fastbee.jt808.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.fastbee.base.core.model.Response;
import com.fastbee.jt808.JT808Message;
import com.fastbee.protocol.base.annotation.Column;
import com.fastbee.protocol.base.annotation.Protocol;

@ToString
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Protocol({JT808.PLAT_COMMON_RESPONSE, JT808.DEVICE_COMMON_RESPONSE})
public class T0001 extends JT808Message implements Response {

    public static final int Success = 0; //成功、确认
    public static final int Failure = 1;//失败
    public static final int MessageError = 2;//消息有误
    public static final int NotSupport = 3;//不支持
    public static final int AlarmAck = 4;//报警处理确认

    @Column(length = 2, desc = "应答流水号")
    private int responseSerialNo;
    @Column(length = 2, desc = "应答ID")
    private int responseMessageId;
    @Column(length = 1, desc = "结果：0.成功 1.失败 2.消息有误 3.不支持 4.报警处理确认")
    private int resultCode;

    public boolean isSuccess() {
        return this.resultCode == Success;
    }
}
