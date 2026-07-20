package com.fastbee.iot.data.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.*;

import jakarta.annotation.Resource;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fastbee.base.service.ISessionStore;
import com.fastbee.base.session.Session;
import com.fastbee.common.constant.FastBeeConstant;
import com.fastbee.common.constant.ProductAuthConstant;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.enums.*;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.mq.DeviceReport;
import com.fastbee.common.extend.core.domin.mq.DeviceReportBo;
import com.fastbee.common.extend.core.domin.mq.DeviceStatusBo;
import com.fastbee.common.extend.core.domin.mq.SubDeviceBo;
import com.fastbee.common.extend.core.domin.mq.message.DeviceData;
import com.fastbee.common.extend.core.domin.mq.message.ReportDataBo;
import com.fastbee.common.extend.core.domin.mq.ota.OtaReplyMessage;
import com.fastbee.common.extend.core.domin.ota.OtaPackageCode;
import com.fastbee.common.extend.enums.ScriptEventEnum;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.gateway.mq.TopicsUtils;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.cache.IOtaTaskCache;
import com.fastbee.iot.data.service.IDataHandler;
import com.fastbee.iot.data.service.IDeviceReportMessageService;
import com.fastbee.iot.data.service.IFirmwareTaskDetailService;
import com.fastbee.iot.domain.FunctionLog;
import com.fastbee.iot.enums.DeviceType;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.vo.DeviceStatusVO;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.iot.service.IFunctionLogService;
import com.fastbee.iot.service.IProductService;
import com.fastbee.iot.service.IScriptService;
import com.fastbee.mq.producer.MessageProducer;
import com.fastbee.mqtt.manager.MqttRemoteManager;
import com.fastbee.mqtt.model.PushMessageBo;
import com.fastbee.mqttclient.EmqxApiClient;
import com.fastbee.protocol.base.protocol.IProtocol;
import com.fastbee.protocol.service.IProtocolManagerService;
import com.fastbee.protocol.util.IntegerToByteUtil;
import com.fastbee.rule.context.MsgContext;

/**
 * 处理类 处理设备主动上报和设备回调信息
 *
 * @author bill
 */
@Service
@Slf4j
public class DeviceReportMessageServiceImpl implements IDeviceReportMessageService {

    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private IProductService productService;
    @Autowired
    private IProtocolManagerService protocolManagerService;
    @Autowired
    private IFirmwareTaskDetailService firmwareTaskDetailService;
    @Resource
    private IDataHandler dataHandler;
    @Resource
    private RedisCache redisCache;
    @Resource
    private IFunctionLogService logService;
    @Resource
    private ISessionStore sessionStore;
    @Resource
    private IOtaTaskCache otaTaskCache;
    @Resource
    private TopicsUtils topicsUtils;
    @Resource
    private MqttRemoteManager remoteManager;
    @Value("${server.broker.enabled}")
    private Boolean enabled;
    @Resource
    private EmqxApiClient emqxApiClient;
    @Resource
    private IDeviceCache deviceCache;

    @Resource
    private IScriptService scriptService;

