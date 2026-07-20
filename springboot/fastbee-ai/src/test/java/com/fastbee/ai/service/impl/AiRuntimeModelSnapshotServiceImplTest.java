package com.fastbee.ai.service.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.domain.AiModel;
import com.fastbee.ai.domain.AiProvider;
import com.fastbee.ai.mapper.AiModelMapper;
import com.fastbee.ai.mapper.AiProviderMapper;
import com.fastbee.ai.model.enums.AiModelProviderType;
import com.fastbee.ai.model.enums.AiRegionProfile;
import com.fastbee.ai.model.runtime.AiRuntimeModelSnapshot;
import com.fastbee.ai.model.vo.AiModelRouteVO;
import com.fastbee.ai.model.vo.AiRuntimeModelSnapshotCacheStatsVO;
import com.fastbee.ai.service.AiModelRoutingService;

/**
 * 运行时模型快照服务回归测试。
 */
class AiRuntimeModelSnapshotServiceImplTest {

    private final AiRuntimeModelSnapshotServiceImpl service = new AiRuntimeModelSnapshotServiceImpl();
    private final FastBeeAiProperties properties = new FastBeeAiProperties();
    private final AiModelRoutingService aiModelRoutingService = Mockito.mock(AiModelRoutingService.class);
    private final AiProviderMapper aiProviderMapper = Mockito.mock(AiProviderMapper.class);
    private final AiModelMapper aiModelMapper = Mockito.mock(AiModelMapper.class);
    private final AtomicLong now = new AtomicLong(1000L);

    AiRuntimeModelSnapshotServiceImplTest() {
        properties.getRuntime().setModelSnapshotCacheEnabled(true);
        properties.getRuntime().setModelSnapshotCacheTtlMs(300000L);
        ReflectionTestUtils.setField(service, "properties", properties);
        ReflectionTestUtils.setField(service, "aiModelRoutingService", aiModelRoutingService);
        ReflectionTestUtils.setField(service, "aiProviderMapper", aiProviderMapper);
        ReflectionTestUtils.setField(service, "aiModelMapper", aiModelMapper);
        ReflectionTestUtils.setField(service, "nowSupplier", (java.util.function.LongSupplier) now::get);
    }

    @Test
    void shouldBuildDatabaseSnapshotFromProviderAndModel() {
        AiModelRouteVO route = new AiModelRouteVO();
        route.setProviderId(10L);
        route.setProviderCode("deepseek");
        route.setProviderName("DeepSeek");
        route.setModelId(20L);
        route.setModelCode("deepseek-chat");
        route.setModelName("DeepSeek Chat");
        route.setBaseUrl("https://route.example.com");
        route.setRegion(AiRegionProfile.CN);

        AiProvider provider = new AiProvider()
                .setProviderId(10L)
                .setProviderCode("deepseek")
                .setProviderName("DeepSeek")
                .setApiBaseUrl("https://api.deepseek.com")
                .setAuthType("API_KEY")
                .setApiKeyCipher("cipher-key");
        AiModel model = new AiModel()
                .setModelId(20L)
                .setProviderId(10L)
                .setModelCode("deepseek-chat")
                .setModelName("DeepSeek Chat")
                .setModelType("CHAT")
                .setRequestOptions("{\"temperature\":0.2}");

        Mockito.when(aiModelMapper.selectById(20L)).thenReturn(model);
        Mockito.when(aiProviderMapper.selectById(10L)).thenReturn(provider);

        AiRuntimeModelSnapshot snapshot = service.resolveSnapshot(route);

        Assertions.assertEquals("DB", snapshot.getSnapshotSource());
        Assertions.assertEquals("DB:CN:deepseek:deepseek-chat", snapshot.getSnapshotKey());
        Assertions.assertEquals(AiRegionProfile.CN, snapshot.getRegion());
        Assertions.assertEquals(10L, snapshot.getProviderId());
        Assertions.assertEquals("deepseek", snapshot.getProviderCode());
        Assertions.assertEquals("DeepSeek", snapshot.getProviderName());
        Assertions.assertEquals(AiModelProviderType.DEEPSEEK, snapshot.getProviderType());
        Assertions.assertEquals(20L, snapshot.getModelId());
        Assertions.assertEquals("deepseek-chat", snapshot.getModelCode());
        Assertions.assertEquals("DeepSeek Chat", snapshot.getModelName());
        Assertions.assertEquals("CHAT", snapshot.getModelType());
        Assertions.assertEquals("https://api.deepseek.com/", snapshot.getApiBaseUrl());
        Assertions.assertEquals("API_KEY", snapshot.getAuthType());
        Assertions.assertEquals("cipher-key", snapshot.getRuntimeApiKey());
        Assertions.assertEquals("{\"temperature\":0.2}", snapshot.getRequestOptions());
    }

