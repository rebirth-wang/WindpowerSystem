package com.fastbee.iot.model.modbus;

import java.util.List;

import lombok.Data;

import com.fastbee.iot.domain.ModbusConfig;

/**
 * @author bill
 */
@Data
public class ModbusConfigVO {

    /**产品id*/
    private Long productId;
    /**modbus配置列表*/
    private List<ModbusConfig> configList;
    /**
     * 删除的id集合
     */
    private Long[] delIds;
}
