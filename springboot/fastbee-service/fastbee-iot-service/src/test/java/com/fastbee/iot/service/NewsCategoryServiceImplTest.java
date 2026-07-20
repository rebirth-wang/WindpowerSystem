package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
import static com.fastbee.framework.core.util.RandomUtils.randomString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
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

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.NewsCategory;
import com.fastbee.iot.mapper.NewsCategoryMapper;
import com.fastbee.iot.model.IdAndName;
import com.fastbee.iot.model.vo.NewsCategoryVO;
import com.fastbee.iot.service.impl.NewsCategoryServiceImpl;

/**
 * {@link NewsCategoryServiceImpl} 单元测试
 *
 * @author fastbee
 */
@DisplayName("新闻分类 Service 单元测试")
public class NewsCategoryServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private NewsCategoryServiceImpl newsCategoryService;

    @Mock
    private NewsCategoryMapper newsCategoryMapper;

    private MockedStatic<MessageUtils> messageUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), ""), NewsCategory.class);
        ReflectionTestUtils.setField(newsCategoryService, "baseMapper", newsCategoryMapper);
        messageUtilsMock = mockStatic(MessageUtils.class);
        messageUtilsMock.when(() -> MessageUtils.message(anyString())).thenReturn("mocked message");
    }

    @AfterEach
    void tearDown() {
        if (messageUtilsMock != null) {
            messageUtilsMock.close();
        }
    }

    @Test
    @DisplayName("selectNewsCategoryByCategoryId - 应返回分类")
    void testSelectNewsCategoryByCategoryId_ShouldReturnEntity() {
        Long id = randomLongId();
        NewsCategory entity = new NewsCategory();
        entity.setCategoryId(id);
        entity.setCategoryName(randomString());
        when(newsCategoryMapper.selectById(id)).thenReturn(entity);

        NewsCategory result = newsCategoryService.selectNewsCategoryByCategoryId(id);

        assertNotNull(result);
        assertEquals(id, result.getCategoryId());
        verify(newsCategoryMapper).selectById(id);
    }

    @Test
    @DisplayName("selectNewsCategoryList - 有数据时应返回 VO 列表")
    void testSelectNewsCategoryList_WithData_ShouldReturnList() {
        NewsCategory query = new NewsCategory();
        NewsCategory row = new NewsCategory();
        row.setCategoryId(randomLongId());
        row.setCategoryName("标题");
        when(newsCategoryMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(row));

        List<NewsCategoryVO> result = newsCategoryService.selectNewsCategoryList(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(row.getCategoryId(), result.get(0).getCategoryId());
    }

    @Test
    @DisplayName("pageNewsCategoryVO - 应返回分页 VO")
    void testPageNewsCategoryVO_ShouldReturnPage() {
        NewsCategory query = new NewsCategory();
        query.setPageNum(1);
        query.setPageSize(10);
        NewsCategory row = new NewsCategory();
        row.setCategoryId(randomLongId());
        row.setCategoryName("分类");
        Page<NewsCategory> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(row));
        when(newsCategoryMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<NewsCategoryVO> result = newsCategoryService.pageNewsCategoryVO(query);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(row.getCategoryId(), result.getRecords().get(0).getCategoryId());
    }

    @Test
    @DisplayName("selectNewsCategoryShortList - 应委托 Mapper 查询")
    void testSelectNewsCategoryShortList_ShouldDelegate() {
        IdAndName item = new IdAndName();
        item.setId(randomLongId());
        item.setName("n");
        when(newsCategoryMapper.selectNewsCategoryShortList()).thenReturn(List.of(item));

        List<IdAndName> result = newsCategoryService.selectNewsCategoryShortList();

        assertEquals(1, result.size());
        assertEquals(item.getId(), result.get(0).getId());
        verify(newsCategoryMapper).selectNewsCategoryShortList();
    }

    @Test
    @DisplayName("insertNewsCategory - 应设置创建信息并插入")
    void testInsertNewsCategory_ShouldInsert() {
        NewsCategory entity = new NewsCategory();
        entity.setCategoryName("new");
        when(newsCategoryMapper.insert(any(NewsCategory.class))).thenReturn(1);

        int rows = newsCategoryService.insertNewsCategory(entity);

        assertEquals(1, rows);
        assertNotNull(entity.getCreateTime());
        assertNotNull(entity.getCreateBy());
        verify(newsCategoryMapper).insert(entity);
    }

    @Test
    @DisplayName("updateNewsCategory - 应设置更新信息并更新")
    void testUpdateNewsCategory_ShouldUpdate() {
        NewsCategory entity = new NewsCategory();
        entity.setCategoryId(randomLongId());
        when(newsCategoryMapper.updateById(any(NewsCategory.class))).thenReturn(1);

        int rows = newsCategoryService.updateNewsCategory(entity);

        assertEquals(1, rows);
        assertNotNull(entity.getUpdateTime());
        assertNotNull(entity.getUpdateBy());
        verify(newsCategoryMapper).updateById(entity);
    }

    @Test
    @DisplayName("deleteNewsCategoryByCategoryIds - 分类下有新闻时应返回错误")
    void testDeleteNewsCategoryByCategoryIds_HasNews_ShouldError() {
        Long[] ids = {randomLongId()};
        when(newsCategoryMapper.newsCountInCategorys(ids)).thenReturn(1);

        AjaxResult result = newsCategoryService.deleteNewsCategoryByCategoryIds(ids);

        assertNotEquals(200, result.get(AjaxResult.CODE_TAG));
        verify(newsCategoryMapper, never()).deleteBatchIds(anyList());
    }

    @Test
    @DisplayName("deleteNewsCategoryByCategoryIds - 无新闻且删除成功应返回成功")
    void testDeleteNewsCategoryByCategoryIds_Success_ShouldReturnOk() {
        Long[] ids = {randomLongId()};
        when(newsCategoryMapper.newsCountInCategorys(ids)).thenReturn(0);
        when(newsCategoryMapper.deleteBatchIds(anyList())).thenReturn(1);

        AjaxResult result = newsCategoryService.deleteNewsCategoryByCategoryIds(ids);

        assertEquals(200, result.get(AjaxResult.CODE_TAG));
        verify(newsCategoryMapper).deleteBatchIds(Arrays.asList(ids));
    }

    @Test
    @DisplayName("deleteNewsCategoryByCategoryId - 应按 ID 删除")
    void testDeleteNewsCategoryByCategoryId_ShouldDelete() {
        Long id = randomLongId();
        when(newsCategoryMapper.deleteById(id)).thenReturn(1);

        int rows = newsCategoryService.deleteNewsCategoryByCategoryId(id);

        assertEquals(1, rows);
        verify(newsCategoryMapper).deleteById(id);
    }
}
