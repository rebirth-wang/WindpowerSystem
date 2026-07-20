package com.fastbee.ai.service.impl;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;

import com.fastbee.ai.model.dto.AiNl2SqlQueryRequest;
import com.fastbee.ai.model.enums.AiQueryMode;
import com.fastbee.ai.model.vo.AiHybridQueryResultVO;
import com.fastbee.ai.model.vo.AiModelRouteVO;
import com.fastbee.ai.model.vo.AiNl2SqlAuditTrailVO;
import com.fastbee.ai.model.vo.AiNl2SqlGenerateResultVO;
import com.fastbee.ai.model.vo.AiNl2SqlQueryResultVO;
import com.fastbee.ai.model.vo.AiNl2SqlStructuredResultVO;
import com.fastbee.ai.model.vo.AiQueryPlanVO;
import com.fastbee.ai.model.vo.AiRedisRealtimeQueryResultVO;
import com.fastbee.ai.model.vo.AiTsdbQueryResultVO;
import com.fastbee.ai.service.IAiHybridQueryService;
import com.fastbee.ai.service.IAiNl2SqlGenerateService;
import com.fastbee.ai.service.IAiNl2SqlQueryService;
import com.fastbee.ai.service.IAiNl2SqlWorkflowService;
import com.fastbee.ai.service.IAiQueryPlanService;
import com.fastbee.ai.service.IAiRedisRealtimeQueryService;
import com.fastbee.ai.service.IAiTsdbQueryService;

/**
 * AI 智能问数统一工作流。
 */
@Service
public class AiNl2SqlWorkflowServiceImpl implements IAiNl2SqlWorkflowService {

    @Resource
    private IAiNl2SqlGenerateService aiNl2SqlGenerateService;

    @Resource
    private IAiNl2SqlQueryService aiNl2SqlQueryService;

    @Resource
    private IAiQueryPlanService aiQueryPlanService;

    @Resource
    private IAiRedisRealtimeQueryService aiRedisRealtimeQueryService;

    @Resource
    private IAiTsdbQueryService aiTsdbQueryService;

    @Resource
    private IAiHybridQueryService aiHybridQueryService;

    @Override
    public AiNl2SqlGenerateResultVO generateAndExecute(String question, Integer rowLimit, AiModelRouteVO route) {
        AiQueryPlanVO queryPlan = aiQueryPlanService.buildPlan(question);
        AiQueryMode queryMode = AiQueryMode.fromCode(queryPlan.getQueryMode());
        return switch (queryMode) {
            case REDIS_VALUE -> buildRedisResult(question, queryPlan);
            case TSDB_QUERY -> buildTsdbResult(question, queryPlan);
            case HYBRID_PIPELINE -> buildHybridResult(question, queryPlan);
            case RDB_SQL -> buildRdbResult(question, rowLimit, route, queryPlan);
        };
    }

    private AiNl2SqlGenerateResultVO buildRdbResult(String question, Integer rowLimit, AiModelRouteVO route, AiQueryPlanVO queryPlan) {
        AiNl2SqlStructuredResultVO generationResult = aiNl2SqlGenerateService.generateStructured(question, route);

        AiNl2SqlQueryRequest queryRequest = new AiNl2SqlQueryRequest();
        queryRequest.setSqlText(generationResult.getSql());
        queryRequest.setRowLimit(rowLimit);
        AiNl2SqlQueryResultVO queryResult = aiNl2SqlQueryService.executeReadOnlyQuery(queryRequest);

        AiNl2SqlGenerateResultVO result = new AiNl2SqlGenerateResultVO();
        result.setQuestion(question);
        result.setQueryMode(AiQueryMode.RDB_SQL.name());
        result.setQueryPlan(queryPlan);
        result.setGeneratedSql(generationResult.getSql());
        result.setSummary(generationResult.getSummary());
        result.setConfidence(generationResult.getConfidence());
        result.setTables(generationResult.getTables());
        result.setModelResponse(generationResult.getModelResponse());
        result.setStructuredPayload(generationResult.getStructuredPayload());
        result.setParseStatus(generationResult.getParseStatus());
        result.setParseErrorCode(generationResult.getParseErrorCode());
        result.setParseErrorMessage(generationResult.getParseErrorMessage());
        result.setStructuredOutput(generationResult.getStructuredOutput());
        result.setGenerationResult(generationResult);
        result.setQueryResult(queryResult);
        result.setAuditTrail(buildRdbAuditTrail(generationResult, queryResult, queryPlan));
        return result;
    }

