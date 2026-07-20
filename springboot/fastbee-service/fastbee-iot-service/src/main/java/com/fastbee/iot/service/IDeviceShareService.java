package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.iot.domain.DeviceShare;
import com.fastbee.iot.model.vo.DeviceShareVO;

/**
 * 设备分享Service接口
 *
 * @author kerwincui
 * @date 2024-04-03
 */
public interface IDeviceShareService extends IService<DeviceShare>
{

    /**
     * 根据用户id、设备Id获取分享详情
     * @param deviceId
     * @param userId
     * @return
     */
    DeviceShare selectDeviceShareByDeviceIdAndUserId(Long deviceId,Long userId);

    /**
     * 查询设备分享
     *
     * @param deviceId 设备分享主键
     * @return 设备分享
     */
    public List<DeviceShare> selectDeviceShareByDeviceId(Long deviceId);

    /**
     * 查询设备分享列表
     *
     * @param deviceShare 设备分享
     * @return 设备分享集合
     */
    Page<DeviceShareVO> selectDeviceShareVOList(DeviceShare deviceShare);

    /**
     * 新增设备分享
     *
     * @param deviceShare 设备分享
     * @return 结果
     */
    public int insertDeviceShare(DeviceShare deviceShare);

    /**
     * 修改设备分享
     *
     * @param deviceShare 设备分享
     * @return 结果
     */
    public int updateDeviceShare(DeviceShare deviceShare);

    /**
     * 批量删除设备分享
     *
     * @param deviceIds 需要删除的设备分享主键集合
     * @return 结果
     */
    public int deleteDeviceShareByDeviceIds(Long[] deviceIds);

    /**
     * 删除设备分享信息
     *
     * @param deviceId 设备分享主键
     * @return 结果
     */
    public int deleteDeviceShareByDeviceId(Long deviceId);


    /**
     * 查询分享设备用户
     *
     * @param share 设备分享
     * @return 设备用户集合
     */
    public SysUser selectShareUser(DeviceShare share);

    /**
     * 删除分享设备
     * @param deviceShare
     * @return
     */
    int deleteDeviceShareByDeviceIdAndUserId(DeviceShare deviceShare);
}
