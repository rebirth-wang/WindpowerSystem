package com.fastbee.jt808.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import com.fastbee.base.session.Session;
import com.fastbee.common.annotation.SysProtocol;
import com.fastbee.common.extend.core.domin.mq.DeviceReport;
import com.fastbee.common.extend.core.domin.mq.MQSendMessageBo;
import com.fastbee.common.extend.core.domin.mq.message.DeviceData;
import com.fastbee.common.extend.core.domin.mq.message.FunctionCallBackBo;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.jt808.JT808Message;
import com.fastbee.jt808.transform.ThingsModelConverter;
import com.fastbee.protocol.base.protocol.IProtocol;
import com.fastbee.protocol.util.ExplainUtils;

/**
 * JT808协议
 * @author gsb
 * @date 2025/10/24 10:05
 */
@Slf4j
@Component
@SysProtocol(name = "JT808协议", protocolCode = "JT808", description = "JT808通讯协议")
public class JT808ProtocolService implements IProtocol {


    private final JT808Encoder messageEncoder;
    private final  JT808Decoder messageDecoder;

    public JT808ProtocolService(JT808Encoder messageEncoder, JT808Decoder messageDecoder) {
        this.messageEncoder = messageEncoder;
        this.messageDecoder = messageDecoder;
    }


    @Override
    public DeviceReport decode(DeviceData data, String clientId) {
        JT808Message message = null;
        DeviceReport report = new DeviceReport();
        try {
             message = messageDecoder.decode(data.getBuf(), new ExplainUtils());
            List<ThingsModelSimpleItem> items = ThingsModelConverter.beanToThingsModel(message);
            report.setThingsModelSimpleItem(items);
            report.setMessage(message);
            report.setMgId(String.valueOf(message.getMessageId()));
            report.setSerialNumber(message.getClientId());
            report.setSources(Hex.encodeHexString(data.getData()));
            return report;
        }catch (Exception e){
            log.error("JT808解码异常",e);
        }

        return null;
    }

    @Override
    public FunctionCallBackBo encode(MQSendMessageBo message) {
        FunctionCallBackBo callBackBo = new FunctionCallBackBo();
        JT808Message jt808Message = new JT808Message();
        int messageId = jt808Message.reflectMessageId();
        if (messageId != 0) jt808Message.setMessageId(messageId);
        jt808Message.setSerialNumber(message.getSerialNumber());
        jt808Message.setSerialNo(100);
        ByteBuf output = messageEncoder.encode(jt808Message);
        callBackBo.setMessage(ByteBufUtil.getBytes( output));
        return callBackBo;
    }

    @Override
    public byte[] encodeCallBack(Object message) {
        JT808Message jt808Message = (JT808Message) message;
        ByteBuf outPut = messageEncoder.encode(jt808Message);
        return ByteBufUtil.getBytes(outPut);
    }

    public ByteBuf encode(JT808Message message, ExplainUtils explain) {
        return messageEncoder.encode(message, explain);
    }

    public JT808Message decode(ByteBuf input, ExplainUtils explain) {
        return messageDecoder.decode(input, explain);
    }

    public ByteBuf encode(JT808Message message) {
        return messageEncoder.encode(message);
    }

    public JT808Message decode(ByteBuf input) {
        return messageDecoder.decode(input);
    }



    public void encodeLog(Session session, JT808Message message, ByteBuf output) {
        if (log.isInfoEnabled())
            log.info("{}\n>>>>>-{},hex[{}]", session, message, ByteBufUtil.hexDump(output));
    }

    public void decodeLog(Session session, JT808Message message, ByteBuf input) {
        if (log.isInfoEnabled())
            log.info("{}\n<<<<<-{},hex[{}]", session, message, ByteBufUtil.hexDump(input, 0, input.writerIndex()));
    }


}
