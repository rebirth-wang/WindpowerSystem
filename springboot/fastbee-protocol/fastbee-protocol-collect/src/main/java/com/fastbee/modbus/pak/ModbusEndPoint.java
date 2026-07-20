package com.fastbee.modbus.pak;

import static com.fastbee.modbus.pak.TcpDtu.心跳包;
import static com.fastbee.modbus.pak.TcpDtu.注册报文;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fastbee.base.core.annotation.Node;
import com.fastbee.base.core.annotation.PakMapping;
import com.fastbee.base.model.DeviceMsg;
import com.fastbee.base.model.SessionKey;
import com.fastbee.base.session.Session;
import com.fastbee.common.extend.core.domin.mq.DeviceReport;

/**
 * 报文消息映射处理
 * @author gsb
 * @date 2022/11/25 14:08
 */
@Node
@Component
@Slf4j
public class ModbusEndPoint {

    @PakMapping(types = 注册报文)
    public DeviceReport register(DeviceReport message, Session session){
        //注册设备
        session.register(message);
        //记录设备信息
        DeviceMsg deviceMsg = new DeviceMsg();
        deviceMsg.setClientId(message.getSerialNumber());
        session.setAttribute(SessionKey.DeviceMsg,deviceMsg);
        String hex = "FE8001FE";
        message.setBody(hex);
        return message;
    }

    @PakMapping(types = 心跳包)
    public void heartbeat(DeviceReport message, Session session){

    }

}
