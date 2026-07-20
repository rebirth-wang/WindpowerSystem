package com.fastbee.ai.service;

import jakarta.servlet.http.HttpServletResponse;

import com.fastbee.ai.model.vo.AiModelRouteVO;
import com.fastbee.ai.model.vo.AiThingModelGenerateResultVO;

/**
 * AI 物模型生成服务。
 */
public interface IAiThingModelGenerateService {

    /**
     * 根据上传文件抽取文本生成物模型导入模板。
     *
     * @param sourceFileName 源文件名称
     * @param contentType 内容类型
     * @param contentText 文件正文摘录
     * @param requirement 用户生成要求
     * @param route 模型路由
     * @return 物模型生成结果
     */
    AiThingModelGenerateResultVO generateFromText(String sourceFileName,
                                                  String contentType,
                                                  String contentText,
                                                  String requirement,
                                                  AiModelRouteVO route);

    /**
     * 下载生成的物模型导入模板。
     *
     * @param artifactCode 产物编码
     * @param response HTTP 响应
     */
    void downloadWorkbook(String artifactCode, HttpServletResponse response);
}
