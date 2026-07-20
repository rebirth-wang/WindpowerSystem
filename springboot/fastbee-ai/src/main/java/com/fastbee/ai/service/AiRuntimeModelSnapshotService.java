package com.fastbee.ai.service;

import com.fastbee.ai.model.runtime.AiRuntimeModelSnapshot;
import com.fastbee.ai.model.vo.AiModelRouteVO;
import com.fastbee.ai.model.vo.AiRuntimeModelSnapshotCacheStatsVO;

/**
 * AI 运行时模型快照服务。
 */
public interface AiRuntimeModelSnapshotService {

    /**
     * 根据当前默认路由解析运行时快照。
     *
     * @return 运行时模型快照
     */
    AiRuntimeModelSnapshot resolveDefaultSnapshot();

    /**
     * 根据模型编码解析运行时快照。
     *
     * @param modelCode 模型编码
     * @return 运行时模型快照
     */
    AiRuntimeModelSnapshot resolveByModelCode(String modelCode);

    /**
     * 根据已解析好的路由结果构建运行时快照。
     *
     * @param route 路由结果
     * @return 运行时模型快照
     */
    AiRuntimeModelSnapshot resolveSnapshot(AiModelRouteVO route);

    /**
     * 按厂商 ID 失效相关快照。
     *
     * @param providerId 厂商 ID
     */
    void evictSnapshotsByProviderId(Long providerId);

    /**
     * 按模型 ID 失效相关快照。
     *
     * @param modelId 模型 ID
     */
    void evictSnapshotsByModelId(Long modelId);

    /**
     * 清空全部运行时快照缓存。
     */
    void clearSnapshotCache();

    /**
     * 查询运行时快照缓存统计信息。
     *
     * @return 统计信息
     */
    AiRuntimeModelSnapshotCacheStatsVO getSnapshotCacheStats();
}
