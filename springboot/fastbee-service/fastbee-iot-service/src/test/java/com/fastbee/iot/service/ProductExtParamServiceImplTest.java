package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
import static com.fastbee.framework.core.util.RandomUtils.randomString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.ProductExtParam;
import com.fastbee.iot.mapper.ProductExtParamMapper;
import com.fastbee.iot.model.vo.ProductExtParamVO;
import com.fastbee.iot.service.impl.ProductExtParamServiceImpl;

/**
 * {@link ProductExtParamServiceImpl} 单元测试
 *
 * <p>跳过依赖 SecurityUtils 的 insert/update 方法，建议由集成测试覆盖。</p>
 *
 * @author fastbee
 */
@DisplayName("产品扩展参数 Service 单元测试")
public class ProductExtParamServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    @Spy
    private ProductExtParamServiceImpl productExtParamService;

    @Mock
    private ProductExtParamMapper productExtParamMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ProductExtParam.class);
        ReflectionTestUtils.setField(productExtParamService, "baseMapper", productExtParamMapper);
    }

    @Test
    @DisplayName("selectProductExtParamById - 应返回实体")
    void testSelectProductExtParamById_ShouldReturnEntity() {
        Long id = randomLongId();
        ProductExtParam row = new ProductExtParam();
        row.setParamId(id);
        when(productExtParamMapper.selectById(id)).thenReturn(row);

        ProductExtParam result = productExtParamService.selectProductExtParamById(id);

        assertNotNull(result);
        assertEquals(id, result.getParamId());
        verify(productExtParamMapper).selectById(id);
    }

    @Test
    @DisplayName("queryByIdWithCache - 应使用 getOne 查询")
    void testQueryByIdWithCache_ShouldGetOne() {
        ProductExtParam query = new ProductExtParam();
        query.setParamId(randomLongId());

        ProductExtParam row = new ProductExtParam();
        row.setParamId(query.getParamId());
        when(productExtParamMapper.selectOne(any(LambdaQueryWrapper.class), eq(true))).thenReturn(row);

        ProductExtParam result = productExtParamService.queryByIdWithCache(query);

        assertNotNull(result);
        verify(productExtParamMapper).selectOne(any(LambdaQueryWrapper.class), eq(true));
    }

    @Test
    @DisplayName("pageProductExtParamVO - 应返回分页 VO")
    void testPageProductExtParamVO_ShouldReturnPage() {
        ProductExtParam query = new ProductExtParam();
        query.setPageNum(1);
        query.setPageSize(10);

        ProductExtParam row = new ProductExtParam();
        row.setParamId(randomLongId());
        row.setParamName(randomString());

        Page<ProductExtParam> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(row));
        when(productExtParamMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<ProductExtParamVO> result = productExtParamService.pageProductExtParamVO(query);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(row.getParamId(), result.getRecords().get(0).getParamId());
    }

    @Test
    @DisplayName("listProductExtParamVO - 应返回 VO 列表")
    void testListProductExtParamVO_ShouldReturnList() {
        ProductExtParam query = new ProductExtParam();
        ProductExtParam row = new ProductExtParam();
        row.setParamId(randomLongId());
        when(productExtParamMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(row));

        List<ProductExtParamVO> result = productExtParamService.listProductExtParamVO(query);

        assertEquals(1, result.size());
        assertEquals(row.getParamId(), result.get(0).getParamId());
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 应 removeByIds")
    void testDeleteWithCacheByIds_ShouldRemove() {
        Long[] ids = {randomLongId(), randomLongId()};
        doReturn(true).when(productExtParamService).removeByIds(anyList());

        Boolean result = productExtParamService.deleteWithCacheByIds(ids, false);

        assertTrue(result);
        verify(productExtParamService).removeByIds(anyList());
    }
}

