package com.fastbee.isup.model.cb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FaceCaptureEvent extends DeviceEventBase {
    private CapturePictureResolution faceBackgroundImageResolution;
    private List<FaceCapture> faceCapture;
    private String URLCertificationType;
    @JsonProperty("GPS")
    private GPSData gps; // 有时 faceCapture 事件也带 GPS
}

