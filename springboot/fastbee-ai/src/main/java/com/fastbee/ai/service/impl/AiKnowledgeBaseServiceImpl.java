package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastbee.ai.convert.AiKnowledgeBaseConvert;
import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeDocument;
import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.mapper.AiKnowledgeBaseMapper;
import com.fastbee.ai.mapper.AiKnowledgeDocumentMapper;
import com.fastbee.ai.mapper.AiKnowledgeVersionMapper;
import com.fastbee.ai.model.vo.AiKnowledgeBaseVO;
import com.fastbee.ai.service.IAiKnowledgeBaseService;
import com.fastbee.ai.service.IAiSemanticRuntimeStore;
import com.fastbee.ai.support.AiSecuritySupport;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 知识库服务实现。
 */
@Service
public class AiKnowledgeBaseServiceImpl extends ServiceImpl<AiKnowledgeBaseMapper, AiKnowledgeBase> implements IAiKnowledgeBaseService {

    private static final String KB_TYPE_PLATFORM_DOC = "PLATFORM_DOC";
    private static final String KB_TYPE_NL2SQL = "NL2SQL_SEMANTIC";
    private static final String KB_TYPE_PROTOCOL = "PROTOCOL_SPEC";
    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_ENABLED = "0";
    private static final String STATUS_DISABLED = "1";
    private static final String DEFAULT_OPERATOR = "system";
    private static final String DEFAULT_VECTOR_STORE_TYPE = "REDIS";

    @Resource
    private AiKnowledgeDocumentMapper aiKnowledgeDocumentMapper;

    @Resource
    private AiKnowledgeVersionMapper aiKnowledgeVersionMapper;

    @Autowired(required = false)
    private List<IAiSemanticRuntimeStore> aiSemanticRuntimeStores = Collections.emptyList();

    @Override
    public List<AiKnowledgeBase> listAiKnowledgeBase(AiKnowledgeBase aiKnowledgeBase) {
        return listAndSortKnowledgeBases(aiKnowledgeBase);
    }

    @Override
    public List<AiKnowledgeBaseVO> listAiKnowledgeBaseVO(AiKnowledgeBase aiKnowledgeBase) {
        List<AiKnowledgeBaseVO> knowledgeBaseVOList = AiKnowledgeBaseConvert.INSTANCE
                .convertAiKnowledgeBaseVOList(listAiKnowledgeBase(aiKnowledgeBase));
        fillKnowledgeBaseSnapshotVO(knowledgeBaseVOList);
        return knowledgeBaseVOList;
    }

    @Override
    public AiKnowledgeBase selectAiKnowledgeBase(AiKnowledgeBase aiKnowledgeBase) {
        return this.getOne(buildQueryWrapper(aiKnowledgeBase), false);
    }

    @Override
    public AiKnowledgeBaseVO selectAiKnowledgeBaseVO(AiKnowledgeBase aiKnowledgeBase) {
        AiKnowledgeBase knowledgeBase = selectAiKnowledgeBase(aiKnowledgeBase);
        if (knowledgeBase == null) {
            return null;
        }
        AiKnowledgeBaseVO knowledgeBaseVO = AiKnowledgeBaseConvert.INSTANCE.convertAiKnowledgeBaseVO(knowledgeBase);
        fillKnowledgeBaseSnapshotVO(Collections.singletonList(knowledgeBaseVO));
        return knowledgeBaseVO;
    }

    @Override
    public Page<AiKnowledgeBase> pageAiKnowledgeBase(AiKnowledgeBase aiKnowledgeBase) {
        AiKnowledgeBase actualQuery = aiKnowledgeBase == null ? new AiKnowledgeBase() : aiKnowledgeBase;
        List<AiKnowledgeBase> knowledgeBaseList = listAndSortKnowledgeBases(actualQuery);
        return buildPage(actualQuery, knowledgeBaseList);
    }

