package com.fastbee.http.handler;

import java.text.ParseException;

import io.netty.handler.codec.http.FullHttpResponse;

public interface IHttpResHandler {
    public void processMsg(FullHttpResponse req) throws ParseException;
}
