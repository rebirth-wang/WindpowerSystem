package com.fastbee.ai.service.impl;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.model.vo.AiPlatformAssistantContextSnapshotVO;
import com.fastbee.ai.model.vo.AiPlatformDocKnowledgeChunkVO;
import com.fastbee.ai.model.vo.AiPlatformDocKnowledgeContextVO;

class AiPlatformAssistantConversationContextServiceImplTest {

    private final AiPlatformAssistantConversationContextServiceImpl service = new AiPlatformAssistantConversationContextServiceImpl();

    @Test
    void shouldResolveLatestSnapshotFromUserToolResult() {
        AiChatMessage userMessage = new AiChatMessage();
        userMessage.setRoleType("user");
        userMessage.setMessageStatus("SUCCESS");

        AiPlatformAssistantContextSnapshotVO snapshot = new AiPlatformAssistantContextSnapshotVO();
        snapshot.setMenuPath("系统管理 > 用户管理");
        snapshot.setPageTitle("用户管理");
        snapshot.setHeadingPath("新增用户");
        service.attachContextSnapshot(userMessage, snapshot);

        AiPlatformAssistantContextSnapshotVO resolved = service.resolveLatestContext(List.of(userMessage));

        Assertions.assertNotNull(resolved);
        Assertions.assertEquals("系统管理 > 用户管理", resolved.getMenuPath());
        Assertions.assertEquals("用户管理", resolved.getPageTitle());
        Assertions.assertEquals("新增用户", resolved.getHeadingPath());
    }

    @Test
    void shouldEnrichFollowUpQuestionWithMenuAndPageHints() {
        AiChatMessage userMessage = new AiChatMessage();
        userMessage.setRoleType("user");
        userMessage.setMessageStatus("SUCCESS");

        AiPlatformAssistantContextSnapshotVO snapshot = new AiPlatformAssistantContextSnapshotVO();
        snapshot.setMenuPath("系统管理 > 用户管理");
        snapshot.setPageTitle("用户管理");
        snapshot.setHeadingPath("新增用户");
        service.attachContextSnapshot(userMessage, snapshot);

        String enrichedQuestion = service.enrichExecutionQuestion("继续说下一步怎么做", List.of(userMessage));

        Assertions.assertTrue(enrichedQuestion.contains("菜单路径 系统管理 > 用户管理"));
        Assertions.assertTrue(enrichedQuestion.contains("页面 用户管理"));
        Assertions.assertTrue(enrichedQuestion.contains("标题路径 新增用户"));
    }

    @Test
    void shouldBuildContextSnapshotFromPlatformKnowledgeContext() {
        AiPlatformDocKnowledgeChunkVO chunk = new AiPlatformDocKnowledgeChunkVO();
        chunk.setMenuPath("系统管理 > 用户管理");
        chunk.setPageTitle("用户管理");
        chunk.setHeadingPath("新增用户");
        chunk.setSectionName("用户管理");
        chunk.setKnowledgeType("GUIDE");
        chunk.setTargetRole("系统管理员");
        chunk.setSourceUrl("https://example.com/user");
        chunk.setContentPreview("进入用户管理页面后点击新增。");

        AiPlatformDocKnowledgeContextVO context = new AiPlatformDocKnowledgeContextVO();
        context.setKbCode("PLATFORM_DOC");
        context.setKbName("平台文档知识库");
        context.setVersionNo("v1");
        context.setChunks(List.of(chunk));

        AiPlatformAssistantContextSnapshotVO snapshot = service.buildContextSnapshot("用户怎么新增", context);

        Assertions.assertNotNull(snapshot);
        Assertions.assertEquals("PLATFORM_DOC", snapshot.getKbCode());
        Assertions.assertEquals("平台文档知识库", snapshot.getKbName());
        Assertions.assertEquals("v1", snapshot.getVersionNo());
        Assertions.assertEquals("系统管理 > 用户管理", snapshot.getMenuPath());
        Assertions.assertEquals("用户管理", snapshot.getPageTitle());
        Assertions.assertEquals("新增用户", snapshot.getHeadingPath());
    }
}
