package com.fastbee.bootstrap.tcp.config;

import lombok.extern.slf4j.Slf4j;

import com.fastbee.base.core.HandlerInterceptor;
import com.fastbee.base.session.Session;
import com.fastbee.jt808.JT808Message;
import com.fastbee.jt808.item.JT808;
import com.fastbee.jt808.item.T0001;
import com.fastbee.jt808.item.T0200;

/**
 * 消息拦截应答
 * @author bill
 */
@Slf4j
public class JT808HandlerInterceptor implements HandlerInterceptor<JT808Message> {
    @Override
    public JT808Message notSupported(JT808Message request, Session session) {
        T0001 response = new T0001();
        response.copyThat(request);
        response.setMessageId(JT808.PLAT_COMMON_RESPONSE);
        response.setSerialNo(session.nextSerialNO());

        response.setResponseSerialNo(request.getSerialNo());
        response.setResponseMessageId(request.getMessageId());
        response.setResultCode(T0001.NotSupport);

        log.info("{}\n<<<<-未识别的消息{}\n>>>>-{}", session, request, response);
        return response;
    }

    @Override
    public boolean beforeHandle(JT808Message request, Session session) {
        int messageId = request.getMessageId();
        if (messageId == JT808.REGISTRATION || messageId == JT808.AUTHENTICATION)
            return true;
        if (!session.isRegistered()) {
            log.warn("{}未注册的设备<<<<-{}", session, request);
//            return false;//忽略该消息
        }

        if (messageId == JT808.LOCALTION) {
            T0200 t0200 = (T0200) request;
            if (t0200.getDeviceTime() == null) {
                return false;//忽略没有时间的消息
            }
            //request.setExtData(new T0200Ext(t0200));
            return true;
        }
        return true;
    }

    @Override
    public JT808Message successful(JT808Message request, Session session) {
        T0001 response = new T0001();
        response.copyThat(request);
        response.setMessageId(JT808.PLAT_COMMON_RESPONSE);
        response.setSerialNo(session.nextSerialNO());

        response.setResponseSerialNo(request.getSerialNo());
        response.setResponseMessageId(request.getMessageId());
        response.setResultCode(T0001.Success);

        log.info("{}\n<<<<-{}\n>>>>-{}", session, request, response);
        return response;
    }

    /**
     * 调用之后
     */
    @Override
    public void afterHandle(JT808Message request, JT808Message response, Session session) {
        if (response != null) {
            response.copyThat(request);
            response.setSerialNo(session.nextSerialNO());

            if (response.getMessageId() == 0) {
                response.setMessageId(response.reflectMessageId());
            }
        }
    }

    @Override
    public JT808Message exceptional(JT808Message request, Session session, Exception e) {
        T0001 response = new T0001();
        response.copyThat(request);
        response.setMessageId(JT808.PLAT_COMMON_RESPONSE);
        response.setSerialNo(session.nextSerialNO());

        response.setResponseSerialNo(request.getSerialNo());
        response.setResponseMessageId(request.getMessageId());
        response.setResultCode(T0001.Failure);

        log.warn(session + "\n<<<<-" + request + "\n>>>>-" + response + '\n', e);
        return response;
    }
}