    @Test
    void shouldBuildYamlSnapshotWhenRouteComesFromConfig() {
        AiModelRouteVO route = new AiModelRouteVO();
        route.setProviderCode("openai");
        route.setProviderName("OpenAI");
        route.setProviderType(AiModelProviderType.OPENAI);
        route.setModelCode("gpt-4.1-mini");
        route.setModelName("GPT-4.1 Mini");
        route.setBaseUrl("https://api.openai.com");
        route.setRegion(AiRegionProfile.GLOBAL);

        AiRuntimeModelSnapshot snapshot = service.resolveSnapshot(route);

        Assertions.assertEquals("YAML", snapshot.getSnapshotSource());
        Assertions.assertEquals("YAML:GLOBAL:openai:gpt-4.1-mini", snapshot.getSnapshotKey());
        Assertions.assertEquals(AiRegionProfile.GLOBAL, snapshot.getRegion());
        Assertions.assertEquals("openai", snapshot.getProviderCode());
        Assertions.assertEquals("OpenAI", snapshot.getProviderName());
        Assertions.assertEquals(AiModelProviderType.OPENAI, snapshot.getProviderType());
        Assertions.assertEquals("gpt-4.1-mini", snapshot.getModelCode());
        Assertions.assertEquals("GPT-4.1 Mini", snapshot.getModelName());
        Assertions.assertEquals("https://api.openai.com/", snapshot.getApiBaseUrl());
        Assertions.assertNull(snapshot.getRuntimeApiKey());
        Assertions.assertNull(snapshot.getRequestOptions());
        Mockito.verifyNoInteractions(aiProviderMapper, aiModelMapper);
    }

    @Test
    void shouldUseLocalPlaceholderWhenAuthTypeIsNoneAndApiKeyMissing() {
        AiModelRouteVO route = new AiModelRouteVO();
        route.setProviderId(30L);
        route.setProviderCode("local");
        route.setModelId(40L);
        route.setModelCode("local-chat");

        AiProvider provider = new AiProvider()
                .setProviderId(30L)
                .setProviderCode("local")
                .setProviderName("本地模型")
                .setAuthType("NONE")
                .setApiBaseUrl("http://127.0.0.1:11434");
        AiModel model = new AiModel()
                .setModelId(40L)
                .setProviderId(30L)
                .setModelCode("local-chat")
                .setModelName("Local Chat");

        Mockito.when(aiModelMapper.selectById(40L)).thenReturn(model);
        Mockito.when(aiProviderMapper.selectById(30L)).thenReturn(provider);

        AiRuntimeModelSnapshot snapshot = service.resolveSnapshot(route);

        Assertions.assertEquals("NONE", snapshot.getAuthType());
        Assertions.assertEquals("LOCAL_PLACEHOLDER", snapshot.getRuntimeApiKey());
        Assertions.assertEquals(AiModelProviderType.OLLAMA, snapshot.getProviderType());
    }

    @Test
    void shouldNormalizeZhipuBaseUrlToOfficialV4CompatibilityEndpoint() {
        AiModelRouteVO route = new AiModelRouteVO();
        route.setProviderId(50L);
        route.setProviderCode("zhipu");
        route.setModelId(60L);
        route.setModelCode("glm-4-flash");
        route.setRegion(AiRegionProfile.CN);

        AiProvider provider = new AiProvider()
                .setProviderId(50L)
                .setProviderCode("zhipu")
                .setProviderName("智谱AI")
                .setApiBaseUrl("https://open.bigmodel.cn/api/paas")
                .setAuthType("API_KEY")
                .setApiKeyCipher("zhipu-key");
        AiModel model = new AiModel()
                .setModelId(60L)
                .setProviderId(50L)
                .setModelCode("glm-4-flash")
                .setModelName("GLM-4-Flash")
                .setModelType("CHAT");

        Mockito.when(aiModelMapper.selectById(60L)).thenReturn(model);
        Mockito.when(aiProviderMapper.selectById(50L)).thenReturn(provider);

        AiRuntimeModelSnapshot snapshot = service.resolveSnapshot(route);

        Assertions.assertEquals(AiModelProviderType.ZHIPU, snapshot.getProviderType());
        Assertions.assertEquals("https://open.bigmodel.cn/api/paas/v4/", snapshot.getApiBaseUrl());
    }

