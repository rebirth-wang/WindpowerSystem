package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.domain.AiModel;
import com.fastbee.ai.domain.AiProvider;
import com.fastbee.ai.mapper.AiModelMapper;
import com.fastbee.ai.mapper.AiProviderMapper;
import com.fastbee.ai.model.enums.AiModelProviderType;
import com.fastbee.ai.model.runtime.AiRuntimeModelSnapshot;
import com.fastbee.ai.model.vo.AiModelRouteVO;
import com.fastbee.ai.model.vo.AiRuntimeModelSnapshotCacheStatsVO;
import com.fastbee.ai.service.AiModelRoutingService;
import com.fastbee.ai.service.AiRuntimeModelSnapshotService;
import com.fastbee.ai.support.AiProviderBaseUrlSupport;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 运行时模型快照服务默认实现。
 */
@Service
public class AiRuntimeModelSnapshotServiceImpl implements AiRuntimeModelSnapshotService {

    private static final String SNAPSHOT_SOURCE_DB = "DB";
    private static final String SNAPSHOT_SOURCE_YAML = "YAML";
    private static final long DEFAULT_CACHE_TTL_MS = 300000L;

    @Resource
    private AiModelRoutingService aiModelRoutingService;

    @Resource
    private FastBeeAiProperties properties;

    @Resource
    private AiProviderMapper aiProviderMapper;

    @Resource
    private AiModelMapper aiModelMapper;

    private final Map<String, CachedSnapshotEntry> snapshotCache = new ConcurrentHashMap<>();
    private final AtomicLong requestCount = new AtomicLong();
    private final AtomicLong hitCount = new AtomicLong();
    private final AtomicLong coldMissCount = new AtomicLong();
    private final AtomicLong expiredMissCount = new AtomicLong();
    private final AtomicLong bypassCount = new AtomicLong();
    private final AtomicLong providerEvictCount = new AtomicLong();
    private final AtomicLong modelEvictCount = new AtomicLong();
    private final AtomicLong clearCount = new AtomicLong();
    private LongSupplier nowSupplier = System::currentTimeMillis;

    @Override
    public AiRuntimeModelSnapshot resolveDefaultSnapshot() {
        return resolveSnapshot(aiModelRoutingService.resolveDefaultRoute());
    }

    @Override
    public AiRuntimeModelSnapshot resolveByModelCode(String modelCode) {
        return resolveSnapshot(aiModelRoutingService.resolveByModelCode(modelCode));
    }

    @Override
    public AiRuntimeModelSnapshot resolveSnapshot(AiModelRouteVO route) {
        if (route == null) {
            throw new ServiceException(message("ai.runtime.model.route.required"));
        }
        requestCount.incrementAndGet();
        String snapshotSource = resolveSnapshotSource(route);
        if (!isSnapshotCacheEnabled()) {
            bypassCount.incrementAndGet();
            return buildSnapshot(route, snapshotSource);
        }
        String cacheKey = buildSnapshotKey(route, snapshotSource);
        long now = currentTimeMillis();
        CacheResolveDecision decision = new CacheResolveDecision();
        CachedSnapshotEntry entry = snapshotCache.compute(cacheKey, (key, currentEntry) -> {
            if (currentEntry != null && !currentEntry.isExpired(now)) {
                decision.hit = true;
                return currentEntry;
            }
            if (currentEntry == null) {
                decision.coldMiss = true;
            } else {
                decision.expiredMiss = true;
            }
            AiRuntimeModelSnapshot snapshot = buildSnapshot(route, snapshotSource);
            return new CachedSnapshotEntry(copySnapshot(snapshot), now + resolveSnapshotCacheTtlMs());
        });
        if (decision.hit) {
            hitCount.incrementAndGet();
        } else if (decision.expiredMiss) {
            expiredMissCount.incrementAndGet();
        } else {
            coldMissCount.incrementAndGet();
        }
        return copySnapshot(entry.snapshot);
    }

    @Override
    public void evictSnapshotsByProviderId(Long providerId) {
        if (providerId == null || snapshotCache.isEmpty()) {
            return;
        }
        providerEvictCount.incrementAndGet();
        snapshotCache.entrySet().removeIf(entry -> providerId.equals(entry.getValue().snapshot.getProviderId()));
    }

    @Override
    public void evictSnapshotsByModelId(Long modelId) {
        if (modelId == null || snapshotCache.isEmpty()) {
            return;
        }
        modelEvictCount.incrementAndGet();
        snapshotCache.entrySet().removeIf(entry -> modelId.equals(entry.getValue().snapshot.getModelId()));
    }

    @Override
    public void clearSnapshotCache() {
        clearCount.incrementAndGet();
        snapshotCache.clear();
    }

