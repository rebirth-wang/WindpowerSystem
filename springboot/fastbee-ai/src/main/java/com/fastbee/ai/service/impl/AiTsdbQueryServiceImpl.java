package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.model.enums.AiQueryMode;
import com.fastbee.ai.model.vo.AiQueryGovernanceSnapshotVO;
import com.fastbee.ai.model.vo.AiQueryRouteVO;
import com.fastbee.ai.model.vo.AiSemanticContextVO;
import com.fastbee.ai.model.vo.AiSemanticFieldVO;
import com.fastbee.ai.model.vo.AiTsdbHistoryPointVO;
import com.fastbee.ai.model.vo.AiTsdbQueryResultVO;
import com.fastbee.ai.service.IAiDeviceResolveService;
import com.fastbee.ai.service.IAiQueryGovernanceService;
import com.fastbee.ai.service.IAiQuerySourceRouter;
import com.fastbee.ai.service.IAiSemanticNormalizationService;
import com.fastbee.ai.service.IAiTsdbQueryService;
import com.fastbee.ai.support.AiRuntimeFieldSelectionSupport;
import com.fastbee.common.enums.DeviceLogTypeEnum;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.enums.scenemodel.SceneModelTagOpreationEnum;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceLog;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.HistoryBo;
import com.fastbee.iot.model.HistoryModel;
import com.fastbee.iot.model.ThingsModelItem.Datatype;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.iot.model.vo.FunctionLogVO;
import com.fastbee.iot.service.IFunctionLogService;
import com.fastbee.iot.service.IThingsModelService;
import com.fastbee.iot.tsdb.service.ILogService;

@Service
public class AiTsdbQueryServiceImpl implements IAiTsdbQueryService {

    private static final String QUERY_TYPE_LATEST = "LATEST";
    private static final String QUERY_TYPE_HISTORY = "HISTORY";
    private static final String QUERY_TYPE_AGGREGATE = "AGGREGATE";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Pattern RELATIVE_WINDOW_PATTERN =
            Pattern.compile("(?:最近|近)\\s*([0-9一二三四五六七八九十两]+)\\s*(分钟|小时|天|周|月)");

    @Resource
    private IAiSemanticNormalizationService aiSemanticNormalizationService;

    @Resource
    private IAiQuerySourceRouter aiQuerySourceRouter;

    @Resource
    private IAiDeviceResolveService aiDeviceResolveService;

    @Resource
    private IAiQueryGovernanceService aiQueryGovernanceService;

    @Resource
    private IThingsModelService thingsModelService;

    @Resource
    private ILogService logService;

    @Resource
    private IFunctionLogService functionLogService;

    @Override
    public AiTsdbQueryResultVO query(String question) {
        AiSemanticContextVO context = aiSemanticNormalizationService.buildNl2SqlContext(question);
        AiQueryRouteVO route = aiQuerySourceRouter.route(question, context);
        if (!AiQueryMode.TSDB_QUERY.name().equals(route.getQueryMode())) {
            throw new ServiceException(message("ai.tsdb.query.route.required"));
        }

        DeviceMetaData deviceMetaData = aiDeviceResolveService.resolveRequiredDeviceMetaData(question);
        Device device = deviceMetaData.getDevice();

        AiSemanticFieldVO runtimeField = resolveRuntimeField(question, context);
        String identifier = runtimeField.getSourceCode();
        AiQueryGovernanceSnapshotVO governanceSnapshot =
                aiQueryGovernanceService.validateMultiSourceAccess(deviceMetaData, identifier, AiQueryMode.TSDB_QUERY.name());
        QueryWindow window = resolveWindow(question);
        String queryType = resolveQueryType(question);

        AiTsdbQueryResultVO result = new AiTsdbQueryResultVO();
        result.setQuestion(question);
        result.setQueryMode(AiQueryMode.TSDB_QUERY.name());
        result.setQueryType(queryType);
        result.setDeviceId(governanceSnapshot.getDeviceId());
        result.setSerialNumber(device.getSerialNumber());
        result.setDeviceName(device.getDeviceName());
        result.setProductId(device.getProductId());
        result.setProductName(governanceSnapshot.getProductName());
        result.setSemanticName(runtimeField.getSemanticName());
        result.setIdentifier(identifier);
        result.setBeginTime(window.beginTime);
        result.setEndTime(window.endTime);
        result.setTimeWindowLabel(window.label);
        result.setUnit(resolveUnit(device.getProductId(), identifier));
        result.setAccessValidated(governanceSnapshot.getAccessValidated());
        result.getPermissionChecks().addAll(governanceSnapshot.getPermissionChecks());
        result.getMessages().add(route.getRouteReason());
        result.getMessages().addAll(governanceSnapshot.getPermissionChecks());
        result.getMessages().add("设备与产品信息来自设备元数据缓存。");
        if (window.defaulted) {
            result.getMessages().add("当前问题未显式提供时间范围，系统已自动使用默认时间窗口：" + window.label);
        }

        DeviceLog deviceLog = buildDeviceLog(device, identifier, window);
        boolean functionModel = isFunctionModel(runtimeField, device.getProductId(), identifier);
        if (QUERY_TYPE_LATEST.equals(queryType)) {
            if (functionModel) {
                handleFunctionLatestQuery(result, device, identifier, window);
            } else {
                handleLatestQuery(result, deviceLog);
            }
        } else if (QUERY_TYPE_AGGREGATE.equals(queryType)) {
            if (functionModel) {
                handleFunctionAggregateQuery(result, question, device, identifier, window);
            } else {
                handleAggregateQuery(result, question, deviceLog);
            }
        } else {
            if (functionModel) {
                handleFunctionHistoryQuery(result, device, identifier, window);
            } else {
                handleHistoryQuery(result, deviceLog);
            }
        }
        return result;
    }