    @Test
    void shouldReuseCachedSnapshotBeforeExpire() {
        AiModelRouteVO route = new AiModelRouteVO();
        route.setProviderId(10L);
        route.setProviderCode("deepseek");
        route.setModelId(20L);
        route.setModelCode("deepseek-chat");
        route.setRegion(AiRegionProfile.CN);

        AiProvider provider = new AiProvider()
                .setProviderId(10L)
                .setProviderCode("deepseek")
                .setProviderName("DeepSeek")
                .setApiBaseUrl("https://api.deepseek.com")
                .setAuthType("API_KEY")
                .setApiKeyCipher("cipher-key");
        AiModel model = new AiModel()
                .setModelId(20L)
                .setProviderId(10L)
                .setModelCode("deepseek-chat")
                .setModelName("DeepSeek Chat")
                .setRequestOptions("{\"temperature\":0.2}");

        Mockito.when(aiModelMapper.selectById(20L)).thenReturn(model);
        Mockito.when(aiProviderMapper.selectById(10L)).thenReturn(provider);

        AiRuntimeModelSnapshot first = service.resolveSnapshot(route);
        AiRuntimeModelSnapshot second = service.resolveSnapshot(route);

        Assertions.assertNotSame(first, second);
        Assertions.assertEquals(first, second);
        Mockito.verify(aiModelMapper, Mockito.times(1)).selectById(20L);
        Mockito.verify(aiProviderMapper, Mockito.times(1)).selectById(10L);

        AiRuntimeModelSnapshotCacheStatsVO stats = service.getSnapshotCacheStats();
        Assertions.assertEquals(2L, stats.getRequestCount());
        Assertions.assertEquals(2L, stats.getCacheLookupCount());
        Assertions.assertEquals(1L, stats.getHitCount());
        Assertions.assertEquals(1L, stats.getColdMissCount());
        Assertions.assertEquals(0L, stats.getExpiredMissCount());
        Assertions.assertEquals(0L, stats.getBypassCount());
        Assertions.assertEquals(0.5D, stats.getHitRate());
    }

    @Test
    void shouldRebuildSnapshotAfterModelCacheEvicted() {
        AiModelRouteVO route = new AiModelRouteVO();
        route.setProviderId(10L);
        route.setProviderCode("deepseek");
        route.setModelId(20L);
        route.setModelCode("deepseek-chat");
        route.setRegion(AiRegionProfile.CN);

        AiProvider provider = new AiProvider()
                .setProviderId(10L)
                .setProviderCode("deepseek")
                .setProviderName("DeepSeek")
                .setApiBaseUrl("https://api.deepseek.com")
                .setAuthType("API_KEY")
                .setApiKeyCipher("cipher-key");
        AiModel oldModel = new AiModel()
                .setModelId(20L)
                .setProviderId(10L)
                .setModelCode("deepseek-chat")
                .setModelName("DeepSeek Chat")
                .setRequestOptions("{\"temperature\":0.2}");
        AiModel newModel = new AiModel()
                .setModelId(20L)
                .setProviderId(10L)
                .setModelCode("deepseek-chat")
                .setModelName("DeepSeek Chat V2")
                .setRequestOptions("{\"temperature\":0.8}");

        Mockito.when(aiModelMapper.selectById(20L)).thenReturn(oldModel, newModel);
        Mockito.when(aiProviderMapper.selectById(10L)).thenReturn(provider);

        AiRuntimeModelSnapshot first = service.resolveSnapshot(route);
        service.evictSnapshotsByModelId(20L);
        AiRuntimeModelSnapshot second = service.resolveSnapshot(route);

        Assertions.assertEquals("{\"temperature\":0.2}", first.getRequestOptions());
        Assertions.assertEquals("{\"temperature\":0.8}", second.getRequestOptions());
        Assertions.assertEquals("DeepSeek Chat V2", second.getModelName());
        Mockito.verify(aiModelMapper, Mockito.times(2)).selectById(20L);
        Mockito.verify(aiProviderMapper, Mockito.times(2)).selectById(10L);

        AiRuntimeModelSnapshotCacheStatsVO stats = service.getSnapshotCacheStats();
        Assertions.assertEquals(2L, stats.getRequestCount());
        Assertions.assertEquals(2L, stats.getColdMissCount());
        Assertions.assertEquals(1L, stats.getModelEvictCount());
    }

