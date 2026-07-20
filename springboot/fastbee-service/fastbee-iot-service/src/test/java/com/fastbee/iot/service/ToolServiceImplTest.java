package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.mapper.ProductAuthorizeMapper;
import com.fastbee.iot.model.AuthenticateInputModel;
import com.fastbee.iot.model.MqttAuthenticationModel;
import com.fastbee.iot.model.ProductAuthenticateModel;
import com.fastbee.iot.service.impl.ToolServiceImpl;
import com.fastbee.system.service.ISysUserService;

@DisplayName("工具 Service 单元测试")
class ToolServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ToolServiceImpl toolService;

    @Mock
    private ISysUserService sysUserService;
    @Mock
    private ProductAuthorizeMapper productAuthorizeMapper;
    @Mock
    private IProductAuthorizeService productAuthorizeService;
    @Mock
    private IDeviceService deviceService;

    @Test
    @DisplayName("getStringRandom - 应返回指定长度字符串")
    void testGetStringRandom_ShouldReturnGivenLength() {
        String result = toolService.getStringRandom(8);

        assertEquals(8, result.length());
    }

    @Test
    @DisplayName("generateRandomHex - 应以 D 开头且长度正确")
    void testGenerateRandomHex_ShouldStartWithD() {
        String result = toolService.generateRandomHex(6);

        assertEquals(6, result.length());
        assertTrue(result.startsWith("D"));
    }

    @Test
    @DisplayName("selectUserList - 应委托用户服务")
    void testSelectUserList_ShouldDelegate() {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
        when(sysUserService.queryUserList(any(SysUser.class))).thenReturn(page);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> result = toolService.selectUserList(new SysUser());

        assertSame(page, result);
    }

    @Test
    @DisplayName("simpleMqttAuthentication - 不支持简单认证时应返回 401")
    void testSimpleMqttAuthentication_UnsupportedMethod_ShouldUnauthorized() {
        MqttAuthenticationModel mqttModel = new MqttAuthenticationModel("cid", "user", "pwd");
        ProductAuthenticateModel productModel = new ProductAuthenticateModel();
        productModel.setVertificateMethod(2);

        ResponseEntity result = toolService.simpleMqttAuthentication(mqttModel, productModel);

        assertEquals(401, result.getStatusCode().value());
    }

    @Test
    @DisplayName("clientAuth - clientId 格式错误时应返回 401")
    void testClientAuth_InvalidClientId_ShouldUnauthorized() throws Exception {
        ResponseEntity result = toolService.clientAuth("bad", "user", "pwd");

        assertEquals(401, result.getStatusCode().value());
    }

    @Test
    @DisplayName("clientAuth - 产品不存在时应返回 401")
    void testClientAuth_ProductNotFound_ShouldUnauthorized() throws Exception {
        when(deviceService.selectProductAuthenticate(any(AuthenticateInputModel.class))).thenReturn(null);

        ResponseEntity result = toolService.clientAuth("S&SN001&10&20", "user", "pwd");

        assertEquals(401, result.getStatusCode().value());
    }
}
