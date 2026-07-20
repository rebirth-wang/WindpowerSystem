package com.fastbee.ai.service;

import java.util.List;

import com.fastbee.ai.model.vo.AiSemanticFieldVO;

/**
 * AI 运行时语义提供者。
 * <p>用于承接知识库快照之外的动态语义来源，例如物模型、缓存定义、
 * 运行时枚举等。这类语义先进入上下文辅助层，避免直接污染 NL2SQL 主提示词。</p>
 */
public interface IAiSemanticRuntimeProvider {

    /**
     * 提供者编码。
     *
     * @return 提供者编码
     */
    String getProviderCode();

    /**
     * 根据问题返回候选运行时语义。
     *
     * @param question 用户问题
     * @return 候选语义字段
     */
    List<AiSemanticFieldVO> listSemanticFields(String question);
}