    private AiNl2SqlGenerateResultVO buildRedisResult(String question, AiQueryPlanVO queryPlan) {
        AiRedisRealtimeQueryResultVO realtimeResult = aiRedisRealtimeQueryService.queryCurrentValue(question);
        AiNl2SqlGenerateResultVO result = new AiNl2SqlGenerateResultVO();
        result.setQuestion(question);
        result.setQueryMode(AiQueryMode.REDIS_VALUE.name());
        result.setQueryPlan(queryPlan);
        result.setSummary(realtimeResult.getSummary());
        result.setRealtimeResult(realtimeResult);
        result.setAuditTrail(buildRedisAuditTrail(question, queryPlan, realtimeResult));
        return result;
    }

    private AiNl2SqlGenerateResultVO buildTsdbResult(String question, AiQueryPlanVO queryPlan) {
        AiTsdbQueryResultVO tsdbResult = aiTsdbQueryService.query(question);
        AiNl2SqlGenerateResultVO result = new AiNl2SqlGenerateResultVO();
        result.setQuestion(question);
        result.setQueryMode(AiQueryMode.TSDB_QUERY.name());
        result.setQueryPlan(queryPlan);
        result.setSummary(tsdbResult.getSummary());
        result.setTsdbResult(tsdbResult);
        result.setAuditTrail(buildTsdbAuditTrail(question, queryPlan, tsdbResult));
        return result;
    }

    private AiNl2SqlGenerateResultVO buildHybridResult(String question, AiQueryPlanVO queryPlan) {
        AiHybridQueryResultVO hybridResult = aiHybridQueryService.query(question);
        AiNl2SqlGenerateResultVO result = new AiNl2SqlGenerateResultVO();
        result.setQuestion(question);
        result.setQueryMode(AiQueryMode.HYBRID_PIPELINE.name());
        result.setQueryPlan(queryPlan);
        result.setSummary(hybridResult.getSummary());
        result.setHybridResult(hybridResult);
        result.setAuditTrail(buildHybridAuditTrail(question, queryPlan, hybridResult));
        return result;
    }

    private AiNl2SqlAuditTrailVO buildRdbAuditTrail(AiNl2SqlStructuredResultVO generationResult,
                                                    AiNl2SqlQueryResultVO queryResult,
                                                    AiQueryPlanVO queryPlan) {
        AiNl2SqlAuditTrailVO auditTrail = new AiNl2SqlAuditTrailVO();
        auditTrail.setQuestion(generationResult.getQuestion());
        auditTrail.setRawModelResponse(generationResult.getModelResponse());
        auditTrail.setStructuredPayload(resolveAuditPayload(generationResult));
        auditTrail.setExecutedSql(queryResult == null ? generationResult.getSql() : queryResult.getExecutedSql());
        auditTrail.setExecutionStatus(queryResult == null ? "GENERATED" : "EXECUTED");
        auditTrail.setFailedReason(generationResult.getParseErrorMessage());
        applyPlanAuditFields(auditTrail, queryPlan);
        return auditTrail;
    }

    private AiNl2SqlAuditTrailVO buildRedisAuditTrail(String question, AiQueryPlanVO queryPlan, AiRedisRealtimeQueryResultVO result) {
        AiNl2SqlAuditTrailVO auditTrail = new AiNl2SqlAuditTrailVO();
        auditTrail.setQuestion(question);
        auditTrail.setRawModelResponse(null);
        auditTrail.setStructuredPayload(queryPlan == null ? null : JSONObject.toJSONString(queryPlan));
        auditTrail.setExecutedSql(null);
        auditTrail.setExecutionStatus("EXECUTED_REDIS");
        auditTrail.setFailedReason(result == null ? null : result.getSummary());
        applyPlanAuditFields(auditTrail, queryPlan);
        if (result != null) {
            applyTargetAuditFields(auditTrail, result.getDeviceId(), result.getSerialNumber(), result.getDeviceName(),
                    result.getProductId(), result.getProductName(), result.getIdentifier(), result.getPermissionChecks());
        }
        return auditTrail;
    }

