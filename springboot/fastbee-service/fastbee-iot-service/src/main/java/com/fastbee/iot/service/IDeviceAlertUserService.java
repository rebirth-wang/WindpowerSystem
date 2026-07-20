package com.fastbee.iot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.iot.domain.DeviceAlertUser;
import com.fastbee.iot.model.vo.DeviceAlertUserVO;

/**
 * 设备告警用户Service接口
 *
 * @author kerwincui
 * @date 2024-05-15
 */
public interface IDeviceAlertUserService extends IService<DeviceAlertUser>
{

    /**
     * 查询设备告警用户列表
     *
     * @param deviceAlertUser 设备告警用户
     * @return 设备告警用户集合
     */
    public Page<DeviceAlertUserVO> selectDeviceAlertUserList(DeviceAlertUser deviceAlertUser);

    /**
     * 新增设备告警用户
     *
     * @param deviceAlertUserVO 设备告警用户
     * @return 结果
     */
    public int insertDeviceAlertUser(DeviceAlertUserVO deviceAlertUserVO);

    /**
     * 删除设备告警用户
     * @param deviceId 设备id
     * @param: userId 用户id
     * @return int
     */
    int deleteByDeviceIdAndUserId(Long deviceId, Long userId);

    /**
     * 查询用户
     * @param sysUser 系统用户
     * @return java.util.List<com.fastbee.common.core.domain.entity.SysUser>
     */
    Page<SysUser> selectUserList(SysUser sysUser);
}
