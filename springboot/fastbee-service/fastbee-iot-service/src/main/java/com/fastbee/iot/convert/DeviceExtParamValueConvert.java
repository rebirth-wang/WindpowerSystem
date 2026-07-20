package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.DeviceExtParamValue;
import com.fastbee.iot.model.vo.DeviceExtParamValueVO;

/**
 * 设备扩展参数值Convert转换类
 *
 * @author fastbee
 * @date 2026-03-18
 */
@Mapper
public interface DeviceExtParamValueConvert
{
    /** 代码生成区域 可直接覆盖**/
    DeviceExtParamValueConvert INSTANCE = Mappers.getMapper(DeviceExtParamValueConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param deviceExtParamValue
     * @return 设备扩展参数值集合
     */
    DeviceExtParamValueVO convertDeviceExtParamValueVO(DeviceExtParamValue deviceExtParamValue);

    /**
     * VO类转换为实体类集合
     *
     * @param deviceExtParamValueVO
     * @return 设备扩展参数值集合
     */
    DeviceExtParamValue convertDeviceExtParamValue(DeviceExtParamValueVO deviceExtParamValueVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param deviceExtParamValueList
     * @return 设备扩展参数值集合
     */
    List<DeviceExtParamValueVO> convertDeviceExtParamValueVOList(List<DeviceExtParamValue> deviceExtParamValueList);

    /**
     * VO类转换为实体类
     *
     * @param deviceExtParamValueVOList
     * @return 设备扩展参数值集合
     */
    List<DeviceExtParamValue> convertDeviceExtParamValueList(List<DeviceExtParamValueVO> deviceExtParamValueVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param deviceExtParamValuePage
     * @return 设备扩展参数值分页
     */
    Page<DeviceExtParamValueVO> convertDeviceExtParamValueVOPage(Page<DeviceExtParamValue> deviceExtParamValuePage);

    /**
     * VO类转换为实体类
     *
     * @param deviceExtParamValueVOPage
     * @return 设备扩展参数值分页
     */
    Page<DeviceExtParamValue> convertDeviceExtParamValuePage(Page<DeviceExtParamValueVO> deviceExtParamValueVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
