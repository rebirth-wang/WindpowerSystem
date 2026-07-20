package com.fastbee.ai.service;

import org.springframework.web.multipart.MultipartFile;

import com.fastbee.ai.model.vo.AiCodebaseGuideContextVO;
import com.fastbee.ai.model.vo.AiKnowledgeTemplateUploadResultVO;

/**
 * 源码导航知识服务。
 */
public interface IAiCodebaseGuideKnowledgeService {

    /**
     * 重建源码导航知识文档。
     *
     * @param knowledgeBaseId 知识库 ID
     * @return 构建结果
     */
    AiKnowledgeTemplateUploadResultVO rebuildCodebaseGuide(Long knowledgeBaseId);

    /**
     * 上传本地生成的源码导航安全摘要 JSON。
     *
     * @param knowledgeBaseId 知识库 ID
     * @param sourceOrigin    来源类型
     * @param appVersion      平台版本
     * @param sortNum         排序号
     * @param file            源码导航安全摘要 JSON
     * @return 导入结果
     */
    AiKnowledgeTemplateUploadResultVO uploadCodebaseGuideSnapshot(Long knowledgeBaseId, String sourceOrigin,
                                                                  String appVersion, Integer sortNum,
                                                                  MultipartFile file);

    /**
     * 构建源码导航问答上下文。
     *
     * @param question 用户问题
     * @return 源码导航上下文
     */
    AiCodebaseGuideContextVO buildCodebaseContext(String question);
}
