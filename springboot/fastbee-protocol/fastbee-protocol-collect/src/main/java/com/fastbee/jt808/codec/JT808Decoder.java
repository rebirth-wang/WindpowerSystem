package com.fastbee.jt808.codec;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.*;

import com.fastbee.common.extend.utils.jt.JT808Utils;
import com.fastbee.jt808.JT808Message;
import com.fastbee.protocol.WModelManager;
import com.fastbee.protocol.base.model.RuntimeSchema;
import com.fastbee.protocol.util.ArrayMap;
import com.fastbee.protocol.util.ExplainUtils;

/**
 * JT808协议解码器
 * @author gsb
 * @date 2024/5/27 17:05
 */

public class JT808Decoder {

    private static final ByteBufAllocator ALLOC = PooledByteBufAllocator.DEFAULT;

    private final WModelManager manager;

    private final   ArrayMap<RuntimeSchema> headerSchemaMap;

    public JT808Decoder(String... basePackages) {
        this.manager = new WModelManager(basePackages);
        this.headerSchemaMap = manager.getActiveMap(JT808Message.class);
    }

    public JT808Decoder(WModelManager manager) {
        this.manager = manager;
        this.headerSchemaMap = manager.getActiveMap(JT808Message.class);
    }

    public JT808Message decode(ByteBuf input) {
        return decode(input, null);
    }

    public JT808Message decode(ByteBuf input, ExplainUtils explain) {
        ByteBuf buf = unescape(input);

        boolean verified = verify(buf);
        int messageId = buf.getUnsignedShort(0);
        int properties = buf.getUnsignedShort(2);

        int version = 0;//缺省值为2013版本
        if (JT808Utils.isTrue(properties, 14))//识别2019及后续版本
            version = buf.getUnsignedByte(4);

        boolean isSubpackage = JT808Utils.isTrue(properties, 13);
        int headLen = JT808Utils.headerLength(version, isSubpackage);

        RuntimeSchema<JT808Message> headSchema = headerSchemaMap.get(version);
        RuntimeSchema<JT808Message> bodySchema = manager.getActiveMap(messageId, version);

        JT808Message message;
        if (bodySchema == null)
            message = new JT808Message();
        else
            message = bodySchema.newInstance();
        message.setVerified(verified);
        message.setPayload(input);

        int writerIndex = buf.writerIndex();
        buf.writerIndex(headLen);
        headSchema.mergeFrom(buf, message, explain);
        buf.writerIndex(writerIndex - 1);

        int realVersion = message.getProtocolVersion();
        if (realVersion != version)
            bodySchema = manager.getActiveMap(messageId, realVersion);

        if (bodySchema != null) {
            int bodyLen = message.getBodyLength();

            if (isSubpackage) {

                ByteBuf bytes = ALLOC.buffer(bodyLen);
                buf.getBytes(headLen, bytes);

                ByteBuf[] packages = addAndGet(message, bytes);
                if (packages == null)
                    return message;

                ByteBuf bodyBuf = Unpooled.wrappedBuffer(packages);
                bodySchema.mergeFrom(bodyBuf, message, explain);
                if (message.noBuffer()) {
                    bodyBuf.release();
                }
            } else {
                buf.readerIndex(headLen);
                bodySchema.mergeFrom(buf, message, explain);
            }
        }
        return message;
    }

    protected ByteBuf[] addAndGet(JT808Message message, ByteBuf bytes) {
        return null;
    }

    /** 校验 */
    public static boolean verify(ByteBuf buf) {
        byte checkCode = JT808Utils.bcc(buf, -1);
        return checkCode == buf.getByte(buf.writerIndex() - 1);
    }

    /** 反转义 */
    public static ByteBuf unescape(ByteBuf source) {
        int low = source.readerIndex();
        int high = source.writerIndex();

        if (source.getByte(low) == 0x7e)
            low++;

        if (source.getByte(high - 1) == 0x7e)
            high--;

        int mark = source.indexOf(low, high - 1, (byte) 0x7d);
        if (mark == -1) {
            return source.slice(low, high - low);
        }

        List<ByteBuf> bufList = new ArrayList<>(3);

        int len;
        do {

            len = mark + 2 - low;
            bufList.add(slice(source, low, len));
            low += len;

            mark = source.indexOf(low, high, (byte) 0x7d);
        } while (mark > 0);

        bufList.add(source.slice(low, high - low));

        return new CompositeByteBuf(ALLOC, false, bufList.size(), bufList);
    }

    /** 截取转义前报文，并还原转义位 */
    protected static ByteBuf slice(ByteBuf byteBuf, int index, int length) {
        byte second = byteBuf.getByte(index + length - 1);
        if (second == 0x01) {
            return byteBuf.slice(index, length - 1);
        } else if (second == 0x02) {
            byteBuf.setByte(index + length - 2, 0x7e);
            return byteBuf.slice(index, length - 1);
        } else {
            return byteBuf.slice(index, length);
        }
    }
}
