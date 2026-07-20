package com.fastbee.ai.service;

/**
 * AI 设备控制治理服务。
 */
public interface IAiDeviceControlGovernanceService {

    /**
     * 校验当前用户是否有权控制指定设备物模型。
     *
     * @param serialNumber 设备编号
     * @param identifier   物模型标识符
     */
    void validateControlPermission(String serialNumber, String identifier);
}
