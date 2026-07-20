package com.fastbee.ai.service.impl.runtime;

import java.util.List;

import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.model.vo.AiKnowledgeRuntimeStatusVO;
import com.fastbee.ai.service.IAiKnowledgeRuntimeHandler;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 知识库运行态处理器抽象基类。
 */
public abstract class AbstractKnowledgeRuntimeHandler implements IAiKnowledgeRuntimeHandler {

    protected static final String KNOWLEDGE_STATUS_ENABLED = "0";
    protected static final String DEFAULT_STORE_TYPE = "REDIS";

    /**
     * 初始化基础运行态对象。
     *
     * @param knowledgeBase 知识库
     * @return 基础运行态对象
     */
    protected AiKnowledgeRuntimeStatusVO initStatus(AiKnowledgeBase knowledgeBase) {
        AiKnowledgeRuntimeStatusVO status = new AiKnowledgeRuntimeStatusVO();
        status.setKnowledgeBaseId(knowledgeBase.getKnowledgeBaseId());
        status.setKbCode(knowledgeBase.getKbCode());
        status.setKbName(knowledgeBase.getKbName());
        status.setKbType(knowledgeBase.getKbType());
        status.setKnowledgeStatus(knowledgeBase.getStatus());
        status.setPublishStatus(knowledgeBase.getPublishStatus());
        status.setActiveVersionId(knowledgeBase.getActiveVersionId());
        return status;
    }

    /**
     * 统一收口运行态摘要与问题明细。
     *
     * @param status  运行态对象
     * @param issues  问题列表
     * @param summary 摘要
     */
    protected void finalizeStatus(AiKnowledgeRuntimeStatusVO status, List<String> issues, String summary) {
        status.setIssues(issues);
        status.setConsistent(issues == null || issues.isEmpty());
        status.setSummary(summary);
    }

    /**
     * 兜底处理计数。
     *
     * @param count 原始计数
     * @return 实际计数
     */
    protected int defaultCount(Integer count) {
        return count == null ? 0 : count;
    }

    /**
     * 空白字符串兜底。
     *
     * @param value        原始值
     * @param defaultValue 默认值
     * @return 处理结果
     */
    protected String defaultIfBlank(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    /**
     * 裁剪异常信息长度，避免问题明细过长。
     *
     * @param message 原始异常
     * @return 裁剪结果
     */
    protected String trimMessage(String message) {
        if (StringUtils.isBlank(message)) {
            return "未知异常";
        }
        String actualMessage = message.trim();
        return actualMessage.length() > 200 ? actualMessage.substring(0, 200) : actualMessage;
    }
}
