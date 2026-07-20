package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

import com.fastbee.common.extend.utils.ObjectChangeDetector;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.ObjectOperationLog;
import com.fastbee.iot.mapper.ObjectOperationLogMapper;
import com.fastbee.iot.service.impl.ObjectOperationLogServiceImpl;

/**
 * {@link ObjectOperationLogServiceImpl} 单元测试
 *
 * @author fastbee
 */
@DisplayName("对象操作日志 Service 单元测试")
public class ObjectOperationLogServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ObjectOperationLogServiceImpl objectOperationLogService;

    @Mock
    private ObjectOperationLogMapper objectOperationLogMapper;

    private MockedStatic<ObjectChangeDetector> changeDetectorMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ObjectOperationLog.class);
        ReflectionTestUtils.setField(objectOperationLogService, "baseMapper", objectOperationLogMapper);
        changeDetectorMock = mockStatic(ObjectChangeDetector.class);
    }

    @AfterEach
    void tearDown() {
        if (changeDetectorMock != null) {
            changeDetectorMock.close();
        }
    }

    @Test
    @DisplayName("pageObjectOperationLog - 应返回分页")
    void testPageObjectOperationLog_ShouldReturnPage() {
        ObjectOperationLog query = new ObjectOperationLog();
        query.setPageNum(1);
        query.setPageSize(10);
        Page<ObjectOperationLog> page = new Page<>();
        page.setTotal(1);
        when(objectOperationLogMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<ObjectOperationLog> result = objectOperationLogService.pageObjectOperationLog(query);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("generateObjectOperationLog - 无变更时应返回 null")
    void testGenerateObjectOperationLog_NoChanges_ShouldReturnNull() {
        changeDetectorMock.when(() -> ObjectChangeDetector.detectChanges(any(), any())).thenReturn("");

        ObjectOperationLog result = objectOperationLogService.generateObjectOperationLog(new Object(), new Object(), 1L, 1, "u");

        assertNull(result);
    }

    @Test
    @DisplayName("generateObjectOperationLog - 有变更时应返回日志对象")
    void testGenerateObjectOperationLog_WithChanges_ShouldReturnLog() {
        String diff = "changed";
        changeDetectorMock.when(() -> ObjectChangeDetector.detectChanges(any(), any())).thenReturn(diff);

        Long objectId = randomLongId();
        int type = 2;
        String createBy = "tester";
        ObjectOperationLog result = objectOperationLogService.generateObjectOperationLog(new Object(), new Object(), objectId, type, createBy);

        assertNotNull(result);
        assertEquals(objectId, result.getObjectId());
        assertEquals(type, result.getType());
        assertEquals(diff, result.getContent());
        assertEquals(createBy, result.getCreateBy());
    }

    @Test
    @DisplayName("insert - 无变更时应返回 false 且不保存")
    void testInsert_NoChanges_ShouldReturnFalse() {
        changeDetectorMock.when(() -> ObjectChangeDetector.detectChanges(any(), any())).thenReturn("");

        Boolean result = objectOperationLogService.insert(new Object(), new Object(), 1L, 1, "u");

        assertFalse(result);
        verify(objectOperationLogMapper, never()).insert(any(ObjectOperationLog.class));
    }

    @Test
    @DisplayName("insert - 有变更时应保存并返回 true")
    void testInsert_WithChanges_ShouldSave() {
        changeDetectorMock.when(() -> ObjectChangeDetector.detectChanges(any(), any())).thenReturn("diff");
        when(objectOperationLogMapper.insert(any(ObjectOperationLog.class))).thenReturn(1);

        Boolean result = objectOperationLogService.insert(new Object(), new Object(), 2L, 3, "u");

        assertTrue(result);
        verify(objectOperationLogMapper).insert(any(ObjectOperationLog.class));
    }

    @Test
    @DisplayName("deleteByObjectIdAndType - 删除成功应返回 true")
    void testDeleteByObjectIdAndType_ShouldReturnTrue() {
        when(objectOperationLogMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(2);

        Boolean result = objectOperationLogService.deleteByObjectIdAndType(List.of(1L, 2L), 1);

        assertTrue(result);
        verify(objectOperationLogMapper).delete(any(LambdaQueryWrapper.class));
    }
}

