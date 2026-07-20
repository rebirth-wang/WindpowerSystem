package com.fastbee.iot.data.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fastbee.base.service.ISessionStore;
import com.fastbee.base.session.Session;
import com.fastbee.common.constant.FastBeeConstant;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.enums.DeviceStatus;
import com.fastbee.common.enums.ServerType;
import com.fastbee.common.enums.TopicType;
import com.fastbee.common.extend.core.domin.mq.DeviceStatusBo;
import com.fastbee.common.extend.core.domin.mq.message.ModbusPollMsg;
import com.fastbee.common.extend.core.protocol.Message;
import com.fastbee.common.extend.enums.ModbusJobCommantEnum;
import com.fastbee.common.extend.enums.ModbusJobTypeEnum;
import com.fastbee.common.extend.enums.ScriptEventEnum;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.gateway.mq.TopicsUtils;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.data.service.impl.MessageManager;
import com.fastbee.iot.domain.DeviceJob;
import com.fastbee.iot.mapper.SubGatewayMapper;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.gateWay.GateSubDeviceParamsVO;
import com.fastbee.iot.model.modbus.ModbusPollJob;
import com.fastbee.iot.model.vo.ModbusJobVO;
import com.fastbee.iot.service.IModbusJobService;
import com.fastbee.iot.service.IScriptService;
import com.fastbee.mq.producer.MessageProducer;
import com.fastbee.mqttclient.PubMqttClient;
import com.fastbee.rule.context.MsgContext;

/**
 * 平台定时批量获取设备属性(或单个获取)
 *
 * @author bill
 */
@Slf4j
@Component
public class DevicePropFetchConsumer {

    @Autowired
    private PubMqttClient pubMqttClient;
    @Autowired
    private RedisCache redisCache;
    @Resource
    private MessageManager messageManager;
    @Resource
    private TopicsUtils topicsUtils;
    @Resource
    private IModbusJobService modbusJobService;
    @Resource
    private ISessionStore sessionStore;
    @Resource
    private SubGatewayMapper subGatewayMapper;
    @Resource
    private IScriptService scriptService;
    @Resource
    private IDeviceCache deviceCache;

    @Value("${server.broker.enabled}")
    private Boolean enabled;
    private final ConcurrentHashMap<String, Lock> taskLocks = new ConcurrentHashMap<>();

    @Async(FastBeeConstant.TASK.DEVICE_FETCH_PROP_TASK)
    public void consume(ModbusPollJob job) {
        Integer type = job.getType();
        DeviceJob deviceJob = job.getDeviceJob();
        ModbusPollMsg task = ModbusJobTypeEnum.SINGLE.getType().equals(type) ? job.getPollMsg() : getCommandList(deviceJob);
        if (CollectionUtils.isEmpty(task.getCommandList())) return;
        String serialNumber = task.getSerialNumber();
        //获取一个线程锁
        Lock lock = taskLocks.computeIfAbsent(serialNumber, k -> new ReentrantLock());
        //阻塞直到获取到锁
        lock.lock();
        try {
            Long productId = task.getProductId();
            List<String> commandList = task.getCommandList();
            ServerType serverType = ServerType.explain(task.getTransport());
            String topic = topicsUtils.buildTopic(productId, serialNumber, TopicType.FUNCTION_GET);
            for (String command : commandList) {
                String cacheKey = RedisKeyBuilder.buildModbusRuntimeCacheKey(serialNumber);
                redisCache.zSetAdd(cacheKey, command, DateUtils.getTimestampSeconds());
                switch (serverType) {
                    //通过mqtt内部客户端 下发指令
                    case MQTT:
                        MsgContext contextMqtt = scriptService.processRuleScript(serialNumber, ScriptEventEnum.DEVICE_INVOKE.getType(), topic, command);
                        if (Objects.nonNull(contextMqtt) && StringUtils.isNotEmpty(contextMqtt.getPayload())) {
                            command = contextMqtt.getPayload();
                        }
                        publish(topic, ByteBufUtil.decodeHexDump(command));
                        log.info("=>MQTT-线程=[{}],轮询指令:[{}],主题:[{}]", Thread.currentThread().getName(), command, topic);
                        break;
                    //  下发TCP客户端
                    case TCP:
                        Message msg = new Message();
                        MsgContext contextTcp = scriptService.processRuleScript(serialNumber, ScriptEventEnum.DEVICE_INVOKE.getType(), topic, command);
                        if (Objects.nonNull(contextTcp) && StringUtils.isNotEmpty(contextTcp.getPayload())) {
                            command = contextTcp.getPayload();
                        }
                        msg.setSerialNumber(serialNumber);
                        msg.setPayload(Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(command)));
                        // todo tcp下发规则脚本处理
                        messageManager.requestR(serialNumber, msg, Message.class);
                        log.info("=>TCP-线程=[{}],轮询指令:[{}]", Thread.currentThread().getName(), command);
                        break;
                }
                //指令间隔时间
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            log.error("线程错误e", e);
        } finally {
            lock.unlock();
        }
    }

