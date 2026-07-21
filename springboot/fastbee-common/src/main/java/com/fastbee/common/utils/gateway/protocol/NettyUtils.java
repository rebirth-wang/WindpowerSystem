//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.gateway.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public class NettyUtils {
    public static byte[] readBytesFromByteBuf(ByteBuf buf) {
        return ByteBufUtil.getBytes(buf);
    }
}
