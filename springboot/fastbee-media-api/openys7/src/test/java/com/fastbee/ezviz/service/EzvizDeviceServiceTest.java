package com.fastbee.ezviz.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.fastbee.ezviz.client.EzvizHttpClient;
import com.fastbee.ezviz.exception.EzvizApiException;
import com.fastbee.ezviz.model.EzvizBaseResponse;
import com.fastbee.ezviz.model.EzvizCameraInfo;
import com.fastbee.ezviz.model.EzvizDeviceInfo;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;

/**
 * EzvizDeviceService 单元测试
 * 测试设备列表查询、设备详情、摄像头列表、设备重命名等功能
 *
 * @author fastbee
 */
@DisplayName("萤石云设备管理服务单元测试")
public class EzvizDeviceServiceTest extends BaseMockitoUnitTest {

    @Mock
    private EzvizHttpClient httpClient;

    @InjectMocks
    private EzvizDeviceService deviceService;

    @Captor
    private ArgumentCaptor<Map<String, String>> paramsCaptor;

    // ======================== listDevices 测试 ========================

    @Test
    @DisplayName("listDevices - 成功返回设备列表")
    public void testListDevices_success() throws Exception {
        EzvizDeviceInfo device1 = buildDeviceInfo("D001", "Camera-1");
        EzvizDeviceInfo device2 = buildDeviceInfo("D002", "Camera-2");
        List<EzvizDeviceInfo> deviceList = Arrays.asList(device1, device2);

        Object listResp = buildDeviceListResponse("200", deviceList);

        when(httpClient.postWithToken(eq("/api/lapp/device/list"), anyMap(), any()))
                .thenAnswer(inv -> listResp);

        List<EzvizDeviceInfo> result = deviceService.listDevices(0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("D001", result.get(0).getDeviceSerial());
        assertEquals("D002", result.get(1).getDeviceSerial());
    }

    @Test
    @DisplayName("listDevices - pageSize 超过50时，自动限制为50")
    public void testListDevices_pageSizeCapped() throws Exception {
        Object listResp = buildDeviceListResponse("200", Collections.emptyList());
        when(httpClient.postWithToken(eq("/api/lapp/device/list"), paramsCaptor.capture(), any()))
                .thenAnswer(inv -> listResp);

        deviceService.listDevices(0, 200);

        Map<String, String> capturedParams = paramsCaptor.getValue();
        assertEquals("50", capturedParams.get("pageSize"));
    }

    @Test
    @DisplayName("listDevices - 返回空列表时，返回空集合而非 null")
    public void testListDevices_emptyData_returnsEmptyList() throws Exception {
        Object listResp = buildDeviceListResponse("200", null);
        when(httpClient.postWithToken(eq("/api/lapp/device/list"), anyMap(), any()))
                .thenAnswer(inv -> listResp);

        List<EzvizDeviceInfo> result = deviceService.listDevices(0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("listDevices - API 返回错误码，抛出 EzvizApiException")
    public void testListDevices_apiError_throwsException() {
        when(httpClient.postWithToken(eq("/api/lapp/device/list"), anyMap(), any()))
                .thenThrow(new EzvizApiException("设备列表获取失败", "20006"));

        EzvizApiException ex = assertThrows(EzvizApiException.class,
                () -> deviceService.listDevices(0, 10));
        assertEquals("20006", ex.getErrorCode());
    }

    // ======================== getDeviceInfo 测试 ========================

    @Test
    @DisplayName("getDeviceInfo - 成功返回设备详情")
    public void testGetDeviceInfo_success() throws Exception {
        EzvizDeviceInfo device = buildDeviceInfo("DS-TEST-001", "测试摄像头");
        device.setStatus(1);
        device.setDeviceType("C6");

        Object infoResp = buildDeviceInfoResponse("200", device);
        when(httpClient.postWithToken(eq("/api/lapp/device/info"), anyMap(), any()))
                .thenAnswer(inv -> infoResp);

        EzvizDeviceInfo result = deviceService.getDeviceInfo("DS-TEST-001");

        assertNotNull(result);
        assertEquals("DS-TEST-001", result.getDeviceSerial());
        assertEquals("测试摄像头", result.getDeviceName());
        assertEquals(1, result.getStatus());
    }

    @Test
    @DisplayName("getDeviceInfo - 设备不存在，API 抛出异常")
    public void testGetDeviceInfo_notFound_throwsException() {
        when(httpClient.postWithToken(eq("/api/lapp/device/info"), anyMap(), any()))
                .thenThrow(new EzvizApiException("设备不存在", "20301"));

        EzvizApiException ex = assertThrows(EzvizApiException.class,
                () -> deviceService.getDeviceInfo("NONEXISTENT"));
        assertEquals("20301", ex.getErrorCode());
    }

    // ======================== listCameras 测试 ========================

    @Test
    @DisplayName("listCameras - 成功返回摄像头列表")
    public void testListCameras_success() throws Exception {
        EzvizCameraInfo cam1 = buildCameraInfo("D001", 1, "通道1");
        EzvizCameraInfo cam2 = buildCameraInfo("D001", 2, "通道2");

        Object cameraResp = buildCameraListResponse("200", Arrays.asList(cam1, cam2));
        when(httpClient.postWithToken(eq("/api/lapp/camera/list"), anyMap(), any()))
                .thenAnswer(inv -> cameraResp);

        List<EzvizCameraInfo> result = deviceService.listCameras("D001");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getChannelNo());
        assertEquals(2, result.get(1).getChannelNo());
    }

    @Test
    @DisplayName("listCameras - 无摄像头数据，返回空集合")
    public void testListCameras_emptyData_returnsEmptyList() throws Exception {
        Object cameraResp = buildCameraListResponse("200", null);
        when(httpClient.postWithToken(eq("/api/lapp/camera/list"), anyMap(), any()))
                .thenAnswer(inv -> cameraResp);

        List<EzvizCameraInfo> result = deviceService.listCameras("D001");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ======================== renameDevice 测试 ========================

    @Test
    @DisplayName("renameDevice - 成功重命名设备")
    public void testRenameDevice_success() throws Exception {
        EzvizBaseResponse baseResp = new EzvizBaseResponse();
        baseResp.setCode("200");
        baseResp.setMsg("操作成功");
        when(httpClient.postWithToken(eq("/api/lapp/device/name/update"), anyMap(), any()))
                .thenReturn(baseResp);

        // 验证不抛异常
        assertDoesNotThrow(() -> deviceService.renameDevice("D001", "新名称"));
        verify(httpClient, times(1)).postWithToken(eq("/api/lapp/device/name/update"), anyMap(), any());
    }

    @Test
    @DisplayName("renameDevice - API 失败，抛出 EzvizApiException")
    public void testRenameDevice_failure_throwsException() {
        when(httpClient.postWithToken(eq("/api/lapp/device/name/update"), anyMap(), any()))
                .thenThrow(new EzvizApiException("重命名失败", "10401"));

        EzvizApiException ex = assertThrows(EzvizApiException.class,
                () -> deviceService.renameDevice("D001", "新名称"));
        assertEquals("10401", ex.getErrorCode());
    }

    // ======================== 辅助方法 ========================

    private EzvizDeviceInfo buildDeviceInfo(String serial, String name) {
        EzvizDeviceInfo info = new EzvizDeviceInfo();
        info.setDeviceSerial(serial);
        info.setDeviceName(name);
        return info;
    }

    private EzvizCameraInfo buildCameraInfo(String serial, int channelNo, String channelName) {
        EzvizCameraInfo info = new EzvizCameraInfo();
        info.setDeviceSerial(serial);
        info.setChannelNo(channelNo);
        info.setChannelName(channelName);
        return info;
    }

    /**
     * 通过反射构建 EzvizDeviceListResponse（package-private 内部类）
     */
    @SuppressWarnings("unchecked")
    private Object buildDeviceListResponse(String code, List<EzvizDeviceInfo> data) throws Exception {
        Class<?> clazz = Class.forName("com.fastbee.ezviz.service.EzvizDeviceService$EzvizDeviceListResponse");
        Object resp = clazz.getDeclaredConstructor().newInstance();
        clazz.getMethod("setCode", String.class).invoke(resp, code);
        if (data != null) {
            Method setData = clazz.getMethod("setData", List.class);
            setData.invoke(resp, data);
        }
        return resp;
    }

    /**
     * 通过反射构建 EzvizDeviceInfoResponse（package-private 内部类）
     */
    private Object buildDeviceInfoResponse(String code, EzvizDeviceInfo data) throws Exception {
        Class<?> clazz = Class.forName("com.fastbee.ezviz.service.EzvizDeviceService$EzvizDeviceInfoResponse");
        Object resp = clazz.getDeclaredConstructor().newInstance();
        clazz.getMethod("setCode", String.class).invoke(resp, code);
        if (data != null) {
            clazz.getMethod("setData", EzvizDeviceInfo.class).invoke(resp, data);
        }
        return resp;
    }

    /**
     * 通过反射构建 EzvizCameraListResponse（package-private 内部类）
     */
    @SuppressWarnings("unchecked")
    private Object buildCameraListResponse(String code, List<EzvizCameraInfo> data) throws Exception {
        Class<?> clazz = Class.forName("com.fastbee.ezviz.service.EzvizDeviceService$EzvizCameraListResponse");
        Object resp = clazz.getDeclaredConstructor().newInstance();
        clazz.getMethod("setCode", String.class).invoke(resp, code);
        if (data != null) {
            clazz.getMethod("setData", List.class).invoke(resp, data);
        }
        return resp;
    }
}
