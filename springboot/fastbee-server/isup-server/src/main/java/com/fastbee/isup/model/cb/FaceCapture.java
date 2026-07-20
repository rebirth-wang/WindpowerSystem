package com.fastbee.isup.model.cb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceCapture {
    private String faceTime;
    private TargetAttrs targetAttrs;
    private List<Face> faces;
    private String uid;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class TargetAttrs {
    private String deviceName;
    private Integer deviceChannel;
    private String deviceId;
    private String faceTime;
    private String bkgUrl;
    private String contentID;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Face {
    private Integer faceId;
    private Object faceRect;
    private Age age;
    private Gender gender;
    private Glass glass;
    private String contentID;
    @JsonProperty("URL")
    private String url;
    private Integer stayDuration;
    private Integer faceScore;
    private Boolean captureEndMark;
    private Object FacePictureRect;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Age {
    private Integer range;
    private Integer value;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Gender {
    private String value;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Glass {
    private String value;
}
