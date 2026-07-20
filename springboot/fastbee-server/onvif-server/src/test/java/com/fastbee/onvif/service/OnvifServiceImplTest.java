package com.fastbee.onvif.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.media.service.IMediaServerService;
import com.fastbee.onvif.WebsocketSessionManger;
import com.fastbee.onvif.domain.OnvifDeviceChannel;
import com.fastbee.onvif.service.impl.OnvifServiceImpl;
import com.fastbee.onvif.util.OnvifClient;

/**
 * {@link OnvifServiceImpl} 单元测试
 *
 * <p>覆盖场景：
 * <ul>
 *   <li>设备未连接时 discovery 抛出 ServiceException</li>
 *   <li>设备连接时 discovery 正常发送消息</li>
 *   <li>getDeviceInfo 正常获取设备信息</li>
 *   <li>通道不存在时 getSnapshotUri 抛出异常</li>
 *   <li>ptzStart 设备未连接时抛出异常</li>
 * </ul>
 *
 * @author fastbee
 */
@DisplayName("OnvifServiceImpl 单元测试")
class OnvifServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private OnvifServiceImpl onvifService;

    @Mock
    private WebsocketSessionManger websocketSessionManger;

    @Mock
    private IOnvifDeviceChannelService onvifDeviceChannelService;

    @Mock
    private IMediaServerService mediaServerService;

    @Mock
    private OnvifClient onvifClient;

    @Mock
    private Session mockSession;

    @Mock
    private RemoteEndpoint.Async mockAsyncRemote;

    @BeforeEach
    void setUp() {
        // 注入 websocketSessionManger 到 OnvifServiceImpl
        ReflectionTestUtils.setField(onvifService, "websocketSessionManger", websocketSessionManger);
        ReflectionTestUtils.setField(onvifService, "onvifDeviceChannelService", onvifDeviceChannelService);
        ReflectionTestUtils.setField(onvifService, "mediaServerService", mediaServerService);
        ReflectionTestUtils.setField(onvifService, "onvifClient", onvifClient);
    }

    // ======================== discovery ========================

    @Test
    @DisplayName("discovery - 设备未连接时抛出 ServiceException")
    void testDiscovery_deviceNotConnected_shouldThrow() {
        Integer deviceId = 1;
        when(websocketSessionManger.getSession(deviceId)).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> onvifService.discovery(deviceId, false));
        assertTrue(ex.getMessage().contains("设备尚未连接") || ex.getCode() != 0);
    }

    @Test
    @DisplayName("discovery - WebSocket 已连接时正常发送 DISCOVERY 消息")
    void testDiscovery_sessionOpen_shouldSendMessage() {
        Integer deviceId = 1;
        when(websocketSessionManger.getSession(deviceId)).thenReturn(mockSession);
        when(mockSession.isOpen()).thenReturn(true);
        when(mockSession.getAsyncRemote()).thenReturn(mockAsyncRemote);

        assertDoesNotThrow(() -> onvifService.discovery(deviceId, false));
        verify(mockAsyncRemote).sendText(anyString());
    }

    // ======================== getDeviceInfo ========================

    @Test
    @DisplayName("getDeviceInfo - 通道不存在时抛出 ServiceException")
    void testGetDeviceInfo_channelNotFound_shouldThrow() {
        Integer channelId = 999;
        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(channelId)).thenReturn(null);

        assertThrows(ServiceException.class, () -> onvifService.getDeviceInfo(channelId));
    }

    @Test
    @DisplayName("getDeviceInfo - 正常查询返回设备信息 Map")
    void testGetDeviceInfo_success() throws Exception {
        Integer channelId = 1;
        OnvifDeviceChannel channel = buildChannel(channelId);

        Map<String, String> deviceInfo = new LinkedHashMap<>();
        deviceInfo.put("manufacturer", "Test");
        deviceInfo.put("model", "Cam-001");
        deviceInfo.put("firmwareVersion", "1.0.0");

        Map<String, String> caps = new LinkedHashMap<>();
        caps.put("mediaServiceUrl", "http://192.168.1.1/onvif/media");
        caps.put("ptzServiceUrl", "http://192.168.1.1/onvif/ptz");

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(channelId)).thenReturn(channel);
        when(onvifClient.getDeviceInformation(channel)).thenReturn(deviceInfo);
        when(onvifClient.getCapabilities(channel)).thenReturn(caps);
        when(onvifClient.getProfiles(channel)).thenReturn(Arrays.asList("Profile_000"));
        when(onvifDeviceChannelService.updateWithCache(any())).thenReturn(true);

        Map<String, Object> result = onvifService.getDeviceInfo(channelId);

        assertNotNull(result);
        assertEquals("Test", result.get("manufacturer"));
        verify(onvifClient).getDeviceInformation(channel);
        verify(onvifClient).getCapabilities(channel);
    }

    // ======================== getSnapshotUri ========================

    @Test
    @DisplayName("getSnapshotUri - 通道不存在时抛出 ServiceException")
    void testGetSnapshotUri_channelNotFound_shouldThrow() {
        Integer channelId = 999;
        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(channelId)).thenReturn(null);

        assertThrows(ServiceException.class, () -> onvifService.getSnapshotUri(channelId));
    }

    @Test
    @DisplayName("getSnapshotUri - 已有 profileToken 时直接调用 GetSnapshotUri")
    void testGetSnapshotUri_withProfileToken_success() throws Exception {
        Integer channelId = 1;
        OnvifDeviceChannel channel = buildChannel(channelId);
        channel.setProfileToken("Profile_000");

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(channelId)).thenReturn(channel);
        when(onvifClient.getSnapshotUri(channel)).thenReturn("http://192.168.1.1/snapshot.jpg");

        String uri = onvifService.getSnapshotUri(channelId);

        assertNotNull(uri);
        assertTrue(uri.contains("snapshot") || uri.contains("192.168.1.1"));
        verify(onvifClient).getSnapshotUri(channel);
    }

    // ======================== ptzStart ========================

    @Test
    @DisplayName("ptzStart - 设备未连接时抛出 ServiceException")
    void testPtzStart_sessionNotConnected_shouldThrow() {
        Integer channelId = 1;
        OnvifDeviceChannel channel = buildChannel(channelId);
        channel.setDeviceId(10);

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(channelId)).thenReturn(channel);
        when(websocketSessionManger.getSession(10)).thenReturn(null);

        assertThrows(ServiceException.class,
                () -> onvifService.ptzStart(channelId, 1, 0, 0));
    }

    // ======================== play ========================

    @Test
    @DisplayName("play - 设备未连接时抛出 ServiceException")
    void testPlay_deviceNotConnected_shouldThrow() {
        Integer channelId = 1;
        OnvifDeviceChannel channel = buildChannel(channelId);
        channel.setDeviceId(10);
        channel.setProfileToken("Profile_000");

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(channelId)).thenReturn(channel);
        when(mediaServerService.getMediaServerForMinimumLoad(null)).thenReturn(null);

        // play 需要 ZLM，若无媒体服务器则应抛出异常
        assertThrows(ServiceException.class, () -> onvifService.play(channelId));
    }

    // ======================== initDeviceStatus ========================

    @Test
    @DisplayName("initDeviceStatus - 正常执行不抛出异常")
    void testInitDeviceStatus_shouldNotThrow() {
        assertDoesNotThrow(() -> onvifService.initDeviceStatus());
    }

    // ======================== 工具方法 ========================

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
}
