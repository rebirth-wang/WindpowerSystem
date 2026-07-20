package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
import static com.fastbee.framework.core.util.RandomUtils.randomString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.AppVersion;
import com.fastbee.iot.mapper.AppVersionMapper;
import com.fastbee.iot.model.vo.AppVersionVO;
import com.fastbee.iot.service.impl.AppVersionServiceImpl;

/**
 * {@link AppVersionServiceImpl} 单元测试
 *
 * <p>跳过依赖 SecurityUtils 的 insert/update 方法，建议由集成测试覆盖。</p>
 *
 * @author fastbee
 */
@DisplayName("APP 版本 Service 单元测试")
@MockitoSettings(strictness = Strictness.LENIENT)
public class AppVersionServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    @Spy
    private AppVersionServiceImpl appVersionService;

    @Mock
    private AppVersionMapper appVersionMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), AppVersion.class);
        ReflectionTestUtils.setField(appVersionService, "baseMapper", appVersionMapper);
    }

    @Test
    @DisplayName("selectAppVersionById - 应返回实体")
    void testSelectAppVersionById_ShouldReturnEntity() {
        Long id = randomLongId();
        AppVersion row = new AppVersion();
        row.setId(id);
        when(appVersionMapper.selectById(id)).thenReturn(row);

        AppVersion result = appVersionService.selectAppVersionById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(appVersionMapper).selectById(id);
    }

    @Test
    @DisplayName("pageAppVersionVO - 应返回分页 VO")
    void testPageAppVersionVO_ShouldReturnPage() {
        AppVersion query = new AppVersion();
        query.setPageNum(1);
        query.setPageSize(10);

        AppVersion row = new AppVersion();
        row.setId(randomLongId());
        row.setVersion("1.0.0");

        Page<AppVersion> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(row));
        when(appVersionMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<AppVersionVO> result = appVersionService.pageAppVersionVO(query);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(row.getId(), result.getRecords().get(0).getId());
    }

    @Test
    @DisplayName("listAppVersionVO - 应返回 VO 列表")
    void testListAppVersionVO_ShouldReturnList() {
        AppVersion query = new AppVersion();
        AppVersion row = new AppVersion();
        row.setId(randomLongId());
        row.setVersionName(randomString());
        when(appVersionMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(row));

        List<AppVersionVO> result = appVersionService.listAppVersionVO(query);

        assertEquals(1, result.size());
        assertEquals(row.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 应 removeByIds")
    void testDeleteWithCacheByIds_ShouldRemove() {
        Long[] ids = {randomLongId(), randomLongId()};
        doReturn(true).when(appVersionService).removeByIds(anyList());
        Boolean result = appVersionService.deleteWithCacheByIds(ids, true);

        assertTrue(result);
        verify(appVersionService).removeByIds(anyList());    }

    @Test
    @DisplayName("selectLatestAppVersion - 应 getOne 查询最新版本")
    void testSelectLatestAppVersion_ShouldGetOne() {
        AppVersion row = new AppVersion();
        row.setId(randomLongId());
        row.setVersion("9.9.9");
        doReturn(row).when(appVersionService).getOne(any());
        AppVersion result = appVersionService.selectLatestAppVersion(new AppVersion());

        assertNotNull(result);
        assertEquals("9.9.9", result.getVersion());
//        verify(appVersionMapper).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectLatestApkVersion - 应 getOne 查询最新 APK 版本")
    void testSelectLatestApkVersion_ShouldGetOne() {
        AppVersion row = new AppVersion();
        row.setId(randomLongId());
        row.setVersion("8.8.8");
        doReturn(row).when(appVersionService).getOne(any());
        AppVersion result = appVersionService.selectLatestApkVersion(new AppVersion());

        assertNotNull(result);
        assertEquals("8.8.8", result.getVersion());
//        verify(appVersionMapper).selectOne(any(LambdaQueryWrapper.class));
    }
}

