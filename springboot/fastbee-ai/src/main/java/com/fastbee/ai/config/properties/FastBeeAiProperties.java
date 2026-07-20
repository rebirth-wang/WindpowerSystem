package com.fastbee.ai.config.properties;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.fastbee.ai.model.enums.AiRegionProfile;

/**
 * AI 模块配置属性。
 */
@Data
@ConfigurationProperties(prefix = "fastbee.ai")
public class FastBeeAiProperties {

    /**
     * AI 总开关。
     */
    private boolean enabled = true;

    /**
     * 默认区域档位。
     */
    private AiRegionProfile region = AiRegionProfile.CN;

    /**
     * 默认供应商编码。
     */
    private String defaultProvider = "qwen";

    /**
     * 分区域供应商配置。
     */
    private Map<String, ProviderGroupProperties> providers = new LinkedHashMap<>();

    /**
     * 运行时治理配置。
     */
    private RuntimeProperties runtime = new RuntimeProperties();

    /**
     * 问数配置。
     */
    private Nl2SqlProperties nl2sql = new Nl2SqlProperties();

    /**
     * 协议解析配置。
     */
    private ProtocolProperties protocol = new ProtocolProperties();

    /**
     * 知识检索配置。
     */
    private RagProperties rag = new RagProperties();

    /**
     * 源码导航配置。
     */
    private CodebaseProperties codebase = new CodebaseProperties();

    @Data
    public static class ProviderGroupProperties {

        /**
         * 当前区域默认供应商编码。
         */
        private String provider;

        /**
         * 当前区域默认模型编码。
         */
        private String model;

        /**
         * 当前区域基础地址。
         */
        private String baseUrl;
    }

    @Data
    public static class RuntimeProperties {
        private boolean chatEnabled = true;
        private boolean nl2sqlEnabled = true;
        private boolean deviceControlEnabled = true;
        private boolean protocolEnabled = true;
        private boolean knowledgeEnabled = true;
        private boolean modelSnapshotCacheEnabled = true;
        private long modelSnapshotCacheTtlMs = 300000L;
        private double autoRouteGeneralThreshold = 0.60D;
        private double autoRoutePlatformAssistantThreshold = 0.65D;
        private double autoRouteNl2sqlThreshold = 0.78D;
        private double autoRouteDeviceControlThreshold = 0.86D;
        private double autoRouteProtocolParseThreshold = 0.72D;
        private boolean autoRouteModelArbitrationEnabled = true;
        private int autoRouteArbitrationTimeoutMs = 2500;
        private int autoRouteLocalDeterministicScore = 70;
        private int autoRouteHighRiskAcceptScore = 70;
        private int autoRouteDataQueryAcceptScore = 70;
        private int autoRouteDeviceControlAcceptScore = 70;
        private double autoRouteFastGeneralMinConfidence = 0.90D;
        private int timeoutMs = 30000;
        private int retryTimes = 1;
        private int maxMessagesPerSession = 50;
    }

    @Data
    public static class Nl2SqlProperties {
        private boolean enabled = true;
        private int maxRows = 200;
        private int timeoutMs = 10000;
        private List<String> allowedTables = new ArrayList<>();
        private String schemaPrompt;
        private int maxSemanticPromptItems = 12;
        private int maxSemanticValueMappings = 8;
        private String semanticStoreType = "REDIS";
        private String semanticRedisPrefix = "fastbee:ai:semantic";
        private int maxThingModelSemanticProducts = 50;
        private int maxThingModelSemanticMatches = 12;
    }

    @Data
    public static class ProtocolProperties {
        private boolean enabled = true;
    }

    @Data
    public static class RagProperties {
        private boolean enabled = true;
        private String vectorStoreType = "MEMORY";
        private int topK = 5;
        private int chunkSize = 800;
        private int chunkOverlap = 120;
        private int maxDocumentChars = 20000;
    }

    @Data
    public static class CodebaseProperties {
        private boolean enabled = true;
        private String rootPath;
        private List<String> includeRoots = new ArrayList<>();
        private int maxFiles = 5000;
        private int maxItems = 20000;
    }
}
