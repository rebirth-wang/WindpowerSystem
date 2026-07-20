package com.fastbee.scada.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fastbee.scada.domain.ScadaComponent;
import com.fastbee.scada.vo.ScadaComponentVO;

/**
 * 组件管理Service接口
 *
 * @author kerwincui
 * @date 2023-11-10
 */
public interface IScadaComponentService extends IService<ScadaComponent>
{
    /**
     * 查询组件管理
     *
     * @param scadaComponent 组件管理
     * @return 组件管理
     */
    public ScadaComponent selectScadaComponentById(ScadaComponent scadaComponent);

    /**
     * 查询组态组件列表
     *
     * @param scadaComponent 组态组件
     * @return 组态组件分页集合
     */
    Page<ScadaComponentVO> pageScadaComponentVO(ScadaComponent scadaComponent);

    /**
     * 查询组态组件列表
     *
     * @param scadaComponent 组态组件
     * @return 组态组件集合
     */
    List<ScadaComponentVO> listScadaComponentVO(ScadaComponent scadaComponent);

    /**
     * 新增组件管理
     *
     * @param scadaComponent 组件管理
     * @return 结果
     */
    public int insertScadaComponent(ScadaComponent scadaComponent);

    /**
     * 修改组件管理
     *
     * @param scadaComponentVO 组件管理
     * @return 结果
     */
    public int updateScadaComponent(ScadaComponentVO scadaComponentVO);

    /**
     * 批量删除组件管理
     *
     * @param ids 需要删除的组件管理主键集合
     * @return 结果
     */
    public int deleteScadaComponentByIds(Long[] ids);

    /**
     * 删除组件管理信息
     *
     * @param id 组件管理主键
     * @return 结果
     */
    public int deleteScadaComponentById(Long id);
}
