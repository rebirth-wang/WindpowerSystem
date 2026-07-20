package com.fastbee.ai.service;

import java.util.List;

import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.model.vo.AiSemanticFieldVO;
import com.fastbee.ai.model.vo.AiSemanticRuntimeBundleVO;

/**
 * AI 运行时语义存储策略。
 */
public interface IAiSemanticRuntimeStore {

    /**
     * 当前策略对应的存储类型。
     *
     * @return 存储类型
     */
    String getStoreType();

    /**
     * 是否支持当前存储类型。
     *
     * @param storeType 存储类型
     * @return 是否支持
     */
    default boolean supports(String storeType) {
        return getStoreType() != null && getStoreType().equalsIgnoreCase(storeType);
    }

    /**
     * 发布 NL2SQL 运行时语义包。
     *
     * @param knowledgeBase 知识库
     * @param version       版本
     * @param fields        语义字段
     */
    void publishNl2SqlBundle(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion version, List<AiSemanticFieldVO> fields);

    /**
     * 删除指定版本的运行时语义包。
     *
     * @param knowledgeBase 知识库
     * @param version       版本
     */
    void deleteNl2SqlVersion(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion version);

    /**
     * 将指定版本恢复为当前知识库的 NL2SQL 活跃运行时语义包。
     * 该操作优先复用已发布版本包，不重新生成历史版本内容。
     *
     * @param knowledgeBase 知识库
     * @param version       版本
     */
    default void restoreActiveNl2SqlBundle(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion version) {
        // 默认无操作，由具体存储实现按需覆盖。
    }

    /**
     * 清理当前知识库的 NL2SQL 活跃运行时语义包。
     * 该操作仅清理 active 指针，不删除历史版本包。
     *
     * @param knowledgeBase 知识库
     */
    default void clearActiveNl2SqlBundle(AiKnowledgeBase knowledgeBase) {
        // 默认无操作，由具体存储实现按需覆盖。
    }

    /**
     * 读取租户当前生效的 NL2SQL 语义包。
     *
     * @param tenantId 租户 ID
     * @return 语义包列表
     */
    List<AiSemanticRuntimeBundleVO> listActiveNl2SqlBundles(Long tenantId);
}
