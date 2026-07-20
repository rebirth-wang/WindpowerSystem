package com.fastbee.scada.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fastbee.scada.domain.ScadaEchart;
import com.fastbee.scada.vo.ScadaEchartVO;

import java.util.List;

/**
 * 图表管理Service接口
 *
 * @author kerwincui
 * @date 2023-11-10
 */
public interface IScadaEchartService extends IService<ScadaEchart>
{
    /**
     * 查询图表管理
     *
     * @param scadaEchart 图表管理
     * @return 图表管理
     */
    public ScadaEchart selectScadaEchartById(ScadaEchart scadaEchart);

    /**
     * 查询图管理列表
     *
     * @param scadaEchart 图管理
     * @return 图管理分页集合
     */
    Page<ScadaEchartVO> pageScadaEchartVO(ScadaEchart scadaEchart);

    /**
     * 查询图管理列表
     *
     * @param scadaEchart 图管理
     * @return 图管理集合
     */
    List<ScadaEchartVO> listScadaEchartVO(ScadaEchart scadaEchart);

    /**
     * 新增图表管理
     *
     * @param scadaEchartVO 图表管理
     * @return 结果
     */
    public int insertScadaEchart(ScadaEchartVO scadaEchartVO);

    /**
     * 修改图表管理
     *
     * @param scadaEchartVO 图表管理
     * @return 结果
     */
    public int updateScadaEchart(ScadaEchartVO scadaEchartVO);

    /**
     * 批量删除图表管理
     *
     * @param ids 需要删除的图表管理主键集合
     * @return 结果
     */
    public int deleteScadaEchartByIds(Long[] ids);

    /**
     * 删除图表管理信息
     *
     * @param id 图表管理主键
     * @return 结果
     */
    public int deleteScadaEchartById(Long id);
}
