package com.fastbee.iot.model.vo;

import java.util.List;

import lombok.Data;

/**
 * @author zzy
 * @description: TODO
 * @date 2025-07-30 15:33
 */
@Data
public class ReportAggregateHistoryVO {

    private List<List<String>> head; // 表头数据
    private List<List<Object>> data; // 表格数据
}
