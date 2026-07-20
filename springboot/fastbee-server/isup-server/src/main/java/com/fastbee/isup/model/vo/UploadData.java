package com.fastbee.isup.model.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class UploadData implements Serializable {
    private static final long serialVersionUID = 7632941126425093560L;
    private String dataType;
    private String data;
}
