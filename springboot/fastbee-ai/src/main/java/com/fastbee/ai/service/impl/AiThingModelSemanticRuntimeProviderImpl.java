package com.fastbee.ai.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.model.vo.AiSemanticFieldVO;
import com.fastbee.ai.model.vo.AiSemanticValueMappingVO;
import com.fastbee.ai.service.IAiDeviceMetadataService;
import com.fastbee.ai.service.IAiDeviceResolveService;
import com.fastbee.ai.service.IAiSemanticRuntimeProvider;
import com.fastbee.ai.support.AiRuntimeFieldSelectionSupport;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.cache.ITSLCache;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.enums.ThingsModelTypeEnum;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.DeviceShortOutput;
import com.fastbee.iot.model.ThingsModelItem.Datatype;
import com.fastbee.iot.model.ThingsModelItem.EnumItem;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;

@Service
public class AiThingModelSemanticRuntimeProviderImpl implements IAiSemanticRuntimeProvider {

    private static final String PROVIDER_CODE = "THING_MODEL";
    private static final String KB_CODE = "TSL_RUNTIME";
    private static final String VERSION_NO = "RUNTIME";
    private static final String SOURCE_TYPE = "TSL";
    private static final String ROUTE_AUTO = "AUTO";
    private static final String ROUTE_REDIS_VALUE = "REDIS_VALUE";
    private static final String ROUTE_TSDB_QUERY = "TSDB_QUERY";
    private static final String ROUTE_HYBRID_PIPELINE = "HYBRID_PIPELINE";
    private static final String TABLE_NAME = "iot_things_model";
    private static final String COLUMN_NAME = "model_name";
    private static final int CORE_METRIC_MATCH_BONUS = 60;
    private static final int CORE_METRIC_MISS_PENALTY = 45;
    private static final List<String> METRIC_INTENT_TOKENS = List.of(
            "当前", "实时", "现在", "此刻", "最新", "历史", "最近", "当天", "今天", "昨天", "昨日", "趋势", "统计"
    );
    private static final List<String> METRIC_QUESTION_NOISE_TOKENS = List.of(
            "productname", "devicename", "serialnumber", "identifier",
            "产品名称", "产品名", "设备名称", "设备编号", "标识符",
            "物模型", "指标", "当前值", "实时值", "数值", "读数",
            "是多少", "多少", "什么", "查看", "看下", "查下"
    );
    /**
     * 物模型名称里的修饰词。
     * 这些词通常不会改变指标主语义，但会导致简称问法无法命中，例如“空气温度”“室内亮度”“运行档位”。
     */
    private static final List<String> SEMANTIC_DECORATION_TOKENS = List.of(
            "空气", "环境", "室内", "室外", "回风", "送风", "进风", "出风",
            "设备", "运行", "当前", "实时", "累计", "总", "监测", "上报",
            "只读", "只写", "读数", "数值"
    );
    /**
     * 常见核心指标词，用于把“空气温度”“总有功功率”“设备开关”归一到更稳定的简称语义。
     */
    private static final List<String> CORE_METRIC_KEYWORDS = List.of(
            "功率因数", "有功功率", "无功功率", "视在功率", "有功电能", "无功电能",
            "温度", "湿度", "电量", "电能", "电压", "电流", "功率", "频率",
            "亮度", "光照", "压力", "流量", "液位", "转速", "档位", "开关",
            "状态", "消息", "二氧化碳", "co2", "pm2.5", "pm10"
    );

    @Resource
    private IAiDeviceResolveService aiDeviceResolveService;

    @Resource
    private IAiDeviceMetadataService aiDeviceMetadataService;

    @Resource
    private ITSLCache itslCache;

    @Resource
    private FastBeeAiProperties fastBeeAiProperties;

    @Override
    public String getProviderCode() {
        return PROVIDER_CODE;
    }

