package com.fastbee.modbustcp.codec.overRtu;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fastbee.common.SubGatewayAdapter;
import com.fastbee.common.annotation.SysProtocol;
import com.fastbee.common.constant.FastBeeConstant;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.enums.FunctionReplyStatus;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.mq.DeviceReport;
import com.fastbee.common.extend.core.domin.mq.MQSendMessageBo;
import com.fastbee.common.extend.core.domin.mq.message.DeviceData;
import com.fastbee.common.extend.core.domin.mq.message.FunctionCallBackBo;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.common.extend.core.protocol.modbus.ModbusCode;
import com.fastbee.common.utils.CaculateUtils;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.modbus.BitUtils;
import com.fastbee.iot.cache.IModbusConfigCache;
import com.fastbee.iot.domain.ModbusConfig;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.modbus.model.OldModbusValue;
import com.fastbee.modbustcp.model.ModbusTcp;
import com.fastbee.protocol.base.protocol.IProtocol;

/**
 * @author bill
 */
@Slf4j
@Component
@SysProtocol(name = "ModbusTcpOverRtu协议", protocolCode = FastBeeConstant.PROTOCOL.ModbusTcpOverRtu, description = "系统内置ModbusTcp解析协议")
public class ModbusTcpOverRtuProtocol implements IProtocol {


    @Resource
    private ModbusTcpOverRtuDecoder messageDecoder;
    @Resource
    private ModbusTcpOverRtuEncoder messageEncoder;
    @Resource
    private RedisCache redisCache;
    @Resource
    private IModbusConfigCache modbusConfigCache;
    @Resource
    private SubGatewayAdapter subGatewayAdapter;

