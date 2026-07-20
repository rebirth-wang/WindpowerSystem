package com.fastbee.onvif.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.onvif.domain.OnvifDeviceChannel;
import com.fastbee.onvif.domain.vo.OnvifDeviceChannelVO;

/**
 * onvif设备通道Convert转换类
 *
 * @author fastbee
 * @date 2026-01-06
 */
@Mapper
public interface OnvifDeviceChannelConvert
{
    /** 代码生成区域 可直接覆盖**/
    OnvifDeviceChannelConvert INSTANCE = Mappers.getMapper(OnvifDeviceChannelConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param onvifDeviceChannel
     * @return onvif设备通道集合
     */
    OnvifDeviceChannelVO convertOnvifDeviceChannelVO(OnvifDeviceChannel onvifDeviceChannel);

    /**
     * VO类转换为实体类集合
     *
     * @param onvifDeviceChannelVO
     * @return onvif设备通道集合
     */
    OnvifDeviceChannel convertOnvifDeviceChannel(OnvifDeviceChannelVO onvifDeviceChannelVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param onvifDeviceChannelList
     * @return onvif设备通道集合
     */
    List<OnvifDeviceChannelVO> convertOnvifDeviceChannelVOList(List<OnvifDeviceChannel> onvifDeviceChannelList);

    /**
     * VO类转换为实体类
     *
     * @param onvifDeviceChannelVOList
     * @return onvif设备通道集合
     */
    List<OnvifDeviceChannel> convertOnvifDeviceChannelList(List<OnvifDeviceChannelVO> onvifDeviceChannelVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param onvifDeviceChannelPage
     * @return onvif设备通道分页
     */
    Page<OnvifDeviceChannelVO> convertOnvifDeviceChannelVOPage(Page<OnvifDeviceChannel> onvifDeviceChannelPage);

    /**
     * VO类转换为实体类
     *
     * @param onvifDeviceChannelVOPage
     * @return onvif设备通道分页
     */
    Page<OnvifDeviceChannel> convertOnvifDeviceChannelPage(Page<OnvifDeviceChannelVO> onvifDeviceChannelVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
