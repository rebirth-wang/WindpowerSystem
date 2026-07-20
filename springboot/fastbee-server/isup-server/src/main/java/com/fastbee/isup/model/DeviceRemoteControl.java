package com.fastbee.isup.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class DeviceRemoteControl implements Serializable {
    private static final long serialVersionUID = -4118835736556424637L;

    private int isOnline;// 0:离线 1:在线
    private String lChannel;// 通道号
}
