package com.fastbee.onvif.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.fastbee.onvif.domain.OnvifDevice;
import com.fastbee.onvif.domain.vo.OnvifDeviceVO;

/**
 * onvif设备Service接口
 *
 * @author fastbee
 * @date 2026-01-06
 */
public interface IOnvifDeviceService
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询onvif设备列表
     *
     * @param onvifDevice onvif设备
     * @return onvif设备分页集合
     */
    Page<OnvifDeviceVO> pageOnvifDeviceVO(OnvifDevice onvifDevice);

    /**
     * 查询onvif设备列表
     *
     * @param onvifDevice onvif设备
     * @return onvif设备集合
     */
    List<OnvifDeviceVO> listOnvifDeviceVO(OnvifDevice onvifDevice);

    /**
     * 查询onvif设备
     *
     * @param id 主键
     * @return onvif设备
     */
     OnvifDevice selectOnvifDeviceById(Integer id);

    /**
     * 查询onvif设备
     *
     * @param id 主键
     * @return onvif设备
     */
    OnvifDevice queryByIdWithCache(Integer id);

    /**
     * 新增onvif设备
     *
     * @param onvifDevice onvif设备
     * @return 是否新增成功
     */
    Boolean insertWithCache(OnvifDevice onvifDevice);

    /**
     * 修改onvif设备
     *
     * @param onvifDevice onvif设备
     * @return 是否修改成功
     */
    Boolean updateWithCache(OnvifDevice onvifDevice);

    /**
     * 校验并批量删除onvif设备信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/

}
