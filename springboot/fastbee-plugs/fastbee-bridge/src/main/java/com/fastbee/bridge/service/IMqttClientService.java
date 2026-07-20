package com.fastbee.bridge.service;

/**
 * mqtt桥接配置表Service接口
 *
 * @author gx_ma
 * @date 2024-06-03
 */
public interface IMqttClientService extends IBridge {

    public void deleteMqttClientByKey(String key);
}
