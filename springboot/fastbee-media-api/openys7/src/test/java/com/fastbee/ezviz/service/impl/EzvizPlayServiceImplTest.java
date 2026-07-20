package com.fastbee.ezviz.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import com.fastbee.ezviz.client.EzvizHttpClient;
import com.fastbee.ezviz.client.EzvizTokenHolder;
import com.fastbee.ezviz.exception.EzvizApiException;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.model.Platform;
import com.fastbee.media.model.StreamInfo;

/**
 * EzvizPlayServiceImpl 单元测试
 * 测试实时预览流获取、通道号解析、stopPlay、closeStream 等功能
 *
 * @author fastbee
 */
@DisplayName("萤石云实时预览服务单元测试")
public class EzvizPlayServiceImplTest extends BaseMockitoUnitTest {

    @Mock
    private EzvizHttpClient httpClient;

    @Mock
    private EzvizTokenHolder tokenHolder;

    private EzvizPlayServiceImpl playService;

    @Captor
    private ArgumentCaptor<Map<String, String>> paramsCaptor;

    @BeforeEach
    public void setUp() {
        playService = new EzvizPlayServiceImpl(httpClient, tokenHolder);
    }

    // ======================== play 测试 ========================

    @Test
    @DisplayName("play - 成功获取 HLS 直播流")
    public void testPlay_success() throws Exception {
        CommonChannel channel = buildChannel("D12345678", "D12345678_1");
        Platform platform = new Platform();

        // Mock 返回直播流地址
        Object liveResp = buildLiveAddressResponse("200", "https://hls.test.com/live.m3u8");
        when(httpClient.postWithToken(eq("/api/lapp/live/address/get"), anyMap(), any()))
                .thenAnswer(inv -> liveResp);

        StreamInfo result = playService.play(channel, platform, false);

        assertNotNull(result);
        assertEquals("https://hls.test.com/live.m3u8", result.getHls());
        assertEquals("https://hls.test.com/live.m3u8", result.getPlayurl());
        assertEquals("D12345678", result.getDeviceId());
        assertEquals("D12345678_1", result.getChannelId());
    }

    @Test
    @DisplayName("play - channelId 为纯数字格式时，正确解析通道号")
    public void testPlay_channelIdPureNumber_parsedCorrectly() throws Exception {
        CommonChannel channel = buildChannel("D12345678", "2");

        Object liveResp = buildLiveAddressResponse("200", "https://hls.test.com/ch2.m3u8");
        when(httpClient.postWithToken(eq("/api/lapp/live/address/get"), paramsCaptor.capture(), any()))
                .thenAnswer(inv -> liveResp);

        playService.play(channel, new Platform(), false);

        Map<String, String> params = paramsCaptor.getValue();
        assertEquals("2", params.get("channelNo"));
    }

    @Test
    @DisplayName("play - channelId 为 deviceSerial_no 格式时，正确解析通道号")
    public void testPlay_channelIdWithPrefix_parsedCorrectly() throws Exception {
        CommonChannel channel = buildChannel("D12345678", "D12345678_3");

        Object liveResp = buildLiveAddressResponse("200", "https://hls.test.com/ch3.m3u8");
        when(httpClient.postWithToken(eq("/api/lapp/live/address/get"), paramsCaptor.capture(), any()))
                .thenAnswer(inv -> liveResp);

        playService.play(channel, new Platform(), false);

        Map<String, String> params = paramsCaptor.getValue();
        assertEquals("3", params.get("channelNo"));
    }

    @Test
    @DisplayName("play - channelId 为 null 时，默认使用通道号 1")
    public void testPlay_nullChannelId_usesChannelNo1() throws Exception {
        CommonChannel channel = buildChannel("D12345678", null);

        Object liveResp = buildLiveAddressResponse("200", "https://hls.test.com/default.m3u8");
        when(httpClient.postWithToken(eq("/api/lapp/live/address/get"), paramsCaptor.capture(), any()))
                .thenAnswer(inv -> liveResp);

        playService.play(channel, new Platform(), false);

        Map<String, String> params = paramsCaptor.getValue();
        assertEquals("1", params.get("channelNo"));
    }

