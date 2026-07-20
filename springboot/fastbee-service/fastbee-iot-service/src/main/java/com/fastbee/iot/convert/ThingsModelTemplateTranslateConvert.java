package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.ThingsModelTemplateTranslate;
import com.fastbee.iot.model.vo.ThingsModelTemplateTranslateVO;

/**
 * 物模型模板翻译Convert转换类
 *
 * @author fastbee
 * @date 2025-12-26
 */
@Mapper
public interface ThingsModelTemplateTranslateConvert
{
    /** 代码生成区域 可直接覆盖**/
    ThingsModelTemplateTranslateConvert INSTANCE = Mappers.getMapper(ThingsModelTemplateTranslateConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param thingsModelTemplateTranslate
     * @return 物模型模板翻译集合
     */
    ThingsModelTemplateTranslateVO convertThingsModelTemplateTranslateVO(ThingsModelTemplateTranslate thingsModelTemplateTranslate);

    /**
     * VO类转换为实体类集合
     *
     * @param thingsModelTemplateTranslateVO
     * @return 物模型模板翻译集合
     */
    ThingsModelTemplateTranslate convertThingsModelTemplateTranslate(ThingsModelTemplateTranslateVO thingsModelTemplateTranslateVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param thingsModelTemplateTranslateList
     * @return 物模型模板翻译集合
     */
    List<ThingsModelTemplateTranslateVO> convertThingsModelTemplateTranslateVOList(List<ThingsModelTemplateTranslate> thingsModelTemplateTranslateList);

    /**
     * VO类转换为实体类
     *
     * @param thingsModelTemplateTranslateVOList
     * @return 物模型模板翻译集合
     */
    List<ThingsModelTemplateTranslate> convertThingsModelTemplateTranslateList(List<ThingsModelTemplateTranslateVO> thingsModelTemplateTranslateVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param thingsModelTemplateTranslatePage
     * @return 物模型模板翻译分页
     */
    Page<ThingsModelTemplateTranslateVO> convertThingsModelTemplateTranslateVOPage(Page<ThingsModelTemplateTranslate> thingsModelTemplateTranslatePage);

    /**
     * VO类转换为实体类
     *
     * @param thingsModelTemplateTranslateVOPage
     * @return 物模型模板翻译分页
     */
    Page<ThingsModelTemplateTranslate> convertThingsModelTemplateTranslatePage(Page<ThingsModelTemplateTranslateVO> thingsModelTemplateTranslateVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
