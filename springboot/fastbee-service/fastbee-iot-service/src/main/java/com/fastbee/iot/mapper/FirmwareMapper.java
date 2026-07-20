package com.fastbee.iot.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import com.fastbee.iot.domain.Firmware;

/**
 * 产品固件Mapper接口
 *
 * @author kerwincui
 * @date 2024-08-18
 */
public interface FirmwareMapper extends BaseMapper<Firmware>
{


    /**
     * 查询设备最新固件
     * @param deviceId 产品固件主键
     * @param firmwareType 固件类型
     * @return 产品固件
     */
    IPage<Firmware> selectLatestFirmware(IPage<Firmware> page, @Param("deviceId") Long deviceId, @Param("firmwareType") Long firmwareType);


    /**
     * 查询待升级固件版本列表
     * @param firmware
     * @return
     */
    List<Firmware> selectUpGradeVersionList(Firmware firmware);
}
