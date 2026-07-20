package com.fastbee.iot.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.DeviceUser;
import com.fastbee.iot.model.vo.DeviceUserVO;

/**
 * 设备用户Mapper接口
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Repository
public interface DeviceUserMapper extends BaseMapperX<DeviceUser>
{
    /**
     * 查询设备用户
     *
     * @param deviceId 设备用户主键
     * @return 设备用户
     */
    public DeviceUserVO selectDeviceUserByDeviceId(Long deviceId);

    /**
     * 查询设备用户列表
     *
     * @param deviceUser 设备用户
     * @return 设备用户集合
     */
    public Page<DeviceUser> selectDeviceUserList(Page<DeviceUser> page, @Param("deviceUser") DeviceUser deviceUser);

    /**
     * 查询设备分享用户
     *
     * @param deviceUser 设备用户
     * @return 设备用户集合
     */
    public SysUser selectShareUser(DeviceUser deviceUser);

    /**
     * 修改设备用户
     *
     * @param deviceUser 设备用户
     * @return 结果
     */
    public int updateDeviceUser(DeviceUser deviceUser);

}
