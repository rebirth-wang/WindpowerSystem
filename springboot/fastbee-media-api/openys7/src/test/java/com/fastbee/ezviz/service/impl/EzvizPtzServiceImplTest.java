package com.fastbee.ezviz.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import com.fastbee.ezviz.client.EzvizHttpClient;
import com.fastbee.ezviz.exception.EzvizApiException;
import com.fastbee.ezviz.model.EzvizBaseResponse;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.model.FrontEndControlCodeForPTZ;
import com.fastbee.media.model.FrontEndControlCodeForPreset;
import com.fastbee.media.model.Preset;

/**
 * EzvizPtzServiceImpl 单元测试
 * 测试云台控制方向映射、速度、预置点操作、不支持功能返回值
 *
 * @author fastbee
 */
@DisplayName("萤石云云台控制服务单元测试")
public class EzvizPtzServiceImplTest extends BaseMockitoUnitTest {

    @Mock
    private EzvizHttpClient httpClient;

    private EzvizPtzServiceImpl ptzService;

    @Captor
    private ArgumentCaptor<Map<String, String>> paramsCaptor;

    @BeforeEach
    public void setUp() {
        ptzService = new EzvizPtzServiceImpl(httpClient);
        // 默认 postWithToken 不抛异常
        EzvizBaseResponse okResp = new EzvizBaseResponse();
        okResp.setCode("200");
        when(httpClient.postWithToken(anyString(), anyMap(), any())).thenReturn(okResp);
    }

    // ======================== ptz 方向控制测试 ========================

    @Test
    @DisplayName("ptz - 向上运动（tilt=0, pan=null）→ direction=0")
    public void testPtz_tiltUp_direction0() {
        CommonChannel channel = buildChannel("D001", "1");
        FrontEndControlCodeForPTZ code = new FrontEndControlCodeForPTZ();
        code.setTilt(0); // 上
        when(httpClient.postWithToken(eq("/api/lapp/device/ptz/start"), paramsCaptor.capture(), any()))
                .thenReturn(okResponse());

        Boolean result = ptzService.ptz(channel, code);

        assertTrue(result);
        assertEquals("0", paramsCaptor.getValue().get("direction"));
    }

    @Test
    @DisplayName("ptz - 向下运动（tilt=1, pan=null）→ direction=1")
    public void testPtz_tiltDown_direction1() {
        CommonChannel channel = buildChannel("D001", "1");
        FrontEndControlCodeForPTZ code = new FrontEndControlCodeForPTZ();
        code.setTilt(1); // 下
        when(httpClient.postWithToken(eq("/api/lapp/device/ptz/start"), paramsCaptor.capture(), any()))
                .thenReturn(okResponse());

        assertTrue(ptzService.ptz(channel, code));
        assertEquals("1", paramsCaptor.getValue().get("direction"));
    }

    @Test
    @DisplayName("ptz - 向左运动（pan=0, tilt=null）→ direction=2")
    public void testPtz_panLeft_direction2() {
        CommonChannel channel = buildChannel("D001", "1");
        FrontEndControlCodeForPTZ code = new FrontEndControlCodeForPTZ();
        code.setPan(0); // 左
        when(httpClient.postWithToken(eq("/api/lapp/device/ptz/start"), paramsCaptor.capture(), any()))
                .thenReturn(okResponse());

        assertTrue(ptzService.ptz(channel, code));
        assertEquals("2", paramsCaptor.getValue().get("direction"));
    }

    @Test
    @DisplayName("ptz - 向右运动（pan=1, tilt=null）→ direction=3")
    public void testPtz_panRight_direction3() {
        CommonChannel channel = buildChannel("D001", "1");
        FrontEndControlCodeForPTZ code = new FrontEndControlCodeForPTZ();
        code.setPan(1); // 右
        when(httpClient.postWithToken(eq("/api/lapp/device/ptz/start"), paramsCaptor.capture(), any()))
                .thenReturn(okResponse());

        assertTrue(ptzService.ptz(channel, code));
        assertEquals("3", paramsCaptor.getValue().get("direction"));
    }

