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
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.model.RecordList;
import com.fastbee.media.model.StreamInfo;

/**
 * EzvizPlaybackServiceImpl 单元测试
 * 测试录像回放、录像查询、暂停/恢复/拖动/倍速等功能
 *
 * @author fastbee
 */
@DisplayName("萤石云录像回放服务单元测试")
public class EzvizPlaybackServiceImplTest extends BaseMockitoUnitTest {

    @Mock
    private EzvizHttpClient httpClient;

    private EzvizPlaybackServiceImpl playbackService;

    @Captor
    private ArgumentCaptor<Map<String, String>> paramsCaptor;

    @BeforeEach
    public void setUp() {
        playbackService = new EzvizPlaybackServiceImpl(httpClient);
    }

    // ======================== playback 测试 ========================

    @Test
    @DisplayName("playback - 成功获取 HLS 回放流")
    public void testPlayback_success() throws Exception {
        CommonChannel channel = buildChannel("D12345678", "D12345678_1");

        Object resp = buildLiveAddressResponse("200", "https://hls.test.com/playback.m3u8");
        when(httpClient.postWithToken(eq("/api/lapp/video/by/time"), anyMap(), any()))
                .thenAnswer(inv -> resp);

        StreamInfo result = playbackService.playback(channel, "2024-01-01 08:00:00", "2024-01-01 09:00:00");

        assertNotNull(result);
        assertEquals("https://hls.test.com/playback.m3u8", result.getHls());
        assertEquals("D12345678", result.getDeviceId());
    }

    @Test
    @DisplayName("playback - 时间戳格式自动转换为 yyyy-MM-dd HH:mm:ss")
    public void testPlayback_timestampFormat_converted() throws Exception {
        CommonChannel channel = buildChannel("D12345678", "1");

        Object resp = buildLiveAddressResponse("200", "https://hls.test.com/ts.m3u8");
        when(httpClient.postWithToken(eq("/api/lapp/video/by/time"), paramsCaptor.capture(), any()))
                .thenAnswer(inv -> resp);

        // 传入毫秒级时间戳
        playbackService.playback(channel, "1704067200000", "1704070800000");

        Map<String, String> params = paramsCaptor.getValue();
        // 验证时间参数已转换为字符串格式（非时间戳）
        assertFalse(params.get("startTime").matches("\\d{13}"), "startTime 不应为毫秒时间戳格式");
        assertFalse(params.get("stopTime").matches("\\d{13}"), "stopTime 不应为毫秒时间戳格式");
    }

    @Test
    @DisplayName("playback - 使用 HLS 协议（protocol=2）")
    public void testPlayback_usesHlsProtocol() throws Exception {
        CommonChannel channel = buildChannel("D12345678", "D12345678_1");

        Object resp = buildLiveAddressResponse("200", "https://hls.test.com/playback.m3u8");
        when(httpClient.postWithToken(eq("/api/lapp/video/by/time"), paramsCaptor.capture(), any()))
                .thenAnswer(inv -> resp);

        playbackService.playback(channel, "2024-01-01 08:00:00", "2024-01-01 09:00:00");

        Map<String, String> params = paramsCaptor.getValue();
        assertEquals("2", params.get("protocol"));
    }

    @Test
    @DisplayName("playback - API 异常，抛出 EzvizApiException")
    public void testPlayback_apiError_throwsException() {
        CommonChannel channel = buildChannel("D12345678", "D12345678_1");
        when(httpClient.postWithToken(eq("/api/lapp/video/by/time"), anyMap(), any()))
                .thenThrow(new EzvizApiException("云存储不存在", "20302"));

        EzvizApiException ex = assertThrows(EzvizApiException.class,
                () -> playbackService.playback(channel, "2024-01-01 08:00:00", "2024-01-01 09:00:00"));
        assertEquals("20302", ex.getErrorCode());
    }

    // ======================== stopPlayback / pause / resume / seek / speed 测试 ========================

    @Test
    @DisplayName("stopPlayback - 萤石云 HLS 无需停止，返回 true")
    public void testStopPlayback_returnsTrue() {
        CommonChannel channel = buildChannel("D12345678", "D12345678_1");
        assertTrue(playbackService.stopPlayback(channel, "stream"));
        verifyNoInteractions(httpClient);
    }

    @Test
    @DisplayName("playbackPause - 不支持服务端暂停，返回 true")
    public void testPlaybackPause_returnsTrue() {
        assertTrue(playbackService.playbackPause(buildChannel("D", "1"), "stream"));
        verifyNoInteractions(httpClient);
    }

    @Test
    @DisplayName("playbackResume - 不支持服务端恢复，返回 true")
    public void testPlaybackResume_returnsTrue() {
        assertTrue(playbackService.playbackResume(buildChannel("D", "1"), "stream"));
        verifyNoInteractions(httpClient);
    }

    @Test
    @DisplayName("playbackSeek - 跳转通过重新请求实现，返回 true")
    public void testPlaybackSeek_returnsTrue() {
        assertTrue(playbackService.playbackSeek(buildChannel("D", "1"), "stream", 60L));
        verifyNoInteractions(httpClient);
    }

