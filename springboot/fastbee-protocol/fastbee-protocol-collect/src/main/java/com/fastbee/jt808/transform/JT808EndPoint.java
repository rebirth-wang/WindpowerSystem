package com.fastbee.jt808.transform;

import static com.fastbee.jt808.item.JT808.*;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fastbee.base.core.annotation.Node;
import com.fastbee.base.core.annotation.PakMapping;
import com.fastbee.base.session.Session;
import com.fastbee.common.extend.core.domin.mq.DeviceReport;
import com.fastbee.jt808.JT808Message;
import com.fastbee.jt808.item.*;

/**
 * 报文消息映射处理
 * @author gsb
 * @date 2025/10/25 14:08
 */
@Node
@Component
@Slf4j
public class JT808EndPoint {

    private AtomicInteger serialNo = new AtomicInteger();

    @PakMapping(types = JT808.DEVICE_COMMON_RESPONSE, desc = "设备通用应答")
    public DeviceReport T0001(T0001 message, Session session){
        session.response(message);
        return null;
    }

    @PakMapping(types = JT808.终端心跳, desc = "终端心跳")
    public void T0002(DeviceReport message, Session session){

    }

    @PakMapping(types = JT808.终端注销, desc = "终端注销")
    public void T0003(DeviceReport message, Session session){
        session.invalidate();
    }


    @PakMapping(types = JT808.REGISTRATION, desc = "终端注册")
    public T8100 T0100(DeviceReport message, Session session){
        session.register(message);
        T0100 t0100 = (T0100) message.getMessage();
        T8100 t8100 = new T8100();
        t8100.setResponseSerialNo(t0100.getSerialNo());
        t8100.setToken(t0100.getDeviceId() + "," + t0100.getPlateNo());
        t8100.setResultCode(T8100.Success);
        return t8100;
    }

    @PakMapping(types = AUTHENTICATION, desc = "终端鉴权")
    public T0001 T0102(DeviceReport message, Session session) {
        session.register(message);
        T0102 t0102 = (T0102) message.getMessage();
        T0001 result = new T0001();
        result.setResponseSerialNo(t0102.getSerialNo());
        result.setResponseMessageId(t0102.getMessageId());
        result.setResultCode(T0001.Success);
        return result;
    }

    @PakMapping(types = DEVICE_REGISTER_RESPONSE, desc = "终端注册应答")
    public T0001 T8100 (T8100 message) {
        return buildResult(message, T0001.Success);
    }

    //@PakMapping(types = LOCALTION, desc = "位置信息汇报")
    //public void T0200(List<T0200> list) {
    //}

    @PakMapping(types = 定位数据批量上传, desc = "定位数据批量上传")
    public void T0704(T0704 message) {
    }


    @PakMapping(types = 位置信息查询应答, desc = "位置信息查询应答")
    public void T0201(T0201 message, Session session) {
        session.response(message);
    }


    private T0001 buildResult(JT808Message message, int resultCode) {
        T0001 result = new T0001();
        result.copyThat(message);
        result.setMessageId(DEVICE_COMMON_RESPONSE);
        result.setClientId(message.getClientId());
        result.setSerialNo(serialNo.addAndGet(1));

        result.setResponseSerialNo(message.getSerialNo());
        result.setResponseMessageId(message.getMessageId());
        result.setResultCode(resultCode);
        return result;
    }

}