    @Test
    @DisplayName("ptz - 左上运动（tilt=0, pan=0）→ direction=4")
    public void testPtz_leftUp_direction4() {
        CommonChannel channel = buildChannel("D001", "1");
        FrontEndControlCodeForPTZ code = new FrontEndControlCodeForPTZ();
        code.setTilt(0);
        code.setPan(0); // 左上
        when(httpClient.postWithToken(eq("/api/lapp/device/ptz/start"), paramsCaptor.capture(), any()))
                .thenReturn(okResponse());

        assertTrue(ptzService.ptz(channel, code));
        assertEquals("4", paramsCaptor.getValue().get("direction"));
    }

    @Test
    @DisplayName("ptz - 右下运动（tilt=1, pan=1）→ direction=7")
    public void testPtz_rightDown_direction7() {
        CommonChannel channel = buildChannel("D001", "1");
        FrontEndControlCodeForPTZ code = new FrontEndControlCodeForPTZ();
        code.setTilt(1);
        code.setPan(1); // 右下
        when(httpClient.postWithToken(eq("/api/lapp/device/ptz/start"), paramsCaptor.capture(), any()))
                .thenReturn(okResponse());

        assertTrue(ptzService.ptz(channel, code));
        assertEquals("7", paramsCaptor.getValue().get("direction"));
    }

    @Test
    @DisplayName("ptz - 放大（zoom=1）→ direction=8")
    public void testPtz_zoomIn_direction8() {
        CommonChannel channel = buildChannel("D001", "1");
        FrontEndControlCodeForPTZ code = new FrontEndControlCodeForPTZ();
        code.setZoom(1); // 放大
        when(httpClient.postWithToken(eq("/api/lapp/device/ptz/start"), paramsCaptor.capture(), any()))
                .thenReturn(okResponse());

        assertTrue(ptzService.ptz(channel, code));
        assertEquals("8", paramsCaptor.getValue().get("direction"));
    }

    @Test
    @DisplayName("ptz - 缩小（zoom=0）→ direction=9")
    public void testPtz_zoomOut_direction9() {
        CommonChannel channel = buildChannel("D001", "1");
        FrontEndControlCodeForPTZ code = new FrontEndControlCodeForPTZ();
        code.setZoom(0); // 缩小
        when(httpClient.postWithToken(eq("/api/lapp/device/ptz/start"), paramsCaptor.capture(), any()))
                .thenReturn(okResponse());

        assertTrue(ptzService.ptz(channel, code));
        assertEquals("9", paramsCaptor.getValue().get("direction"));
    }

    @Test
    @DisplayName("ptz - pan/tilt/zoom 均为 null → 触发停止控制")
    public void testPtz_noDirection_stopsPtz() {
        CommonChannel channel = buildChannel("D001", "1");
        FrontEndControlCodeForPTZ code = new FrontEndControlCodeForPTZ();
        // 不设置任何方向 → resolveDirection 返回 null → ptzStop

        when(httpClient.postWithToken(eq("/api/lapp/device/ptz/stop"), paramsCaptor.capture(), any()))
                .thenReturn(okResponse());

        Boolean result = ptzService.ptz(channel, code);

        assertTrue(result);
        verify(httpClient).postWithToken(eq("/api/lapp/device/ptz/stop"), anyMap(), any());
        verify(httpClient, never()).postWithToken(eq("/api/lapp/device/ptz/start"), anyMap(), any());
    }

