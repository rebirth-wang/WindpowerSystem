package com.fastbee.onvif.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.service.IMediaCacheService;
import com.fastbee.media.service.IMediaServerService;
import com.fastbee.media.service.IMediaService;
import com.fastbee.media.util.ZlmApiUtils;
import com.fastbee.onvif.domain.OnvifDeviceChannel;
import com.fastbee.onvif.service.impl.PlayServiceForOnvifImpl;
import com.fastbee.onvif.util.OnvifClient;

/**
 * {@link PlayServiceForOnvifImpl} 单元测试
 *
 * <p>覆盖场景：
 * <ul>
 *   <li>通道不存在时 play 抛出 ServiceException</li>
 *   <li>无 ZLM 媒体服务器时 play 抛出 ServiceException</li>
 *   <li>正常 play 流程（GetStreamUri + ZLM 拉流）</li>
 *   <li>stopPlay 无活跃流时返回 true</li>
 * </ul>
 *
 * @author fastbee
 */
@DisplayName("PlayServiceForOnvifImpl 单元测试")
class PlayServiceForOnvifImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private PlayServiceForOnvifImpl playService;

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
        ReflectionTestUtils.setField(playService, "onvifDeviceChannelService", onvifDeviceChannelService);
        ReflectionTestUtils.setField(playService, "mediaCacheService", mediaCacheService);
        ReflectionTestUtils.setField(playService, "mediaServerService", mediaServerService);
        ReflectionTestUtils.setField(playService, "mediaService", mediaService);
        ReflectionTestUtils.setField(playService, "zlmApiUtils", zlmApiUtils);
        ReflectionTestUtils.setField(playService, "onvifClient", onvifClient);
    }

    // ======================== play ========================

    @Test
    @DisplayName("play - 通道不存在时抛出 ServiceException")
    void testPlay_channelNotFound_shouldThrow() {
        CommonChannel channel = buildCommonChannel(999, "999");
        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(999)).thenReturn(null);

        assertThrows(ServiceException.class, () -> playService.play(channel, null, false));
    }

    @Test
    @DisplayName("play - 无 ZLM 媒体服务器时抛出 ServiceException")
    void testPlay_noMediaServer_shouldThrow() {
        CommonChannel channel = buildCommonChannel(1, "1");
        OnvifDeviceChannel onvifChannel = buildChannel(1);
        onvifChannel.setProfileToken("Profile_000");

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(1)).thenReturn(onvifChannel);
        when(mediaCacheService.getStreamInfo(anyString(), anyString(), anyString())).thenReturn(null);
        when(mediaServerService.getMediaServerForMinimumLoad(null)).thenReturn(null);

        assertThrows(ServiceException.class, () -> playService.play(channel, null, false));
    }

    @Test
    @DisplayName("play - 正常流程：GetStreamUri + ZLM 拉流")
    void testPlay_success() throws Exception {
        CommonChannel channel = buildCommonChannel(1, "1");
        OnvifDeviceChannel onvifChannel = buildChannel(1);
        onvifChannel.setProfileToken("Profile_000");
        MediaServer mediaServer = buildMediaServer();
        StreamInfo streamInfo = new StreamInfo();

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(1)).thenReturn(onvifChannel);
        when(mediaCacheService.getStreamInfo(anyString(), anyString(), anyString())).thenReturn(null);
        when(mediaServerService.getMediaServerForMinimumLoad(null)).thenReturn(mediaServer);
        when(onvifClient.getStreamUri(onvifChannel, "RtspUnicast"))
                .thenReturn("rtsp://192.168.1.1:554/stream");
        when(onvifDeviceChannelService.updateWithCache(any())).thenReturn(true);
        when(zlmApiUtils.addStreamProxy(any(), anyString(), anyString(), anyString(),
                anyBoolean(), anyBoolean(), anyInt(), anyInt())).thenReturn(null);
        when(mediaService.getStreamInfoByAppAndStream(any(), anyString(), anyString(), any(), any()))
                .thenReturn(streamInfo);

        StreamInfo result = playService.play(channel, null, false);

        assertNotNull(result);
        verify(onvifClient).getStreamUri(onvifChannel, "RtspUnicast");
        verify(zlmApiUtils).addStreamProxy(eq(mediaServer), anyString(), anyString(),
                contains("192.168.1.1"), anyBoolean(), anyBoolean(), anyInt(), anyInt());
    }

    // ======================== stopPlay ========================

    @Test
    @DisplayName("stopPlay - 无活跃流时返回 true")
    void testStopPlay_noActiveStream_shouldReturnTrue() {
        CommonChannel channel = buildCommonChannel(1, "1");
        OnvifDeviceChannel onvifChannel = buildChannel(1);
        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(1)).thenReturn(onvifChannel);
        when(mediaCacheService.getStreamInfo(anyString(), anyString(), anyString())).thenReturn(null);

        Boolean result = playService.stopPlay(channel);

        assertTrue(result);
        verify(zlmApiUtils, never()).closeStreams(any(), anyString(), anyString());
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
        return mediaServer;
    }
}
