package com.fastbee.scada.vo;

import lombok.Data;

/**
 * @author zzy
 * @description: TODO
 * @date 2025-07-11 15:15
 */
@Data
public class ScadaDateQueryVO {

    private String guid;

    private String serialNumber;

    private Boolean initializedData;

    private String platform;

    private String pageName;

    private Long productId;

}