    @Test
    @DisplayName("playbackSpeed - 不支持服务端倍速，返回 false")
    public void testPlaybackSpeed_returnsFalse() {
        assertFalse(playbackService.playbackSpeed(buildChannel("D", "1"), "stream", 2.0));
        verifyNoInteractions(httpClient);
    }

    // ======================== queryRecord 测试 ========================

    @Test
    @DisplayName("queryRecord - 成功查询录像列表")
    public void testQueryRecord_success() throws Exception {
        CommonChannel channel = buildChannel("D12345678", "D12345678_1");

        // 构建录像列表数据
        List<Map<String, String>> records = new ArrayList<>();
        Map<String, String> r1 = new HashMap<>();
        r1.put("startTime", "2024-01-01 08:00:00");
        r1.put("stopTime", "2024-01-01 08:30:00");
        records.add(r1);
        Map<String, String> r2 = new HashMap<>();
        r2.put("startTime", "2024-01-01 09:00:00");
        r2.put("stopTime", "2024-01-01 09:30:00");
        records.add(r2);

        Object resp = buildRecordListResponse("200", records);
        when(httpClient.postWithToken(eq("/api/lapp/record/query"), anyMap(), any()))
                .thenAnswer(inv -> resp);

        RecordList result = playbackService.queryRecord(channel, "2024-01-01 08:00:00", "2024-01-01 10:00:00");

        assertNotNull(result);
        assertEquals(2, result.getSumNum());
        assertNotNull(result.getRecordItems());
        assertEquals(2, result.getRecordItems().size());
        // 验证时间戳转换（单位：秒）
        assertTrue(result.getRecordItems().get(0).getStart() > 0);
    }

    @Test
    @DisplayName("queryRecord - 录像列表为空，返回空 RecordList")
    public void testQueryRecord_emptyList() throws Exception {
        CommonChannel channel = buildChannel("D12345678", "D12345678_1");

        Object resp = buildRecordListResponseNullData("200");
        when(httpClient.postWithToken(eq("/api/lapp/record/query"), anyMap(), any()))
                .thenAnswer(inv -> resp);

        RecordList result = playbackService.queryRecord(channel, "2024-01-01 08:00:00", "2024-01-01 09:00:00");

        assertNotNull(result);
        assertEquals(0, result.getSumNum());
    }

    @Test
    @DisplayName("queryRecord - 请求参数包含 recType=0（全部类型）")
    public void testQueryRecord_recTypeAll() throws Exception {
        CommonChannel channel = buildChannel("D12345678", "1");

        Object resp = buildRecordListResponseNullData("200");
        when(httpClient.postWithToken(eq("/api/lapp/record/query"), paramsCaptor.capture(), any()))
                .thenAnswer(inv -> resp);

        playbackService.queryRecord(channel, "2024-01-01 08:00:00", "2024-01-01 09:00:00");

        Map<String, String> params = paramsCaptor.getValue();
        assertEquals("0", params.get("recType"));
    }

    // ======================== download 测试 ========================

    @Test
    @DisplayName("download - 不支持直接下载，降级为返回回放地址")
    public void testDownload_fallbackToPlayback() throws Exception {
        CommonChannel channel = buildChannel("D12345678", "D12345678_1");

        Object resp = buildLiveAddressResponse("200", "https://hls.test.com/download.m3u8");
        when(httpClient.postWithToken(eq("/api/lapp/video/by/time"), anyMap(), any()))
                .thenAnswer(inv -> resp);

        StreamInfo result = playbackService.download(channel, "2024-01-01 08:00:00", "2024-01-01 09:00:00", 1);

        assertNotNull(result);
        assertEquals("https://hls.test.com/download.m3u8", result.getHls());
    }

    // ======================== 辅助方法 ========================

    private CommonChannel buildChannel(String deviceId, String channelId) {
        CommonChannel channel = new CommonChannel();
        channel.setDeviceId(deviceId);
        channel.setChannelId(channelId);
        return channel;
    }

    private Object buildLiveAddressResponse(String code, String url) throws Exception {
        Class<?> clazz = Class.forName(
                "com.fastbee.ezviz.service.impl.EzvizPlaybackServiceImpl$EzvizLiveAddressResponse");
        Object resp = clazz.getDeclaredConstructor().newInstance();
        clazz.getSuperclass().getMethod("setCode", String.class).invoke(resp, code);
        if (url != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("url", url);
            clazz.getMethod("setData", Object.class).invoke(resp, data);
        }
        return resp;
    }

    private Object buildRecordListResponse(String code, List<Map<String, String>> records) throws Exception {
        Class<?> clazz = Class.forName(
                "com.fastbee.ezviz.service.impl.EzvizPlaybackServiceImpl$EzvizRecordListResponse");
        Object resp = clazz.getDeclaredConstructor().newInstance();
        clazz.getSuperclass().getMethod("setCode", String.class).invoke(resp, code);
        clazz.getMethod("setData", Object.class).invoke(resp, records);
        return resp;
    }

    private Object buildRecordListResponseNullData(String code) throws Exception {
        Class<?> clazz = Class.forName(
                "com.fastbee.ezviz.service.impl.EzvizPlaybackServiceImpl$EzvizRecordListResponse");
        Object resp = clazz.getDeclaredConstructor().newInstance();
        clazz.getSuperclass().getMethod("setCode", String.class).invoke(resp, code);
        return resp;
    }
}
