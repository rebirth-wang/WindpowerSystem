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
import com.fastbee.iot.domain.News;
import com.fastbee.iot.mapper.NewsMapper;
import com.fastbee.iot.model.CategoryNews;
import com.fastbee.iot.model.vo.NewsVO;
import com.fastbee.iot.service.impl.NewsServiceImpl;

/**
 * {@link NewsServiceImpl} 单元测试
 *
 * 仅覆盖纯业务逻辑与 MyBatis-Plus 交互，不依赖 Security 上下文。
 *
 * @author fastbee
 */
@DisplayName("新闻资讯 Service 单元测试")
public class NewsServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private NewsServiceImpl newsService;

    @Mock
    private NewsMapper newsMapper;

    @BeforeEach
    void setUp() {
        // 初始化 MyBatis-Plus Lambda 缓存，解决纯 Mockito 测试中 LambdaWrapper 报错
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), News.class);
        // 将 newsMapper 注入到 ServiceImpl 的 baseMapper 字段，确保 baseMapper 与 @Mock 同一实例
        ReflectionTestUtils.setField(newsService, "baseMapper", newsMapper);
    }

    // ======================== selectNewsByNewsId ========================

    @Test
    @DisplayName("selectNewsByNewsId - 正常查询应返回 News 对象")
    void testSelectNewsByNewsId_ShouldReturnNews() {
        Long newsId = randomLongId();
        News news = new News();
        news.setNewsId(newsId);
        news.setTitle(randomString());
        when(newsMapper.selectById(newsId)).thenReturn(news);

        News result = newsService.selectNewsByNewsId(newsId);

        assertNotNull(result);
        assertEquals(newsId, result.getNewsId());
        verify(newsMapper).selectById(newsId);
    }

    // ======================== selectNewsList ========================

    @Test
    @DisplayName("selectNewsList - 有数据时应返回转换后的 VO 列表")
    void testSelectNewsList_WithData_ShouldReturnVOList() {
        News query = new News();
        query.setPageNum(1);
        query.setPageSize(10);

        News news = new News();
        news.setNewsId(randomLongId());
        news.setTitle("测试标题");

        when(newsMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(news));

        List<NewsVO> result = newsService.selectNewsList(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(news.getNewsId(), result.get(0).getNewsId());
    }

    @Test
    @DisplayName("selectNewsList - 无数据时应返回空列表")
    void testSelectNewsList_NoData_ShouldReturnEmptyList() {
        News query = new News();
        query.setPageNum(1);
        query.setPageSize(10);

        when(newsMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());

        List<NewsVO> result = newsService.selectNewsList(query);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ======================== pageNewsVO ========================

    @Test
    @DisplayName("pageNewsVO - 正常分页查询，应返回转换后的分页结果")
    void testPageNewsVO_Normal_ShouldReturnConvertedPage() {
        News query = new News();
        query.setPageNum(1);
        query.setPageSize(10);

        News news = new News();
        news.setNewsId(randomLongId());
        news.setTitle("标题");

        Page<News> newsPage = new Page<>();
        newsPage.setTotal(1);
        newsPage.setRecords(List.of(news));

        when(newsMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(newsPage);

        Page<NewsVO> result = newsService.pageNewsVO(query);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(news.getNewsId(), result.getRecords().get(0).getNewsId());
    }

    @Test
    @DisplayName("pageNewsVO - 无数据时，应返回空分页")
    void testPageNewsVO_NoData_ShouldReturnEmptyPage() {
        News query = new News();
        query.setPageNum(1);
        query.setPageSize(10);

        Page<News> emptyPage = new Page<>();
        emptyPage.setTotal(0);
        emptyPage.setRecords(List.of());

        when(newsMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(emptyPage);

        Page<NewsVO> result = newsService.pageNewsVO(query);

        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
    }

    // ======================== selectTopNewsList ========================

    @Test
    @DisplayName("selectTopNewsList - 应按分类聚合置顶新闻")
    void testSelectTopNewsList_ShouldGroupByCategory() {
        Long categoryId1 = randomLongId();
        Long categoryId2 = randomLongId();

        News n1 = new News();
        n1.setNewsId(randomLongId());
        n1.setCategoryId(categoryId1);
        n1.setCategoryName("分类一");

        News n2 = new News();
        n2.setNewsId(randomLongId());
        n2.setCategoryId(categoryId1);
        n2.setCategoryName("分类一");

        News n3 = new News();
        n3.setNewsId(randomLongId());
        n3.setCategoryId(categoryId2);
        n3.setCategoryName("分类二");

        when(newsMapper.selectTopNewsList()).thenReturn(List.of(n1, n2, n3));

        List<CategoryNews> result = newsService.selectTopNewsList();

        assertNotNull(result);
        assertEquals(2, result.size());

        CategoryNews cat1 = result.stream()
                .filter(c -> c.getCategoryId().equals(categoryId1))
                .findFirst()
                .orElseThrow();
        assertEquals(2, cat1.getNewsList().size());

        CategoryNews cat2 = result.stream()
                .filter(c -> c.getCategoryId().equals(categoryId2))
                .findFirst()
                .orElseThrow();
        assertEquals(1, cat2.getNewsList().size());
    }

    // ======================== insertNews ========================

    @Test
    @DisplayName("insertNews - 正常插入应设置创建时间和创建人并返回影响行数")
    void testInsertNews_ShouldSetCreateInfoAndReturnRows() {
        News news = new News();
        news.setTitle("新增新闻");

        when(newsMapper.insert(any(News.class))).thenReturn(1);

        int result = newsService.insertNews(news);

        assertEquals(1, result);
        assertNotNull(news.getCreateTime());
        // createBy 取自 SystemUtils.getUserName()，只校验非空即可
        assertNotNull(news.getCreateBy());
        verify(newsMapper).insert(news);
    }

    // ======================== updateNews ========================

    @Test
    @DisplayName("updateNews - 正常更新应设置更新时间和更新人并返回影响行数")
    void testUpdateNews_ShouldSetUpdateInfoAndReturnRows() {
        News news = new News();
        news.setNewsId(randomLongId());
        news.setTitle("更新新闻");

        when(newsMapper.updateById(any(News.class))).thenReturn(1);

        int result = newsService.updateNews(news);

        assertEquals(1, result);
        assertNotNull(news.getUpdateTime());
        assertNotNull(news.getUpdateBy());
        verify(newsMapper).updateById(news);
    }

    // ======================== deleteNewsByNewsIds / deleteNewsByNewsId ========================

    @Test
    @DisplayName("deleteNewsByNewsIds - 应调用批量删除并返回删除数量")
    void testDeleteNewsByNewsIds_ShouldCallBatchDelete() {
        Long[] ids = {randomLongId(), randomLongId()};
        when(newsMapper.deleteBatchIds(any(List.class))).thenReturn(2);

        int result = newsService.deleteNewsByNewsIds(ids);

        assertEquals(2, result);
        verify(newsMapper).deleteBatchIds(Arrays.asList(ids));
    }

    @Test
    @DisplayName("deleteNewsByNewsId - 应调用按 ID 删除并返回删除数量")
    void testDeleteNewsByNewsId_ShouldCallDeleteById() {
        Long id = randomLongId();
        when(newsMapper.deleteById(id)).thenReturn(1);

        int result = newsService.deleteNewsByNewsId(id);

        assertEquals(1, result);
        verify(newsMapper).deleteById(id);
    }
}

