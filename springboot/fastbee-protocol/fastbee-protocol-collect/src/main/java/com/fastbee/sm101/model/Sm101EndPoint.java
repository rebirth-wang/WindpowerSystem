package com.fastbee.sm101.model;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fastbee.base.core.annotation.Node;
import com.fastbee.base.core.annotation.PakMapping;
import com.fastbee.base.session.Session;
import com.fastbee.common.extend.core.domin.mq.DeviceReport;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.sm101.dcode.SM0101Encoder;

/**
 * @author gsb
 * @date 2025/12/9 17:36
 */
@Node
@Component
@Slf4j
public class Sm101EndPoint {

    @Resource
    private SM0101Encoder sm0101Encoder;

    @PakMapping(types = 100)
    public DeviceReport registerAck(DeviceReport message, Session session){
        SM0101Message sm0101Message = new SM0101Message();
        sm0101Message.setCommandCode((short) 0x01);
        sm0101Message.setServerTime(DateUtils.dateTime());
        return message;
    }
}
