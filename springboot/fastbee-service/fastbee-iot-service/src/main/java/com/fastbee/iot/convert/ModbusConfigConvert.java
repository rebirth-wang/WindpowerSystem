package com.fastbee.iot.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.ModbusConfig;
import com.fastbee.iot.model.vo.ModbusConfigVO;

/**
 * modbus配置Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */
@Mapper
public interface ModbusConfigConvert
{

    ModbusConfigConvert INSTANCE = Mappers.getMapper(ModbusConfigConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param modbusConfig
     * @return modbus配置集合
     */
    ModbusConfigVO convertModbusConfigVO(ModbusConfig modbusConfig);

    /**
     * VO类转换为实体类集合
     *
     * @param modbusConfigVO
     * @return modbus配置集合
     */
    ModbusConfig convertModbusConfig(ModbusConfigVO modbusConfigVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param modbusConfigList
     * @return modbus配置集合
     */
    List<ModbusConfigVO> convertModbusConfigVOList(List<ModbusConfig> modbusConfigList);

    /**
     * VO类转换为实体类
     *
     * @param modbusConfigVOList
     * @return modbus配置集合
     */
    List<ModbusConfig> convertModbusConfigList(List<ModbusConfigVO> modbusConfigVOList);
}
