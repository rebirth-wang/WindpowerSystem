package com.fastbee.ai.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.ai.domain.AiModel;
import com.fastbee.ai.model.vo.AiModelVO;
import com.fastbee.ai.model.vo.AiProviderModelGroupVO;

/**
 * AI 模型服务接口。
 */
public interface IAiModelService extends IService<AiModel> {

    /**
     * 查询 AI 模型详情。
     *
     * @param aiModel 查询条件
     * @return AI 模型
     */
    AiModel selectAiModel(AiModel aiModel);

    /**
     * 查询 AI 模型展示详情。
     *
     * @param aiModel 查询条件
     * @return AI 模型展示对象
     */
    AiModelVO selectAiModelVO(AiModel aiModel);

    /**
     * 分页查询 AI 模型。
     *
     * @param aiModel 查询条件
     * @return 分页结果
     */
    Page<AiModel> pageAiModel(AiModel aiModel);

    /**
     * 分页查询 AI 模型展示列表。
     *
     * @param aiModel 查询条件
     * @return 分页结果
     */
    Page<AiModelVO> pageAiModelVO(AiModel aiModel);

    /**
     * 新增 AI 模型。
     *
     * @param aiModel 模型信息
     * @return 影响行数
     */
    int insertAiModel(AiModel aiModel);

    /**
     * 修改 AI 模型。
     *
     * @param aiModel 模型信息
     * @return 影响行数
     */
    int updateAiModel(AiModel aiModel);

    /**
     * 修改 AI 模型状态。
     *
     * @param aiModel 模型信息
     * @return 影响行数
     */
    int updateAiModelStatus(AiModel aiModel);

    /**
     * 删除 AI 模型。
     *
     * @param modelIds 模型 ID 集合
     * @return 影响行数
     */
    int deleteAiModelByIds(Long[] modelIds);

    /**
     * 按模型编码查询模型。
     *
     * @param modelCode 模型编码
     * @return 模型信息
     */
    AiModel selectByModelCode(String modelCode);

    /**
     * 查询模型分组选项。
     *
     * @return 厂商模型分组
     */
    List<AiProviderModelGroupVO> listGroupedOptions();
}
