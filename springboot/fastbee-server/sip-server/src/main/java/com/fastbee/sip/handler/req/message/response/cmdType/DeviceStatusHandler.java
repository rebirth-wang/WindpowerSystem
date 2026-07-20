package com.fastbee.sip.handler.req.message.response.cmdType;

import java.text.ParseException;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;

import com.alibaba.fastjson2.JSONObject;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.xml.sax.SAXException;

import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.handler.req.ReqAbstractHandler;
import com.fastbee.sip.handler.req.message.IMessageHandler;
import com.fastbee.sip.handler.req.message.response.ResponseMessageHandler;
import com.fastbee.sip.service.ISipCacheService;
import com.fastbee.sip.util.XmlUtil;

@Component
public class DeviceStatusHandler extends ReqAbstractHandler implements InitializingBean, IMessageHandler {

    @Autowired
    private ResponseMessageHandler responseMessageHandler;

    @Autowired
    private ISipCacheService sipCacheService;

    @Override
    public void handlerCmdType(RequestEvent evt, SipDevice device, Element element) {
        try {
            Element rootElement = getRootElement(evt);
            String deviceId = XmlUtil.getText(rootElement, "DeviceID");
            if (ObjectUtils.isEmpty(deviceId) && device != null) {
                deviceId = device.getDeviceSipId();
            }
            JSONObject json = new JSONObject();
            json.put("deviceId", deviceId);
            json.put("status", XmlUtil.getText(rootElement, "Status"));
            json.put("online", XmlUtil.getText(rootElement, "Online"));
            json.put("result", XmlUtil.getText(rootElement, "Result"));
            json.put("deviceTime", XmlUtil.getText(rootElement, "DeviceTime"));
            json.put("encode", XmlUtil.getText(rootElement, "Encode"));
            json.put("record", XmlUtil.getText(rootElement, "Record"));
            json.put("rawXml", rootElement.asXML());
            sipCacheService.recordDeviceStatus(deviceId, "DEVICE_STATUS", json);
            // 回复200 OK
            responseAck(evt);
        } catch (ParseException | SipException | InvalidArgumentException | DocumentException | SAXException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String cmdType = "DeviceStatus";
        responseMessageHandler.addHandler(cmdType, this);
    }
}
