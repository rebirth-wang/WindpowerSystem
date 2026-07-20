package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSONObject;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import com.fastbee.ai.model.runtime.AiRuntimeModelSnapshot;
import com.fastbee.ai.model.vo.AiModelRouteVO;
import com.fastbee.ai.service.AiChatModelFactoryService;
import com.fastbee.ai.service.AiRuntimeModelSnapshotService;
import com.fastbee.ai.support.AiProviderBaseUrlSupport;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 运行时模型工厂实现。
 *
 * <p>一期优先基于 OpenAI 兼容协议构建动态模型，
 * 兼容国内外常见模型厂商的运行时切换。</p>
 */
@Service
public class AiChatModelFactoryServiceImpl implements AiChatModelFactoryService {

    @Resource
    private AiRuntimeModelSnapshotService aiRuntimeModelSnapshotService;

    /**
     * 多模型 Bean 属于特殊场景，这里使用自动注入集合处理。
     */
    @Autowired(required = false)
    private List<ChatModel> chatModels = Collections.emptyList();

    /**
     * 默认 OpenAI ChatModel 作为动态构建模板，允许为空。
     */
    @Autowired(required = false)
    private OpenAiChatModel openAiChatModel;

    /**
     * 运行时动态构建 OpenAI 模型时复用系统已有重试模板。
     */
    @Autowired(required = false)
    private RetryTemplate retryTemplate;

    /**
     * 运行时动态构建 OpenAI 模型时复用系统已有观测注册器。
     */
    @Autowired(required = false)
    private ObservationRegistry observationRegistry;

    /**
     * 工具调用管理器在后续引入 Skills/Tool Calling 时继续复用。
     */
    @Autowired(required = false)
    private ToolCallingManager toolCallingManager;

    @Override
    public ChatModel resolveChatModel(AiRuntimeModelSnapshot snapshot) {
        if (snapshot == null) {
            throw new ServiceException(message("ai.model.snapshot.required"));
        }

        if (StringUtils.isNotBlank(snapshot.getApiBaseUrl())
                && snapshot.getRuntimeApiKey() != null) {
            return buildOpenAiCompatibleModel(snapshot);
        }

        if (openAiChatModel != null) {
            return openAiChatModel.mutate()
                    .defaultOptions(buildOpenAiChatOptions(snapshot.getModelCode(), snapshot.getRequestOptions()))
                    .build();
        }

        if (chatModels != null && !chatModels.isEmpty()) {
            return chatModels.get(0);
        }
        throw new ServiceException(message("ai.model.available.required"));
    }

    @Override
    public ChatModel resolveChatModel(AiModelRouteVO route) {
        return resolveChatModel(aiRuntimeModelSnapshotService.resolveSnapshot(route));
    }

    private ChatModel buildOpenAiCompatibleModel(AiRuntimeModelSnapshot snapshot) {
        OpenAiApi.Builder apiBuilder = OpenAiApi.builder()
                .baseUrl(snapshot.getApiBaseUrl())
                .apiKey(snapshot.getRuntimeApiKey());
        String completionsPath = AiProviderBaseUrlSupport.resolveChatCompletionsPath(snapshot.getProviderType());
        if (StringUtils.isNotBlank(completionsPath)) {
            apiBuilder.completionsPath(completionsPath);
        }
        OpenAiApi openAiApi = apiBuilder.build();
        OpenAiChatOptions options = buildOpenAiChatOptions(snapshot.getModelCode(), snapshot.getRequestOptions());

        if (openAiChatModel != null) {
            return openAiChatModel.mutate()
                    .openAiApi(openAiApi)
                    .defaultOptions(options)
                    .build();
        }

        OpenAiChatModel.Builder builder = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .retryTemplate(retryTemplate != null ? retryTemplate : RetryTemplate.defaultInstance())
                .observationRegistry(observationRegistry != null ? observationRegistry : ObservationRegistry.NOOP);

        if (toolCallingManager != null) {
            builder.toolCallingManager(toolCallingManager);
        }
        return builder.build();
    }

    private OpenAiChatOptions buildOpenAiChatOptions(String modelCode, String requestOptions) {
        OpenAiChatOptions.Builder builder = OpenAiChatOptions.builder();
        if (StringUtils.isNotBlank(modelCode)) {
            builder.model(modelCode);
        }
        if (StringUtils.isNotBlank(requestOptions)) {
            applyRequestOptions(builder, requestOptions);
        }
        return builder.build();
    }

    private void applyRequestOptions(OpenAiChatOptions.Builder builder, String requestOptions) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(requestOptions);
            if (jsonObject == null || jsonObject.isEmpty()) {
                return;
            }
            if (jsonObject.containsKey("temperature")) {
                builder.temperature(jsonObject.getDouble("temperature"));
            }
            if (jsonObject.containsKey("topP")) {
                builder.topP(jsonObject.getDouble("topP"));
            }
            if (jsonObject.containsKey("maxTokens")) {
                builder.maxTokens(jsonObject.getInteger("maxTokens"));
            }
            if (jsonObject.containsKey("presencePenalty")) {
                builder.presencePenalty(jsonObject.getDouble("presencePenalty"));
            }
            if (jsonObject.containsKey("frequencyPenalty")) {
                builder.frequencyPenalty(jsonObject.getDouble("frequencyPenalty"));
            }
            if (jsonObject.containsKey("seed")) {
                builder.seed(jsonObject.getInteger("seed"));
            }
            if (jsonObject.containsKey("stop")) {
                List<String> stopWords = resolveStopWords(jsonObject);
                if (!stopWords.isEmpty()) {
                    builder.stop(stopWords);
                }
            }
            if (jsonObject.containsKey("user")) {
                builder.user(jsonObject.getString("user"));
            }
        } catch (Exception ignore) {
            // 一期先容错处理错误 JSON，避免影响模型基础可用性。
        }
    }

    private List<String> resolveStopWords(JSONObject jsonObject) {
        Object stopValue = jsonObject.get("stop");
        if (stopValue instanceof List<?> stopList) {
            return stopList.stream()
                    .map(item -> item == null ? null : String.valueOf(item))
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
        }
        String stopText = jsonObject.getString("stop");
        if (StringUtils.isBlank(stopText)) {
            return Collections.emptyList();
        }
        return Collections.singletonList(stopText);
    }
}
