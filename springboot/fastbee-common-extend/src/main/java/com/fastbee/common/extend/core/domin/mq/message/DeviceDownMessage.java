package com.fastbee.common.extend.core.domin.mq.message;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fastbee.common.enums.ServerType;
import com.fastbee.common.extend.core.protocol.modbus.ModbusCode;

/**
 * 设备下发指令model
 *
 * @author gsb
 * @date 2022/10/10 16:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDownMessage {

    private String messageId;
    /**
     * 时间戳，单位毫秒
     */
    private Long timestamp;
    /**
     * 消息体
     */
    private Object body;
    /*下发的指令,服务调用的时候就是服务标识符*/
    private String identifier;
    /*产品id*/
    private Long productId;
    /**
     * 设备编码
     */
    private String serialNumber;
    /**
     * 从机编号
     */
    private Integer slaveId;
    private ModbusCode code;
    private String protocolCode;

    private List<com.fastbee.common.extend.core.domin.mq.message.PropRead> values;
    private String topic;
    private String subCode;
    private ServerType serverType;

    public DeviceDownMessage(List<PropRead> values, String topic, String subCode, String transport) {
        this.values = values;
        this.topic = topic;
        this.subCode = subCode;
        this.serverType = ServerType.explain(transport);
    }
}
