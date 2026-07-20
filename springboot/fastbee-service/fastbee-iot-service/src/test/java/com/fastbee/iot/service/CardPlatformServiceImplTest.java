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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.utils.spring.SpringUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.Card;
import com.fastbee.iot.domain.CardPlatform;
import com.fastbee.iot.mapper.CardMapper;
import com.fastbee.iot.mapper.CardPlatformMapper;
import com.fastbee.iot.model.vo.CardPlatformVO;
import com.fastbee.iot.service.impl.CardPlatformServiceImpl;

/**
 * {@link CardPlatformServiceImpl} 单元测试（已修复版）
 */
@DisplayName("物联卡平台 Service 单元测试")
@MockitoSettings(strictness = Strictness.LENIENT)
public class CardPlatformServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    @Spy // 关键：必须使用 Spy
    private CardPlatformServiceImpl cardPlatformService;

    @Mock
    private CardPlatformMapper cardPlatformMapper;

    @Mock
    private CardMapper cardMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), ""),
                CardPlatform.class
        );
        ReflectionTestUtils.setField(cardPlatformService, "baseMapper", cardPlatformMapper);
    }

    // ===================== 查询 =====================

    @Test
    @DisplayName("queryByIdWithCache - 应使用 getOne 查询")
    void testQueryByIdWithCache_ShouldGetOne() {
        CardPlatform query = new CardPlatform();
        query.setId(randomLongId());

        CardPlatform row = new CardPlatform();
        row.setId(query.getId());

        // 核心修复：mock getOne，而不是 mapper.selectOne
        doReturn(row).when(cardPlatformService).getOne(any());

        CardPlatform result = cardPlatformService.queryByIdWithCache(query);

        assertNotNull(result);
        assertEquals(query.getId(), result.getId());
    }

    @Test
    @DisplayName("selectCardPlatformById - 应返回实体")
    void testSelectCardPlatformById_ShouldReturnEntity() {
        Long id = randomLongId();
        CardPlatform row = new CardPlatform();
        row.setId(id);

        when(cardPlatformMapper.selectById(id)).thenReturn(row);

        CardPlatform result = cardPlatformService.selectCardPlatformById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(cardPlatformMapper).selectById(id);
    }

    @Test
    @DisplayName("pageCardPlatformVO - 应返回分页 VO")
    void testPageCardPlatformVO_ShouldReturnPage() {
        CardPlatform query = new CardPlatform();
        query.setPageNum(1);
        query.setPageSize(10);

        CardPlatform row = new CardPlatform();
        row.setId(randomLongId());
        row.setName("p");

        Page<CardPlatform> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(row));

        when(cardPlatformMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        Page<CardPlatformVO> result = cardPlatformService.pageCardPlatformVO(query);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(row.getId(), result.getRecords().get(0).getId());
    }

    @Test
    @DisplayName("listCardPlatformVO - 应返回 VO 列表")
    void testListCardPlatformVO_ShouldReturnList() {
        CardPlatform query = new CardPlatform();

        CardPlatform row = new CardPlatform();
        row.setId(randomLongId());

        when(cardPlatformMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(row));

        List<CardPlatformVO> result = cardPlatformService.listCardPlatformVO(query);

        assertEquals(1, result.size());
        assertEquals(row.getId(), result.get(0).getId());
    }

    // ===================== 删除 =====================

    @Test
    void testDeleteWithCacheByIds_HasCards_ShouldError() {
        Long[] ids = {randomLongId()};

        when(cardMapper.selectList(any()))
                .thenReturn(List.of(new Card()));

        try (MockedStatic<com.fastbee.common.utils.spring.SpringUtils> mocked =
                     mockStatic(com.fastbee.common.utils.spring.SpringUtils.class)) {

            MessageSource messageSource = mock(MessageSource.class);

            when(messageSource.getMessage(anyString(), any(), any()))
                    .thenReturn("error");

            mocked.when(() -> com.fastbee.common.utils.spring.SpringUtils.getBean((String) any()))
                    .thenReturn(messageSource);

            AjaxResult result = cardPlatformService.deleteWithCacheByIds(ids, true);

            assertNotEquals(200, result.get(AjaxResult.CODE_TAG));
        }
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 无卡且删除成功")
    void testDeleteWithCacheByIds_NoCards_DeleteSuccess_ShouldOk() {
        Long[] ids = {randomLongId(), randomLongId()};

        when(cardMapper.selectList(any()))
                .thenReturn(List.of());

        doReturn(true).when(cardPlatformService).removeByIds(any());

        try (MockedStatic<SpringUtils> mocked = mockStatic(SpringUtils.class)) {

            MessageSource messageSource = mock(MessageSource.class);

            when(messageSource.getMessage(anyString(), any(), any()))
                    .thenReturn("ok");

            mocked.when(() -> SpringUtils.getBean(MessageSource.class))
                    .thenReturn(messageSource);

            AjaxResult result = cardPlatformService.deleteWithCacheByIds(ids, true);

            assertEquals(200, result.get(AjaxResult.CODE_TAG));
        }
    }
}