    private void handleLatestQuery(AiTsdbQueryResultVO result, DeviceLog deviceLog) {
        DeviceLog lastReport = logService.selectLastReport(deviceLog);
        if (lastReport == null) {
            throw new ServiceException(message("ai.tsdb.query.latest.not.found"));
        }
        result.setLatestValue(lastReport.getLogValue());
        result.setLatestTime(resolveLogTime(lastReport));
        result.setSummary(buildLatestSummary(result));
    }

    private void handleAggregateQuery(AiTsdbQueryResultVO result, String question, DeviceLog deviceLog) {
        Integer operation = resolveStatisticOperation(question);
        deviceLog.setOperation(operation);
        List<String> valueList = logService.selectStatsValue(deviceLog);
        if (valueList == null || valueList.isEmpty()) {
            throw new ServiceException(message("ai.tsdb.query.stat.samples.not.found"));
        }
        result.setStatisticOperation(resolveOperationName(operation));
        result.setStatisticSamples(new ArrayList<>(valueList));
        result.setStatisticValue(calculateStatisticValue(valueList, operation));
        result.setSummary(buildAggregateSummary(result));
    }

    private void handleHistoryQuery(AiTsdbQueryResultVO result, DeviceLog deviceLog) {
        List<HistoryBo> historyList = logService.selectHistorySingleBo(deviceLog);
        if (historyList == null || historyList.isEmpty()) {
            throw new ServiceException(message("ai.tsdb.query.history.not.found"));
        }
        List<AiTsdbHistoryPointVO> points = new ArrayList<>();
        for (HistoryBo historyBo : historyList) {
            if (historyBo == null) {
                continue;
            }
            AiTsdbHistoryPointVO point = new AiTsdbHistoryPointVO();
            point.setTime(historyBo.getTime());
            point.setValue(historyBo.getValue());
            point.setIdentifier(StringUtils.isNotBlank(historyBo.getIdentify()) ? historyBo.getIdentify() : deviceLog.getIdentify());
            points.add(point);
        }
        points.sort(Comparator.comparing(AiTsdbHistoryPointVO::getTime, Comparator.nullsLast(String::compareTo)));
        result.setHistoryPoints(points);
        result.setSummary(buildHistorySummary(result));
    }

    private void handleFunctionLatestQuery(AiTsdbQueryResultVO result, Device device, String identifier, QueryWindow window) {
        List<HistoryModel> historyList = loadFunctionHistory(device, identifier, window);
        HistoryModel latest = firstHistoryModel(historyList);
        if (latest == null) {
            throw new ServiceException(message("ai.tsdb.query.function.latest.not.found"));
        }
        result.setLatestValue(latest.getValue());
        result.setLatestTime(formatDate(latest.getTime()));
        result.getMessages().add("功能类最新值已通过 FunctionLog 查询完成。");
        result.setSummary(buildLatestSummary(result));
    }

