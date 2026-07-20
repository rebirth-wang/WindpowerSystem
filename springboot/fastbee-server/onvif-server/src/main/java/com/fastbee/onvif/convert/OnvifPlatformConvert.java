package com.fastbee.onvif.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.onvif.domain.OnvifPlatform;
import com.fastbee.onvif.domain.vo.OnvifPlatformVO;

/**
 * onvif平台Convert转换类
 *
 * @author fastbee
 * @date 2026-01-06
 */
@Mapper
public interface OnvifPlatformConvert
{
    /** 代码生成区域 可直接覆盖**/
    OnvifPlatformConvert INSTANCE = Mappers.getMapper(OnvifPlatformConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param onvifPlatform
     * @return onvif平台集合
     */
    OnvifPlatformVO convertOnvifPlatformVO(OnvifPlatform onvifPlatform);

    /**
     * VO类转换为实体类集合
     *
     * @param onvifPlatformVO
     * @return onvif平台集合
     */
    OnvifPlatform convertOnvifPlatform(OnvifPlatformVO onvifPlatformVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param onvifPlatformList
     * @return onvif平台集合
     */
    List<OnvifPlatformVO> convertOnvifPlatformVOList(List<OnvifPlatform> onvifPlatformList);

    /**
     * VO类转换为实体类
     *
     * @param onvifPlatformVOList
     * @return onvif平台集合
     */
    List<OnvifPlatform> convertOnvifPlatformList(List<OnvifPlatformVO> onvifPlatformVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param onvifPlatformPage
     * @return onvif平台分页
     */
    Page<OnvifPlatformVO> convertOnvifPlatformVOPage(Page<OnvifPlatform> onvifPlatformPage);

    /**
     * VO类转换为实体类
     *
     * @param onvifPlatformVOPage
     * @return onvif平台分页
     */
    Page<OnvifPlatform> convertOnvifPlatformPage(Page<OnvifPlatformVO> onvifPlatformVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