    @Test
    @DisplayName("play - API 返回错误码，抛出 EzvizApiException")
    public void testPlay_apiError_throwsException() {
        CommonChannel channel = buildChannel("D12345678", "D12345678_1");
        when(httpClient.postWithToken(eq("/api/lapp/live/address/get"), anyMap(), any()))
                .thenThrow(new EzvizApiException("设备离线", "20014"));

        EzvizApiException ex = assertThrows(EzvizApiException.class,
                () -> playService.play(channel, new Platform(), false));
        assertEquals("20014", ex.getErrorCode());
    }

    @Test
    @DisplayName("play - 携带 protocol=2 (HLS) 参数")
    public void testPlay_usesHlsProtocol() throws Exception {
        CommonChannel channel = buildChannel("D12345678", "D12345678_1");

        Object liveResp = buildLiveAddressResponse("200", "https://hls.test.com/live.m3u8");
        when(httpClient.postWithToken(eq("/api/lapp/live/address/get"), paramsCaptor.capture(), any()))
                .thenAnswer(inv -> liveResp);

        playService.play(channel, new Platform(), false);

        Map<String, String> params = paramsCaptor.getValue();
        assertEquals("2", params.get("protocol")); // 2 = HLS
        assertEquals("1", params.get("code"));     // 1 = 高清
    }

    // ======================== stopPlay 测试 ========================

    @Test
    @DisplayName("stopPlay - 萤石云无需停止，直接返回 true")
    public void testStopPlay_returnsTrue() {
        CommonChannel channel = buildChannel("D12345678", "D12345678_1");

        Boolean result = playService.stopPlay(channel);

        assertTrue(result);
        // 萤石云不需要发起停止请求
        verifyNoInteractions(httpClient);
    }

    // ======================== closeStream 测试 ========================

    @Test
    @DisplayName("closeStream - 萤石云无需主动关流，直接返回 true")
    public void testCloseStream_returnsTrue() {
        CommonChannel channel = buildChannel("D12345678", "D12345678_1");

        Boolean result = playService.closeStream(channel, "stream-id", false);

        assertTrue(result);
        verifyNoInteractions(httpClient);
    }

    // ======================== getLiveUrl 测试 ========================

    @Test
    @DisplayName("getLiveUrl - 成功返回带过期时间的地址")
    public void testGetLiveUrl_success() throws Exception {
        long expireTs = System.currentTimeMillis() + 1800 * 1000L;
        Object liveResp = buildLiveAddressResponseWithExpire("200", "https://hls.test.com/expire.m3u8", expireTs);
        when(httpClient.postWithToken(eq("/api/lapp/live/address/get"), anyMap(), any()))
                .thenAnswer(inv -> liveResp);

        com.fastbee.ezviz.model.EzvizLiveUrl url = playService.getLiveUrl("D12345678", 1, 2);

        assertNotNull(url);
        assertEquals("https://hls.test.com/expire.m3u8", url.getHls());
        assertEquals(expireTs, url.getExpireTime());
    }

    // ======================== 辅助方法 ========================

    private CommonChannel buildChannel(String deviceId, String channelId) {
        CommonChannel channel = new CommonChannel();
        channel.setDeviceId(deviceId);
        channel.setChannelId(channelId);
        return channel;
    }

    /**
     * 构建直播地址响应（内部类，通过反射构建）
     */
    private Object buildLiveAddressResponse(String code, String url) throws Exception {
        Class<?> clazz = Class.forName(
                "com.fastbee.ezviz.service.impl.EzvizPlayServiceImpl$EzvizLiveAddressResponse");
        Object resp = clazz.getDeclaredConstructor().newInstance();
        clazz.getSuperclass().getMethod("setCode", String.class).invoke(resp, code);
        if (url != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("url", url);
            clazz.getMethod("setData", Object.class).invoke(resp, data);
        }
        return resp;
    }

    /**
     * 构建带过期时间的直播地址响应
     */
    private Object buildLiveAddressResponseWithExpire(String code, String url, long expireTime) throws Exception {
        Class<?> clazz = Class.forName(
                "com.fastbee.ezviz.service.impl.EzvizPlayServiceImpl$EzvizLiveAddressResponse");
        Object resp = clazz.getDeclaredConstructor().newInstance();
        clazz.getSuperclass().getMethod("setCode", String.class).invoke(resp, code);
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        data.put("expireTime", expireTime);
        clazz.getMethod("setData", Object.class).invoke(resp, data);
        return resp;
    }
}
