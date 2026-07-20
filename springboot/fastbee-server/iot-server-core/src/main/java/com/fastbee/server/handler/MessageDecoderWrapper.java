package com.fastbee.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.base.codec.MessageDecoder;
import com.fastbee.base.session.Packet;
import com.fastbee.common.extend.core.domin.mq.DeviceReport;

/**
 * 基础消息解码 -进站消息-处理步骤3
 *
 * @author bill
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageDecoderWrapper extends ChannelInboundHandlerAdapter {

    private final MessageDecoder decoder;

    public MessageDecoderWrapper(MessageDecoder decoder) {
        this.decoder = decoder;
    }

    /**
     * TCP-2.报文解码
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        /*取出上一站报文*/
        Packet packet = (Packet) msg;
        ByteBuf input = packet.take();
        try {
            /*匹配协议中适合的协议解码器 解码*/
            DeviceReport message =  decoder.decode(input, packet.session.getClientId());
            if (message != null) {
                /*传递到下一个处理器*/
                ctx.fireChannelRead(packet.replace(message));
            }
            /*将当前readerIndex增加指定的长度，已经解码完成*/
            input.skipBytes(input.readableBytes());
        } catch (Exception e) {
            log.error("消息解码异常[" + ByteBufUtil.hexDump(input, 0, input.writerIndex()) + "]", e);
        } finally {
            input.release();
        }
    }
}
