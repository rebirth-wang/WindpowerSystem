package com.fastbee.scada.vo;

import java.util.List;

import lombok.Data;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2024-07-05 15:16
 */
@Data
public class SceneVariableVO {

    private Integer total;

    private List<ScadaVariableDataVO> list;

}
