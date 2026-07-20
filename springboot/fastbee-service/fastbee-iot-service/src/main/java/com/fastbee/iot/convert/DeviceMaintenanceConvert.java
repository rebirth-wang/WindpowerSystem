package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.DeviceMaintenance;
import com.fastbee.iot.model.vo.DeviceMaintenanceVO;

/**
 * 设备维保Convert转换类
 *
 * @author fastbee
 * @date 2025-12-25
 */
@Mapper
public interface DeviceMaintenanceConvert
{
    /** 代码生成区域 可直接覆盖**/
    DeviceMaintenanceConvert INSTANCE = Mappers.getMapper(DeviceMaintenanceConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param deviceMaintenance
     * @return 设备维保集合
     */
    DeviceMaintenanceVO convertDeviceMaintenanceVO(DeviceMaintenance deviceMaintenance);

    /**
     * VO类转换为实体类集合
     *
     * @param deviceMaintenanceVO
     * @return 设备维保集合
     */
    DeviceMaintenance convertDeviceMaintenance(DeviceMaintenanceVO deviceMaintenanceVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param deviceMaintenanceList
     * @return 设备维保集合
     */
    List<DeviceMaintenanceVO> convertDeviceMaintenanceVOList(List<DeviceMaintenance> deviceMaintenanceList);

    /**
     * VO类转换为实体类
     *
     * @param deviceMaintenanceVOList
     * @return 设备维保集合
     */
    List<DeviceMaintenance> convertDeviceMaintenanceList(List<DeviceMaintenanceVO> deviceMaintenanceVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param deviceMaintenancePage
     * @return 设备维保分页
     */
    Page<DeviceMaintenanceVO> convertDeviceMaintenanceVOPage(Page<DeviceMaintenance> deviceMaintenancePage);

    /**
     * VO类转换为实体类
     *
     * @param deviceMaintenanceVOPage
     * @return 设备维保分页
     */
    Page<DeviceMaintenance> convertDeviceMaintenancePage(Page<DeviceMaintenanceVO> deviceMaintenanceVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
