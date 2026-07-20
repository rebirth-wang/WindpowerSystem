package com.fastbee.sip.handler.req.message;

import javax.sip.RequestEvent;

import org.dom4j.Element;

import com.fastbee.sip.domain.SipDevice;

public interface IMessageHandler {
    /**
     * 处理来自设备的信息
     * @param evt
     * @param device
     */
    void handlerCmdType(RequestEvent evt, SipDevice device, Element element);
}
