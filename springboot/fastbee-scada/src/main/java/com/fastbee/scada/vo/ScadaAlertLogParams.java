package com.fastbee.scada.vo;

import com.fastbee.common.core.domain.PageEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zzy
 * @description: 查询设备告警参数
 * @date 2025-11-04 9:49
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ScadaAlertLogParams extends PageEntity {

    private String guid;

    private Integer scadaType;

    private Long sceneModelId;

    private String serialNumber;
}
