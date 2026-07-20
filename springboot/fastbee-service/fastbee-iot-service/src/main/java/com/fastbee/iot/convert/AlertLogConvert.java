package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.AlertLog;
import com.fastbee.iot.model.vo.AlertLogVO;

/**
 * 设备告警日志Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-12
 */
@Mapper
public interface AlertLogConvert
{

    AlertLogConvert INSTANCE = Mappers.getMapper(AlertLogConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param alertLog
     * @return 设备告警日志集合
     */
    AlertLogVO convertAlertLogVO(AlertLog alertLog);

    /**
     * VO类转换为实体类集合
     *
     * @param alertLogVO
     * @return 设备告警日志集合
     */
    AlertLog convertAlertLog(AlertLogVO alertLogVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param alertLogList
     * @return 设备告警日志集合
     */
    List<AlertLogVO> convertAlertLogVOList(List<AlertLog> alertLogList);

    /**
     * VO类转换为实体类
     *
     * @param alertLogVOList
     * @return 设备告警日志集合
     */
    List<AlertLog> convertAlertLogList(List<AlertLogVO> alertLogVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param alertLogPage
     * @return 数据桥接分页
     */
    Page<AlertLogVO> convertAlertLogVOPage(Page<AlertLog> alertLogPage);

    /**
     * VO类转换为实体类
     *
     * @param alertLogVOPage
     * @return 数据桥接分页
     */
    Page<AlertLog> convertAlertLogPage(Page<AlertLogVO> alertLogVOPage);
}