    private void handleFunctionAggregateQuery(AiTsdbQueryResultVO result, String question, Device device, String identifier, QueryWindow window) {
        Integer operation = resolveStatisticOperation(question);
        List<HistoryModel> historyList = loadFunctionHistory(device, identifier, window);
        List<String> valueList = collectFunctionHistoryValues(historyList);
        if (valueList.isEmpty()) {
            throw new ServiceException(message("ai.tsdb.query.function.stat.samples.not.found"));
        }
        result.setStatisticOperation(resolveOperationName(operation));
        result.setStatisticSamples(new ArrayList<>(valueList));
        result.setStatisticValue(calculateStatisticValue(valueList, operation));
        result.getMessages().add("功能类统计值已通过 FunctionLog 历史样本计算完成。");
        result.setSummary(buildAggregateSummary(result));
    }

    private void handleFunctionHistoryQuery(AiTsdbQueryResultVO result, Device device, String identifier, QueryWindow window) {
        List<HistoryModel> historyList = loadFunctionHistory(device, identifier, window);
        if (historyList == null || historyList.isEmpty()) {
            throw new ServiceException(message("ai.tsdb.query.function.history.not.found"));
        }
        List<AiTsdbHistoryPointVO> points = new ArrayList<>();
        for (HistoryModel historyModel : historyList) {
            if (historyModel == null) {
                continue;
            }
            AiTsdbHistoryPointVO point = new AiTsdbHistoryPointVO();
            point.setTime(formatDate(historyModel.getTime()));
            point.setValue(historyModel.getValue());
            point.setIdentifier(StringUtils.isNotBlank(historyModel.getIdentify()) ? historyModel.getIdentify() : identifier);
            points.add(point);
        }
        points.sort(Comparator.comparing(AiTsdbHistoryPointVO::getTime, Comparator.nullsLast(String::compareTo)));
        result.setHistoryPoints(points);
        result.getMessages().add("功能类历史值已通过 FunctionLog 查询完成。");
        result.setSummary(buildHistorySummary(result));
    }

    private DeviceLog buildDeviceLog(Device device, String identifier, QueryWindow window) {
        DeviceLog deviceLog = new DeviceLog();
        deviceLog.setDeviceId(device.getDeviceId());
        deviceLog.setDeviceName(device.getDeviceName());
        deviceLog.setSerialNumber(device.getSerialNumber());
        deviceLog.setTenantId(device.getTenantId());
        deviceLog.setIdentify(identifier);
        deviceLog.setLogType(DeviceLogTypeEnum.ATTRIBUTE_REPORT.getType());
        if (StringUtils.isNotBlank(window.beginTime)) {
            deviceLog.setBeginTime(window.beginTime);
        }
        if (StringUtils.isNotBlank(window.endTime)) {
            deviceLog.setEndTime(window.endTime);
        }
        return deviceLog;
    }

    private List<HistoryModel> loadFunctionHistory(Device device, String identifier, QueryWindow window) {
        FunctionLogVO functionLogVO = buildFunctionLogVO(device, identifier, window);
        return functionLogService.listHistory(functionLogVO);
    }

    private FunctionLogVO buildFunctionLogVO(Device device, String identifier, QueryWindow window) {
        FunctionLogVO functionLogVO = new FunctionLogVO();
        functionLogVO.setSerialNumber(device.getSerialNumber());
        functionLogVO.setIdentifyList(List.of(identifier));
        functionLogVO.setBeginTime(parseWindowTime(window.beginTime));
        functionLogVO.setEndTime(parseWindowTime(window.endTime));
        return functionLogVO;
    }

    private Date parseWindowTime(String time) {
        if (StringUtils.isBlank(time)) {
            return null;
        }
        return Date.from(LocalDateTime.parse(time, TIME_FORMATTER).atZone(ZoneId.systemDefault()).toInstant());
    }

    private HistoryModel firstHistoryModel(List<HistoryModel> historyList) {
        if (historyList == null || historyList.isEmpty()) {
            return null;
        }
        for (HistoryModel historyModel : historyList) {
            if (historyModel != null) {
                return historyModel;
            }
        }
        return null;
    }

