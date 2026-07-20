package com.fastbee.sm101.test;

import io.netty.buffer.Unpooled;
import org.apache.commons.codec.binary.Hex;

import com.fastbee.common.extend.core.domin.mq.message.DeviceData;
import com.fastbee.sm101.dcode.SM0101Decoder;
import com.fastbee.sm101.dcode.SM0101Encoder;
import com.fastbee.sm101.model.SM0101Message;

/**
 * SM0101协议测试工具类
 */
public class SM0101ProtocolTesterCRC32Fixed {

    // 修正后的测试用报文常量
    private static final String LOGIN_HEX =
            "534D0101" +           // 帧头: SM 0x01 0x01
                    "66000000" +           // 帧长: 102字节
                    "C2F7A4AA" +           // 地址: 0xC2F7A4AA
                    "C0000000" +           // 控制码=0xC0, 指令码=0x00, 帧计数=0x00, 扩展1=0x00
                    "383637393937303733373232343134" +  // IMEI: "867997073722414"
                    "57" +                 // CSQ: 0x57 = 87
                    "3839383630343035313932323930303332373339" +  // ICCID: "89860405192290032739"
                    "1234567890654321" +   // 自定义序列号: BCD码
                    "0000" +               // 状态寄存器: 0x0000
                    "0000" +               // 压力值: 0 Kpa
                    "E3130000" +           // 流量采样值: 0x000013E3 = 5091
                    "1300" +               // 空管采样值: 0x0013 = 19
                    "0000" +               // 励磁采样值: 0x0000 = 0
                    "0301" +               // 单位代码: 累计m³(01), 瞬时m³/h(03)
                    "0403" +               // 小数位数: 累计3位小数(03), 瞬时自动小数位(04)
                    "BAA32F45" +           // 流速: 0x452FA3BA = 2810.23291015625 mm/s
                    "38EA9E42" +           // 瞬时流量: 0x429EEA38 = 79.45745849609375 m³/h
                    "022C3100" +           // 正向累计: 0x00312C02 = 3222530
                    "F5FEFFFF" +           // 反向累计: 0xFFFFEFF5 = -267
                    "F72A3100" +           // 总累计: 0x00312AF7 = 3222263
                    "A8F0AA0B" +           // CRC32: 0x0BAAF0A8 (小端序)
                    "4E44";                // 帧尾: ND


    public static void main(String[] args) {


        // 创建编解码器
        SM0101Decoder decoder = new SM0101Decoder();
        SM0101Encoder encoder = new SM0101Encoder();

        // 测试1: 解析登录报文
        System.out.println("【测试1】解析登录/心跳报文");
        System.out.println("========================================");
        testDecodeLoginPacket(decoder);

        //// 测试2: 创建确认报文
        //System.out.println("\n【测试2】创建确认报文");
        //System.out.println("========================================");
        //ByteBuf ackBuf = encoder.createAckResponse(0xFFFFFFFFL, (short) 0, "230915184225");
        //System.out.println("ACK报文: " + ByteBufUtil.hexDump(ackBuf));

    }

