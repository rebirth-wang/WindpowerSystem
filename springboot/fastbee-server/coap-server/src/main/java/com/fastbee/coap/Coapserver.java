package com.fastbee.coap;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fastbee.coap.handler.*;
import com.fastbee.coap.server.CoapServerChannelInitializer;
import com.fastbee.coap.server.ResourceRegistry;
import com.fastbee.server.Server;

@Slf4j
@Component
public class Coapserver extends Server {
    @Override
    protected AbstractBootstrap initialize() {
        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory(config.name, Thread.MAX_PRIORITY));
        if (config.businessCore > 0) {
            businessService = new ThreadPoolExecutor(config.businessCore, config.businessCore, 1L,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new DefaultThreadFactory(config.name, true, Thread.NORM_PRIORITY));
        }
        //注册数据路由
        ResourceRegistry resourceRegistry = new ResourceRegistry();
        resourceRegistry.register(new TimeResourceHandler("/utc-time"));
        resourceRegistry.register(new ConnectionHandler("/connection"));
        resourceRegistry.register(new KeepaliveHandler("/keepalive"));
        resourceRegistry.register(new InfoHandler("/info/post"));
        resourceRegistry.register(new PropertyHandler("/property/post"));
        resourceRegistry.register(new FunctionHandler("/function/post"));
        resourceRegistry.register(new PropertyHandler("/event/post"));
        return new Bootstrap()
                .group(bossGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new CoapServerChannelInitializer(resourceRegistry));
    }
}
