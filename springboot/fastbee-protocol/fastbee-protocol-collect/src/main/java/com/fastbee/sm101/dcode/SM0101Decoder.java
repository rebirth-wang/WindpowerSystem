package com.fastbee.sm101.dcode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.mq.message.DeviceData;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.sm101.model.SM0101Message;

/**
 * SM0101协议解码器
 */
@Slf4j
@Component
public class SM0101Decoder {

    // 固定帧头
    private static final byte[] FRAME_HEADER = {0x53, 0x4D, 0x01, 0x01};
    // 固定帧尾
    private static final byte[] FRAME_TAIL = {0x4E, 0x44};

    // 是否跳过CRC32验证
    private static final boolean SKIP_CRC32_CHECK = true;

    /**
     * 解码SM0101消息
     */
    public SM0101Message decode(DeviceData deviceData) {
        try {
            ByteBuf in = deviceData.getBuf();

            // 验证最小长度
            if (in.readableBytes() < 22) { // 最小帧头4+帧长4+地址4+控制1+指令1+计数1+扩展1+CRC4+帧尾2
                throw new ServiceException("数据长度不足，至少需要22字节，实际:" + in.readableBytes());
            }

            // 记录原始位置
            int originalReaderIndex = in.readerIndex();

            // 1. 验证帧头
            byte[] header = new byte[4];
            in.getBytes(originalReaderIndex, header);
            if (!compareBytes(header, FRAME_HEADER)) {
                throw new ServiceException("帧头验证失败，期望:534D0101，实际:" + ByteBufUtil.hexDump(header));
            }

            // 2. 解析固定字段
            SM0101Message message = new SM0101Message();

            // 读取帧头
            in.readBytes(message.getFrameHeader());

            // 读取帧长（小端序）
            int frameLength = in.readIntLE();
            message.setFrameLength(frameLength);

            log.debug("帧长: {} 字节, 当前可读字节: {}", frameLength, in.readableBytes());

            int remainingBytes = frameLength - 8; // 减去已读的帧头4字节和帧长4字节
            if (in.readableBytes() < remainingBytes) {
                throw new ServiceException(String.format("数据不完整，期望剩余长度:%d，实际可用:%d",
                        remainingBytes, in.readableBytes()));
            }

            // 地址（小端序）
            long address = in.readUnsignedIntLE();
            message.setDeviceAddress(address);

            // 控制码
            message.setControlCode(in.readUnsignedByte());

            // 指令码
            message.setCommandCode(in.readUnsignedByte());

            // 帧计数
            message.setFrameCounter(in.readUnsignedByte());

            // 扩展1
            message.setExtension1(in.readUnsignedByte());

            // 3. 读取变长数据
            int variableDataLength = calculateVariableDataLength(frameLength);
            log.debug("变长数据长度: {} 字节", variableDataLength);

            if (variableDataLength > 0) {
                byte[] variableData = new byte[variableDataLength];
                in.readBytes(variableData);
                message.setVariableData(variableData);

                // 根据指令码解析变长数据
                parseVariableData(message, variableData);
            }

            // 4. 读取CRC32（小端序）
            long crc32 = in.readUnsignedIntLE();
            message.setCrc32(crc32);

            // 5. 验证CRC32（暂时跳过）
            if (!SKIP_CRC32_CHECK) {
                verifyCRC32(in, frameLength, message, originalReaderIndex);
            } else {
                log.warn("⚠ 跳过CRC32验证，计算值: 0x{}, 报文值: 0x{}",
                        calculateCRC32(in, frameLength, originalReaderIndex),
                        Long.toHexString(crc32));
            }

            // 6. 验证帧尾
            byte[] tail = new byte[2];
            in.readBytes(tail);
            if (!compareBytes(tail, FRAME_TAIL)) {
                throw new ServiceException("帧尾验证失败，期望:4E44，实际:" + ByteBufUtil.hexDump(tail));
            }

            log.debug("解码SM0101消息成功，指令码:0x{}", Integer.toHexString(message.getCommandCode()));
            return message;

        } catch (Exception e) {
            log.error("解码SM0101消息失败", e);
            throw new ServiceException("解码SM0101消息失败: " + e.getMessage());
        }
    }

    /**
     * 计算CRC32值（用于调试）
     */
    private long calculateCRC32(ByteBuf in, int frameLength, int originalReaderIndex) {
        try {
            // 计算CRC32的范围：从帧头开始到变长数据包结束（包括所有数据，除了CRC32和帧尾）
            int variableDataLength = calculateVariableDataLength(frameLength);
            int crcDataLength = 4 + 4 + 4 + 1 + 1 + 1 + 1 + variableDataLength;

            ByteBuf crcData = in.slice(originalReaderIndex, crcDataLength);
            byte[] dataForCrc = new byte[crcDataLength];
            crcData.readBytes(dataForCrc);

            CRC32 crc32 = new CRC32();
            crc32.update(dataForCrc);
            return crc32.getValue();
        } catch (Exception e) {
            log.error("计算CRC32失败", e);
            return 0;
        }
    }