    /**
     * 测试1: 解析登录报文
     */
    private static void testDecodeLoginPacket(SM0101Decoder decoder) {
        try {

            // 解码报文
            System.out.println("\n开始解码...");
            byte[] bytes = Hex.decodeHex(LOGIN_HEX);
            DeviceData data = DeviceData.builder()
                    .data(bytes)
                    .buf(Unpooled.wrappedBuffer(bytes))
                    .build();
            SM0101Message message = decoder.decode(data);

            // 打印解码结果
            printDecodedMessage(message);

            System.out.println("✓ 登录报文解析成功");
        } catch (Exception e) {
            System.err.println("✗ 登录报文解析失败: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    /**
     * 打印解码后的消息
     */
    private static void printDecodedMessage(SM0101Message message) {
        System.out.println("\n解码结果:");
        System.out.println("----------------------------------------");
        System.out.printf("帧头: %s\n", bytesToHex(message.getFrameHeader()));
        System.out.printf("帧长: %d 字节\n", message.getFrameLength());
        System.out.printf("地址: 0x%08X %s\n",
                message.getDeviceAddress(),
                message.getDeviceAddress() == 0xFFFFFFFFL ? "(广播)" : "(设备地址)");
        System.out.printf("控制码: 0x%02X\n", message.getControlCode() & 0xFF);
        System.out.printf("指令码: 0x%02X - %s\n",
                message.getCommandCode() & 0xFF,
                getCommandCodeDesc(message.getCommandCode() & 0xFF));
        System.out.printf("帧计数: %d\n", message.getFrameCounter() & 0xFF);
        System.out.printf("扩展1: 0x%02X\n", message.getExtension1() & 0xFF);

        printLoginDataDetails(message);

        System.out.printf("CRC32: 0x%08X\n", message.getCrc32());
        System.out.printf("帧尾: %s\n", bytesToHex(message.getFrameTail()));
        System.out.println("----------------------------------------");
    }

    /**
     * 打印登录数据详情
     */
    private static void printLoginDataDetails(SM0101Message message) {
        System.out.println("\n登录/心跳数据:");
        System.out.println("  IMEI: " + message.getImei());
        System.out.printf("  信号强度: %d (0x%02X)\n",
                message.getSignalStrength(), message.getSignalStrength());
        System.out.println("  ICCID: " + message.getIccid());
        System.out.println("  自定义序列号: " + message.getCustomSerial());

        if (message.getStatusRegister() != null) {
            int status = message.getStatusRegister();
            System.out.printf("  状态寄存器: 0x%04X\n", status);
            System.out.printf("    下限告警: %s\n", (status & 0x01) != 0 ? "是" : "否");
            System.out.printf("    上限告警: %s\n", (status & 0x02) != 0 ? "是" : "否");
            System.out.printf("    标定状态: %s\n", (status & 0x04) != 0 ? "是" : "否");
            System.out.printf("    低电量: %s\n", (status & 0x08) != 0 ? "是" : "否");
            System.out.printf("    空管告警: %s\n", (status & 0x10) != 0 ? "是" : "否");
            System.out.printf("    励磁告警: %s\n", (status & 0x20) != 0 ? "是" : "否");
        }

        System.out.printf("  压力值: %d Kpa\n", message.getPressure());
        System.out.printf("  流量采样值: %d\n", message.getFlowSample());
        System.out.printf("  空管采样值: %d\n", message.getEmptyTubeSample());
        System.out.printf("  励磁采样值: %d\n", message.getExcitationSample());

        if (message.getUnitCode() != null) {
            int unitCode = message.getUnitCode() & 0xFFFF;
            int totalUnit = (unitCode >> 8) & 0xFF;
            int instantUnit = unitCode & 0xFF;
            System.out.printf("  单位代码: 累计=%s, 瞬时=%s\n",
                    getTotalUnitName(totalUnit), getInstantUnitName(instantUnit));
        }

        if (message.getDecimalPlaces() != null) {
            int decimalCode = message.getDecimalPlaces() & 0xFFFF;
            int totalPlaces = (decimalCode >> 8) & 0xFF;
            int instantPlaces = decimalCode & 0xFF;
            System.out.printf("  小数位数: 累计=%d位, 瞬时=%s\n",
                    totalPlaces,
                    instantPlaces == 4 ? "自动" : (instantPlaces + "位"));
        }

        System.out.printf("  流速: %.2f mm/s\n", message.getFlowVelocity());
        System.out.printf("  瞬时流量: %.2f m³/h\n", message.getInstantFlow());
        System.out.printf("  正向累计: %d (原始值)\n", message.getForwardTotal());
        System.out.printf("  反向累计: %d (原始值)\n", message.getReverseTotal());
        System.out.printf("  总累计: %d (原始值)\n", message.getTotalFlow());

        // 计算实际累计值（考虑小数位数）
        if (message.getDecimalPlaces() != null && message.getForwardTotal() != null) {
            int decimalCode = message.getDecimalPlaces() & 0xFFFF;
            int totalPlaces = (decimalCode >> 8) & 0xFF;
            if (totalPlaces > 0) {
                double divisor = Math.pow(10, totalPlaces);
                System.out.printf("  实际正向累计: %.{}f m³\n", totalPlaces, message.getForwardTotal() / divisor);
                System.out.printf("  实际反向累计: %.{}f m³\n", totalPlaces, message.getReverseTotal() / divisor);
                System.out.printf("  实际总累计: %.{}f m³\n", totalPlaces, message.getTotalFlow() / divisor);
            }
        }
    }


    /**
     * 工具方法：字节数组转16进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * 获取指令码描述
     */
    private static String getCommandCodeDesc(int code) {
        switch (code) {
            case 0x00:
                return "登录/心跳";
            case 0x01:
                return "确认/否认";
            case 0x02:
                return "配置修改";
            case 0x03:
                return "参数读取";
            case 0x04:
                return "参数设置";
            default:
                if (code >= 0x00 && code <= 0x0F) return "设备状态类指令";
                if (code >= 0x10 && code <= 0x1F) return "参数配置类指令";
                return String.format("未知指令(0x%02X)", code);
        }
    }

    /**
     * 获取累计单位名称
     */
    private static String getTotalUnitName(int code) {
        switch (code) {
            case 0:
                return "L";
            case 1:
                return "m³";
            case 2:
                return "Kg";
            case 3:
                return "T";
            case 4:
                return "Gal(英制)";
            case 5:
                return "Gal(美制)";
            default:
                return "未知";
        }
    }

    /**
     * 获取瞬时单位名称
     */
    private static String getInstantUnitName(int code) {
        switch (code) {
            case 0:
                return "L/H";
            case 1:
                return "L/M";
            case 2:
                return "L/S";
            case 3:
                return "M³/H";
            case 4:
                return "M³/M";
            case 5:
                return "M³/S";
            case 6:
                return "Kg/H";
            case 7:
                return "Kg/M";
            case 8:
                return "Kg/S";
            case 9:
                return "T/H";
            case 10:
                return "T/M";
            case 11:
                return "T/S";
            case 12:
                return "Gal/H(英制)";
            case 13:
                return "Gal/M(英制)";
            case 14:
                return "Gal/S(英制)";
            case 15:
                return "Gal/H(美制)";
            case 16:
                return "Gal/M(美制)";
            case 17:
                return "Gal/S(美制)";
            default:
                return "未知";
        }
    }
}
