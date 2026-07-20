package com.fastbee.iot.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.DeviceShare;
import com.fastbee.iot.model.vo.DeviceShareVO;

/**
 * 设备分享Mapper接口
 *
 * @author kerwincui
 * @date 2024-04-03
 */
@Repository
public interface DeviceShareMapper extends BaseMapperX<DeviceShare>
{

    /**
     * 查询设备分享列表
     *
     * @param deviceShare 设备分享
     * @return 设备分享集合
     */
    public List<DeviceShareVO> selectDeviceShareList(@Param("deviceShare") DeviceShare deviceShare);
    public Page<DeviceShareVO> selectDeviceShareList(Page<DeviceShareVO> page, @Param("deviceShare") DeviceShare deviceShare);

    /**
     * 查询分享设备用户
     *
     * @param share 设备分享
     * @return 设备用户集合
     */
    public SysUser selectShareUser(DeviceShare share);
}
