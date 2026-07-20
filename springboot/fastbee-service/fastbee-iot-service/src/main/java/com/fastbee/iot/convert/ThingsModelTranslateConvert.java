package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.ThingsModelTranslate;
import com.fastbee.iot.model.vo.ThingsModelTranslateVO;

/**
 * 物模型翻译Convert转换类
 *
 * @author fastbee
 * @date 2025-12-26
 */
@Mapper
public interface ThingsModelTranslateConvert
{
    /** 代码生成区域 可直接覆盖**/
    ThingsModelTranslateConvert INSTANCE = Mappers.getMapper(ThingsModelTranslateConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param thingsModelTranslate
     * @return 物模型翻译集合
     */
    ThingsModelTranslateVO convertThingsModelTranslateVO(ThingsModelTranslate thingsModelTranslate);

    /**
     * VO类转换为实体类集合
     *
     * @param thingsModelTranslateVO
     * @return 物模型翻译集合
     */
    ThingsModelTranslate convertThingsModelTranslate(ThingsModelTranslateVO thingsModelTranslateVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param thingsModelTranslateList
     * @return 物模型翻译集合
     */
    List<ThingsModelTranslateVO> convertThingsModelTranslateVOList(List<ThingsModelTranslate> thingsModelTranslateList);

    /**
     * VO类转换为实体类
     *
     * @param thingsModelTranslateVOList
     * @return 物模型翻译集合
     */
    List<ThingsModelTranslate> convertThingsModelTranslateList(List<ThingsModelTranslateVO> thingsModelTranslateVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param thingsModelTranslatePage
     * @return 物模型翻译分页
     */
    Page<ThingsModelTranslateVO> convertThingsModelTranslateVOPage(Page<ThingsModelTranslate> thingsModelTranslatePage);

    /**
     * VO类转换为实体类
     *
     * @param thingsModelTranslateVOPage
     * @return 物模型翻译分页
     */
    Page<ThingsModelTranslate> convertThingsModelTranslatePage(Page<ThingsModelTranslateVO> thingsModelTranslateVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