    /**
     * 处理设备主动上报数据
     */
    @Override
    public void parseReportMsg(DeviceReportBo bo) {
//        DeviceStatusVO deviceStatusVO = buildReport(bo);
        String serialNumber = bo.getSerialNumber();
        // 查询设备缓存
        DeviceMetaData deviceMetaData = deviceCache.getDeviceMetaDataCache(serialNumber);
        if (Objects.isNull(deviceMetaData)) {
            // 查询设备缓存
            deviceMetaData = deviceCache.getDeviceMetaDataCache(topicsUtils.parseSerialNumber(bo.getTopicName()));
        }
        Optional.ofNullable(deviceMetaData).orElseThrow(() -> new ServiceException(MessageUtils.message("device.not.exist")));
        bo.setProductId(deviceMetaData.getDevice().getProductId());
        switch (bo.getServerType()) {
            case MQTT:
                log.debug("=>MQ*收到设备主题[{}],消息:[{}]", bo.getTopicName(), bo.getData());
                //构建消息
//                DeviceStatusVO deviceStatusVO = this.buildReport(bo);
                serialNumber = deviceMetaData.getDevice().getSerialNumber();
                Long productId = bo.getProductId();
                /*获取协议处理器*/
                IProtocol protocol = this.selectProtocolByCode(deviceMetaData.getProduct().getProtocolCode());
                DeviceData data = DeviceData.builder()
                        .serialNumber(serialNumber)
                        .topicName(bo.getTopicName())
                        .productId(deviceMetaData.getDevice().getProductId())
                        .data(bo.getData())
                        .buf(Unpooled.wrappedBuffer(bo.getData()))
                        .build();
                // 编码前执行脚本，兼容编码前需要改写数据的需求
                // 使用 UTF-8 将 byte[] 转为 String，脚本处理后再以相同编码转回 byte[]，保持数据一致
                String rawPayload = new String(bo.getData(), StandardCharsets.UTF_8);
                MsgContext msg = scriptService.processRuleScript(bo.getSerialNumber(), ScriptEventEnum.DEVICE_REPORT.getType(), bo.getTopic(), rawPayload);
                if (msg != null) {
                    log.debug("=>脚本处理后的设备主题[{}],消息:[{}]", bo.getTopicName(), msg.getPayload());
                    data.setTopicName(msg.getTopic());
                    if (msg.getPayload() != null) {
                        data.setData(msg.getPayload().getBytes(StandardCharsets.UTF_8));
                    }
                }
                /*根据协议解析后的数据*/
                DeviceReport reportMessage = protocol.decode(data, serialNumber);
                //设备回复，更新指令下发记录
                if (reportMessage.getIsReply()) {
                    handlerDeviceReply(reportMessage);
                }
//                serialNumber = StringUtils.isEmpty(reportMessage.getSerialNumber()) ? serialNumber : reportMessage.getSerialNumber();
//                productId = reportMessage.getProductId() == null ? productId : reportMessage.getProductId();
                // 就用网关的设备编号和产品id
                if (StringUtils.isEmpty(reportMessage.getSerialNumber())) {
                    reportMessage.setSerialNumber(serialNumber);
                }
                if (null == reportMessage.getProductId()) {
                    reportMessage.setProductId(productId);
                }
                if (null == reportMessage.getTopic()) {
                    reportMessage.setTopic(bo.getTopicName());
                }
                reportMessage.setPlatformDate(bo.getPlatformDate());
                reportMessage.setServerType(bo.getServerType());
                //同步网关与子设备状态
                this.synchDeviceStatus(serialNumber);
                if (CollectionUtils.isNotEmpty(reportMessage.getSubDeviceBoList())) {
                    for (SubDeviceBo subDeviceBo : reportMessage.getSubDeviceBoList()) {
                        this.synchDeviceStatus(subDeviceBo.getSubClientId());
                    }
                }
                //使用emq，兜底处理网关设备的状态,注意非网关设备不用处理，在上面的方法已经处理了
                if (!enabled && deviceMetaData.getProduct().getDeviceType() == DeviceType.GATEWAY.getCode()) {
                    if (deviceMetaData.getDevice().getStatus() == DeviceStatus.OFFLINE.getType()) {
                        String clientId = ProductAuthConstant.CLIENT_ID_AUTH_TYPE_SIMPLE
                                + "&" + deviceMetaData.getDevice().getSerialNumber()
                                + "&" + deviceMetaData.getDevice().getProductId()
                                + "&" + deviceMetaData.getDevice().getTenantId();

                        //如果数据库中状态为离线
                        if (emqxApiClient.isEnabled()
                                && emqxApiClient.isDeviceOnline(clientId)) {
                            //更新为在线
                            DeviceStatusBo statusBo = new DeviceStatusBo();
                            statusBo.setStatus(DeviceStatus.ONLINE);
                            statusBo.setSerialNumber(deviceMetaData.getDevice().getSerialNumber());
                            statusBo.setTimestamp(DateUtils.getNowDate());
                            MessageProducer.sendStatusMsg(statusBo);
                        }
                    }

                }
                //处理网关设备上报数据
                this.processNoSub(reportMessage, bo.getTopicName());
                break;
            case TCP:
                log.debug("*MQ收到TCP推送消息[{}]", JSON.toJSON(bo.getThingsModelSimpleItem()));
                //同步设备状态
                this.synchDeviceStatus(serialNumber);
                DeviceReport deviceReport = new DeviceReport();
                BeanUtils.copyProperties(bo, deviceReport);
                deviceReport.setProductId(deviceMetaData.getDevice().getProductId());
                deviceReport.setThingsModelSimpleItem(bo.getThingsModelSimpleItem());
//                deviceReport.setAddress(bo.getSlaveId());
                deviceReport.setSerialNumber(deviceMetaData.getDevice().getSerialNumber());
                //设备回复数据处理
                if (bo.getIsReply()) {
                    this.handlerDeviceReply(deviceReport);
                }
                this.processNoSub(deviceReport, null);
                break;
            case OTHER:
                IProtocol modbusProtocol = this.selectProtocolByCode(bo.getProtocolCode());
                DeviceData modbusTcpData = DeviceData.builder()
                        .serialNumber(deviceMetaData.getDevice().getSerialNumber())
                        .productId(deviceMetaData.getDevice().getProductId())
                        .response(bo.getResponse())
                        .data(bo.getData())
                        .build();
                DeviceReport modbusTcpReport = modbusProtocol.decode(modbusTcpData, deviceMetaData.getDevice().getSerialNumber());
                this.synchDeviceStatus(modbusTcpReport.getSerialNumber());
                this.processNoSub(modbusTcpReport, null);
                break;
            default:
                break;
        }
    }