    @Test
    @DisplayName("ptz - 速度参数使用 panSpeed，范围限制在 1-7")
    public void testPtz_speedCappedAt7() {
        CommonChannel channel = buildChannel("D001", "1");
        FrontEndControlCodeForPTZ code = new FrontEndControlCodeForPTZ();
        code.setTilt(0);
        code.setPanSpeed(100); // 超出最大值
        when(httpClient.postWithToken(eq("/api/lapp/device/ptz/start"), paramsCaptor.capture(), any()))
                .thenReturn(okResponse());

        ptzService.ptz(channel, code);

        assertEquals("7", paramsCaptor.getValue().get("speed"));
    }

    @Test
    @DisplayName("ptz - 无速度参数时使用默认速度 3")
    public void testPtz_defaultSpeed3() {
        CommonChannel channel = buildChannel("D001", "1");
        FrontEndControlCodeForPTZ code = new FrontEndControlCodeForPTZ();
        code.setTilt(0); // 只设置方向，不设速度
        when(httpClient.postWithToken(eq("/api/lapp/device/ptz/start"), paramsCaptor.capture(), any()))
                .thenReturn(okResponse());

        ptzService.ptz(channel, code);

        assertEquals("3", paramsCaptor.getValue().get("speed"));
    }

    @Test
    @DisplayName("ptz - API 返回错误码，抛出 EzvizApiException")
    public void testPtz_apiError_throwsException() {
        CommonChannel channel = buildChannel("D001", "1");
        FrontEndControlCodeForPTZ code = new FrontEndControlCodeForPTZ();
        code.setTilt(0);
        when(httpClient.postWithToken(eq("/api/lapp/device/ptz/start"), anyMap(), any()))
                .thenThrow(new EzvizApiException("设备离线", "20014"));

        assertThrows(EzvizApiException.class, () -> ptzService.ptz(channel, code));
    }

    // ======================== preset 测试 ========================

    @Test
    @DisplayName("preset - code=1 设置预置点，调用 /api/lapp/preset/set")
    public void testPreset_set_callsSetApi() {
        CommonChannel channel = buildChannel("D001", "1");
        FrontEndControlCodeForPreset presetCode = new FrontEndControlCodeForPreset();
        presetCode.setCode(1);
        presetCode.setPresetId(5);
        presetCode.setPresetName("门口");

        ptzService.preset(channel, presetCode);

        verify(httpClient).postWithToken(eq("/api/lapp/preset/set"), anyMap(), any());
    }

    @Test
    @DisplayName("preset - code=2 调用预置点，调用 /api/lapp/preset/move")
    public void testPreset_move_callsMoveApi() {
        CommonChannel channel = buildChannel("D001", "1");
        FrontEndControlCodeForPreset presetCode = new FrontEndControlCodeForPreset();
        presetCode.setCode(2);
        presetCode.setPresetId(5);

        ptzService.preset(channel, presetCode);

        verify(httpClient).postWithToken(eq("/api/lapp/preset/move"), anyMap(), any());
    }

    @Test
    @DisplayName("preset - code=3 删除预置点，调用 /api/lapp/preset/clear")
    public void testPreset_clear_callsClearApi() {
        CommonChannel channel = buildChannel("D001", "1");
        FrontEndControlCodeForPreset presetCode = new FrontEndControlCodeForPreset();
        presetCode.setCode(3);
        presetCode.setPresetId(5);

        ptzService.preset(channel, presetCode);

        verify(httpClient).postWithToken(eq("/api/lapp/preset/clear"), anyMap(), any());
    }

    // ======================== queryPresetList 测试 ========================

