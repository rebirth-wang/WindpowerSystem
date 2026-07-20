package com.fastbee.iot.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.SimulateLog;
import com.fastbee.iot.model.vo.SimulateLogVO;

/**
 * 模拟设备日志Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */
@Mapper
public interface SimulateLogConvert
{

    SimulateLogConvert INSTANCE = Mappers.getMapper(SimulateLogConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param simulateLog
     * @return 模拟设备日志集合
     */
    SimulateLogVO convertSimulateLogVO(SimulateLog simulateLog);

    /**
     * VO类转换为实体类集合
     *
     * @param simulateLogVO
     * @return 模拟设备日志集合
     */
    SimulateLog convertSimulateLog(SimulateLogVO simulateLogVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param simulateLogList
     * @return 模拟设备日志集合
     */
    List<SimulateLogVO> convertSimulateLogVOList(List<SimulateLog> simulateLogList);

    /**
     * VO类转换为实体类
     *
     * @param simulateLogVOList
     * @return 模拟设备日志集合
     */
    List<SimulateLog> convertSimulateLogList(List<SimulateLogVO> simulateLogVOList);
}
