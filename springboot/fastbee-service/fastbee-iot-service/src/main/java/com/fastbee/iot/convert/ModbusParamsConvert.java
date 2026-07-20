package com.fastbee.iot.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.ModbusParams;
import com.fastbee.iot.model.vo.ModbusParamsVO;

/**
 * 产品modbus配置参数Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */
@Mapper
public interface ModbusParamsConvert
{

    ModbusParamsConvert INSTANCE = Mappers.getMapper(ModbusParamsConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param modbusParams
     * @return 产品modbus配置参数集合
     */
    ModbusParamsVO convertModbusParamsVO(ModbusParams modbusParams);

    /**
     * VO类转换为实体类集合
     *
     * @param modbusParamsVO
     * @return 产品modbus配置参数集合
     */
    ModbusParams convertModbusParams(ModbusParamsVO modbusParamsVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param modbusParamsList
     * @return 产品modbus配置参数集合
     */
    List<ModbusParamsVO> convertModbusParamsVOList(List<ModbusParams> modbusParamsList);

    /**
     * VO类转换为实体类
     *
     * @param modbusParamsVOList
     * @return 产品modbus配置参数集合
     */
    List<ModbusParams> convertModbusParamsList(List<ModbusParamsVO> modbusParamsVOList);
}
