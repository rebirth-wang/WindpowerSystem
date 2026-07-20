package com.fastbee.ai.service;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.model.vo.AiKnowledgeRuntimeStatusVO;
import com.fastbee.ai.model.vo.AiKnowledgeRuntimeSyncResultVO;
import com.fastbee.common.exception.ServiceException;

/**
 * AI 知识库运行态处理器。
 */
public interface IAiKnowledgeRuntimeHandler {

    /**
     * 是否支持当前知识库类型。
     *
     * @param kbType 知识库类型
     * @return 是否支持
     */
    boolean supports(String kbType);

    /**
     * 是否为默认兜底处理器。
     *
     * @return 是否默认处理器
     */
    default boolean isDefaultHandler() {
        return false;
    }

    /**
     * 构建知识库运行态详情。
     *
     * @param knowledgeBase 知识库
     * @param activeVersion 当前已发布版本
     * @return 运行态详情
     */
    AiKnowledgeRuntimeStatusVO buildStatus(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion activeVersion);

    /**
     * 是否支持重建运行态。
     *
     * @return 是否支持
     */
    default boolean supportsRebuild() {
        return false;
    }

    /**
     * 重建运行态。
     *
     * @param knowledgeBase 知识库
     * @param activeVersion 当前已发布版本
     * @return 重建结果
     */
    default AiKnowledgeRuntimeSyncResultVO rebuild(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion activeVersion) {
        throw new ServiceException(message("ai.knowledge.runtime.rebuild.unsupported"));
    }
}
