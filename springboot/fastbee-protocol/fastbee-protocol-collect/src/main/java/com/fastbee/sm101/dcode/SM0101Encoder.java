package com.fastbee.sm101.dcode;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.CRC32;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fastbee.sm101.model.SM0101Message;

/**
 * SM0101协议编码器
 */
@Slf4j
@Component
public class SM0101Encoder {

    private static final ByteBufAllocator ALLOC = PooledByteBufAllocator.DEFAULT;

    // 固定帧头
    private static final byte[] FRAME_HEADER = {0x53, 0x4D, 0x01, 0x01};
    // 固定帧尾
    private static final byte[] FRAME_TAIL = {0x4E, 0x44};

    // BCD时间格式：秒-分-时-日-月-年
    // 注意：每个字段都是2位数字，所以总共需要12个字符
    private static final DateTimeFormatter BCD_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("ssmmHHddMMyy");

    /**
     * 编码SM0101消息为字节流
     */
    public ByteBuf encode(SM0101Message message) {
        try {
            // 1. 准备变长数据
            byte[] variableData = prepareVariableData(message);

            // 2. 计算总帧长
            int totalFrameLength = 4 + 4 + 4 + 4 + variableData.length + 4 + 2;

            // 3. 创建ByteBuf并写入数据
            ByteBuf buf = ALLOC.buffer(totalFrameLength);

            // 写入帧头
            buf.writeBytes(FRAME_HEADER);

            // 写入帧长（小端序）
            buf.writeIntLE(totalFrameLength);

            // 写入地址（小端序）
            buf.writeIntLE((int) message.getDeviceAddress());

            // 写入控制字（4字节）- 小端序
            buf.writeIntLE(0x00000100);

            // 写入变长数据
            buf.writeBytes(variableData);

            // 4. 计算CRC32并写入
            long crcValue = calculateCRC32(message, totalFrameLength, variableData);
            buf.writeIntLE((int) crcValue);
            message.setCrc32(crcValue);

            // 5. 写入帧尾
            buf.writeBytes(FRAME_TAIL);

            log.debug("编码SM0101消息成功，总帧长: {} 字节", totalFrameLength);
            log.debug("CRC32计算结果: 0x{}", Long.toHexString(crcValue).toUpperCase());

            return buf;

        } catch (Exception e) {
            log.error("编码SM0101消息失败", e);
            throw new RuntimeException("编码SM0101消息失败", e);
        }
    }

    /**
     * 计算CRC32（使用ByteBuf简化小端序处理）
     */
    private long calculateCRC32(SM0101Message message, int totalFrameLength, byte[] variableData) {
        // 使用ByteBuf处理小端序
        ByteBuf buffer = Unpooled.buffer(4 + 4 + 4 + variableData.length);

        // 写入帧长（小端序）
        buffer.writeIntLE(totalFrameLength);

        // 写入地址（小端序）
        buffer.writeIntLE((int) message.getDeviceAddress());

        // 写入控制字（小端序）- 0x00000100
        buffer.writeIntLE(0x00000100);

        // 写入变长数据
        buffer.writeBytes(variableData);

        // 获取字节数组
        byte[] crcData = new byte[buffer.readableBytes()];
        buffer.readBytes(crcData);
        buffer.release();

        // 计算标准CRC32
        CRC32 crc32 = new CRC32();
        crc32.update(crcData);
        long crc = crc32.getValue();

        log.debug("CRC32计算数据: {}", bytesToHex(crcData));
        log.debug("CRC32计算结果: 0x{}", Long.toHexString(crc).toUpperCase());
        return crc;
    }

    /**
     * 准备变长数据
     */
    private byte[] prepareVariableData(SM0101Message message) {
        if (message.getCommandCode() == 0x01) { // 确认/否认
            return encodeAckData(message);
        }
        return new byte[0];
    }

    /**
     * 编码确认/否认数据
     */
    private byte[] encodeAckData(SM0101Message message) {
        ByteBuffer buffer = ByteBuffer.allocate(7);

        // 处理结果 (1字节)
        buffer.put((byte) 0x00);

        // 时间BCD码
        if (message.getServerTime() != null && message.getServerTime().length() == 12) {
            // 使用传入的时间
            for (int i = 0; i < 6; i++) {
                String bcdByte = message.getServerTime().substring(i * 2, i * 2 + 2);
                buffer.put((byte) Integer.parseInt(bcdByte, 16));
            }
        } else {
            // 生成当前时间
            buffer.put(generateCurrentTimeBCD());
        }

        return buffer.array();
    }

    /**
     * 生成当前时间的BCD码
     * 格式：秒-分-时-日-月-年（每个占1字节BCD码）
     * 注意：每个字段都是2位十进制数
     */
    private byte[] generateCurrentTimeBCD() {
        try {
            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();

            // 格式化为BCD字符串：秒分时日月年（每个2位十进制数）
            String bcdTimeStr = now.format(BCD_TIME_FORMATTER);

            // 确保字符串长度是12（6个字段 * 2位）
            if (bcdTimeStr.length() != 12) {
                log.warn("格式化时间字符串长度不正确: {}, 使用默认时间", bcdTimeStr.length());
                // 使用默认时间：2023年12月31日23:59:59
                bcdTimeStr = "595923123123";
            }

            // 转换为6字节BCD数组
            byte[] bcdBytes = new byte[6];
            for (int i = 0; i < 6; i++) {
                String twoDigitStr = bcdTimeStr.substring(i * 2, i * 2 + 2);
                int decimalValue = Integer.parseInt(twoDigitStr);

                // 将十进制数转换为BCD码
                // 例如：37 -> 0x37, 25 -> 0x25
                bcdBytes[i] = (byte) ((decimalValue / 10 << 4) | (decimalValue % 10));
            }

            log.debug("生成BCD时间: {}", bytesToHex(bcdBytes));
            return bcdBytes;

        } catch (Exception e) {
            log.error("生成BCD时间失败", e);
            // 返回默认时间：2023年12月31日23:59:59
            return new byte[] {0x59, 0x59, 0x23, 0x31, 0x12, 0x23};
        }
    }

    /**
     * 字节数组转十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    /**
     * 创建确认响应报文
     */
    public ByteBuf createAckResponse(long deviceAddress, String serverTime) {
        SM0101Message message = new SM0101Message();
        message.setDeviceAddress(deviceAddress);
        message.setControlWord(0x00000100);
        message.setCommandCode((short) 0x01);
        message.setProcessResult((short) 0x00);
        message.setServerTime(serverTime);

        return encode(message);
    }



}
