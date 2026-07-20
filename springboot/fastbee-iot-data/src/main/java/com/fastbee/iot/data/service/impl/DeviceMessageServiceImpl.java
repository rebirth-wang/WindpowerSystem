package com.fastbee.iot.data.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fastbee.common.constant.FastBeeConstant;
import com.fastbee.common.enums.DeviceStatus;
import com.fastbee.common.enums.ServerType;
import com.fastbee.common.enums.TopicType;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.mq.DeviceReportBo;
import com.fastbee.common.extend.core.domin.mq.MQSendMessageBo;
import com.fastbee.common.extend.core.domin.mq.message.*;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.common.extend.core.protocol.modbus.ModbusCode;
import com.fastbee.common.extend.enums.EnableEnum;
import com.fastbee.common.extend.enums.ModbusConfigTypeEnum;
import com.fastbee.common.extend.enums.ModbusJobTypeEnum;
import com.fastbee.common.extend.utils.modbus.ModbusUtils;
import com.fastbee.common.utils.BitUtils;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.gateway.CRC16Utils;
import com.fastbee.common.utils.gateway.mq.TopicsUtils;
import com.fastbee.common.utils.json.JsonUtils;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.cache.ITSLCache;
import com.fastbee.iot.data.service.IDeviceMessageService;
import com.fastbee.iot.data.service.IMqttMessagePublish;
import com.fastbee.iot.domain.ModbusConfig;
import com.fastbee.iot.enums.DeviceType;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.iot.model.ThingsModels.ValueItem;
import com.fastbee.iot.model.modbus.ModbusPollJob;
import com.fastbee.iot.model.vo.DeviceStatusVO;
import com.fastbee.iot.model.vo.ModbusJobVO;
import com.fastbee.iot.model.vo.VariableReadVO;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.iot.service.IModbusJobService;
import com.fastbee.modbus.codec.ModbusEncoder;
import com.fastbee.modbus.codec.ModbusProtocol;
import com.fastbee.modbus.model.ModbusRtu;
import com.fastbee.modbus.tcp.config.ModbusScheduler;
import com.fastbee.modbustcp.codec.overRtu.ModbusTcpOverRtuEncoder;
import com.fastbee.modbustcp.codec.overRtu.ModbusTcpOverRtuProtocol;
import com.fastbee.modbustcp.codec.tcpIp.ModbusTcpIpEncoder;
import com.fastbee.modbustcp.model.ModbusTcp;
import com.fastbee.mq.producer.MessageProducer;
import com.fastbee.mqttclient.PubMqttClient;

@Service
@Slf4j
public class DeviceMessageServiceImpl implements IDeviceMessageService {

    @Resource
    private PubMqttClient mqttClient;
    @Resource
    private ModbusEncoder modbusMessageEncoder;
    @Resource
    private IDeviceService deviceService;
    @Resource
    private TopicsUtils topicsUtils;
    @Resource
    private ITSLCache itslCache;
    @Resource
    private ModbusProtocol modbusProtocol;
    @Resource
    private IModbusJobService modbusJobService;
    @Resource
    private ModbusTcpOverRtuProtocol modbusTcpOverRtuProtocol;
    @Resource
    private ModbusTcpOverRtuEncoder modbusTcpOverRtuEncoder;
    @Resource
    private IMqttMessagePublish mqttMessagePublish;
    @Resource
    private ModbusScheduler modbusScheduler;
    @Resource
    private IDeviceCache deviceCache;
    @Resource
    private ModbusTcpIpEncoder modbusTcpIpEncoder;