    @Override
    public AiRuntimeModelSnapshotCacheStatsVO getSnapshotCacheStats() {
        AiRuntimeModelSnapshotCacheStatsVO stats = new AiRuntimeModelSnapshotCacheStatsVO();
        long actualHitCount = hitCount.get();
        long actualColdMissCount = coldMissCount.get();
        long actualExpiredMissCount = expiredMissCount.get();
        long lookupCount = actualHitCount + actualColdMissCount + actualExpiredMissCount;
        stats.setCacheEnabled(isSnapshotCacheEnabled());
        stats.setCacheTtlMs(resolveSnapshotCacheTtlMs());
        stats.setRequestCount(requestCount.get());
        stats.setCacheLookupCount(lookupCount);
        stats.setHitCount(actualHitCount);
        stats.setColdMissCount(actualColdMissCount);
        stats.setExpiredMissCount(actualExpiredMissCount);
        stats.setBypassCount(bypassCount.get());
        stats.setProviderEvictCount(providerEvictCount.get());
        stats.setModelEvictCount(modelEvictCount.get());
        stats.setClearCount(clearCount.get());
        stats.setCacheEntryCount(snapshotCache.size());
        stats.setHitRate(calculateHitRate(actualHitCount, lookupCount));
        return stats;
    }

    private String resolveSnapshotSource(AiModelRouteVO route) {
        return route.getProviderId() != null || route.getModelId() != null ? SNAPSHOT_SOURCE_DB : SNAPSHOT_SOURCE_YAML;
    }

    private AiRuntimeModelSnapshot buildSnapshot(AiModelRouteVO route, String snapshotSource) {
        if (SNAPSHOT_SOURCE_DB.equals(snapshotSource)) {
            return buildDatabaseSnapshot(route);
        }
        return buildConfigSnapshot(route);
    }

    private AiRuntimeModelSnapshot buildDatabaseSnapshot(AiModelRouteVO route) {
        AiModel model = resolveModel(route);
        AiProvider provider = resolveProvider(route, model);
        if (provider == null) {
            throw new ServiceException(message("ai.runtime.model.provider.required"));
        }
        AiRuntimeModelSnapshot snapshot = new AiRuntimeModelSnapshot();
        AiModelProviderType providerType = AiModelProviderType.fromCode(provider.getProviderCode());
        snapshot.setSnapshotSource(SNAPSHOT_SOURCE_DB);
        snapshot.setSnapshotKey(buildSnapshotKey(route, SNAPSHOT_SOURCE_DB));
        snapshot.setRegion(route.getRegion());
        snapshot.setProviderId(provider.getProviderId());
        snapshot.setProviderCode(provider.getProviderCode());
        snapshot.setProviderName(provider.getProviderName());
        snapshot.setProviderType(providerType);
        snapshot.setModelId(model == null ? route.getModelId() : model.getModelId());
        snapshot.setModelCode(StringUtils.defaultIfBlank(route.getModelCode(), model == null ? null : model.getModelCode()));
        snapshot.setModelName(StringUtils.defaultIfBlank(route.getModelName(), model == null ? null : model.getModelName()));
        snapshot.setModelType(model == null ? null : model.getModelType());
        snapshot.setApiBaseUrl(AiProviderBaseUrlSupport.normalizeOpenAiCompatibleBaseUrl(
                providerType,
                StringUtils.defaultIfBlank(provider.getApiBaseUrl(), route.getBaseUrl())
        ));
        snapshot.setAuthType(StringUtils.defaultIfBlank(provider.getAuthType(), "API_KEY"));
        snapshot.setRuntimeApiKey(resolveRuntimeApiKey(provider));
        snapshot.setRequestOptions(model == null ? null : model.getRequestOptions());
        return snapshot;
    }

    private AiRuntimeModelSnapshot buildConfigSnapshot(AiModelRouteVO route) {
        AiRuntimeModelSnapshot snapshot = new AiRuntimeModelSnapshot();
        AiModelProviderType providerType = route.getProviderType() == null
                ? AiModelProviderType.fromCode(route.getProviderCode())
                : route.getProviderType();
        snapshot.setSnapshotSource(SNAPSHOT_SOURCE_YAML);
        snapshot.setSnapshotKey(buildSnapshotKey(route, SNAPSHOT_SOURCE_YAML));
        snapshot.setRegion(route.getRegion());
        snapshot.setProviderId(route.getProviderId());
        snapshot.setProviderCode(route.getProviderCode());
        snapshot.setProviderName(route.getProviderName());
        snapshot.setProviderType(providerType);
        snapshot.setModelId(route.getModelId());
        snapshot.setModelCode(route.getModelCode());
        snapshot.setModelName(route.getModelName());
        snapshot.setApiBaseUrl(AiProviderBaseUrlSupport.normalizeOpenAiCompatibleBaseUrl(providerType, route.getBaseUrl()));
        return snapshot;
    }

