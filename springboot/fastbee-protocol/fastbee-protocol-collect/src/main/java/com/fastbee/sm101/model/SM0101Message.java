package com.fastbee.sm101.model;

import java.util.Map;

import lombok.Data;

/**
 * SM0101协议类
 * @author gsb
 * @date 2025/12/09 10:37
 */
@Data
public class SM0101Message {

    // ============ 帧头部分 ============
    private byte[] frameHeader = new byte[]{0x53, 0x4D, 0x01, 0x01};

    private long frameLength;

    private long deviceAddress;

    private short controlCode;

    // 控制字（4字节）
    private int controlWord;

    private short commandCode;

    private short frameCounter;

    private short extension1;

    // ============ 变长数据部分 ============
    private byte[] variableData;

    // ============ 校验和帧尾 ============
    private long crc32;

    private byte[] frameTail = new byte[]{0x4E, 0x44};

    // ============ 登录/心跳指令数据结构 ============
    // 用于解析0x00指令的数据
    private String imei;
    private Short signalStrength;
    private String iccid;
    private String customSerial;
    private Integer statusRegister;
    private Integer pressure;
    private Integer flowSample;
    private Integer emptyTubeSample;
    private Integer excitationSample;
    private Short unitCode;
    private Short decimalPlaces;
    private Float flowVelocity;
    private Float instantFlow;
    private Long forwardTotal;
    private Long reverseTotal;
    private Long totalFlow;

    private Map<String, Object> extraProperties;

    // ============ 确认/否认指令数据结构 ============
    // 用于解析0x01指令的数据
    private Short processResult;
    private String serverTime;

    // ============ 辅助方法 ============
    public int getDataLength() {
        return variableData != null ? variableData.length : 0;
    }

    /**
     * 获取状态寄存器各标志位
     */
    public StatusFlags getStatusFlags() {
        return new StatusFlags(statusRegister);
    }

    /**
     * 状态标志位内部类
     */
    @Data
    public static class StatusFlags {
        private int lowerLimitAlarm;  // B0: 下限告警
        private int upperLimitAlarm;  // B1: 上限告警
        private int calibrationStatus; // B2: 标定状态
        private int lowBattery;       // B3: 低电量
        private int emptyTubeAlarm;   // B4: 空管告警
        private int excitationAlarm;  // B5: 励磁告警

        public StatusFlags(Integer statusRegister) {
            if (statusRegister != null) {
                int value = statusRegister;
                this.lowerLimitAlarm = (value & 0x01) ;
                this.upperLimitAlarm = (value & 0x02);
                this.calibrationStatus = (value & 0x04);
                this.lowBattery = (value & 0x08);
                this.emptyTubeAlarm = (value & 0x10);
                this.excitationAlarm = (value & 0x20);
            }
        }
    }

    /**
     * 单位代码解析
     */
    @Data
    public static class UnitInfo {
        private String totalUnit;      // 累计单位
        private String instantUnit;    // 瞬时单位

        public UnitInfo(Short unitCode) {
            if (unitCode != null) {
                int code = unitCode & 0xFFFF;
                int highByte = (code >> 8) & 0xFF;  // 累计单位
                int lowByte = code & 0xFF;          // 瞬时单位

                this.totalUnit = getTotalUnitName(highByte);
                this.instantUnit = getInstantUnitName(lowByte);
            }
        }

        private String getTotalUnitName(int code) {
            switch (code) {
                case 0: return "L";
                case 1: return "m³";
                case 2: return "Kg";
                case 3: return "T";
                case 4: return "Gal(英制)";
                case 5: return "Gal(美制)";
                default: return "未知";
            }
        }

        private String getInstantUnitName(int code) {
            switch (code) {
                case 0: return "L/H";
                case 1: return "L/M";
                case 2: return "L/S";
                case 3: return "M³/H";
                case 4: return "M³/M";
                case 5: return "M³/S";
                case 6: return "Kg/H";
                case 7: return "Kg/M";
                case 8: return "Kg/S";
                case 9: return "T/H";
                case 10: return "T/M";
                case 11: return "T/S";
                case 12: return "Gal/H(英制)";
                case 13: return "Gal/M(英制)";
                case 14: return "Gal/S(英制)";
                case 15: return "Gal/H(美制)";
                case 16: return "Gal/M(美制)";
                case 17: return "Gal/S(美制)";
                default: return "未知";
            }
        }
    }
}