    private List<String> collectFunctionHistoryValues(List<HistoryModel> historyList) {
        List<String> valueList = new ArrayList<>();
        if (historyList == null || historyList.isEmpty()) {
            return valueList;
        }
        for (HistoryModel historyModel : historyList) {
            if (historyModel == null || StringUtils.isBlank(historyModel.getValue())) {
                continue;
            }
            valueList.add(historyModel.getValue());
        }
        return valueList;
    }

    private AiSemanticFieldVO resolveRuntimeField(String question, AiSemanticContextVO context) {
        List<AiSemanticFieldVO> runtimeFields = context.getRuntimeFields();
        return AiRuntimeFieldSelectionSupport.resolveRuntimeField(question, runtimeFields,
                "未命中可用的运行时物模型语义，无法执行时序问数。", "时序问数");
    }

    private String resolveUnit(Long productId, String identifier) {
        ThingsModelValueItem item = resolveThingModelItem(productId, identifier);
        if (item == null) {
            return null;
        }
        Datatype datatype = item.getDatatype();
        return datatype == null ? null : datatype.getUnit();
    }

    private ThingsModelValueItem resolveThingModelItem(Long productId, String identifier) {
        if (productId == null || StringUtils.isBlank(identifier)) {
            return null;
        }
        try {
            return thingsModelService.getSingleThingModels(productId, identifier);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String resolveQueryType(String question) {
        if (containsAny(question, "最近一次", "最后一次", "最新一次", "最新值", "最新")) {
            return QUERY_TYPE_LATEST;
        }
        if (containsAny(question, "总计", "总和", "累计", "平均", "均值", "最大", "最高", "峰值", "最小", "最低", "统计")) {
            return QUERY_TYPE_AGGREGATE;
        }
        return QUERY_TYPE_HISTORY;
    }

    private Integer resolveStatisticOperation(String question) {
        if (containsAny(question, "平均", "均值")) {
            return SceneModelTagOpreationEnum.AVERAGE_VALUE.getCode();
        }
        if (containsAny(question, "最大", "最高", "峰值")) {
            return SceneModelTagOpreationEnum.MAX_VALUE.getCode();
        }
        if (containsAny(question, "最小", "最低")) {
            return SceneModelTagOpreationEnum.MIN_VALUE.getCode();
        }
        return SceneModelTagOpreationEnum.CUMULATIVE.getCode();
    }

    private String resolveOperationName(Integer operation) {
        SceneModelTagOpreationEnum operationEnum = SceneModelTagOpreationEnum.getByCode(operation);
        if (operationEnum == null) {
            return "累计";
        }
        if (operationEnum == SceneModelTagOpreationEnum.AVERAGE_VALUE) {
            return "平均";
        }
        if (operationEnum == SceneModelTagOpreationEnum.MAX_VALUE) {
            return "最大";
        }
        if (operationEnum == SceneModelTagOpreationEnum.MIN_VALUE) {
            return "最小";
        }
        if (operationEnum == SceneModelTagOpreationEnum.ORIGINAL_VALUE) {
            return "原始值";
        }
        return "累计";
    }

    private String calculateStatisticValue(List<String> valueList, Integer operation) {
        List<BigDecimal> numberList = new ArrayList<>();
        for (String value : valueList) {
            if (StringUtils.isBlank(value)) {
                continue;
            }
            try {
                numberList.add(new BigDecimal(value.trim()));
            } catch (NumberFormatException ignored) {
            }
        }
        if (numberList.isEmpty()) {
            throw new ServiceException(message("ai.query.stat.samples.not.numeric"));
        }
        SceneModelTagOpreationEnum operationEnum = SceneModelTagOpreationEnum.getByCode(operation);
        if (operationEnum == null) {
            operationEnum = SceneModelTagOpreationEnum.CUMULATIVE;
        }
        if (operationEnum == SceneModelTagOpreationEnum.AVERAGE_VALUE) {
            return numberList.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(new BigDecimal(numberList.size()), 4, RoundingMode.HALF_UP)
                    .stripTrailingZeros()
                    .toPlainString();
        }
        if (operationEnum == SceneModelTagOpreationEnum.MAX_VALUE) {
            return numberList.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO).stripTrailingZeros().toPlainString();
        }
        if (operationEnum == SceneModelTagOpreationEnum.MIN_VALUE) {
            return numberList.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO).stripTrailingZeros().toPlainString();
        }
        if (operationEnum == SceneModelTagOpreationEnum.ORIGINAL_VALUE) {
            return numberList.get(numberList.size() - 1).stripTrailingZeros().toPlainString();
        }
        return numberList.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .stripTrailingZeros()
                .toPlainString();
    }

