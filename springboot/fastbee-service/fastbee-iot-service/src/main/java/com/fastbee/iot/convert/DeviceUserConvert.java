package com.fastbee.iot.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.DeviceUser;
import com.fastbee.iot.model.vo.DeviceUserVO;

/**
 * 设备用户Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */
@Mapper
public interface DeviceUserConvert
{

    DeviceUserConvert INSTANCE = Mappers.getMapper(DeviceUserConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param deviceUser
     * @return 设备用户集合
     */
    DeviceUserVO convertDeviceUserVO(DeviceUser deviceUser);

    /**
     * VO类转换为实体类集合
     *
     * @param deviceUserVO
     * @return 设备用户集合
     */
    DeviceUser convertDeviceUser(DeviceUserVO deviceUserVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param deviceUserList
     * @return 设备用户集合
     */
    List<DeviceUserVO> convertDeviceUserVOList(List<DeviceUser> deviceUserList);

    /**
     * VO类转换为实体类
     *
     * @param deviceUserVOList
     * @return 设备用户集合
     */
    List<DeviceUser> convertDeviceUserList(List<DeviceUserVO> deviceUserVOList);
}
