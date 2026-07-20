package com.fastbee.iot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.ModbusConfig;

/**
 * modbus配置Mapper接口
 *
 * @author kerwincui
 * @date 2024-05-22
 */
public interface ModbusConfigMapper extends BaseMapperX<ModbusConfig>
{

    /**
     * 根据标识查询配置
     * @param productId
     * @param identifiers
     * @return
     */
    List<ModbusConfig> selectListByProductIdAndIdentifiers(@Param("productId") Long productId, @Param("identifiers") List<String> identifiers);
}
