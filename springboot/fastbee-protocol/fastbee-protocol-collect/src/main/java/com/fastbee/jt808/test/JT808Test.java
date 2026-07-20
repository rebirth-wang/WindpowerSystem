package com.fastbee.jt808.test;

import static com.fastbee.jt808.transform.ThingsModelConverter.beanToThingsModel;

import java.util.List;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.jt808.JT808Message;
import com.fastbee.jt808.codec.JT808Decoder;
import com.fastbee.jt808.codec.JT808Encoder;
import com.fastbee.jt808.codec.JT808ProtocolService;
import com.fastbee.protocol.util.ExplainUtils;

/**
 * @author gsb
 * @date 2025/10/27 10:28
 */
public class JT808Test {


    public static final JT808ProtocolService coder = new JT808ProtocolService(new JT808Encoder("com.fastbee.jt808"),new JT808Decoder("com.fastbee.jt808"));

    public static void main(String[] args)  throws Exception {
        String hex = "7E0200001C1306250906770003000000000000000302F7FBE60089279C00C500CB00BE250613224856307E";
        JT808Message msg = decode(hex);
        List<ThingsModelSimpleItem> thingsModelSimpleItems = beanToThingsModel(msg);
        for (ThingsModelSimpleItem item : thingsModelSimpleItems) {
            System.out.println(item.getId() + ":" + item.getValue());
        }
    }


    private static JT808Message decode(String hex) {
        System.out.println("====================================================================================\n");
        ExplainUtils explain = new ExplainUtils();
        JT808Message message = null;
        try {
            message = coder.decode(Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(hex)), explain);
        } catch (Exception e) {
        }
        System.out.println(hex);
        return message;
    }
}