    private AiNl2SqlAuditTrailVO buildTsdbAuditTrail(String question, AiQueryPlanVO queryPlan, AiTsdbQueryResultVO result) {
        AiNl2SqlAuditTrailVO auditTrail = new AiNl2SqlAuditTrailVO();
        auditTrail.setQuestion(question);
        auditTrail.setRawModelResponse(null);
        auditTrail.setStructuredPayload(queryPlan == null ? null : JSONObject.toJSONString(queryPlan));
        auditTrail.setExecutedSql(null);
        auditTrail.setExecutionStatus("EXECUTED_TSDB");
        auditTrail.setFailedReason(result == null ? null : result.getSummary());
        applyPlanAuditFields(auditTrail, queryPlan);
        if (result != null) {
            applyTargetAuditFields(auditTrail, result.getDeviceId(), result.getSerialNumber(), result.getDeviceName(),
                    result.getProductId(), result.getProductName(), result.getIdentifier(), result.getPermissionChecks());
        }
        return auditTrail;
    }

    private AiNl2SqlAuditTrailVO buildHybridAuditTrail(String question, AiQueryPlanVO queryPlan, AiHybridQueryResultVO result) {
        AiNl2SqlAuditTrailVO auditTrail = new AiNl2SqlAuditTrailVO();
        auditTrail.setQuestion(question);
        auditTrail.setRawModelResponse(null);
        auditTrail.setStructuredPayload(queryPlan == null ? null : JSONObject.toJSONString(queryPlan));
        auditTrail.setExecutedSql(null);
        auditTrail.setExecutionStatus("EXECUTED_HYBRID");
        auditTrail.setFailedReason(result == null ? null : result.getSummary());
        applyPlanAuditFields(auditTrail, queryPlan);
        if (result != null) {
            auditTrail.setExecutionFallbackUsed(Boolean.TRUE.equals(result.getFallbackUsed()));
            auditTrail.setDegradedReason(Boolean.TRUE.equals(result.getFallbackUsed()) ? "Hybrid 执行过程中触发实时值回退" : null);
            applyTargetAuditFields(auditTrail, result.getDeviceId(), result.getSerialNumber(), result.getDeviceName(),
                    result.getProductId(), result.getProductName(), result.getIdentifier(), result.getPermissionChecks());
        }
        return auditTrail;
    }

    private void applyPlanAuditFields(AiNl2SqlAuditTrailVO auditTrail, AiQueryPlanVO queryPlan) {
        if (auditTrail == null || queryPlan == null) {
            return;
        }
        auditTrail.setQueryMode(queryPlan.getQueryMode());
        auditTrail.setRuntimeSource(queryPlan.getRuntimeSource());
        auditTrail.setPrimarySemantic(queryPlan.getPrimarySemantic());
        auditTrail.setKnowledgeBases(copyList(queryPlan.getKnowledgeBases()));
        auditTrail.setVersionNos(copyList(queryPlan.getVersionNos()));
        auditTrail.setMatchedFieldKeys(copyList(queryPlan.getMatchedFieldKeys()));
        auditTrail.setRuntimeFieldKeys(copyList(queryPlan.getRuntimeFieldKeys()));
        auditTrail.setCandidateIdentifiers(copyList(queryPlan.getCandidateIdentifiers()));
        auditTrail.setSemanticFallbackUsed(Boolean.TRUE.equals(queryPlan.getFallbackUsed()));
        auditTrail.setRouteReason(queryPlan.getReason());
    }

    private void applyTargetAuditFields(AiNl2SqlAuditTrailVO auditTrail,
                                        Long deviceId,
                                        String serialNumber,
                                        String deviceName,
                                        Long productId,
                                        String productName,
                                        String identifier,
                                        java.util.List<String> permissionChecks) {
        if (auditTrail == null) {
            return;
        }
        auditTrail.setDeviceId(deviceId);
        auditTrail.setSerialNumber(serialNumber);
        auditTrail.setDeviceName(deviceName);
        auditTrail.setProductId(productId);
        auditTrail.setProductName(productName);
        auditTrail.setIdentifier(identifier);
        auditTrail.setPermissionChecks(copyList(permissionChecks));
    }

    private java.util.List<String> copyList(java.util.List<String> source) {
        return source == null ? new java.util.ArrayList<>() : new java.util.ArrayList<>(source);
    }

    private String resolveAuditPayload(AiNl2SqlStructuredResultVO generationResult) {
        if (generationResult.getStructuredPayload() != null) {
            return generationResult.getStructuredPayload();
        }
        return JSONObject.toJSONString(generationResult);
    }
}
