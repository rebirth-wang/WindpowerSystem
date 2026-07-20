package com.fastbee.controller.device;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import com.fastbee.iot.model.vo.DeviceVO;

/**
 * GB28181接入排障数据构建器。
 */
public final class DeviceAccessDiagnosticsBuilder {

    private static final String GB_DEVICE_STATUS_LATEST = "gb28181:device:status:latest:";
    private static final String GB_DEVICE_STATUS_HISTORY = "gb28181:device:status:history:";
    private static final DateTimeFormatter RECORD_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final long STALE_MINUTES = 10L;

    private DeviceAccessDiagnosticsBuilder() {
    }

    public static String buildDeviceStatusLatestKey(String deviceId) {
        return GB_DEVICE_STATUS_LATEST + deviceId;
    }

    public static String buildDeviceStatusHistoryKey(String deviceId) {
        return GB_DEVICE_STATUS_HISTORY + deviceId;
    }

    public static Map<String, Object> build(DeviceVO deviceVO, Object latestRaw, List<Object> historyRaw) {
        String serialNumber = deviceVO.getSerialNumber();
        List<JSONObject> history = parseHistory(historyRaw);
        JSONObject latest = parseRecord(latestRaw);
        Map<String, JSONObject> latestByEvent = latestByEvent(history);
        if (latest != null && !latestByEvent.containsKey(latest.getString("event"))) {
            latestByEvent.put(latest.getString("event"), latest);
        }

        JSONObject register = latestByEvent.get("REGISTER");
        JSONObject keepalive = latestByEvent.get("KEEPALIVE");
        JSONObject catalog = latestByEvent.get("CATALOG");
        JSONObject deviceStatus = latestByEvent.get("DEVICE_STATUS");

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("device", buildDevice(deviceVO));
        data.put("latest", buildLatest(register, keepalive, catalog, deviceStatus));
        data.put("history", history);
        data.put("diagnosisItems", diagnose(history, register, keepalive, catalog, deviceStatus));
        data.put("redisKeys", buildRedisKeys(serialNumber));
        return data;
    }

    public static List<Map<String, Object>> diagnose(List<JSONObject> history, JSONObject register,
                                                     JSONObject keepalive, JSONObject catalog,
                                                     JSONObject deviceStatus) {
        List<Map<String, Object>> items = new ArrayList<>();
        boolean emptyHistory = history == null || history.isEmpty();
        if (emptyHistory) {
            items.add(item("danger", "未发现接入缓存",
                    "Redis中没有注册、心跳或目录缓存。请检查设备是否向平台IP和SIP端口发起GB28181接入，确认UDP/TCP协议、防火墙/NAT、SIP服务是否正常。",
                    "history为空"));
            return items;
        }

        if (register == null) {
            items.add(item("danger", "无注册报文",
                    "平台未记录到REGISTER。请检查设备端平台IP、SIP接入端口、UDP/TCP协议、网络路由、防火墙/NAT以及SIP服务监听状态。",
                    "未找到REGISTER记录"));
        } else {
            String registerStatus = register.getString("status");
            if (Objects.equals("UNAUTHORIZED", registerStatus) || Objects.equals("FORBIDDEN", registerStatus)) {
                items.add(item("danger", "注册鉴权失败",
                        "请检查国标接入密码、服务端SIP ID、domain和设备编号是否与平台配置一致，修改后让设备重新注册。",
                        "REGISTER.status=" + registerStatus));
            } else if (Objects.equals("DEVICE_NOT_FOUND", registerStatus)) {
                items.add(item("danger", "平台注册设备不存在",
                        "设备已经向平台发起注册，但平台未匹配到设备。请确认平台已添加该设备，设备编号与国标设备ID一致。",
                        "REGISTER.status=DEVICE_NOT_FOUND"));
            } else if (Objects.equals("SIP_CONFIG_NOT_FOUND", registerStatus)) {
                items.add(item("danger", "未找到SIP配置",
                        "请检查平台国标SIP配置是否存在，设备编号、服务器ID和domain是否能匹配到接入配置。",
                        "REGISTER.status=SIP_CONFIG_NOT_FOUND"));
            } else if (Objects.equals("ONLINE", registerStatus)) {
                items.add(item("success", "注册正常",
                        "最近一次REGISTER已成功，若仍无法接入请继续查看心跳和通道目录状态。",
                        "REGISTER.status=ONLINE"));
            }
        }

        if (keepalive == null) {
            items.add(item("warning", "有注册但无心跳",
                    "请检查设备Keepalive周期、保活开关、网络稳定性、防火墙会话保持，以及设备是否在注册后继续向平台发送MESSAGE心跳。",
                    "未找到KEEPALIVE记录"));
        } else if (Objects.equals("UNKNOWN_DEVICE", keepalive.getString("status"))) {
            items.add(item("danger", "心跳设备未匹配",
                    "平台收到心跳但未找到对应国标设备。请重点检查设备编号、平台设备编号和绑定关系。",
                    "KEEPALIVE.status=UNKNOWN_DEVICE"));
        } else {
            items.add(item("success", "心跳正常",
                    "最近一次Keepalive已记录，设备保活链路可达。",
                    "KEEPALIVE.status=" + keepalive.getString("status")));
        }

        if (catalog == null) {
            items.add(item("warning", "通道目录未更新",
                    "未发现Catalog目录缓存。请检查设备目录上报、通道ID/国标编码、设备端通道启用状态、目录订阅和刷新配置。",
                    "未找到CATALOG记录"));
        } else {
            int total = catalog.getIntValue("total");
            int online = catalog.getIntValue("online");
            if (total == 0) {
                items.add(item("warning", "通道数量为0",
                        "设备已响应Catalog但未上报通道。请检查NVR/IPC通道是否启用、通道ID是否按国标规则填写、设备端目录是否允许上报。",
                        "CATALOG.total=0"));
            } else if (online == 0) {
                items.add(item("warning", "通道全部离线",
                        "目录中存在通道但无在线通道。请检查摄像头通道状态、通道编码、设备端通道配置和网络连接。",
                        "CATALOG.total=" + total + ", online=0"));
            } else {
                items.add(item("success", "通道目录正常",
                        "最近一次Catalog已上报通道，可继续排查播放或推流链路。",
                        "CATALOG.total=" + total + ", online=" + online));
            }
        }

        JSONObject newest = newestRecord(history);
        if (newest != null && isStale(newest.getString("recordTime"))) {
            items.add(item("warning", "最近缓存已超时",
                    "最近一条接入缓存超过" + STALE_MINUTES + "分钟未更新。请检查设备是否仍在线、Redis缓存是否过期、服务是否已部署最新记录逻辑。",
                    "recordTime=" + newest.getString("recordTime")));
        }
        if (deviceStatus == null) {
            items.add(item("info", "无设备状态响应",
                    "暂未记录DeviceStatus响应。若需要确认设备工作状态，可触发设备状态查询后刷新排障信息。",
                    "未找到DEVICE_STATUS记录"));
        }
        return items;
    }

