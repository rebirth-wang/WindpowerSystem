package com.fastbee.isup.model.tts;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataItem implements Serializable {
    private static final long serialVersionUID = -3572278952302040034L;
    private String desc;
    private String text;
    private String voice;
    private String volume;
}
