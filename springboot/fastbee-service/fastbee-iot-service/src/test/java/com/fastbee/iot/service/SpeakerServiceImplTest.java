package com.fastbee.iot.service;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.mapper.SpeakerMapper;
import com.fastbee.iot.model.speaker.OauthAccessTokenReportVO;
import com.fastbee.iot.model.speaker.OauthClientDetailsReportVO;
import com.fastbee.iot.service.impl.SpeakerServiceImpl;
import com.fastbee.system.service.ISysUserService;

@DisplayName("音箱上报 Service 单元测试")
class SpeakerServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private SpeakerServiceImpl speakerService;

    @Mock
    private SpeakerMapper speakerMapper;

    @Mock
    private ISysUserService sysUserService;

    @Mock
    private IDeviceService deviceService;

    @Test
    @DisplayName("reportDuerosAttribute - 未配置技能时应直接返回")
    void testReportDuerosAttribute_NoSkill_ShouldReturnEarly() throws IOException {
        when(speakerMapper.selectOauthClientDetailsByType(1)).thenReturn(null);

        speakerService.reportDuerosAttribute("sn", List.of("temp"));

        verifyNoInteractions(deviceService, sysUserService);
    }

    @Test
    @DisplayName("reportDuerosAttribute - 设备不存在时应直接返回")
    void testReportDuerosAttribute_NoDevice_ShouldReturnEarly() throws IOException {
        OauthClientDetailsReportVO details = new OauthClientDetailsReportVO();
        details.setCloudSkillId("skill");
        when(speakerMapper.selectOauthClientDetailsByType(1)).thenReturn(details);
        when(deviceService.selectDeviceBySerialNumber("sn")).thenReturn(null);

        speakerService.reportDuerosAttribute("sn", List.of("temp"));

        verify(deviceService).selectDeviceBySerialNumber("sn");
        verifyNoInteractions(sysUserService);
    }

    @Test
    @DisplayName("reportDuerosAttribute - 用户不存在时应直接返回")
    void testReportDuerosAttribute_NoUser_ShouldReturnEarly() throws IOException {
        OauthClientDetailsReportVO details = new OauthClientDetailsReportVO();
        details.setCloudSkillId("skill");
        details.setClientId("client");
        Device device = new Device();
        device.setTenantId(1L);
        when(speakerMapper.selectOauthClientDetailsByType(1)).thenReturn(details);
        when(deviceService.selectDeviceBySerialNumber("sn")).thenReturn(device);
        when(sysUserService.selectUserById(1L)).thenReturn(null);

        speakerService.reportDuerosAttribute("sn", List.of("temp"));

        verify(sysUserService).selectUserById(1L);
        verify(speakerMapper, never()).selectOauthAccessTokenByUserNameAndClientId(anyString(), anyString());
    }

    @Test
    @DisplayName("reportDuerosAttribute - openId 缺失时应直接返回")
    void testReportDuerosAttribute_NoOpenId_ShouldReturnEarly() throws IOException {
        OauthClientDetailsReportVO details = new OauthClientDetailsReportVO();
        details.setCloudSkillId("skill");
        details.setClientId("client");
        Device device = new Device();
        device.setTenantId(1L);
        SysUser user = new SysUser();
        user.setUserName("tester");
        OauthAccessTokenReportVO token = new OauthAccessTokenReportVO();

        when(speakerMapper.selectOauthClientDetailsByType(1)).thenReturn(details);
        when(deviceService.selectDeviceBySerialNumber("sn")).thenReturn(device);
        when(sysUserService.selectUserById(1L)).thenReturn(user);
        when(speakerMapper.selectOauthAccessTokenByUserNameAndClientId("tester", "client")).thenReturn(token);

        speakerService.reportDuerosAttribute("sn", List.of("temp"));

        verify(speakerMapper).selectOauthAccessTokenByUserNameAndClientId("tester", "client");
        verify(speakerMapper, never()).listAttributesByRelatedIdAndIdentifier(anyLong(), anyList());
    }
}
