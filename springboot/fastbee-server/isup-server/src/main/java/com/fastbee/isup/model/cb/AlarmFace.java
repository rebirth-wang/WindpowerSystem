package com.fastbee.isup.model.cb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AlarmFace {
    private Integer faceId;
    private Object faceRect;
    private Object recommendFaceRect;
    private Object score;
    private String modelData;
    //private String contentID;
    private List<AlarmIdentify> identify;
    @JsonProperty("URL")
    private String url;
    private Integer stayDuration;
}
