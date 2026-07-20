package com.fastbee.scada.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fastbee.scada.domain.Scada;
import com.fastbee.scada.vo.ScadaVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 组态页面Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-04
 */
@Mapper
public interface ScadaConvert
{

    ScadaConvert INSTANCE = Mappers.getMapper(ScadaConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param scada
     * @return 组态页面集合
     */
    ScadaVO convertScadaVO(Scada scada);

    /**
     * VO类转换为实体类集合
     *
     * @param scadaVO
     * @return 组态页面集合
     */
    Scada convertScada(ScadaVO scadaVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param scadaList
     * @return 组态页面集合
     */
    List<ScadaVO> convertScadaVOList(List<Scada> scadaList);

    /**
     * VO类转换为实体类
     *
     * @param scadaVOList
     * @return 组态页面集合
     */
    List<Scada> convertScadaList(List<ScadaVO> scadaVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param scadaPage
     * @return 组态页面分页
     */
    Page<ScadaVO> convertScadaVOPage(Page<Scada> scadaPage);

    /**
     * VO类转换为实体类
     *
     * @param scadaVOPage
     * @return 组态页面分页
     */
    Page<Scada> convertScadaPage(Page<ScadaVO> scadaVOPage);
}
