package com.fastbee.sip.handler;

import java.text.ParseException;

import javax.sip.ResponseEvent;

public interface IResHandler {
    public void processMsg(ResponseEvent evt) throws ParseException;
}