    @Override
    public DeviceReport decode(DeviceData deviceData, String clientId) {
        try {
            DeviceReport report = new DeviceReport();
            report.setProductId(deviceData.getProductId());
            report.setSerialNumber(deviceData.getSerialNumber());
            //原始报文字符串
            String hexDump = ByteBufUtil.hexDump(deviceData.getBuf());
            if ("7e817e".equals(hexDump)) {
                report.setSerialNumber(null);
                report.setMgId("129");
                report.setSources(hexDump);
                return report;
            }
            ModbusTcp message = messageDecoder.decode(deviceData);
            int address = message.getAddress();
            //获取功能码
            int code = message.getCode();
//            if (message.getmId() != 0) {
//                report.setClientId(message.getMac());
//                report.setMessageId(String.valueOf(message.getmId()));
//                report.setSources(hexDump);
//                return report;
//            }
//            GwDeviceBo gwDeviceBo = new GwDeviceBo();
//            gwDeviceBo.setProductId(deviceData.getProductId());
//            gwDeviceBo.setSerialNumber(deviceData.getSerialNumber());
//            report.setGwDeviceBo(gwDeviceBo);
            subGatewayAdapter.matchSubDev(report, address);
//            String serialNumber = report.getSerialNumber();
//            Long productId = report.getProductId();
            ModbusCode modbusCode = ModbusCode.getInstance(code);
            List<ThingsModelSimpleItem> values = new ArrayList<>();
            Long productId = StringUtils.isNotEmpty(report.getDirectConnAddress()) ? report.getProductId() : report.getSubDeviceBoList().get(0).getSubProductId();
            switch (modbusCode) {
                case Read01:
                case Read02:
                    OldModbusValue cacheModbusAddress = getCacheModbusAddress(deviceData.getSerialNumber(), message.getTransactionIdentifier());
                    handleRead0102(productId, message, cacheModbusAddress, values, report.getDirectConnAddress());
                    break;
                case Read03:
                case Read04:
                    OldModbusValue cacheModbusAddress1 = getCacheModbusAddress(deviceData.getSerialNumber(), message.getTransactionIdentifier());
                    handleRead0304(productId, message, cacheModbusAddress1, values, hexDump, report.getDirectConnAddress());
                    break;
                case Write06:
                case Write05:
                    //如果返回06编码，说明是设备回复，更新对应寄存器的值，并发送通知前端
                    report.setIsReply(true);
                    report.setSources(hexDump);
                    report.setProtocolCode(FastBeeConstant.PROTOCOL.ModbusTcpOverRtu);
                    report.setStatus(FunctionReplyStatus.SUCCESS);
                    this.handleMsgReply(productId, message, report, modbusCode);
                    return report;
            }
            if (StringUtils.isNotEmpty(report.getDirectConnAddress())) {
                report.setThingsModelSimpleItem(values);
            } else {
                report.getSubDeviceBoList().get(0).setThingsModelSimpleItem(values);
            }
            report.setSources(hexDump);
            return report;
        } catch (Exception e) {
            log.error("=>解码异常[{}]", e, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 读线圈或读离散型寄存器处理
     *
     * @param productId      产品id
     * @param message        数据集合
     * @param cacheModbusAddress    下发报文
     * @param simpleItemList 返回的物模型值
     */
    private void handleRead0102(Long productId, ModbusTcp message, OldModbusValue cacheModbusAddress, List<ThingsModelSimpleItem> simpleItemList, String directConnAddress) {
        //字节数
        int ioAd = cacheModbusAddress.getRegister();
        int bitCount = cacheModbusAddress.getIoCount();
        //字节数据集合
        byte[] byteDatas = message.getBitData();
        String hexDump = ByteBufUtil.hexDump(byteDatas);
        //处理多个的情况
        Map<Integer, List<ModbusConfig>> modbusConfigMap = modbusConfigCache.getModbusConfigCacheByProductId(productId, directConnAddress);
        if (CollectionUtils.isEmpty(modbusConfigMap)) {
            log.warn("寄存器地址：{},不存在", ioAd);
            return;
        }
        List<ModbusConfig> modbusConfigList = new ArrayList<>();
        for (Map.Entry<Integer, List<ModbusConfig>> entry : modbusConfigMap.entrySet()) {
            modbusConfigList.addAll(entry.getValue());
        }
        modbusConfigList.sort(Comparator.nullsLast(Comparator.comparing(ModbusConfig::getRegister).thenComparing(ModbusConfig::getBitOrder)));
        int isReadOnly = message.getCode() == 1 ? 0 : 1;
        Map<Integer, List<ModbusConfig>> listMap = modbusConfigList.stream().filter(config ->
        { return config.getType() == 1 && config.getIsReadonly() == isReadOnly;}).collect(Collectors.groupingBy(ModbusConfig::getRegister));
        //分割值
        List<String> values = StringUtils.splitEvenly(hexDump, 2);
        HashMap<Integer, Integer> addressIndexMap = new HashMap<>(2);
        for (Map.Entry<Integer, List<ModbusConfig>> entry : listMap.entrySet()) {
            addressIndexMap.put(entry.getKey(), entry.getValue().size());
        }
        //匹配寄存器,一个字节有8个位需要处理
        for (int i = 0; i < bitCount; i++) {
            List<ModbusConfig> configList = listMap.get(ioAd);
            if (CollectionUtils.isEmpty(configList)) {
                continue;
            }
            int bitIndex = addressIndexMap.get(ioAd);
            if (configList.size() == 1) {
                ModbusConfig modbusConfig = configList.get(0);
                String hex = values.get(i / 8);
                int result = BitUtils.deterHex(hex, i % 8);
                ThingsModelSimpleItem simpleItem = new ThingsModelSimpleItem();
                simpleItem.setId(modbusConfig.getIdentifier());
                simpleItem.setValue(result + "");
                simpleItem.setTs(DateUtils.getNowDate());
                simpleItemList.add(simpleItem);
                bitIndex--;
            } else {
                ModbusConfig modbusConfig = configList.get(i % 8);
                String hex = values.get(i / 8);
                int result = BitUtils.deterHex(hex, i % 8);
                ThingsModelSimpleItem simpleItem = new ThingsModelSimpleItem();
                simpleItem.setId(modbusConfig.getIdentifier());
                simpleItem.setValue(result + "");
                simpleItem.setTs(DateUtils.getNowDate());
                simpleItemList.add(simpleItem);
                bitIndex--;
            }
            addressIndexMap.put(ioAd, bitIndex);
            if (bitIndex == 0) {
                ioAd++;
            }
        }
    }

    /**
     * 处理读保持寄存器/输入寄存器
     *
     * @param productId      产品id
     * @param message        数据
     * @param simpleItemList 返回的物模型值
     * @param sourceStr      原始报文字符串
     */
    private void handleRead0304(Long productId, ModbusTcp message, OldModbusValue cacheModbusAddress, List<ThingsModelSimpleItem> simpleItemList, String sourceStr, String directConnAddress) {
        short[] data = message.getData();
        int length = data.length;
        Map<Integer, List<ModbusConfig>> modbusConfigMap = modbusConfigCache.getModbusConfigCacheByProductId(productId, directConnAddress);
        if (CollectionUtils.isEmpty(modbusConfigMap)) {
            log.warn("寄存器数据不存在");
            return;
        }
        if (length == 1) {
            //单个寄存器上报情况处理
            List<ModbusConfig> modbusConfig = modbusConfigMap.get(cacheModbusAddress.getRegister());
            if (CollectionUtils.isEmpty(modbusConfig)) {
                log.warn("寄存器地址：{},不存在", cacheModbusAddress.getRegister());
                return;
            }
            Map<Integer, List<ModbusConfig>> listMap = modbusConfig.stream().collect(Collectors.groupingBy(ModbusConfig::getType));
            //处理IO类型
            List<ModbusConfig> IOConfigList = listMap.get(1);
            if (!CollectionUtils.isEmpty(IOConfigList)) {
                if (IOConfigList.size() > 1) {
                    //03按位运行情况，读寄存器需要将16进制转换为2进制，按位取值
                    // 如1-4个继电器开关情况，寄存器值是0x0007 从右到左，1-4 对应 on-on-on-off
                    for (ModbusConfig config : IOConfigList) {
                        int result = BitUtils.deter(data[0], config.getBitOrder());
                        ThingsModelSimpleItem simpleItem = new ThingsModelSimpleItem();
                        simpleItem.setId(config.getIdentifier());
                        simpleItem.setValue(result + "");
                        simpleItem.setTs(DateUtils.getNowDate());
                        simpleItemList.add(simpleItem);
                    }
                }
            }
            //单个寄存器值
            List<ModbusConfig> dataConfigList = listMap.get(2);
            if (!CollectionUtils.isEmpty(dataConfigList)) {
                ModbusConfig config = dataConfigList.get(0);
                //普通取值，应该只有一个数据，将identity与address替换
                ThingsModelSimpleItem simpleItem = new ThingsModelSimpleItem();
                simpleItem.setId(config.getIdentifier());
                simpleItem.setValue(CaculateUtils.handleToUnSign16(data[0] + "", config.getDataType()));
                simpleItem.setTs(DateUtils.getNowDate());
                simpleItemList.add(simpleItem);
            }
        } else {
            //原数据字符串
            String dataHex = sourceStr.substring(18);
            //处理多个情况
            List<ModbusConfig> modbusConfigList = new ArrayList<>();
            for (Map.Entry<Integer, List<ModbusConfig>> entry : modbusConfigMap.entrySet()) {
                modbusConfigList.addAll(entry.getValue());
            }
            int isReadOnly = message.getCode() == 3 ? 0 : 1;
            Map<Integer, ModbusConfig> configMap = modbusConfigList.stream().filter(config ->
            {return config.getType() == 2 && config.getIsReadonly() == isReadOnly;}).collect(Collectors.toMap(ModbusConfig::getRegister, Function.identity()));
            if (MapUtils.isEmpty(configMap)) {
                log.warn("寄存器数据不存在");
                return;
            }
            ModbusConfig maxRegisterConfig = configMap.values().stream().max(Comparator.comparing(ModbusConfig::getRegister)).orElse(null);
            int maxRegister = Objects.isNull(maxRegisterConfig) ? 0 : maxRegisterConfig.getRegister();
            int registerAd = cacheModbusAddress.getRegister();
            for (int i = 0; i < length; i++) {
                ModbusConfig modbusConfig = configMap.get(registerAd);
                if (Objects.isNull(modbusConfig)) {
                    //处理可能是 03按位运行情况,判断是否有寄存器，而且寄存器对应物模型多个
                    List<ModbusConfig> dataToIoList = modbusConfigMap.get(registerAd);
                    if (!CollectionUtils.isEmpty(dataToIoList) && dataToIoList.size() > 1) {
                        for (ModbusConfig config : dataToIoList) {
                            int result = BitUtils.deter(data[i], config.getBitOrder());
                            ThingsModelSimpleItem simpleItem = new ThingsModelSimpleItem();
                            simpleItem.setId(config.getIdentifier());
                            simpleItem.setValue(result + "");
                            simpleItem.setTs(DateUtils.getNowDate());
                            simpleItemList.add(simpleItem);
                        }
                    } else {
                        i--;
                        log.warn("寄存器地址：{},不存在", registerAd);
                    }
                    if (registerAd > maxRegister) {
                        break;
                    }
                    // 32位读取会丢失个数，这是临时解决，可把下面注释放开
//                    i -= 1;
                    registerAd += 1;
                    continue;
                }
                //个数
                Integer quantity = modbusConfig.getQuantity();
                ThingsModelSimpleItem simpleItem = new ThingsModelSimpleItem();
                simpleItem.setId(modbusConfig.getIdentifier());
                simpleItem.setTs(DateUtils.getNowDate());
                if (quantity == 1) {
                    //将identity与address替换
                    simpleItem.setValue(CaculateUtils.handleToUnSign16(data[i] + "", modbusConfig.getDataType()));
                    simpleItemList.add(simpleItem);
                    dataHex = dataHex.substring(4);
                } else if (quantity == 2) {
                    //这里做数据处理
                    String handleHex = dataHex.substring(0, 8);
                    String value = CaculateUtils.parseValue(modbusConfig.getDataType(), handleHex);
                    simpleItem.setValue(value);
                    simpleItemList.add(simpleItem);
                    //处理dataHex
                    dataHex = dataHex.substring(8);
                    //处理quantity ==2的情况，跳过一次循环
                    i += 1;
                }
                //寄存器地址+1
                registerAd += 1;
            }
        }
    }

    /**
     * 处理下发指令设备回复消息，按照modbus协议约定会返回一样的指令
     * @param message 解析后数据
     */
    private void handleMsgReply(Long productId, ModbusTcp message,DeviceReport report,ModbusCode code){
        //单个寄存器上报情况处理
        Integer register = message.getRegister();
        List<ModbusConfig> modbusConfig = modbusConfigCache.getSingleModbusConfig(productId, register, report.getDirectConnAddress());
        if (CollectionUtils.isEmpty(modbusConfig)) {
            log.warn("寄存器地址：{},不存在", register);
            return;
        }
        Map<Integer, List<ModbusConfig>> listMap = modbusConfig.stream().collect(Collectors.groupingBy(ModbusConfig::getType));
        // 1是IO类型,2是寄存器类型
        int type = code.equals(ModbusCode.Write05) ? 1 : 2;
        List<ModbusConfig> modbusConfigList = listMap.get(type);
        ModbusConfig config = modbusConfigList.get(0);
        List<ThingsModelSimpleItem> values = new ArrayList<>();
        ThingsModelSimpleItem simpleItem = new ThingsModelSimpleItem();
        simpleItem.setId(config.getIdentifier());
        simpleItem.setTs(DateUtils.getNowDate());
        int data = message.getWriteData();
        if (type == 1){
            //对于IO类型的数据做一个数据转换 0x0000 是关 0xff00 是开 对应值 1
            if (data == 0xff00){
                data = 1;
            }
        }
        simpleItem.setValue(data +"");
        values.add(simpleItem);
        report.setThingsModelSimpleItem(values);
    }

    @Override
    public FunctionCallBackBo encode(MQSendMessageBo message) {
        FunctionCallBackBo callBack = new FunctionCallBackBo();
        ModbusTcp modbusTcp = new ModbusTcp();
        String thingsModel = message.getThingsModel();
        ThingsModelValueItem item = JSONObject.parseObject(thingsModel, ThingsModelValueItem.class);
        ModbusConfig config = item.getConfig();
        String modbusTcpId;
        String serialNumber;
        if (StringUtils.isEmpty(message.getParentSerialNumber())) {
            modbusTcpId = redisCache.getCacheModbusTcpId(message.getSerialNumber());
            serialNumber = message.getSerialNumber();
        } else {
            modbusTcpId = redisCache.getCacheModbusTcpId(message.getParentSerialNumber());
            serialNumber = message.getParentSerialNumber();
        }
        modbusTcp.setTransactionIdentifier(Integer.parseInt(modbusTcpId));
        switch (config.getModbusCode()) {
            case Read01:
            case Read02:
                // MBAP 长度默认6个字节
                modbusTcp.setBitLength(6);
                this.read01(config, modbusTcp);
                break;
            case Read03:
            case Read04:
                // MBAP 长度默认6个字节
                modbusTcp.setBitLength(6);
                this.read03(config, modbusTcp);
                break;
            case Write05:
                // MBAP 长度默认6个字节
                modbusTcp.setBitLength(6);
                write05(config,message.getValue(), modbusTcp);
                break;
            case Write06:
                // MBAP 长度默认6个字节
                modbusTcp.setBitLength(6);
                write06(config, message.getValue(), modbusTcp);
                break;
            case Write0F:
                // MBAP 长度默认8个字节
                modbusTcp.setBitLength(8);
                write0F(config, message.getValue(), modbusTcp);
                break;
            case Write10:
                // MBAP 长度默认8个字节
                modbusTcp.setBitLength(9);
                write10(config, message.getValue(), modbusTcp);
                break;
        }
        ByteBuf out = messageEncoder.encode(modbusTcp);
        byte[] data = new byte[out.writerIndex()];
        out.readBytes(data);
        ReferenceCountUtil.release(out);
        // 不需要crc校验
        callBack.setMessage(data);
        String s = ByteBufUtil.hexDump(data);
        callBack.setSources(s);
        redisCache.cacheModbusTcpData(serialNumber, modbusTcpId, s);
        return callBack;
    }

    private void read01(ModbusConfig modbusConfig, ModbusTcp modbusTcp) {
        modbusTcp.setAddress(Integer.parseInt(modbusConfig.getAddress()));
        modbusTcp.setCode(modbusConfig.getModbusCode().getCode());
        modbusTcp.setRegister(modbusConfig.getRegister());
        modbusTcp.setCount(modbusConfig.getQuantity());
    }

    private void write10(ModbusConfig modbusConfig, String value, ModbusTcp modbusTcp) {
        modbusTcp.setAddress(Integer.parseInt(modbusConfig.getAddress()));
        modbusTcp.setCode(modbusConfig.getModbusCode().getCode());
        modbusTcp.setRegister(modbusConfig.getRegister());
        modbusTcp.setCount(modbusConfig.getQuantity());
        modbusTcp.setBitCount(1 + (modbusTcp.getCount() - 1) / 8);
        int data;
        if (value.contains("0x")) {
            data = Integer.parseInt(value.substring(2), 16);
        } else {
            data = Integer.parseInt(value);
        }
        modbusTcp.setWriteData(data == 1 ? 0xFF00 : 0x0000);
    }

    private void write0F(ModbusConfig modbusConfig, String value, ModbusTcp modbusTcp) {
        modbusTcp.setAddress(Integer.parseInt(modbusConfig.getAddress()));
        modbusTcp.setCode(modbusConfig.getModbusCode().getCode());
        modbusTcp.setRegister(modbusConfig.getRegister());
        modbusTcp.setCount(modbusConfig.getQuantity());
        modbusTcp.setBitCount(modbusTcp.getCount() * 2);
        int data;
        if (value.contains("0x")) {
            data = Integer.parseInt(value.substring(2), 16);
        } else {
            data = Integer.parseInt(value);
        }
        modbusTcp.setWriteData(data == 1 ? 0xFF00 : 0x0000);
    }

    /**
     * read03指令
     */
    private void read03(ModbusConfig modbusConfig, ModbusTcp modbusTcp) {
        modbusTcp.setAddress(Integer.parseInt(modbusConfig.getAddress()));
        modbusTcp.setCode(modbusConfig.getModbusCode().getCode());
        modbusTcp.setRegister(modbusConfig.getRegister());
        modbusTcp.setCount(modbusConfig.getQuantity());
    }

    /**
     * writ05/06指令配置
     */
    private void write05(ModbusConfig modbusConfig, String value, ModbusTcp modbusTcp) {
        modbusTcp.setAddress(Integer.parseInt(modbusConfig.getAddress()));
        modbusTcp.setCode(modbusConfig.getModbusCode().getCode());
        modbusTcp.setRegister(modbusConfig.getRegister());
        int data;
        if (value.contains("0x")) {
            data = Integer.parseInt(value.substring(2), 16);
        } else {
            data = Integer.parseInt(value);
        }
        modbusTcp.setWriteData(data == 1 ? 0xFF00 : 0x0000);
    }

    /**
     * writ05/06指令配置
     */
    private void write06(ModbusConfig modbusConfig, String value, ModbusTcp modbusTcp) {
        modbusTcp.setWriteData(Integer.parseInt(value));
        modbusTcp.setRegister(modbusConfig.getRegister());
        modbusTcp.setCode(modbusConfig.getModbusCode().getCode());
        modbusTcp.setAddress(Integer.parseInt(modbusConfig.getAddress()));
    }

    /**
     * 获取modbus地址
     */
    private OldModbusValue getCacheModbusAddress(String serialNumber, int transactionIdentifier) {
        String key = RedisKeyBuilder.buildModbusTcpRuntimeCacheKey(serialNumber);
        Object rawMessage = redisCache.getStringHashValue(key, String.valueOf(transactionIdentifier));
        if (Objects.isNull(rawMessage)) {
            throw new ServiceException("No cache modbus-tcp rawMessage found");
        }
        DeviceData deviceData = DeviceData.builder().buf(Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(rawMessage.toString()))).build();
        ModbusTcp modbusTcp = messageDecoder.decode(deviceData);
        redisCache.delStringHashValue(key, String.valueOf(transactionIdentifier));
        OldModbusValue modbusValue = new OldModbusValue();
        String sourceData = rawMessage.toString();
        // 取其实寄存器地址
        modbusValue.setRegister(Integer.parseInt(sourceData.substring(16, 20), 16));
        // 取寄存器数量
        modbusValue.setIoCount(Integer.parseInt(sourceData.substring(20, 24), 16));
        return modbusValue;
    }

}
