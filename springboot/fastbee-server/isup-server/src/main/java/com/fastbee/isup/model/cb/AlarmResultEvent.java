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
public class AlarmResultEvent extends DeviceEventBase {
    private CapturePictureResolution capturePictureResolution;
    private List<AlarmResult> alarmResult;
    private String URLCertificationType;
    @JsonProperty("GPS")
    private GPSData gps;
}

@Data
class CapturePictureResolution {
    private Integer height;
    private Integer width;
}