    @Test
    @DisplayName("queryPresetList - 成功返回预置点列表")
    public void testQueryPresetList_success() throws Exception {
        CommonChannel channel = buildChannel("D001", "1");

        List<Map<String, Object>> presets = new ArrayList<>();
        Map<String, Object> p1 = new HashMap<>();
        p1.put("index", 1);
        p1.put("name", "预置点1");
        presets.add(p1);
        Map<String, Object> p2 = new HashMap<>();
        p2.put("index", 2);
        p2.put("name", "预置点2");
        presets.add(p2);

        Object resp = buildPresetListResponse("200", presets);
        when(httpClient.postWithToken(eq("/api/lapp/preset/list"), anyMap(), any()))
                .thenAnswer(inv -> resp);

        List<Preset> result = ptzService.queryPresetList(channel);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getPresetId());
        assertEquals("预置点1", result.get(0).getPresetName());
        assertEquals("2", result.get(1).getPresetId());
    }

    @Test
    @DisplayName("queryPresetList - 空数据时返回空列表")
    public void testQueryPresetList_emptyData_returnsEmptyList() throws Exception {
        CommonChannel channel = buildChannel("D001", "1");

        Object resp = buildPresetListResponseNullData("200");
        when(httpClient.postWithToken(eq("/api/lapp/preset/list"), anyMap(), any()))
                .thenAnswer(inv -> resp);

        List<Preset> result = ptzService.queryPresetList(channel);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ======================== 不支持的功能测试 ========================

    @Test
    @DisplayName("frontEndCommand - 不支持，返回 false")
    public void testFrontEndCommand_returnsFalse() {
        assertFalse(ptzService.frontEndCommand(buildChannel("D", "1"), 1, 0, 0, 0));
    }

    @Test
    @DisplayName("fi - 不支持独立聚焦，返回 false")
    public void testFi_returnsFalse() {
        assertFalse(ptzService.fi(buildChannel("D", "1"),
                new com.fastbee.media.model.FrontEndControlCodeForFI()));
    }

    @Test
    @DisplayName("tour - 不支持巡航，返回 false")
    public void testTour_returnsFalse() {
        assertFalse(ptzService.tour(buildChannel("D", "1"),
                new com.fastbee.media.model.FrontEndControlCodeForTour()));
    }

    @Test
    @DisplayName("scan - 不支持扫描，返回 false")
    public void testScan_returnsFalse() {
        assertFalse(ptzService.scan(buildChannel("D", "1"),
                new com.fastbee.media.model.FrontEndControlCodeForScan()));
    }

    @Test
    @DisplayName("auxiliary - 不支持辅助开关，返回 false")
    public void testAuxiliary_returnsFalse() {
        assertFalse(ptzService.auxiliary(buildChannel("D", "1"),
                new com.fastbee.media.model.FrontEndControlCodeForAuxiliary()));
    }

    @Test
    @DisplayName("wiper - 不支持雨刷，返回 false")
    public void testWiper_returnsFalse() {
        assertFalse(ptzService.wiper(buildChannel("D", "1"),
                new com.fastbee.media.model.FrontEndControlCodeForWiper()));
    }

    // ======================== 辅助方法 ========================

    private CommonChannel buildChannel(String deviceId, String channelId) {
        CommonChannel channel = new CommonChannel();
        channel.setDeviceId(deviceId);
        channel.setChannelId(channelId);
        return channel;
    }

    private EzvizBaseResponse okResponse() {
        EzvizBaseResponse resp = new EzvizBaseResponse();
        resp.setCode("200");
        return resp;
    }

    private Object buildPresetListResponse(String code, List<Map<String, Object>> data) throws Exception {
        Class<?> clazz = Class.forName(
                "com.fastbee.ezviz.service.impl.EzvizPtzServiceImpl$EzvizPresetListResponse");
        Object resp = clazz.getDeclaredConstructor().newInstance();
        clazz.getSuperclass().getMethod("setCode", String.class).invoke(resp, code);
        clazz.getMethod("setData", Object.class).invoke(resp, data);
        return resp;
    }

    private Object buildPresetListResponseNullData(String code) throws Exception {
        Class<?> clazz = Class.forName(
                "com.fastbee.ezviz.service.impl.EzvizPtzServiceImpl$EzvizPresetListResponse");
        Object resp = clazz.getDeclaredConstructor().newInstance();
        clazz.getSuperclass().getMethod("setCode", String.class).invoke(resp, code);
        return resp;
    }
}
