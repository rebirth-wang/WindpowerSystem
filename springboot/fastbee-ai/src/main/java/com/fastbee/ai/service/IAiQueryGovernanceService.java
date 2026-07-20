package com.fastbee.ai.service;

import com.fastbee.ai.model.vo.AiQueryGovernanceSnapshotVO;
import com.fastbee.iot.model.DeviceMetaData;

/**
 * AI 问数治理服务。
 */
public interface IAiQueryGovernanceService {

    /**
     * 对多源问数执行前做设备、产品、物模型权限校验。
     *
     * @param deviceMetaData 设备元数据
     * @param identifier     物模型标识符
     * @param queryMode      执行模式
     * @return 治理快照
     */
    AiQueryGovernanceSnapshotVO validateMultiSourceAccess(DeviceMetaData deviceMetaData, String identifier, String queryMode);
}
