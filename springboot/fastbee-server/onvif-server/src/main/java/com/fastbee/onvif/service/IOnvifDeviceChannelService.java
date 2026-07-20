package com.fastbee.onvif.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.fastbee.onvif.domain.OnvifDeviceChannel;
import com.fastbee.onvif.domain.vo.OnvifDeviceChannelVO;

/**
 * onvif设备通道Service接口
 *
 * @author fastbee
 * @date 2026-01-06
 */
public interface IOnvifDeviceChannelService
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询onvif设备通道列表
     *
     * @param onvifDeviceChannel onvif设备通道
     * @return onvif设备通道分页集合
     */
    Page<OnvifDeviceChannelVO> pageOnvifDeviceChannelVO(OnvifDeviceChannel onvifDeviceChannel);

    /**
     * 查询onvif设备通道列表
     *
     * @param onvifDeviceChannel onvif设备通道
     * @return onvif设备通道集合
     */
    List<OnvifDeviceChannelVO> listOnvifDeviceChannelVO(OnvifDeviceChannel onvifDeviceChannel);

    /**
     * 查询onvif设备通道
     *
     * @param id 主键
     * @return onvif设备通道
     */
     OnvifDeviceChannel selectOnvifDeviceChannelById(Integer id);

    /**
     * 查询onvif设备通道
     *
     * @param id 主键
     * @return onvif设备通道
     */
    OnvifDeviceChannel queryByIdWithCache(Integer id);

    /**
     * 新增onvif设备通道
     *
     * @param onvifDeviceChannel onvif设备通道
     * @return 是否新增成功
     */
    Boolean insertWithCache(OnvifDeviceChannel onvifDeviceChannel);

    /**
     * 修改onvif设备通道
     *
     * @param onvifDeviceChannel onvif设备通道
     * @return 是否修改成功
     */
    Boolean updateWithCache(OnvifDeviceChannel onvifDeviceChannel);

    /**
     * 校验并批量删除onvif设备通道信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/
    OnvifDeviceChannel getChannelByIpAndPort(String ip, Integer port);
    Boolean  updateByIpAndPort(OnvifDeviceChannel onvifDeviceChannel);

    Boolean clearByDeviceId(Integer deviceId);
    /** 自定义代码区域 END**/

}
