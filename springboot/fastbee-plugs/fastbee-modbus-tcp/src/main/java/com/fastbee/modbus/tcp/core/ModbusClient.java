package com.fastbee.modbus.tcp.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import com.alibaba.fastjson2.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fastbee.base.service.ISessionStore;
import com.fastbee.base.session.Session;
import com.fastbee.common.enums.ServerType;
import com.fastbee.common.extend.core.domin.mq.message.Command;
import com.fastbee.common.extend.core.domin.mq.message.ModbusDevice;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.modbus.tcp.codec.ModbusTcpCodec;
import com.fastbee.modbus.tcp.config.HeartbeatHandler;
import com.fastbee.modbus.tcp.model.RequestContext;
import com.fastbee.modbus.tcp.utils.ModbusFrameUtils;
import com.fastbee.modbus.tcp.utils.ModbusTimeoutHandler;
import com.fastbee.modbus.tcp.utils.TransactionManager;

@Component
public class ModbusClient {
    private static final Logger logger = LoggerFactory.getLogger(ModbusClient.class);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
    private final Bootstrap bootstrap = new Bootstrap();
    private final ISessionStore sessionStore;


    private final ModbusClientHandler handler;
    private final TransactionManager transactionManager;

    public ModbusClient(ISessionStore sessionStore, ModbusClientHandler handler, TransactionManager transactionManager) {
        this.sessionStore = sessionStore;
        this.handler = handler;
        this.transactionManager = transactionManager;
    }

    @PostConstruct
    public void init() {
        logger.info("初始化 Modbus 客户端");
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_RCVBUF, 1024 * 1024)
                .option(ChannelOption.SO_SNDBUF, 1024 * 1024)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                                new IdleStateHandler(60, 60, 60),
                                new ReadTimeoutHandler(30, TimeUnit.SECONDS),
                                new ModbusTcpCodec.Encoder(),
                                new ModbusTcpCodec.Decoder(),
                                new ModbusTimeoutHandler(),
                                handler
                        );
                        ch.pipeline().addLast(new HeartbeatHandler());
                    }
                });

    }

    public CompletableFuture<Void> executeDevice(ModbusDevice modbusDevice) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        ChannelFuture connectFuture = bootstrap.connect(modbusDevice.getDeviceIp(), modbusDevice.getDevicePort());

        connectFuture.addListener((ChannelFutureListener) cf -> {
            if (cf.isSuccess()) {
                logger.info("成功连接到设备 {}", modbusDevice.getDeviceIp());
                cf.channel().attr(AttributeKey.valueOf("device")).set(modbusDevice);
                scheduleNextCommand(cf.channel(), 0);
                deterDeviceStatus(modbusDevice.getSerialNumber());
            } else {
                logger.error("连接设备 {} 失败: {}", modbusDevice.getDeviceId(), cf.cause().getMessage());
                future.completeExceptionally(cf.cause());
            }
        });
        return future;
    }

    private void scheduleNextCommand(Channel channel, int index) {
        ModbusDevice modbusDevice = channel.attr(AttributeKey.<ModbusDevice>valueOf("device")).get();
        List<Command> commands = JSON.parseArray(modbusDevice.getCommand(), Command.class);

        if (index >= commands.size()) {
            // logger.info("[指令完成] 设备 {} 的所有指令已发送，等待最后响应后关闭通道", device.getClientId());
            // 等待500ms确保最后响应到达
            channel.eventLoop().schedule(() -> {
                if (channel.isActive()) {
                    logger.info("[通道关闭] 设备 {} 通道主动关闭", modbusDevice.getDeviceIp());
                    channel.close();
                }
            }, 500, TimeUnit.MILLISECONDS);
            return;
        }

        Command cmd = commands.get(index);
        channel.eventLoop().schedule(() -> {
            if (!channel.isActive()) {
                logger.warn("[通道异常] 设备 {} 通道已关闭，终止指令发送", modbusDevice.getDeviceId());
                return;
            }

            ByteBuf frame = ModbusFrameUtils.buildReadRequest(
                    transactionManager.nextTransactionId(modbusDevice.getDeviceId()),
                    cmd.getAddress(),
                    cmd.getCode(),
                    cmd.getRegister(),
                    cmd.getQuantity()
            );

            int currentId = frame.getUnsignedShort(0);
            transactionManager.putContext(currentId, new RequestContext(modbusDevice.getDeviceId(), cmd.getQuantity(), cmd.getRegister()));

            logger.debug("[指令发送] 设备 {} 第{}/{}条指令 => {}",
                    modbusDevice.getDeviceIp(),
                    index + 1,
                    commands.size(),
                    ByteBufUtil.hexDump(frame));

            channel.writeAndFlush(frame).addListener(f -> {
                if (f.isSuccess()) {
                    logger.debug("[发送成功] 设备 {} 第{}条指令发送成功", modbusDevice.getDeviceIp(), index + 1);
                    // 递归发送下一条指令
                    scheduleNextCommand(channel, index + 1);
                } else {
                    logger.error("[发送失败] 设备 {} 第{}条指令发送失败: {}",
                            modbusDevice.getDeviceIp(),
                            index + 1,
                            f.cause().getMessage());
                    channel.close();
                }
            });
        }, index == 0 ? 0 : 100, TimeUnit.MILLISECONDS); // 第一条立即发送，后续间隔100ms
    }

        private void deterDeviceStatus(String serialNumber) {
            Session session = sessionStore.getSession(serialNumber);
            if (session == null) {
                session = new Session();
                session.setServerType(ServerType.TCP);
                session.setClientId(serialNumber);
                session.setConnected(true);
                sessionStore.storeSession(serialNumber, session);
            }
            session.setLastAccessTime(DateUtils.getTimestamp());
            sessionStore.storeSession(serialNumber, session);

        }

    @PreDestroy
    public void shutdown() {
        logger.info("关闭 Modbus 客户端");
        workerGroup.shutdownGracefully();
    }
}
