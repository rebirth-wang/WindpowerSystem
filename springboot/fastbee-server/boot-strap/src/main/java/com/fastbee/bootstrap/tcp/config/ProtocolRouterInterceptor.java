package com.fastbee.bootstrap.tcp.config;

import lombok.extern.slf4j.Slf4j;

import com.fastbee.base.core.HandlerInterceptor;
import com.fastbee.base.session.Session;
import com.fastbee.common.extend.core.domin.mq.DeviceReport;
import com.fastbee.common.extend.core.protocol.Message;
import com.fastbee.jt808.JT808Message;

/**
 * 协议路由拦截器
 * @author bill
 * @date 2025/10/28
 */
@Slf4j
public class ProtocolRouterInterceptor implements HandlerInterceptor<Message> {

    private final TcpHandlerInterceptor modbusInterceptor;
    private final JT808HandlerInterceptor jt808Interceptor;

    public ProtocolRouterInterceptor(TcpHandlerInterceptor modbusInterceptor,
                                   JT808HandlerInterceptor jt808Interceptor) {
        this.modbusInterceptor = modbusInterceptor;
        this.jt808Interceptor = jt808Interceptor;
    }

    // 协议检测逻辑
    private boolean isJT808Protocol(Message request) {
        // 根据实际业务逻辑检测协议类型
        DeviceReport report = (DeviceReport) request;
        return report.getMessage() instanceof JT808Message;

    }

    @Override
    public Message notSupported(Message request, Session session) {
        if (isJT808Protocol(request)) {
            // 对于JT808消息，需要确保类型匹配
            DeviceReport report = (DeviceReport) request;
            if (report.getMessage() instanceof JT808Message) {
                return jt808Interceptor.notSupported((JT808Message) report.getMessage(), session);
            } else {
                // 如果消息不是JT808Message但被识别为JT808协议，可能需要转换
                log.warn("消息类型不匹配，期望JT808Message但收到: {}", request.getClass().getSimpleName());
                return modbusInterceptor.notSupported(request, session);
            }
        } else {
            return modbusInterceptor.notSupported(request, session);
        }
    }

    @Override
    public boolean beforeHandle(Message request, Session session) {
        if (isJT808Protocol(request)) {
            DeviceReport report = (DeviceReport) request;
            if (report.getMessage() instanceof JT808Message) {
                return jt808Interceptor.beforeHandle((JT808Message) report.getMessage(), session);
            } else {
                log.warn("消息类型不匹配，期望JT808Message但收到: {}", request.getClass().getSimpleName());
                return modbusInterceptor.beforeHandle(request, session);
            }
        } else {
            return modbusInterceptor.beforeHandle(request, session);
        }
    }

    @Override
    public Message successful(Message request, Session session) {
        if (isJT808Protocol(request)) {
            DeviceReport report = (DeviceReport) request;
            if (report.getMessage() instanceof JT808Message) {
                return jt808Interceptor.successful((JT808Message) report.getMessage(), session);
            } else {
                log.warn("消息类型不匹配，期望JT808Message但收到: {}", request.getClass().getSimpleName());
                return modbusInterceptor.successful(request, session);
            }
        } else {
            return modbusInterceptor.successful(request, session);
        }
    }

    @Override
    public void afterHandle(Message request, Message response, Session session) {
        if (isJT808Protocol(request)) {
            DeviceReport requestReport = (DeviceReport) request;

            if (requestReport.getMessage() instanceof JT808Message && response instanceof JT808Message) {
                jt808Interceptor.afterHandle((JT808Message) requestReport.getMessage(),
                        (JT808Message)response, session);
            } else {
                log.warn("JT808消息类型不匹配");
                modbusInterceptor.afterHandle(request, response, session);
            }
        } else {
            modbusInterceptor.afterHandle(request, response, session);
        }
    }

    @Override
    public Message exceptional(Message request, Session session, Exception e) {
        if (isJT808Protocol(request)) {
            DeviceReport requestReport = (DeviceReport) request;
            if (requestReport.getMessage() instanceof JT808Message) {
                return jt808Interceptor.exceptional((JT808Message) requestReport.getMessage(), session, e);
            } else {
                log.warn("消息类型不匹配，期望JT808Message但收到: {}", requestReport.getMessage().getClass().getSimpleName());
                return modbusInterceptor.exceptional(request, session, e);
            }
        } else {
            return modbusInterceptor.exceptional(request, session, e);
        }
    }
}
