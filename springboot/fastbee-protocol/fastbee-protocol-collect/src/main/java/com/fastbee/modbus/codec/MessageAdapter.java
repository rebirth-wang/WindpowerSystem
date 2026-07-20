package com.fastbee.modbus.codec;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.annotation.Resource;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fastbee.base.codec.MessageDecoder;
import com.fastbee.base.codec.MessageEncoder;
import com.fastbee.common.ProtocolColl;
import com.fastbee.common.constant.FastBeeConstant;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.mq.DeviceReport;
import com.fastbee.common.extend.core.domin.mq.message.DeviceData;
import com.fastbee.common.extend.core.protocol.Message;
import com.fastbee.common.extend.enums.ScriptEventEnum;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.model.ProductCode;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.iot.service.IScriptService;
import com.fastbee.protocol.base.protocol.IProtocol;
import com.fastbee.protocol.service.IProtocolManagerService;
import com.fastbee.rule.context.MsgContext;

/**
 * 消息编解码适配器
 *
 * @author bill
 */
@Slf4j
@Component
public class MessageAdapter implements MessageDecoder, MessageEncoder {

    @Autowired
    private IProtocolManagerService managerService;
    @Resource
    private IDeviceService deviceService;
    @Resource
    private IScriptService scriptService;

    // 需要规则脚本处理的协议集合
    private static final Set<String> RULE_PROCESS_PROTOCOLS = new HashSet<>(Arrays.asList(
            FastBeeConstant.PROTOCOL.ModbusRtu,
            FastBeeConstant.PROTOCOL.ModbusTcp,
            FastBeeConstant.PROTOCOL.ModbusTcpOverRtu,
            FastBeeConstant.PROTOCOL.FlowMeter,
            FastBeeConstant.PROTOCOL.CH,
            FastBeeConstant.PROTOCOL.RJ45
    ));

    /**
     * modbus消息解码
     *
     * @param buf      原数据
     * @param clientId 客户端id
     * @return 解析后bean
     */
    @Override
    public DeviceReport decode(ByteBuf buf, String clientId) {
        byte[] rawData = ByteBufUtil.getBytes(buf);
        String hexData = ByteBufUtil.hexDump(buf);
        log.info("=>上报hex数据:{}", hexData);

        IProtocol protocol;
        Long productId = null;

        if (clientId == null) {
            protocol = determineProtocolByData(hexData, new String(rawData, StandardCharsets.UTF_8));
        } else {
            ProtocolColl protocolColl = getProtocolByClientId(clientId);
            protocol = protocolColl.getProtocol();
            productId = protocolColl.getProductId();

            // 应用规则脚本处理 TODO - 16进制数据不能这样处理，JSON ASCll可以
            //buf = applyRuleProcessing(clientId,protocolColl.getProtocolCode(), hexData,
            //        new String(rawData, StandardCharsets.UTF_8));
        }

        DeviceData deviceData = buildDeviceData(buf, productId, clientId, rawData);
        return protocol.decode(deviceData, clientId);
    }

    /**
     * modbus消息编码
     *
     * @param message modbusbean
     * @return 编码指令
     */
    @Override
    public ByteBuf encode(Message message, String clientId) {
        String serialNumber = StringUtils.isNotEmpty(clientId) ? clientId : message.getSerialNumber();
        ProtocolColl protocolColl = getProtocolByClientId(serialNumber);

        byte[] encodedData = protocolColl.getProtocol().encodeCallBack(message);

        log.info("应答设备,clientId=[{}],指令=[{}]", serialNumber, ByteBufUtil.hexDump(encodedData));
        return Unpooled.wrappedBuffer(encodedData);
    }

    /**
     * 根据数据特征判断协议类型
     */
    private IProtocol determineProtocolByData(String hexData, String textData) {
        if (StringUtils.isEmpty(hexData)) {
            return getDefaultProtocol();
        }

        // 使用策略模式简化协议判断
        ProtocolDetector detector = new ProtocolDetector(hexData, textData);
        String protocolCode = detector.detect();

        return managerService.getProtocolByProtocolCode(protocolCode);
    }

