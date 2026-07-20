package com.fastbee.iot.model.vo;

import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * @author zzy
 * @date 2025年06月18日 16:27
 */
@Data
public class DataCenterExportVO {

    @Excel(name = "更新时间")
    private String ts;

    @Excel(name = "历史数据")
    private String identifyJson;
}
