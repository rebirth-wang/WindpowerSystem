package com.fastbee.ai.service.impl.runtime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.ai.service.IAiProtocolKnowledgeService;

/**
 * 协议知识库运行态处理器。
 */
@Service
public class AiProtocolKnowledgeRuntimeHandler extends AbstractSnapshotKnowledgeRuntimeHandler {

    private static final String KB_TYPE_PROTOCOL = "PROTOCOL_SPEC";
    private static final String RUNTIME_SCENE_PROTOCOL = "PROTOCOL_SNAPSHOT";

    @Autowired(required = false)
    private IAiProtocolKnowledgeService aiProtocolKnowledgeService;

    @Override
    public boolean supports(String kbType) {
        return KB_TYPE_PROTOCOL.equalsIgnoreCase(kbType);
    }

    @Override
    protected String runtimeScene() {
        return RUNTIME_SCENE_PROTOCOL;
    }

    @Override
    protected String runtimeTargetName() {
        return "协议消费快照";
    }

    @Override
    protected String expectedStoreType() {
        return "结构化快照";
    }

    @Override
    protected String readerStoreType() {
        return "协议知识消费";
    }

    @Override
    protected boolean isRuntimeImplemented() {
        return aiProtocolKnowledgeService != null;
    }

    @Override
    protected String missingImplementationIssue() {
        return "协议知识消费实现未接入";
    }
}
