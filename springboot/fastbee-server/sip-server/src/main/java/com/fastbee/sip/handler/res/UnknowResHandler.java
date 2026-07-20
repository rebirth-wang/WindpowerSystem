package com.fastbee.sip.handler.res;

import java.text.ParseException;

import javax.sip.ResponseEvent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fastbee.sip.handler.IResHandler;

@Slf4j
@Component
public class UnknowResHandler implements IResHandler {
    @Override
    public void processMsg(ResponseEvent evt) throws ParseException {
        log.warn("Unknow Response! ReasonPhrase:" + evt.getResponse().getReasonPhrase());
    }
}
