package com.fastbee.modbus.tcp.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.common.extend.core.domin.mq.ModbusResponse;
import com.fastbee.modbus.tcp.model.ModbusRequest;

/**
 * Modbus TCP 编解码器
 */
@Slf4j
public class ModbusTcpCodec {

    public static class Encoder extends MessageToByteEncoder<ModbusRequest> {
        @Override
        protected void encode(ChannelHandlerContext ctx, ModbusRequest msg, ByteBuf out) {
            log.trace("请求内容: {}", msg);

            // 事务标识符
            out.writeShort(msg.getTransactionId());
            // 协议标识符，Modbus TCP 固定为 0
            out.writeShort(0);
            // 后续数据长度
            out.writeShort(6);
            // 单元标识符
            out.writeByte(msg.getUnitId());
            // 功能码
            out.writeByte(msg.getFunctionCode());
            // 起始地址
            out.writeShort(msg.getStartAddress());
            // 数量
            out.writeShort(msg.getQuantity());

            log.info("编码数据:{}", ByteBufUtil.hexDump(out));
        }
    }

    public static class Decoder extends ByteToMessageDecoder {
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
            String hexDump = ByteBufUtil.hexDump(in);
            in.markReaderIndex();

            // 检查基础头长度
            if (in.readableBytes() < 7) {
                log.debug("[协议解析] 数据不足，等待更多数据");
                in.resetReaderIndex();
                return;
            }

            int transactionId = in.readUnsignedShort();
            int protocolId = in.readUnsignedShort();
            int length = in.readUnsignedShort();
            int address = in.readUnsignedByte();
            int code = in.readUnsignedByte();

            int requiredBytes = length - 2;

            if (in.readableBytes() < requiredBytes) {
                log.debug("[协议解析] 数据不完整，需要{}字节，当前剩余{}字节",
                        requiredBytes, in.readableBytes());
                in.resetReaderIndex();
                return;
            }

            byte[] payload = new byte[requiredBytes];
            in.readBytes(payload);
            ModbusResponse response = new ModbusResponse(
                    transactionId,
                    protocolId,
                    address,
                    (byte) code,
                    hexDump,
                    0,
                    null
            );
            out.add(response);
        }
    }
}
