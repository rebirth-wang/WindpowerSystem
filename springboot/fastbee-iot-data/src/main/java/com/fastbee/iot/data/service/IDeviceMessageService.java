package com.fastbee.iot.data.service;

import java.util.List;

import com.fastbee.common.extend.core.domin.mq.MQSendMessageBo;
import com.fastbee.common.extend.core.domin.mq.message.DeviceMessage;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.iot.model.vo.VariableReadVO;
import com.fastbee.modbus.model.ModbusRtu;

/**
 * 设备消息Service接口
 */
public interface IDeviceMessageService {

    void messagePost(DeviceMessage deviceMessage);

    String messageEncode(ModbusRtu modbusRtu);

    List<ThingsModelSimpleItem> messageDecode(DeviceMessage deviceMessage);


    /**
     * 变量读取
     * @param readVO
     */
    public void readVariableValue(VariableReadVO readVO);

    String commandGenerate(MQSendMessageBo messageBo);
}
