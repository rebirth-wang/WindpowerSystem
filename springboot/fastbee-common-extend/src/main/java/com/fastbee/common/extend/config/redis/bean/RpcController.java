package com.fastbee.common.extend.config.redis.bean;

import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.fastbee.common.extend.config.redis.RedisRpcConfig;

public class RpcController {

    @Autowired
    private RedisRpcConfig redisRpcConfig;

    @PostConstruct
    public void init() {
        String controllerPath = this.getClass().getAnnotation(RedisRpcController.class).value();
        // 扫描其下的方法
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method : methods) {
            RedisRpcMapping annotation = method.getAnnotation(RedisRpcMapping.class);
            if (annotation != null) {
                String methodPath =  annotation.value();
                if (methodPath != null) {
                    redisRpcConfig.addHandler(controllerPath + "/" + methodPath, new RedisRpcClassHandler(this, method));
                }
            }

        }
    }
}
