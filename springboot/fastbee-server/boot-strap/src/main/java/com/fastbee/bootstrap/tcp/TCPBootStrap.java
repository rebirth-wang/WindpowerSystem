package com.fastbee.bootstrap.tcp;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.fastbee.base.codec.Delimiter;
import com.fastbee.base.core.HandlerMapping;
import com.fastbee.base.session.SessionManager;
import com.fastbee.bootstrap.tcp.config.JT808HandlerInterceptor;
import com.fastbee.bootstrap.tcp.config.ProtocolRouterInterceptor;
import com.fastbee.bootstrap.tcp.config.TcpHandlerInterceptor;
import com.fastbee.common.enums.ServerType;
import com.fastbee.modbus.codec.MessageAdapter;
import com.fastbee.server.Server;
import com.fastbee.server.config.NettyConfig;

/**
 * TCP服务端启动
 *
 * @author bill
 */
@Order(11)
@Configuration
@ConditionalOnProperty(value = "server.tcp.enabled", havingValue = "true")//设置是否启动
@ConfigurationProperties(value = "server.tcp")
@Data
public class TCPBootStrap {

    private int port;
    private int keepAlive;
    private byte delimiter;
    private MessageAdapter messageAdapter;
    private HandlerMapping handlerMapping;
    private TcpHandlerInterceptor handlerInterpolator;
    private SessionManager sessionManager;
    private JT808HandlerInterceptor jt808HandlerInterceptor;


    public TCPBootStrap(MessageAdapter messageAdapter,
                        HandlerMapping handlerMapping,
                        TcpHandlerInterceptor interpolator,
                        SessionManager sessionManager,
                        JT808HandlerInterceptor jt808HandlerInterceptor) {
        this.messageAdapter = messageAdapter;
        this.handlerMapping = handlerMapping;
        this.handlerInterpolator = interpolator;
        this.sessionManager = sessionManager;
        this.jt808HandlerInterceptor = jt808HandlerInterceptor;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server TCPServer() {
        ProtocolRouterInterceptor protocolInterceptor =
                new ProtocolRouterInterceptor(handlerInterpolator, jt808HandlerInterceptor);

        return NettyConfig.custom()
                .setIdleStateTime(keepAlive, 0, 0)
                .setPort(port)
                //设置报文最大长度  modbus-rtu
                .setMaxFrameLength(100)
                .setDelimiters(new Delimiter(new byte[]{0x7e}, true))
                .setDecoder(messageAdapter)
                .setEncoder(messageAdapter)
                .setHandlerMapping(handlerMapping)
                .setHandlerInterceptor(protocolInterceptor)
                .setSessionManager(sessionManager)
                .setName(ServerType.TCP.getDes())
                .setType(ServerType.TCP)
                .build();
    }
}