    @Override
    public List<AiSemanticFieldVO> listSemanticFields(String question) {
        if (StringUtils.isBlank(question)) {
            return new ArrayList<>();
        }
        String explicitIdentifier = AiRuntimeFieldSelectionSupport.extractExplicitIdentifier(question);

        DeviceMetaData deviceMetaData = resolveRuntimeDeviceMetaData(question);
        if (deviceMetaData == null || deviceMetaData.getProduct() == null) {
            return new ArrayList<>();
        }

        Product product = deviceMetaData.getProduct();
        List<ThingsModelValueItem> thingsModelList = loadThingsModels(product.getProductId());
        if (thingsModelList.isEmpty()) {
            return new ArrayList<>();
        }

        String normalizedQuestion = normalizeText(question);
        String normalizedMetricQuestion = normalizeMetricQuestion(question, deviceMetaData, normalizedQuestion);
        if (StringUtils.isNotBlank(explicitIdentifier)) {
            AiSemanticFieldVO explicitField = buildExplicitIdentifierField(question, normalizedQuestion,
                    normalizedMetricQuestion, deviceMetaData, thingsModelList, explicitIdentifier);
            if (explicitField != null) {
                return new ArrayList<>(List.of(explicitField));
            }
        }
        List<AiSemanticFieldVO> candidates = new ArrayList<>();
        for (ThingsModelValueItem item : thingsModelList) {
            AiSemanticFieldVO field = buildField(question, normalizedQuestion, normalizedMetricQuestion, deviceMetaData, item);
            if (field.getMatchScore() != null && field.getMatchScore() > 0) {
                candidates.add(field);
            }
        }
        List<AiSemanticFieldVO> deduplicatedCandidates = deduplicateCandidates(candidates);
        List<AiSemanticFieldVO> prioritizedCandidates = prioritizeSemanticCandidates(normalizedMetricQuestion, deduplicatedCandidates);
        return prioritizedCandidates.stream()
                .sorted(Comparator.comparing(AiSemanticFieldVO::getMatchScore, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(AiSemanticFieldVO::getSemanticName, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(AiSemanticFieldVO::getSourceCode, Comparator.nullsLast(String::compareToIgnoreCase)))
                .limit(Math.max(6, fastBeeAiProperties.getNl2sql().getMaxThingModelSemanticMatches()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private AiSemanticFieldVO buildExplicitIdentifierField(String question,
                                                           String normalizedQuestion,
                                                           String normalizedMetricQuestion,
                                                           DeviceMetaData deviceMetaData,
                                                           List<ThingsModelValueItem> thingsModelList,
                                                           String explicitIdentifier) {
        ThingsModelValueItem selectedItem = findThingModelByIdentifier(thingsModelList, explicitIdentifier);
        if (selectedItem == null) {
            return null;
        }
        AiSemanticFieldVO field = buildField(question, normalizedQuestion, normalizedMetricQuestion, deviceMetaData, selectedItem);
        Integer currentScore = field.getMatchScore();
        field.setMatchScore(currentScore == null ? 1000 : Math.max(currentScore, 1000));
        return field;
    }

    private ThingsModelValueItem findThingModelByIdentifier(List<ThingsModelValueItem> thingsModelList, String explicitIdentifier) {
        if (thingsModelList == null || thingsModelList.isEmpty() || StringUtils.isBlank(explicitIdentifier)) {
            return null;
        }
        for (ThingsModelValueItem item : thingsModelList) {
            if (item == null || StringUtils.isBlank(item.getId())) {
                continue;
            }
            if (item.getId().trim().equalsIgnoreCase(explicitIdentifier.trim())) {
                return item;
            }
        }
        return null;
    }

    /**
     * 运行时物模型语义优先按唯一设备解析；若问句命中多个同产品设备，则退化为“按唯一产品解析物模型”。
     * 这样像“查询智能开关产品 当前温度是多少”这类产品级问法也能先命中温度语义，再在执行阶段要求用户确认具体设备。
     */
    private DeviceMetaData resolveRuntimeDeviceMetaData(String question) {
        DeviceMetaData directMetaData = aiDeviceResolveService.resolveOptionalDeviceMetaData(question);
        if (directMetaData != null && directMetaData.getProduct() != null) {
            return directMetaData;
        }
        List<DeviceShortOutput> candidates = aiDeviceResolveService.listDeviceCandidates(question);
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        DeviceShortOutput representative = resolveUniqueProductCandidate(candidates);
        if (representative == null || StringUtils.isBlank(representative.getSerialNumber())) {
            return null;
        }
        DeviceMetaData candidateMetaData = aiDeviceMetadataService.getOptionalDeviceMetaData(representative.getSerialNumber());
        return candidateMetaData != null && candidateMetaData.getProduct() != null ? candidateMetaData : null;
    }

    private DeviceShortOutput resolveUniqueProductCandidate(List<DeviceShortOutput> candidates) {
        Long uniqueProductId = null;
        String uniqueProductName = null;
        DeviceShortOutput representative = null;
        for (DeviceShortOutput candidate : candidates) {
            if (candidate == null || StringUtils.isBlank(candidate.getSerialNumber())) {
                continue;
            }
            if (representative == null) {
                representative = candidate;
                uniqueProductId = candidate.getProductId();
                uniqueProductName = normalizeText(candidate.getProductName());
                continue;
            }
            if (!Objects.equals(uniqueProductId, candidate.getProductId())) {
                return null;
            }
            if (!StringUtils.equals(uniqueProductName, normalizeText(candidate.getProductName()))) {
                return null;
            }
        }
        return representative;
    }

    private List<ThingsModelValueItem> loadThingsModels(Long productId) {
        if (productId == null) {
            return new ArrayList<>();
        }
        try {
            List<ThingsModelValueItem> items = itslCache.getThingsModelList(productId);
            return items == null ? new ArrayList<>() : items;
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }

    private AiSemanticFieldVO buildField(String question,
                                         String normalizedQuestion,
                                         String normalizedMetricQuestion,
                                         DeviceMetaData deviceMetaData,
                                         ThingsModelValueItem item) {
        Product product = deviceMetaData.getProduct();
        Device device = deviceMetaData.getDevice();
        String identifier = trimToEmpty(item.getId());
        String semanticName = resolveSemanticName(item);

        AiSemanticFieldVO field = new AiSemanticFieldVO();
        field.setKbCode(KB_CODE);
        field.setVersionNo(VERSION_NO);
        field.setTableName(TABLE_NAME);
        field.setColumnName(COLUMN_NAME);
        field.setTableColumnKey("tsl_runtime:" + product.getProductId() + ":" + identifier);
        field.setSemanticName(semanticName);
        field.setSemanticType(resolveSemanticType(item));
        field.setSourceType(SOURCE_TYPE);
        field.setSourceCode(identifier);
        field.setDataSourceType(resolveExecutionRoute(question, item));
        field.setSemanticSource("THING_MODEL_RUNTIME");
        field.setAliases(buildAliases(device, product, item, semanticName, identifier));
        field.setQueryHints(buildQueryHints(device, product, item));
        field.setRelationHints(buildRelationHints(device, product, item));
        field.setValueMappings(buildValueMappings(item.getDatatype()));
        field.setMatchScore(calculateScore(normalizedQuestion, normalizedMetricQuestion, field, device, product));
        return field;
    }

    private String normalizeMetricQuestion(String question, DeviceMetaData deviceMetaData, String normalizedQuestion) {
        String metricQuestion = StringUtils.isNotBlank(normalizedQuestion) ? normalizedQuestion : normalizeText(question);
        if (deviceMetaData != null) {
            Device device = deviceMetaData.getDevice();
            Product product = deviceMetaData.getProduct();
            if (device != null) {
                metricQuestion = removeNormalizedToken(metricQuestion, device.getDeviceName());
                metricQuestion = removeNormalizedToken(metricQuestion, device.getSerialNumber());
            }
            if (product != null) {
                metricQuestion = removeNormalizedToken(metricQuestion, product.getProductName());
            }
        }
        metricQuestion = removeNormalizedToken(metricQuestion, "查询");
        metricQuestion = removeNormalizedToken(metricQuestion, "设备");
        metricQuestion = removeNormalizedToken(metricQuestion, "产品");
        metricQuestion = removeNormalizedToken(metricQuestion, "请问");
        metricQuestion = removeNormalizedToken(metricQuestion, "帮我");
        metricQuestion = removeNormalizedToken(metricQuestion, "一下");
        metricQuestion = removeNormalizedToken(metricQuestion, "是多少");
        metricQuestion = removeNormalizedToken(metricQuestion, "多少");
        metricQuestion = removeNormalizedToken(metricQuestion, "什么");
        metricQuestion = removeNormalizedToken(metricQuestion, "已确认");
        metricQuestion = removeNormalizedToken(metricQuestion, "确认");
        metricQuestion = removeNormalizedToken(metricQuestion, "物模型");
        metricQuestion = removeNormalizedToken(metricQuestion, "指标");
        for (String noiseToken : METRIC_QUESTION_NOISE_TOKENS) {
            metricQuestion = removeNormalizedToken(metricQuestion, noiseToken);
        }
        return StringUtils.isNotBlank(metricQuestion) ? metricQuestion : normalizedQuestion;
    }

    private String removeNormalizedToken(String source, String token) {
        if (StringUtils.isBlank(source) || StringUtils.isBlank(token)) {
            return source;
        }
        String normalizedToken = normalizeText(token);
        if (StringUtils.isBlank(normalizedToken)) {
            return source;
        }
        return source.replace(normalizedToken, "");
    }

    private String resolveSemanticName(ThingsModelValueItem item) {
        if (StringUtils.isNotBlank(item.getName_zh_CN())) {
            return item.getName_zh_CN().trim();
        }
        if (StringUtils.isNotBlank(item.getName())) {
            return item.getName().trim();
        }
        if (StringUtils.isNotBlank(item.getName_en_US())) {
            return item.getName_en_US().trim();
        }
        return trimToEmpty(item.getId());
    }

    private String resolveSemanticType(ThingsModelValueItem item) {
        Datatype datatype = item.getDatatype();
        if (datatype != null) {
            if (datatype.getEnumList() != null && !datatype.getEnumList().isEmpty()) {
                return "ENUM";
            }
            if (StringUtils.isNotBlank(datatype.getTrueText()) || StringUtils.isNotBlank(datatype.getFalseText())) {
                return "ENUM";
            }
        }
        return "DIMENSION";
    }

    private String resolveExecutionRoute(String question, ThingsModelValueItem item) {
        boolean historyQuery = containsAny(question, "历史", "最近", "当天", "今天", "昨日", "昨天",
                "趋势", "统计", "总计", "总和", "平均", "最大", "最小");
        boolean currentQuery = containsAny(question, "当前", "实时", "现在", "此刻", "最新")
                || isImplicitCurrentValueQuestion(question, historyQuery);
        if (currentQuery && supportsCurrentValue(item)) {
            return ROUTE_REDIS_VALUE;
        }
        if (historyQuery) {
            if (supportsHistory(item)) {
                return ROUTE_TSDB_QUERY;
            }
            return ROUTE_HYBRID_PIPELINE;
        }
        return ROUTE_AUTO;
    }

    private boolean isImplicitCurrentValueQuestion(String question, boolean historyQuery) {
        if (historyQuery) {
            return false;
        }
        return containsAny(question, "数值是多少", "值是多少", "是多少", "多少");
    }

    private boolean supportsCurrentValue(ThingsModelValueItem item) {
        return !Objects.equals(ThingsModelTypeEnum.EVENT.getCode(), item.getType());
    }

    private boolean supportsHistory(ThingsModelValueItem item) {
        return Integer.valueOf(1).equals(item.getIsHistory());
    }

    private List<String> buildAliases(Device device, Product product, ThingsModelValueItem item, String semanticName,
                                      String identifier) {
        LinkedHashSet<String> aliases = new LinkedHashSet<>();
        addAliasVariants(aliases, semanticName);
        addAliasVariants(aliases, item.getName());
        addAliasVariants(aliases, item.getName_zh_CN());
        addAliasVariants(aliases, item.getName_en_US());
        addAliasVariants(aliases, identifier);
        if (device != null) {
            addAliasVariants(aliases, device.getDeviceName());
            addAliasVariants(aliases, device.getSerialNumber());
        }
        if (product != null) {
            addAliasVariants(aliases, product.getProductName());
        }
        addAliasVariants(aliases, resolveModelTypeText(item.getType()));
        Datatype datatype = item.getDatatype();
        if (datatype != null) {
            addAliasVariants(aliases, datatype.getUnit());
        }
        return new ArrayList<>(aliases);
    }

    private List<String> buildQueryHints(Device device, Product product, ThingsModelValueItem item) {
        List<String> hints = new ArrayList<>();
        hints.add("runtime-tsl");
        if (device != null) {
            hints.add("device=" + trimToEmpty(device.getDeviceName()));
            hints.add("serialNumber=" + trimToEmpty(device.getSerialNumber()));
        }
        if (product != null) {
            hints.add("product=" + trimToEmpty(product.getProductName()));
        }
        hints.add("identifier=" + trimToEmpty(item.getId()));
        hints.add("model-type=" + resolveModelTypeText(item.getType()));
        hints.add("readonly=" + (Integer.valueOf(1).equals(item.getIsReadonly()) ? "1" : "0"));
        hints.add("history=" + (Integer.valueOf(1).equals(item.getIsHistory()) ? "1" : "0"));
        hints.add("current-route=" + (supportsCurrentValue(item) ? ROUTE_REDIS_VALUE : ROUTE_HYBRID_PIPELINE));
        hints.add("history-route=" + (supportsHistory(item) ? ROUTE_TSDB_QUERY : ROUTE_HYBRID_PIPELINE));
        Datatype datatype = item.getDatatype();
        if (datatype != null && StringUtils.isNotBlank(datatype.getUnit())) {
            hints.add("unit=" + datatype.getUnit().trim());
        }
        return hints;
    }

    private List<String> buildRelationHints(Device device, Product product, ThingsModelValueItem item) {
        List<String> relationHints = new ArrayList<>();
        if (device != null) {
            relationHints.add("已通过设备解析定位设备：" + trimToEmpty(device.getSerialNumber()));
        }
        relationHints.add("iot_device.product_id=iot_product.product_id");
        relationHints.add("iot_product.product_id=iot_things_model.product_id");
        if (product != null) {
            relationHints.add("已限定产品：" + trimToEmpty(product.getProductName()) + "，identifier=" + trimToEmpty(item.getId()));
        }
        relationHints.add("运行时根据设备所属产品自动解析物模型 identifier");
        return relationHints;
    }

    private List<AiSemanticValueMappingVO> buildValueMappings(Datatype datatype) {
        List<AiSemanticValueMappingVO> mappings = new ArrayList<>();
        if (datatype == null) {
            return mappings;
        }
        if (datatype.getEnumList() != null) {
            for (EnumItem enumItem : datatype.getEnumList()) {
                if (enumItem == null) {
                    continue;
                }
                AiSemanticValueMappingVO mapping = new AiSemanticValueMappingVO();
                mapping.setLabel(trimToEmpty(enumItem.getText()));
                mapping.setValue(trimToEmpty(enumItem.getValue()));
                mappings.add(mapping);
            }
        }
        if (StringUtils.isNotBlank(datatype.getFalseText()) || StringUtils.isNotBlank(datatype.getTrueText())) {
            AiSemanticValueMappingVO falseMapping = new AiSemanticValueMappingVO();
            falseMapping.setLabel(defaultIfBlank(datatype.getFalseText(), "关闭"));
            falseMapping.setValue("false");
            mappings.add(falseMapping);

            AiSemanticValueMappingVO trueMapping = new AiSemanticValueMappingVO();
            trueMapping.setLabel(defaultIfBlank(datatype.getTrueText(), "开启"));
            trueMapping.setValue("true");
            mappings.add(trueMapping);
        }
        return mappings;
    }

    private Integer calculateScore(String normalizedQuestion,
                                   String normalizedMetricQuestion,
                                   AiSemanticFieldVO field,
                                   Device device,
                                   Product product) {
        int score = 0;
        String semanticQuestion = StringUtils.isNotBlank(normalizedMetricQuestion) ? normalizedMetricQuestion : normalizedQuestion;
        Set<String> questionMetricKeywords = extractCoreMetricKeywords(semanticQuestion);
        Set<String> fieldMetricKeywords = extractFieldMetricKeywords(field);
        score = Math.max(score, matchScore(semanticQuestion, field.getSemanticName(), 80));
        score = Math.max(score, matchScore(semanticQuestion, field.getSourceCode(), 60));
        score = Math.max(score, matchScore(semanticQuestion, resolveModelTypeTextFromField(field), 40));

        for (String alias : safeList(field.getAliases())) {
            score = Math.max(score, matchScore(semanticQuestion, alias, 70));
        }
        for (AiSemanticValueMappingVO mapping : safeValueMappings(field.getValueMappings())) {
            score = Math.max(score, matchScore(semanticQuestion, mapping.getLabel(), 35));
        }

        if (device != null) {
            score += containsNormalized(normalizedQuestion, device.getSerialNumber()) ? 12 : 0;
            score += containsNormalized(normalizedQuestion, device.getDeviceName()) ? 18 : 0;
        }
        if (product != null) {
            score += containsNormalized(normalizedQuestion, product.getProductName()) ? 12 : 0;
        }
        if (ROUTE_REDIS_VALUE.equals(field.getDataSourceType()) && containsAny(semanticQuestion, "当前", "实时", "现在", "此刻")) {
            score += 10;
        }
        if (ROUTE_TSDB_QUERY.equals(field.getDataSourceType()) && containsAny(semanticQuestion, "历史", "趋势", "统计", "当天", "今天", "昨日", "昨天", "总计", "平均", "最大", "最小")) {
            score += 10;
        }
        score += scoreByCoreMetricKeywords(questionMetricKeywords, fieldMetricKeywords);
        if (!questionMetricKeywords.isEmpty() && !hasMetricKeywordOverlap(questionMetricKeywords, fieldMetricKeywords)) {
            score -= CORE_METRIC_MISS_PENALTY;
        }
        return Math.max(score, 0);
    }

    private List<AiSemanticFieldVO> deduplicateCandidates(List<AiSemanticFieldVO> candidates) {
        Map<String, AiSemanticFieldVO> deduplicated = new LinkedHashMap<>();
        for (AiSemanticFieldVO candidate : candidates) {
            if (candidate == null || StringUtils.isBlank(candidate.getTableColumnKey())) {
                continue;
            }
            AiSemanticFieldVO previous = deduplicated.get(candidate.getTableColumnKey());
            if (previous == null || compareScore(candidate, previous) > 0) {
                deduplicated.put(candidate.getTableColumnKey(), candidate);
            }
        }
        return new ArrayList<>(deduplicated.values());
    }

    private List<AiSemanticFieldVO> prioritizeSemanticCandidates(String normalizedQuestion, List<AiSemanticFieldVO> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return new ArrayList<>();
        }
        List<AiSemanticFieldVO> prioritizedCandidates = new ArrayList<>(candidates);
        String metricFocusText = extractMetricFocusText(normalizedQuestion);
        if (StringUtils.isNotBlank(metricFocusText)) {
            List<AiSemanticFieldVO> exactTextCandidates = filterCandidatesByText(prioritizedCandidates, metricFocusText, true);
            if (!exactTextCandidates.isEmpty()) {
                prioritizedCandidates = exactTextCandidates;
            } else {
                List<AiSemanticFieldVO> fuzzyTextCandidates = filterCandidatesByText(prioritizedCandidates, metricFocusText, false);
                if (!fuzzyTextCandidates.isEmpty()) {
                    prioritizedCandidates = fuzzyTextCandidates;
                }
            }
        }
        Set<String> questionMetricKeywords = extractCoreMetricKeywords(StringUtils.isNotBlank(metricFocusText) ? metricFocusText : normalizedQuestion);
        if (!questionMetricKeywords.isEmpty()) {
            List<AiSemanticFieldVO> exactCandidates = filterCandidatesByMetricKeyword(prioritizedCandidates, questionMetricKeywords, true);
            if (!exactCandidates.isEmpty()) {
                prioritizedCandidates = exactCandidates;
            } else {
                List<AiSemanticFieldVO> fuzzyCandidates = filterCandidatesByMetricKeyword(prioritizedCandidates, questionMetricKeywords, false);
                if (!fuzzyCandidates.isEmpty()) {
                    prioritizedCandidates = fuzzyCandidates;
                }
            }
        }
        return refineCandidatesByQueryIntent(normalizedQuestion, prioritizedCandidates);
    }

    private List<AiSemanticFieldVO> refineCandidatesByQueryIntent(String normalizedQuestion, List<AiSemanticFieldVO> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return new ArrayList<>();
        }
        boolean currentQuery = containsAny(normalizedQuestion, "当前", "实时", "现在", "此刻", "最新");
        boolean historyQuery = containsAny(normalizedQuestion, "历史", "最近", "当天", "今天", "昨天", "昨日",
                "趋势", "统计", "总计", "总和", "平均", "最大", "最小");
        if (!currentQuery && !historyQuery) {
            return candidates;
        }

        List<AiSemanticFieldVO> propertyCandidates = filterCandidatesByHint(candidates, "model-type", "属性");
        if (propertyCandidates.isEmpty()) {
            return candidates;
        }
        if (currentQuery) {
            List<AiSemanticFieldVO> readonlyCandidates = filterCandidatesByHint(propertyCandidates, "readonly", "1");
            return readonlyCandidates.isEmpty() ? propertyCandidates : readonlyCandidates;
        }
        List<AiSemanticFieldVO> historyCandidates = filterCandidatesByHint(propertyCandidates, "history", "1");
        if (!historyCandidates.isEmpty()) {
            return historyCandidates;
        }
        List<AiSemanticFieldVO> readonlyCandidates = filterCandidatesByHint(propertyCandidates, "readonly", "1");
        return readonlyCandidates.isEmpty() ? propertyCandidates : readonlyCandidates;
    }

    private List<AiSemanticFieldVO> filterCandidatesByHint(List<AiSemanticFieldVO> candidates, String key, String expectedValue) {
        List<AiSemanticFieldVO> filtered = new ArrayList<>();
        if (candidates == null || candidates.isEmpty() || StringUtils.isBlank(key) || StringUtils.isBlank(expectedValue)) {
            return filtered;
        }
        for (AiSemanticFieldVO candidate : candidates) {
            if (expectedValue.equals(extractQueryHintValue(candidate, key))) {
                filtered.add(candidate);
            }
        }
        return filtered;
    }

    private String extractMetricFocusText(String normalizedQuestion) {
        if (StringUtils.isBlank(normalizedQuestion)) {
            return "";
        }
        String focusText = normalizedQuestion;
        int intentIndex = locateLastIntentIndex(focusText);
        if (intentIndex >= 0 && intentIndex < focusText.length()) {
            String tail = focusText.substring(intentIndex);
            String cleanedTail = removeIntentTokens(tail);
            if (StringUtils.isNotBlank(cleanedTail)) {
                focusText = cleanedTail;
            }
        }
        focusText = removeNormalizedToken(focusText, "已确认");
        focusText = removeNormalizedToken(focusText, "确认");
        focusText = removeNormalizedToken(focusText, "物模型");
        focusText = removeNormalizedToken(focusText, "指标");
        return focusText;
    }

    private int locateLastIntentIndex(String normalizedQuestion) {
        if (StringUtils.isBlank(normalizedQuestion)) {
            return -1;
        }
        int result = -1;
        for (String token : METRIC_INTENT_TOKENS) {
            String normalizedToken = normalizeText(token);
            if (StringUtils.isBlank(normalizedToken)) {
                continue;
            }
            int index = normalizedQuestion.lastIndexOf(normalizedToken);
            if (index > result) {
                result = index;
            }
        }
        return result;
    }

    private String removeIntentTokens(String source) {
        String result = source;
        for (String token : METRIC_INTENT_TOKENS) {
            result = removeNormalizedToken(result, token);
        }
        return result;
    }

    private List<AiSemanticFieldVO> filterCandidatesByText(List<AiSemanticFieldVO> candidates, String focusText, boolean exactMatch) {
        List<AiSemanticFieldVO> filtered = new ArrayList<>();
        if (candidates == null || candidates.isEmpty() || StringUtils.isBlank(focusText)) {
            return filtered;
        }
        for (AiSemanticFieldVO candidate : candidates) {
            if (matchesSemanticText(candidate, focusText, exactMatch)) {
                filtered.add(candidate);
            }
        }
        return filtered;
    }

    private List<AiSemanticFieldVO> filterCandidatesByMetricKeyword(List<AiSemanticFieldVO> candidates,
                                                                    Collection<String> questionMetricKeywords,
                                                                    boolean exactMatch) {
        List<AiSemanticFieldVO> filtered = new ArrayList<>();
        if (candidates == null || candidates.isEmpty() || questionMetricKeywords == null || questionMetricKeywords.isEmpty()) {
            return filtered;
        }
        for (AiSemanticFieldVO candidate : candidates) {
            if (matchesMetricKeyword(candidate, questionMetricKeywords, exactMatch)) {
                filtered.add(candidate);
            }
        }
        return filtered;
    }

    private int compareScore(AiSemanticFieldVO left, AiSemanticFieldVO right) {
        Integer leftScore = left == null ? null : left.getMatchScore();
        Integer rightScore = right == null ? null : right.getMatchScore();
        return Comparator.<Integer>nullsLast(Integer::compareTo).compare(leftScore, rightScore);
    }

    private int matchScore(String normalizedQuestion, String candidate, int hitScore) {
        if (StringUtils.isBlank(candidate)) {
            return 0;
        }
        String normalizedCandidate = normalizeText(candidate);
        if (normalizedCandidate.isEmpty()) {
            return 0;
        }
        if (normalizedQuestion.equals(normalizedCandidate)) {
            return hitScore + 12;
        }
        if (normalizedQuestion.contains(normalizedCandidate)) {
            return hitScore;
        }
        if (normalizedCandidate.length() >= 2 && normalizedCandidate.contains(normalizedQuestion)) {
            return Math.max(12, hitScore - 18);
        }
        int keywordScore = scoreByKeywordOverlap(normalizedQuestion, normalizedCandidate, hitScore);
        return Math.max(0, keywordScore);
    }

    private boolean containsNormalized(String normalizedQuestion, String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        String normalizedValue = normalizeText(value);
        return normalizedValue.length() >= 2 && normalizedQuestion.contains(normalizedValue);
    }

    private boolean matchesMetricKeyword(AiSemanticFieldVO field, Collection<String> metricKeywords, boolean exactMatch) {
        if (field == null || metricKeywords == null || metricKeywords.isEmpty()) {
            return false;
        }
        for (String metricKeyword : metricKeywords) {
            if (matchesSemanticFieldMetricKeyword(field, metricKeyword, exactMatch)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesSemanticText(AiSemanticFieldVO field, String focusText, boolean exactMatch) {
        if (field == null || StringUtils.isBlank(focusText)) {
            return false;
        }
        for (String value : collectSemanticMatchValues(field)) {
            if (matchesFocusValue(value, focusText, exactMatch)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesAliasMetricKeyword(Collection<String> aliases, String metricKeyword) {
        return matchesAliasMetricKeyword(aliases, metricKeyword, false);
    }

    private boolean matchesAliasMetricKeyword(Collection<String> aliases, String metricKeyword, boolean exactMatch) {
        if (aliases == null || aliases.isEmpty()) {
            return false;
        }
        for (String alias : aliases) {
            if (matchesMetricKeywordValue(alias, metricKeyword, exactMatch)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesMetricKeywordValue(String value, String metricKeyword, boolean exactMatch) {
        if (StringUtils.isBlank(value) || StringUtils.isBlank(metricKeyword)) {
            return false;
        }
        String normalizedValue = normalizeText(value);
        if (normalizedValue.length() < 2) {
            return false;
        }
        return exactMatch ? normalizedValue.equals(metricKeyword) : normalizedValue.contains(metricKeyword);
    }

    private boolean matchesFocusValue(String value, String focusText, boolean exactMatch) {
        if (StringUtils.isBlank(value) || StringUtils.isBlank(focusText)) {
            return false;
        }
        String normalizedValue = normalizeText(value);
        if (normalizedValue.length() < 2) {
            return false;
        }
        return exactMatch
                ? normalizedValue.equals(focusText)
                : normalizedValue.contains(focusText) || focusText.contains(normalizedValue);
    }

    private boolean matchesSemanticFieldMetricKeyword(AiSemanticFieldVO field, String metricKeyword, boolean exactMatch) {
        if (field == null || StringUtils.isBlank(metricKeyword)) {
            return false;
        }
        for (String value : collectSemanticMatchValues(field)) {
            if (matchesMetricKeywordValue(value, metricKeyword, exactMatch)) {
                return true;
            }
        }
        return false;
    }

    private List<String> collectSemanticMatchValues(AiSemanticFieldVO field) {
        LinkedHashSet<String> values = new LinkedHashSet<>();
        if (field == null) {
            return new ArrayList<>();
        }
        addSemanticMatchValue(values, field.getSemanticName());
        addSemanticMatchValue(values, field.getSourceCode());
        for (String alias : filterSemanticAliases(field, field.getAliases())) {
            addSemanticMatchValue(values, alias);
        }
        return new ArrayList<>(values);
    }

    private void addSemanticMatchValue(Set<String> values, String value) {
        if (values == null || StringUtils.isBlank(value)) {
            return;
        }
        values.add(value.trim());
    }

    private List<String> filterSemanticAliases(AiSemanticFieldVO field, Collection<String> aliases) {
        List<String> semanticAliases = new ArrayList<>();
        if (aliases == null || aliases.isEmpty()) {
            return semanticAliases;
        }
        String normalizedDevice = normalizeText(extractQueryHintValue(field, "device"));
        String normalizedSerial = normalizeText(extractQueryHintValue(field, "serialNumber"));
        String normalizedProduct = normalizeText(extractQueryHintValue(field, "product"));
        String normalizedModelType = normalizeText(extractQueryHintValue(field, "model-type"));
        String normalizedUnit = normalizeText(extractQueryHintValue(field, "unit"));
        for (String alias : aliases) {
            if (StringUtils.isBlank(alias)) {
                continue;
            }
            String normalizedAlias = normalizeText(alias);
            if (normalizedAlias.length() < 2) {
                continue;
            }
            if (normalizedAlias.equals(normalizedDevice)
                    || normalizedAlias.equals(normalizedSerial)
                    || normalizedAlias.equals(normalizedProduct)
                    || normalizedAlias.equals(normalizedModelType)
                    || normalizedAlias.equals(normalizedUnit)) {
                continue;
            }
            semanticAliases.add(alias.trim());
        }
        return semanticAliases;
    }

    private String extractQueryHintValue(AiSemanticFieldVO field, String key) {
        if (field == null || field.getQueryHints() == null) {
            return null;
        }
        for (String hint : field.getQueryHints()) {
            if (StringUtils.isBlank(hint) || StringUtils.isBlank(key)) {
                continue;
            }
            String prefix = key + "=";
            if (hint.startsWith(prefix)) {
                return hint.substring(prefix.length());
            }
        }
        return null;
    }

    private String resolveModelTypeTextFromField(AiSemanticFieldVO field) {
        return extractQueryHintValue(field, "model-type");
    }

    private String resolveModelTypeText(Integer type) {
        if (type == null) {
            return null;
        }
        if (Objects.equals(ThingsModelTypeEnum.PROPERTY.getCode(), type)) {
            return "属性";
        }
        if (Objects.equals(ThingsModelTypeEnum.FUNCTION.getCode(), type)) {
            return "功能";
        }
        if (Objects.equals(ThingsModelTypeEnum.EVENT.getCode(), type)) {
            return "事件";
        }
        return null;
    }

    private void addAlias(Set<String> aliases, String alias) {
        if (aliases == null || StringUtils.isBlank(alias)) {
            return;
        }
        aliases.add(alias.trim());
    }

    /**
     * 给物模型名称补充运行时派生别名，解决“温度 -> 空气温度”“档位 -> 运行档位”这类简称问法。
     */
    private void addAliasVariants(Set<String> aliases, String rawAlias) {
        if (aliases == null || StringUtils.isBlank(rawAlias)) {
            return;
        }
        LinkedHashSet<String> variants = new LinkedHashSet<>();
        String alias = rawAlias.trim();
        variants.add(alias);
        variants.addAll(splitAliasParts(alias));
        addSequenceAlias(variants, alias);

        String simplifiedAlias = simplifyAlias(alias);
        if (StringUtils.isNotBlank(simplifiedAlias)) {
            variants.add(simplifiedAlias);
            variants.addAll(splitAliasParts(simplifiedAlias));
            addSequenceAlias(variants, simplifiedAlias);
        }

        for (String variant : new ArrayList<>(variants)) {
            variants.addAll(extractCoreMetricAliases(variant));
        }

        for (String variant : variants) {
            addAlias(aliases, variant);
        }
    }

    private void addSequenceAlias(Set<String> aliases, String alias) {
        if (aliases == null || StringUtils.isBlank(alias)) {
            return;
        }
        String sequenceAlias = stripSequenceSuffix(alias);
        if (StringUtils.isNotBlank(sequenceAlias)) {
            aliases.add(sequenceAlias);
            aliases.addAll(splitAliasParts(sequenceAlias));
        }
    }

    private List<String> splitAliasParts(String alias) {
        if (StringUtils.isBlank(alias)) {
            return new ArrayList<>();
        }
        String[] parts = alias.trim().split("[\\s\\-_/（）()【】\\[\\]、，,；;:：]+");
        LinkedHashSet<String> result = new LinkedHashSet<>();
        for (String part : parts) {
            if (StringUtils.isNotBlank(part) && part.trim().length() >= 2) {
                result.add(part.trim());
            }
        }
        return new ArrayList<>(result);
    }

    private String simplifyAlias(String alias) {
        if (StringUtils.isBlank(alias)) {
            return "";
        }
        String simplified = alias.trim()
                .replaceAll("（[^）]*）", "")
                .replaceAll("\\([^)]*\\)", "")
                .replaceAll("【[^】]*】", "")
                .replaceAll("\\[[^\\]]*\\]", "");
        for (String token : SEMANTIC_DECORATION_TOKENS) {
            if (simplified.length() > token.length() + 1) {
                simplified = simplified.replace(token, "");
            }
        }
        simplified = simplified.replaceAll("[\\s\\-_/（）()【】\\[\\]、，,；;:：]+", "");
        if (simplified.length() < 2) {
            return "";
        }
        return simplified;
    }

    private String stripSequenceSuffix(String alias) {
        if (StringUtils.isBlank(alias)) {
            return "";
        }
        String stripped = alias.trim().replaceAll("([A-Za-z\\u4e00-\\u9fa5]+?)(?:[-_\\s]?[0-9]+)$", "$1");
        if (StringUtils.isBlank(stripped)) {
            return "";
        }
        stripped = stripped.trim();
        return stripped.length() >= 2 && !stripped.equals(alias.trim()) ? stripped : "";
    }

    private List<String> extractCoreMetricAliases(String alias) {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        if (StringUtils.isBlank(alias)) {
            return new ArrayList<>();
        }
        String normalizedAlias = normalizeText(alias);
        for (String keyword : CORE_METRIC_KEYWORDS) {
            String normalizedKeyword = normalizeText(keyword);
            if (normalizedKeyword.length() >= 2 && normalizedAlias.contains(normalizedKeyword)) {
                result.add(keyword);
            }
        }
        return new ArrayList<>(result);
    }

    private boolean containsAny(String text, String... keywords) {
        if (StringUtils.isBlank(text) || keywords == null) {
            return false;
        }
        String source = normalizeText(text);
        for (String keyword : keywords) {
            if (StringUtils.isNotBlank(keyword) && source.contains(normalizeText(keyword))) {
                return true;
            }
        }
        return false;
    }

    private int scoreByKeywordOverlap(String normalizedQuestion, String normalizedCandidate, int hitScore) {
        Set<String> questionKeywords = splitNormalizedKeywords(normalizedQuestion);
        Set<String> candidateKeywords = splitNormalizedKeywords(normalizedCandidate);
        if (questionKeywords.isEmpty() || candidateKeywords.isEmpty()) {
            return 0;
        }
        int matched = 0;
        for (String keyword : candidateKeywords) {
            if (questionKeywords.contains(keyword)) {
                matched++;
            }
        }
        if (matched <= 0) {
            return 0;
        }
        return Math.max(10, matched * Math.max(6, hitScore / 4));
    }

    private int scoreByCoreMetricKeywords(Set<String> questionMetricKeywords, Set<String> fieldMetricKeywords) {
        if (questionMetricKeywords.isEmpty() || fieldMetricKeywords.isEmpty()) {
            return 0;
        }
        int matched = 0;
        for (String keyword : fieldMetricKeywords) {
            if (questionMetricKeywords.contains(keyword)) {
                matched++;
            }
        }
        return matched <= 0 ? 0 : CORE_METRIC_MATCH_BONUS + matched * 12;
    }

    private boolean hasMetricKeywordOverlap(Set<String> questionMetricKeywords, Set<String> fieldMetricKeywords) {
        if (questionMetricKeywords.isEmpty() || fieldMetricKeywords.isEmpty()) {
            return false;
        }
        for (String keyword : fieldMetricKeywords) {
            if (questionMetricKeywords.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private Set<String> extractCoreMetricKeywords(String normalizedText) {
        LinkedHashSet<String> keywords = new LinkedHashSet<>();
        if (StringUtils.isBlank(normalizedText)) {
            return keywords;
        }
        for (String keyword : CORE_METRIC_KEYWORDS) {
            String normalizedKeyword = normalizeText(keyword);
            if (normalizedKeyword.length() >= 2 && normalizedText.contains(normalizedKeyword)) {
                keywords.add(normalizedKeyword);
            }
        }
        return keywords;
    }

    private Set<String> extractFieldMetricKeywords(AiSemanticFieldVO field) {
        LinkedHashSet<String> keywords = new LinkedHashSet<>();
        if (field == null) {
            return keywords;
        }
        appendMetricKeywords(keywords, field.getSemanticName());
        appendMetricKeywords(keywords, field.getSourceCode());
        for (String alias : safeList(field.getAliases())) {
            appendMetricKeywords(keywords, alias);
        }
        return keywords;
    }

    private void appendMetricKeywords(Set<String> keywords, String value) {
        if (keywords == null || StringUtils.isBlank(value)) {
            return;
        }
        String normalizedValue = normalizeText(value);
        if (StringUtils.isBlank(normalizedValue)) {
            return;
        }
        for (String keyword : CORE_METRIC_KEYWORDS) {
            String normalizedKeyword = normalizeText(keyword);
            if (normalizedKeyword.length() >= 2 && normalizedValue.contains(normalizedKeyword)) {
                keywords.add(normalizedKeyword);
            }
        }
    }

    private Set<String> splitNormalizedKeywords(String normalizedText) {
        LinkedHashSet<String> keywords = new LinkedHashSet<>();
        if (StringUtils.isBlank(normalizedText)) {
            return keywords;
        }
        for (String keyword : CORE_METRIC_KEYWORDS) {
            String normalizedKeyword = normalizeText(keyword);
            if (normalizedKeyword.length() >= 2 && normalizedText.contains(normalizedKeyword)) {
                keywords.add(normalizedKeyword);
            }
        }
        if (keywords.isEmpty() && normalizedText.length() >= 2) {
            keywords.add(normalizedText);
        }
        return keywords;
    }

    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        return text.trim()
                .replaceAll("[\\s\\-_.()（）【】\\[\\]{}｛｝,，。；;:：？?\"'`“”‘’]+", "")
                .toLowerCase(Locale.ROOT);
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value.trim();
    }

    private List<String> safeList(Collection<String> values) {
        return values == null ? new ArrayList<>() : new ArrayList<>(values);
    }

    private List<AiSemanticValueMappingVO> safeValueMappings(Collection<AiSemanticValueMappingVO> values) {
        return values == null ? new ArrayList<>() : new ArrayList<>(values);
    }
}
