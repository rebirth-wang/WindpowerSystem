package com.fastbee.ai.service;

import com.fastbee.ai.model.vo.AiModelRouteVO;
import com.fastbee.ai.model.vo.AiNl2SqlStructuredResultVO;

/**
 * AI 智能问数结构化生成服务接口。
 */
public interface IAiNl2SqlGenerateService {

    /**
     * 根据自然语言问题生成结构化问数结果。
     *
     * @param question 自然语言问题
     * @param route 模型路由
     * @return 结构化生成结果
     */
    AiNl2SqlStructuredResultVO generateStructured(String question, AiModelRouteVO route);
}
