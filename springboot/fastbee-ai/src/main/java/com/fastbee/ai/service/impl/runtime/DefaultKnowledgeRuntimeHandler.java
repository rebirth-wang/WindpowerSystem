package com.fastbee.ai.service.impl.runtime;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.model.vo.AiKnowledgeRuntimeStatusVO;

/**
 * 默认知识库运行态兜底处理器。
 */
@Service
public class DefaultKnowledgeRuntimeHandler extends AbstractSnapshotKnowledgeRuntimeHandler {

    private static final String RUNTIME_SCENE_DEFAULT = "DEFAULT_RUNTIME";

    @Override
    public boolean supports(String kbType) {
        return true;
    }

    @Override
    public boolean isDefaultHandler() {
        return true;
    }

    @Override
    protected String runtimeScene() {
        return RUNTIME_SCENE_DEFAULT;
    }

    @Override
    protected String runtimeTargetName() {
        return "默认消费快照";
    }

    @Override
    protected String expectedStoreType() {
        return "默认快照";
    }

    @Override
    protected String readerStoreType() {
        return "默认运行态策略";
    }

    @Override
    protected boolean isRuntimeImplemented() {
        return true;
    }

    @Override
    protected String missingImplementationIssue() {
        return "当前知识库类型尚未接入专属运行态策略";
    }

    @Override
    protected void appendCustomIssues(List<String> issues,
                                      AiKnowledgeBase knowledgeBase,
                                      AiKnowledgeVersion activeVersion,
                                      AiKnowledgeRuntimeStatusVO status) {
        issues.add("当前知识库类型尚未接入专属运行态策略，已使用默认运行态详情");
    }

    @Override
    protected String buildEnabledSummary(List<String> issues,
                                         AiKnowledgeBase knowledgeBase,
                                         AiKnowledgeVersion activeVersion,
                                         AiKnowledgeRuntimeStatusVO status) {
        return "当前知识库类型尚未接入专属运行态策略，已显示默认运行态信息";
    }
}
