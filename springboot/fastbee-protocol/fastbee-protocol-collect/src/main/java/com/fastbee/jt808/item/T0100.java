package com.fastbee.jt808.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.fastbee.jt808.JT808Message;
import com.fastbee.protocol.base.annotation.Column;
import com.fastbee.protocol.base.annotation.Protocol;

/**
 * 终端注册
 */
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Protocol(JT808.REGISTRATION)
public class T0100 extends JT808Message {

    /** 设备安装车辆所在的省域,省域ID采用GB/T2260中规定的行政区划代码6位中前两位 */
    @Column(length = 2, desc = "省域ID")
    private int provinceId;
    /** 设备安装车辆所在的市域或县域,市县域ID采用GB/T2260中规定的行政区划代码6位中后四位 */
    @Column(length = 2, desc = "市县域ID")
    private int cityId;
    /** 终端制造商编码 */
    @Column(length = 5, desc = "制造商ID", version = {-1, 0})
    @Column(length = 11, desc = "制造商ID", version = 1)
    private String makerId;
    /** 由制造商自行定义,位数不足时,后补"0x00" */
    @Column(length = 8, desc = "终端型号", version = -1) // 2011
    @Column(length = 20, desc = "终端型号", version = 0) // 2013
    @Column(length = 30, desc = "终端型号", version = 1) // 2019
    private String deviceModel;
    /** 由大写字母和数字组成,此终端ID由制造商自行定义 */
    @Column(length = 7, desc = "终端ID", version = {-1, 0}) // 2011 &　2013
    @Column(length = 30, desc = "终端ID", version = 1)     // 2019　
    private String deviceId;
    /** 按照JT/T415-2006的5.4.12 */
    @Column(length = 1, desc = "车牌颜色：0.未上车牌 1.蓝色 2.黄色 3.黑色 4.白色 9.其他")
    private int plateColor;
    /** 车牌颜色为0时,表示车辆VIN 否则,表示公安交通管理部门颁发的机动车号牌 */
    @Column(desc = "车辆标识")
    private String plateNo;

    @Override
    public int getProtocolVersion() {
        int bodyLength = getBodyLength();
        if (bodyLength > 0 && bodyLength < 37)
            return -1;
        return super.getProtocolVersion();
    }
}
