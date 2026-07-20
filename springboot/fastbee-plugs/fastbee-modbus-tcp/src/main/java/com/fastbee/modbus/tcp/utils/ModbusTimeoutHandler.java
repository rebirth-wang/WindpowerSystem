package com.fastbee.modbus.tcp.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.common.extend.core.domin.mq.message.ModbusDevice;

@Slf4j
public class ModbusTimeoutHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                log.warn("[超时关闭] 设备 {} 响应超时",
                           getDevice(ctx.channel()));
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ModbusDevice device = getDevice(ctx.channel());
        log.error("[通信异常] 设备 {} | 异常：{}",
                   device.getDeviceIp(),
                   cause.getMessage());
        ctx.close();
    }

    private ModbusDevice getDevice(Channel channel) {
        return channel.attr(AttributeKey.<ModbusDevice>valueOf("device")).get();
    }
}
