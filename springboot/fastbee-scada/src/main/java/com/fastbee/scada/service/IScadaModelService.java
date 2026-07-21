package com.fastbee.scada.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.scada.domain.ScadaModel;
import com.fastbee.scada.vo.ScadaModelVO;

/**
 * 模型管理Service接口
 *
 * @author kerwincui
 * @date 2023-11-10
 */
public interface IScadaModelService extends IService<ScadaModel>
{
    /**
     * 查询模型管理
     *
     * @param scadaModel 模型管理
     * @return 模型管理
     */
    public ScadaModel selectScadaModelById(ScadaModel scadaModel);

    /**
     * 查询三维配置列表
     *
     * @param scadaModel 三维配置
     * @return 三维配置分页集合
     */
    Page<ScadaModelVO> pageScadaModelVO(ScadaModel scadaModel);

    /**
     * 查询三维配置列表
     *
     * @param scadaModel 三维配置
     * @return 三维配置集合
     */
    List<ScadaModelVO> listScadaModelVO(ScadaModel scadaModel);

    /**
     * 新增模型管理
     *
     * @param scadaModel 模型管理
     * @return 结果
     */
    public int insertScadaModel(ScadaModel scadaModel);

    /**
     * 修改模型管理
     *
     * @param scadaModel 模型管理
     * @return 结果
     */
    public int updateScadaModel(ScadaModel scadaModel);

    /**
     * 批量删除模型管理
     *
     * @param ids 需要删除的模型管理主键集合
     * @return 结果
     */
    public int deleteScadaModelByIds(Long[] ids);

    /**
     * 删除模型管理信息
     *
     * @param id 模型管理主键
     * @return 结果
     */
    public int deleteScadaModelById(Long id);
}
