package com.fastbee.hp;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fastbee.common.annotation.SysProtocol;
import com.fastbee.common.constant.FastBeeConstant;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.mq.DeviceReport;
import com.fastbee.common.extend.core.domin.mq.MQSendMessageBo;
import com.fastbee.common.extend.core.domin.mq.SubDeviceBo;
import com.fastbee.common.extend.core.domin.mq.message.DeviceData;
import com.fastbee.common.extend.core.domin.mq.message.FunctionCallBackBo;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.domain.ModbusConfig;
import com.fastbee.iot.domain.SubGateway;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.modbusToJson.FYModel;
import com.fastbee.protocol.base.protocol.IProtocol;

/**
 * @author gsb
 * @date 2023/8/14 16:04
 */
@Slf4j
@Component
@SysProtocol(name = "Modbus转Json解析协议-华普物联",protocolCode = FastBeeConstant.PROTOCOL.ModbusToJsonHP,description = "modbus转json解析协议-华普物联")
public class ModbusToJsonHPProtocolService implements IProtocol {

    @Resource
    private IDeviceCache deviceCache;

    /**
     * 上报数据格式:              <p>
     *  device1：   从机1标识     <p>
     *  name：      物模型标识符   <p>
     *  value：     上报值        <p>
     * {
     * 	"device1": [
     *                {
     * 			"name": "J2",
     * 			"value": 8.331631
     *        },
     *        {
     * 			"name": "J1",
     * 			"value": -130.123718
     *        }
     * 	],
     * 	"device2": [
     *        {
     * 			"name": "J4",
     * 			"value": -16.350224
     *        },
     *        {
     * 			"name": "J3",
     * 			"value": 94.769806
     *        }
     * 	]
     * }
     *
     * 下发报文格式<p>
     * device   从机编号  <p>
     * name     标识符    <p>
     * value    值        <p>
     * serNo    流水号    <p>
     * {
     * 	"device": 1,
     * 	"name": "template",
     * 	"value": 111,
     * 	"serNo": "213245489543789"
     * }
     * </p>
     *
     * 下发指令回复格式<p>
     * serNo   平台的流水号，用于对应回复消息   <p>
     * ack     下发指令状态 0是失败 1是成功    <p>
     *   {
     *  "serNo": "213245489543789",
     *  "ack": 1
     * }
     * </p>
     *
     */
    @Override
    public DeviceReport decode(DeviceData deviceData, String clientId) {
        try {
            DeviceReport reportMessage = new DeviceReport();
            String data = new String(deviceData.getData(),StandardCharsets.UTF_8);
            List<ThingsModelSimpleItem> result = new ArrayList<>();
            Map<String,Object> values = JSON.parseObject(data, Map.class);
            if (values.containsKey("serNo")){
                reportMessage.setIsReply(true);
                reportMessage.setProtocolCode(FastBeeConstant.PROTOCOL.ModbusToJsonHP);
                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    ThingsModelSimpleItem simpleItem = new ThingsModelSimpleItem();
                    simpleItem.setTs(DateUtils.getNowDate());
                    simpleItem.setId(entry.getKey());
                    simpleItem.setValue(entry.getValue()+"");
                    if (entry.getKey().equals("serNo")){
                        reportMessage.setMgId(entry.getValue()+"");
                    }
                    result.add(simpleItem);
                }
            }else {
                List<SubDeviceBo> subDeviceBoList = new ArrayList<>();
                DeviceMetaData deviceMetaDataCache = deviceCache.getDeviceMetaDataCache(clientId);
                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    String slaveKey = entry.getKey();
                    Integer address = StringUtils.matcherNum(slaveKey);
                    List<FYModel> valueList = JSON.parseArray(JSON.toJSONString(entry.getValue()), FYModel.class);
                    SubDeviceBo subDeviceBo = this.matchSubDev(address, deviceMetaDataCache.getSubGatewayList());
                    List<ThingsModelSimpleItem> resultSub = new ArrayList<>();
                    for (FYModel fyModel : valueList) {
                        ThingsModelSimpleItem item = new ThingsModelSimpleItem();
                        item.setTs(DateUtils.getNowDate());
                        item.setValue(fyModel.getValue());
                        item.setId(fyModel.getName());
                        resultSub.add(item);
                    }
                    if (Objects.isNull(subDeviceBo)) {
                        result.addAll(resultSub);
                    } else {
                        subDeviceBo.setThingsModelSimpleItem(resultSub);
                        subDeviceBoList.add(subDeviceBo);
                    }
                }
                reportMessage.setSubDeviceBoList(subDeviceBoList);
            }
            reportMessage.setThingsModelSimpleItem(result);
            reportMessage.setSources(data);
            return reportMessage;
        }catch (Exception e){
            throw new ServiceException(StringUtils.format(MessageUtils.message("protocol.data.parse.exception"), e.getMessage()));
        }
    }

    private SubDeviceBo matchSubDev(Integer address, List<SubGateway> subGatewayList) {
        for (SubGateway subGateway : subGatewayList) {
            if (subGateway.getAddress().equals(address.toString())) {
                SubDeviceBo subDeviceBo = new SubDeviceBo();
                subDeviceBo.setSubProductId(subGateway.getSubProductId());
                subDeviceBo.setSubClientId(subGateway.getSubClientId());
                subDeviceBo.setAddress(subGateway.getAddress());
                return subDeviceBo;
            }
        }
        return null;
    }


    @Override
    public FunctionCallBackBo encode(MQSendMessageBo message) {
        try {
            FunctionCallBackBo callBack = new FunctionCallBackBo();
            String thingsModel = message.getThingsModel();
            ThingsModelValueItem item = JSONObject.parseObject(thingsModel, ThingsModelValueItem.class);
            ModbusConfig modbusConfig = item.getConfig();
            JSONObject params = new JSONObject();
            params.put("device",modbusConfig.getAddress());
            params.put("name",message.getIdentifier());
            params.put("value",message.getValue());
            params.put("serNo",message.getMessageId());
            String msg = JSONObject.toJSONString(params);
            callBack.setSources(msg);
            callBack.setMessage(msg.getBytes());
            return callBack;
        }catch (Exception e){
            log.error("=>指令编码异常,device={}",message.getSerialNumber());
            return null;
        }
    }

}
