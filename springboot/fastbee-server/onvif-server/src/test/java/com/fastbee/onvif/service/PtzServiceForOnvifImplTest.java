package com.fastbee.onvif.service;

import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.model.FrontEndControlCodeForPTZ;
import com.fastbee.media.model.FrontEndControlCodeForPreset;
import com.fastbee.media.model.Preset;
import com.fastbee.media.model.PtzInput;
import com.fastbee.onvif.domain.OnvifDeviceChannel;
import com.fastbee.onvif.service.impl.PtzServiceForOnvifImpl;
import com.fastbee.onvif.util.OnvifClient;

/**
 * {@link PtzServiceForOnvifImpl} 单元测试
 *
 * <p>覆盖场景：
 * <ul>
 *   <li>通道不存在时 ptz 抛出 ServiceException</li>
 *   <li>ptz(PtzInput) 停止指令调用 PTZ Stop</li>
 *   <li>ptz(PtzInput) 移动指令调用 ContinuousMove</li>
 *   <li>preset - GotoPreset 正常调用</li>
 *   <li>preset - SetPreset 正常调用</li>
 *   <li>queryPresetList 正常返回预置位列表</li>
 * </ul>
 *
 * @author fastbee
 */
@DisplayName("PtzServiceForOnvifImpl 单元测试")
class PtzServiceForOnvifImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private PtzServiceForOnvifImpl ptzService;

    @Mock
    private IOnvifDeviceChannelService onvifDeviceChannelService;

    @Mock
    private OnvifClient onvifClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(ptzService, "onvifDeviceChannelService", onvifDeviceChannelService);
        ReflectionTestUtils.setField(ptzService, "onvifClient", onvifClient);
    }

    // ======================== ptz(PtzInput) ========================

    @Test
    @DisplayName("ptz(PtzInput) - 通道不存在时抛出 ServiceException")
    void testPtz_channelNotFound_shouldThrow() {
        CommonChannel channel = buildCommonChannel(999, "999");
        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(999)).thenReturn(null);

        PtzInput input = new PtzInput();
        input.setLeftRight(1);
        input.setUpDown(0);
        input.setInOut(0);
        input.setMoveSpeed(50);

        assertThrows(ServiceException.class, () -> ptzService.ptz(channel, input));
    }

    @Test
    @DisplayName("ptz(PtzInput) - 停止指令（全为0）调用 PTZ Stop")
    void testPtz_stopInput_shouldCallPtzStop() throws Exception {
        CommonChannel channel = buildCommonChannel(1, "1");
        OnvifDeviceChannel onvifChannel = buildChannel(1);
        onvifChannel.setProfileToken("Profile_000");

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(1)).thenReturn(onvifChannel);

        PtzInput input = new PtzInput();
        input.setLeftRight(0);
        input.setUpDown(0);
        input.setInOut(0);
        input.setMoveSpeed(0);

        Boolean result = ptzService.ptz(channel, input);

        assertTrue(result);
        verify(onvifClient).ptzStop(onvifChannel, true, true);
        verify(onvifClient, never()).continuousMove(any(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("ptz(PtzInput) - 移动指令调用 ContinuousMove")
    void testPtz_moveInput_shouldCallContinuousMove() throws Exception {
        CommonChannel channel = buildCommonChannel(1, "1");
        OnvifDeviceChannel onvifChannel = buildChannel(1);
        onvifChannel.setProfileToken("Profile_000");

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(1)).thenReturn(onvifChannel);
        doNothing().when(onvifClient).continuousMove(any(), anyDouble(), anyDouble(), anyDouble());

        PtzInput input = new PtzInput();
        input.setLeftRight(1);  // 向右
        input.setUpDown(0);
        input.setInOut(0);
        input.setMoveSpeed(50);

        Boolean result = ptzService.ptz(channel, input);

        assertTrue(result);
        verify(onvifClient).continuousMove(eq(onvifChannel), AdditionalMatchers.gt(0.0), eq(0.0), eq(0.0));
    }

    // ======================== ptz(FrontEndControlCodeForPTZ) ========================

    @Test
    @DisplayName("ptz(FEC) - Pan 右移调用 ContinuousMove")
    void testPtzFec_panRight_shouldCallContinuousMove() throws Exception {
        CommonChannel channel = buildCommonChannel(1, "1");
        OnvifDeviceChannel onvifChannel = buildChannel(1);
        onvifChannel.setProfileToken("Profile_000");

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(1)).thenReturn(onvifChannel);
        doNothing().when(onvifClient).continuousMove(any(), anyDouble(), anyDouble(), anyDouble());

        FrontEndControlCodeForPTZ fec = new FrontEndControlCodeForPTZ();
        fec.setPan(1);  // 向右
        fec.setPanSpeed(50);
        fec.setTiltSpeed(50);
        fec.setZoomSpeed(50);

        Boolean result = ptzService.ptz(channel, fec);

        assertTrue(result);
        verify(onvifClient).continuousMove(eq(onvifChannel), AdditionalMatchers.gt(0.0), anyDouble(), anyDouble());
    }

    // ======================== preset ========================

    @Test
    @DisplayName("preset - GotoPreset（code=2）正常调用")
    void testPreset_gotoPreset_shouldCallOnvif() throws Exception {
        CommonChannel channel = buildCommonChannel(1, "1");
        OnvifDeviceChannel onvifChannel = buildChannel(1);
        onvifChannel.setProfileToken("Profile_000");

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(1)).thenReturn(onvifChannel);
        doNothing().when(onvifClient).gotoPreset(any(), anyString());

        FrontEndControlCodeForPreset preset = new FrontEndControlCodeForPreset();
        preset.setCode(2);  // 调用预置位
        preset.setPresetId(3);

        Boolean result = ptzService.preset(channel, preset);

        assertTrue(result);
        verify(onvifClient).gotoPreset(onvifChannel, "3");
    }

    @Test
    @DisplayName("preset - SetPreset（code=1）正常调用")
    void testPreset_setPreset_shouldCallOnvif() throws Exception {
        CommonChannel channel = buildCommonChannel(1, "1");
        OnvifDeviceChannel onvifChannel = buildChannel(1);
        onvifChannel.setProfileToken("Profile_000");

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(1)).thenReturn(onvifChannel);
        when(onvifClient.setPreset(any(), anyString())).thenReturn("NewPresetToken");

        FrontEndControlCodeForPreset preset = new FrontEndControlCodeForPreset();
        preset.setCode(1);  // 设置预置位
        preset.setPresetId(1);
        preset.setPresetName("Home");

        Boolean result = ptzService.preset(channel, preset);

        assertTrue(result);
        verify(onvifClient).setPreset(onvifChannel, "Home");
    }

    @Test
    @DisplayName("preset - 调用预置位时 presetId 为 null 抛出 ServiceException")
    void testPreset_gotoPreset_noPresetId_shouldThrow() {
        CommonChannel channel = buildCommonChannel(1, "1");
        OnvifDeviceChannel onvifChannel = buildChannel(1);
        onvifChannel.setProfileToken("Profile_000");

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(1)).thenReturn(onvifChannel);

        FrontEndControlCodeForPreset preset = new FrontEndControlCodeForPreset();
        preset.setCode(2);
        preset.setPresetId(null);  // 缺少 presetId

        assertThrows(ServiceException.class, () -> ptzService.preset(channel, preset));
    }

    // ======================== queryPresetList ========================

    @Test
    @DisplayName("queryPresetList - 正常返回预置位列表")
    void testQueryPresetList_success() throws Exception {
        CommonChannel channel = buildCommonChannel(1, "1");
        OnvifDeviceChannel onvifChannel = buildChannel(1);
        onvifChannel.setProfileToken("Profile_000");

        Map<String, String> preset1 = new LinkedHashMap<>();
        preset1.put("token", "Preset_001");
        preset1.put("name", "Home");
        Map<String, String> preset2 = new LinkedHashMap<>();
        preset2.put("token", "Preset_002");
        preset2.put("name", "Position1");

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(1)).thenReturn(onvifChannel);
        when(onvifClient.getPresets(onvifChannel)).thenReturn(Arrays.asList(preset1, preset2));

        List<Preset> result = ptzService.queryPresetList(channel);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Preset_001", result.get(0).getPresetId());
        assertEquals("Home", result.get(0).getPresetName());
    }

    @Test
    @DisplayName("queryPresetList - OnvifClient 异常时返回空列表")
    void testQueryPresetList_onvifFails_shouldReturnEmpty() throws Exception {
        CommonChannel channel = buildCommonChannel(1, "1");
        OnvifDeviceChannel onvifChannel = buildChannel(1);
        onvifChannel.setProfileToken("Profile_000");

        when(onvifDeviceChannelService.selectOnvifDeviceChannelById(1)).thenReturn(onvifChannel);
        when(onvifClient.getPresets(onvifChannel)).thenThrow(new RuntimeException("SOAP error"));

        List<Preset> result = ptzService.queryPresetList(channel);

        assertNotNull(result);
        assertTrue(result.isEmpty());
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
}