    /**
     * 根据客户端ID获取协议配置
     */
    private ProtocolColl getProtocolByClientId(String clientId) {
        ProductCode productCode = deviceService.getProtocolBySerialNumber(clientId);
        if (Objects.isNull(productCode) || StringUtils.isEmpty(productCode.getProtocolCode())) {
            productCode = createDefaultProductCode();
        }
        return buildProtocolColl(productCode);
    }

    /**
     * 应用规则脚本处理
     */
    private ByteBuf applyRuleProcessing(String clientId, String protocolCode, String hexData, String textData) {
        if (!RULE_PROCESS_PROTOCOLS.contains(protocolCode)) {
            return Unpooled.wrappedBuffer(textData.getBytes(StandardCharsets.UTF_8));
        }

        String message = RULE_PROCESS_PROTOCOLS.contains(protocolCode) ? hexData : textData;
        MsgContext context = scriptService.processRuleScript(clientId, ScriptEventEnum.DEVICE_REPORT.getType(), null, message);

        if (Objects.nonNull(context) && StringUtils.isNotEmpty(context.getPayload())) {
            return Unpooled.wrappedBuffer(context.getPayload().getBytes(StandardCharsets.UTF_8));
        }

        return Unpooled.wrappedBuffer(message.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 构建设备数据对象
     */
    private DeviceData buildDeviceData(ByteBuf buf, Long productId, String clientId, byte[] rawData) {
        return DeviceData.builder()
                .buf(buf)
                .productId(productId)
                .serialNumber(clientId)
                .data(rawData)
                .build();
    }

    /**
     * 构建协议集合
     */
    private ProtocolColl buildProtocolColl(ProductCode productCode) {
        try {
            IProtocol protocol = managerService.getProtocolByProtocolCode(productCode.getProtocolCode());
            ProtocolColl protocolColl = new ProtocolColl();
            protocolColl.setProtocol(protocol);
            protocolColl.setProductId(productCode.getProductId());
            protocolColl.setProtocolCode(productCode.getProtocolCode());
            return protocolColl;
        } catch (Exception e) {
            log.error("构建协议失败, protocolCode: {}", productCode.getProtocolCode(), e);
            throw new ServiceException("协议配置错误: " + e.getMessage());
        }
    }

    /**
     * 创建默认产品编码
     */
    private ProductCode createDefaultProductCode() {
        ProductCode productCode = new ProductCode();
        productCode.setProtocolCode(FastBeeConstant.PROTOCOL.ModbusRtu);
        return productCode;
    }

    /**
     * 获取默认协议
     */
    private IProtocol getDefaultProtocol() {
        return managerService.getProtocolByProtocolCode(FastBeeConstant.PROTOCOL.ModbusRtu);
    }

    /**
     * 协议检测器 - 内部类封装协议检测逻辑
     */
    private static class ProtocolDetector {
        private final String hexData;
        private final String textData;

        public ProtocolDetector(String hexData, String textData) {
            this.hexData = hexData;
            this.textData = textData;
        }

        public String detect() {
            if (hexData.startsWith("68") && hexData.endsWith("16")) {
                return FastBeeConstant.PROTOCOL.FlowMeter;
            } else if (hexData.startsWith("7e") && hexData.endsWith("7e")) {
                //这里兼容一下JT808协议
                if (hexData.substring(2, 4).equals("80")){
                    return FastBeeConstant.PROTOCOL.ModbusRtu;
                }else {
                    return "JT808";
                }
            } else if (textData.startsWith("{") && textData.endsWith("}")) {
                return FastBeeConstant.PROTOCOL.SGZ;
            } else if (textData.startsWith("[") && textData.endsWith("]")) {
                return FastBeeConstant.PROTOCOL.JsonArray;
            } else if (hexData.startsWith("fedc")) {
                return FastBeeConstant.PROTOCOL.CH;
            } else if (hexData.startsWith("500100")) {
                return FastBeeConstant.PROTOCOL.RJ45;
            } else if (hexData.startsWith("534d")){
                return "SM0101";
            } else {
                return FastBeeConstant.PROTOCOL.ModbusRtu;
            }
        }
    }
}
