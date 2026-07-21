package com.fastbee.scada.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.scada.domain.ScadaModel;
import com.fastbee.scada.vo.ScadaModelVO;

/**
 * 三维配置Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-04
 */
@Mapper
public interface ScadaModelConvert
{

    ScadaModelConvert INSTANCE = Mappers.getMapper(ScadaModelConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param scadaModel
     * @return 三维配置集合
     */
    ScadaModelVO convertScadaModelVO(ScadaModel scadaModel);

    /**
     * VO类转换为实体类集合
     *
     * @param scadaModelVO
     * @return 三维配置集合
     */
    ScadaModel convertScadaModel(ScadaModelVO scadaModelVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param scadaModelList
     * @return 三维配置集合
     */
    List<ScadaModelVO> convertScadaModelVOList(List<ScadaModel> scadaModelList);

    /**
     * VO类转换为实体类
     *
     * @param scadaModelVOList
     * @return 三维配置集合
     */
    List<ScadaModel> convertScadaModelList(List<ScadaModelVO> scadaModelVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param scadaModelPage
     * @return 三维配置分页
     */
    Page<ScadaModelVO> convertScadaModelVOPage(Page<ScadaModel> scadaModelPage);

    /**
     * VO类转换为实体类
     *
     * @param scadaModelVOPage
     * @return 三维配置分页
     */
    Page<ScadaModel> convertScadaModelPage(Page<ScadaModelVO> scadaModelVOPage);
}
