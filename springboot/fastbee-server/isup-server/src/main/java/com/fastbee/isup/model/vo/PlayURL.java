package com.fastbee.isup.model.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class PlayURL implements Serializable {
    private static final long serialVersionUID = 3992050009557341065L;
    private String wsFlv;
    private String rtmp;
    private String httpFlv;
    private String httpHls;
}
