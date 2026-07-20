package com.fastbee.server.handler;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Modbus RTU粘包处理器
 * 专门处理Modbus RTU协议的帧拆分
 * @author gsb
 * @date 2025/11/26 13:44
 */
@Slf4j
@ChannelHandler.Sharable
public class ModbusRtuFrameDecoder extends MessageToMessageDecoder<ByteBuf> {

    private static final int CRC16_MODBUS_POLY = 0xA001; // Modbus CRC16多项式
    private static final int CRC16_MODBUS_INIT = 0xFFFF; // Modbus CRC16初始值

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        log.debug("=>开始处理Modbus粘包，原数据: {}", ByteBufUtil.hexDump(in));

        // 检查是否是Modbus RTU协议
        if (!isModbusRtuProtocol(in)) {
            // 不是Modbus协议，直接传递原始数据
            out.add(in.retain());
            return;
        }

        // 处理Modbus粘包
        while (in.readableBytes() >= 4) { // Modbus最小帧长检查
            in.markReaderIndex();

            int frameLength = calculateModbusFrameLength(in);
            if (frameLength == -1) {
                log.debug("=>无法确定帧长度");
                in.resetReaderIndex();
                break;
            }

            if (in.readableBytes() < frameLength) {
                log.debug("=>数据不完整，需要{}字节，当前{}字节", frameLength, in.readableBytes());
                in.resetReaderIndex();
                break;
            }

            ByteBuf frame = in.readRetainedSlice(frameLength);
            if (validateModbusCrc(frame)) {
                log.debug("=>成功提取完整Modbus帧，长度: {}", frameLength);
                out.add(frame);
            } else {
                frame.release();
                log.warn("=>Modbus CRC校验失败，帧长度: {}", frameLength);
            }
        }

        int remaining = in.readableBytes();
        if (remaining > 0) {
            log.debug("=>粘包处理完成，剩余{}字节", remaining);
        }
    }

    /**
     * 检测是否是Modbus RTU协议
     */
    private boolean isModbusRtuProtocol(ByteBuf in) {
        if (in.readableBytes() < 4) return false;

        int readerIndex = in.readerIndex();

        // 检查地址字段是否合法 (1-247, 0为广播)
        int address = in.getUnsignedByte(readerIndex);
        if (address > 247) return false;

        // 检查功能码是否合法
        int functionCode = in.getUnsignedByte(readerIndex + 1);
        return functionCode >= 1 && functionCode <= 0x10;
    }

    private int calculateModbusFrameLength(ByteBuf in) {
        int readerIndex = in.readerIndex();
        int functionCode = in.getUnsignedByte(readerIndex + 1);
        log.debug("=>获取到的功能码:{}",functionCode);
        switch (functionCode) {
            case 0x01: case 0x02: case 0x03: case 0x04:
                if (in.readableBytes() < 5) return -1;
                int byteCount = in.getUnsignedByte(readerIndex + 2);
                return 3 + byteCount + 2; // 地址1+功能码1+字节数1+数据+CRC2
            case 0x05: case 0x06: return 8; // 地址1+功能码1+地址2+数据2+CRC2
            case 0x0F: case 0x10:
                if (in.readableBytes() < 7) return -1;
                int dataLength = in.getUnsignedByte(readerIndex + 6);
                return 9 + dataLength; // 地址1+功能码1+起始地址2+数量2+字节数1+数据+CRC2
            default: return -1;
        }
    }

    private boolean validateModbusCrc(ByteBuf frame) {
        int frameLength = frame.readableBytes();
        if (frameLength < 2) return false;

        // 使用小端序读取CRC（Modbus RTU标准）
        int receivedCRC = frame.getUnsignedShortLE(frameLength - 2);

        // 提取数据部分（排除CRC）
        byte[] data = new byte[frameLength - 2];
        frame.getBytes(0, data);

        int calculatedCRC = calculateCRC16(data);

        // 调试日志
        log.debug("=>CRC校验: 数据长度={}, 接收CRC=0x{}, 计算CRC=0x{}",
                data.length,
                Integer.toHexString(receivedCRC),
                Integer.toHexString(calculatedCRC));

        boolean isValid = receivedCRC == calculatedCRC;
        if (!isValid) {
            log.warn("=>CRC校验失败: 数据={}, 接收CRC=0x{}, 计算CRC=0x{}",
                    bytesToHex(data),
                    Integer.toHexString(receivedCRC),
                    Integer.toHexString(calculatedCRC));
        }

        return isValid;
    }

    /**
     * 字节数组转十六进制字符串（用于调试）
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * 计算Modbus RTU CRC16校验码
     * @param data 字节数组
     * @return CRC16校验码
     */
    public static int calculateCRC16(byte[] data) {
        return calculateCRC16(data, 0, data.length);
    }

    public static int calculateCRC16(byte[] data, int offset, int length) {
        int crc = CRC16_MODBUS_INIT;

        for (int i = offset; i < offset + length; i++) {
            crc ^= (data[i] & 0xFF);
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x0001) != 0) {
                    crc = (crc >> 1) ^ CRC16_MODBUS_POLY;
                } else {
                    crc = crc >> 1;
                }
            }
        }
        return crc & 0xFFFF;
    }
}
