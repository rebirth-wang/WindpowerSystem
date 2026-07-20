package com.fastbee.onvif.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.model.MediaInfo;
import com.fastbee.media.model.RecordList;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.service.IMediaCacheService;
import com.fastbee.media.service.IMediaServerService;
import com.fastbee.media.service.IMediaService;
import com.fastbee.media.util.ZlmApiUtils;
import com.fastbee.onvif.domain.OnvifDeviceChannel;
import com.fastbee.onvif.service.impl.PlaybackServiceForOnvifImpl;
import com.fastbee.onvif.util.OnvifClient;

/**
 * {@link PlaybackServiceForOnvifImpl} 单元测试
 *
 * <p>覆盖场景：
 * <ul>
 *   <li>playback - 通道不存在时抛出 ServiceException</li>
 *   <li>playback - 无 ZLM 媒体服务器时抛出 ServiceException</li>
 *   <li>playback - 正常流程（GetReplayUri + ZLM 拉流）</li>
 *   <li>stopPlayback - 无活跃流时返回 true</li>
 *   <li>queryRecord - 正常返回录像摘要</li>
 *   <li>queryRecord - ONVIF 不支持时返回空记录</li>
 * </ul>
 *
 * @author fastbee
 */
@DisplayName("PlaybackServiceForOnvifImpl 单元测试")
class PlaybackServiceForOnvifImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private PlaybackServiceForOnvifImpl playbackService;

    @Mock
    private IOnvifDeviceChannelService onvifDeviceChannelService;

    @Mock
    private IMediaCacheService mediaCacheService;

    @Mock
    private IMediaServerService mediaServerService;

    @Mock
    private IMediaService mediaService;

    @Mock
    private ZlmApiUtils zlmApiUtils;

    @Mock
    private OnvifClient onvifClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(playbackService, "onvifDeviceChannelService", onvifDeviceChannelService);
        ReflectionTestUtils.setField(playbackService, "mediaCacheService", mediaCacheService);
        ReflectionTestUtils.setField(playbackService, "mediaServerService", mediaServerService);
        ReflectionTestUtils.setField(playbackService, "mediaService", mediaService);
        ReflectionTestUtils.setField(playbackService, "zlmApiUtils", zlmApiUtils);
        ReflectionTestUtils.setField(playbackService, "onvifClient", onvifClient);
    }

    // ======================== playback ========================

    @Test
    @DisplayName("playback - 通道不存在时抛出 ServiceException")
    void testPlayback_channelNotFound_shouldThrow() {
        CommonChannel channel = buildCommonChannel(999, "999");
        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(999)).thenReturn(null);

        assertThrows(ServiceException.class,
                () -> playbackService.playback(channel, "2024-01-01 00:00:00", "2024-01-01 01:00:00"));
    }

    @Test
    @DisplayName("playback - 无 ZLM 媒体服务器时抛出 ServiceException")
    void testPlayback_noMediaServer_shouldThrow() {
        CommonChannel channel = buildCommonChannel(1, "1");
        OnvifDeviceChannel onvifChannel = buildChannel(1);
        onvifChannel.setProfileToken("Profile_000");

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(1)).thenReturn(onvifChannel);
        when(mediaServerService.getMediaServerForMinimumLoad(null)).thenReturn(null);

        assertThrows(ServiceException.class,
                () -> playbackService.playback(channel, "2024-01-01 00:00:00", "2024-01-01 01:00:00"));
    }

    @Test
    @DisplayName("playback - 正常流程（GetReplayUri + ZLM 拉流）")
    void testPlayback_success() throws Exception {
        CommonChannel channel = buildCommonChannel(1, "1");
        OnvifDeviceChannel onvifChannel = buildChannel(1);
        onvifChannel.setProfileToken("Profile_000");
        MediaServer mediaServer = buildMediaServer();
        StreamInfo expectedStream = new StreamInfo();

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(1)).thenReturn(onvifChannel);
        when(mediaServerService.getMediaServerForMinimumLoad(null)).thenReturn(mediaServer);
        when(onvifClient.getReplayUri(any(), anyString(), anyString()))
                .thenReturn("rtsp://192.168.1.1:554/replay");
        when(zlmApiUtils.addStreamProxy(any(), anyString(), anyString(), anyString(),
                anyBoolean(), anyBoolean(), anyInt(), anyInt())).thenReturn(null);
        when(mediaService.getStreamInfoByAppAndStream(any(), anyString(), anyString(), any(), any()))
                .thenReturn(expectedStream);

        StreamInfo result = playbackService.playback(channel, "2024-01-01 00:00:00", "2024-01-01 01:00:00");

        assertNotNull(result);
        verify(onvifClient).getReplayUri(any(), anyString(), anyString());
        verify(zlmApiUtils).addStreamProxy(eq(mediaServer), anyString(), anyString(),
                anyString(), anyBoolean(), anyBoolean(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("playback - GetReplayUri 失败时使用回退地址")
    void testPlayback_replayUriFailsFallback() throws Exception {
        CommonChannel channel = buildCommonChannel(1, "1");
        OnvifDeviceChannel onvifChannel = buildChannel(1);
        onvifChannel.setProfileToken("Profile_000");
        onvifChannel.setLiveStreamTcp("rtsp://192.168.1.1:554/live");
        MediaServer mediaServer = buildMediaServer();
        StreamInfo expectedStream = new StreamInfo();

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(1)).thenReturn(onvifChannel);
        when(mediaServerService.getMediaServerForMinimumLoad(null)).thenReturn(mediaServer);
        when(onvifClient.getReplayUri(any(), anyString(), anyString()))
                .thenThrow(new RuntimeException("设备不支持 Replay Service"));
        when(zlmApiUtils.addStreamProxy(any(), anyString(), anyString(), anyString(),
                anyBoolean(), anyBoolean(), anyInt(), anyInt())).thenReturn(null);
        when(mediaService.getStreamInfoByAppAndStream(any(), anyString(), anyString(), any(), any()))
                .thenReturn(expectedStream);

        // 应使用回退地址而不抛出异常
        StreamInfo result = playbackService.playback(channel, "2024-01-01 00:00:00", "2024-01-01 01:00:00");

        assertNotNull(result);
        verify(zlmApiUtils).addStreamProxy(any(), anyString(), anyString(),
                contains("192.168.1.1"), anyBoolean(), anyBoolean(), anyInt(), anyInt());
    }

    // ======================== stopPlayback ========================

    @Test
    @DisplayName("stopPlayback - 无活跃流时返回 true")
    void testStopPlayback_noActiveStream_shouldReturnTrue() {
        CommonChannel channel = buildCommonChannel(1, "1");
        when(mediaCacheService.getStreamInfo(anyString(), anyString(), anyString())).thenReturn(null);

        Boolean result = playbackService.stopPlayback(channel, "1_20240101");

        assertTrue(result);
        verify(zlmApiUtils, never()).closeStreams(any(), anyString(), anyString());
    }

    @Test
    @DisplayName("stopPlayback - 有活跃流时关闭并清理")
    void testStopPlayback_withActiveStream_shouldClose() {
        CommonChannel channel = buildCommonChannel(1, "1");
        String streamId = "1_20240101";
        MediaServer mediaServer = buildMediaServer();
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.setServerId("test-server");

        when(mediaCacheService.getStreamInfo(anyString(), eq(streamId), anyString())).thenReturn(mediaInfo);
        when(mediaServerService.selectMediaServerByServerId("test-server")).thenReturn(mediaServer);

        Boolean result = playbackService.stopPlayback(channel, streamId);

        assertTrue(result);
        verify(zlmApiUtils).closeStreams(mediaServer, "onvif_playback", streamId);
    }

    // ======================== queryRecord ========================

    @Test
    @DisplayName("queryRecord - 正常返回录像摘要")
    void testQueryRecord_success() throws Exception {
        CommonChannel channel = buildCommonChannel(1, "1");
        OnvifDeviceChannel onvifChannel = buildChannel(1);

        Map<String, String> summary = new LinkedHashMap<>();
        summary.put("dataFrom", "2024-01-01T00:00:00Z");
        summary.put("dataUntil", "2024-01-01T23:59:59Z");
        summary.put("numberRecordings", "5");

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(1)).thenReturn(onvifChannel);
        when(onvifClient.getRecordingSummary(onvifChannel)).thenReturn(summary);

        RecordList result = playbackService.queryRecord(channel, "2024-01-01 00:00:00", "2024-01-01 23:59:59");

        assertNotNull(result);
        assertEquals(1, result.getSumNum());
        verify(onvifClient).getRecordingSummary(onvifChannel);
    }

    @Test
    @DisplayName("queryRecord - 设备不支持 Recording Service 时返回空记录")
    void testQueryRecord_deviceNotSupported_shouldReturnEmpty() throws Exception {
        CommonChannel channel = buildCommonChannel(1, "1");
        OnvifDeviceChannel onvifChannel = buildChannel(1);

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(1)).thenReturn(onvifChannel);
        when(onvifClient.getRecordingSummary(onvifChannel))
                .thenThrow(new RuntimeException("Device does not support Recording Service"));

        RecordList result = playbackService.queryRecord(channel, "2024-01-01 00:00:00", "2024-01-01 23:59:59");

        assertNotNull(result);
        assertEquals(0, result.getSumNum());
    }

    // ======================== 工具方法 ========================

    private CommonChannel buildCommonChannel(long id, String channelId) {
        CommonChannel channel = new CommonChannel();
        channel.setId(id);
        channel.setChannelId(channelId);
        return channel;
    }

    private OnvifDeviceChannel buildChannel(Integer id) {
        OnvifDeviceChannel channel = new OnvifDeviceChannel();
        channel.setId(id);
        channel.setIp("192.168.1.1");
        channel.setPort(80);
        channel.setUsername("admin");
        channel.setPassword("admin123");
        channel.setDeviceId(10);
        return channel;
    }

    private MediaServer buildMediaServer() {
        MediaServer mediaServer = new MediaServer();
        mediaServer.setIp("127.0.0.1");
        mediaServer.setPortHttp(8080L);
        mediaServer.setPortRtsp(8554L);
        mediaServer.setPortRtmp(1935L);
        mediaServer.setSecret("fastbee_secret");
        mediaServer.setServerId("test-server");
        return mediaServer;
    }
}
