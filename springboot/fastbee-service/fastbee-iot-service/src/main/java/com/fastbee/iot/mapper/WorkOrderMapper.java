package com.fastbee.iot.mapper;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.WorkOrder;
import com.fastbee.iot.model.vo.WorkOrderVO;

/**
 * 工单管理Mapper接口
 *
 * @author fastbee
 * @date 2025-08-18
 */
public interface WorkOrderMapper extends BaseMapperX<WorkOrder>
{

    WorkOrderVO selectVoById(Long id);
}