    @Override
    public void messagePost(DeviceMessage deviceMessage) {
        String topicName = deviceMessage.getTopicName();
        String serialNumber = deviceMessage.getSerialNumber();
        DeviceStatusVO deviceStatusVO = deviceService.selectDeviceStatusAndTransportStatus(serialNumber);
        if (deviceStatusVO == null) {
            log.warn("messagePost: 设备不存在或未激活，serialNumber={}", serialNumber);
            return;
        }
        String transport = deviceStatusVO.getTransport();
        TopicType type = TopicType.getType(topicName);
        topicName = topicsUtils.buildTopic(deviceStatusVO.getProductId(), serialNumber,type);
        switch (type){
            case FUNCTION_GET:
                if (transport.equals(ServerType.MQTT.getCode())){
                    if (FastBeeConstant.PROTOCOL.ModbusRtu.equals(deviceStatusVO.getProtocolCode())) {
                        mqttClient.publish(ByteBufUtil.decodeHexDump(deviceMessage.getMessage().toString()), topicName, false, 0);
                    } else {
                        mqttClient.publish(0, false, topicName, deviceMessage.getMessage().toString());
                    }
                }else if (transport.equals(ServerType.TCP.getCode())){
                    //处理TCP下发
                    ModbusPollMsg modbusPollMsg = new ModbusPollMsg();
                    modbusPollMsg.setSerialNumber(serialNumber);
                    modbusPollMsg.setProductId(deviceStatusVO.getProductId());
                    List<String> commandList = new ArrayList<>();
                    commandList.add(deviceMessage.getMessage().toString());
                    modbusPollMsg.setCommandList(commandList);
                    modbusPollMsg.setTransport(deviceStatusVO.getTransport());
                    if (deviceStatusVO.getStatus() != DeviceStatus.ONLINE.getType()){
                        log.info("设备：[{}],不在线",modbusPollMsg.getSerialNumber());
                        return;
                    }
                    ModbusPollJob modbusPollJob = new ModbusPollJob();
                    modbusPollJob.setPollMsg(modbusPollMsg);
                    modbusPollJob.setType(ModbusJobTypeEnum.SINGLE.getType());
                    MessageProducer.sendPropFetch(modbusPollJob);
                }
                break;
            case PROPERTY_POST:
                //下发的不经过mqtt或TCP直接转发到数据处理模块
                DeviceReportBo reportBo = DeviceReportBo.builder()
                        .serverType(ServerType.explain(transport))
                        .data(BitUtils.hexStringToByteArray(deviceMessage.getMessage().toString()))
                        .platformDate(DateUtils.getNowDate())
                        .serialNumber(serialNumber)
                        .topicName(topicName).build();
                MessageProducer.sendPublishMsg(reportBo);
                break;
        }
    }

    @Override
    public String messageEncode(ModbusRtu modbusRtu) {
        ByteBuf out;
        if (FastBeeConstant.PROTOCOL.ModbusTcpOverRtu.equals(modbusRtu.getProtocolCode())) {
            ModbusTcp modbusTcp = modbusTcpOverRtuEncoder.change(modbusRtu);
            modbusTcp.setTransactionIdentifier(0);
            out = modbusTcpOverRtuEncoder.encode(modbusTcp);
        } else if (FastBeeConstant.PROTOCOL.ModbusTcp.equals(modbusRtu.getProtocolCode())) {
            ModbusTcp modbusTcp = modbusTcpIpEncoder.change(modbusRtu);
            modbusTcp.setTransactionIdentifier(0);
            out = modbusTcpOverRtuEncoder.encode(modbusTcp);
        } else {
            //兼容15、16功能码
            if (modbusRtu.getCode() == ModbusCode.Write10.getCode()){
                //计算:字节数=2*N；N为寄存器个数
                modbusRtu.setBitCount(2 * modbusRtu.getCount());
            }else if (modbusRtu.getCode() == ModbusCode.Write0F.getCode()){
                //计算:字节数=N/8 余数为0是 N需要再+1。N是线圈个数
                int i = modbusRtu.getCount() / 8;
                if (modbusRtu.getCount() % 8!= 0){
                    i++;
                }
                modbusRtu.setBitCount(i);
                //计算线圈值，前端返回二进制的字符串，需要将高低位先翻转，在转化为16进制
                String reverse = StringUtils.reverse(modbusRtu.getBitString());
                modbusRtu.setBitData(BitUtils.string2bytes(reverse));
            }
            out = modbusMessageEncoder.encode(modbusRtu);
        }
        byte[] result = new byte[out.writerIndex()];
        out.readBytes(result);
        ReferenceCountUtil.release(out);
        if (FastBeeConstant.PROTOCOL.ModbusTcpOverRtu.equals(modbusRtu.getProtocolCode()) || FastBeeConstant.PROTOCOL.ModbusTcp.equals(modbusRtu.getProtocolCode())) {
            return ByteBufUtil.hexDump(result);
        }
        return ByteBufUtil.hexDump(CRC16Utils.AddCRC(result));
    }