    private AiModel resolveModel(AiModelRouteVO route) {
        if (route.getModelId() != null) {
            AiModel model = aiModelMapper.selectById(route.getModelId());
            if (model != null) {
                return model;
            }
        }
        if (StringUtils.isNotBlank(route.getModelCode())) {
            return aiModelMapper.selectOne(com.baomidou.mybatisplus.core.toolkit.Wrappers.<AiModel>lambdaQuery()
                    .eq(AiModel::getModelCode, route.getModelCode())
                    .last("limit 1"));
        }
        return null;
    }

    private AiProvider resolveProvider(AiModelRouteVO route, AiModel model) {
        if (route.getProviderId() != null) {
            AiProvider provider = aiProviderMapper.selectById(route.getProviderId());
            if (provider != null) {
                return provider;
            }
        }
        if (model != null && model.getProviderId() != null) {
            AiProvider provider = aiProviderMapper.selectById(model.getProviderId());
            if (provider != null) {
                return provider;
            }
        }
        if (StringUtils.isNotBlank(route.getProviderCode())) {
            return aiProviderMapper.selectOne(com.baomidou.mybatisplus.core.toolkit.Wrappers.<AiProvider>lambdaQuery()
                    .eq(AiProvider::getProviderCode, route.getProviderCode())
                    .last("limit 1"));
        }
        return null;
    }

    private String resolveRuntimeApiKey(AiProvider provider) {
        if (provider == null) {
            return null;
        }
        String authType = StringUtils.isBlank(provider.getAuthType()) ? "API_KEY" : provider.getAuthType().trim();
        if ("NONE".equalsIgnoreCase(authType)) {
            return StringUtils.isNotBlank(provider.getApiKeyCipher()) ? provider.getApiKeyCipher() : "LOCAL_PLACEHOLDER";
        }
        return StringUtils.isNotBlank(provider.getApiKeyCipher()) ? provider.getApiKeyCipher() : null;
    }

    private String buildSnapshotKey(AiModelRouteVO route, String source) {
        String regionPart = route.getRegion() == null ? "region" : route.getRegion().name();
        String providerPart = StringUtils.defaultIfBlank(route.getProviderCode(),
                route.getProviderId() == null ? "provider" : String.valueOf(route.getProviderId()));
        String modelPart = StringUtils.defaultIfBlank(route.getModelCode(),
                route.getModelId() == null ? "default" : String.valueOf(route.getModelId()));
        return source + ":" + regionPart + ":" + providerPart + ":" + modelPart;
    }

    private boolean isSnapshotCacheEnabled() {
        FastBeeAiProperties.RuntimeProperties runtime = properties == null ? null : properties.getRuntime();
        return runtime == null || (runtime.isModelSnapshotCacheEnabled() && runtime.getModelSnapshotCacheTtlMs() > 0);
    }

    private long resolveSnapshotCacheTtlMs() {
        FastBeeAiProperties.RuntimeProperties runtime = properties == null ? null : properties.getRuntime();
        if (runtime == null || runtime.getModelSnapshotCacheTtlMs() <= 0) {
            return DEFAULT_CACHE_TTL_MS;
        }
        return runtime.getModelSnapshotCacheTtlMs();
    }

    private long currentTimeMillis() {
        return nowSupplier.getAsLong();
    }

    private double calculateHitRate(long actualHitCount, long lookupCount) {
        if (lookupCount <= 0) {
            return 0D;
        }
        return Math.round((actualHitCount * 1.0D / lookupCount) * 10000D) / 10000D;
    }

    private AiRuntimeModelSnapshot copySnapshot(AiRuntimeModelSnapshot source) {
        if (source == null) {
            return null;
        }
        AiRuntimeModelSnapshot target = new AiRuntimeModelSnapshot();
        target.setSnapshotSource(source.getSnapshotSource());
        target.setSnapshotKey(source.getSnapshotKey());
        target.setRegion(source.getRegion());
        target.setProviderId(source.getProviderId());
        target.setProviderCode(source.getProviderCode());
        target.setProviderName(source.getProviderName());
        target.setProviderType(source.getProviderType());
        target.setModelId(source.getModelId());
        target.setModelCode(source.getModelCode());
        target.setModelName(source.getModelName());
        target.setModelType(source.getModelType());
        target.setApiBaseUrl(source.getApiBaseUrl());
        target.setAuthType(source.getAuthType());
        target.setRuntimeApiKey(source.getRuntimeApiKey());
        target.setRequestOptions(source.getRequestOptions());
        return target;
    }

    private static class CachedSnapshotEntry {
        private final AiRuntimeModelSnapshot snapshot;
        private final long expireAt;

        private CachedSnapshotEntry(AiRuntimeModelSnapshot snapshot, long expireAt) {
            this.snapshot = snapshot;
            this.expireAt = expireAt;
        }

        private boolean isExpired(long now) {
            return now >= expireAt;
        }
    }

    private static class CacheResolveDecision {
        private boolean hit;
        private boolean coldMiss;
        private boolean expiredMiss;
    }
}
