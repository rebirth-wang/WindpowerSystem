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
@Protocol(JT808.DEVICE_REGISTER_RESPONSE)
public class T8100 extends JT808Message implements Response {

    /** 0.成功 */
    public static final int Success = 0;
    /** 1.车辆已被注册 */
    public static final int AlreadyRegisteredVehicle = 1;
    /** 2.数据库中无该车辆 */
    public static final int NotFoundVehicle = 2;
    /** 3.终端已被注册 */
    public static final int AlreadyRegisteredTerminal = 3;
    /** 4.数据库中无该终端 */
    public static final int NotFoundTerminal = 4;

    @Column(length = 2, desc = "应答流水号")
    private int responseSerialNo;
    @Column(length = 1, desc = "结果：0.成功 1.车辆已被注册 2.数据库中无该车辆 3.终端已被注册 4.数据库中无该终端")
    private int resultCode;
    @Column(desc = "鉴权码(成功后才有该字段)")
    private String token;

}
