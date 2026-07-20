package com.fastbee.iot.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.Firmware;
import com.fastbee.iot.model.vo.FirmwareVO;

/**
 * 产品固件Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */
@Mapper
public interface FirmwareConvert
{

    FirmwareConvert INSTANCE = Mappers.getMapper(FirmwareConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param firmware
     * @return 产品固件集合
     */
    FirmwareVO convertFirmwareVO(Firmware firmware);

    /**
     * VO类转换为实体类集合
     *
     * @param firmwareVO
     * @return 产品固件集合
     */
    Firmware convertFirmware(FirmwareVO firmwareVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param firmwareList
     * @return 产品固件集合
     */
    List<FirmwareVO> convertFirmwareVOList(List<Firmware> firmwareList);

    /**
     * VO类转换为实体类
     *
     * @param firmwareVOList
     * @return 产品固件集合
     */
    List<Firmware> convertFirmwareList(List<FirmwareVO> firmwareVOList);
}
