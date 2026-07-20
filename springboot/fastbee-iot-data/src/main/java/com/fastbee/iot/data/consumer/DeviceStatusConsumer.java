package com.fastbee.iot.data.consumer;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fastbee.base.service.ISessionStore;
import com.fastbee.common.enums.DeviceStatus;
import com.fastbee.common.extend.core.domin.mq.DeviceStatusBo;
import com.fastbee.common.extend.core.domin.mq.MQSendMessageBo;
import com.fastbee.common.extend.core.domin.mq.message.ReportDataBo;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.common.extend.enums.DeviceStatusEnum;
import com.fastbee.common.extend.enums.EnableEnum;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.data.service.IMqttMessagePublish;
import com.fastbee.iot.data.service.IRuleEngine;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.domain.SubGateway;
import com.fastbee.iot.enums.DeviceEventType;
import com.fastbee.iot.enums.DeviceType;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.ThingsModels.ThingsModelShadow;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.iot.service.IDeviceUpdateService;
import com.fastbee.iot.util.SnowflakeIdWorker;
import com.fastbee.mq.producer.MessageProducer;

/**
 * 设备状态消息处理类
 *
 * @author gsb
 * @date 2022/10/10 11:02
 */
@Slf4j
@Component
public class DeviceStatusConsumer {

    @Autowired
    private IDeviceCache deviceCache;
    @Resource
    private IRuleEngine ruleEngine;
    @Resource
    private IDeviceService deviceService;
    @Resource
    private ISessionStore sessionStore;
    @Resource
    private IMqttMessagePublish mqttMessagePublish;
    @Value("${server.broker.enabled}")
    private Boolean enabled;
    @Resource
    private IDeviceUpdateService deviceUpdateService;

    private SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(2);

    public synchronized void consume(DeviceStatusBo bo) {
        try {
            List<DeviceStatusBo> list = new ArrayList<>();
            DeviceMetaData deviceMetaData = deviceCache.getDeviceMetaDataCache(bo.getSerialNumber());
            if (deviceMetaData == null) {
                log.warn("DeviceStatusConsumer: 设备元数据缓存为空, serialNumber={}", bo.getSerialNumber());
                return;
            }
            if (DeviceType.GATEWAY.getCode() == deviceMetaData.getProduct().getDeviceType()) {
                List<SubGateway> subGatewayList = deviceMetaData.getSubGatewayList();
                if (!CollectionUtils.isEmpty(subGatewayList) && bo.getStatus()== DeviceStatus.OFFLINE){
                    for (SubGateway subGateway : subGatewayList) {
                        DeviceStatusBo deviceStatusBo = new DeviceStatusBo();
                        BeanUtils.copyProperties(bo, deviceStatusBo);
                        deviceStatusBo.setSerialNumber(subGateway.getSubClientId());
                        list.add(deviceStatusBo);
                    }
                }
            }
            list.add(bo);
            for (DeviceStatusBo statusBo : list) {
                DeviceStatus status = statusBo.getStatus();
                // 调整为查询设备缓存
                DeviceMetaData deviceMetaDataCache = deviceCache.getDeviceMetaDataCache(statusBo.getSerialNumber());
                if (deviceMetaDataCache == null) {
                    log.warn("DeviceStatusConsumer: 子设备元数据缓存为空, serialNumber={}", statusBo.getSerialNumber());
                    continue;
                }
                Device device = deviceMetaDataCache.getDevice();
                Product product = deviceMetaDataCache.getProduct();
                //如果使用Netty版本 监控设备不走这里
                if (enabled && !DeviceType.CAMERA.getCode().equals(product.getDeviceType())) {
                    boolean containsKey = sessionStore.containsKey(statusBo.getSerialNumber());
                    boolean isOnline = DeviceStatusEnum.ONLINE.getStatus().equals(device.getStatus());
                    log.info("=>session：{},数据库：{}，更新状态:{}", containsKey, isOnline, bo.getStatus().getCode());
                    if (containsKey && !isOnline) {
                        //如果session存在，但数据库状态不在线，则以session为准
                        statusBo.setStatus(DeviceStatus.ONLINE);
                    }
                    if (!containsKey && isOnline) {
                        statusBo.setStatus(DeviceStatus.OFFLINE);
                    }
                    if (statusBo.getSerialNumber().equals(bo.getSerialNumber()) && DeviceStatus.FORBIDDEN.equals(bo.getStatus())) {
                        statusBo.setStatus(DeviceStatus.FORBIDDEN);
                    }
                }
                //设备上下线执行规则引擎
                ReportDataBo dataBo = new ReportDataBo();
                dataBo.setRuleEngine(true);
                dataBo.setProductId(device.getProductId());
                dataBo.setType(status.equals(DeviceStatus.ONLINE) ? DeviceEventType.DEV_ONLINE.getCode() : DeviceEventType.DEV_OFFLINE.getCode());
                dataBo.setSerialNumber(statusBo.getSerialNumber());
                ruleEngine.ruleMatch(dataBo);
                /*更新设备状态*/
                deviceCache.updateDeviceStatusCache(statusBo, device);
                deviceUpdateService.updateDeviceStatusAndLocation(device, statusBo.getIp());
                //处理影子模式值
                this.handlerShadow(device, status, deviceMetaData.getSubGatewayList());
                //推送到前端
                mqttMessagePublish.pushDeviceStatus(device, status);
            }
        } catch (Exception e) {
            log.error("=>设备状态处理异常", e);
        }
    }

    private void handlerShadow(Device device, DeviceStatus status, List<SubGateway> subGatewayList) {
        //获取设备协议编码
        /* 设备上线 处理影子值*/
        if (status.equals(DeviceStatus.ONLINE) && EnableEnum.ENABLE.getType().equals(device.getIsShadow())) {
            if (CollectionUtils.isEmpty(subGatewayList)) {
                this.handlerSingleShadow(device);
            } else {
                for (SubGateway subGateway : subGatewayList) {
                    DeviceMetaData deviceMetaDataCache = deviceCache.getDeviceMetaDataCache(subGateway.getSubClientId());
                    if (deviceMetaDataCache == null || deviceMetaDataCache.getDevice() == null) {
                        log.warn("handlerShadow: 子设备元数据缓存为空, subClientId={}", subGateway.getSubClientId());
                        continue;
                    }
                    this.handlerSingleShadow(deviceMetaDataCache.getDevice());
                }
            }
        }
    }

    private void handlerSingleShadow(Device device) {
        ThingsModelShadow shadow = deviceService.getDeviceShadowThingsModel(device);
        if (shadow == null) {
            return;
        }
        List<ThingsModelSimpleItem> properties = shadow.getProperties();
        List<ThingsModelSimpleItem> functions = shadow.getFunctions();
        if (functions == null) {
            functions = new ArrayList<>();
        }
        if (properties != null) {
            functions.addAll(properties);
        }
        if (!CollectionUtils.isEmpty(functions)) {
            for (ThingsModelSimpleItem function : functions) {
                MQSendMessageBo bo = new MQSendMessageBo();
                bo.setIsShadow(Boolean.FALSE);
                bo.setIdentifier(function.getId());
                bo.setSerialNumber(device.getSerialNumber());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(function.getId(), function.getValue());
                bo.setParams(jsonObject);
                bo.setValue(function.getValue());
                bo.setDelay(1000L);
                long id = snowflakeIdWorker.nextId();
                bo.setMessageId(id + "");
                //发送到MQ处理
                MessageProducer.sendFunctionInvoke(bo);
            }
        }
    }
}
