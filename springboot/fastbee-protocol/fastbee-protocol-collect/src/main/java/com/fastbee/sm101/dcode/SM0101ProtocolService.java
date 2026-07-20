package com.fastbee.sm101.dcode;

import java.util.*;

import jakarta.annotation.Resource;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fastbee.common.annotation.SysProtocol;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.mq.DeviceReport;
import com.fastbee.common.extend.core.domin.mq.MQSendMessageBo;
import com.fastbee.common.extend.core.domin.mq.message.DeviceData;
import com.fastbee.common.extend.core.domin.mq.message.FunctionCallBackBo;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.protocol.base.protocol.IProtocol;
import com.fastbee.sm101.model.SM0101Message;

/**
 * SM0101协议服务
 */
@Slf4j
@Component
@SysProtocol(name = "SM0101流量计协议",
            protocolCode = "SM0101",
            description = "SM0101流量计远传协议")
public class SM0101ProtocolService implements IProtocol {

    @Resource
    private SM0101Decoder sm0101Decoder;

    @Resource
    private SM0101Encoder sm0101Encoder;

    @Override
    public DeviceReport decode(DeviceData data, String clientId) {
        try {
            DeviceReport report = new DeviceReport();
            SM0101Message message = sm0101Decoder.decode(data);

            // 转换为物模型数据
            List<ThingsModelSimpleItem> items = convertToThingsModel(message);

            report.setThingsModelSimpleItem(items);
            report.setSerialNumber(message.getImei());
            report.setMessageId("100");
            report.setIsPackage(true);
            report.setUserId(message.getDeviceAddress());
            report.setSources(ByteBufUtil.hexDump(data.getBuf()));

            return report;
        } catch (Exception e) {
            log.error("=> SM0101协议数据解析出错", e);
            throw new ServiceException(StringUtils.format(
                    MessageUtils.message("protocol.data.parse.error"), e));
        }
    }

    @Override
    public FunctionCallBackBo encode(MQSendMessageBo message) {
        try {
            FunctionCallBackBo callBack = new FunctionCallBackBo();

            // 根据下发的指令类型构建响应消息
            SM0101Message response = buildResponseMessage(message);

            // 编码为字节流
            ByteBuf buf = sm0101Encoder.encode(response);
            byte[] result = ByteBufUtil.getBytes(buf);

            // 释放ByteBuf
            ReferenceCountUtil.release(buf);

            callBack.setSources(ByteBufUtil.hexDump(buf));
            callBack.setMessage(result);
            return callBack;

        } catch (Exception e) {
            log.error("=> SM0101协议指令编码异常, device={}, data={}",
                    message.getSerialNumber(), message.getParams());
            return null;
        }
    }

    @Override
    public byte[] encodeCallBack(Object message) {
        DeviceData deviceData = (DeviceData) message;
        DeviceReport report = (DeviceReport) deviceData.getBody();
        long address = report.getUserId();
        SM0101Message sm0101Message = new SM0101Message();
        sm0101Message.setDeviceAddress(address);
        sm0101Message.setCommandCode((short) 0x01);
        ByteBuf buf = sm0101Encoder.encode(sm0101Message);
        return ByteBufUtil.getBytes(buf);
    }

    /**
     * 将协议数据转换为物模型数据
     */
    private List<ThingsModelSimpleItem> convertToThingsModel(SM0101Message message) {
        List<ThingsModelSimpleItem> items = new ArrayList<>();

        // 根据指令码处理不同数据
        switch (message.getCommandCode()) {
            case 0x00: // 登录/心跳
                items.addAll(convertLoginHeartbeatData(message));
                break;
            case 0x01: // 确认/否认
                items.addAll(convertAckNakData(message));
                break;
        }

        return items;
    }