    @Override
    public List<ThingsModelSimpleItem> messageDecode(DeviceMessage deviceMessage) {
        // ProductCode productCode = productService.getProtocolBySerialNumber(deviceMessage.getSerialNumber());
        // ByteBuf buf = Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(deviceMessage.getMessage()));
        // DeviceData deviceData = DeviceData.builder()
        //         .buf(buf)
        //         .productId(productCode.getProductId())
        //         .serialNumber(productCode.getSerialNumber())
        //         .data(ByteBufUtil.getBytes(buf))
        //         .build();
        // return modbusRtuPakProtocol.decodeMessage(deviceData, deviceMessage.getSerialNumber());
        return null;
    }

    /**
     * 变量读取
     * @param readVO
     */
    @Override
    public void readVariableValue(VariableReadVO readVO){
        String serialNumber = readVO.getSerialNumber();
        assert !Objects.isNull(serialNumber) : "设备编号为空";
//        DeviceAndProtocol deviceAndProtocol = deviceService.selectProtocolBySerialNumber(serialNumber);
        DeviceMetaData deviceMetaDataCache = deviceCache.getDeviceMetaDataCache(readVO.getSerialNumber());
        Optional.ofNullable(deviceMetaDataCache).orElseThrow(() -> new ServiceException(StringUtils.format(MessageUtils.message("mqtt.service.send.device.not.exist"), readVO.getSerialNumber())));
        String address = deviceMetaDataCache.getSubGateway().getAddress();
        Long subProductId = deviceMetaDataCache.getDevice().getProductId();
        Integer subDeviceType = deviceMetaDataCache.getProduct().getDeviceType();
        // 处理子设备，用绑定网关下发指令
        if (DeviceType.SUB_GATEWAY.getCode() == subDeviceType) {
            Optional.ofNullable(deviceMetaDataCache.getSubGateway().getAddress()).orElseThrow(() -> new ServiceException(StringUtils.format(MessageUtils.message("mqtt.service.send.device.not.exist"), readVO.getSerialNumber())));
            DeviceMetaData gwDeviceMetaDataCache = deviceCache.getDeviceMetaDataCache(deviceMetaDataCache.getSubGateway().getParentClientId());
            Optional.ofNullable(gwDeviceMetaDataCache).orElseThrow(() -> new ServiceException(StringUtils.format(MessageUtils.message("mqtt.service.send.device.not.exist"), readVO.getSerialNumber())));
            serialNumber = gwDeviceMetaDataCache.getDevice().getSerialNumber();
            deviceMetaDataCache = gwDeviceMetaDataCache;
        }
        if (!(FastBeeConstant.PROTOCOL.ModbusRtu.equals(deviceMetaDataCache.getProduct().getProtocolCode()) || FastBeeConstant.PROTOCOL.ModbusTcpOverRtu.equals(deviceMetaDataCache.getProduct().getProtocolCode())
                || FastBeeConstant.PROTOCOL.ModbusTcp.equals(deviceMetaDataCache.getProduct().getProtocolCode()))) {
            throw new ServiceException("非modbus协议请先配置主动采集协议");
        }
        Integer type = readVO.getType();
        type = Objects.isNull(type) ? ModbusJobTypeEnum.SINGLE.getType() : type;
        List<ModbusDevice> modbusDeviceList = new ArrayList<>();
        ModbusDevice modbusDevice = new ModbusDevice();
        modbusDevice.setDeviceId(deviceMetaDataCache.getDevice().getDeviceId());
        modbusDevice.setSerialNumber(deviceMetaDataCache.getDevice().getSerialNumber());
        modbusDevice.setDeviceIp(deviceMetaDataCache.getDevice().getDeviceIp());
        modbusDevice.setStatus(deviceMetaDataCache.getDevice().getStatus());
        if (Objects.nonNull(deviceMetaDataCache.getDevice().getDevicePort())) {
            modbusDevice.setDevicePort(deviceMetaDataCache.getDevice().getDevicePort());
        }
        if (ModbusJobTypeEnum.SINGLE.getType().equals(type)){
            //单个变量获取
            String identifier = readVO.getIdentifier();
            ThingsModelValueItem thingModels = itslCache.getSingleThingModels(subProductId, identifier);
            if (thingModels == null) {
                throw new ServiceException(StringUtils.format(MessageUtils.message("modbus.point.not.config")));
            }
            ModbusConfig config = thingModels.getConfig();
            if (Objects.isNull(config)) {
                throw new ServiceException(MessageUtils.message("modbus.point.not.config"));
            }
            thingModels.getConfig().setModbusCode(ModbusUtils.getReadModbusCode(config.getType(),config.getIsReadonly()));
            if (subDeviceType == DeviceType.SUB_GATEWAY.getCode()) {
                //这里获取绑定网关时，设置的子设备地址
                thingModels.getConfig().setAddress(StringUtils.isNotEmpty(address) ? address : deviceMetaDataCache.getModbusParams().getAddress());
            } else if (DeviceType.DIRECT_DEVICE.getCode() == subDeviceType) {
                if (StringUtils.isEmpty(config.getAddress())) {
                    thingModels.getConfig().setAddress(deviceMetaDataCache.getModbusParams().getAddress());
                } else {
                    address = config.getAddress();
                }
            }
            if (FastBeeConstant.PROTOCOL.ModbusTcp.equals(deviceMetaDataCache.getProduct().getProtocolCode())) {
                Command command = new Command();
                command.setAddress(Integer.parseInt(address));
                command.setRegister(thingModels.getConfig().getRegister());
                ModbusConfig modbusConfig = thingModels.getConfig();
                int code;
                if (ModbusConfigTypeEnum.IO.getType().equals(modbusConfig.getType())) {
                    code = EnableEnum.ENABLE.getType().equals(modbusConfig.getIsReadonly()) ? 2 : 1;
                } else {
                    code = EnableEnum.ENABLE.getType().equals(modbusConfig.getIsReadonly()) ? 4 : 3;
                }
                command.setCode(code);
                command.setQuantity(1);
                List<Command> commandList = new ArrayList<>();
                commandList.add(command);
                modbusDevice.setCommands(commandList);
                modbusDevice.setCommand(JSON.toJSONString(commandList));
                modbusDeviceList.add(modbusDevice);
                modbusScheduler.processBatch(modbusDeviceList);
                return;
            }

            MQSendMessageBo messageBo = new MQSendMessageBo();
            messageBo.setThingsModel(JSON.toJSONString(thingModels));
            messageBo.setSerialNumber(readVO.getSerialNumber());
            messageBo.setParentSerialNumber(readVO.getParentSerialNumber());
            FunctionCallBackBo encode;
            if (FastBeeConstant.PROTOCOL.ModbusTcpOverRtu.equals(deviceMetaDataCache.getProduct().getProtocolCode())) {
                encode = modbusTcpOverRtuProtocol.encode(messageBo);
            } else {
                encode = modbusProtocol.encode(messageBo);
            }
            List<String> commandList = new ArrayList<>();
            commandList.add(encode.getSources());
            ModbusPollMsg modbusPollMsg = new ModbusPollMsg();
            modbusPollMsg.setSerialNumber(serialNumber);
            modbusPollMsg.setProductId(deviceMetaDataCache.getProduct().getProductId());
            modbusPollMsg.setCommandList(commandList);
            DeviceStatusVO deviceStatusVO = deviceService.selectDeviceStatusAndTransportStatus(serialNumber);
            if (deviceStatusVO == null) {
                log.warn("readVariableValue: 设备不存在，serialNumber={}", serialNumber);
                return;
            }
            modbusPollMsg.setTransport(deviceStatusVO.getTransport());
            if (deviceStatusVO.getStatus() != DeviceStatus.ONLINE.getType()){
                log.info("设备：[{}],不在线",modbusPollMsg.getSerialNumber());
                return;
            }
            ModbusPollJob job = new ModbusPollJob();
            job.setPollMsg(modbusPollMsg);
            job.setType(ModbusJobTypeEnum.SINGLE.getType());
            MessageProducer.sendPropFetch(job);
        }else {
            List<ModbusJobVO> modbusJobVOList = modbusJobService.selectDevicesJobByDeviceType(serialNumber);
            if (FastBeeConstant.PROTOCOL.ModbusTcp.equals(deviceMetaDataCache.getProduct().getProtocolCode())) {
                if (modbusJobVOList == null || modbusJobVOList.isEmpty()) {
                    log.warn("readVariableValue: modbusJob列表为空, serialNumber={}", serialNumber);
                    return;
                }
                ModbusJobVO modbusJobVO = modbusJobVOList.get(0);
                String command = modbusJobVO.getCommand();
                List<Command> commandList = JsonUtils.parseArray(command, Command.class);
                if (StringUtils.isNotEmpty(address)) {
                    String finalAddress = address;
                    List<Command> commandList1 = commandList.stream().filter(command1 -> Integer.parseInt(finalAddress) == command1.getAddress()).collect(Collectors.toList());
                    modbusDevice.setCommands(commandList1);
                    modbusDevice.setCommand(JSON.toJSONString(commandList1));
                } else {
                    modbusDevice.setCommands(commandList);
                    modbusDevice.setCommand(command);
                }
                modbusDeviceList.add(modbusDevice);
                modbusScheduler.processBatch(modbusDeviceList);
            } else {
                if (StringUtils.isNotEmpty(address)) {
                    String finalAddress1 = address;
                    if (modbusJobVOList == null) {
                        log.warn("readVariableValue: modbusJobVOList为空, serialNumber={}", serialNumber);
                        return;
                    }
                    modbusJobVOList = modbusJobVOList.stream().filter(m -> finalAddress1.equals(m.getAddress())).collect(Collectors.toList());
                }
                ModbusPollMsg msg = new ModbusPollMsg();
                msg.setCommandList(modbusJobVOList.stream().map(ModbusJobVO::getCommand).collect(Collectors.toList()));
                msg.setTransport(deviceMetaDataCache.getProduct().getTransport());
                msg.setProductId(deviceMetaDataCache.getDevice().getProductId());
                msg.setSerialNumber(serialNumber);
                ModbusPollJob job = new ModbusPollJob();
                job.setPollMsg(msg);
                job.setType(ModbusJobTypeEnum.SINGLE.getType());
                MessageProducer.sendPropFetch(job);
            }
        }
    }

