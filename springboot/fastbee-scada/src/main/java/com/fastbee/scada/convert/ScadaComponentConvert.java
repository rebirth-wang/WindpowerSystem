package com.fastbee.scada.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.scada.domain.ScadaComponent;
import com.fastbee.scada.vo.ScadaComponentVO;

/**
 * 组态组件Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-04
 */
@Mapper
public interface ScadaComponentConvert
{

    ScadaComponentConvert INSTANCE = Mappers.getMapper(ScadaComponentConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param scadaComponent
     * @return 组态组件集合
     */
    ScadaComponentVO convertScadaComponentVO(ScadaComponent scadaComponent);

    /**
     * VO类转换为实体类集合
     *
     * @param scadaComponentVO
     * @return 组态组件集合
     */
    ScadaComponent convertScadaComponent(ScadaComponentVO scadaComponentVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param scadaComponentList
     * @return 组态组件集合
     */
    List<ScadaComponentVO> convertScadaComponentVOList(List<ScadaComponent> scadaComponentList);

    /**
     * VO类转换为实体类
     *
     * @param scadaComponentVOList
     * @return 组态组件集合
     */
    List<ScadaComponent> convertScadaComponentList(List<ScadaComponentVO> scadaComponentVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param scadaComponentPage
     * @return 组态组件分页
     */
    Page<ScadaComponentVO> convertScadaComponentVOPage(Page<ScadaComponent> scadaComponentPage);

    /**
     * VO类转换为实体类
     *
     * @param scadaComponentVOPage
     * @return 组态组件分页
     */
    Page<ScadaComponent> convertScadaComponentPage(Page<ScadaComponentVO> scadaComponentVOPage);
}
