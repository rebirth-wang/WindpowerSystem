package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastbee.ai.convert.AiKnowledgeVersionConvert;
import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeDocument;
import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.mapper.AiKnowledgeBaseMapper;
import com.fastbee.ai.mapper.AiKnowledgeVersionMapper;
import com.fastbee.ai.model.vo.AiKnowledgeVersionQualityCheckVO;
import com.fastbee.ai.model.vo.AiKnowledgeVersionVO;
import com.fastbee.ai.service.IAiKnowledgeDocumentService;
import com.fastbee.ai.service.IAiKnowledgeVersionService;
import com.fastbee.ai.service.IAiNl2SqlQualityGateService;
import com.fastbee.ai.service.IAiSemanticNormalizationService;
import com.fastbee.ai.service.IAiSemanticRuntimeStore;
import com.fastbee.ai.support.AiSecuritySupport;
import com.fastbee.common.config.RuoYiConfig;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 知识库版本服务实现。
 */
@Service
public class AiKnowledgeVersionServiceImpl extends ServiceImpl<AiKnowledgeVersionMapper, AiKnowledgeVersion>
        implements IAiKnowledgeVersionService {

    private static final String KB_TYPE_NL2SQL = "NL2SQL_SEMANTIC";
    private static final String KB_TYPE_PROTOCOL = "PROTOCOL_SPEC";
    private static final String KB_TYPE_PLATFORM_DOC = "PLATFORM_DOC";
    private static final String KB_TYPE_CODEBASE_GUIDE = "CODEBASE_GUIDE";
    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_PUBLISHED = "PUBLISHED";
    private static final String STATUS_ARCHIVED = "ARCHIVED";
    private static final String ACTIVE_YES = "1";
    private static final String ACTIVE_NO = "0";
    private static final String KNOWLEDGE_STATUS_ENABLED = "0";
    private static final String DOCUMENT_STATUS_SUCCESS = "SUCCESS";
    private static final String DOCUMENT_ENABLED = "0";

    @Resource
    private AiKnowledgeBaseMapper aiKnowledgeBaseMapper;

    @Resource
    private IAiKnowledgeDocumentService aiKnowledgeDocumentService;

    @Resource
    private IAiSemanticNormalizationService aiSemanticNormalizationService;

    @Resource
    private IAiNl2SqlQualityGateService aiNl2SqlQualityGateService;

    @Autowired(required = false)
    private List<IAiSemanticRuntimeStore> aiSemanticRuntimeStores = Collections.emptyList();

    @Override
    public List<AiKnowledgeVersion> listAiKnowledgeVersion(AiKnowledgeVersion aiKnowledgeVersion) {
        LambdaQueryWrapper<AiKnowledgeVersion> lqw = buildQueryWrapper(aiKnowledgeVersion);
        lqw.orderByDesc(AiKnowledgeVersion::getIsActive)
                .orderByDesc(AiKnowledgeVersion::getPublishTime)
                .orderByDesc(AiKnowledgeVersion::getCreateTime);
        return baseMapper.selectList(lqw);
    }

    @Override
    public List<AiKnowledgeVersionVO> listAiKnowledgeVersionVO(AiKnowledgeVersion aiKnowledgeVersion) {
        return AiKnowledgeVersionConvert.INSTANCE.convertAiKnowledgeVersionVOList(listAiKnowledgeVersion(aiKnowledgeVersion));
    }

    @Override
    public Page<AiKnowledgeVersion> pageAiKnowledgeVersion(AiKnowledgeVersion aiKnowledgeVersion) {
        AiKnowledgeVersion actualQuery = aiKnowledgeVersion == null ? new AiKnowledgeVersion() : aiKnowledgeVersion;
        LambdaQueryWrapper<AiKnowledgeVersion> lqw = buildQueryWrapper(actualQuery);
        lqw.orderByDesc(AiKnowledgeVersion::getIsActive)
                .orderByDesc(AiKnowledgeVersion::getPublishTime)
                .orderByDesc(AiKnowledgeVersion::getCreateTime);
        return baseMapper.selectPage(new Page<>(actualQuery.getPageNum(), actualQuery.getPageSize()), lqw);
    }

    @Override
    public Page<AiKnowledgeVersionVO> pageAiKnowledgeVersionVO(AiKnowledgeVersion aiKnowledgeVersion) {
        return AiKnowledgeVersionConvert.INSTANCE.convertAiKnowledgeVersionVOPage(pageAiKnowledgeVersion(aiKnowledgeVersion));
    }

    @Override
    public AiKnowledgeVersion selectAiKnowledgeVersion(Long versionId) {
        AiKnowledgeVersion query = new AiKnowledgeVersion();
        query.setVersionId(versionId);
        return baseMapper.selectOne(buildQueryWrapper(query));
    }

    @Override
    public AiKnowledgeVersionVO selectAiKnowledgeVersionVO(Long versionId) {
        AiKnowledgeVersion version = selectAiKnowledgeVersion(versionId);
        if (version == null) {
            return null;
        }
        return AiKnowledgeVersionConvert.INSTANCE.convertAiKnowledgeVersionVO(version);
    }

    @Override
    public int insertAiKnowledgeVersion(AiKnowledgeVersion aiKnowledgeVersion) {
        AiKnowledgeBase knowledgeBase = requireKnowledgeBase(aiKnowledgeVersion.getKnowledgeBaseId());
        validKnowledgeVersionBeforeSave(aiKnowledgeVersion);
        checkSingleActiveVersion(aiKnowledgeVersion);
        aiKnowledgeVersion.setCreateBy(AiSecuritySupport.resolveUsername());
        aiKnowledgeVersion.setCreateTime(AiSecuritySupport.now());
        aiKnowledgeVersion.setUpdateBy(AiSecuritySupport.resolveUsername());
        aiKnowledgeVersion.setUpdateTime(AiSecuritySupport.now());
        if (StringUtils.isBlank(aiKnowledgeVersion.getVectorStoreType())) {
            aiKnowledgeVersion.setVectorStoreType(resolveKnowledgeBaseVectorStoreType(knowledgeBase));
        }
        if (StringUtils.isBlank(aiKnowledgeVersion.getPublishStatus())) {
            aiKnowledgeVersion.setPublishStatus(STATUS_DRAFT);
        }
        if (StringUtils.isBlank(aiKnowledgeVersion.getIsActive())) {
            aiKnowledgeVersion.setIsActive(ACTIVE_NO);
        }
        if (aiKnowledgeVersion.getSourceFileCount() == null) {
            aiKnowledgeVersion.setSourceFileCount(0);
        }
        if (aiKnowledgeVersion.getMergedItemCount() == null) {
            aiKnowledgeVersion.setMergedItemCount(0);
        }
        if (aiKnowledgeVersion.getOverrideCount() == null) {
            aiKnowledgeVersion.setOverrideCount(0);
        }
        if (aiKnowledgeVersion.getConflictCount() == null) {
            aiKnowledgeVersion.setConflictCount(0);
        }
        int rows = baseMapper.insert(aiKnowledgeVersion);
        syncKnowledgeBaseVersionSnapshot(aiKnowledgeVersion, null);
        return rows;
    }

    @Override
    public int updateAiKnowledgeVersion(AiKnowledgeVersion aiKnowledgeVersion) {
        AiKnowledgeVersion old = baseMapper.selectById(aiKnowledgeVersion.getVersionId());
        if (old == null) {
            throw new ServiceException(message("ai.knowledge.version.update.target.not.found"));
        }
        AiKnowledgeBase knowledgeBase = requireKnowledgeBase(old.getKnowledgeBaseId());
        if (aiKnowledgeVersion.getKnowledgeBaseId() == null) {
            aiKnowledgeVersion.setKnowledgeBaseId(old.getKnowledgeBaseId());
        }
        if (StringUtils.isBlank(aiKnowledgeVersion.getVersionNo())) {
            aiKnowledgeVersion.setVersionNo(old.getVersionNo());
        }
        if (StringUtils.isBlank(aiKnowledgeVersion.getPublishStatus())) {
            aiKnowledgeVersion.setPublishStatus(old.getPublishStatus());
        }
        if (StringUtils.isBlank(aiKnowledgeVersion.getIsActive())) {
            aiKnowledgeVersion.setIsActive(old.getIsActive());
        }
        validKnowledgeVersionBeforeSave(aiKnowledgeVersion);
        checkSingleActiveVersion(aiKnowledgeVersion);
        if (StringUtils.isBlank(aiKnowledgeVersion.getVectorStoreType())) {
            aiKnowledgeVersion.setVectorStoreType(StringUtils.isNotBlank(old.getVectorStoreType())
                    ? old.getVectorStoreType() : resolveKnowledgeBaseVectorStoreType(knowledgeBase));
        }
        if (aiKnowledgeVersion.getSourceFileCount() == null) {
            aiKnowledgeVersion.setSourceFileCount(old.getSourceFileCount());
        }
        if (aiKnowledgeVersion.getMergedItemCount() == null) {
            aiKnowledgeVersion.setMergedItemCount(old.getMergedItemCount());
        }
        if (aiKnowledgeVersion.getOverrideCount() == null) {
            aiKnowledgeVersion.setOverrideCount(old.getOverrideCount());
        }
        if (aiKnowledgeVersion.getConflictCount() == null) {
            aiKnowledgeVersion.setConflictCount(old.getConflictCount());
        }
        if (StringUtils.isBlank(aiKnowledgeVersion.getBuildSummary())) {
            aiKnowledgeVersion.setBuildSummary(old.getBuildSummary());
        }
        aiKnowledgeVersion.setUpdateBy(AiSecuritySupport.resolveUsername());
        aiKnowledgeVersion.setUpdateTime(AiSecuritySupport.now());
        int rows = baseMapper.updateById(aiKnowledgeVersion);
        AiKnowledgeVersion latest = baseMapper.selectById(aiKnowledgeVersion.getVersionId());
        syncKnowledgeBaseVersionSnapshot(latest, old);
        return rows;
    }

    @Override
    public int deleteAiKnowledgeVersionByIds(Long[] versionIds) {
        List<AiKnowledgeVersion> versionList = baseMapper.selectBatchIds(Arrays.asList(versionIds));
        for (AiKnowledgeVersion version : versionList) {
            if (isPublishedOrActive(version)) {
                throw new ServiceException(message("ai.knowledge.version.delete.published.active.exists"));
            }
        }
        int rows = baseMapper.deleteBatchIds(Arrays.asList(versionIds));
        for (AiKnowledgeVersion version : versionList) {
            cleanupSemanticRuntimeIfNecessary(version);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiKnowledgeVersion buildDraftAiKnowledgeVersion(Long knowledgeBaseId) {
        AiKnowledgeBase knowledgeBase = requireKnowledgeBase(knowledgeBaseId);
        List<AiKnowledgeDocument> allDocuments = listKnowledgeDocuments(knowledgeBaseId);
        List<AiKnowledgeDocument> buildableDocuments = allDocuments.stream()
                .filter(this::isBuildableDocument)
                .toList();
        if (buildableDocuments.isEmpty()) {
            throw new ServiceException(message("ai.knowledge.version.build.enabled.document.required"));
        }

        String versionNo = buildVersionNo();
        MergeBuildResult mergeResult = mergeKnowledgeSnapshots(knowledgeBase, buildableDocuments, versionNo);
        AiKnowledgeVersion knowledgeVersion = new AiKnowledgeVersion();
        knowledgeVersion.setKnowledgeBaseId(knowledgeBaseId);
        knowledgeVersion.setVersionNo(versionNo);
        knowledgeVersion.setSourceDocumentIds(joinDocumentIds(buildableDocuments));
        knowledgeVersion.setSnapshotPath(mergeResult.snapshotPath());
        knowledgeVersion.setVectorStoreType(resolveKnowledgeBaseVectorStoreType(knowledgeBase));
        knowledgeVersion.setPublishStatus(STATUS_DRAFT);
        knowledgeVersion.setIsActive(ACTIVE_NO);
        knowledgeVersion.setSourceFileCount(buildableDocuments.size());
        knowledgeVersion.setMergedItemCount(mergeResult.mergedItemCount());
        knowledgeVersion.setOverrideCount(mergeResult.overrideCount());
        knowledgeVersion.setConflictCount(mergeResult.conflictCount());
        knowledgeVersion.setBuildSummary(buildMergedSummary(buildableDocuments, allDocuments.size(), mergeResult));
        knowledgeVersion.setRemark("基于当前已启用且解析成功的文档自动构建草稿版本");
        insertAiKnowledgeVersion(knowledgeVersion);
        return baseMapper.selectById(knowledgeVersion.getVersionId());
    }

    @Override
    public AiKnowledgeVersionQualityCheckVO previewAiKnowledgeVersionQuality(Long versionId) {
        if (versionId == null) {
            throw new ServiceException(message("ai.knowledge.version.id.required"));
        }
        AiKnowledgeVersion version = baseMapper.selectById(versionId);
        if (version == null) {
            throw new ServiceException(message("ai.knowledge.version.not.exists"));
        }
        AiKnowledgeBase knowledgeBase = requireKnowledgeBase(version.getKnowledgeBaseId());
        return aiNl2SqlQualityGateService.checkBeforePublish(knowledgeBase, version);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int publishAiKnowledgeVersion(Long versionId) {
        return activateKnowledgeVersion(versionId, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int rollbackAiKnowledgeVersion(Long versionId) {
        return activateKnowledgeVersion(versionId, true);
    }

    private LambdaQueryWrapper<AiKnowledgeVersion> buildQueryWrapper(AiKnowledgeVersion query) {
        AiKnowledgeVersion actualQuery = query == null ? new AiKnowledgeVersion() : query;
        Map<String, Object> params = actualQuery.getParams() == null ? Collections.emptyMap() : actualQuery.getParams();
        LambdaQueryWrapper<AiKnowledgeVersion> lqw = Wrappers.lambdaQuery();
        lqw.eq(actualQuery.getVersionId() != null, AiKnowledgeVersion::getVersionId, actualQuery.getVersionId());
        lqw.eq(actualQuery.getKnowledgeBaseId() != null, AiKnowledgeVersion::getKnowledgeBaseId, actualQuery.getKnowledgeBaseId());
        lqw.like(StringUtils.isNotBlank(actualQuery.getVersionNo()), AiKnowledgeVersion::getVersionNo, actualQuery.getVersionNo());
        lqw.eq(StringUtils.isNotBlank(actualQuery.getVectorStoreType()), AiKnowledgeVersion::getVectorStoreType, actualQuery.getVectorStoreType());
        lqw.eq(StringUtils.isNotBlank(actualQuery.getPublishStatus()), AiKnowledgeVersion::getPublishStatus, actualQuery.getPublishStatus());
        lqw.eq(StringUtils.isNotBlank(actualQuery.getIsActive()), AiKnowledgeVersion::getIsActive, actualQuery.getIsActive());
        if (Objects.nonNull(params.get("beginTime")) && Objects.nonNull(params.get("endTime"))) {
            lqw.between(AiKnowledgeVersion::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    private List<AiKnowledgeDocument> listKnowledgeDocuments(Long knowledgeBaseId) {
        AiKnowledgeDocument query = new AiKnowledgeDocument();
        query.setKnowledgeBaseId(knowledgeBaseId);
        List<AiKnowledgeDocument> documents = aiKnowledgeDocumentService.listAiKnowledgeDocument(query);
        documents.sort((left, right) -> {
            int sortCompare = compareNullableInteger(left == null ? null : left.getSortNum(), right == null ? null : right.getSortNum());
            if (sortCompare != 0) {
                return sortCompare;
            }
            int updateCompare = compareNullableDateDesc(left == null ? null : left.getUpdateTime(), right == null ? null : right.getUpdateTime());
            if (updateCompare != 0) {
                return updateCompare;
            }
            return compareNullableDateDesc(left == null ? null : left.getCreateTime(), right == null ? null : right.getCreateTime());
        });
        return documents;
    }

    private boolean isBuildableDocument(AiKnowledgeDocument document) {
        return document != null
                && DOCUMENT_ENABLED.equals(document.getStatus())
                && DOCUMENT_STATUS_SUCCESS.equalsIgnoreCase(Objects.toString(document.getParseStatus(), ""))
                && StringUtils.isNotBlank(document.getParsedSnapshotPath());
    }

    private MergeBuildResult mergeKnowledgeSnapshots(AiKnowledgeBase knowledgeBase,
                                                     List<AiKnowledgeDocument> documents,
                                                     String versionNo) {
        JSONObject mergedSnapshot = buildMergedSnapshotHeader(knowledgeBase, versionNo, documents);
        JSONArray mergedItems = new JSONArray();
        LinkedHashMap<String, JSONObject> mergedMap = new LinkedHashMap<>();
        Map<String, LinkedHashMap<String, JSONObject>> enterpriseSheetMaps = createNl2SqlEnterpriseSheetMaps();
        int overrideCount = 0;
        int conflictCount = 0;
        int warningCount = 0;

        for (AiKnowledgeDocument document : documents) {
            JSONObject snapshot = readDocumentSnapshot(document);
            JSONArray items = snapshot.getJSONArray("items");
            if (items == null || items.isEmpty()) {
                continue;
            }
            warningCount += snapshot.getIntValue("validationWarningCount");
            for (int index = 0; index < items.size(); index++) {
                JSONObject item = items.getJSONObject(index);
                if (item == null) {
                    continue;
                }
                String mergeKey = resolveMergeKey(knowledgeBase.getKbType(), item, document, index);
                JSONObject clonedItem = JSON.parseObject(JSON.toJSONString(item));
                JSONObject previous = mergedMap.put(mergeKey, clonedItem);
                if (previous != null) {
                    overrideCount++;
                    if (!Objects.equals(JSON.toJSONString(previous), JSON.toJSONString(clonedItem))) {
                        conflictCount++;
                    }
                }
            }
            mergeNl2SqlEnterpriseSheets(knowledgeBase, snapshot, enterpriseSheetMaps, document);
        }

        mergedMap.values().forEach(mergedItems::add);
        mergedSnapshot.put("items", mergedItems);
        mergedSnapshot.put("rowCount", mergedItems.size());
        mergedSnapshot.put("mergedItemCount", mergedItems.size());
        mergedSnapshot.put("sourceFileCount", documents.size());
        mergedSnapshot.put("overrideCount", overrideCount);
        mergedSnapshot.put("conflictCount", conflictCount);
        mergedSnapshot.put("validationErrorCount", 0);
        mergedSnapshot.put("validationWarningCount", warningCount);

        if (KB_TYPE_NL2SQL.equalsIgnoreCase(knowledgeBase.getKbType())) {
            mergedSnapshot.put("tableCount", countDistinct(mergedItems, "tableName"));
            mergedSnapshot.put("fieldCount", countDistinct(mergedItems, "tableColumnKey"));
            appendMergedNl2SqlEnterpriseSheets(mergedSnapshot, enterpriseSheetMaps);
        } else if (KB_TYPE_PROTOCOL.equalsIgnoreCase(knowledgeBase.getKbType())) {
            mergedSnapshot.put("moduleCount", countDistinct(mergedItems, "moduleName"));
        } else if (KB_TYPE_CODEBASE_GUIDE.equalsIgnoreCase(knowledgeBase.getKbType())) {
            mergedSnapshot.put("sourcePathCount", countDistinct(mergedItems, "sourcePath"));
            mergedSnapshot.put("sourcePolicy", "仅保存源码相对路径、类名、方法名、接口路径、表名和AI摘要，不保存源码正文、配置值或密钥。");
        }

        String snapshotPath = saveMergedSnapshotFile(knowledgeBase, versionNo, mergedSnapshot);
        return new MergeBuildResult(snapshotPath, mergedItems.size(), overrideCount, conflictCount);
    }

    private Map<String, LinkedHashMap<String, JSONObject>> createNl2SqlEnterpriseSheetMaps() {
        Map<String, LinkedHashMap<String, JSONObject>> result = new LinkedHashMap<>();
        result.put("businessObjects", new LinkedHashMap<>());
        result.put("tableSemantics", new LinkedHashMap<>());
        result.put("relationPaths", new LinkedHashMap<>());
        result.put("metricRules", new LinkedHashMap<>());
        result.put("questionExamples", new LinkedHashMap<>());
        result.put("qualityReports", new LinkedHashMap<>());
        return result;
    }

    private void mergeNl2SqlEnterpriseSheets(AiKnowledgeBase knowledgeBase,
                                             JSONObject documentSnapshot,
                                             Map<String, LinkedHashMap<String, JSONObject>> enterpriseSheetMaps,
                                             AiKnowledgeDocument document) {
        if (!isNl2SqlKnowledgeBase(knowledgeBase) || documentSnapshot == null || enterpriseSheetMaps == null) {
            return;
        }
        mergeEnterpriseSheet(documentSnapshot, enterpriseSheetMaps, "businessObjects", document);
        mergeEnterpriseSheet(documentSnapshot, enterpriseSheetMaps, "tableSemantics", document);
        mergeEnterpriseSheet(documentSnapshot, enterpriseSheetMaps, "relationPaths", document);
        mergeEnterpriseSheet(documentSnapshot, enterpriseSheetMaps, "metricRules", document);
        mergeEnterpriseSheet(documentSnapshot, enterpriseSheetMaps, "questionExamples", document);
        mergeEnterpriseSheet(documentSnapshot, enterpriseSheetMaps, "qualityReports", document);
    }

    private void mergeEnterpriseSheet(JSONObject documentSnapshot,
                                      Map<String, LinkedHashMap<String, JSONObject>> enterpriseSheetMaps,
                                      String sheetKey,
                                      AiKnowledgeDocument document) {
        JSONArray items = documentSnapshot.getJSONArray(sheetKey);
        if (items == null || items.isEmpty()) {
            return;
        }
        LinkedHashMap<String, JSONObject> targetMap = enterpriseSheetMaps.get(sheetKey);
        if (targetMap == null) {
            return;
        }
        for (int index = 0; index < items.size(); index++) {
            JSONObject item = items.getJSONObject(index);
            if (item == null) {
                continue;
            }
            JSONObject clonedItem = JSON.parseObject(JSON.toJSONString(item));
            if (document != null) {
                clonedItem.put("sourceDocumentId", document.getDocumentId());
                clonedItem.put("sourceFileName", document.getFileName());
            }
            String mergeKey = resolveEnterpriseSheetMergeKey(sheetKey, clonedItem, document, index);
            targetMap.put(mergeKey, clonedItem);
        }
    }

    private String resolveEnterpriseSheetMergeKey(String sheetKey, JSONObject item, AiKnowledgeDocument document, int index) {
        if ("businessObjects".equals(sheetKey)) {
            return firstNonBlank(
                    item.getString("businessObjectCode"),
                    item.getString("primaryTable"),
                    item.getString("businessObjectName"),
                    fallbackEnterpriseSheetKey(document, index)
            );
        }
        if ("tableSemantics".equals(sheetKey)) {
            return firstNonBlank(item.getString("tableName"), fallbackEnterpriseSheetKey(document, index));
        }
        if ("relationPaths".equals(sheetKey)) {
            String relationKey = firstNonBlank(item.getString("relationCode"));
            if (StringUtils.isNotBlank(relationKey)) {
                return relationKey;
            }
            String sourceTable = trimToEmpty(item.getString("sourceTable"));
            String sourceColumn = trimToEmpty(item.getString("sourceColumn"));
            String targetTable = trimToEmpty(item.getString("targetTable"));
            String targetColumn = trimToEmpty(item.getString("targetColumn"));
            if (StringUtils.isBlank(sourceTable + sourceColumn + targetTable + targetColumn)) {
                return fallbackEnterpriseSheetKey(document, index);
            }
            return sourceTable + "." + sourceColumn + "->" + targetTable + "." + targetColumn;
        }
        if ("metricRules".equals(sheetKey)) {
            String businessMetricKey = buildMetricRuleMergeKey(item.getString("businessObjectName"), item.getString("metricRuleName"));
            String tableMetricKey = buildMetricRuleMergeKey(item.getString("primaryTable"), item.getString("metricRuleName"));
            return firstNonBlank(
                    item.getString("metricRuleCode"),
                    businessMetricKey,
                    tableMetricKey,
                    fallbackEnterpriseSheetKey(document, index)
            );
        }
        if ("questionExamples".equals(sheetKey)) {
            return firstNonBlank(item.getString("question"), fallbackEnterpriseSheetKey(document, index));
        }
        if ("qualityReports".equals(sheetKey)) {
            return firstNonBlank(item.getString("checkItem"), fallbackEnterpriseSheetKey(document, index));
        }
        return fallbackEnterpriseSheetKey(document, index);
    }

    private String fallbackEnterpriseSheetKey(AiKnowledgeDocument document, int index) {
        return (document == null ? "DOC" : String.valueOf(document.getDocumentId())) + "_" + index;
    }

    private void appendMergedNl2SqlEnterpriseSheets(JSONObject mergedSnapshot,
                                                   Map<String, LinkedHashMap<String, JSONObject>> enterpriseSheetMaps) {
        int enterpriseSheetCount = 0;
        for (Map.Entry<String, LinkedHashMap<String, JSONObject>> entry : enterpriseSheetMaps.entrySet()) {
            JSONArray array = new JSONArray();
            array.addAll(entry.getValue().values());
            mergedSnapshot.put(entry.getKey(), array);
            if (!array.isEmpty()) {
                enterpriseSheetCount++;
            }
        }
        JSONArray questionExamples = mergedSnapshot.getJSONArray("questionExamples");
        mergedSnapshot.put("enterpriseSheetCount", enterpriseSheetCount);
        mergedSnapshot.put("metricRuleCount", countJsonArray(mergedSnapshot.getJSONArray("metricRules")));
        mergedSnapshot.put("goldenQuestionCount", questionExamples == null ? 0 : questionExamples.size());
        mergedSnapshot.put("goldenQuestionP0Count", countRiskLevel(questionExamples, "P0"));
    }

    private int countJsonArray(JSONArray array) {
        return array == null ? 0 : array.size();
    }

    private String buildMetricRuleMergeKey(String first, String second) {
        String actualFirst = trimToEmpty(first);
        String actualSecond = trimToEmpty(second);
        if (StringUtils.isBlank(actualFirst) && StringUtils.isBlank(actualSecond)) {
            return "";
        }
        return actualFirst + "|" + actualSecond;
    }

    private int countRiskLevel(JSONArray questionExamples, String riskLevel) {
        if (questionExamples == null || questionExamples.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (int index = 0; index < questionExamples.size(); index++) {
            JSONObject item = questionExamples.getJSONObject(index);
            if (item != null && riskLevel.equalsIgnoreCase(trimToEmpty(item.getString("riskLevel")))) {
                count++;
            }
        }
        return count;
    }

    private JSONObject buildMergedSnapshotHeader(AiKnowledgeBase knowledgeBase,
                                                 String versionNo,
                                                 List<AiKnowledgeDocument> documents) {
        JSONObject snapshot = new JSONObject();
        snapshot.put("knowledgeBaseId", knowledgeBase.getKnowledgeBaseId());
        snapshot.put("kbCode", knowledgeBase.getKbCode());
        snapshot.put("kbName", knowledgeBase.getKbName());
        snapshot.put("kbType", knowledgeBase.getKbType());
        snapshot.put("versionNo", versionNo);
        snapshot.put("generatedAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        snapshot.put("buildMode", "MERGED_DOCUMENT_SNAPSHOTS");
        snapshot.put("sourceDocumentIds", documents.stream().map(AiKnowledgeDocument::getDocumentId).toList());
        JSONArray sourceDocuments = new JSONArray();
        for (AiKnowledgeDocument document : documents) {
            JSONObject sourceDocument = new JSONObject();
            sourceDocument.put("documentId", document.getDocumentId());
            sourceDocument.put("fileName", document.getFileName());
            sourceDocument.put("sortNum", document.getSortNum());
            sourceDocument.put("sourceOrigin", document.getSourceOrigin());
            sourceDocument.put("appVersion", document.getAppVersion());
            sourceDocument.put("parsedSnapshotPath", document.getParsedSnapshotPath());
            sourceDocument.put("parsedSummary", document.getParsedSummary());
            sourceDocuments.add(sourceDocument);
        }
        snapshot.put("sourceDocuments", sourceDocuments);
        snapshot.put("sourceOriginSummary", buildSourceOriginSummary(documents));
        snapshot.put("officialAppVersions", collectOfficialAppVersions(documents));
        return snapshot;
    }

    private JSONObject readDocumentSnapshot(AiKnowledgeDocument document) {
        Path snapshotPath = Paths.get(document.getParsedSnapshotPath().trim());
        if (!Files.exists(snapshotPath)) {
            throw new ServiceException(message("ai.knowledge.version.document.snapshot.not.exists", document.getFileName()));
        }
        try {
            return JSON.parseObject(Files.readString(snapshotPath, StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new ServiceException(message("ai.knowledge.version.document.snapshot.read.failed",
                    document.getFileName(), ex.getMessage()));
        }
    }

    private String resolveMergeKey(String kbType, JSONObject item, AiKnowledgeDocument document, int index) {
        String actualKbType = StringUtils.isBlank(kbType) ? KB_TYPE_PLATFORM_DOC : kbType.trim().toUpperCase();
        if (KB_TYPE_NL2SQL.equals(actualKbType)) {
            String key = trimToEmpty(item.getString("tableColumnKey"));
            if (StringUtils.isNotBlank(key)) {
                return key;
            }
            return trimToEmpty(item.getString("tableName")) + "." + trimToEmpty(item.getString("columnName"));
        }
        if (KB_TYPE_PROTOCOL.equals(actualKbType)) {
            return trimToEmpty(item.getString("moduleName")) + "|" + firstNonBlank(
                    item.getString("fieldCode"),
                    item.getString("fieldName"),
                    "ROW_" + index
            );
        }
        if (KB_TYPE_CODEBASE_GUIDE.equals(actualKbType)) {
            String sourcePath = trimToEmpty(item.getString("sourcePath"));
            String symbolPart = String.join("|",
                    trimToEmpty(item.getString("symbolType")),
                    trimToEmpty(item.getString("className")),
                    trimToEmpty(item.getString("methodName")),
                    trimToEmpty(item.getString("endpointPath")),
                    trimToEmpty(item.getString("symbolName"))
            );
            if (StringUtils.isNotBlank(sourcePath) || StringUtils.isNotBlank(symbolPart.replace("|", ""))) {
                return sourcePath + "|" + symbolPart;
            }
            return document.getDocumentId() + "_" + index;
        }
        return trimToEmpty(item.getString("sectionName")) + "|" + firstNonBlank(
                item.getString("title"),
                item.getString("content"),
                document.getDocumentId() + "_" + index
        );
    }

    private int countDistinct(JSONArray items, String fieldName) {
        LinkedHashSet<String> values = new LinkedHashSet<>();
        for (int index = 0; index < items.size(); index++) {
            JSONObject item = items.getJSONObject(index);
            String value = item == null ? "" : trimToEmpty(item.getString(fieldName));
            if (StringUtils.isNotBlank(value)) {
                values.add(value);
            }
        }
        return values.size();
    }

    private String joinDocumentIds(List<AiKnowledgeDocument> documents) {
        List<String> ids = new ArrayList<>();
        for (AiKnowledgeDocument document : documents) {
            if (document != null && document.getDocumentId() != null) {
                ids.add(String.valueOf(document.getDocumentId()));
            }
        }
        return String.join(",", ids);
    }

    private String buildMergedSummary(List<AiKnowledgeDocument> includedDocuments, int totalDocumentCount, MergeBuildResult mergeResult) {
        int includedDocumentCount = includedDocuments == null ? 0 : includedDocuments.size();
        int skippedCount = Math.max(totalDocumentCount - includedDocumentCount, 0);
        int officialCount = countDocumentsBySourceOrigin(includedDocuments, "OFFICIAL");
        int customCount = Math.max(includedDocumentCount - officialCount, 0);
        List<String> officialAppVersions = collectOfficialAppVersions(includedDocuments);
        StringBuilder summary = new StringBuilder();
        summary.append("基于 ").append(includedDocumentCount).append(" 个已启用且解析成功的文档构建草稿版本");
        summary.append("（官方 ").append(officialCount).append(" 个 / 客户 ").append(customCount).append(" 个）");
        summary.append("，合并后共 ").append(mergeResult.mergedItemCount()).append(" 条记录");
        if (!officialAppVersions.isEmpty()) {
            summary.append("，官方版本：").append(String.join("、", officialAppVersions));
        }
        if (mergeResult.overrideCount() > 0) {
            summary.append("，发生 ").append(mergeResult.overrideCount()).append(" 次覆盖");
        }
        if (mergeResult.conflictCount() > 0) {
            summary.append("，检测到 ").append(mergeResult.conflictCount()).append(" 次冲突覆盖");
        }
        if (skippedCount > 0) {
            summary.append("，另有 ").append(skippedCount).append(" 个文档因未启用或未解析成功未参与构建");
        }
        return summary.toString();
    }

    private JSONObject buildSourceOriginSummary(List<AiKnowledgeDocument> documents) {
        JSONObject summary = new JSONObject();
        summary.put("officialCount", countDocumentsBySourceOrigin(documents, "OFFICIAL"));
        summary.put("customCount", countDocumentsBySourceOrigin(documents, "CUSTOM"));
        return summary;
    }

    private int countDocumentsBySourceOrigin(List<AiKnowledgeDocument> documents, String sourceOrigin) {
        if (documents == null || documents.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (AiKnowledgeDocument document : documents) {
            if (document != null && sourceOrigin.equalsIgnoreCase(trimToEmpty(document.getSourceOrigin()))) {
                count++;
            }
        }
        return count;
    }

    private List<String> collectOfficialAppVersions(List<AiKnowledgeDocument> documents) {
        LinkedHashSet<String> versions = new LinkedHashSet<>();
        if (documents == null || documents.isEmpty()) {
            return new ArrayList<>();
        }
        for (AiKnowledgeDocument document : documents) {
            if (document == null) {
                continue;
            }
            if (!"OFFICIAL".equalsIgnoreCase(trimToEmpty(document.getSourceOrigin()))) {
                continue;
            }
            String appVersion = trimToEmpty(document.getAppVersion());
            if (StringUtils.isNotBlank(appVersion)) {
                versions.add(appVersion);
            }
        }
        return new ArrayList<>(versions);
    }

    private int compareNullableInteger(Integer left, Integer right) {
        if (left == null && right == null) {
            return 0;
        }
        if (left == null) {
            return 1;
        }
        if (right == null) {
            return -1;
        }
        return Integer.compare(left, right);
    }

    private int compareNullableDateDesc(Date left, Date right) {
        if (left == null && right == null) {
            return 0;
        }
        if (left == null) {
            return 1;
        }
        if (right == null) {
            return -1;
        }
        return right.compareTo(left);
    }

    private String saveMergedSnapshotFile(AiKnowledgeBase knowledgeBase, String versionNo, JSONObject snapshot) {
        String safeCode = StringUtils.isBlank(knowledgeBase.getKbCode())
                ? "knowledge" : knowledgeBase.getKbCode().trim().toLowerCase();
        Path targetDir = Paths.get(RuoYiConfig.getProfile(), "ai", "knowledge", "snapshot", safeCode);
        try {
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(versionNo + ".json");
            Files.writeString(targetFile, JSON.toJSONString(snapshot), StandardCharsets.UTF_8);
            return targetFile.toString();
        } catch (IOException ex) {
            throw new ServiceException(message("ai.knowledge.version.snapshot.write.failed", ex.getMessage()));
        }
    }

    private String buildVersionNo() {
        return "VER_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String firstNonBlank(String... values) {
        if (values == null || values.length == 0) {
            return "";
        }
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                return value.trim();
            }
        }
        return "";
    }

    private void validKnowledgeVersionBeforeSave(AiKnowledgeVersion aiKnowledgeVersion) {
        if (aiKnowledgeVersion.getKnowledgeBaseId() == null) {
            throw new ServiceException(message("ai.knowledge.base.id.required"));
        }
        if (StringUtils.isBlank(aiKnowledgeVersion.getVersionNo())) {
            throw new ServiceException(message("ai.knowledge.version.no.required"));
        }
        Long count = baseMapper.selectCount(Wrappers.<AiKnowledgeVersion>lambdaQuery()
                .eq(AiKnowledgeVersion::getKnowledgeBaseId, aiKnowledgeVersion.getKnowledgeBaseId())
                .eq(AiKnowledgeVersion::getVersionNo, aiKnowledgeVersion.getVersionNo())
                .ne(aiKnowledgeVersion.getVersionId() != null, AiKnowledgeVersion::getVersionId, aiKnowledgeVersion.getVersionId()));
        if (count != null && count > 0) {
            throw new ServiceException(message("ai.knowledge.version.no.exists"));
        }
    }

    private void checkSingleActiveVersion(AiKnowledgeVersion aiKnowledgeVersion) {
        if (!isPublishedOrActive(aiKnowledgeVersion)) {
            return;
        }
        Long count = baseMapper.selectCount(Wrappers.<AiKnowledgeVersion>lambdaQuery()
                .eq(AiKnowledgeVersion::getKnowledgeBaseId, aiKnowledgeVersion.getKnowledgeBaseId())
                .ne(aiKnowledgeVersion.getVersionId() != null, AiKnowledgeVersion::getVersionId, aiKnowledgeVersion.getVersionId())
                .and(wrapper -> wrapper.eq(AiKnowledgeVersion::getIsActive, ACTIVE_YES)
                        .or()
                        .eq(AiKnowledgeVersion::getPublishStatus, STATUS_PUBLISHED)));
        if (count != null && count > 0) {
                throw new ServiceException(message("ai.knowledge.version.single.active.required"));
        }
    }

    private void syncKnowledgeBaseVersionSnapshot(AiKnowledgeVersion latest, AiKnowledgeVersion old) {
        if (latest == null) {
            return;
        }
        AiKnowledgeBase update = new AiKnowledgeBase();
        update.setKnowledgeBaseId(latest.getKnowledgeBaseId());
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        if (isPublishedOrActive(latest)) {
            update.setActiveVersionId(latest.getVersionId());
            update.setPublishStatus(latest.getPublishStatus());
            aiKnowledgeBaseMapper.updateById(update);
            return;
        }
        if (old != null && isPublishedOrActive(old)) {
            AiKnowledgeBase knowledgeBase = aiKnowledgeBaseMapper.selectById(latest.getKnowledgeBaseId());
            if (knowledgeBase != null && Objects.equals(knowledgeBase.getActiveVersionId(), latest.getVersionId())) {
                update.setActiveVersionId(null);
                update.setPublishStatus(STATUS_DRAFT);
                aiKnowledgeBaseMapper.updateById(update);
            }
        }
    }

    private int activateKnowledgeVersion(Long versionId, boolean rollback) {
        if (versionId == null) {
            throw new ServiceException(message("ai.knowledge.version.id.required"));
        }
        AiKnowledgeVersion targetVersion = baseMapper.selectById(versionId);
        if (targetVersion == null) {
            throw new ServiceException(message("ai.knowledge.version.not.exists"));
        }
        if (StringUtils.isBlank(targetVersion.getSnapshotPath())) {
            throw new ServiceException(message("ai.knowledge.version.snapshot.required.publish"));
        }
        if ("FAILED".equalsIgnoreCase(targetVersion.getPublishStatus())) {
            throw new ServiceException(message("ai.knowledge.version.parse.failed.publish.denied"));
        }
        AiKnowledgeBase knowledgeBase = requireKnowledgeBase(targetVersion.getKnowledgeBaseId());
        if (!KNOWLEDGE_STATUS_ENABLED.equals(knowledgeBase.getStatus())) {
            throw new ServiceException(message("ai.knowledge.version.knowledge.disabled.publish"));
        }
        if (!rollback && isPublishedOrActive(targetVersion)) {
            throw new ServiceException(message("ai.knowledge.version.already.active"));
        }
        if (!rollback && STATUS_ARCHIVED.equalsIgnoreCase(targetVersion.getPublishStatus())) {
            throw new ServiceException(message("ai.knowledge.version.archived.rollback.required"));
        }
        if (rollback && !STATUS_ARCHIVED.equalsIgnoreCase(targetVersion.getPublishStatus())) {
            throw new ServiceException(message("ai.knowledge.version.rollback.archived.only"));
        }
        IAiSemanticRuntimeStore runtimeStore = requireSemanticRuntimeStoreIfNecessary(knowledgeBase, targetVersion);
        if (!rollback) {
            aiNl2SqlQualityGateService.validateBeforePublish(knowledgeBase, targetVersion);
        }
        List<AiKnowledgeVersion> versionList = baseMapper.selectList(Wrappers.<AiKnowledgeVersion>lambdaQuery()
                .eq(AiKnowledgeVersion::getKnowledgeBaseId, targetVersion.getKnowledgeBaseId()));
        AiKnowledgeVersion currentActiveVersion = versionList.stream()
                .filter(this::isPublishedOrActive)
                .filter(item -> !Objects.equals(item.getVersionId(), targetVersion.getVersionId()))
                .findFirst()
                .orElse(null);

        for (AiKnowledgeVersion version : versionList) {
            if (version == null || Objects.equals(version.getVersionId(), targetVersion.getVersionId())) {
                continue;
            }
            if (!isPublishedOrActive(version)) {
                continue;
            }
            AiKnowledgeVersion updateVersion = new AiKnowledgeVersion();
            updateVersion.setVersionId(version.getVersionId());
            updateVersion.setIsActive(ACTIVE_NO);
            updateVersion.setPublishStatus(STATUS_ARCHIVED);
            updateVersion.setUpdateBy(AiSecuritySupport.resolveUsername());
            updateVersion.setUpdateTime(AiSecuritySupport.now());
            baseMapper.updateById(updateVersion);
        }

        AiKnowledgeVersion updateTarget = new AiKnowledgeVersion();
        updateTarget.setVersionId(targetVersion.getVersionId());
        updateTarget.setPublishStatus(STATUS_PUBLISHED);
        updateTarget.setIsActive(ACTIVE_YES);
        updateTarget.setPublishTime(new Date());
        updateTarget.setPublishedBy(AiSecuritySupport.resolveUsername());
        updateTarget.setUpdateBy(AiSecuritySupport.resolveUsername());
        updateTarget.setUpdateTime(AiSecuritySupport.now());
        if (rollback && currentActiveVersion != null) {
            updateTarget.setRollbackFromVersion(currentActiveVersion.getVersionNo());
        }
        int rows = baseMapper.updateById(updateTarget);

        AiKnowledgeBase updateBase = new AiKnowledgeBase();
        updateBase.setKnowledgeBaseId(targetVersion.getKnowledgeBaseId());
        updateBase.setActiveVersionId(targetVersion.getVersionId());
        updateBase.setPublishStatus(STATUS_PUBLISHED);
        updateBase.setUpdateBy(AiSecuritySupport.resolveUsername());
        updateBase.setUpdateTime(AiSecuritySupport.now());
        aiKnowledgeBaseMapper.updateById(updateBase);
        targetVersion.setPublishStatus(STATUS_PUBLISHED);
        targetVersion.setIsActive(ACTIVE_YES);
        targetVersion.setPublishTime(updateTarget.getPublishTime());
        targetVersion.setPublishedBy(updateTarget.getPublishedBy());
        publishSemanticRuntimeIfNecessary(knowledgeBase, targetVersion, runtimeStore);
        return rows;
    }

    private boolean isPublishedOrActive(AiKnowledgeVersion aiKnowledgeVersion) {
        if (aiKnowledgeVersion == null) {
            return false;
        }
        return ACTIVE_YES.equals(aiKnowledgeVersion.getIsActive())
                || STATUS_PUBLISHED.equalsIgnoreCase(Objects.toString(aiKnowledgeVersion.getPublishStatus(), ""));
    }

    private AiKnowledgeBase requireKnowledgeBase(Long knowledgeBaseId) {
        AiKnowledgeBase knowledgeBase = aiKnowledgeBaseMapper.selectById(knowledgeBaseId);
        if (knowledgeBase == null) {
            throw new ServiceException(message("ai.knowledge.base.not.exists.or.deleted"));
        }
        return knowledgeBase;
    }

    private String resolveKnowledgeBaseVectorStoreType(AiKnowledgeBase knowledgeBase) {
        return StringUtils.isBlank(knowledgeBase.getVectorStoreType()) ? "REDIS" : knowledgeBase.getVectorStoreType();
    }

    private void publishSemanticRuntimeIfNecessary(AiKnowledgeBase knowledgeBase,
                                                   AiKnowledgeVersion version,
                                                   IAiSemanticRuntimeStore runtimeStore) {
        if (!isNl2SqlKnowledgeBase(knowledgeBase) || version == null) {
            return;
        }
        if (runtimeStore == null) {
            return;
        }
        runtimeStore.publishNl2SqlBundle(knowledgeBase, version,
                aiSemanticNormalizationService.listSemanticFieldsByVersion(knowledgeBase, version));
    }

    private void cleanupSemanticRuntimeIfNecessary(AiKnowledgeVersion version) {
        if (version == null || version.getKnowledgeBaseId() == null) {
            return;
        }
        AiKnowledgeBase knowledgeBase = aiKnowledgeBaseMapper.selectById(version.getKnowledgeBaseId());
        if (!isNl2SqlKnowledgeBase(knowledgeBase)) {
            return;
        }
        IAiSemanticRuntimeStore runtimeStore = resolveSemanticRuntimeStore(resolveRuntimeStoreType(knowledgeBase, version));
        if (runtimeStore == null) {
            return;
        }
        runtimeStore.deleteNl2SqlVersion(knowledgeBase, version);
    }

    private IAiSemanticRuntimeStore resolveSemanticRuntimeStore(String storeType) {
        if (StringUtils.isBlank(storeType) || aiSemanticRuntimeStores == null || aiSemanticRuntimeStores.isEmpty()) {
            return null;
        }
        for (IAiSemanticRuntimeStore runtimeStore : aiSemanticRuntimeStores) {
            if (runtimeStore != null && runtimeStore.supports(storeType)) {
                return runtimeStore;
            }
        }
        return null;
    }

    private IAiSemanticRuntimeStore requireSemanticRuntimeStoreIfNecessary(AiKnowledgeBase knowledgeBase,
                                                                           AiKnowledgeVersion version) {
        if (!isNl2SqlKnowledgeBase(knowledgeBase)) {
            return null;
        }
        String storeType = resolveRuntimeStoreType(knowledgeBase, version);
        IAiSemanticRuntimeStore runtimeStore = resolveSemanticRuntimeStore(storeType);
        if (runtimeStore == null) {
            throw new ServiceException(message("ai.knowledge.version.runtime.store.unsupported.publish", storeType));
        }
        return runtimeStore;
    }

    private String resolveRuntimeStoreType(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion version) {
        if (version != null && StringUtils.isNotBlank(version.getVectorStoreType())) {
            return version.getVectorStoreType().trim().toUpperCase();
        }
        return resolveKnowledgeBaseVectorStoreType(knowledgeBase).trim().toUpperCase();
    }

    private boolean isNl2SqlKnowledgeBase(AiKnowledgeBase knowledgeBase) {
        return knowledgeBase != null && "NL2SQL_SEMANTIC".equalsIgnoreCase(knowledgeBase.getKbType());
    }

    private static final class MergeBuildResult {

        private final String snapshotPath;
        private final int mergedItemCount;
        private final int overrideCount;
        private final int conflictCount;

        private MergeBuildResult(String snapshotPath, int mergedItemCount, int overrideCount, int conflictCount) {
            this.snapshotPath = snapshotPath;
            this.mergedItemCount = mergedItemCount;
            this.overrideCount = overrideCount;
            this.conflictCount = conflictCount;
        }

        private String snapshotPath() {
            return snapshotPath;
        }

        private int mergedItemCount() {
            return mergedItemCount;
        }

        private int overrideCount() {
            return overrideCount;
        }

        private int conflictCount() {
            return conflictCount;
        }
    }
}
