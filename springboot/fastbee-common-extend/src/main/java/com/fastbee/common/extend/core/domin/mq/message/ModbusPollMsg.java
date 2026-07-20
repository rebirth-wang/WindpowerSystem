package com.fastbee.common.extend.core.domin.mq.message;

import java.util.List;

import lombok.Data;

/**
 * @author gsb
 * @date 2024/6/20 10:53
 */
@Data
public class ModbusPollMsg {

    /**
     * 下发指令
     */
    private List<String> commandList;
    /**
     * 服务端类型
     */
    private Integer serverType;
    /**
     * 产品id
     */
    private Long productId;
    /**
     * 设备编码
     */
    private String serialNumber;

    private String transport;
}
