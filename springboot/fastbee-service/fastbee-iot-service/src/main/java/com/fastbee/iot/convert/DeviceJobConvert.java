package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.DeviceJob;
import com.fastbee.iot.model.vo.DeviceJobVO;

/**
 * 设备定时Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2025-01-07
 */
@Mapper
public interface DeviceJobConvert
{
    /** 代码生成区域 可直接覆盖**/
    DeviceJobConvert INSTANCE = Mappers.getMapper(DeviceJobConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param deviceJob
     * @return 设备定时集合
     */
    DeviceJobVO convertDeviceJobVO(DeviceJob deviceJob);

    /**
     * VO类转换为实体类集合
     *
     * @param deviceJobVO
     * @return 设备定时集合
     */
    DeviceJob convertDeviceJob(DeviceJobVO deviceJobVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param deviceJobList
     * @return 设备定时集合
     */
    List<DeviceJobVO> convertDeviceJobVOList(List<DeviceJob> deviceJobList);

    /**
     * VO类转换为实体类
     *
     * @param deviceJobVOList
     * @return 设备定时集合
     */
    List<DeviceJob> convertDeviceJobList(List<DeviceJobVO> deviceJobVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param deviceJobPage
     * @return 设备定时分页
     */
    Page<DeviceJobVO> convertDeviceJobVOPage(Page<DeviceJob> deviceJobPage);

    /**
     * VO类转换为实体类
     *
     * @param deviceJobVOPage
     * @return 设备定时分页
     */
    Page<DeviceJob> convertDeviceJobPage(Page<DeviceJobVO> deviceJobVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
