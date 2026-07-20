package com.fastbee.base.codec;

import io.netty.buffer.ByteBuf;

import com.fastbee.common.extend.core.protocol.Message;

/**
 * 基础消息编码类
 *
 * @author bill
 */
public interface MessageEncoder{

    ByteBuf encode(Message message, String clientId);
}
