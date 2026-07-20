package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.WorkOrder;
import com.fastbee.iot.model.vo.WorkOrderVO;

/**
 * 工单管理Convert转换类
 *
 * @author fastbee
 * @date 2025-08-18
 */
@Mapper
public interface WorkOrderConvert
{

    WorkOrderConvert INSTANCE = Mappers.getMapper(WorkOrderConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param workOrder
     * @return 工单管理集合
     */
    WorkOrderVO convertWorkOrderVO(WorkOrder workOrder);

    /**
     * VO类转换为实体类集合
     *
     * @param workOrderVO
     * @return 工单管理集合
     */
    WorkOrder convertWorkOrder(WorkOrderVO workOrderVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param workOrderList
     * @return 工单管理集合
     */
    List<WorkOrderVO> convertWorkOrderVOList(List<WorkOrder> workOrderList);

    /**
     * VO类转换为实体类
     *
     * @param workOrderVOList
     * @return 工单管理集合
     */
    List<WorkOrder> convertWorkOrderList(List<WorkOrderVO> workOrderVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param workOrderPage
     * @return 工单管理分页
     */
    Page<WorkOrderVO> convertWorkOrderVOPage(Page<WorkOrder> workOrderPage);

    /**
     * VO类转换为实体类
     *
     * @param workOrderVOPage
     * @return 工单管理分页
     */
    Page<WorkOrder> convertWorkOrderPage(Page<WorkOrderVO> workOrderVOPage);

}