    @Override
    public String commandGenerate(MQSendMessageBo messageBo) {
        String result;
        String protocolCode = messageBo.getDp().getProtocolCode();
        Long productId = messageBo.getDp().getProductId();
        JSONObject params = messageBo.getParams();
        if (FastBeeConstant.PROTOCOL.JsonArray.equals(protocolCode)) {
            List<ValueItem> list = new ArrayList<>();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                ValueItem valueItem = new ValueItem();
                valueItem.setId(entry.getKey());
                valueItem.setValue(entry.getValue() + "");
                valueItem.setRemark("");
                list.add(valueItem);
            }
            result = JSON.toJSONString(list);
        } else {
            Map<String, ThingsModelValueItem> valueItemMap = itslCache.getCacheThMapByProductId(productId);
            if (valueItemMap == null) {
                throw new ServiceException(StringUtils.format(MessageUtils.message("product.things.model.not.exist"), productId));
            }
            ThingsModelValueItem thingsModelValueItem = new ThingsModelValueItem();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                thingsModelValueItem = valueItemMap.get(entry.getKey());
            }
            messageBo.setThingsModel(JSON.toJSONString(thingsModelValueItem));
            FunctionCallBackBo functionCallBackBo = mqttMessagePublish.buildMessage(messageBo);
            result = functionCallBackBo.getSources();
        }
        return result;
    }

}
