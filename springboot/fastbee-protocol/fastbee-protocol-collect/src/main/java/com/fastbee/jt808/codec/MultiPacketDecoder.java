package com.fastbee.jt808.codec;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.jt808.JT808Message;
import com.fastbee.protocol.WModelManager;

/**
 * 分包消息管理
 */
@Slf4j
public class MultiPacketDecoder extends JT808Decoder {

    private final Map<String, MultiPacket> multiPacketsMap;

    private final MultiPacketListener multiPacketListener;

    public MultiPacketDecoder(String... basePackages) {
        this(new WModelManager(basePackages));
    }

    public MultiPacketDecoder(WModelManager manager) {
        this(manager, null);
    }

    public MultiPacketDecoder(WModelManager manager, MultiPacketListener multiPacketListener) {
        super(manager);
        if (multiPacketListener == null) {
            this.multiPacketsMap = new WeakHashMap<>();
            this.multiPacketListener = null;
        } else {
            this.multiPacketsMap = new ConcurrentHashMap<>();
            this.multiPacketListener = multiPacketListener;
            startListener();
        }
    }

    @Override
    protected ByteBuf[] addAndGet(JT808Message message, ByteBuf packetData) {
        String clientId = message.getSerialNumber();
        int messageId = message.getMessageId();
        int packageTotal = message.getPackageTotal();
        int packetNo = message.getPackageNo();

        String key = new StringBuilder(21).append(clientId).append('/').append(messageId).append('/').append(packageTotal).toString();

        MultiPacket multiPacket = multiPacketsMap.get(key);
        if (multiPacket == null)
            multiPacketsMap.put(key, multiPacket = new MultiPacket(message));
        if (packetNo == 1)
            multiPacket.setSerialNo(message.getSerialNo());


        ByteBuf[] packages = multiPacket.addAndGet(packetNo, packetData);
        log.debug("<<<<<分包消息{}", multiPacket);
        if (packages == null)
            return null;
        multiPacketsMap.remove(key);
        return packages;
    }

    private void startListener() {
        Thread thread = new Thread(() -> {
            long timeout = multiPacketListener.timeout;
            for (; ; ) {
                long nextDelay = timeout;
                long now = System.currentTimeMillis();

                for (Map.Entry<String, MultiPacket> entry : multiPacketsMap.entrySet()) {
                    MultiPacket packet = entry.getValue();

                    long time = timeout - (now - packet.getLastAccessedTime());
                    if (time <= 0) {
                        if (!multiPacketListener.receiveTimeout(packet)) {
                            log.warn("<<<<<分包接收超时{}", packet);
                            multiPacketsMap.remove(entry.getKey());
                            packet.release();
                        }
                    } else {
                        nextDelay = Math.min(time, nextDelay);
                    }
                }
                try {
                    Thread.sleep(nextDelay);
                } catch (InterruptedException e) {
                    log.error("MultiPacketListener", e);
                }
            }
        });
        thread.setName("MultiPacketListener");
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setDaemon(true);
        thread.start();
    }
}
