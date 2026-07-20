package com.fastbee.http.handler;

import jakarta.servlet.http.HttpSession;

import io.netty.handler.codec.http.FullHttpRequest;

public interface IHttpReqHandler {
    public void processMsg(FullHttpRequest req, HttpSession session);
}
