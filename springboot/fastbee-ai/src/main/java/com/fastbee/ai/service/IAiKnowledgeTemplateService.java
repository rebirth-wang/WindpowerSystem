package com.fastbee.ai.service;

import jakarta.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.ai.model.vo.AiKnowledgeDocumentParseResultVO;
import com.fastbee.ai.model.vo.AiKnowledgeTemplateUploadResultVO;

/**
 * AI 知识库模板服务。
 */
public interface IAiKnowledgeTemplateService {

    /**
     * 下载知识库模板。
     *
     * @param response        响应对象
     * @param knowledgeBaseId 知识库 ID
     * @param kbType          知识库类型
     * @param templateMode    模板模式
     * @param sourceStrategy  导出源策略
     */
    void downloadTemplate(HttpServletResponse response, Long knowledgeBaseId, String kbType,
                          String templateMode, String sourceStrategy);

    /**
     * 上传并解析知识库模板。
     *
     * @param knowledgeBaseId 知识库 ID
     * @param sourceOrigin    来源类型
     * @param appVersion      平台版本
     * @param sortNum         排序号
     * @param file            模板文件
     * @return 上传解析结果
     * @throws Exception 上传异常
     */
    AiKnowledgeTemplateUploadResultVO uploadAndParseTemplate(Long knowledgeBaseId, String sourceOrigin,
                                                             String appVersion, Integer sortNum,
                                                             MultipartFile file) throws Exception;

    /**
     * 重新解析知识文档。
     *
     * @param documentId 文档 ID
     * @return 解析结果
     */
    AiKnowledgeDocumentParseResultVO reparseDocument(Long documentId);

    /**
     * 查询知识文档结构化快照预览。
     *
     * @param documentId  文档 ID
     * @param previewSize 预览条数
     * @return 快照预览
     */
    JSONObject loadDocumentSnapshot(Long documentId, Integer previewSize);
}
