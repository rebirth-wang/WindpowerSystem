package com.fastbee.ai.model.runtime;

import lombok.Data;

import com.fastbee.ai.model.enums.AiModelProviderType;
import com.fastbee.ai.model.enums.AiRegionProfile;

/**
 * AI 运行时模型快照。
 *
 * <p>该对象用于冻结一次模型调用真正生效的运行时配置，
 * 避免工厂层在构建 ChatModel 时再次回库查询厂商和模型。</p>
 */
@Data
public class AiRuntimeModelSnapshot {

    /**
     * 快照来源，当前支持 DB / YAML。
     */
    private String snapshotSource;

    /**
     * 快照键，当前用于本地快照缓存复用。
     */
    private String snapshotKey;

    /**
     * 区域档位。
     */
    private AiRegionProfile region;

    /**
     * 厂商 ID。
     */
    private Long providerId;

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
     * 模型类型。
     */
    private String modelType;

    /**
     * 接口基础地址。
     */
    private String apiBaseUrl;

    /**
     * 鉴权方式。
     */
    private String authType;

    /**
     * 运行时使用的 API Key。
     */
    private String runtimeApiKey;

    /**
     * 推理参数 JSON。
     */
    private String requestOptions;
}
