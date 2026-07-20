package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
import static com.fastbee.framework.core.util.RandomUtils.randomString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.Protocol;
import com.fastbee.iot.mapper.ProtocolMapper;
import com.fastbee.iot.model.vo.ProtocolVO;
import com.fastbee.iot.service.impl.ProtocolServiceImpl;

/**
 * {@link ProtocolServiceImpl} 单元测试
 *
 * @author fastbee
 */
@DisplayName("协议 Service 单元测试")
public class ProtocolServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ProtocolServiceImpl protocolService;

    @Mock
    private ProtocolMapper protocolMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Protocol.class);
        ReflectionTestUtils.setField(protocolService, "baseMapper", protocolMapper);
    }

    @Test
    @DisplayName("selectProtocolById - dataFormat 为 null 时应设为空串")
    void testSelectProtocolById_NullDataFormat_ShouldDefaultEmptyString() {
        Long id = randomLongId();
        Protocol protocol = new Protocol();
        protocol.setId(id);
        protocol.setDataFormat(null);
        when(protocolMapper.selectById(id)).thenReturn(protocol);

        Protocol result = protocolService.selectProtocolById(id);

        assertNotNull(result);
        assertEquals("", result.getDataFormat());
    }

    @Test
    @DisplayName("selectProtocolById - 记录不存在时应返回 null")
    void testSelectProtocolById_NotFound_ShouldReturnNull() {
        Long id = randomLongId();
        when(protocolMapper.selectById(id)).thenReturn(null);

        assertNull(protocolService.selectProtocolById(id));
    }

    @Test
    @DisplayName("pageProtocolVO - 应返回分页 VO")
    void testPageProtocolVO_ShouldReturnPage() {
        Protocol query = new Protocol();
        query.setPageNum(1);
        query.setPageSize(10);
        Protocol row = new Protocol();
        row.setId(randomLongId());
        row.setProtocolCode("MQTT");
        Page<Protocol> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(row));
        when(protocolMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<ProtocolVO> result = protocolService.pageProtocolVO(query);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(row.getId(), result.getRecords().get(0).getId());
    }

    @Test
    @DisplayName("listProtocolVO - 应返回列表 VO")
    void testListProtocolVO_ShouldReturnList() {
        Protocol query = new Protocol();
        Protocol row = new Protocol();
        row.setId(randomLongId());
        when(protocolMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(row));

        List<ProtocolVO> result = protocolService.listProtocolVO(query);

        assertEquals(1, result.size());
        assertEquals(row.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("insertProtocol - protocolStatus 为空时应默认 1")
    void testInsertProtocol_NullStatus_ShouldDefaultOne() {
        Protocol protocol = new Protocol();
        protocol.setProtocolName(randomString());
        protocol.setProtocolStatus(null);
        when(protocolMapper.insert(any(Protocol.class))).thenReturn(1);

        int rows = protocolService.insertProtocol(protocol);

        assertEquals(1, rows);
        assertEquals(1, protocol.getProtocolStatus());
        assertNotNull(protocol.getCreateTime());
        verify(protocolMapper).insert(protocol);
    }

    @Test
    @DisplayName("updateProtocol - 应设置更新时间")
    void testUpdateProtocol_ShouldSetUpdateTime() {
        Protocol protocol = new Protocol();
        protocol.setId(randomLongId());
        when(protocolMapper.updateById(any(Protocol.class))).thenReturn(1);

        int rows = protocolService.updateProtocol(protocol);

        assertEquals(1, rows);
        assertNotNull(protocol.getUpdateTime());
        verify(protocolMapper).updateById(protocol);
    }

    @Test
    @DisplayName("deleteProtocolByIds - 应批量删除")
    void testDeleteProtocolByIds_ShouldBatchDelete() {
        Long[] ids = {randomLongId(), randomLongId()};
        when(protocolMapper.deleteBatchIds(any(List.class))).thenReturn(2);

        int rows = protocolService.deleteProtocolByIds(ids);

        assertEquals(2, rows);
        verify(protocolMapper).deleteBatchIds(Arrays.asList(ids));
    }

    @Test
    @DisplayName("deleteProtocolById - 应按 ID 删除")
    void testDeleteProtocolById_ShouldDelete() {
        Long id = randomLongId();
        when(protocolMapper.deleteById(id)).thenReturn(1);

        assertEquals(1, protocolService.deleteProtocolById(id));
        verify(protocolMapper).deleteById(id);
    }

    @Test
    @DisplayName("selectAll - 应委托 Mapper")
    void testSelectAll_ShouldDelegate() {
        Protocol p = new Protocol();
        p.setId(randomLongId());
        when(protocolMapper.selectAll(1, 0)).thenReturn(List.of(p));

        List<Protocol> result = protocolService.selectAll();

        assertEquals(1, result.size());
        verify(protocolMapper).selectAll(1, 0);
    }

    @Test
    @DisplayName("selectByCondition - 应委托 Mapper")
    void testSelectByCondition_ShouldDelegate() {
        Protocol condition = new Protocol();
        condition.setProtocolCode("X");
        when(protocolMapper.selectByUnion(condition)).thenReturn(List.of());

        List<Protocol> result = protocolService.selectByCondition(condition);

        assertNotNull(result);
        verify(protocolMapper).selectByUnion(condition);
    }
}
