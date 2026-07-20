package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.enums.Language;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.cache.ITSLCache;
import com.fastbee.iot.cache.ITSLValueCache;
import com.fastbee.iot.domain.ThingsModel;
import com.fastbee.iot.domain.ThingsModelTag;
import com.fastbee.iot.domain.ThingsModelTranslate;
import com.fastbee.iot.mapper.*;
import com.fastbee.iot.model.ThingsModels.IdentifierVO;
import com.fastbee.iot.model.ThingsModels.ValueItem;
import com.fastbee.iot.model.vo.ThingsModelVO;
import com.fastbee.iot.service.impl.ThingsModelServiceImpl;

@DisplayName("物模型 Service 单元测试")
class ThingsModelServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ThingsModelServiceImpl thingsModelService;

    @Mock
    private ThingsModelMapper thingsModelMapper;
    @Mock
    private ThingsModelTemplateMapper thingsModelTemplateMapper;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private com.fastbee.common.core.redis.RedisCache redisCache;
    @Mock
    private SceneModelDataMapper sceneModelDataMapper;
    @Mock
    private SceneModelDeviceMapper sceneModelDeviceMapper;
    @Mock
    private ITSLValueCache thingModelCache;
    @Mock
    private ITSLCache itslCache;
    @Mock
    private ThingsModelTranslateMapper thingsModelTranslateMapper;
    @Mock
    private DeviceShareMapper shareMapper;
    @Mock
    private IDeviceUserService deviceUserService;
    @Mock
    private ThingsModelTagMapper thingsModelTagMapper;
    @Mock
    private IThingsModelTranslateService translateService;
    @Mock
    private IThingsModelTemplateService thingsModelTemplateService;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ThingsModel.class);
        ReflectionTestUtils.setField(thingsModelService, "baseMapper", thingsModelMapper);
    }

    @AfterEach
    void tearDown() {
        if (securityUtilsMock != null) {
            securityUtilsMock.close();
        }
    }

    @Test
    @DisplayName("selectThingsModelVoByModelId - 英文环境应组装翻译和标签")
    void testSelectThingsModelVoByModelId_ShouldAssembleTranslateAndTags() {
        ThingsModel model = new ThingsModel();
        model.setModelId(1L);
        model.setModelName("温度");
        model.setIdentifier("temp");
        ThingsModelTranslate translate = new ThingsModelTranslate();
        translate.setZhCn("温度");
        translate.setEnUs("Temperature");
        ThingsModelTag tag = new ThingsModelTag();
        tag.setId(7L);
        tag.setIdentifier("tag1");
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLanguage).thenReturn(Language.EN.getValue());
        when(thingsModelMapper.selectById(1L)).thenReturn(model);
        when(thingsModelTranslateMapper.selectById(1L)).thenReturn(translate);
        when(thingsModelTagMapper.selectList(any())).thenReturn(List.of(tag));

        ThingsModelVO result = thingsModelService.selectThingsModelVoByModelId(1L);

        assertNotNull(result);
        assertEquals("Temperature", result.getModelName());
        assertEquals("温度", result.getModelName_zh_CN());
        assertEquals("Temperature", result.getModelName_en_US());
        assertEquals(1, result.getTagVOList().size());
    }

    @Test
    @DisplayName("selectSingleThingsModel - 查询到模型时英文环境应回填英文名")
    void testSelectSingleThingsModel_ShouldFillEnglishName() {
        ThingsModel query = new ThingsModel();
        ThingsModel model = new ThingsModel();
        model.setModelId(2L);
        model.setModelName("湿度");
        ThingsModelTranslate translate = new ThingsModelTranslate();
        translate.setEnUs("Humidity");
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLanguage).thenReturn(Language.EN.getValue());
        when(thingsModelMapper.selectOne(any())).thenReturn(model);
        when(thingsModelTranslateMapper.selectById(2L)).thenReturn(translate);

        ThingsModelVO result = thingsModelService.selectSingleThingsModel(query);

        assertNotNull(result);
        assertEquals("Humidity", result.getModelName());
    }

    @Test
    @DisplayName("revertObjectOrArrayIdentifier - 数组标识应拆分出索引和原始标识")
    void testRevertObjectOrArrayIdentifier_ShouldParseArrayIdentifier() {
        IdentifierVO result = thingsModelService.revertObjectOrArrayIdentifier("array_03_temp");

        assertEquals("temp", result.getIdentifier());
        assertEquals(3, result.getIndex());
    }

    @Test
    @DisplayName("getCacheIdentifierValue - 缓存为空时应返回默认空值")
    void testGetCacheIdentifierValue_WhenCacheMissing_ShouldReturnEmptyValue() {
        when(thingModelCache.getCacheIdentifier(10L, "SN001", "temp")).thenReturn(null);

        ValueItem result = thingsModelService.getCacheIdentifierValue(10L, "SN001", "temp");

        assertNotNull(result);
        assertEquals("", result.getValue());
    }

    @Test
    @DisplayName("selectThingsModelListByModelIds - 应委托 Mapper 查询")
    void testSelectThingsModelListByModelIds_ShouldDelegate() {
        List<ThingsModel> expected = List.of(new ThingsModel());
        when(thingsModelMapper.selectThingsModelListByModelIds(List.of(1L, 2L))).thenReturn(expected);

        List<ThingsModel> result = thingsModelService.selectThingsModelListByModelIds(List.of(1L, 2L));

        assertSame(expected, result);
    }
}
