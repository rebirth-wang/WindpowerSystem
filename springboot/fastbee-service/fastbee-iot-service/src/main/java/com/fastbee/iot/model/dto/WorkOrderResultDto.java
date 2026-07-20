package com.fastbee.iot.model.dto;

import java.util.List;

import lombok.Data;

/**
 * @author zzy
 * @description: 工单结单结果信息
 * @date 2025-08-28 16:34
 */
@Data
public class WorkOrderResultDto {

    private String description;

    private List<String> image;
}
