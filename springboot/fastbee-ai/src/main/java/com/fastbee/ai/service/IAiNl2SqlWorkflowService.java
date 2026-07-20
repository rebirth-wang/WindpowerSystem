package com.fastbee.ai.service;

import com.fastbee.ai.model.vo.AiModelRouteVO;
import com.fastbee.ai.model.vo.AiNl2SqlGenerateResultVO;

/**
 * AI 智能问数工作流服务接口。
 */
public interface IAiNl2SqlWorkflowService {

    /**
     * 根据自然语言生成受控 SQL 并执行。
     *
     * @param question 自然语言问题
     * @param rowLimit 行数限制
     * @param route 模型路由
     * @return 生成与执行结果
     */
    AiNl2SqlGenerateResultVO generateAndExecute(String question, Integer rowLimit, AiModelRouteVO route);
}
