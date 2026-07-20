package com.fastbee.ai.service;

import java.util.List;

import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.DeviceShortOutput;

/**
 * AI 设备解析服务。
 */
public interface IAiDeviceResolveService {

    /**
     * 根据问句解析设备，解析失败时抛出异常。
     *
     * @param question 用户问句
     * @return 设备元数据
     */
    DeviceMetaData resolveRequiredDeviceMetaData(String question);

    /**
     * 根据问句尝试解析设备，解析失败时返回 null。
     *
     * @param question 用户问句
     * @return 设备元数据，解析失败时返回 null
     */
    DeviceMetaData resolveOptionalDeviceMetaData(String question);

    /**
     * 根据问句列出设备候选，用于歧义澄清。
     *
     * @param question 用户问句
     * @return 设备候选列表
     */
    List<DeviceShortOutput> listDeviceCandidates(String question);
}
