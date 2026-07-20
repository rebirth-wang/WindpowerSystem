package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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

import com.fastbee.common.extend.core.domin.model.GroupCount;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.card.factory.CardPlatformFactory;
import com.fastbee.iot.domain.Card;
import com.fastbee.iot.domain.CardPlatform;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.mapper.CardMapper;
import com.fastbee.iot.mapper.CardPlatformMapper;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.model.vo.CardStatisticsVO;
import com.fastbee.iot.model.vo.CardVO;
import com.fastbee.iot.service.impl.CardServiceImpl;

@DisplayName("物联网卡 Service 单元测试")
class CardServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardMapper cardMapper;
    @Mock
    private CardPlatformFactory cardPlatformFactory;
    @Mock
    private CardPlatformMapper cardPlatformMapper;
    @Mock
    private DeviceMapper deviceMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Card.class);
        ReflectionTestUtils.setField(cardService, "baseMapper", cardMapper);
    }

    @Test
    @DisplayName("queryByIdWithCache - 应补充平台和设备信息")
    void testQueryByIdWithCache_ShouldFillPlatformAndDevice() {
        Card query = new Card();
        query.setId(1L);
        Card card = new Card();
        card.setId(1L);
        card.setCardPlatformId(2L);
        card.setDeviceId(3L);
        CardPlatform platform = new CardPlatform();
        platform.setId(2L);
        platform.setName("移动");
        Device device = new Device();
        device.setDeviceId(3L);
        device.setDeviceName("设备A");
        device.setSerialNumber("SN001");
        device.setProductId(11L);
        device.setProductName("产品A");
        when(cardMapper.selectOne(any(LambdaQueryWrapper.class), eq(true))).thenReturn(card);
        when(cardPlatformMapper.selectById(2L)).thenReturn(platform);
        when(deviceMapper.selectById(3L)).thenReturn(device);

        CardVO result = cardService.queryByIdWithCache(query);

        assertEquals("移动", result.getCardPlatformName());
        assertEquals("设备A", result.getDeviceName());
        assertEquals("SN001", result.getSerialNumber());
    }

    @Test
    @DisplayName("selectIccIdByDeviceId - 有绑定卡时应返回 iccid")
    void testSelectIccIdByDeviceId_ShouldReturnIccid() {
        Card card = new Card();
        card.setIccid("8986");
        when(cardMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(card);

        String result = cardService.selectIccIdByDeviceId(1L);

        assertEquals("8986", result);
    }

    @Test
    @DisplayName("statistics - 无排行卡片时应返回空列表")
    void testStatistics_NoTopCards_ShouldReturnEmptyCardList() {
        GroupCount statusCount = new GroupCount();
        statusCount.setKey(1);
        statusCount.setCount(2);
        when(cardMapper.countCardStatusGroup()).thenReturn(List.of(statusCount));
        when(cardMapper.countCardOperatorGroup()).thenReturn(List.of());
        when(cardMapper.countCardPlatformGroup()).thenReturn(List.of());
        Page<Card> page = new Page<>();
        page.setTotal(0);
        when(cardMapper.selectPage(any(Page.class), any())).thenReturn(page);

        CardStatisticsVO result = cardService.statistics();

        assertNotNull(result.getCardVOList());
        assertTrue(result.getCardVOList().isEmpty());
    }

    @Test
    @DisplayName("statistics - 有卡片时应计算流量占比和告警标记")
    void testStatistics_WithCards_ShouldCalculateTrafficProportion() {
        when(cardMapper.countCardStatusGroup()).thenReturn(List.of());
        when(cardMapper.countCardOperatorGroup()).thenReturn(List.of());
        when(cardMapper.countCardPlatformGroup()).thenReturn(List.of());
        Card card = new Card();
        card.setMsisdn("138");
        card.setTotalData(new BigDecimal("100"));
        card.setDataUsed(new BigDecimal("80"));
        card.setDataAlertThreshold(new BigDecimal("70"));
        Page<Card> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(card));
        when(cardMapper.selectPage(any(Page.class), any())).thenReturn(page);

        CardStatisticsVO result = cardService.statistics();

        assertEquals(1, result.getCardVOList().size());
        assertEquals(new BigDecimal("80.0000"), result.getCardVOList().get(0).getTrafficProportion());
        assertTrue(result.getCardVOList().get(0).getAlertFlag());
    }
}
