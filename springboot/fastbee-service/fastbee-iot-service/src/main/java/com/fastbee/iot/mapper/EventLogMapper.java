package com.fastbee.iot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.EventLog;
import com.fastbee.iot.model.HistoryModel;
import com.fastbee.iot.model.vo.EventLogVO;

/**
 * 事件日志Mapper接口
 *
 * @author kerwincui
 * @date 2024-08-16
 */
public interface EventLogMapper extends BaseMapperX<EventLog>
{

    /**
     * 查询物模型历史数据
     * @param eventLogVO 事件日志
     * @return java.util.List<com.fastbee.iot.model.HistoryModel>
     */
    List<HistoryModel> listHistory(EventLogVO eventLogVO);

    Long selectEventLogCount(@Param("device") Device device);

}
