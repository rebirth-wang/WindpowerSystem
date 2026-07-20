package com.fastbee.ai.model.vo;

import lombok.Data;

/**
 * 运行时模型快照缓存统计对象。
 */
@Data
public class AiRuntimeModelSnapshotCacheStatsVO {

    /**
     * 缓存是否启用。
     */
    private boolean cacheEnabled;

    /**
     * 缓存 TTL，单位毫秒。
     */
    private long cacheTtlMs;

    /**
     * 总请求数。
     */
    private long requestCount;

    /**
     * 实际命中/未命中判断次数。
     */
    private long cacheLookupCount;

    /**
     * 缓存命中次数。
     */
    private long hitCount;

    /**
     * 首次未命中次数。
     */
    private long coldMissCount;

    /**
     * 过期后重建次数。
     */
    private long expiredMissCount;

    /**
     * 缓存关闭时的旁路次数。
     */
    private long bypassCount;

    /**
     * 厂商维度主动失效次数。
     */
    private long providerEvictCount;

    /**
     * 模型维度主动失效次数。
     */
    private long modelEvictCount;

    /**
     * 全量清空次数。
     */
    private long clearCount;

    /**
     * 当前缓存条目数。
     */
    private long cacheEntryCount;

    /**
     * 缓存命中率。
     */
    private double hitRate;
}
