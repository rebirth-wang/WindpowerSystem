package com.fastbee.iot.service;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.iot.domain.DeviceMaintenance;
import com.fastbee.iot.model.vo.DeviceMaintenanceVO;

/**
 * 设备维保Service接口
 *
 * @author fastbee
 * @date 2025-12-25
 */
public interface IDeviceMaintenanceService extends IService<DeviceMaintenance>
{

    /**
     * 查询设备维保列表
     *
     * @param deviceMaintenance 设备维保
     * @return 设备维保分页集合
     */
    Page<DeviceMaintenanceVO> pageDeviceMaintenanceVO(DeviceMaintenance deviceMaintenance);

    /**
     * 查询设备维保列表
     *
     * @param deviceMaintenance 设备维保
     * @return 设备维保集合
     */
    List<DeviceMaintenanceVO> listDeviceMaintenanceVO(DeviceMaintenance deviceMaintenance);

    /**
     * 查询设备维保
     *
     * @param id 主键
     * @return 设备维保
     */
     DeviceMaintenance selectDeviceMaintenanceById(Long id);

    /**
     * 查询设备维保
     *
     * @param deviceMaintenance 设备维保
     * @return 设备维保
     */
    DeviceMaintenance queryByIdWithCache(DeviceMaintenance deviceMaintenance);

    /**
     * 新增设备维保
     *
     * @param deviceMaintenance 设备维保
     * @return 是否新增成功
     */
    AjaxResult insertWithCache(DeviceMaintenance deviceMaintenance);

    /**
     * 修改设备维保
     *
     * @param deviceMaintenance 设备维保
     * @return 是否修改成功
     */
    AjaxResult updateWithCache(DeviceMaintenance deviceMaintenance);

    /**
     * 校验并批量删除设备维保信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);

    /**
     * 查询所有开始维保的
     *
     * @return {@link List }<{@link DeviceMaintenance }>
     */
    List<DeviceMaintenance> selectAllStratList();

    /**
     * 按id更新状态
     *
     * @param updateIdList 更新ID列表
     * @param status       地位
     */
    void updateStatusByIds(List<Long> updateIdList, Integer status);

    /**
     * 计算下次维护时间
     *
     * @param add  添加
     * @param time 时间
     * @return {@link Date }
     */
    Date calculateNextMaintenanceTime(DeviceMaintenance add, Date time);
}
