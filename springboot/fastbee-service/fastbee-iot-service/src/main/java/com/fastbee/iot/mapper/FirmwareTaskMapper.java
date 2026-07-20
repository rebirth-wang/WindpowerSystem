package com.fastbee.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import com.fastbee.iot.domain.FirmwareTask;

/**
 * 固件升级任务对象Mapper接口
 *
 * @author kerwincui
 * @date 2024-08-18
 */
public interface FirmwareTaskMapper extends BaseMapper<FirmwareTask>
{


    /**
     * 根据任务名称查询固件升级任务总数
     * @param firmwareId 固件id
     * @param taskName 固件升级任务名称
     * @return 固件升级任务
     */
    public int selectCountFirmwareTaskByTaskName(@Param("firmwareId") Long firmwareId, @Param("taskName") String taskName);
}
