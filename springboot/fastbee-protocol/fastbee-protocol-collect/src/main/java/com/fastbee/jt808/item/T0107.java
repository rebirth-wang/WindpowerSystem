package com.fastbee.jt808.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.fastbee.jt808.JT808Message;
import com.fastbee.protocol.base.annotation.Column;
import com.fastbee.protocol.base.annotation.Protocol;

@EqualsAndHashCode(callSuper = true)
@ToString
@Data
@Accessors(chain = true)
@Protocol(JT808.ATTRI_RESPONSE)
public class T0107 extends JT808Message {

    @Column(length = 2, desc = "终端类型")
    private int deviceType;
    @Column(length = 5, desc = "制造商ID,终端制造商编码")
    private String makerId;
    /** 由制造商自行定义,位数不足时,后补"0x00" */
    @Column(length = 8, desc = "终端属性", version = {-2})       //2011版本
    @Column(length = 20, desc = "终端型号", version = {-1, 0})  // 2013版本
    @Column(length = 30, desc = "终端型号", version = 1)        // 2019版本
    private String deviceModel;
    /** 由大写字母和数字组成,此终端ID由制造商自行定义,位数不足时,后补"0x00" */
    @Column(length = 7, desc = "终端ID", version = {-1, 0,-2})
    @Column(length = 30, desc = "终端ID", version = 1)
    private String deviceId;
    @Column(length = 10, charset = "HEX", desc = "终端SIM卡ICCID")
    private String iccid;
    @Column(lengthUnit = 1, desc = "硬件版本号")
    private String hardwareVersion;
    @Column(lengthUnit = 1, desc = "固件版本号")
    private String firmwareVersion;
    @Column(length = 1, desc = "GNSS模块属性")
    private int gnssAttribute;
    @Column(length = 1, desc = "通信模块属性")
    private int networkAttribute;

}
