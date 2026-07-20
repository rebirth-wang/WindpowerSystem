package com.fastbee.iot.model.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author zzy
 * @description: TODO
 * @date 2025-07-30 15:29
 */
@Data
@AllArgsConstructor
public class ReportTimeRangeVO {

    private Date start;
    private Date end;
}