    /**
     * 转换登录/心跳数据
     */
    private List<ThingsModelSimpleItem> convertLoginHeartbeatData(SM0101Message message) {
        List<ThingsModelSimpleItem> items = new ArrayList<>();

        addItem(items, "imei", message.getImei());
        addItem(items, "signalStrength", message.getSignalStrength());
        addItem(items, "iccid", message.getIccid());
        addItem(items, "customSerial", message.getCustomSerial());

        // 状态寄存器各标志位
        SM0101Message.StatusFlags statusFlags = message.getStatusFlags();
        if (statusFlags != null) {
            addItem(items, "lowerLimitAlarm", statusFlags.getLowerLimitAlarm());
            addItem(items, "upperLimitAlarm", statusFlags.getUpperLimitAlarm());
            addItem(items, "calibrationStatus", statusFlags.getCalibrationStatus());
            addItem(items, "lowBattery", statusFlags.getLowBattery());
            addItem(items, "emptyTubeAlarm", statusFlags.getEmptyTubeAlarm());
            addItem(items, "excitationAlarm", statusFlags.getExcitationAlarm());
        }

        addItem(items, "pressure", message.getPressure());
        addItem(items, "flowSample", message.getFlowSample());
        addItem(items, "emptyTubeSample", message.getEmptyTubeSample());
        addItem(items, "excitationSample", message.getExcitationSample());

        // 单位信息
        SM0101Message.UnitInfo unitInfo = new SM0101Message.UnitInfo(message.getUnitCode());
        addItem(items, "totalUnit", unitInfo.getTotalUnit());
        addItem(items, "instantUnit", unitInfo.getInstantUnit());

        addItem(items, "flowVelocity", message.getFlowVelocity());
        addItem(items, "instantFlow", message.getInstantFlow());
        addItem(items, "forwardTotal", formatCumulativeFlow(message.getForwardTotal(),
                message.getDecimalPlaces()));
        addItem(items, "reverseTotal", formatCumulativeFlow(message.getReverseTotal(),
                message.getDecimalPlaces()));
        addItem(items, "totalFlow", formatCumulativeFlow(message.getTotalFlow(),
                message.getDecimalPlaces()));

        return items;
    }

    /**
     * 转换确认/否认数据
     */
    private List<ThingsModelSimpleItem> convertAckNakData(SM0101Message message) {
        List<ThingsModelSimpleItem> items = new ArrayList<>();
        addItem(items, "processResult", message.getProcessResult());
        addItem(items, "serverTime", message.getServerTime());
        return items;
    }

    /**
     * 构建响应消息
     */
    private SM0101Message buildResponseMessage(MQSendMessageBo message) {
        SM0101Message response = new SM0101Message();

        Map<String, Object> params = message.getParams();

        // 设置基本字段
        response.setDeviceAddress(Long.parseLong(message.getSerialNumber()));
        response.setControlCode((short) 0xC0); // 控制码：响应
        response.setCommandCode((short) 0x01); // 指令码：确认/否认
        response.setFrameCounter((short) 0x00);
        response.setExtension1((short) 0x00);

        // 设置处理结果
        if (params.containsKey("processResult")) {
            response.setProcessResult(Short.parseShort(params.get("processResult").toString()));
        } else {
            response.setProcessResult((short) 0); // 默认正常
        }

        // 设置服务器时间（如果需要）
        if (params.containsKey("serverTime")) {
            response.setServerTime(params.get("serverTime").toString());
        }

        return response;
    }

    /**
     * 格式化累计流量（根据小数位数）
     */
    private String formatCumulativeFlow(Long value, Short decimalPlaces) {
        if (value == null) return "0";

        int places = (decimalPlaces != null ? (decimalPlaces >> 8) & 0xFF : 0);
        if (places > 0) {
            double divisor = Math.pow(10, places);
            return String.format("%." + places + "f", value / divisor);
        }
        return value.toString();
    }

    /**
     * 添加物模型项
     */
    private void addItem(List<ThingsModelSimpleItem> items, String id, Object value) {
        if (value != null) {
            ThingsModelSimpleItem item = new ThingsModelSimpleItem();
            item.setId(id);
            item.setValue(value.toString());
            item.setTs(new Date());
            items.add(item);
        }
    }
}
