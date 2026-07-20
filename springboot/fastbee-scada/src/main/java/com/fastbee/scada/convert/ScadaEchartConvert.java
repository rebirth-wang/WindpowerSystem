package com.fastbee.scada.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fastbee.scada.domain.ScadaEchart;
import com.fastbee.scada.vo.ScadaEchartVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 图管理Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-04
 */
@Mapper
public interface ScadaEchartConvert
{

    ScadaEchartConvert INSTANCE = Mappers.getMapper(ScadaEchartConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param scadaEchart
     * @return 图管理集合
     */
    ScadaEchartVO convertScadaEchartVO(ScadaEchart scadaEchart);

    /**
     * VO类转换为实体类集合
     *
     * @param scadaEchartVO
     * @return 图管理集合
     */
    ScadaEchart convertScadaEchart(ScadaEchartVO scadaEchartVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param scadaEchartList
     * @return 图管理集合
     */
    List<ScadaEchartVO> convertScadaEchartVOList(List<ScadaEchart> scadaEchartList);

    /**
     * VO类转换为实体类
     *
     * @param scadaEchartVOList
     * @return 图管理集合
     */
    List<ScadaEchart> convertScadaEchartList(List<ScadaEchartVO> scadaEchartVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param scadaEchartPage
     * @return 图管理分页
     */
    Page<ScadaEchartVO> convertScadaEchartVOPage(Page<ScadaEchart> scadaEchartPage);

    /**
     * VO类转换为实体类
     *
     * @param scadaEchartVOPage
     * @return 图管理分页
     */
    Page<ScadaEchart> convertScadaEchartPage(Page<ScadaEchartVO> scadaEchartVOPage);
}
