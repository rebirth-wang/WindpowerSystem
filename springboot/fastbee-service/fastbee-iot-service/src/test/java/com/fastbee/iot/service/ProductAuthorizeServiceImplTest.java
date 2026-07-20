package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.ProductAuthorize;
import com.fastbee.iot.mapper.ProductAuthorizeMapper;
import com.fastbee.iot.model.vo.ProductAuthorizeVO;
import com.fastbee.iot.service.impl.ProductAuthorizeServiceImpl;

@DisplayName("产品授权码 Service 单元测试")
class ProductAuthorizeServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ProductAuthorizeServiceImpl productAuthorizeService;

    @Mock
    private ProductAuthorizeMapper productAuthorizeMapper;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ProductAuthorize.class);
        ReflectionTestUtils.setField(productAuthorizeService, "baseMapper", productAuthorizeMapper);
    }

    @AfterEach
    void tearDown() {
        if (securityUtilsMock != null) {
            securityUtilsMock.close();
        }
    }

    @Test
    @DisplayName("pageProductAuthorizeVO - 应返回转换后的分页结果")
    void testPageProductAuthorizeVO_ShouldReturnPage() {
        ProductAuthorize query = new ProductAuthorize();
        query.setPageNum(1);
        query.setPageSize(10);
        ProductAuthorize entity = new ProductAuthorize();
        entity.setAuthorizeId(1L);
        Page<ProductAuthorize> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(entity));
        when(productAuthorizeMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<ProductAuthorizeVO> result = productAuthorizeService.pageProductAuthorizeVO(query);

        assertEquals(1, result.getTotal());
        assertEquals(1L, result.getRecords().get(0).getAuthorizeId());
    }

    @Test
    @DisplayName("insertProductAuthorize - 应初始化状态和创建人")
    void testInsertProductAuthorize_ShouldInitFields() {
        ProductAuthorize entity = new ProductAuthorize();
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getUsername).thenReturn("tester");
        when(productAuthorizeMapper.insert(entity)).thenReturn(1);

        int result = productAuthorizeService.insertProductAuthorize(entity);

        assertEquals(1, result);
        assertEquals(1, entity.getStatus());
        assertEquals("tester", entity.getCreateBy());
        assertNotNull(entity.getCreateTime());
    }

    @Test
    @DisplayName("updateProductAuthorize - 已绑定设备时应设置已使用状态")
    void testUpdateProductAuthorize_WithDevice_ShouldMarkUsed() {
        ProductAuthorize entity = new ProductAuthorize();
        entity.setAuthorizeId(1L);
        entity.setDeviceId(2L);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getUsername).thenReturn("tester");
        when(productAuthorizeMapper.updateById(entity)).thenReturn(1);

        int result = productAuthorizeService.updateProductAuthorize(entity);

        assertEquals(1, result);
        assertEquals(2, entity.getStatus());
        assertEquals("tester", entity.getUpdateBy());
        assertNotNull(entity.getUpdateTime());
    }

    @Test
    @DisplayName("addProductAuthorizeByNum - 应按数量批量生成授权码")
    void testAddProductAuthorizeByNum_ShouldInsertBatch() {
        ProductAuthorizeVO vo = new ProductAuthorizeVO();
        vo.setProductId(10L);
        vo.setCreateNum(2);

        SysUser user = new SysUser();
        user.setUserName("tester");
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);

        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        when(productAuthorizeMapper.insertBatch(anyList())).thenReturn(true);

        boolean result = productAuthorizeService.addProductAuthorizeByNum(vo);

        assertTrue(result);
        verify(productAuthorizeMapper).insertBatch(argThat(list -> list.size() == 2));
    }
}
