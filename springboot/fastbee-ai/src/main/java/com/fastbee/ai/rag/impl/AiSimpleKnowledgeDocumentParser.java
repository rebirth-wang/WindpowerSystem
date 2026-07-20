package com.fastbee.ai.rag.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.rag.IAiKnowledgeDocumentParser;
import com.fastbee.ai.rag.model.AiKnowledgeDocumentReference;
import com.fastbee.ai.rag.model.AiKnowledgeSegment;
import com.fastbee.common.utils.StringUtils;

/**
 * 简单知识文档解析器。
 *
 * <p>当前首版先基于字符窗口与换行边界做切分，后续再接入更细粒度的
 * 语义切分或专用文档解析器。</p>
 */
@Service
public class AiSimpleKnowledgeDocumentParser implements IAiKnowledgeDocumentParser {

    @Resource
    private FastBeeAiProperties fastBeeAiProperties;

    @Override
    public List<AiKnowledgeSegment> split(AiKnowledgeDocumentReference documentReference, String documentContent) {
        if (documentReference == null || StringUtils.isBlank(documentContent)) {
            return Collections.emptyList();
        }
        String normalizedContent = normalizeContent(documentContent);
        if (StringUtils.isBlank(normalizedContent)) {
            return Collections.emptyList();
        }
        int chunkSize = Math.max(200, fastBeeAiProperties.getRag().getChunkSize());
        int chunkOverlap = Math.max(0, Math.min(fastBeeAiProperties.getRag().getChunkOverlap(), chunkSize / 2));
        List<AiKnowledgeSegment> result = new ArrayList<>();
        int start = 0;
        int chunkIndex = 0;
        while (start < normalizedContent.length()) {
            int rawEnd = Math.min(start + chunkSize, normalizedContent.length());
            int actualEnd = adjustChunkEnd(normalizedContent, start, rawEnd, chunkSize);
            String segmentContent = normalizedContent.substring(start, actualEnd).trim();
            if (StringUtils.isNotBlank(segmentContent)) {
                result.add(buildSegment(documentReference, chunkIndex, segmentContent));
                chunkIndex++;
            }
            if (actualEnd >= normalizedContent.length()) {
                break;
            }
            start = Math.max(actualEnd - chunkOverlap, start + 1);
        }
        return result;
    }

    private String normalizeContent(String documentContent) {
        return documentContent.replace("\r\n", "\n")
                .replace('\r', '\n')
                .replace("\u0000", "")
                .trim();
    }

    private int adjustChunkEnd(String content, int start, int rawEnd, int chunkSize) {
        if (rawEnd >= content.length()) {
            return rawEnd;
        }
        int minEnd = Math.min(content.length(), start + Math.max(120, chunkSize / 2));
        int newlinePos = content.lastIndexOf('\n', rawEnd);
        if (newlinePos >= minEnd) {
            return newlinePos + 1;
        }
        int punctuationPos = Math.max(content.lastIndexOf('。', rawEnd), content.lastIndexOf('.', rawEnd));
        if (punctuationPos >= minEnd) {
            return punctuationPos + 1;
        }
        return rawEnd;
    }

    private AiKnowledgeSegment buildSegment(AiKnowledgeDocumentReference documentReference, int chunkIndex, String segmentContent) {
        AiKnowledgeSegment segment = new AiKnowledgeSegment();
        segment.setSegmentCode(documentReference.getDocumentCode() + "_SEG_" + String.format("%04d", chunkIndex + 1));
        segment.setKnowledgeBaseId(documentReference.getKnowledgeBaseId());
        segment.setDocumentId(documentReference.getDocumentId());
        segment.setChunkIndex(chunkIndex + 1);
        segment.setSegmentTitle(documentReference.getFileName() + " - 分段 " + (chunkIndex + 1));
        segment.setContent(segmentContent);
        segment.setContentLength(segmentContent.length());
        segment.setSourceCode(documentReference.getSourceCode());
        segment.setSourceName(documentReference.getSourceName());
        return segment;
    }
}