    /**
     * 处理设备回调数据
     */
    @Override
    public void parseReplyMsg(DeviceReportBo bo) {
        log.debug("=>MQ*收到设备回调消息,[{}]", bo);
        this.buildReport(bo);
        //获取解析协议
        IProtocol protocol = selectedProtocol(bo.getProductId());
        DeviceData deviceSource = DeviceData.builder()
                .serialNumber(bo.getSerialNumber())
                .productId(bo.getProductId())
                .topicName(bo.getTopicName())
                .data(bo.getData())
                .build();
        //协议解析后的数据
        DeviceReport message = protocol.decode(deviceSource, null);
        //处理网关设备回复数据
        processNoSub(message, bo.getTopicName());

    }

    /**
     * 处理设备OTA升级
     *
     * @param bo
     */
    @Override
    public void parseOTAUpdateReply(DeviceReportBo bo) {
        if (bo.getTopicName().endsWith(TopicType.FETCH_UPGRADE_REPLY.getTopicSuffix())) {
            String message = ByteBufUtil.hexDump(bo.getData());
            IProtocol protocol = protocolManagerService.getProtocolByProtocolCode(FastBeeConstant.PROTOCOL.NetOTA);
            DeviceData deviceData = protocol.decode(message);
            if (OtaPackageCode.OTA_0B.equals(deviceData.getNetModbusCode())) {
                // 获取包偏移
                byte[] data = deviceData.getData();
                if (data == null || data.length != 4) {
                    return;
                }
                int offset = IntegerToByteUtil.bytes4ToInt(data);
                otaTaskCache.setOtaCacheValue(bo.getSerialNumber(), "offset", offset + "", 5);
            } else if (OtaPackageCode.OTA_0A.equals(deviceData.getNetModbusCode())) {
                // 获取分包传输大小
                byte[] data = deviceData.getData();
                if (data == null || data.length != 1) {
                    return;
                }
                int size = 256;
                if ((int) data[0] == 1) {
                    size = 512;
                } else if ((int) data[0] == 2) {
                    size = 1024;
                }
                otaTaskCache.setOtaCacheValue(bo.getSerialNumber(), "packageSize", size + "", 5);
            }
        }
        // HTTP固件升级回复
        else if (bo.getTopicName().endsWith(TopicType.HTTP_UPGRADE_REPLY.getTopicSuffix())) {
            String data = new String(bo.getData(), StandardCharsets.UTF_8);
            OtaReplyMessage replyMessage = com.alibaba.fastjson2.JSONObject.parseObject(data, OtaReplyMessage.class);
            if (replyMessage == null) {
                log.warn("parseOTAUpdateReply: OTA回复消息解析失败, serialNumber={}", bo.getSerialNumber());
                return;
            }
            OTAUpgrade otaUpgrade = OTAUpgrade.parse(replyMessage.getStatus());
            if (otaUpgrade == null) {
                log.warn("parseOTAUpdateReply: OTA升级状态解析失败, status={}", replyMessage.getStatus());
                return;
            }

            PushMessageBo appMessageBo = new PushMessageBo();
            appMessageBo.setTopic(topicsUtils.buildTopic(String.valueOf(replyMessage.getTaskId()), TopicType.WS_OTA_STATUS));
            com.alibaba.fastjson2.JSONObject wsMessage = new com.alibaba.fastjson2.JSONObject();
            wsMessage.put("serialNumber", bo.getSerialNumber());
            wsMessage.put("status", otaUpgrade.getStatus());
            wsMessage.put("timestamp", System.currentTimeMillis());
            if (replyMessage.getStatus() == 2) {
                wsMessage.put("progress", replyMessage.getProgress());
            }
            // WS消息发送前端
            appMessageBo.setMessage(wsMessage.toString());
            remoteManager.pushCommon(appMessageBo);
            Map<String, String> hMap = new HashMap<>();
            hMap.put("progress", String.valueOf(replyMessage.getProgress()));
            hMap.put("version", String.valueOf(replyMessage.getVersion()));
            hMap.put("status", String.valueOf(replyMessage.getStatus()));
            otaTaskCache.setOtaCache(bo.getSerialNumber(), hMap, 5);
            firmwareTaskDetailService.updateStatus(replyMessage.getTaskId(), bo.getSerialNumber(), otaUpgrade);
        }
    }