    @Test
    void shouldRebuildSnapshotAfterCacheExpired() {
        properties.getRuntime().setModelSnapshotCacheTtlMs(100L);

        AiModelRouteVO route = new AiModelRouteVO();
        route.setProviderId(10L);
        route.setProviderCode("deepseek");
        route.setModelId(20L);
        route.setModelCode("deepseek-chat");
        route.setRegion(AiRegionProfile.CN);

        AiProvider provider = new AiProvider()
                .setProviderId(10L)
                .setProviderCode("deepseek")
                .setProviderName("DeepSeek")
                .setApiBaseUrl("https://api.deepseek.com")
                .setAuthType("API_KEY")
                .setApiKeyCipher("cipher-key");
        AiModel oldModel = new AiModel()
                .setModelId(20L)
                .setProviderId(10L)
                .setModelCode("deepseek-chat")
                .setModelName("DeepSeek Chat")
                .setRequestOptions("{\"temperature\":0.2}");
        AiModel newModel = new AiModel()
                .setModelId(20L)
                .setProviderId(10L)
                .setModelCode("deepseek-chat")
                .setModelName("DeepSeek Chat V2")
                .setRequestOptions("{\"temperature\":0.9}");

        Mockito.when(aiModelMapper.selectById(20L)).thenReturn(oldModel, newModel);
        Mockito.when(aiProviderMapper.selectById(10L)).thenReturn(provider);

        AiRuntimeModelSnapshot first = service.resolveSnapshot(route);
        now.addAndGet(101L);
        AiRuntimeModelSnapshot second = service.resolveSnapshot(route);

        Assertions.assertEquals("{\"temperature\":0.2}", first.getRequestOptions());
        Assertions.assertEquals("{\"temperature\":0.9}", second.getRequestOptions());
        Mockito.verify(aiModelMapper, Mockito.times(2)).selectById(20L);
        Mockito.verify(aiProviderMapper, Mockito.times(2)).selectById(10L);

        AiRuntimeModelSnapshotCacheStatsVO stats = service.getSnapshotCacheStats();
        Assertions.assertEquals(2L, stats.getRequestCount());
        Assertions.assertEquals(1L, stats.getColdMissCount());
        Assertions.assertEquals(1L, stats.getExpiredMissCount());
        Assertions.assertEquals(0L, stats.getHitCount());
        Assertions.assertEquals(0D, stats.getHitRate());
    }

    @Test
    void shouldBypassCacheWhenDisabled() {
        properties.getRuntime().setModelSnapshotCacheEnabled(false);

        AiModelRouteVO route = new AiModelRouteVO();
        route.setProviderId(10L);
        route.setProviderCode("deepseek");
        route.setModelId(20L);
        route.setModelCode("deepseek-chat");
        route.setRegion(AiRegionProfile.CN);

        AiProvider provider = new AiProvider()
                .setProviderId(10L)
                .setProviderCode("deepseek")
                .setProviderName("DeepSeek")
                .setApiBaseUrl("https://api.deepseek.com")
                .setAuthType("API_KEY")
                .setApiKeyCipher("cipher-key");
        AiModel model = new AiModel()
                .setModelId(20L)
                .setProviderId(10L)
                .setModelCode("deepseek-chat")
                .setModelName("DeepSeek Chat")
                .setRequestOptions("{\"temperature\":0.2}");

        Mockito.when(aiModelMapper.selectById(20L)).thenReturn(model);
        Mockito.when(aiProviderMapper.selectById(10L)).thenReturn(provider);

        service.resolveSnapshot(route);
        service.resolveSnapshot(route);

        Mockito.verify(aiModelMapper, Mockito.times(2)).selectById(20L);
        Mockito.verify(aiProviderMapper, Mockito.times(2)).selectById(10L);

        AiRuntimeModelSnapshotCacheStatsVO stats = service.getSnapshotCacheStats();
        Assertions.assertFalse(stats.isCacheEnabled());
        Assertions.assertEquals(2L, stats.getRequestCount());
        Assertions.assertEquals(0L, stats.getCacheLookupCount());
        Assertions.assertEquals(0L, stats.getHitCount());
        Assertions.assertEquals(0L, stats.getColdMissCount());
        Assertions.assertEquals(0L, stats.getExpiredMissCount());
        Assertions.assertEquals(2L, stats.getBypassCount());
        Assertions.assertEquals(0L, stats.getCacheEntryCount());
    }
}