    private String buildLatestSummary(AiTsdbQueryResultVO result) {
        StringBuilder builder = new StringBuilder();
        builder.append("设备 ").append(result.getSerialNumber()).append(" 的 ");
        builder.append(StringUtils.isNotBlank(result.getSemanticName()) ? result.getSemanticName() : result.getIdentifier());
        builder.append(" 最近一次时序值为 ").append(defaultValue(result.getLatestValue()));
        if (StringUtils.isNotBlank(result.getUnit())) {
            builder.append(result.getUnit());
        }
        if (StringUtils.isNotBlank(result.getLatestTime())) {
            builder.append("，时间 ").append(result.getLatestTime());
        }
        return builder.toString();
    }

    private String buildAggregateSummary(AiTsdbQueryResultVO result) {
        StringBuilder builder = new StringBuilder();
        builder.append("设备 ").append(result.getSerialNumber()).append(" 的 ");
        builder.append(StringUtils.isNotBlank(result.getSemanticName()) ? result.getSemanticName() : result.getIdentifier());
        builder.append(" 在 ").append(defaultValue(result.getTimeWindowLabel()));
        builder.append(" 的 ").append(defaultValue(result.getStatisticOperation())).append("值为 ");
        builder.append(defaultValue(result.getStatisticValue()));
        if (StringUtils.isNotBlank(result.getUnit())) {
            builder.append(result.getUnit());
        }
        return builder.toString();
    }

    private String buildHistorySummary(AiTsdbQueryResultVO result) {
        StringBuilder builder = new StringBuilder();
        builder.append("设备 ").append(result.getSerialNumber()).append(" 的 ");
        builder.append(StringUtils.isNotBlank(result.getSemanticName()) ? result.getSemanticName() : result.getIdentifier());
        builder.append(" 在 ").append(defaultValue(result.getTimeWindowLabel()));
        builder.append(" 共查询到 ").append(result.getHistoryPoints().size()).append(" 个历史点位。");
        return builder.toString();
    }

