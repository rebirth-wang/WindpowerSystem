package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.iot.domain.DeviceExtParamValue;
import com.fastbee.iot.model.vo.DeviceExtParamValueVO;
import com.fastbee.iot.model.vo.ProductExtParamVO;

/**
 * 设备扩展参数值Service接口
 *
 * @author fastbee
 * @date 2026-03-18
 */
public interface IDeviceExtParamValueService extends IService<DeviceExtParamValue>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询设备扩展参数值列表
     *
     * @param deviceExtParamValue 设备扩展参数值
     * @return 设备扩展参数值分页集合
     */
    Page<ProductExtParamVO> pageDeviceExtParamValueVO(DeviceExtParamValue deviceExtParamValue);

    /**
     * 查询设备扩展参数值列表
     *
     * @param deviceExtParamValue 设备扩展参数值
     * @return 设备扩展参数值集合
     */
    List<DeviceExtParamValueVO> listDeviceExtParamValueVO(DeviceExtParamValue deviceExtParamValue);

    /**
     * 查询设备扩展参数值
     *
     * @param id 主键
     * @return 设备扩展参数值
     */
     DeviceExtParamValue selectDeviceExtParamValueById(Long id);

    /**
     * 查询设备扩展参数值
     *
     * @param deviceExtParamValue 设备扩展参数值
     * @return 设备扩展参数值
     */
    DeviceExtParamValue queryByIdWithCache(DeviceExtParamValue deviceExtParamValue);

    /**
     * 新增设备扩展参数值
     *
     * @param deviceExtParamValue 设备扩展参数值
     * @return 是否新增成功
     */
    Boolean insertWithCache(DeviceExtParamValue deviceExtParamValue);

    /**
     * 修改设备扩展参数值
     *
     * @param deviceExtParamValue 设备扩展参数值
     * @return 是否修改成功
     */
    Boolean updateWithCache(DeviceExtParamValue deviceExtParamValue);

    /**
     * 校验并批量删除设备扩展参数值信息
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
