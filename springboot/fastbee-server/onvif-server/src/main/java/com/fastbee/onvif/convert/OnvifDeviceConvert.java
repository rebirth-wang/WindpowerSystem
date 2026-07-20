package com.fastbee.onvif.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.onvif.domain.OnvifDevice;
import com.fastbee.onvif.domain.vo.OnvifDeviceVO;

/**
 * onvif设备Convert转换类
 *
 * @author fastbee
 * @date 2026-01-06
 */
@Mapper
public interface OnvifDeviceConvert
{
    /** 代码生成区域 可直接覆盖**/
    OnvifDeviceConvert INSTANCE = Mappers.getMapper(OnvifDeviceConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param onvifDevice
     * @return onvif设备集合
     */
    OnvifDeviceVO convertOnvifDeviceVO(OnvifDevice onvifDevice);

    /**
     * VO类转换为实体类集合
     *
     * @param onvifDeviceVO
     * @return onvif设备集合
     */
    OnvifDevice convertOnvifDevice(OnvifDeviceVO onvifDeviceVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param onvifDeviceList
     * @return onvif设备集合
     */
    List<OnvifDeviceVO> convertOnvifDeviceVOList(List<OnvifDevice> onvifDeviceList);

    /**
     * VO类转换为实体类
     *
     * @param onvifDeviceVOList
     * @return onvif设备集合
     */
    List<OnvifDevice> convertOnvifDeviceList(List<OnvifDeviceVO> onvifDeviceVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param onvifDevicePage
     * @return onvif设备分页
     */
    Page<OnvifDeviceVO> convertOnvifDeviceVOPage(Page<OnvifDevice> onvifDevicePage);

    /**
     * VO类转换为实体类
     *
     * @param onvifDeviceVOPage
     * @return onvif设备分页
     */
    Page<OnvifDevice> convertOnvifDevicePage(Page<OnvifDeviceVO> onvifDeviceVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
