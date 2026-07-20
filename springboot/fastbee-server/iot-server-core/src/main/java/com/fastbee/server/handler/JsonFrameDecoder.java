package com.fastbee.server.handler;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * JSON 帧解码器 - 基于括号匹配处理 TCP 分包/粘包
 * 支持 JSON 数组 [ ... ] 和 JSON 对象 { ... }
 * 正确处理字符串内的括号和转义字符
 *
 * @author bill
 */
@Slf4j
public class JsonFrameDecoder extends ByteToMessageDecoder {

    // 最大 JSON 长度限制，防止恶意数据（可根据实际调整）
    private static final int MAX_FRAME_LENGTH = 1024 * 1024; // 1MB

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 1. 检查是否为 JSON 起始字符（跳过前导空白）
        if (!isJsonStart(in)) {
            // 非 JSON 数据直接透传（例如自定义协议）
            out.add(in.readRetainedSlice(in.readableBytes()));
            return;
        }

        // 2. 尝试解析完整 JSON 帧
        ByteBuf completeFrame = tryParseCompleteJson(in);
        if (completeFrame == null) {
            // 数据不完整，等待更多数据（不移动读指针）
            return;
        }

        // 3. 将完整帧交给下一个 handler
        out.add(completeFrame);

    }

    /**
     * 检查当前可读字节是否以 JSON 起始字符（[ 或 {）开头，忽略前导空白。
     */
    private boolean isJsonStart(ByteBuf in) {
        if (!in.isReadable()) {
            return false;
        }
        int readerIndex = in.readerIndex();
        // 跳过空白字符
        while (in.isReadable(readerIndex)) {
            byte b = in.getByte(readerIndex);
            if (b != ' ' && b != '\r' && b != '\n' && b != '\t') {
                break;
            }
            readerIndex++;
        }
        if (!in.isReadable(readerIndex)) {
            return false;
        }
        byte b = in.getByte(readerIndex);
        return b == '[' || b == '{';
    }

    /**
     * 尝试从缓冲区中解析出一个完整的 JSON 对象或数组。
     */
    private ByteBuf tryParseCompleteJson(ByteBuf in) {
        int start = in.readerIndex();
        int length = in.readableBytes();
        if (length == 0) {
            return null;
        }

        // 找到第一个非空白字符的位置
        int startPos = start;
        while (startPos < start + length) {
            byte b = in.getByte(startPos);
            if (b != ' ' && b != '\r' && b != '\n' && b != '\t') {
                break;
            }
            startPos++;
        }
        if (startPos >= start + length) {
            return null;
        }

        // 确定 JSON 类型
        byte firstByte = in.getByte(startPos);
        boolean isArray = (firstByte == '[');
        boolean isObject = (firstByte == '{');
        if (!isArray && !isObject) {
            return null; // 理论上不会发生
        }

        // 括号深度计数器
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;

        // 从起始位置开始扫描
        for (int i = startPos; i < start + length; i++) {
            byte b = in.getByte(i);

            // 转义处理
            if (escaped) {
                escaped = false;
                continue;
            }
            if (b == '\\' && inString) {
                escaped = true;
                continue;
            }

            // 字符串边界
            if (b == '"') {
                inString = !inString;
                continue;
            }

            // 仅在字符串外才进行括号匹配
            if (!inString) {
                if (isArray) {
                    if (b == '[') {
                        depth++;
                    } else if (b == ']') {
                        depth--;
                        if (depth == 0) {
                            int endPos = i + 1; // 包含右括号
                            int frameLen = endPos - startPos;
                            if (frameLen > MAX_FRAME_LENGTH) {
                                log.warn("JSON frame too large ({} bytes), discarding", frameLen);
                                // 跳过这个过大的帧，防止内存溢出
                                in.readerIndex(endPos);
                                return null;
                            }
                            // 复制帧数据（独立副本）
                            ByteBuf frame = Unpooled.buffer(frameLen);
                            in.getBytes(startPos, frame, frameLen);
                            // 移动读指针到帧结束位置
                            in.readerIndex(endPos);
                            return frame;
                        }
                    }
                } else { // JSON 对象
                    if (b == '{') {
                        depth++;
                    } else if (b == '}') {
                        depth--;
                        if (depth == 0) {
                            int endPos = i + 1;
                            int frameLen = endPos - startPos;
                            if (frameLen > MAX_FRAME_LENGTH) {
                                log.warn("JSON frame too large ({} bytes), discarding", frameLen);
                                in.readerIndex(endPos);
                                return null;
                            }
                            ByteBuf frame = Unpooled.buffer(frameLen);
                            in.getBytes(startPos, frame, frameLen);
                            in.readerIndex(endPos);
                            return frame;
                        }
                    }
                }
            }
        }

        // 未找到匹配的括号
        return null;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isActive()) {
            log.debug("Channel closed, discarding incomplete JSON data");
        }
        super.channelInactive(ctx);
    }
}
