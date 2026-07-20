package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * AI 厂商与模型分组视图。
 */
@Data
public class AiProviderModelGroupVO {

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
     * 区域档位。
     */
    private String regionProfile;

    /**
     * 厂商下模型列表。
     */
    private List<ModelItem> models = new ArrayList<>();

    @Data
    public static class ModelItem {
        private Long modelId;
        private String modelCode;
        private String modelName;
        private String modelType;
        private String isDefault;
    }
}
