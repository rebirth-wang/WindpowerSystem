package com.fastbee.ai.service.impl.runtime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.ai.service.IAiCodebaseGuideKnowledgeService;

/**
 * 源码导航知识库运行态处理器。
 */
@Service
public class AiCodebaseGuideKnowledgeRuntimeHandler extends AbstractSnapshotKnowledgeRuntimeHandler {

    private static final String KB_TYPE_CODEBASE_GUIDE = "CODEBASE_GUIDE";
    private static final String RUNTIME_SCENE_CODEBASE = "CODEBASE_GUIDE_RUNTIME";

    @Autowired(required = false)
    private IAiCodebaseGuideKnowledgeService aiCodebaseGuideKnowledgeService;

    @Override
    public boolean supports(String kbType) {
        return KB_TYPE_CODEBASE_GUIDE.equalsIgnoreCase(kbType);
    }

    @Override
    protected String runtimeScene() {
        return RUNTIME_SCENE_CODEBASE;
    }

    @Override
    protected String runtimeTargetName() {
        return "源码导航摘要";
    }

    @Override
    protected String expectedStoreType() {
        return "源码安全摘要快照";
    }

    @Override
    protected String readerStoreType() {
        return "源码导航检索";
    }

    @Override
    protected boolean isRuntimeImplemented() {
        return aiCodebaseGuideKnowledgeService != null;
    }

    @Override
    protected String missingImplementationIssue() {
        return "源码导航消费实现未接入";
    }
}
