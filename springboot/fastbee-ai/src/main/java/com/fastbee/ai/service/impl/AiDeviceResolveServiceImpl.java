package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import com.fastbee.ai.service.IAiDeviceMetadataService;
import com.fastbee.ai.service.IAiDeviceResolveService;
import com.fastbee.ai.support.AiCandidateMatchSupport;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.DeviceShortOutput;
import com.fastbee.iot.model.vo.DeviceVO;
import com.fastbee.iot.service.IDeviceService;

@Service
public class AiDeviceResolveServiceImpl implements IAiDeviceResolveService {

    private static final int DEVICE_CANDIDATE_PAGE_SIZE = 50;
    private static final int DEVICE_CANDIDATE_MESSAGE_LIMIT = 20;
    private static final int NAME_MATCH_SCORE_EXACT = 100;
    private static final int NAME_MATCH_SCORE_PREFIX_OR_SUFFIX = 80;
    private static final int NAME_MATCH_SCORE_CONTAINS = 60;

    private static final Pattern SERIAL_PATTERN = Pattern.compile(
            "(?:serialNumber|deviceNumber|设备编号)\\s*[=:：]\\s*([^\\s,，；;]+)|(?:serialNumber|deviceNumber|设备编号)\\s+([^\\s,，；;]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern BARE_SERIAL_PATTERN = Pattern.compile(
            "(?<![A-Za-z0-9_-])([A-Za-z][A-Za-z0-9_-]{5,})(?![A-Za-z0-9_-])",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern DEVICE_NAME_PATTERN = Pattern.compile(
            "(?:deviceName|设备名称)\\s*[=:：]\\s*([^\\s,，；;]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PRODUCT_NAME_PATTERN = Pattern.compile(
            "(?:productName|产品名称|产品名)\\s*[=:：]\\s*([^\\s,，；;]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern EXPLICIT_DEVICE_TOKEN_PATTERN = Pattern.compile(
            "(?:设备|device)\\s*([^\\s,，。；;？?]{2,64}?)(?=(?:\\s|的|当前|实时|现在|此刻|最新|最近|历史|今天|昨天|昨日|温度|湿度|电量|电压|电流|功率|压力|状态|在线|离线|是多少|多少|统计|趋势|总计|总和|平均|最大|最小|,|，|。|；|;|？|\\?|$))",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern[] DEVICE_CANDIDATE_PATTERNS = new Pattern[] {
            Pattern.compile("(?:查询|统计|查看|获取|请问|帮我查询|帮我查下|帮我查看|帮我看下|帮我)([^\\s,，。；;？?]{2,32}?)(?:设备)?(?:当前|实时|现在|此刻|最近一次|最后一次|历史|当天|今天|昨日|昨天|最近|趋势|统计|总计|总和|平均|最大|最小|温度|湿度|电量|电压|电流|功率|压力|告警|状态|在线|离线|是多少|多少)"),
            Pattern.compile("([^\\s,，。；;？?]{2,32}?)(?:设备)?(?:当前|实时|现在|此刻|最近一次|最后一次|历史|当天|今天|昨日|昨天|最近|趋势|统计|总计|总和|平均|最大|最小|温度|湿度|电量|电压|电流|功率|压力|告警|状态|在线|离线|是多少|多少)")
    };

    @Resource
    private IAiDeviceMetadataService aiDeviceMetadataService;

    @Resource
    private IDeviceService deviceService;

    @Override
    public DeviceMetaData resolveRequiredDeviceMetaData(String question) {
        DeviceMetaData deviceMetaData = resolveOptionalDeviceMetaData(question);
        if (deviceMetaData == null) {
            throw buildDeviceResolveException(question);
        }
        return deviceMetaData;
    }

    @Override
    public DeviceMetaData resolveOptionalDeviceMetaData(String question) {
        String serialNumber = extractSerialNumber(question);
        if (StringUtils.isNotBlank(serialNumber)) {
            return aiDeviceMetadataService.getOptionalDeviceMetaData(serialNumber);
        }

        String explicitDeviceName = extractExplicitDeviceName(question);
        if (StringUtils.isNotBlank(explicitDeviceName)) {
            return resolveExplicitDeviceName(explicitDeviceName);
        }

        String explicitDeviceToken = extractExplicitDeviceToken(question);
        if (StringUtils.isNotBlank(explicitDeviceToken)) {
            return resolveExplicitMentionedDevice(explicitDeviceToken);
        }

        String explicitProductName = extractExplicitProductName(question);
        if (StringUtils.isNotBlank(explicitProductName)) {
            return resolveExplicitProductName(explicitProductName);
        }

        for (String candidate : extractDeviceNameCandidates(question)) {
            DeviceMetaData deviceMetaData = resolveByDeviceName(candidate, false, false, preferProductMatch(question, candidate));
            if (deviceMetaData != null) {
                return deviceMetaData;
            }
        }
        return null;
    }

    @Override
    public List<DeviceShortOutput> listDeviceCandidates(String question) {
        if (StringUtils.isBlank(question)) {
            return new ArrayList<>();
        }
        if (StringUtils.isNotBlank(extractSerialNumber(question))) {
            return new ArrayList<>();
        }

        String explicitDeviceName = extractExplicitDeviceName(question);
        if (StringUtils.isNotBlank(explicitDeviceName)) {
            return findExplicitDeviceCandidates(explicitDeviceName);
        }

        String explicitDeviceToken = extractExplicitDeviceToken(question);
        if (StringUtils.isNotBlank(explicitDeviceToken)) {
            return findExplicitDeviceCandidates(explicitDeviceToken);
        }

        String explicitProductName = extractExplicitProductName(question);
        if (StringUtils.isNotBlank(explicitProductName)) {
            return findExplicitProductCandidates(explicitProductName);
        }

        for (String candidate : extractDeviceNameCandidates(question)) {
            List<DeviceShortOutput> records = findDeviceCandidates(candidate, false, preferProductMatch(question, candidate));
            if (!records.isEmpty()) {
                return records;
            }
        }
        return new ArrayList<>();
    }

    private DeviceMetaData resolveByDeviceName(String deviceName, boolean strict, boolean explicitDeviceName, boolean preferProductMatch) {
        String normalizedCandidate = normalizeDeviceName(deviceName);
        if (StringUtils.isBlank(normalizedCandidate) || isGenericCandidate(normalizedCandidate)) {
            return null;
        }

        List<DeviceShortOutput> records = findDeviceCandidates(deviceName, explicitDeviceName, preferProductMatch);
        if (records.isEmpty()) {
            return null;
        }

        if (records.size() == 1) {
            return aiDeviceMetadataService.getOptionalDeviceMetaData(records.get(0).getSerialNumber());
        }
        if (strict) {
            throw new ServiceException(message("ai.device.resolve.multiple.explicit.serial.required"));
        }
        return null;
    }

    private DeviceMetaData resolveExplicitDeviceName(String deviceName) {
        String normalizedCandidate = normalizeDeviceName(deviceName);
        if (StringUtils.isBlank(normalizedCandidate) || isGenericCandidate(normalizedCandidate)) {
            return null;
        }
        List<DeviceShortOutput> candidates = findExplicitDeviceCandidates(deviceName);
        if (candidates.size() == 1) {
            return aiDeviceMetadataService.getOptionalDeviceMetaData(candidates.get(0).getSerialNumber());
        }
        return null;
    }

    private DeviceMetaData resolveExplicitMentionedDevice(String explicitDeviceToken) {
        DeviceMetaData serialMatched = aiDeviceMetadataService.getOptionalDeviceMetaData(explicitDeviceToken);
        if (serialMatched != null) {
            return serialMatched;
        }

        List<DeviceShortOutput> candidates = findExplicitDeviceCandidates(explicitDeviceToken);
        if (candidates.size() == 1) {
            return aiDeviceMetadataService.getOptionalDeviceMetaData(candidates.get(0).getSerialNumber());
        }
        return null;
    }

    private DeviceMetaData resolveExplicitProductName(String productName) {
        String normalizedCandidate = normalizeDeviceName(productName);
        if (StringUtils.isBlank(normalizedCandidate) || isGenericCandidate(normalizedCandidate)) {
            return null;
        }
        List<DeviceShortOutput> candidates = findExplicitProductCandidates(productName);
        if (candidates.size() == 1) {
            return aiDeviceMetadataService.getOptionalDeviceMetaData(candidates.get(0).getSerialNumber());
        }
        return null;
    }

    private List<DeviceShortOutput> findExplicitDeviceCandidates(String keyword) {
        String normalizedCandidate = normalizeDeviceName(keyword);
        if (StringUtils.isBlank(normalizedCandidate) || isGenericCandidate(normalizedCandidate)) {
            return new ArrayList<>();
        }
        List<DeviceShortOutput> deviceNameRecords = queryDeviceShortListByDeviceName(keyword.trim());
        List<DeviceShortOutput> exactDeviceMatches = filterExactDeviceNameMatches(deviceNameRecords, normalizedCandidate);
        if (!exactDeviceMatches.isEmpty()) {
            return exactDeviceMatches;
        }
        return deviceNameRecords;
    }

    private List<DeviceShortOutput> findExplicitProductCandidates(String keyword) {
        String normalizedCandidate = normalizeDeviceName(keyword);
        if (StringUtils.isBlank(normalizedCandidate) || isGenericCandidate(normalizedCandidate)) {
            return new ArrayList<>();
        }
        List<DeviceShortOutput> productNameRecords = queryDeviceShortListByProductName(keyword.trim());
        List<DeviceShortOutput> exactProductMatches = filterExactProductNameMatches(productNameRecords, normalizedCandidate);
        if (!exactProductMatches.isEmpty()) {
            return exactProductMatches;
        }
        return productNameRecords;
    }

    private List<DeviceShortOutput> findDeviceCandidates(String keyword, boolean explicitDeviceName, boolean preferProductMatch) {
        String normalizedCandidate = normalizeDeviceName(keyword);
        if (StringUtils.isBlank(normalizedCandidate) || isGenericCandidate(normalizedCandidate)) {
            return new ArrayList<>();
        }

        List<DeviceShortOutput> deviceNameRecords = queryDeviceShortListByDeviceName(keyword.trim());
        List<DeviceShortOutput> exactDeviceMatches = filterExactDeviceNameMatches(deviceNameRecords, normalizedCandidate);
        if (explicitDeviceName) {
            if (!exactDeviceMatches.isEmpty()) {
                return exactDeviceMatches;
            }
            if (!deviceNameRecords.isEmpty()) {
                return deviceNameRecords;
            }
        } else {
            if (!exactDeviceMatches.isEmpty()) {
                return exactDeviceMatches;
            }
            if (!deviceNameRecords.isEmpty() && !preferProductMatch) {
                return deviceNameRecords;
            }
        }

        List<DeviceShortOutput> productNameRecords = queryDeviceShortListByProductName(keyword.trim());
        if (productNameRecords.isEmpty()) {
            return deviceNameRecords;
        }
        List<DeviceShortOutput> exactProductMatches = filterExactProductNameMatches(productNameRecords, normalizedCandidate);
        if (preferProductMatch && !exactProductMatches.isEmpty()) {
            return exactProductMatches;
        }
        if (!exactProductMatches.isEmpty()) {
            return exactProductMatches;
        }
        if (!deviceNameRecords.isEmpty()) {
            return deviceNameRecords;
        }
        return productNameRecords;
    }

    private boolean preferProductMatch(String question, String candidate) {
        return containsProductHint(question) || containsProductHint(candidate);
    }

    private boolean containsProductHint(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        String normalizedText = text.trim().toLowerCase(Locale.ROOT);
        return normalizedText.contains("产品") || normalizedText.contains("product");
    }

    private List<DeviceShortOutput> queryDeviceShortListByDeviceName(String deviceName) {
        String searchKeyword = sanitizeSearchKeyword(deviceName);
        DeviceVO deviceVO = new DeviceVO();
        deviceVO.setPageNum(1);
        deviceVO.setPageSize(DEVICE_CANDIDATE_PAGE_SIZE);
        deviceVO.setDeviceName(searchKeyword);
        Page<DeviceShortOutput> page = deviceService.selectDeviceShortList(deviceVO);
        List<DeviceShortOutput> accessibleRecords =
                filterAccessibleDeviceRecords(page == null || page.getRecords() == null ? new ArrayList<>() : page.getRecords());
        return filterLikelyDeviceNameRecords(accessibleRecords, normalizeDeviceName(searchKeyword));
    }

    private List<DeviceShortOutput> queryDeviceShortListByProductName(String productName) {
        String searchKeyword = sanitizeSearchKeyword(productName);
        DeviceVO deviceVO = new DeviceVO();
        deviceVO.setPageNum(1);
        deviceVO.setPageSize(DEVICE_CANDIDATE_PAGE_SIZE);
        deviceVO.setProductName(searchKeyword);
        Page<DeviceShortOutput> page = deviceService.selectDeviceShortList(deviceVO);
        List<DeviceShortOutput> accessibleRecords =
                filterAccessibleDeviceRecords(page == null || page.getRecords() == null ? new ArrayList<>() : page.getRecords());
        return filterLikelyProductNameRecords(accessibleRecords, normalizeDeviceName(searchKeyword));
    }

    private List<DeviceShortOutput> filterAccessibleDeviceRecords(List<DeviceShortOutput> records) {
        List<DeviceShortOutput> accessibleRecords = new ArrayList<>();
        if (records == null || records.isEmpty()) {
            return accessibleRecords;
        }
        for (DeviceShortOutput record : records) {
            if (record == null || record.getDeviceId() == null) {
                continue;
            }
            accessibleRecords.add(record);
        }
        return accessibleRecords;
    }

    private List<DeviceShortOutput> filterLikelyDeviceNameRecords(List<DeviceShortOutput> records, String normalizedCandidate) {
        return collectBestMatchedRecords(records, normalizedCandidate, true);
    }

    private List<DeviceShortOutput> filterLikelyProductNameRecords(List<DeviceShortOutput> records, String normalizedCandidate) {
        return collectBestMatchedRecords(records, normalizedCandidate, false);
    }

    private List<DeviceShortOutput> collectBestMatchedRecords(List<DeviceShortOutput> records, String normalizedCandidate, boolean deviceNameMatch) {
        List<DeviceShortOutput> bestMatches = new ArrayList<>();
        if (records == null || records.isEmpty()) {
            return bestMatches;
        }
        if (StringUtils.isBlank(normalizedCandidate)) {
            return new ArrayList<>(records);
        }
        int bestScore = 0;
        for (DeviceShortOutput record : records) {
            if (record == null) {
                continue;
            }
            String sourceText = deviceNameMatch ? record.getDeviceName() : record.getProductName();
            String normalizedSource = normalizeDeviceName(sourceText);
            int score = scoreNameMatch(normalizedCandidate, normalizedSource);
            if (score <= 0) {
                continue;
            }
            if (score > bestScore) {
                bestScore = score;
                bestMatches.clear();
            }
            if (score == bestScore) {
                bestMatches.add(record);
            }
        }
        return bestMatches;
    }

    private int scoreNameMatch(String normalizedCandidate, String normalizedSource) {
        if (StringUtils.isBlank(normalizedCandidate) || StringUtils.isBlank(normalizedSource)) {
            return 0;
        }
        if (normalizedSource.equals(normalizedCandidate)) {
            return NAME_MATCH_SCORE_EXACT;
        }
        if (normalizedSource.startsWith(normalizedCandidate)
                || normalizedSource.endsWith(normalizedCandidate)
                || normalizedCandidate.startsWith(normalizedSource)
                || normalizedCandidate.endsWith(normalizedSource)) {
            return NAME_MATCH_SCORE_PREFIX_OR_SUFFIX;
        }
        if (normalizedSource.contains(normalizedCandidate) || normalizedCandidate.contains(normalizedSource)) {
            return NAME_MATCH_SCORE_CONTAINS;
        }
        return 0;
    }

    private List<DeviceShortOutput> mergeDeviceRecords(List<DeviceShortOutput> first, List<DeviceShortOutput> second) {
        LinkedHashMap<String, DeviceShortOutput> merged = new LinkedHashMap<>();
        appendDeviceRecords(merged, first);
        appendDeviceRecords(merged, second);
        return new ArrayList<>(merged.values());
    }

    private void appendDeviceRecords(LinkedHashMap<String, DeviceShortOutput> merged, List<DeviceShortOutput> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        for (DeviceShortOutput record : records) {
            if (record == null || StringUtils.isBlank(record.getSerialNumber())) {
                continue;
            }
            merged.putIfAbsent(record.getSerialNumber(), record);
        }
    }

    private List<DeviceShortOutput> filterExactDeviceNameMatches(List<DeviceShortOutput> records, String normalizedCandidate) {
        List<DeviceShortOutput> exactMatches = new ArrayList<>();
        if (records == null || records.isEmpty()) {
            return exactMatches;
        }
        for (DeviceShortOutput record : records) {
            if (record == null || StringUtils.isBlank(record.getDeviceName()) || StringUtils.isBlank(record.getSerialNumber())) {
                continue;
            }
            if (normalizeDeviceName(record.getDeviceName()).equals(normalizedCandidate)) {
                exactMatches.add(record);
            }
        }
        return exactMatches;
    }

    private List<DeviceShortOutput> filterExactProductNameMatches(List<DeviceShortOutput> records, String normalizedCandidate) {
        List<DeviceShortOutput> exactMatches = new ArrayList<>();
        if (records == null || records.isEmpty()) {
            return exactMatches;
        }
        for (DeviceShortOutput record : records) {
            if (record == null || StringUtils.isBlank(record.getProductName()) || StringUtils.isBlank(record.getSerialNumber())) {
                continue;
            }
            if (normalizeDeviceName(record.getProductName()).equals(normalizedCandidate)) {
                exactMatches.add(record);
            }
        }
        return exactMatches;
    }

    private String extractSerialNumber(String question) {
        if (StringUtils.isBlank(question)) {
            return null;
        }
        Matcher matcher = SERIAL_PATTERN.matcher(question);
        if (matcher.find()) {
            String first = matcher.group(1);
            if (StringUtils.isNotBlank(first)) {
                return AiCandidateMatchSupport.stripSlotDecorators(first);
            }
            String second = matcher.group(2);
            if (StringUtils.isNotBlank(second)) {
                return AiCandidateMatchSupport.stripSlotDecorators(second);
            }
        }
        return extractBareSerialNumber(question);
    }

    private String extractBareSerialNumber(String question) {
        Matcher matcher = BARE_SERIAL_PATTERN.matcher(question);
        while (matcher.find()) {
            String candidate = matcher.group(1);
            if (StringUtils.isBlank(candidate) || !containsDigit(candidate)) {
                continue;
            }
            String serialNumber = candidate.trim();
            if (aiDeviceMetadataService.getOptionalDeviceMetaData(serialNumber) != null) {
                return serialNumber;
            }
        }
        return null;
    }

    private boolean containsDigit(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            if (Character.isDigit(value.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private String extractExplicitDeviceName(String question) {
        if (StringUtils.isBlank(question)) {
            return null;
        }
        Matcher matcher = DEVICE_NAME_PATTERN.matcher(question);
        return matcher.find() ? AiCandidateMatchSupport.stripSlotDecorators(matcher.group(1)) : null;
    }

    private String extractExplicitProductName(String question) {
        if (StringUtils.isBlank(question)) {
            return null;
        }
        Matcher matcher = PRODUCT_NAME_PATTERN.matcher(question);
        return matcher.find() ? AiCandidateMatchSupport.stripSlotDecorators(matcher.group(1)) : null;
    }

    private String extractExplicitDeviceToken(String question) {
        if (StringUtils.isBlank(question)) {
            return null;
        }
        Matcher matcher = EXPLICIT_DEVICE_TOKEN_PATTERN.matcher(question);
        while (matcher.find()) {
            String candidate = normalizeCandidate(matcher.group(1));
            if (StringUtils.isNotBlank(candidate) && !isGenericCandidate(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private List<String> extractDeviceNameCandidates(String question) {
        LinkedHashSet<String> candidates = new LinkedHashSet<>();
        if (StringUtils.isBlank(question)) {
            return new ArrayList<>();
        }
        for (Pattern pattern : DEVICE_CANDIDATE_PATTERNS) {
            Matcher matcher = pattern.matcher(question);
            while (matcher.find()) {
                String candidate = normalizeCandidate(matcher.group(1));
                if (StringUtils.isNotBlank(candidate) && !isGenericCandidate(candidate)) {
                    candidates.add(candidate);
                }
            }
        }
        return new ArrayList<>(candidates);
    }

    private String normalizeCandidate(String candidate) {
        if (StringUtils.isBlank(candidate)) {
            return null;
        }
        String value = candidate.trim();
        value = value.replaceAll("^(查询|统计|查看|获取|请问|帮我查询|帮我查下|帮我查看|帮我看下|帮我)", "");
        value = value.replaceAll("^(设备|device)", "");
        int possessiveIndex = value.indexOf("的");
        if (possessiveIndex > 0) {
            value = value.substring(0, possessiveIndex);
        }
        value = value.replaceAll("(当前|实时|现在|此刻|最近一次|最后一次|历史|当天|今天|昨日|昨天|最近|趋势|统计|总计|总和|平均|最大|最小|温度|湿度|电量|电压|电流|功率|压力|告警|状态|在线|离线|是多少|多少)$", "");
        value = value.replaceAll("[的\\s]+$", "");
        value = value.replaceAll("^[:：]+", "");
        return AiCandidateMatchSupport.stripSlotDecorators(value);
    }

    private boolean isGenericCandidate(String candidate) {
        String normalized = normalizeDeviceName(candidate);
        return normalized.isEmpty()
                || "设备".equals(normalized)
                || "某设备".equals(normalized)
                || "某个设备".equals(normalized)
                || "某台设备".equals(normalized)
                || "在线设备".equals(normalized)
                || "离线设备".equals(normalized);
    }

    private String normalizeDeviceName(String deviceName) {
        if (deviceName == null) {
            return "";
        }
        return deviceName.trim()
                .replaceAll("[\\s\\-_.()（）【】\\[\\]{}｛｝\"'`“”‘’★☆*＊#＃·•,:：/\\\\]+", "")
                .toLowerCase(Locale.ROOT);
    }

    private String sanitizeSearchKeyword(String keyword) {
        String value = AiCandidateMatchSupport.stripSlotDecorators(keyword);
        return value == null ? "" : value;
    }

    private ServiceException buildDeviceResolveException(String question) {
        List<DeviceShortOutput> candidates = listDeviceCandidates(question);
        if (candidates == null || candidates.isEmpty()) {
            return new ServiceException(message("ai.device.resolve.unique.required"));
        }
        List<String> candidateTexts = new ArrayList<>();
        for (DeviceShortOutput candidate : candidates) {
            if (candidate == null || StringUtils.isBlank(candidate.getSerialNumber())) {
                continue;
            }
            StringBuilder builder = new StringBuilder(candidate.getSerialNumber());
            if (StringUtils.isNotBlank(candidate.getDeviceName())) {
                builder.append('/').append(candidate.getDeviceName().trim());
            }
            if (StringUtils.isNotBlank(candidate.getProductName())) {
                builder.append("（").append(candidate.getProductName().trim()).append("）");
            }
            candidateTexts.add(builder.toString());
            if (candidateTexts.size() >= DEVICE_CANDIDATE_MESSAGE_LIMIT) {
                break;
            }
        }
        if (candidateTexts.isEmpty()) {
            return new ServiceException(message("ai.device.resolve.unique.required"));
        }
        return new ServiceException(message("ai.device.resolve.multiple.candidates", String.join("；", candidateTexts)));
    }
}
