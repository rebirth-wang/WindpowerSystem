package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceMaintenance;
import com.fastbee.iot.domain.WorkOrder;
import com.fastbee.iot.model.vo.AlertSceneSendVO;
import com.fastbee.iot.model.vo.WorkOrderVO;

/**
 * 工单管理Service接口
 *
 * @author fastbee
 * @date 2025-08-18
 */
public interface IWorkOrderService extends IService<WorkOrder>
{

    /**
     * 查询工单管理列表
     *
     * @param workOrderVO 工单管理
     * @return 工单管理分页集合
     */
    Page<WorkOrderVO> pageWorkOrderVO(WorkOrderVO workOrderVO);

    /**
     * 查询工单管理列表
     *
     * @param workOrder 工单管理
     * @return 工单管理集合
     */
    List<WorkOrderVO> listWorkOrderVO(WorkOrder workOrder);

    /**
     * 查询工单管理
     *
     * @param id 主键
     * @return 工单管理
     */
     WorkOrder selectWorkOrderById(Long id);

    /**
     * 查询工单管理
     *
     * @param workOrder 工单
     * @return 工单管理
     */
    WorkOrder queryByIdWithCache(WorkOrder workOrder);

    /**
     * 新增工单管理
     *
     * @param workOrder 工单管理
     * @return 是否新增成功
     */
    WorkOrderVO insertWithCache(WorkOrder workOrder);

    /**
     * 修改工单管理
     *
     * @param workOrderVO 工单管理
     * @return 是否修改成功
     */
    WorkOrderVO updateWithCache(WorkOrderVO workOrderVO);

    /**
     * 校验并批量删除工单管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);

    WorkOrderVO changeStatus(WorkOrderVO workOrderVO);

    AjaxResult endUserAdd(WorkOrderVO workOrderVO);

    WorkOrder endUserQuery(Long id);


    /**
     * 设备维保生成工单
     *
     * @param deviceMaintenance 设备维护
     */
    WorkOrder generateDeviceMaintenanceWorkOrder(DeviceMaintenance deviceMaintenance);

    /**
     * 删除设备维保生成的工单
     *
     * @param deviceMaintenanceIds 朗斯
     */
    void removeDeviceMaintenanceWorkOrder(List<Long> deviceMaintenanceIds);

    /**
     * 设备告警生成工单
     *
     * @param alertSceneSendVO 警报场景发送vo
     * @param device           设备
     */
    void alertGenerateWorkOrder(AlertSceneSendVO alertSceneSendVO, Device device);
}
