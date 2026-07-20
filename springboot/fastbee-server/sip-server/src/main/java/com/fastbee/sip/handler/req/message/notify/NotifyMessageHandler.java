package com.fastbee.sip.handler.req.message.notify;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fastbee.sip.handler.req.message.MessageHandlerAbstract;
import com.fastbee.sip.handler.req.message.MessageRequestProcessor;

@Component
public class NotifyMessageHandler extends MessageHandlerAbstract implements InitializingBean {

    @Autowired
    private MessageRequestProcessor messageRequestProcessor;

    @Override
    public void afterPropertiesSet() throws Exception {
        String messageType = "Notify";
        messageRequestProcessor.addHandler(messageType, this);
    }
}
