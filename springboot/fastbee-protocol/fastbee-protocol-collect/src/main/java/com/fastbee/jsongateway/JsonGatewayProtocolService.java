package com.fastbee.jsongateway;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.fastbee.common.annotation.SysProtocol;
import com.fastbee.common.constant.FastBeeConstant;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.mq.*;
import com.fastbee.common.extend.core.domin.mq.message.DeviceData;
import com.fastbee.common.extend.core.domin.mq.message.FunctionCallBackBo;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.enums.DeviceType;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.jsongateway.model.JsonGatewayPoint;
import com.fastbee.protocol.base.protocol.IProtocol;

/**
 * @author gsb
 * @date 2022/10/10 16:55
 */
@Slf4j
@Component
@SysProtocol(name = "JSON网关解析协议", protocolCode = FastBeeConstant.PROTOCOL.JsonGateway, description = "系统内置JSON网关解析协议")
public class JsonGatewayProtocolService implements IProtocol {

    @Resource
    private IDeviceCache deviceCache;

    /**
     * 上报数据格式 <p>
     [{
     "serialNumber":"164635379",
     "properties":{
            "latitude":"78°25'",
            "longitude":"25°47'"
        }
     }]
     */
    @Override
    public DeviceReport decode(DeviceData deviceData, String clientId) {
        try {
            DeviceReport reportMessage = new DeviceReport();
            // bytep[] 转String
            String data = new String(deviceData.getData(), StandardCharsets.UTF_8);
            List<JsonGatewayPoint> jsonGatewayPointList = JSON.parseArray(data, JsonGatewayPoint.class);
            // 处理子设备
            this.matchSubDev(reportMessage, jsonGatewayPointList);
            reportMessage.setSources(data);
            return reportMessage;
        } catch (Exception e) {
            throw new ServiceException(StringUtils.format(MessageUtils.message("protocol.data.parse.exception"), e));
        }
    }


    /**
     * 下发 [{"id":"switch","value":"0","remark":""}]
     *
     * @param message
     * @return
     */
    @Override
    public FunctionCallBackBo encode(MQSendMessageBo message) {
        try {
            FunctionCallBackBo callBack = new FunctionCallBackBo();
            JsonGatewayPoint jsonGatewayPoint = new JsonGatewayPoint();
            jsonGatewayPoint.setSerialNumber(CollectionUtils.isNotEmpty(message.getSubDeviceBoList()) ? message.getSubDeviceBoList().get(0).getSubClientId() : message.getSerialNumber());
            jsonGatewayPoint.setProperties(message.getParams());
            String msg = JSON.toJSONString(jsonGatewayPoint);
            callBack.setMessage(msg.getBytes());
            callBack.setSources(msg);
            return callBack;
        } catch (Exception e) {
            log.error("=>指令编码异常,device={},data={}", message.getSerialNumber(),
                    message.getParams());
            return null;
        }
    }

    /**
     * 匹配子设备编号
     */
    private void matchSubDev(DeviceReport report, List<JsonGatewayPoint> jsonGatewayPointList) {
        List<SubDeviceBo> subDeviceBoList = new ArrayList<>();
        for (JsonGatewayPoint jsonGatewayPoint : jsonGatewayPointList) {
            DeviceMetaData deviceMetaData = deviceCache.getDeviceMetaDataCache(jsonGatewayPoint.getSerialNumber());
            if (Objects.nonNull(deviceMetaData) && DeviceType.SUB_GATEWAY.getCode() == deviceMetaData.getProduct().getDeviceType()) {
                SubDeviceBo subDeviceBo = new SubDeviceBo();
                subDeviceBo.setSubProductId(deviceMetaData.getProduct().getProductId());
                subDeviceBo.setSubClientId(deviceMetaData.getDevice().getSerialNumber());
                subDeviceBo.setAddress(deviceMetaData.getSubGateway().getAddress());
//                GwDeviceBo gwDeviceBo = new GwDeviceBo();
//                gwDeviceBo.setProductId(deviceMetaData.getSubGateway().getParentProductId());
//                gwDeviceBo.setSerialNumber(deviceMetaData.getSubGateway().getParentClientId());
//                report.setGwDeviceBo(gwDeviceBo);
                Map<String, Object> map = jsonGatewayPoint.getProperties();
                List<ThingsModelSimpleItem> result = new ArrayList<>();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    ThingsModelSimpleItem item = new ThingsModelSimpleItem();
                    item.setTs(DateUtils.getNowDate());
                    item.setValue(entry.getValue()+"");
                    item.setId(entry.getKey());
                    item.setAddress(subDeviceBo.getAddress());
                    result.add(item);
                }
                subDeviceBo.setThingsModelSimpleItem(result);
                subDeviceBoList.add(subDeviceBo);
            }
        }
        report.setSubDeviceBoList(subDeviceBoList);
    }
}
