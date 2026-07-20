package com.fastbee.isup.model.tts;

import java.util.List;

import lombok.Data;

@Data
public class TtsRequest {
    private List<DataItem> data;
}
