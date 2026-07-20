package com.fastbee.isup.model.cb;

import java.util.List;

import lombok.Data;

@Data
public class Candidate {
    private Integer alarmId;
    private String blacklist_id;
    private String customFaceLibID;
    private List<HumanData> human_data;
    private String human_id;
    private ReserveField reserve_field;
    private Double similarity;
    private String listType;
    private List<Object> extendData;
    private String customHumanID;
    private String FDLibName;
    private Integer FDLibThreshold;
}