    /**
     * 构建消息
     *
     * @param bo
     */
    @Override
    public void buildReport(DeviceReportBo bo) {
//        DeviceStatusVO deviceStatusVO = deviceService.selectDeviceStatusAndTransportStatus(bo.getSerialNumber());
//        // 查询topic主题上的sn信息
//        if (Objects.isNull(deviceStatusVO)) {
//            deviceStatusVO = deviceService.selectDeviceStatusAndTransportStatus(topicsUtils.parseSerialNumber(bo.getTopicName()));
//        }
        DeviceMetaData deviceMetaData = deviceCache.getDeviceMetaDataCache(bo.getSerialNumber());
        if (Objects.isNull(deviceMetaData)) {
            deviceMetaData = deviceCache.getDeviceMetaDataCache(topicsUtils.parseSerialNumber(bo.getTopicName()));
        }
        Optional.ofNullable(deviceMetaData).orElseThrow(() -> new ServiceException(MessageUtils.message("device.not.exist")));
        //产品id
        bo.setProductId(deviceMetaData.getDevice().getProductId());
    }

    /**
     * 根据产品id获取协议处理器
     */
    @Override
    public IProtocol selectedProtocol(Long productId) {

        //查询产品获取协议编号
        String code = productService.getProtocolByProductId(productId);
        return protocolManagerService.getProtocolByProtocolCode(code);
    }

    @Override
    public IProtocol selectProtocolByCode(String code) {
        return protocolManagerService.getProtocolByProtocolCode(code);
    }

    /**
     * 处理网关设备
     *
     * @param message
     * @param topicName
     */
    /**
     * 处理网关设备
     *
     * @param message
     * @param topicName
     */
    private void processNoSub(DeviceReport message, String topicName) {
        if (message.getServerType().equals(ServerType.MQTT)) {
            if (StringUtils.isEmpty(topicName) || !topicName.endsWith(TopicType.PROPERTY_POST.getTopicSuffix())) {
                return;
            }
        }
        ReportDataBo report = new ReportDataBo();
        report.setSerialNumber(message.getSerialNumber());
        report.setProductId(message.getProductId());
        report.setDataList(message.getThingsModelSimpleItem());
        report.setType(1);
//        report.setUserId(message.getUserId());
//        report.setUserName(message.getUserName());
//        report.setDeviceName(message.getDeviceName());
        report.setSources(message.getSources());
        report.setPayload(message.getSources());
        report.setTopic(message.getTopic());
//        report.setGwDeviceBo(message.getGwDeviceBo());
        //属性上报执行规则引擎
        report.setRuleEngine(true);
        // 添加子设备集合
        report.setSubDeviceBoList(message.getSubDeviceBoList());
        dataHandler.reportData(report);
    }


