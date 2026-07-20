package com.fastbee.ai.service;

import java.util.List;

import com.fastbee.ai.model.vo.AiSemanticContextVO;

/**
 * 问数提示词上下文装配器。
 */
public interface IAiQueryPromptContextAssembler {

    /**
     * 为关系库问数装配最小必要的提示词上下文。
     *
     * @param semanticContext 问数语义上下文
     * @return 提示词行
     */
    List<String> assembleNl2SqlPromptLines(AiSemanticContextVO semanticContext);
}
