package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
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

import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.SipRelation;
import com.fastbee.iot.mapper.SipRelationMapper;
import com.fastbee.iot.model.vo.SipRelationVO;
import com.fastbee.iot.service.impl.SipRelationServiceImpl;

@DisplayName("监控关联 Service 单元测试")
class SipRelationServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private SipRelationServiceImpl sipRelationService;

    @Mock
    private SipRelationMapper sipRelationMapper;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), SipRelation.class);
        ReflectionTestUtils.setField(sipRelationService, "baseMapper", sipRelationMapper);
    }

    @AfterEach
    void tearDown() {
        if (securityUtilsMock != null) {
            securityUtilsMock.close();
        }
    }

    @Test
    @DisplayName("selectSipRelationByDeviceId - 应返回关联列表")
    void testSelectSipRelationByDeviceId_ShouldReturnList() {
        List<SipRelationVO> expected = List.of(new SipRelationVO());
        when(sipRelationMapper.selectSipRelationList(any(SipRelation.class))).thenReturn(expected);

        List<SipRelationVO> result = sipRelationService.selectSipRelationByDeviceId(10L);

        assertSame(expected, result);
    }

    @Test
    @DisplayName("selectSipRelationList - 应返回分页结果")
    void testSelectSipRelationList_ShouldReturnPage() {
        SipRelation query = new SipRelation();
        query.setPageNum(1);
        query.setPageSize(10);
        Page<SipRelationVO> expected = new Page<>();
        when(sipRelationMapper.selectSipRelationList(any(Page.class), eq(query))).thenReturn(expected);

        Page<SipRelationVO> result = sipRelationService.selectSipRelationList(query);

        assertSame(expected, result);
    }

    @Test
    @DisplayName("addOrUpdateSipRelation - 不存在时应新增并设置创建人")
    void testAddOrUpdateSipRelation_InsertPath_ShouldInsert() {
        SipRelation relation = new SipRelation();
        relation.setChannelId("ch-1");
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getUsername).thenReturn("tester");
        when(sipRelationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(sipRelationMapper.insert(relation)).thenReturn(1);

        int result = sipRelationService.addOrUpdateSipRelation(relation);

        assertEquals(1, result);
        assertEquals("tester", relation.getCreateBy());
        assertNotNull(relation.getCreateTime());
    }

    @Test
    @DisplayName("addOrUpdateSipRelation - 已存在时应更新并设置更新人")
    void testAddOrUpdateSipRelation_UpdatePath_ShouldUpdate() {
        SipRelation relation = new SipRelation();
        relation.setChannelId("ch-1");
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getUsername).thenReturn("tester");
        when(sipRelationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(new SipRelation());
        when(sipRelationMapper.updateByChannelId(relation)).thenReturn(1);

        int result = sipRelationService.addOrUpdateSipRelation(relation);

        assertEquals(1, result);
        assertEquals("tester", relation.getUpdateBy());
        assertNotNull(relation.getUpdateTime());
    }

    @Test
    @DisplayName("deleteSipRelationByIds - 应批量删除")
    void testDeleteSipRelationByIds_ShouldDeleteBatch() {
        Long[] ids = {randomLongId()};
        when(sipRelationMapper.deleteBatchIds(anyList())).thenReturn(1);

        int result = sipRelationService.deleteSipRelationByIds(ids);

        assertEquals(1, result);
    }
}