    /**
     * 计算变长数据长度
     */
    private int calculateVariableDataLength(int frameLength) {
        // 总长度 - 固定部分长度（帧头4+帧长4+地址4+控制1+指令1+计数1+扩展1+CRC4+帧尾2）
        return frameLength - 22;
    }

    /**
     * 解析变长数据
     */
    private void parseVariableData(SM0101Message message, byte[] variableData) {
        if (variableData == null || variableData.length == 0) {
            return;
        }

        ByteBuffer buffer = ByteBuffer.wrap(variableData).order(ByteOrder.LITTLE_ENDIAN);

        if (message.getCommandCode() == 0x00) { // 登录/心跳
            parseLoginData(message, buffer);
        } else {
            log.warn("未知指令码: 0x{}", Integer.toHexString(message.getCommandCode()));
        }
    }

    /**
     * 解析登录/心跳数据（0x00）
     */
    private void parseLoginData(SM0101Message message, ByteBuffer buffer) {
        try {
            // IMEI (15字节字符串)
            byte[] imeiBytes = new byte[15];
            buffer.get(imeiBytes);
            message.setImei(new String(imeiBytes, StandardCharsets.US_ASCII).trim());

            // CSQ信号强度 (1字节)
            message.setSignalStrength((short) (buffer.get() & 0xFF));

            // ICCID (20字节字符串)
            byte[] iccidBytes = new byte[20];
            buffer.get(iccidBytes);
            message.setIccid(new String(iccidBytes, StandardCharsets.US_ASCII).trim());

            // 自定义序列号 (8字节BCD)
            byte[] serialBytes = new byte[8];
            buffer.get(serialBytes);
            message.setCustomSerial(ByteBufUtil.hexDump(serialBytes));

            // 状态寄存器 (2字节)
            message.setStatusRegister(buffer.getShort() & 0xFFFF);

            // 压力值 (2字节, 单位KPa)
            message.setPressure(buffer.getShort() & 0xFFFF);

            // 流量采样值 (4字节有符号)
            message.setFlowSample(buffer.getInt());

            // 空管采样值 (2字节)
            message.setEmptyTubeSample(buffer.getShort() & 0xFFFF);

            // 励磁采样值 (2字节)
            message.setExcitationSample(buffer.getShort() & 0xFFFF);

            // 单位代码 (2字节)
            message.setUnitCode(buffer.getShort());

            // 小数位数 (2字节)
            message.setDecimalPlaces(buffer.getShort());

            // 流速 (4字节浮点数)
            message.setFlowVelocity(buffer.getFloat());

            // 瞬时流量 (4字节浮点数)
            message.setInstantFlow(buffer.getFloat());

            // 正向累计 (4字节)
            message.setForwardTotal(buffer.getInt() & 0xFFFFFFFFL);

            // 反向累计 (4字节)
            int reverseSigned = buffer.getInt();
            message.setReverseTotal((long) reverseSigned); // 存储有符号值

            // 总累计 (4字节)
            message.setTotalFlow(buffer.getInt() & 0xFFFFFFFFL);

        } catch (Exception e) {
            log.error("解析登录/心跳数据失败", e);
            throw new ServiceException("解析登录/心跳数据失败: " + e.getMessage());
        }
    }

    /**
     * 验证CRC32
     */
    private void verifyCRC32(ByteBuf in, int frameLength, SM0101Message message, int originalReaderIndex) {
        try {
            // 计算CRC32的范围：从帧头开始到变长数据包结束（包括所有数据，除了CRC32和帧尾）
            int variableDataLength = calculateVariableDataLength(frameLength);
            int crcDataLength = 4 + 4 + 4 + 1 + 1 + 1 + 1 + variableDataLength;

            ByteBuf crcData = in.slice(originalReaderIndex, crcDataLength);
            byte[] dataForCrc = new byte[crcDataLength];
            crcData.readBytes(dataForCrc);

            CRC32 crc32 = new CRC32();
            crc32.update(dataForCrc);
            long calculatedCrc = crc32.getValue();

            // 验证CRC32
            if (calculatedCrc != message.getCrc32()) {
                log.warn("CRC32校验失败, 计算值: 0x{}, 报文值: 0x{}, 数据长度: {}",
                        Long.toHexString(calculatedCrc),
                        Long.toHexString(message.getCrc32()),
                        crcDataLength);

                log.debug("CRC32计算数据: {}", ByteBufUtil.hexDump(dataForCrc));

                throw new ServiceException(MessageUtils.message("protocol.crc.check.failed"));
            }

            log.debug("CRC32验证通过: 0x{}", Long.toHexString(calculatedCrc));

        } catch (Exception e) {
            log.error("CRC32验证失败", e);
            throw new ServiceException("CRC32验证失败: " + e.getMessage());
        }
    }

    /**
     * 比较字节数组是否相等
     */
    private boolean compareBytes(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }

        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

}
