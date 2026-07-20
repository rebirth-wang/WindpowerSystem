package com.fastbee.ai.model.vo;

import lombok.Data;

import com.fastbee.ai.model.enums.AiModelProviderType;
import com.fastbee.ai.model.enums.AiRegionProfile;

/**
 * 当前模型路由结果。
 */
@Data
public class AiModelRouteVO {

    /**
     * 厂商 ID。
     */
    private Long providerId;

    /**
     * 区域档位。
     */
    private AiRegionProfile region;

    /**
     * 厂商编码。
     */
    private String providerCode;

    /**
     * 厂商名称。
     */
    private String providerName;

    /**
     * 厂商类型。
     */
    private AiModelProviderType providerType;

    /**
     * 模型 ID。
     */
    private Long modelId;

    /**
     * 模型编码。
     */
    private String modelCode;

    /**
     * 模型名称。
     */
    private String modelName;

    /**
     * 基础地址。
     */
    private String baseUrl;
}