    private static Map<String, Object> buildDevice(DeviceVO deviceVO) {
        Map<String, Object> device = new LinkedHashMap<>();
        device.put("deviceId", deviceVO.getDeviceId());
        device.put("deviceName", deviceVO.getDeviceName());
        device.put("serialNumber", deviceVO.getSerialNumber());
        device.put("productName", deviceVO.getProductName());
        device.put("status", deviceVO.getStatus());
        device.put("transport", deviceVO.getTransport());
        device.put("deviceType", deviceVO.getDeviceType());
        return device;
    }

    private static Map<String, Object> buildLatest(JSONObject register, JSONObject keepalive,
                                                   JSONObject catalog, JSONObject deviceStatus) {
        Map<String, Object> latest = new LinkedHashMap<>();
        latest.put("register", register);
        latest.put("keepalive", keepalive);
        latest.put("catalog", catalog);
        latest.put("deviceStatus", deviceStatus);
        return latest;
    }

    private static Map<String, String> buildRedisKeys(String serialNumber) {
        Map<String, String> keys = new LinkedHashMap<>();
        keys.put("latest", buildDeviceStatusLatestKey(serialNumber));
        keys.put("history", buildDeviceStatusHistoryKey(serialNumber));
        return keys;
    }

    private static List<JSONObject> parseHistory(List<Object> historyRaw) {
        if (historyRaw == null || historyRaw.isEmpty()) {
            return new ArrayList<>();
        }
        List<JSONObject> history = new ArrayList<>();
        for (Object raw : historyRaw) {
            JSONObject record = parseRecord(raw);
            if (record != null) {
                history.add(record);
            }
        }
        history.sort(Comparator.comparing(DeviceAccessDiagnosticsBuilder::recordTime).reversed());
        return history;
    }

    private static JSONObject parseRecord(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof JSONObject jsonObject) {
            return jsonObject;
        }
        if (raw instanceof Map<?, ?> map) {
            JSONObject jsonObject = new JSONObject();
            map.forEach((key, value) -> jsonObject.put(String.valueOf(key), value));
            return jsonObject;
        }
        try {
            return JSON.parseObject(String.valueOf(raw));
        } catch (Exception e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", "UNKNOWN");
            jsonObject.put("status", "PARSE_FAILED");
            jsonObject.put("message", "缓存内容解析失败");
            jsonObject.put("raw", String.valueOf(raw));
            return jsonObject;
        }
    }

    private static Map<String, JSONObject> latestByEvent(List<JSONObject> history) {
        if (history == null || history.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, JSONObject> latestByEvent = new LinkedHashMap<>();
        for (JSONObject item : history) {
            String event = item.getString("event");
            if (event != null && !latestByEvent.containsKey(event)) {
                latestByEvent.put(event, item);
            }
        }
        return latestByEvent;
    }

    private static JSONObject newestRecord(List<JSONObject> history) {
        if (history == null || history.isEmpty()) {
            return null;
        }
        return history.get(0);
    }

    private static LocalDateTime recordTime(JSONObject record) {
        String recordTime = record == null ? null : record.getString("recordTime");
        if (recordTime == null) {
            return LocalDateTime.MIN;
        }
        try {
            return LocalDateTime.parse(recordTime, RECORD_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return LocalDateTime.MIN;
        }
    }

    private static boolean isStale(String recordTime) {
        if (recordTime == null) {
            return false;
        }
        try {
            return LocalDateTime.parse(recordTime, RECORD_TIME_FORMATTER)
                    .isBefore(LocalDateTime.now().minusMinutes(STALE_MINUTES));
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static Map<String, Object> item(String level, String title, String suggestion, String evidence) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("level", level);
        item.put("title", title);
        item.put("suggestion", suggestion);
        item.put("evidence", evidence);
        return item;
    }
}
