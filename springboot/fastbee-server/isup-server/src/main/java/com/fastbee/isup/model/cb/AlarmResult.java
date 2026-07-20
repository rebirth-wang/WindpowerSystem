package com.fastbee.isup.model.cb;

import java.util.List;

import lombok.Data;

@Data
public class AlarmResult {
    private String modelData;
    private Integer errorCode;
    private String errorMsg;
    private AlarmTargetAttrs targetAttrs;
    private List<AlarmFace> faces;
}

@Data
class AlarmTargetAttrs {
    private String deviceId;
    private String deviceChannel;
    private String deviceName;
    private String faceTime;
    private Object rect;
    private String bkgUrl;
    private String contentID;
}

@Data
class HumanData {
    private Double similarity;
    private String contentID;
    private String face_id;
    private String face_picurl;
    private String bkg_picurl;
}

