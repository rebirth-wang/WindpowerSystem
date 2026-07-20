package com.fastbee.iot.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author bill
 */
@Data
@AllArgsConstructor
public class HistoryModel {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    private String value;

    private String identify;

    private String moderName;

    private String deviceName;

    private Long sceneModelDeviceId;

    private Integer operation;

    public HistoryModel() {}
}
