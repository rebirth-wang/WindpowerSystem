package com.fastbee.modbus.tcp.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ModbusFrameUtils {
    // 构建读保持寄存器请求帧（功能码03）
    public static ByteBuf buildReadRequest(
            int transactionId,
            int unitId,
            int functionCode,
            int startAddress,
            int quantity) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeShort(transactionId); // 事务ID（后续需要动态生成）
        buf.writeShort(0x0000); // 协议ID11
        buf.writeShort(6);       // 长度
        buf.writeByte(unitId);   // 单元ID
        buf.writeByte(functionCode);     // 功能码
        buf.writeShort(startAddress);
        buf.writeShort(quantity);
        return buf;
    }

    /**
     * 写单个线圈
     */
    public static ByteBuf buildWriteCoilRequest(
            int transactionId,
            int address,
            int register,
            boolean coilValue) {
        ByteBuf buffer = Unpooled.buffer(12);
        buffer.writeShort(transactionId);
        buffer.writeShort(0);
        buffer.writeShort(6);
        buffer.writeByte(address);
        buffer.writeByte(0x05);
        buffer.writeShort(register);
        buffer.writeShort(coilValue ? 0xFF00 : 0x0000);
        return buffer;
    }

    public static ByteBuf buildWriteSingleRequest(
            int transactionId,
            int address,
            int register,
            int value){
        ByteBuf buffer = Unpooled.buffer(12);
        buffer.writeShort(transactionId);
        buffer.writeShort(0);
        buffer.writeShort(6);
        buffer.writeByte(address);
        buffer.writeByte(0x06);
        buffer.writeShort(register);
        buffer.writeShort(value);
        return buffer;
    }



}