    private ModbusPollMsg getCommandList(DeviceJob deviceJob) {
        ModbusPollMsg modbusPollMsg = new ModbusPollMsg();
        Long jobId = deviceJob.getJobId();
        ModbusJobVO modbusJobVO = new ModbusJobVO();
        modbusJobVO.setJobId(jobId);
        modbusJobVO.setStatus(0);
        modbusJobVO.setCommandType(ModbusJobCommantEnum.POLLING.getType());
        List<ModbusJobVO> modbusJobVOList = modbusJobService.selectModbusJobList(modbusJobVO);
        if (CollectionUtils.isEmpty(modbusJobVOList)) return modbusPollMsg;
        //处理子设备情况
        handleSubDeviceStatus(modbusJobVOList);
        List<String> commandList = modbusJobVOList.stream().map(ModbusJobVO::getCommand).collect(Collectors.toList());
        // modbus-tcp 重新处理事务标识符
        List<String> tcpCommandList = new ArrayList<>();
        // modbus-tcp 重新处理事务标识符
        String parentSerialNumber = deviceJob.getSerialNumber();
        // 老数据可能没tcp事务key，还是严谨点查询处理把
        DeviceMetaData deviceMetaDataCache = deviceCache.getDeviceMetaDataCache(parentSerialNumber);
        if (Objects.nonNull(deviceMetaDataCache) && FastBeeConstant.PROTOCOL.ModbusTcpOverRtu.equals(deviceMetaDataCache.getProduct().getProtocolCode())) {
            String tcpRuntimeCacheKey = RedisKeyBuilder.buildModbusTcpRuntimeCacheKey(parentSerialNumber);
            for (String command : commandList) {
                String tranNo = command.substring(0, 4);
                int i = Integer.parseInt(tranNo, 16);
                Object stringHashValue = redisCache.getStringHashValue(tcpRuntimeCacheKey, String.valueOf(i));
                if (Objects.nonNull(stringHashValue)) {
                    tcpCommandList.add(command);
                    continue;
                }
                String modbusTcpId = redisCache.getCacheModbusTcpId(parentSerialNumber);
                String hexString = String.format("%04x", Integer.parseInt(modbusTcpId));
                String newTcpData = hexString + command.substring(4);
                redisCache.cacheModbusTcpData(parentSerialNumber, modbusTcpId, newTcpData);
                tcpCommandList.add(newTcpData);
            }
        }
        modbusPollMsg.setSerialNumber(deviceJob.getSerialNumber());
        modbusPollMsg.setProductId(deviceJob.getProductId());
        if (CollectionUtils.isEmpty(tcpCommandList)) {
            modbusPollMsg.setCommandList(commandList);
        } else {
            modbusPollMsg.setCommandList(tcpCommandList);
        }
        DeviceMetaData deviceMetaDataCache1 = deviceCache.getDeviceMetaDataCache(modbusPollMsg.getSerialNumber());
        if (Objects.isNull(deviceMetaDataCache1)) {
            modbusPollMsg.setCommandList(null);
            return modbusPollMsg;
        }
        modbusPollMsg.setTransport(deviceMetaDataCache1.getProduct().getTransport());
        Session session = sessionStore.getSession(modbusPollMsg.getSerialNumber());
        if (enabled && Objects.isNull(session)) {
            log.info("设备：[{}],不在线", modbusPollMsg.getSerialNumber());
            return modbusPollMsg;
        }
        log.info("执行modbus轮询指令:[{}]", JSON.toJSONString(commandList));
        return modbusPollMsg;
    }


    /**
     * 处理子设备根据设备数据定时更新状态
     */
    public void handleSubDeviceStatus(List<ModbusJobVO> modbusJobVOList) {
        List<String> addressList = modbusJobVOList.stream().map(ModbusJobVO::getAddress).distinct().collect(Collectors.toList());
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(modbusJobVOList)) {
            return;
        }
        List<GateSubDeviceParamsVO> subGatewayList = subGatewayMapper.selectSubModbusParamsVO(modbusJobVOList.get(0).getSerialNumber(), addressList);
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(subGatewayList)) {
            return;
        }
        Map<String, GateSubDeviceParamsVO> subGatewayMap = subGatewayList.stream().collect(Collectors.toMap(GateSubDeviceParamsVO::getAddress, Function.identity()));
        for (ModbusJobVO modbusJobVO : modbusJobVOList) {
            GateSubDeviceParamsVO subGateway = subGatewayMap.get(modbusJobVO.getAddress());
            if (Objects.isNull(subGateway)) {
                continue;
            }
            Session session = sessionStore.getSession(subGateway.getSerialNumber());
            if (Objects.isNull(session)) continue;
            //如果是网关子设备，则检测子设备是否在特定时间内有数据上报
            //这里默认使用5分钟,如果轮询时间大于5分钟，请在配置参数设置更长时间
            long deterTime = 300L;
            if (StringUtils.isNotEmpty(subGateway.getDeterTimer())) {
                deterTime = Long.parseLong(subGateway.getDeterTimer());
            }

            long lastAccessTime = session.getLastAccessTime();
            //如果现在的时间 - 最后访问时间 > 判断时间则更新为离线
            long time = (System.currentTimeMillis() - lastAccessTime - 20) / 1000;
            if (time > deterTime) {
                //处理设备离线
                DeviceStatusBo statusBo = DeviceStatusBo.builder().status(DeviceStatus.OFFLINE)
                        .serialNumber(subGateway.getSerialNumber())
                        .timestamp(DateUtils.getNowDate()).build();
                MessageProducer.sendStatusMsg(statusBo);
                sessionStore.cleanSession(subGateway.getSerialNumber());
                log.info("设备[{}],超过：[{}],未上报数据，更新为离线", subGateway.getSerialNumber(), deterTime);
            }
        }
    }

    public void publish(String topic, byte[] pushMessage) {
        pubMqttClient.publish(pushMessage, topic, false, 0);
    }
}
