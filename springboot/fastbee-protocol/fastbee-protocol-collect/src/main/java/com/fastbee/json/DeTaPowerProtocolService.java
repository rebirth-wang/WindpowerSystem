package com.fastbee.json;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fastbee.common.annotation.SysProtocol;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.mq.DeviceReport;
import com.fastbee.common.extend.core.domin.mq.MQSendMessageBo;
import com.fastbee.common.extend.core.domin.mq.message.DeviceData;
import com.fastbee.common.extend.core.domin.mq.message.FunctionCallBackBo;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.model.ThingsModels.ValueItem;
import com.fastbee.protocol.base.protocol.IProtocol;

/**
 * 德塔电源测试协议
 *
 * 上报数据格式:
 * {
 *   "id": "10c6a4841f1906161d",
 *   "time": "2026-05-07 14:51:00",
 *   "k0": [65535, 65535],
 *   "k1": [65535, 65535]
 * }
 *
 * 解析规则:
 * 仅展开 k + 数字 的数组字段，并映射为 k0_0、k0_1 这类标准物模型标识。
 */
@Slf4j
@Component
@SysProtocol(
        name = "德塔电源测试协议",
        protocolCode = "DeTaPowerTest",
        description = "德塔电源测试数据解析协议，支持将 k0...kn 数组数据展开为标准物模型格式")
public class DeTaPowerProtocolService implements IProtocol {

    private static final String PROTOCOL_CODE = "DeTaPowerTest";
    private static final int DEFAULT_REPORT_POINT_CAPACITY = 128;
    private static final String TIME_FIELD = "time";
    private static final String ITEM_REMARK = "德塔电源数据";
    private static final String COMMAND_REMARK = "德塔电源指令";
    private static final ZoneId REPORT_ZONE = ZoneId.systemDefault();
    private static final DateTimeFormatter REPORT_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public DeviceReport decode(DeviceData deviceData, String clientId) {
        try {
            String data = new String(deviceData.getData(), StandardCharsets.UTF_8);
            JSONObject payload = JSON.parseObject(data);
            Date ts = readReportTime(payload);
            List<ThingsModelSimpleItem> resultList = parsePayload(payload, ts);

            if (resultList.isEmpty()) {
                log.warn("DeTaPower decode: no k-array field found, clientId={}, data={}", clientId, data);
            } else if (log.isDebugEnabled()) {
                log.debug("DeTaPower decode success, clientId={}, points={}", clientId, resultList.size());
            }

            DeviceReport reportMessage = new DeviceReport();
            reportMessage.setThingsModelSimpleItem(resultList);
            reportMessage.setSerialNumber(clientId);
            reportMessage.setSources(data);
            reportMessage.setProtocolCode(PROTOCOL_CODE);
            return reportMessage;
        } catch (Exception e) {
            throw new ServiceException(StringUtils.format(MessageUtils.message("protocol.data.parse.exception"), e.getMessage()));
        }
    }

    private Date readReportTime(JSONObject payload) {
        Date fallback = DateUtils.getNowDate();
        if (payload == null) {
            return fallback;
        }
        return parseReportTime(payload.getString(TIME_FIELD), fallback);
    }

    private Date parseReportTime(String time, Date fallback) {
        if (StringUtils.isEmpty(time)) {
            return fallback;
        }
        try {
            return Date.from(LocalDateTime.parse(time, REPORT_TIME_FORMATTER)
                    .atZone(REPORT_ZONE)
                    .toInstant());
        } catch (Exception e) {
            log.warn("DeTaPower decode: invalid report time, time={}", time);
            return fallback;
        }
    }

    private List<ThingsModelSimpleItem> parsePayload(JSONObject payload, Date ts) {
        List<ThingsModelSimpleItem> resultList = new ArrayList<>(DEFAULT_REPORT_POINT_CAPACITY);
        if (payload == null || payload.isEmpty()) {
            return resultList;
        }

        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            if (isKArrayField(entry.getKey()) && entry.getValue() instanceof JSONArray) {
                readKArray((JSONArray) entry.getValue(), entry.getKey(), ts, resultList);
            }
        }
        return resultList;
    }

    private void readKArray(JSONArray values, String fieldName, Date ts, List<ThingsModelSimpleItem> resultList) {
        String idPrefix = fieldName + "_";
        for (int index = 0; index < values.size(); index++) {
            ThingsModelSimpleItem item = new ThingsModelSimpleItem();
            item.setId(idPrefix + index);
            item.setValue(values.get(index) == null ? null : String.valueOf(values.get(index)));
            item.setRemark(ITEM_REMARK);
            item.setTs(ts);
            resultList.add(item);
        }
    }

    private boolean isKArrayField(String fieldName) {
        if (fieldName == null || fieldName.length() < 2 || fieldName.charAt(0) != 'k') {
            return false;
        }
        for (int i = 1; i < fieldName.length(); i++) {
            char ch = fieldName.charAt(i);
            if (ch < '0' || ch > '9') {
                return false;
            }
        }
        return true;
    }

    @Override
    public FunctionCallBackBo encode(MQSendMessageBo message) {
        try {
            List<ValueItem> valueItems = new ArrayList<>();
            JSONObject params = message.getParams();
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    valueItems.add(buildValueItem(entry.getKey(), entry.getValue()));
                }
            } else if (StringUtils.isNotEmpty(message.getIdentifier())) {
                valueItems.add(buildValueItem(message.getIdentifier(), message.getValue()));
            }

            String msg = JSON.toJSONString(valueItems);
            FunctionCallBackBo callBack = new FunctionCallBackBo();
            callBack.setSources(msg);
            callBack.setMessage(msg.getBytes(StandardCharsets.UTF_8));
            return callBack;
        } catch (Exception e) {
            log.error("DeTaPower encode error, device={}, data={}", message.getSerialNumber(), message.getParams(), e);
            return null;
        }
    }

    private ValueItem buildValueItem(String id, Object value) {
        ValueItem valueItem = new ValueItem();
        valueItem.setId(id);
        valueItem.setValue(value == null ? null : String.valueOf(value));
        valueItem.setRemark(COMMAND_REMARK);
        return valueItem;
    }
}
