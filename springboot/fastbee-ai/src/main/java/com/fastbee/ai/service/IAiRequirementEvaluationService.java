package com.fastbee.ai.service;

import jakarta.servlet.http.HttpServletResponse;

import com.fastbee.ai.model.vo.AiModelRouteVO;
import com.fastbee.ai.model.vo.AiRequirementEvaluationResultVO;

/**
 * AI 需求评估服务。
 */
public interface IAiRequirementEvaluationService {

    /**
     * 根据上传需求文件正文生成初步评估结果。
     *
     * @param sourceFileName 源文件名称
     * @param contentType 内容类型
     * @param contentText 文件正文摘录
     * @param sourceFileBytes 源文件字节
     * @param requirement 用户评估要求
     * @param route 模型路由
     * @return 需求评估结果
     */
    AiRequirementEvaluationResultVO evaluateFromText(String sourceFileName,
                                                     String contentType,
                                                     String contentText,
                                                     byte[] sourceFileBytes,
                                                     String requirement,
                                                     AiModelRouteVO route);

    /**
     * 下载需求评估结果文件。
     *
     * @param artifactCode 产物编码
     * @param response 响应对象
     */
    void downloadReport(String artifactCode, HttpServletResponse response);
}
