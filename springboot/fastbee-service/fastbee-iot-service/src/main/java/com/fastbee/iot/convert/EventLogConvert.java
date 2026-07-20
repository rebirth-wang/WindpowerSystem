package com.fastbee.iot.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.EventLog;
import com.fastbee.iot.model.vo.EventLogVO;

/**
 * 事件日志Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */
@Mapper
public interface EventLogConvert
{

    EventLogConvert INSTANCE = Mappers.getMapper(EventLogConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param eventLog
     * @return 事件日志集合
     */
    EventLogVO convertEventLogVO(EventLog eventLog);

    /**
     * VO类转换为实体类集合
     *
     * @param eventLogVO
     * @return 事件日志集合
     */
    EventLog convertEventLog(EventLogVO eventLogVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param eventLogList
     * @return 事件日志集合
     */
    List<EventLogVO> convertEventLogVOList(List<EventLog> eventLogList);

    /**
     * VO类转换为实体类
     *
     * @param eventLogVOList
     * @return 事件日志集合
     */
    List<EventLog> convertEventLogList(List<EventLogVO> eventLogVOList);
}
