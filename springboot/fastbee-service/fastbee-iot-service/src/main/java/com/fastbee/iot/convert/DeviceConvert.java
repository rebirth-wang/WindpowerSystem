package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.Device;
import com.fastbee.iot.model.DeviceAllShortOutput;
import com.fastbee.iot.model.DeviceShortOutput;
import com.fastbee.iot.model.vo.DeviceVO;

/**
 * 设备Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-24
 */
@Mapper
public interface DeviceConvert
{
    /** 代码生成区域 可直接覆盖**/
    DeviceConvert INSTANCE = Mappers.getMapper(DeviceConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param device
     * @return 设备集合
     */
    DeviceVO convertDeviceVO(Device device);

    /**
     * VO类转换为实体类集合
     *
     * @param deviceVO
     * @return 设备集合
     */
    Device convertDevice(DeviceVO deviceVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param deviceList
     * @return 设备集合
     */
    List<DeviceVO> convertDeviceVOList(List<Device> deviceList);

    /**
     * VO类转换为实体类
     *
     * @param deviceVOList
     * @return 设备集合
     */
    List<Device> convertDeviceList(List<DeviceVO> deviceVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param devicePage
     * @return 设备分页
     */
    Page<DeviceVO> convertDeviceVOPage(Page<Device> devicePage);

    /**
     * VO类转换为实体类
     *
     * @param deviceVOPage
     * @return 设备分页
     */
    Page<Device> convertDevicePage(Page<DeviceVO> deviceVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/
    List<DeviceAllShortOutput> convertDeviceShortList(List<Device> deviceList);

    DeviceShortOutput convertDeviceShortOutput(Device device1);

    /** 自定义代码区域 END**/
}
