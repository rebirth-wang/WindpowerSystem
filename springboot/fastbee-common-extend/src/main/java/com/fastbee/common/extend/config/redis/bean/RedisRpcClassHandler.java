package com.fastbee.common.extend.config.redis.bean;

import java.lang.reflect.Method;

import lombok.Data;

@Data
public class RedisRpcClassHandler {

    private RpcController controller;
    private Method method;

    public RedisRpcClassHandler(RpcController controller, Method method) {
        this.controller = controller;
        this.method = method;
    }
}
