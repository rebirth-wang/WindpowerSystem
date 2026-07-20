package com.fastbee.ai.service.impl.runtime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.ai.service.IAiPlatformDocKnowledgeService;

/**
 * 平台知识库运行态处理器。
 */
@Service
public class AiPlatformDocKnowledgeRuntimeHandler extends AbstractSnapshotKnowledgeRuntimeHandler {

    private static final String KB_TYPE_PLATFORM = "PLATFORM_DOC";
    private static final String RUNTIME_SCENE_PLATFORM = "PLATFORM_DOC_RUNTIME";

    @Autowired(required = false)
    private IAiPlatformDocKnowledgeService aiPlatformDocKnowledgeService;

    @Override
    public boolean supports(String kbType) {
        return KB_TYPE_PLATFORM.equalsIgnoreCase(kbType);
    }

    @Override
    protected String runtimeScene() {
        return RUNTIME_SCENE_PLATFORM;
    }

    @Override
    protected String runtimeTargetName() {
        return "平台检索快照";
    }

    @Override
    protected String expectedStoreType() {
        return "文档切块快照";
    }

    @Override
    protected String readerStoreType() {
        return "平台文档检索";
    }

    @Override
    protected boolean isRuntimeImplemented() {
        return aiPlatformDocKnowledgeService != null;
    }

    @Override
    protected String missingImplementationIssue() {
        return "平台文档消费实现未接入";
    }
}
