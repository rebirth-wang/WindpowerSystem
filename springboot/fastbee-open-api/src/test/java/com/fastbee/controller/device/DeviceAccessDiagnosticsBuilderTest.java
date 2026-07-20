package com.fastbee.controller.device;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GB28181接入排障规则")
public class DeviceAccessDiagnosticsBuilderTest {

    @Test
    @DisplayName("空缓存提示未发现接入缓存")
    public void shouldWarnWhenHistoryEmpty() {
        List<Map<String, Object>> items = DeviceAccessDiagnosticsBuilder.diagnose(new ArrayList<>(), null, null, null, null);

        assertContains(items, "未发现接入缓存");
    }

    @Test
    @DisplayName("注册403提示鉴权失败")
    public void shouldWarnWhenRegisterForbidden() {
        JSONObject register = record("REGISTER", "FORBIDDEN");

        List<Map<String, Object>> items = DeviceAccessDiagnosticsBuilder.diagnose(List.of(register), register, null, null, null);

        assertContains(items, "注册鉴权失败");
    }

    @Test
    @DisplayName("设备不存在提示平台注册设备不存在")
    public void shouldWarnWhenDeviceNotFound() {
        JSONObject register = record("REGISTER", "DEVICE_NOT_FOUND");

        List<Map<String, Object>> items = DeviceAccessDiagnosticsBuilder.diagnose(List.of(register), register, null, null, null);

        assertContains(items, "平台注册设备不存在");
    }

    @Test
    @DisplayName("有注册无心跳提示保活排查")
    public void shouldWarnWhenKeepaliveMissing() {
        JSONObject register = record("REGISTER", "ONLINE");

        List<Map<String, Object>> items = DeviceAccessDiagnosticsBuilder.diagnose(List.of(register), register, null, null, null);

        assertContains(items, "有注册但无心跳");
    }

    @Test
    @DisplayName("未知设备心跳提示编号和绑定关系")
    public void shouldWarnWhenKeepaliveUnknownDevice() {
        JSONObject keepalive = record("KEEPALIVE", "UNKNOWN_DEVICE");

        List<Map<String, Object>> items = DeviceAccessDiagnosticsBuilder.diagnose(List.of(keepalive), null, keepalive, null, null);

        assertContains(items, "心跳设备未匹配");
    }

    @Test
    @DisplayName("Catalog通道数为0提示通道配置")
    public void shouldWarnWhenCatalogEmpty() {
        JSONObject register = record("REGISTER", "ONLINE");
        JSONObject keepalive = record("KEEPALIVE", "ONLINE");
        JSONObject catalog = record("CATALOG", "CATALOG_REPORTED");
        catalog.put("total", 0);
        catalog.put("online", 0);

        List<Map<String, Object>> items = DeviceAccessDiagnosticsBuilder.diagnose(
                List.of(register, keepalive, catalog), register, keepalive, catalog, null);

        assertContains(items, "通道数量为0");
    }

    @Test
    @DisplayName("注册心跳和通道正常时给出正常建议")
    public void shouldShowSuccessWhenAccessNormal() {
        JSONObject register = record("REGISTER", "ONLINE");
        JSONObject keepalive = record("KEEPALIVE", "ONLINE");
        JSONObject catalog = record("CATALOG", "CATALOG_REPORTED");
        catalog.put("total", 2);
        catalog.put("online", 1);

        List<Map<String, Object>> items = DeviceAccessDiagnosticsBuilder.diagnose(
                List.of(register, keepalive, catalog), register, keepalive, catalog, null);

        assertContains(items, "注册正常");
        assertContains(items, "心跳正常");
        assertContains(items, "通道目录正常");
    }

    private JSONObject record(String event, String status) {
        JSONObject record = new JSONObject();
        record.put("event", event);
        record.put("status", status);
        record.put("recordTime", "2999-01-01 00:00:00");
        return record;
    }

    private void assertContains(List<Map<String, Object>> items, String title) {
        assertTrue(items.stream().anyMatch(item -> title.equals(item.get("title"))));
    }
}
