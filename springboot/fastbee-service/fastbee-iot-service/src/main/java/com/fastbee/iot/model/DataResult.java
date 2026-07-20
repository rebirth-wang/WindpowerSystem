package com.fastbee.iot.model;

import java.util.Date;

import lombok.Data;

/**
 * @author gsb
 * @date 2023/4/6 11:57
 */
@Data
public class DataResult {

    private String id;
    /**值*/
    private String value;
    /**时间*/
    private Date  ts;
}
