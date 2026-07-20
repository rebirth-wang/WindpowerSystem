package com.fastbee.onvif.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fastbee.onvif.service.IOnvifService;

/**
 * ONVIF 启动任务
 * 系统山动时初始化所有 ONVIF 设备状态为离线
 * 待设备重新连接 WebSocket 后更新为在线
 *
 * @author fastbee
 */
@Component
@Order(15)
public class OnvifSessionTask implements CommandLineRunner {

    @Autowired
    private IOnvifService onvifService;

    @Override
    public void run(String... args) throws Exception {
        // 启动时将所有设备标记为离线，防止上次关机时状态没有重置
        this.onvifService.initDeviceStatus();
    }
}
