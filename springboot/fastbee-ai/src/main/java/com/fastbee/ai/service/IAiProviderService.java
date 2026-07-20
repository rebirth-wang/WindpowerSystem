package com.fastbee.ai.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.ai.domain.AiProvider;
import com.fastbee.ai.model.vo.AiProviderVO;

/**
 * AI 厂商服务接口。
 */
public interface IAiProviderService extends IService<AiProvider> {

    /**
     * 查询 AI 厂商详情。
     *
     * @param aiProvider 查询条件
     * @return AI 厂商
     */
    AiProvider selectAiProvider(AiProvider aiProvider);

    /**
     * 查询 AI 厂商展示详情。
     *
     * @param aiProvider 查询条件
     * @return AI 厂商展示对象
     */
    AiProviderVO selectAiProviderVO(AiProvider aiProvider);

    /**
     * 分页查询 AI 厂商。
     *
     * @param aiProvider 查询条件
     * @return 分页结果
     */
    Page<AiProvider> pageAiProvider(AiProvider aiProvider);

    /**
     * 分页查询 AI 厂商展示列表。
     *
     * @param aiProvider 查询条件
     * @return 分页结果
     */
    Page<AiProviderVO> pageAiProviderVO(AiProvider aiProvider);

    /**
     * 新增 AI 厂商。
     *
     * @param aiProvider 厂商信息
     * @return 影响行数
     */
    int insertAiProvider(AiProvider aiProvider);

    /**
     * 修改 AI 厂商。
     *
     * @param aiProvider 厂商信息
     * @return 影响行数
     */
    int updateAiProvider(AiProvider aiProvider);

    /**
     * 修改 AI 厂商状态。
     *
     * @param aiProvider 厂商信息
     * @return 影响行数
     */
    int updateAiProviderStatus(AiProvider aiProvider);

    /**
     * 删除 AI 厂商。
     *
     * @param providerIds 厂商 ID 集合
     * @return 影响行数
     */
    int deleteAiProviderByIds(Long[] providerIds);

    /**
     * 查询已启用的厂商列表。
     *
     * @return 厂商列表
     */
    List<AiProvider> listEnabledProviders();

    /**
     * 查询已启用的厂商展示列表。
     *
     * @return 厂商展示列表
     */
    List<AiProviderVO> listEnabledProviderVOs();
}