    /**
     * 处理设备回调信息，此处按照topic区分 prop上报和设备回调reply，
     * 如果模组可订阅的topic有限，不能区分prop上报和reply，自行根据上报数据来区分
     *
     * @param message
     */
    public void handlerDeviceReply(DeviceReport message) {
        String messageId = "";
        String sources = message.getSources();
        String serialNumber = message.getSerialNumber();
        String cacheKey = RedisKeyBuilder.buildDownMessageIdCacheKey(serialNumber);
        Set<String> functionList = redisCache.zRange(cacheKey, 0, -1);
        //从redis中获取messageId 流水号，获取下发记录
        for (String fun : functionList) {
            String[] split = fun.split(":");
            if (split[0].equals(sources)) {
                messageId = split[1];
            }
            redisCache.zRem(cacheKey, fun);
        }
        FunctionLog functionLog = new FunctionLog();
        switch (message.getProtocolCode()) {
            case FastBeeConstant.PROTOCOL.ModbusRtu:
            case FastBeeConstant.PROTOCOL.ModbusToJsonHP:
            case FastBeeConstant.PROTOCOL.ModbusRtuPak:
                //更新值
                functionLog.setResultCode(FunctionReplyStatus.SUCCESS.getCode());
                functionLog.setResultMsg(FunctionReplyStatus.SUCCESS.getMessage());
                functionLog.setReplyTime(DateUtils.getNowDate());
                functionLog.setMessageId(message.getMgId() == null ? messageId : message.getMgId());
                logService.updateByMessageId(functionLog);
                break;
        }
    }


    /**
     * 同步设备状态
     *
     * @param
     */
    private void synchDeviceStatus(String serialNumber) {
        //如果有数据上报，但是数据库设备状态为离线，则进行同步
        // todo 需要评估是否调整为查询设备缓存
        DeviceStatusVO deviceStatusVO = deviceService.selectDeviceStatusAndTransportStatus(serialNumber);
        if (deviceStatusVO == null) {
            log.warn("synchDeviceStatus: 设备不存在, serialNumber={}", serialNumber);
            return;
        }
        if (deviceStatusVO.getStatus() == DeviceStatus.OFFLINE.getType()
                || deviceStatusVO.getStatus() == DeviceStatus.UNACTIVATED.getType()) {
            DeviceStatusBo statusBo = new DeviceStatusBo();
            statusBo.setStatus(DeviceStatus.ONLINE);
            statusBo.setSerialNumber(deviceStatusVO.getSerialNumber());
            statusBo.setTimestamp(DateUtils.getNowDate());
            MessageProducer.sendStatusMsg(statusBo);

            //如果是子设备，维护子设备的状态到session
            if (Objects.equals(deviceStatusVO.getDeviceType(), DeviceType.SUB_GATEWAY.getCode())) {
                Session session = new Session();
                session.setServerType(ServerType.MQTT);
                session.setClientId(deviceStatusVO.getSerialNumber());
                session.setLastAccessTime(DateUtils.getTimestamp());
                session.setConnected(true);
                sessionStore.storeSession(deviceStatusVO.getSerialNumber(), session);
            }

        } else if (deviceStatusVO.getStatus() == DeviceStatus.ONLINE.getType()) {
            if (Objects.equals(deviceStatusVO.getDeviceType(), DeviceType.SUB_GATEWAY.getCode())) {
                //如果是在线,则更新session在线时间
                Session session = sessionStore.getSession(deviceStatusVO.getSerialNumber());
                if (session != null) {
                    session.setLastAccessTime(DateUtils.getTimestamp());
                    sessionStore.storeSession(deviceStatusVO.getSerialNumber(), session);
                }
            }

        }


    }

}
