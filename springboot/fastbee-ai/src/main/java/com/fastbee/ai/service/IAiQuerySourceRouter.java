package com.fastbee.ai.service;

import com.fastbee.ai.model.vo.AiQueryRouteVO;
import com.fastbee.ai.model.vo.AiSemanticContextVO;

/**
 * AI 问数执行源路由器。
 */
public interface IAiQuerySourceRouter {

    /**
     * 根据问题与语义上下文决定执行路由。
     *
     * @param question 用户问题
     * @param semanticContext 语义上下文
     * @return 路由结果
     */
    AiQueryRouteVO route(String question, AiSemanticContextVO semanticContext);
}
