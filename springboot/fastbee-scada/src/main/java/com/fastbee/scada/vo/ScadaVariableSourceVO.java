package com.fastbee.scada.vo;

import lombok.Data;

/**
 * @author fastb
 * @version 1.0
 * @description: 场景变量来源VO
 * @date 2024-06-23 14:49
 */
@Data
public class ScadaVariableSourceVO {

    private String guid;

    private Long productId;

    private Long sceneModelId;
}
