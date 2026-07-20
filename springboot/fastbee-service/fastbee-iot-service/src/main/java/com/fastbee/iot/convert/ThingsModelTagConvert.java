package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.ThingsModelTag;
import com.fastbee.iot.model.vo.ThingsModelTagVO;

/**
 * 物模型计算Convert转换类
 *
 * @author fastbee
 * @date 2025-06-24
 */
@Mapper
public interface ThingsModelTagConvert
{
    /** 代码生成区域 可直接覆盖**/
    ThingsModelTagConvert INSTANCE = Mappers.getMapper(ThingsModelTagConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param thingsModelTag
     * @return 物模型计算集合
     */
    ThingsModelTagVO convertThingsModelTagVO(ThingsModelTag thingsModelTag);

    /**
     * VO类转换为实体类集合
     *
     * @param thingsModelTagVO
     * @return 物模型计算集合
     */
    ThingsModelTag convertThingsModelTag(ThingsModelTagVO thingsModelTagVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param thingsModelTagList
     * @return 物模型计算集合
     */
    List<ThingsModelTagVO> convertThingsModelTagVOList(List<ThingsModelTag> thingsModelTagList);

    /**
     * VO类转换为实体类
     *
     * @param thingsModelTagVOList
     * @return 物模型计算集合
     */
    List<ThingsModelTag> convertThingsModelTagList(List<ThingsModelTagVO> thingsModelTagVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param thingsModelTagPage
     * @return 物模型计算分页
     */
    Page<ThingsModelTagVO> convertThingsModelTagVOPage(Page<ThingsModelTag> thingsModelTagPage);

    /**
     * VO类转换为实体类
     *
     * @param thingsModelTagVOPage
     * @return 物模型计算分页
     */
    Page<ThingsModelTag> convertThingsModelTagPage(Page<ThingsModelTagVO> thingsModelTagVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
