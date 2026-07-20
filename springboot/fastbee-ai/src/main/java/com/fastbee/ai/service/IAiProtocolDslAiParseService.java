package com.fastbee.ai.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import com.fastbee.ai.domain.AiProtocolAdaptationTask;

/**
 * AI 协议 DSL 解析服务。
 */
public interface IAiProtocolDslAiParseService {

    /**
     * 将客户协议文档抽取结果解析为协议 DSL 候选。
     *
     * @param task 协议适配任务
     * @param sourceFileName 原始文件名
     * @param contentText 抽取后的正文文本
     * @param sourceTables 抽取后的表格片段
     * @return 协议 DSL 候选
     */
    JSONObject parseToDsl(AiProtocolAdaptationTask task, String sourceFileName, String contentText, JSONArray sourceTables);
}