    @Override
    public Page<AiKnowledgeBaseVO> pageAiKnowledgeBaseVO(AiKnowledgeBase aiKnowledgeBase) {
        Page<AiKnowledgeBase> knowledgeBasePage = pageAiKnowledgeBase(aiKnowledgeBase);
        Page<AiKnowledgeBaseVO> knowledgeBaseVOPage = AiKnowledgeBaseConvert.INSTANCE
                .convertAiKnowledgeBaseVOPage(knowledgeBasePage);
        if (knowledgeBaseVOPage != null && knowledgeBaseVOPage.getRecords() != null) {
            fillKnowledgeBaseSnapshotVO(knowledgeBaseVOPage.getRecords());
        }
        return knowledgeBaseVOPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertAiKnowledgeBase(AiKnowledgeBase aiKnowledgeBase) {
        validKnowledgeBaseBeforeSave(aiKnowledgeBase);
        SysUser user = AiSecuritySupport.getCurrentUser();
        aiKnowledgeBase.setTenantId(AiSecuritySupport.resolveTenantId(user));
        aiKnowledgeBase.setTenantName(AiSecuritySupport.resolveTenantName(user));
        aiKnowledgeBase.setCreateBy(AiSecuritySupport.resolveUsername());
        aiKnowledgeBase.setCreateTime(AiSecuritySupport.now());
        aiKnowledgeBase.setUpdateBy(AiSecuritySupport.resolveUsername());
        aiKnowledgeBase.setUpdateTime(AiSecuritySupport.now());
        aiKnowledgeBase.setKbType(normalizeKbType(aiKnowledgeBase.getKbType()));
        if (StringUtils.isBlank(aiKnowledgeBase.getVectorStoreType())) {
            aiKnowledgeBase.setVectorStoreType(DEFAULT_VECTOR_STORE_TYPE);
        }
        if (StringUtils.isBlank(aiKnowledgeBase.getPublishStatus())) {
            aiKnowledgeBase.setPublishStatus(STATUS_DRAFT);
        }
        if (StringUtils.isBlank(aiKnowledgeBase.getStatus())) {
            aiKnowledgeBase.setStatus(STATUS_ENABLED);
        }
        int rows = baseMapper.insert(aiKnowledgeBase);
        if (STATUS_ENABLED.equals(aiKnowledgeBase.getStatus())) {
            ensureSingleEnabledKnowledgeBase(aiKnowledgeBase);
            syncRuntimeOnKnowledgeBaseStatusChange(aiKnowledgeBase, STATUS_ENABLED);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAiKnowledgeBase(AiKnowledgeBase aiKnowledgeBase) {
        AiKnowledgeBase old = requireKnowledgeBase(aiKnowledgeBase.getKnowledgeBaseId());
        if (StringUtils.isBlank(aiKnowledgeBase.getKbCode())) {
            aiKnowledgeBase.setKbCode(old.getKbCode());
        }
        if (StringUtils.isBlank(aiKnowledgeBase.getKbName())) {
            aiKnowledgeBase.setKbName(old.getKbName());
        }
        if (StringUtils.isBlank(aiKnowledgeBase.getKbType())) {
            aiKnowledgeBase.setKbType(old.getKbType());
        }
        if (StringUtils.isBlank(aiKnowledgeBase.getVectorStoreType())) {
            aiKnowledgeBase.setVectorStoreType(StringUtils.isNotBlank(old.getVectorStoreType())
                    ? old.getVectorStoreType() : DEFAULT_VECTOR_STORE_TYPE);
        }
        if (StringUtils.isBlank(aiKnowledgeBase.getPublishStatus())) {
            aiKnowledgeBase.setPublishStatus(old.getPublishStatus());
        }
        if (StringUtils.isBlank(aiKnowledgeBase.getStatus())) {
            aiKnowledgeBase.setStatus(old.getStatus());
        }
        validKnowledgeBaseBeforeSave(aiKnowledgeBase);
        aiKnowledgeBase.setKbType(normalizeKbType(aiKnowledgeBase.getKbType()));
        aiKnowledgeBase.setUpdateBy(AiSecuritySupport.resolveUsername());
        aiKnowledgeBase.setUpdateTime(AiSecuritySupport.now());
        int rows = baseMapper.updateById(aiKnowledgeBase);
        if (!StringUtils.equalsIgnoreCase(old.getKbType(), aiKnowledgeBase.getKbType())
                && STATUS_ENABLED.equals(old.getStatus())) {
            syncRuntimeOnKnowledgeBaseStatusChange(old, STATUS_DISABLED);
        }
        if (!Objects.equals(old.getStatus(), aiKnowledgeBase.getStatus())) {
            syncRuntimeOnKnowledgeBaseStatusChange(old, aiKnowledgeBase.getStatus());
        }
        AiKnowledgeBase latest = requireKnowledgeBase(aiKnowledgeBase.getKnowledgeBaseId());
        if (STATUS_ENABLED.equals(latest.getStatus())) {
            ensureSingleEnabledKnowledgeBase(latest);
            syncRuntimeOnKnowledgeBaseStatusChange(latest, STATUS_ENABLED);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAiKnowledgeBaseStatus(AiKnowledgeBase aiKnowledgeBase) {
        AiKnowledgeBase old = requireKnowledgeBase(aiKnowledgeBase.getKnowledgeBaseId());
        if (StringUtils.isBlank(aiKnowledgeBase.getStatus())) {
            throw new ServiceException(message("ai.knowledge.base.status.required"));
        }
        AiKnowledgeBase update = new AiKnowledgeBase();
        update.setKnowledgeBaseId(old.getKnowledgeBaseId());
        update.setStatus(aiKnowledgeBase.getStatus());
        update.setUpdateBy(resolveOperator());
        update.setUpdateTime(AiSecuritySupport.now());
        int rows = baseMapper.updateById(update);
        syncRuntimeOnKnowledgeBaseStatusChange(old, aiKnowledgeBase.getStatus());
        AiKnowledgeBase latest = requireKnowledgeBase(aiKnowledgeBase.getKnowledgeBaseId());
        if (STATUS_ENABLED.equals(latest.getStatus())) {
            ensureSingleEnabledKnowledgeBase(latest);
            syncRuntimeOnKnowledgeBaseStatusChange(latest, STATUS_ENABLED);
        }
        return rows;
    }

    @Override
    public int deleteAiKnowledgeBaseByIds(Long[] knowledgeBaseIds) {
        Long documentCount = aiKnowledgeDocumentMapper.selectCount(Wrappers.<AiKnowledgeDocument>lambdaQuery()
                .in(AiKnowledgeDocument::getKnowledgeBaseId, Arrays.asList(knowledgeBaseIds)));
        if (documentCount != null && documentCount > 0) {
            throw new ServiceException(message("ai.knowledge.base.delete.document.exists"));
        }
        Long versionCount = aiKnowledgeVersionMapper.selectCount(Wrappers.<AiKnowledgeVersion>lambdaQuery()
                .in(AiKnowledgeVersion::getKnowledgeBaseId, Arrays.asList(knowledgeBaseIds)));
        if (versionCount != null && versionCount > 0) {
            throw new ServiceException(message("ai.knowledge.base.delete.version.exists"));
        }
        return baseMapper.deleteBatchIds(Arrays.asList(knowledgeBaseIds));
    }

    private List<AiKnowledgeBase> listAndSortKnowledgeBases(AiKnowledgeBase query) {
        return sortKnowledgeBases(baseMapper.selectList(buildQueryWrapper(query)));
    }

    private <T> Page<T> buildPage(AiKnowledgeBase query, List<T> recordList) {
        long pageNum = query.getPageNum() <= 0 ? 1L : query.getPageNum();
        long pageSize = query.getPageSize() <= 0 ? 10L : query.getPageSize();
        int fromIndex = (int) Math.max((pageNum - 1) * pageSize, 0);
        int toIndex = Math.min(fromIndex + (int) pageSize, recordList.size());
        List<T> records = fromIndex >= recordList.size()
                ? Collections.emptyList()
                : new ArrayList<>(recordList.subList(fromIndex, toIndex));
        Page<T> page = new Page<>(pageNum, pageSize, recordList.size());
        page.setRecords(records);
        return page;
    }

    private LambdaQueryWrapper<AiKnowledgeBase> buildQueryWrapper(AiKnowledgeBase query) {
        AiKnowledgeBase actualQuery = query == null ? new AiKnowledgeBase() : query;
        Map<String, Object> params = actualQuery.getParams() == null ? Collections.emptyMap() : actualQuery.getParams();
        LambdaQueryWrapper<AiKnowledgeBase> lqw = Wrappers.lambdaQuery();
        lqw.eq(actualQuery.getKnowledgeBaseId() != null, AiKnowledgeBase::getKnowledgeBaseId, actualQuery.getKnowledgeBaseId());
        lqw.like(StringUtils.isNotBlank(actualQuery.getKbCode()), AiKnowledgeBase::getKbCode, actualQuery.getKbCode());
        lqw.like(StringUtils.isNotBlank(actualQuery.getKbName()), AiKnowledgeBase::getKbName, actualQuery.getKbName());
        lqw.eq(StringUtils.isNotBlank(actualQuery.getKbType()), AiKnowledgeBase::getKbType, normalizeKbType(actualQuery.getKbType()));
        lqw.eq(StringUtils.isNotBlank(actualQuery.getVectorStoreType()), AiKnowledgeBase::getVectorStoreType, actualQuery.getVectorStoreType());
        lqw.eq(StringUtils.isNotBlank(actualQuery.getPublishStatus()), AiKnowledgeBase::getPublishStatus, actualQuery.getPublishStatus());
        lqw.eq(actualQuery.getActiveVersionId() != null, AiKnowledgeBase::getActiveVersionId, actualQuery.getActiveVersionId());
        lqw.eq(StringUtils.isNotBlank(actualQuery.getStatus()), AiKnowledgeBase::getStatus, actualQuery.getStatus());
        lqw.eq(actualQuery.getTenantId() != null, AiKnowledgeBase::getTenantId, actualQuery.getTenantId());
        lqw.like(StringUtils.isNotBlank(actualQuery.getTenantName()), AiKnowledgeBase::getTenantName, actualQuery.getTenantName());

        if (Objects.nonNull(params.get("beginTime")) && Objects.nonNull(params.get("endTime"))) {
            lqw.between(AiKnowledgeBase::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    private void validKnowledgeBaseBeforeSave(AiKnowledgeBase aiKnowledgeBase) {
        if (StringUtils.isBlank(aiKnowledgeBase.getKbCode())) {
            throw new ServiceException(message("ai.knowledge.base.code.required"));
        }
        if (StringUtils.isBlank(aiKnowledgeBase.getKbName())) {
            throw new ServiceException(message("ai.knowledge.base.name.required"));
        }
        String actualKbType = normalizeKbType(aiKnowledgeBase.getKbType());
        if (StringUtils.isBlank(actualKbType)) {
            throw new ServiceException(message("ai.knowledge.base.type.required"));
        }
        Long count = baseMapper.selectCount(Wrappers.<AiKnowledgeBase>lambdaQuery()
                .eq(AiKnowledgeBase::getKbCode, aiKnowledgeBase.getKbCode())
                .ne(aiKnowledgeBase.getKnowledgeBaseId() != null, AiKnowledgeBase::getKnowledgeBaseId, aiKnowledgeBase.getKnowledgeBaseId()));
        if (count != null && count > 0) {
            throw new ServiceException(message("ai.knowledge.base.code.exists"));
        }
    }

    private String normalizeKbType(String kbType) {
        if (StringUtils.isBlank(kbType)) {
            return KB_TYPE_PLATFORM_DOC;
        }
        String actualType = kbType.trim().toUpperCase();
        if ("GENERAL".equals(actualType) || "PLATFORM".equals(actualType) || "PLATFORM_KNOWLEDGE".equals(actualType)) {
            return KB_TYPE_PLATFORM_DOC;
        }
        if ("NL2SQL".equals(actualType)) {
            return KB_TYPE_NL2SQL;
        }
        if ("PROTOCOL".equals(actualType)) {
            return KB_TYPE_PROTOCOL;
        }
        return actualType;
    }

    private List<AiKnowledgeBase> sortKnowledgeBases(List<AiKnowledgeBase> knowledgeBaseList) {
        if (knowledgeBaseList == null || knowledgeBaseList.isEmpty()) {
            return Collections.emptyList();
        }
        List<AiKnowledgeBase> sortedKnowledgeBaseList = new ArrayList<>(knowledgeBaseList);
        sortedKnowledgeBaseList.sort(Comparator
                .comparingInt(this::resolveKnowledgeBaseOrder)
                .thenComparingInt(item -> STATUS_ENABLED.equals(item.getStatus()) ? 0 : 1)
                .thenComparing(AiKnowledgeBase::getUpdateTime, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(AiKnowledgeBase::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder())));
        return sortedKnowledgeBaseList;
    }

    private int resolveKnowledgeBaseOrder(AiKnowledgeBase aiKnowledgeBase) {
        String kbType = normalizeKbType(aiKnowledgeBase == null ? null : aiKnowledgeBase.getKbType());
        if (KB_TYPE_NL2SQL.equals(kbType)) {
            return 0;
        }
        if (KB_TYPE_PROTOCOL.equals(kbType)) {
            return 1;
        }
        if (KB_TYPE_PLATFORM_DOC.equals(kbType)) {
            return 2;
        }
        return Integer.MAX_VALUE;
    }

    private void fillKnowledgeBaseSnapshotVO(List<AiKnowledgeBaseVO> knowledgeBaseVOList) {
        if (knowledgeBaseVOList == null || knowledgeBaseVOList.isEmpty()) {
            return;
        }
        Set<Long> knowledgeBaseIds = knowledgeBaseVOList.stream()
                .filter(Objects::nonNull)
                .map(AiKnowledgeBaseVO::getKnowledgeBaseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (knowledgeBaseIds.isEmpty()) {
            return;
        }
        Map<Long, Integer> countMap = aiKnowledgeDocumentMapper.selectList(Wrappers.<AiKnowledgeDocument>lambdaQuery()
                        .select(AiKnowledgeDocument::getKnowledgeBaseId, AiKnowledgeDocument::getDocumentId)
                        .in(AiKnowledgeDocument::getKnowledgeBaseId, knowledgeBaseIds))
                .stream()
                .collect(Collectors.groupingBy(AiKnowledgeDocument::getKnowledgeBaseId, Collectors.summingInt(item -> 1)));
        Set<Long> activeVersionIds = knowledgeBaseVOList.stream()
                .filter(Objects::nonNull)
                .map(AiKnowledgeBaseVO::getActiveVersionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, AiKnowledgeVersion> activeVersionMap = activeVersionIds.isEmpty()
                ? Collections.emptyMap()
                : aiKnowledgeVersionMapper.selectList(Wrappers.<AiKnowledgeVersion>lambdaQuery()
                                .select(AiKnowledgeVersion::getVersionId, AiKnowledgeVersion::getVersionNo,
                                        AiKnowledgeVersion::getVectorStoreType)
                                .in(AiKnowledgeVersion::getVersionId, activeVersionIds))
                        .stream()
                        .collect(Collectors.toMap(AiKnowledgeVersion::getVersionId, item -> item,
                                (left, right) -> left));
        for (AiKnowledgeBaseVO knowledgeBaseVO : knowledgeBaseVOList) {
            if (knowledgeBaseVO != null) {
                knowledgeBaseVO.setDocumentCount(countMap.getOrDefault(knowledgeBaseVO.getKnowledgeBaseId(), 0));
                AiKnowledgeVersion activeVersion = activeVersionMap.get(knowledgeBaseVO.getActiveVersionId());
                knowledgeBaseVO.setActiveVersionNo(activeVersion == null ? "" : activeVersion.getVersionNo());
                knowledgeBaseVO.setActiveVectorStoreType(activeVersion == null
                        ? ""
                        : StringUtils.defaultIfBlank(activeVersion.getVectorStoreType(), knowledgeBaseVO.getVectorStoreType()));
            }
        }
    }

    private void syncRuntimeOnKnowledgeBaseStatusChange(AiKnowledgeBase knowledgeBase, String targetStatus) {
        if (!isNl2SqlKnowledgeBase(knowledgeBase)) {
            return;
        }
        AiKnowledgeVersion activeVersion = resolveActiveVersion(knowledgeBase);
        if (activeVersion == null) {
            return;
        }
        IAiSemanticRuntimeStore runtimeStore = resolveSemanticRuntimeStore(resolveRuntimeStoreType(knowledgeBase, activeVersion));
        if (runtimeStore == null) {
            return;
        }
        if (STATUS_ENABLED.equals(targetStatus)) {
            runtimeStore.restoreActiveNl2SqlBundle(knowledgeBase, activeVersion);
            return;
        }
        runtimeStore.clearActiveNl2SqlBundle(knowledgeBase);
    }

    private void ensureSingleEnabledKnowledgeBase(AiKnowledgeBase knowledgeBase) {
        if (knowledgeBase == null || StringUtils.isBlank(knowledgeBase.getKbType())
                || !STATUS_ENABLED.equals(knowledgeBase.getStatus())) {
            return;
        }
        List<AiKnowledgeBase> enabledPeers = baseMapper.selectList(Wrappers.<AiKnowledgeBase>lambdaQuery()
                .eq(AiKnowledgeBase::getKbType, normalizeKbType(knowledgeBase.getKbType()))
                .eq(AiKnowledgeBase::getStatus, STATUS_ENABLED)
                .ne(AiKnowledgeBase::getKnowledgeBaseId, knowledgeBase.getKnowledgeBaseId()));
        for (AiKnowledgeBase peer : enabledPeers) {
            if (peer == null) {
                continue;
            }
            AiKnowledgeBase update = new AiKnowledgeBase();
            update.setKnowledgeBaseId(peer.getKnowledgeBaseId());
            update.setStatus(STATUS_DISABLED);
            update.setUpdateBy(resolveOperator());
            update.setUpdateTime(AiSecuritySupport.now());
            baseMapper.updateById(update);
            syncRuntimeOnKnowledgeBaseStatusChange(peer, STATUS_DISABLED);
        }
    }

    private AiKnowledgeVersion resolveActiveVersion(AiKnowledgeBase knowledgeBase) {
        if (knowledgeBase == null || knowledgeBase.getActiveVersionId() == null) {
            return null;
        }
        return aiKnowledgeVersionMapper.selectById(knowledgeBase.getActiveVersionId());
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

    private String resolveRuntimeStoreType(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion version) {
        if (version != null && StringUtils.isNotBlank(version.getVectorStoreType())) {
            return version.getVectorStoreType().trim().toUpperCase();
        }
        if (knowledgeBase != null && StringUtils.isNotBlank(knowledgeBase.getVectorStoreType())) {
            return knowledgeBase.getVectorStoreType().trim().toUpperCase();
        }
        return DEFAULT_VECTOR_STORE_TYPE;
    }

    private boolean isNl2SqlKnowledgeBase(AiKnowledgeBase knowledgeBase) {
        return knowledgeBase != null && KB_TYPE_NL2SQL.equalsIgnoreCase(knowledgeBase.getKbType());
    }

    private AiKnowledgeBase requireKnowledgeBase(Long knowledgeBaseId) {
        if (knowledgeBaseId == null) {
            throw new ServiceException(message("ai.knowledge.base.id.required"));
        }
        AiKnowledgeBase aiKnowledgeBase = baseMapper.selectById(knowledgeBaseId);
        if (aiKnowledgeBase == null) {
            throw new ServiceException(message("ai.knowledge.base.not.exists.or.deleted"));
        }
        return aiKnowledgeBase;
    }

    private String resolveOperator() {
        String username = AiSecuritySupport.resolveUsername();
        return StringUtils.isBlank(username) ? DEFAULT_OPERATOR : username;
    }
}