    private String resolveLogTime(DeviceLog deviceLog) {
        if (deviceLog == null) {
            return null;
        }
        if (deviceLog.getTs() != null) {
            return formatDate(deviceLog.getTs());
        }
        if (deviceLog.getCreateTime() != null) {
            return formatDate(deviceLog.getCreateTime());
        }
        return null;
    }

    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(date);
    }

    private boolean isFunctionModel(AiSemanticFieldVO runtimeField, Long productId, String identifier) {
        ThingsModelValueItem item = resolveThingModelItem(productId, identifier);
        if (item != null && item.getType() != null) {
            return Integer.valueOf(2).equals(item.getType());
        }
        String modelType = extractQueryHintValue(runtimeField, "model-type");
        return StringUtils.isNotBlank(modelType) && "功能".equals(modelType.trim());
    }

    private String extractQueryHintValue(AiSemanticFieldVO runtimeField, String key) {
        if (runtimeField == null || runtimeField.getQueryHints() == null || StringUtils.isBlank(key)) {
            return null;
        }
        String prefix = key + "=";
        for (String hint : runtimeField.getQueryHints()) {
            if (StringUtils.isNotBlank(hint) && hint.startsWith(prefix)) {
                return hint.substring(prefix.length());
            }
        }
        return null;
    }

    private String defaultValue(String value) {
        return StringUtils.isBlank(value) ? "-" : value;
    }

    private boolean containsAny(String question, String... keywords) {
        if (StringUtils.isBlank(question) || keywords == null) {
            return false;
        }
        String source = normalize(question);
        for (String keyword : keywords) {
            if (StringUtils.isNotBlank(keyword) && source.contains(normalize(keyword))) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String value) {
        return value == null ? "" : value.replaceAll("[\\s,，。；;:：？?]+", "").toLowerCase(Locale.ROOT);
    }

    private QueryWindow resolveWindow(String question) {
        LocalDateTime now = LocalDateTime.now();
        QueryWindow window = new QueryWindow();

        QueryWindow relativeWindow = resolveRelativeWindow(question, now);
        if (relativeWindow != null) {
            return relativeWindow;
        }
        if (containsAny(question, "当天", "今天")) {
            LocalDate today = LocalDate.now();
            window.beginTime = today.atStartOfDay().format(TIME_FORMATTER);
            window.endTime = now.format(TIME_FORMATTER);
            window.label = "当天";
            return window;
        }
        if (containsAny(question, "昨日", "昨天")) {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            window.beginTime = yesterday.atStartOfDay().format(TIME_FORMATTER);
            window.endTime = LocalDateTime.of(yesterday, LocalTime.MAX).format(TIME_FORMATTER);
            window.label = "昨天";
            return window;
        }
        if (containsAny(question, "近7天", "最近7天")) {
            window.beginTime = now.minusDays(7).format(TIME_FORMATTER);
            window.endTime = now.format(TIME_FORMATTER);
            window.label = "最近7天";
            return window;
        }
        if (containsAny(question, "本周")) {
            LocalDate today = LocalDate.now();
            LocalDate monday = today.minusDays((today.getDayOfWeek().getValue() + 6L - DayOfWeek.MONDAY.getValue()) % 7);
            window.beginTime = monday.atStartOfDay().format(TIME_FORMATTER);
            window.endTime = now.format(TIME_FORMATTER);
            window.label = "本周";
            return window;
        }

        window.beginTime = now.minusHours(24).format(TIME_FORMATTER);
        window.endTime = now.format(TIME_FORMATTER);
        window.label = "最近24小时";
        window.defaulted = true;
        return window;
    }

    private QueryWindow resolveRelativeWindow(String question, LocalDateTime now) {
        if (StringUtils.isBlank(question)) {
            return null;
        }
        Matcher matcher = RELATIVE_WINDOW_PATTERN.matcher(question);
        if (!matcher.find()) {
            return null;
        }
        int amount = parsePositiveNumber(matcher.group(1));
        if (amount <= 0) {
            return null;
        }
        String unit = matcher.group(2);
        LocalDateTime beginTime;
        if ("分钟".equals(unit)) {
            beginTime = now.minusMinutes(amount);
        } else if ("小时".equals(unit)) {
            beginTime = now.minusHours(amount);
        } else if ("天".equals(unit)) {
            beginTime = now.minusDays(amount);
        } else if ("周".equals(unit)) {
            beginTime = now.minusWeeks(amount);
        } else if ("月".equals(unit)) {
            beginTime = now.minusMonths(amount);
        } else {
            return null;
        }
        QueryWindow window = new QueryWindow();
        window.beginTime = beginTime.format(TIME_FORMATTER);
        window.endTime = now.format(TIME_FORMATTER);
        window.label = "最近" + matcher.group(1) + unit;
        return window;
    }

    private int parsePositiveNumber(String text) {
        if (StringUtils.isBlank(text)) {
            return 0;
        }
        String value = text.trim();
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return parseChineseNumber(value);
        }
    }

    private int parseChineseNumber(String text) {
        if (StringUtils.isBlank(text)) {
            return 0;
        }
        if ("十".equals(text)) {
            return 10;
        }
        int tenIndex = text.indexOf('十');
        if (tenIndex >= 0) {
            String high = text.substring(0, tenIndex);
            String low = text.substring(tenIndex + 1);
            int highValue = StringUtils.isBlank(high) ? 1 : parseChineseDigit(high);
            int lowValue = StringUtils.isBlank(low) ? 0 : parseChineseDigit(low);
            return highValue <= 0 || lowValue < 0 ? 0 : highValue * 10 + lowValue;
        }
        return parseChineseDigit(text);
    }

    private int parseChineseDigit(String text) {
        if ("一".equals(text)) {
            return 1;
        }
        if ("二".equals(text) || "两".equals(text)) {
            return 2;
        }
        if ("三".equals(text)) {
            return 3;
        }
        if ("四".equals(text)) {
            return 4;
        }
        if ("五".equals(text)) {
            return 5;
        }
        if ("六".equals(text)) {
            return 6;
        }
        if ("七".equals(text)) {
            return 7;
        }
        if ("八".equals(text)) {
            return 8;
        }
        if ("九".equals(text)) {
            return 9;
        }
        return 0;
    }

    private static class QueryWindow {
        private String beginTime;
        private String endTime;
        private String label;
        private boolean defaulted;
    }
}
