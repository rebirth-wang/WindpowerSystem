package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.ai.convert.AiProtocolAdaptationArtifactConvert;
import com.fastbee.ai.convert.AiProtocolAdaptationTaskConvert;
import com.fastbee.ai.convert.AiProtocolGenerationRecordConvert;
import com.fastbee.ai.domain.AiProtocolAdaptationArtifact;
import com.fastbee.ai.domain.AiProtocolAdaptationTask;
import com.fastbee.ai.domain.AiProtocolGenerationRecord;
import com.fastbee.ai.mapper.AiProtocolAdaptationArtifactMapper;
import com.fastbee.ai.mapper.AiProtocolAdaptationTaskMapper;
import com.fastbee.ai.mapper.AiProtocolGenerationRecordMapper;
import com.fastbee.ai.model.vo.AiProtocolAdaptationArtifactVO;
import com.fastbee.ai.model.vo.AiProtocolAdaptationAutoRunResultVO;
import com.fastbee.ai.model.vo.AiProtocolAdaptationTaskVO;
import com.fastbee.ai.model.vo.AiProtocolGenerationRecordVO;
import com.fastbee.ai.service.IAiProtocolAdaptationTaskService;
import com.fastbee.ai.service.IAiProtocolDslAiParseService;
import com.fastbee.ai.support.AiSecuritySupport;
import com.fastbee.ai.support.AiThingModelWorkbookSupport;
import com.fastbee.common.config.RuoYiConfig;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.file.FileUtils;

/**
 * AI 协议适配任务服务实现。
 */
@Service
public class AiProtocolAdaptationTaskServiceImpl
        extends ServiceImpl<AiProtocolAdaptationTaskMapper, AiProtocolAdaptationTask>
        implements IAiProtocolAdaptationTaskService {

    private static final String DEFAULT_TASK_STATUS = "DRAFT";
    private static final String DEFAULT_PARSE_STATUS = "PENDING";
    private static final String DEFAULT_VALIDATION_STATUS = "PENDING";
    private static final String DEFAULT_GENERATION_STATUS = "PENDING";
    private static final String DEFAULT_RISK_LEVEL = "LOW";
    private static final String STATUS_ENABLED = "0";
    private static final String TASK_STATUS_UPLOADED = "UPLOADED";
    private static final String TASK_STATUS_AI_PARSED = "AI_PARSED";
    private static final String TASK_STATUS_WORKBOOK_EXPORTED = "WORKBOOK_EXPORTED";
    private static final String TASK_STATUS_REVIEW_IMPORTED = "REVIEW_IMPORTED";
    private static final String TASK_STATUS_VALIDATED = "VALIDATED";
    private static final String TASK_STATUS_GENERATED = "GENERATED";
    private static final String TASK_STATUS_CONFIRMED = "CONFIRMED";
    private static final String TASK_STATUS_FAILED = "FAILED";
    private static final String PARSE_STATUS_PARSING = "PARSING";
    private static final String PARSE_STATUS_SUCCESS = "SUCCESS";
    private static final String PARSE_STATUS_FAILED = "FAILED";
    private static final String VALIDATION_STATUS_PASSED = "PASSED";
    private static final String VALIDATION_STATUS_WARNING = "WARNING";
    private static final String VALIDATION_STATUS_BLOCKED = "BLOCKED";
    private static final String GENERATION_STATUS_GENERATING = "GENERATING";
    private static final String GENERATION_STATUS_SUCCESS = "SUCCESS";
    private static final String GENERATION_STATUS_FAILED = "FAILED";
    private static final String COMPILE_STATUS_PENDING = "PENDING";
    private static final String COMPILE_STATUS_STATIC_PASSED = "STATIC_PASSED";
    private static final String COMPILE_STATUS_STATIC_WARNING = "STATIC_WARNING";
    private static final String COMPILE_STATUS_STATIC_FAILED = "STATIC_FAILED";
    private static final String AUTO_RUN_STATUS_COMPLETED = "COMPLETED";
    private static final String AUTO_RUN_STATUS_COMPLETED_WITH_WARNINGS = "COMPLETED_WITH_WARNINGS";
    private static final String AUTO_RUN_STATUS_NEED_REVIEW = "NEED_REVIEW";
    private static final String AUTO_RUN_STATUS_FAILED = "FAILED";
    private static final String AUTO_RUN_STAGE_DOCUMENT_PARSE = "DOCUMENT_PARSE";
    private static final String AUTO_RUN_STAGE_QUALITY_GATE = "QUALITY_GATE";
    private static final String AUTO_RUN_STAGE_CODE_GENERATION = "CODE_GENERATION";
    private static final String AUTO_RUN_STAGE_STATIC_VERIFY = "STATIC_VERIFY";
    private static final String FLOW_STATUS_PROCESSING = "PROCESSING";
    private static final String FLOW_STATUS_NEED_REVIEW = "NEED_REVIEW";
    private static final String FLOW_STATUS_PACKAGE_READY = "PACKAGE_READY";
    private static final String FLOW_STATUS_CONFIRMED = "CONFIRMED";
    private static final String FLOW_STATUS_FAILED = "FAILED";
    private static final String SAMPLE_REPLAY_STATUS_PASSED = "PASSED";
    private static final String SAMPLE_REPLAY_STATUS_WARNING = "WARNING";
    private static final String SAMPLE_REPLAY_STATUS_FAILED = "FAILED";
    private static final String ARTIFACT_TYPE_SOURCE_DOCUMENT = "SOURCE_DOCUMENT";
    private static final String ARTIFACT_TYPE_EXTRACTED_TEXT = "EXTRACTED_TEXT";
    private static final String ARTIFACT_TYPE_AI_DSL_DRAFT = "AI_DSL_DRAFT";
    private static final String ARTIFACT_TYPE_VALIDATION_REPORT = "VALIDATION_REPORT";
    private static final String ARTIFACT_TYPE_ENTERPRISE_WORKBOOK = "ENTERPRISE_WORKBOOK";
    private static final String ARTIFACT_TYPE_REVIEW_WORKBOOK = "REVIEW_WORKBOOK";
    private static final String ARTIFACT_STATUS_READY = "READY";
    private static final String SOURCE_TYPE_CUSTOM_UPLOAD = "CUSTOM_UPLOAD";
    private static final String SOURCE_TYPE_SYSTEM_PARSE = "SYSTEM_PARSE";
    private static final String SOURCE_TYPE_SYSTEM_GENERATED = "SYSTEM_GENERATED";
    private static final String RISK_LEVEL_MEDIUM = "MEDIUM";
    private static final String RISK_LEVEL_HIGH = "HIGH";
    private static final String RISK_LEVEL_BLOCKER = "BLOCKER";
    private static final int MAX_EXTRACTED_TEXT_LENGTH = 200000;
    private static final int MAX_EXCEL_ROWS_PER_SHEET = 1000;
    private static final long MAX_ARTIFACT_PREVIEW_BYTES = 1024 * 1024;
    private static final Set<String> UPLOAD_ARTIFACT_TYPES = Set.of(
            ARTIFACT_TYPE_SOURCE_DOCUMENT,
            ARTIFACT_TYPE_ENTERPRISE_WORKBOOK,
            ARTIFACT_TYPE_REVIEW_WORKBOOK
    );
    private static final Set<String> SOURCE_DOCUMENT_SUFFIXES = Set.of(
            ".pdf", ".doc", ".docx", ".txt", ".md", ".xls", ".xlsx", ".csv", ".json"
    );
    private static final Set<String> WORKBOOK_SUFFIXES = Set.of(".xls", ".xlsx");
    private static final String SHEET_INSTRUCTION = "填写说明";
    private static final String SHEET_PROTOCOL = "协议总览";
    private static final String SHEET_MESSAGE_TYPES = "报文类型";
    private static final String SHEET_FIELDS = "字段定义";
    private static final String SHEET_CODEC_RULES = "编解码规则";
    private static final String SHEET_THING_MODEL = "物模型映射";
    private static final String SHEET_SAMPLE_FRAMES = "样例报文";
    private static final String SHEET_GENERATION_STRATEGY = "生成策略";
    private static final String SHEET_QUALITY_ISSUES = "质量问题";
    private static final List<String> THING_MODEL_TEMPLATE_KEYS = List.of(
            "modelName", "modelName_en_US", "identifier", "datatype", "formula", "modelOrder",
            "unit", "limitValue", "typeStr", "isChartStr", "isMonitorStr", "isHistoryStr",
            "isReadonlyStr", "isSharePermStr"
    );
    private static final List<String> THING_MODEL_TEMPLATE_HEADERS = List.of(
            "物模型名称", "英文物模型名称", "标识符", "数据类型", "计算公式", "排序值",
            "单位", "有效值范围", "模型类别", "是否图表展示", "是否实时监测", "是否历史存储",
            "是否只读数据", "是否设备分享权限"
    );

    @Resource
    private AiProtocolAdaptationArtifactMapper aiProtocolAdaptationArtifactMapper;

    @Resource
    private AiProtocolGenerationRecordMapper aiProtocolGenerationRecordMapper;

    @Resource
    private IAiProtocolDslAiParseService aiProtocolDslAiParseService;

    @Resource
    private AiThingModelWorkbookSupport aiThingModelWorkbookSupport;

    @Override
    public List<AiProtocolAdaptationTask> listAiProtocolAdaptationTask(AiProtocolAdaptationTask task) {
        LambdaQueryWrapper<AiProtocolAdaptationTask> lqw = buildQueryWrapper(task);
        lqw.orderByDesc(AiProtocolAdaptationTask::getUpdateTime)
                .orderByDesc(AiProtocolAdaptationTask::getCreateTime);
        return baseMapper.selectList(lqw);
    }

    @Override
    public Page<AiProtocolAdaptationTaskVO> pageAiProtocolAdaptationTaskVO(AiProtocolAdaptationTask task) {
        AiProtocolAdaptationTask actualQuery = task == null ? new AiProtocolAdaptationTask() : task;
        LambdaQueryWrapper<AiProtocolAdaptationTask> lqw = buildQueryWrapper(actualQuery);
        lqw.orderByDesc(AiProtocolAdaptationTask::getUpdateTime)
                .orderByDesc(AiProtocolAdaptationTask::getCreateTime);
        Page<AiProtocolAdaptationTask> page = baseMapper.selectPage(
                new Page<>(actualQuery.getPageNum(), actualQuery.getPageSize()), lqw);
        Page<AiProtocolAdaptationTaskVO> voPage = AiProtocolAdaptationTaskConvert.INSTANCE
                .convertAiProtocolAdaptationTaskVOPage(page);
        fillTaskStatistics(voPage.getRecords());
        return voPage;
    }

    @Override
    public AiProtocolAdaptationTaskVO selectAiProtocolAdaptationTaskVO(Long taskId) {
        AiProtocolAdaptationTask task = requireTask(taskId);
        AiProtocolAdaptationTaskVO vo = AiProtocolAdaptationTaskConvert.INSTANCE.convertAiProtocolAdaptationTaskVO(task);
        fillTaskStatistics(Collections.singletonList(vo));
        return vo;
    }

    @Override
    public int insertAiProtocolAdaptationTask(AiProtocolAdaptationTask task) {
        AiProtocolAdaptationTask actualTask = task == null ? new AiProtocolAdaptationTask() : task;
        validTaskBeforeSave(actualTask);
        SysUser user = AiSecuritySupport.getCurrentUser();
        actualTask.setTenantId(AiSecuritySupport.resolveTenantId(user));
        actualTask.setTenantName(AiSecuritySupport.resolveTenantName(user));
        actualTask.setTaskName(actualTask.getTaskName().trim());
        actualTask.setProtocolCode(trimToEmpty(actualTask.getProtocolCode()));
        actualTask.setProtocolName(trimToEmpty(actualTask.getProtocolName()));
        actualTask.setTaskStatus(defaultIfBlank(actualTask.getTaskStatus(), DEFAULT_TASK_STATUS));
        actualTask.setParseStatus(defaultIfBlank(actualTask.getParseStatus(), DEFAULT_PARSE_STATUS));
        actualTask.setValidationStatus(defaultIfBlank(actualTask.getValidationStatus(), DEFAULT_VALIDATION_STATUS));
        actualTask.setGenerationStatus(defaultIfBlank(actualTask.getGenerationStatus(), DEFAULT_GENERATION_STATUS));
        actualTask.setRiskLevel(defaultIfBlank(actualTask.getRiskLevel(), DEFAULT_RISK_LEVEL));
        actualTask.setStatus(defaultIfBlank(actualTask.getStatus(), STATUS_ENABLED));
        actualTask.setCreateBy(AiSecuritySupport.resolveUsername());
        actualTask.setCreateTime(AiSecuritySupport.now());
        actualTask.setUpdateBy(AiSecuritySupport.resolveUsername());
        actualTask.setUpdateTime(AiSecuritySupport.now());
        return baseMapper.insert(actualTask);
    }

    @Override
    public int updateAiProtocolAdaptationTask(AiProtocolAdaptationTask task) {
        if (task == null || task.getTaskId() == null) {
            throw new ServiceException(message("ai.protocol.adaptation.task.id.required"));
        }
        AiProtocolAdaptationTask old = requireTask(task.getTaskId());
        AiProtocolAdaptationTask update = new AiProtocolAdaptationTask();
        update.setTaskId(old.getTaskId());
        update.setTaskName(defaultIfBlank(task.getTaskName(), old.getTaskName()));
        update.setProtocolCode(task.getProtocolCode() == null ? old.getProtocolCode() : trimToEmpty(task.getProtocolCode()));
        update.setProtocolName(task.getProtocolName() == null ? old.getProtocolName() : trimToEmpty(task.getProtocolName()));
        update.setDslSnapshotPath(task.getDslSnapshotPath() == null ? old.getDslSnapshotPath() : trimToEmpty(task.getDslSnapshotPath()));
        update.setWorkbookPath(task.getWorkbookPath() == null ? old.getWorkbookPath() : trimToEmpty(task.getWorkbookPath()));
        update.setTaskStatus(defaultIfBlank(task.getTaskStatus(), old.getTaskStatus()));
        update.setParseStatus(defaultIfBlank(task.getParseStatus(), old.getParseStatus()));
        update.setValidationStatus(defaultIfBlank(task.getValidationStatus(), old.getValidationStatus()));
        update.setGenerationStatus(defaultIfBlank(task.getGenerationStatus(), old.getGenerationStatus()));
        update.setRiskLevel(defaultIfBlank(task.getRiskLevel(), old.getRiskLevel()));
        update.setErrorSummary(task.getErrorSummary() == null ? old.getErrorSummary() : trimToEmpty(task.getErrorSummary()));
        update.setStatus(defaultIfBlank(task.getStatus(), old.getStatus()));
        update.setRemark(task.getRemark() == null ? old.getRemark() : task.getRemark());
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        return baseMapper.updateById(update);
    }

    @Override
    public int updateAiProtocolAdaptationTaskStatus(AiProtocolAdaptationTask task) {
        if (task == null || task.getTaskId() == null) {
            throw new ServiceException(message("ai.protocol.adaptation.task.id.required"));
        }
        if (StringUtils.isBlank(task.getTaskStatus())) {
            throw new ServiceException(message("ai.protocol.adaptation.task.status.required"));
        }
        AiProtocolAdaptationTask old = requireTask(task.getTaskId());
        AiProtocolAdaptationTask update = new AiProtocolAdaptationTask();
        update.setTaskId(old.getTaskId());
        update.setTaskStatus(task.getTaskStatus().trim());
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        return baseMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAiProtocolAdaptationTaskByIds(Long[] taskIds) {
        if (taskIds == null || taskIds.length == 0) {
            return 0;
        }
        aiProtocolAdaptationArtifactMapper.delete(Wrappers.<AiProtocolAdaptationArtifact>lambdaQuery()
                .in(AiProtocolAdaptationArtifact::getTaskId, Arrays.asList(taskIds)));
        aiProtocolGenerationRecordMapper.delete(Wrappers.<AiProtocolGenerationRecord>lambdaQuery()
                .in(AiProtocolGenerationRecord::getTaskId, Arrays.asList(taskIds)));
        return baseMapper.deleteBatchIds(Arrays.asList(taskIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiProtocolAdaptationArtifactVO uploadTaskArtifact(Long taskId, String artifactType, MultipartFile file) throws Exception {
        AiProtocolAdaptationTask task = requireTask(taskId);
        String actualArtifactType = normalizeUploadArtifactType(artifactType);
        validateUploadFile(actualArtifactType, file);
        byte[] fileBytes = file.getBytes();
        String originalFilename = resolveOriginalFilename(actualArtifactType, file.getOriginalFilename());
        Path savedFile = saveArtifactFile(task, actualArtifactType, originalFilename, fileBytes);
        AiProtocolAdaptationArtifact artifact = buildUploadArtifact(task, actualArtifactType, originalFilename, savedFile, fileBytes);
        aiProtocolAdaptationArtifactMapper.insert(artifact);
        updateTaskAfterArtifactUploaded(task, actualArtifactType, savedFile);
        return AiProtocolAdaptationArtifactConvert.INSTANCE.convertAiProtocolAdaptationArtifactVO(artifact);
    }

    @Override
    public AiProtocolAdaptationTaskVO parseTaskDocument(Long taskId) {
        AiProtocolAdaptationTask task = requireTask(taskId);
        updateTaskParseStart(task.getTaskId());
        try {
            AiProtocolAdaptationArtifact sourceArtifact = requireLatestSourceDocument(task.getTaskId());
            Path sourceFile = requireArtifactPath(sourceArtifact);
            byte[] fileBytes = Files.readAllBytes(sourceFile);
            ExtractedDocument extractedDocument = extractDocument(sourceArtifact, sourceFile, fileBytes);

            JSONObject extractedSnapshot = buildExtractedTextSnapshot(task, sourceArtifact, extractedDocument);
            Path extractedSnapshotPath = saveJsonArtifactFile(task, ARTIFACT_TYPE_EXTRACTED_TEXT,
                    "extracted_text", extractedSnapshot);
            AiProtocolAdaptationArtifact extractedArtifact = buildGeneratedArtifact(task, ARTIFACT_TYPE_EXTRACTED_TEXT,
                    "文本抽取结果.json", extractedSnapshotPath, SOURCE_TYPE_SYSTEM_PARSE,
                    "已抽取文本 " + extractedDocument.textLength + " 字符，表格 " + extractedDocument.tableCount + " 个");
            aiProtocolAdaptationArtifactMapper.insert(extractedArtifact);

            JSONObject dslDraft = buildDslDraftSnapshot(task, sourceArtifact, extractedDocument, extractedSnapshotPath);
            Path dslDraftPath = saveJsonArtifactFile(task, ARTIFACT_TYPE_AI_DSL_DRAFT, "protocol_dsl_draft", dslDraft);
            AiProtocolAdaptationArtifact dslArtifact = buildGeneratedArtifact(task, ARTIFACT_TYPE_AI_DSL_DRAFT,
                    "协议DSL草稿骨架.json", dslDraftPath, SOURCE_TYPE_SYSTEM_GENERATED,
                    buildDslDraftArtifactSummary(dslDraft));
            aiProtocolAdaptationArtifactMapper.insert(dslArtifact);

            updateTaskParseSuccess(task.getTaskId(), dslDraftPath);
            return selectAiProtocolAdaptationTaskVO(task.getTaskId());
        } catch (Exception ex) {
            updateTaskParseFailure(task.getTaskId(), ex.getMessage());
            if (ex instanceof ServiceException) {
                throw (ServiceException) ex;
            }
            throw new ServiceException(message("ai.protocol.adaptation.parse.document.failed", ex.getMessage()));
        }
    }

    @Override
    public AiProtocolAdaptationTaskVO validateTaskDsl(Long taskId) {
        AiProtocolAdaptationTask task = requireTask(taskId);
        try {
            Path dslSnapshotPath = requireCurrentDslSnapshotPath(task);
            JSONObject dslSnapshot = JSON.parseObject(Files.readString(dslSnapshotPath, StandardCharsets.UTF_8));
            ValidationGateResult validationResult = buildDslValidationReport(task, dslSnapshotPath, dslSnapshot);

            Path reportPath = saveJsonArtifactFile(task, ARTIFACT_TYPE_VALIDATION_REPORT,
                    "validation_report", validationResult.report);
            AiProtocolAdaptationArtifact reportArtifact = buildGeneratedArtifact(task, ARTIFACT_TYPE_VALIDATION_REPORT,
                    "协议DSL质量门禁报告.json", reportPath, SOURCE_TYPE_SYSTEM_GENERATED, validationResult.summary);
            aiProtocolAdaptationArtifactMapper.insert(reportArtifact);

            updateTaskValidationResult(task.getTaskId(), validationResult);
            return selectAiProtocolAdaptationTaskVO(task.getTaskId());
        } catch (Exception ex) {
            updateTaskValidationFailure(task.getTaskId(), ex.getMessage());
            if (ex instanceof ServiceException) {
                throw (ServiceException) ex;
            }
            throw new ServiceException(message("ai.protocol.adaptation.dsl.validate.failed", ex.getMessage()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiProtocolAdaptationArtifactVO exportTaskWorkbook(Long taskId) {
        AiProtocolAdaptationTask task = requireTask(taskId);
        try {
            Path dslSnapshotPath = requireCurrentDslSnapshotPath(task);
            JSONObject dslSnapshot = JSON.parseObject(Files.readString(dslSnapshotPath, StandardCharsets.UTF_8));
            byte[] workbookBytes = buildEnterpriseWorkbookBytes(task, dslSnapshotPath, dslSnapshot);
            String workbookName = buildWorkbookFilename(task, "企业协议适配工作簿");
            Path workbookPath = saveArtifactFile(task, ARTIFACT_TYPE_ENTERPRISE_WORKBOOK, workbookName, workbookBytes);
            AiProtocolAdaptationArtifact artifact = buildGeneratedArtifact(task, ARTIFACT_TYPE_ENTERPRISE_WORKBOOK,
                    workbookName, workbookPath, SOURCE_TYPE_SYSTEM_GENERATED,
                    "已基于当前 DSL 快照导出企业协议适配工作簿，等待人工校对后导入");
            aiProtocolAdaptationArtifactMapper.insert(artifact);
            updateTaskWorkbookExported(task.getTaskId(), workbookPath);
            return AiProtocolAdaptationArtifactConvert.INSTANCE.convertAiProtocolAdaptationArtifactVO(artifact);
        } catch (Exception ex) {
            if (ex instanceof ServiceException) {
                throw (ServiceException) ex;
            }
            throw new ServiceException(message("ai.protocol.adaptation.workbook.export.failed", ex.getMessage()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiProtocolAdaptationTaskVO importTaskWorkbook(Long taskId, MultipartFile file) throws Exception {
        AiProtocolAdaptationTask task = requireTask(taskId);
        validateUploadFile(ARTIFACT_TYPE_REVIEW_WORKBOOK, file);
        byte[] fileBytes = file.getBytes();
        String originalFilename = resolveOriginalFilename(ARTIFACT_TYPE_REVIEW_WORKBOOK, file.getOriginalFilename());
        Path workbookPath = saveArtifactFile(task, ARTIFACT_TYPE_REVIEW_WORKBOOK, originalFilename, fileBytes);
        AiProtocolAdaptationArtifact workbookArtifact = buildUploadArtifact(task, ARTIFACT_TYPE_REVIEW_WORKBOOK,
                originalFilename, workbookPath, fileBytes);
        aiProtocolAdaptationArtifactMapper.insert(workbookArtifact);

        JSONObject reviewedDsl = buildDslSnapshotFromWorkbook(task, workbookPath, fileBytes);
        Path reviewedDslPath = saveJsonArtifactFile(task, ARTIFACT_TYPE_AI_DSL_DRAFT,
                "protocol_dsl_reviewed", reviewedDsl);
        AiProtocolAdaptationArtifact dslArtifact = buildGeneratedArtifact(task, ARTIFACT_TYPE_AI_DSL_DRAFT,
                "人工校对DSL候选.json", reviewedDslPath, SOURCE_TYPE_SYSTEM_GENERATED,
                "已从人工回填工作簿生成 DSL 候选快照，等待质量门禁复核");
        aiProtocolAdaptationArtifactMapper.insert(dslArtifact);

        updateTaskWorkbookImported(task.getTaskId(), workbookPath, reviewedDslPath);
        return continueAfterWorkbookImport(task.getTaskId());
    }

    private AiProtocolAdaptationTaskVO continueAfterWorkbookImport(Long taskId) {
        try {
            AiProtocolAdaptationTaskVO validatedTask = validateTaskDsl(taskId);
            if (VALIDATION_STATUS_BLOCKED.equals(defaultIfBlank(validatedTask.getValidationStatus(), DEFAULT_VALIDATION_STATUS))) {
                return validatedTask;
            }
            AiProtocolGenerationRecordVO generationRecord = generateCodePackage(taskId);
            if (generationRecord != null && generationRecord.getRecordId() != null) {
                verifyGenerationRecord(generationRecord.getRecordId());
            }
        } catch (Exception ignored) {
            // 校验、生成或静态验证失败时，前置方法已尽量回写任务状态；导入本身不回滚。
        }
        return selectAiProtocolAdaptationTaskVO(taskId);
    }

    @Override
    public AiProtocolGenerationRecordVO generateCodePackage(Long taskId) {
        AiProtocolAdaptationTask task = requireTask(taskId);
        requireCodeGenerationAllowed(task);
        AiProtocolGenerationRecord record = buildGeneratingRecord(task);
        aiProtocolGenerationRecordMapper.insert(record);
        updateTaskGenerationStart(task.getTaskId());
        try {
            Path dslSnapshotPath = requireCurrentDslSnapshotPath(task);
            JSONObject dslSnapshot = JSON.parseObject(Files.readString(dslSnapshotPath, StandardCharsets.UTF_8));
            CodePackageBuildResult buildResult = buildCodePackage(task, dslSnapshotPath, dslSnapshot);
            updateGenerationRecordSuccess(record.getRecordId(), buildResult);
            updateTaskGenerationSuccess(task.getTaskId());
            return selectGenerationRecordVO(record.getRecordId());
        } catch (Exception ex) {
            updateGenerationRecordFailure(record.getRecordId(), ex.getMessage());
            updateTaskGenerationFailure(task.getTaskId(), ex.getMessage());
            if (ex instanceof ServiceException) {
                throw (ServiceException) ex;
            }
            throw new ServiceException(message("ai.protocol.generation.package.generate.failed", ex.getMessage()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiProtocolGenerationRecordVO verifyGenerationRecord(Long recordId) {
        AiProtocolGenerationRecord record = requireGenerationRecord(recordId);
        if (!GENERATION_STATUS_SUCCESS.equals(defaultIfBlank(record.getGenerationStatus(), DEFAULT_GENERATION_STATUS))) {
            throw new ServiceException(message("ai.protocol.generation.static.verify.success.required"));
        }
        Path packagePath = resolveGenerationFilePath(record, "package");
        Path testReportPath = resolveGenerationFilePath(record, "testReport");
        try {
            CodePackageVerificationResult result = buildCodePackageVerificationReport(record, packagePath);
            byte[] reportBytes = JSON.toJSONString(result.report).getBytes(StandardCharsets.UTF_8);
            Files.write(testReportPath, reportBytes);
            replaceZipEntry(packagePath, "test-report.json", reportBytes);
            updateGenerationRecordVerification(record, result);
            return selectGenerationRecordVO(recordId);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(message("ai.protocol.generation.verify.failed", ex.getMessage()));
        }
    }

    @Override
    public void downloadGenerationFile(Long recordId, String fileType, HttpServletResponse response) {
        AiProtocolGenerationRecord record = requireGenerationRecord(recordId);
        Path filePath = resolveGenerationFilePath(record, fileType);
        try {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, filePath.getFileName().toString());
            FileUtils.writeBytes(filePath.toString(), response.getOutputStream());
        } catch (IOException ex) {
            throw new ServiceException(message("ai.protocol.generation.download.failed", ex.getMessage()));
        } catch (Exception ex) {
            throw new ServiceException(message("ai.protocol.generation.download.failed", ex.getMessage()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiProtocolGenerationRecordVO confirmGenerationRecord(Long recordId) {
        AiProtocolGenerationRecord record = requireGenerationRecord(recordId);
        if (!GENERATION_STATUS_SUCCESS.equals(defaultIfBlank(record.getGenerationStatus(), DEFAULT_GENERATION_STATUS))) {
            throw new ServiceException(message("ai.protocol.generation.manual.confirm.success.required"));
        }
        requireGenerationVerificationPassed(record);
        resolveGenerationFilePath(record, "package");
        if (StringUtils.isNotBlank(record.getFileManifestPath())) {
            resolveGenerationFilePath(record, "manifest");
        }
        if (StringUtils.isNotBlank(record.getTestReportPath())) {
            resolveGenerationFilePath(record, "testReport");
        }

        AiProtocolGenerationRecord update = new AiProtocolGenerationRecord();
        update.setRecordId(record.getRecordId());
        update.setConfirmBy(AiSecuritySupport.resolveUsername());
        update.setConfirmTime(AiSecuritySupport.now());
        update.setRemark(buildGenerationConfirmRemark(record));
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        aiProtocolGenerationRecordMapper.updateById(update);
        updateTaskGenerationConfirmed(record.getTaskId());
        return selectGenerationRecordVO(recordId);
    }

    @Override
    public AiProtocolAdaptationAutoRunResultVO autoRunTask(Long taskId) {
        requireTask(taskId);
        AiProtocolAdaptationAutoRunResultVO result = initAutoRunResult(taskId);
        String currentStage = AUTO_RUN_STAGE_DOCUMENT_PARSE;
        try {
            AiProtocolAdaptationTaskVO task = selectAiProtocolAdaptationTaskVO(taskId);
            if (StringUtils.isBlank(task.getDslSnapshotPath())
                    || !PARSE_STATUS_SUCCESS.equals(defaultIfBlank(task.getParseStatus(), DEFAULT_PARSE_STATUS))) {
                task = parseTaskDocument(taskId);
            }
            result.getCompletedStages().add(AUTO_RUN_STAGE_DOCUMENT_PARSE);

            currentStage = AUTO_RUN_STAGE_QUALITY_GATE;
            if (!VALIDATION_STATUS_PASSED.equals(defaultIfBlank(task.getValidationStatus(), DEFAULT_VALIDATION_STATUS))
                    && !VALIDATION_STATUS_WARNING.equals(defaultIfBlank(task.getValidationStatus(), DEFAULT_VALIDATION_STATUS))) {
                task = validateTaskDsl(taskId);
            }
            result.getCompletedStages().add(AUTO_RUN_STAGE_QUALITY_GATE);
            if (VALIDATION_STATUS_BLOCKED.equals(defaultIfBlank(task.getValidationStatus(), DEFAULT_VALIDATION_STATUS))) {
                return finishAutoRunNeedReview(result, currentStage,
                        message("ai.protocol.autorun.quality.blocked.summary"),
                        task, null, buildQualityGateReviewActions());
            }

            currentStage = AUTO_RUN_STAGE_CODE_GENERATION;
            AiProtocolGenerationRecordVO generationRecord = generateCodePackage(taskId);
            result.getCompletedStages().add(AUTO_RUN_STAGE_CODE_GENERATION);

            currentStage = AUTO_RUN_STAGE_STATIC_VERIFY;
            generationRecord = verifyGenerationRecord(generationRecord.getRecordId());
            result.getCompletedStages().add(AUTO_RUN_STAGE_STATIC_VERIFY);

            task = selectAiProtocolAdaptationTaskVO(taskId);
            return finishAutoRunByVerification(result, task, generationRecord);
        } catch (ServiceException ex) {
            AiProtocolAdaptationTaskVO task = safeSelectTaskVO(taskId);
            return finishAutoRunNeedReview(result, currentStage, ex.getMessage(), task, null,
                    buildStageReviewActions(currentStage));
        } catch (Exception ex) {
            AiProtocolAdaptationTaskVO task = safeSelectTaskVO(taskId);
            result.setRunStatus(AUTO_RUN_STATUS_FAILED);
            result.setCurrentStage(currentStage);
            result.setSummary(message("ai.protocol.autorun.failed", ex.getMessage()));
            result.setTask(task);
            result.setNextActions(buildStageReviewActions(currentStage));
            return result;
        }
    }

    private AiProtocolAdaptationAutoRunResultVO initAutoRunResult(Long taskId) {
        AiProtocolAdaptationAutoRunResultVO result = new AiProtocolAdaptationAutoRunResultVO();
        result.setTaskId(taskId);
        result.setRunStatus(AUTO_RUN_STATUS_FAILED);
        result.setCurrentStage(AUTO_RUN_STAGE_DOCUMENT_PARSE);
        result.setSummary(message("ai.protocol.autorun.started"));
        return result;
    }

    private AiProtocolAdaptationAutoRunResultVO finishAutoRunByVerification(AiProtocolAdaptationAutoRunResultVO result,
                                                                            AiProtocolAdaptationTaskVO task,
                                                                            AiProtocolGenerationRecordVO generationRecord) {
        result.setTask(task);
        result.setGenerationRecord(generationRecord);
        result.setCurrentStage(AUTO_RUN_STAGE_STATIC_VERIFY);
        String compileStatus = generationRecord == null
                ? COMPILE_STATUS_PENDING
                : defaultIfBlank(generationRecord.getCompileStatus(), COMPILE_STATUS_PENDING);
        if (COMPILE_STATUS_STATIC_PASSED.equals(compileStatus)) {
            result.setRunStatus(AUTO_RUN_STATUS_COMPLETED);
            result.setSummary(message("ai.protocol.autorun.static.passed.summary"));
            result.setNextActions(List.of(
                    message("ai.protocol.autorun.action.download.package"),
                    message("ai.protocol.autorun.action.download.report"),
                    message("ai.protocol.autorun.action.manual.confirm")
            ));
            return result;
        }
        if (COMPILE_STATUS_STATIC_WARNING.equals(compileStatus)) {
            result.setRunStatus(AUTO_RUN_STATUS_COMPLETED_WITH_WARNINGS);
            result.setSummary(message("ai.protocol.autorun.static.warning.summary"));
            result.setNextActions(List.of(
                    message("ai.protocol.autorun.action.review.report.warning"),
                    message("ai.protocol.autorun.action.supplement.samples.rules"),
                    message("ai.protocol.autorun.action.confirm.warning.accepted")
            ));
            return result;
        }
        result.setRunStatus(AUTO_RUN_STATUS_NEED_REVIEW);
        result.setSummary(message("ai.protocol.autorun.static.failed.summary"));
        result.setNextActions(List.of(
                message("ai.protocol.autorun.action.review.blockers"),
                message("ai.protocol.autorun.action.fix.hard.errors"),
                message("ai.protocol.autorun.action.supplement.samples.mapping")
        ));
        return result;
    }

    private AiProtocolAdaptationAutoRunResultVO finishAutoRunNeedReview(AiProtocolAdaptationAutoRunResultVO result,
                                                                        String currentStage,
                                                                        String summary,
                                                                        AiProtocolAdaptationTaskVO task,
                                                                        AiProtocolGenerationRecordVO generationRecord,
                                                                        List<String> nextActions) {
        result.setRunStatus(AUTO_RUN_STATUS_NEED_REVIEW);
        result.setCurrentStage(currentStage);
        result.setSummary(defaultIfBlank(summary, message("ai.protocol.autorun.need.review.summary")));
        result.setTask(task);
        result.setGenerationRecord(generationRecord);
        result.setNextActions(nextActions == null ? new ArrayList<>() : nextActions);
        return result;
    }

    private AiProtocolAdaptationTaskVO safeSelectTaskVO(Long taskId) {
        try {
            return selectAiProtocolAdaptationTaskVO(taskId);
        } catch (Exception ignored) {
            return null;
        }
    }

    private List<String> buildQualityGateReviewActions() {
        return List.of(
                message("ai.protocol.autorun.action.view.quality.blockers"),
                message("ai.protocol.autorun.action.complete.message.fields.rules"),
                message("ai.protocol.autorun.action.supplement.mapping.enum.samples"),
                message("ai.protocol.autorun.action.export.workbook.review")
        );
    }

    private List<String> buildStageReviewActions(String currentStage) {
        if (AUTO_RUN_STAGE_DOCUMENT_PARSE.equals(currentStage)) {
            return List.of(
                    message("ai.protocol.autorun.action.upload.original.document"),
                    message("ai.protocol.autorun.action.use.supported.format"),
                    message("ai.protocol.autorun.action.confirm.document.content")
            );
        }
        if (AUTO_RUN_STAGE_QUALITY_GATE.equals(currentStage)) {
            return buildQualityGateReviewActions();
        }
        if (AUTO_RUN_STAGE_CODE_GENERATION.equals(currentStage)) {
            return List.of(
                    message("ai.protocol.autorun.action.confirm.dsl.passed"),
                    message("ai.protocol.autorun.action.complete.generation.strategy"),
                    message("ai.protocol.autorun.action.rerun.generation")
            );
        }
        if (AUTO_RUN_STAGE_STATIC_VERIFY.equals(currentStage)) {
            return List.of(
                    message("ai.protocol.autorun.action.review.static.report"),
                    message("ai.protocol.autorun.action.fix.frame.field.errors"),
                    message("ai.protocol.autorun.action.supplement.samples.mapping"),
                    message("ai.protocol.autorun.action.rerun.generation")
            );
        }
        return List.of(
                message("ai.protocol.autorun.action.view.task.error"),
                message("ai.protocol.autorun.action.supplement.and.rerun")
        );
    }

    @Override
    public List<AiProtocolAdaptationArtifactVO> listTaskArtifacts(Long taskId) {
        requireTask(taskId);
        List<AiProtocolAdaptationArtifact> artifactList = aiProtocolAdaptationArtifactMapper.selectList(
                Wrappers.<AiProtocolAdaptationArtifact>lambdaQuery()
                        .eq(AiProtocolAdaptationArtifact::getTaskId, taskId)
                        .orderByDesc(AiProtocolAdaptationArtifact::getUpdateTime)
                        .orderByDesc(AiProtocolAdaptationArtifact::getCreateTime));
        return AiProtocolAdaptationArtifactConvert.INSTANCE.convertAiProtocolAdaptationArtifactVOList(artifactList);
    }

    @Override
    public void downloadTaskArtifact(Long artifactId, HttpServletResponse response) {
        AiProtocolAdaptationArtifact artifact = requireArtifact(artifactId);
        Path filePath = resolveArtifactFilePath(artifact);
        String downloadName = defaultIfBlank(artifact.getArtifactName(), filePath.getFileName().toString());
        try {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(filePath.toString(), response.getOutputStream());
        } catch (Exception ex) {
            throw new ServiceException(message("ai.protocol.artifact.download.failed", ex.getMessage()));
        }
    }

    @Override
    public Map<String, Object> readTaskArtifactContent(Long artifactId) {
        AiProtocolAdaptationArtifact artifact = requireArtifact(artifactId);
        Path filePath = resolveArtifactFilePath(artifact);
        if (!isPreviewableArtifactFile(filePath, artifact)) {
            throw new ServiceException(message("ai.protocol.artifact.preview.text.only"));
        }
        try {
            long fileSize = Files.size(filePath);
            if (fileSize > MAX_ARTIFACT_PREVIEW_BYTES) {
                throw new ServiceException(message("ai.protocol.artifact.preview.too.large"));
            }
            String content = Files.readString(filePath, StandardCharsets.UTF_8);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("artifactId", artifact.getArtifactId());
            result.put("artifactType", artifact.getArtifactType());
            result.put("artifactName", artifact.getArtifactName());
            result.put("fileSize", fileSize);
            result.put("content", content);
            return result;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(message("ai.protocol.artifact.preview.read.failed", ex.getMessage()));
        }
    }

    @Override
    public List<AiProtocolGenerationRecordVO> listTaskGenerationRecords(Long taskId) {
        requireTask(taskId);
        List<AiProtocolGenerationRecord> recordList = aiProtocolGenerationRecordMapper.selectList(
                Wrappers.<AiProtocolGenerationRecord>lambdaQuery()
                        .eq(AiProtocolGenerationRecord::getTaskId, taskId)
                        .orderByDesc(AiProtocolGenerationRecord::getUpdateTime)
                        .orderByDesc(AiProtocolGenerationRecord::getCreateTime));
        return AiProtocolGenerationRecordConvert.INSTANCE.convertAiProtocolGenerationRecordVOList(recordList);
    }

    @Override
    public AiProtocolGenerationRecordVO selectGenerationRecordVO(Long recordId) {
        if (recordId == null) {
            throw new ServiceException(message("ai.protocol.generation.record.id.required"));
        }
        AiProtocolGenerationRecord record = aiProtocolGenerationRecordMapper.selectById(recordId);
        if (record == null) {
            throw new ServiceException(message("ai.protocol.generation.record.not.exists.or.deleted"));
        }
        return AiProtocolGenerationRecordConvert.INSTANCE.convertAiProtocolGenerationRecordVO(record);
    }

    private AiProtocolGenerationRecord requireGenerationRecord(Long recordId) {
        if (recordId == null) {
            throw new ServiceException(message("ai.protocol.generation.record.id.required"));
        }
        AiProtocolGenerationRecord record = aiProtocolGenerationRecordMapper.selectById(recordId);
        if (record == null) {
            throw new ServiceException(message("ai.protocol.generation.record.not.exists.or.deleted"));
        }
        return record;
    }

    private AiProtocolAdaptationArtifact requireArtifact(Long artifactId) {
        if (artifactId == null) {
            throw new ServiceException(message("ai.protocol.artifact.id.required"));
        }
        AiProtocolAdaptationArtifact artifact = aiProtocolAdaptationArtifactMapper.selectById(artifactId);
        if (artifact == null || !STATUS_ENABLED.equals(defaultIfBlank(artifact.getStatus(), STATUS_ENABLED))) {
            throw new ServiceException(message("ai.protocol.artifact.not.exists.or.deleted"));
        }
        return artifact;
    }

    private LambdaQueryWrapper<AiProtocolAdaptationTask> buildQueryWrapper(AiProtocolAdaptationTask query) {
        AiProtocolAdaptationTask actualQuery = query == null ? new AiProtocolAdaptationTask() : query;
        Map<String, Object> params = actualQuery.getParams() == null ? Collections.emptyMap() : actualQuery.getParams();
        LambdaQueryWrapper<AiProtocolAdaptationTask> lqw = Wrappers.lambdaQuery();
        lqw.eq(actualQuery.getTaskId() != null, AiProtocolAdaptationTask::getTaskId, actualQuery.getTaskId());
        lqw.like(StringUtils.isNotBlank(actualQuery.getTaskName()), AiProtocolAdaptationTask::getTaskName, actualQuery.getTaskName());
        lqw.like(StringUtils.isNotBlank(actualQuery.getProtocolCode()), AiProtocolAdaptationTask::getProtocolCode, actualQuery.getProtocolCode());
        lqw.like(StringUtils.isNotBlank(actualQuery.getProtocolName()), AiProtocolAdaptationTask::getProtocolName, actualQuery.getProtocolName());
        lqw.eq(StringUtils.isNotBlank(actualQuery.getTaskStatus()), AiProtocolAdaptationTask::getTaskStatus, actualQuery.getTaskStatus());
        lqw.eq(StringUtils.isNotBlank(actualQuery.getParseStatus()), AiProtocolAdaptationTask::getParseStatus, actualQuery.getParseStatus());
        lqw.eq(StringUtils.isNotBlank(actualQuery.getValidationStatus()), AiProtocolAdaptationTask::getValidationStatus, actualQuery.getValidationStatus());
        lqw.eq(StringUtils.isNotBlank(actualQuery.getGenerationStatus()), AiProtocolAdaptationTask::getGenerationStatus, actualQuery.getGenerationStatus());
        lqw.eq(StringUtils.isNotBlank(actualQuery.getRiskLevel()), AiProtocolAdaptationTask::getRiskLevel, actualQuery.getRiskLevel());
        lqw.eq(StringUtils.isNotBlank(actualQuery.getStatus()), AiProtocolAdaptationTask::getStatus, actualQuery.getStatus());
        lqw.eq(actualQuery.getTenantId() != null, AiProtocolAdaptationTask::getTenantId, actualQuery.getTenantId());
        lqw.like(StringUtils.isNotBlank(actualQuery.getTenantName()), AiProtocolAdaptationTask::getTenantName, actualQuery.getTenantName());
        if (Objects.nonNull(params.get("beginTime")) && Objects.nonNull(params.get("endTime"))) {
            lqw.between(AiProtocolAdaptationTask::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        applyFlowStatusFilter(lqw, params.get("flowStatus"));
        return lqw;
    }

    private void applyFlowStatusFilter(LambdaQueryWrapper<AiProtocolAdaptationTask> lqw, Object flowStatusValue) {
        String flowStatus = flowStatusValue == null ? "" : trimToEmpty(String.valueOf(flowStatusValue)).toUpperCase(Locale.ROOT);
        if (StringUtils.isBlank(flowStatus)) {
            return;
        }
        switch (flowStatus) {
            case FLOW_STATUS_PROCESSING:
                lqw.and(wrapper -> wrapper
                        .eq(AiProtocolAdaptationTask::getParseStatus, PARSE_STATUS_PARSING)
                        .or()
                        .eq(AiProtocolAdaptationTask::getGenerationStatus, GENERATION_STATUS_GENERATING)
                        .or()
                        .in(AiProtocolAdaptationTask::getTaskStatus, Arrays.asList(
                                DEFAULT_TASK_STATUS,
                                TASK_STATUS_UPLOADED,
                                TASK_STATUS_AI_PARSED,
                                TASK_STATUS_WORKBOOK_EXPORTED,
                                TASK_STATUS_REVIEW_IMPORTED,
                                TASK_STATUS_VALIDATED
                        )));
                lqw.ne(AiProtocolAdaptationTask::getValidationStatus, VALIDATION_STATUS_BLOCKED)
                        .ne(AiProtocolAdaptationTask::getGenerationStatus, GENERATION_STATUS_SUCCESS)
                        .ne(AiProtocolAdaptationTask::getTaskStatus, TASK_STATUS_CONFIRMED)
                        .ne(AiProtocolAdaptationTask::getTaskStatus, TASK_STATUS_FAILED);
                break;
            case FLOW_STATUS_NEED_REVIEW:
                lqw.eq(AiProtocolAdaptationTask::getValidationStatus, VALIDATION_STATUS_BLOCKED);
                break;
            case FLOW_STATUS_PACKAGE_READY:
                lqw.eq(AiProtocolAdaptationTask::getGenerationStatus, GENERATION_STATUS_SUCCESS)
                        .ne(AiProtocolAdaptationTask::getTaskStatus, TASK_STATUS_CONFIRMED);
                break;
            case FLOW_STATUS_CONFIRMED:
                lqw.eq(AiProtocolAdaptationTask::getTaskStatus, TASK_STATUS_CONFIRMED);
                break;
            case FLOW_STATUS_FAILED:
                lqw.and(wrapper -> wrapper
                        .eq(AiProtocolAdaptationTask::getTaskStatus, TASK_STATUS_FAILED)
                        .or()
                        .eq(AiProtocolAdaptationTask::getParseStatus, PARSE_STATUS_FAILED)
                        .or()
                        .eq(AiProtocolAdaptationTask::getGenerationStatus, GENERATION_STATUS_FAILED));
                break;
            default:
                break;
        }
    }

    private void fillTaskStatistics(List<AiProtocolAdaptationTaskVO> taskList) {
        if (taskList == null || taskList.isEmpty()) {
            return;
        }
        for (AiProtocolAdaptationTaskVO task : taskList) {
            if (task == null || task.getTaskId() == null) {
                continue;
            }
            Long artifactCount = aiProtocolAdaptationArtifactMapper.selectCount(Wrappers.<AiProtocolAdaptationArtifact>lambdaQuery()
                    .eq(AiProtocolAdaptationArtifact::getTaskId, task.getTaskId()));
            Long recordCount = aiProtocolGenerationRecordMapper.selectCount(Wrappers.<AiProtocolGenerationRecord>lambdaQuery()
                    .eq(AiProtocolGenerationRecord::getTaskId, task.getTaskId()));
            task.setArtifactCount(artifactCount == null ? 0 : artifactCount.intValue());
            task.setGenerationRecordCount(recordCount == null ? 0 : recordCount.intValue());
        }
    }

    private AiProtocolAdaptationArtifact buildUploadArtifact(AiProtocolAdaptationTask task, String artifactType,
                                                             String originalFilename, Path savedFile, byte[] fileBytes) {
        AiProtocolAdaptationArtifact artifact = new AiProtocolAdaptationArtifact();
        artifact.setTaskId(task.getTaskId());
        artifact.setArtifactType(artifactType);
        artifact.setArtifactName(originalFilename);
        artifact.setFilePath(savedFile.toString());
        artifact.setFileSize((long) fileBytes.length);
        artifact.setChecksum(DigestUtils.md5DigestAsHex(fileBytes));
        artifact.setSourceType(SOURCE_TYPE_CUSTOM_UPLOAD);
        artifact.setArtifactStatus(ARTIFACT_STATUS_READY);
        artifact.setSummary(buildUploadSummary(artifactType, originalFilename, fileBytes.length));
        artifact.setStatus(STATUS_ENABLED);
        artifact.setCreateBy(AiSecuritySupport.resolveUsername());
        artifact.setCreateTime(AiSecuritySupport.now());
        artifact.setUpdateBy(AiSecuritySupport.resolveUsername());
        artifact.setUpdateTime(AiSecuritySupport.now());
        return artifact;
    }

    private void updateTaskAfterArtifactUploaded(AiProtocolAdaptationTask task, String artifactType, Path savedFile) {
        AiProtocolAdaptationTask update = new AiProtocolAdaptationTask();
        update.setTaskId(task.getTaskId());
        if (ARTIFACT_TYPE_SOURCE_DOCUMENT.equals(artifactType)) {
            update.setTaskStatus(TASK_STATUS_UPLOADED);
            update.setParseStatus(DEFAULT_PARSE_STATUS);
            update.setValidationStatus(DEFAULT_VALIDATION_STATUS);
            update.setGenerationStatus(DEFAULT_GENERATION_STATUS);
            update.setRiskLevel(DEFAULT_RISK_LEVEL);
            update.setErrorSummary("");
        } else if (ARTIFACT_TYPE_REVIEW_WORKBOOK.equals(artifactType)) {
            update.setTaskStatus(TASK_STATUS_REVIEW_IMPORTED);
            update.setWorkbookPath(savedFile.toString());
            update.setValidationStatus(DEFAULT_VALIDATION_STATUS);
            update.setGenerationStatus(DEFAULT_GENERATION_STATUS);
            update.setErrorSummary("");
        } else if (ARTIFACT_TYPE_ENTERPRISE_WORKBOOK.equals(artifactType)) {
            update.setTaskStatus(TASK_STATUS_WORKBOOK_EXPORTED);
            update.setWorkbookPath(savedFile.toString());
        }
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        baseMapper.updateById(update);
    }

    private void updateTaskParseStart(Long taskId) {
        AiProtocolAdaptationTask update = new AiProtocolAdaptationTask();
        update.setTaskId(taskId);
        update.setParseStatus(PARSE_STATUS_PARSING);
        update.setErrorSummary("");
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        baseMapper.updateById(update);
    }

    private void updateTaskParseSuccess(Long taskId, Path dslDraftPath) {
        AiProtocolAdaptationTask update = new AiProtocolAdaptationTask();
        update.setTaskId(taskId);
        update.setTaskStatus(TASK_STATUS_AI_PARSED);
        update.setParseStatus(PARSE_STATUS_SUCCESS);
        update.setValidationStatus(DEFAULT_VALIDATION_STATUS);
        update.setGenerationStatus(DEFAULT_GENERATION_STATUS);
        update.setRiskLevel(RISK_LEVEL_MEDIUM);
        update.setDslSnapshotPath(dslDraftPath.toString());
        update.setErrorSummary("");
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        baseMapper.updateById(update);
    }

    private void updateTaskParseFailure(Long taskId, String failureReason) {
        AiProtocolAdaptationTask update = new AiProtocolAdaptationTask();
        update.setTaskId(taskId);
        update.setTaskStatus(TASK_STATUS_FAILED);
        update.setParseStatus(PARSE_STATUS_FAILED);
        update.setErrorSummary(abbreviate(defaultIfBlank(failureReason, "协议文档解析失败"), 1000));
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        baseMapper.updateById(update);
    }

    private void updateTaskValidationResult(Long taskId, ValidationGateResult result) {
        AiProtocolAdaptationTask update = new AiProtocolAdaptationTask();
        update.setTaskId(taskId);
        if (!VALIDATION_STATUS_BLOCKED.equals(result.validationStatus)) {
            update.setTaskStatus(TASK_STATUS_VALIDATED);
        }
        update.setValidationStatus(result.validationStatus);
        update.setGenerationStatus(DEFAULT_GENERATION_STATUS);
        update.setRiskLevel(result.riskLevel);
        update.setErrorSummary(VALIDATION_STATUS_PASSED.equals(result.validationStatus)
                ? ""
                : abbreviate(result.summary, 1000));
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        baseMapper.updateById(update);
    }

    private void updateTaskValidationFailure(Long taskId, String failureReason) {
        AiProtocolAdaptationTask update = new AiProtocolAdaptationTask();
        update.setTaskId(taskId);
        update.setValidationStatus(VALIDATION_STATUS_BLOCKED);
        update.setGenerationStatus(DEFAULT_GENERATION_STATUS);
        update.setRiskLevel(RISK_LEVEL_BLOCKER);
        update.setErrorSummary(abbreviate(defaultIfBlank(failureReason, "协议 DSL 质量校验失败"), 1000));
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        baseMapper.updateById(update);
    }

    private void updateTaskWorkbookExported(Long taskId, Path workbookPath) {
        AiProtocolAdaptationTask update = new AiProtocolAdaptationTask();
        update.setTaskId(taskId);
        update.setTaskStatus(TASK_STATUS_WORKBOOK_EXPORTED);
        update.setWorkbookPath(workbookPath.toString());
        update.setErrorSummary("");
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        baseMapper.updateById(update);
    }

    private void updateTaskWorkbookImported(Long taskId, Path workbookPath, Path dslSnapshotPath) {
        AiProtocolAdaptationTask update = new AiProtocolAdaptationTask();
        update.setTaskId(taskId);
        update.setTaskStatus(TASK_STATUS_REVIEW_IMPORTED);
        update.setDslSnapshotPath(dslSnapshotPath.toString());
        update.setWorkbookPath(workbookPath.toString());
        update.setValidationStatus(DEFAULT_VALIDATION_STATUS);
        update.setGenerationStatus(DEFAULT_GENERATION_STATUS);
        update.setRiskLevel(RISK_LEVEL_MEDIUM);
        update.setErrorSummary("");
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        baseMapper.updateById(update);
    }

    private void updateTaskGenerationStart(Long taskId) {
        AiProtocolAdaptationTask update = new AiProtocolAdaptationTask();
        update.setTaskId(taskId);
        update.setGenerationStatus(GENERATION_STATUS_GENERATING);
        update.setErrorSummary("");
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        baseMapper.updateById(update);
    }

    private void updateTaskGenerationSuccess(Long taskId) {
        AiProtocolAdaptationTask update = new AiProtocolAdaptationTask();
        update.setTaskId(taskId);
        update.setTaskStatus(TASK_STATUS_GENERATED);
        update.setGenerationStatus(GENERATION_STATUS_SUCCESS);
        update.setErrorSummary("");
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        baseMapper.updateById(update);
    }

    private void updateTaskGenerationConfirmed(Long taskId) {
        AiProtocolAdaptationTask update = new AiProtocolAdaptationTask();
        update.setTaskId(taskId);
        update.setTaskStatus(TASK_STATUS_CONFIRMED);
        update.setGenerationStatus(GENERATION_STATUS_SUCCESS);
        update.setErrorSummary("");
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        baseMapper.updateById(update);
    }

    private void updateTaskGenerationFailure(Long taskId, String failureReason) {
        AiProtocolAdaptationTask update = new AiProtocolAdaptationTask();
        update.setTaskId(taskId);
        update.setGenerationStatus(GENERATION_STATUS_FAILED);
        update.setErrorSummary(abbreviate(defaultIfBlank(failureReason, "协议代码包生成失败"), 1000));
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        baseMapper.updateById(update);
    }

    private AiProtocolAdaptationArtifact requireLatestSourceDocument(Long taskId) {
        AiProtocolAdaptationArtifact artifact = selectLatestArtifact(taskId, ARTIFACT_TYPE_SOURCE_DOCUMENT);
        if (artifact == null) {
            throw new ServiceException(message("ai.protocol.adaptation.original.document.required"));
        }
        return artifact;
    }

    private Path requireCurrentDslSnapshotPath(AiProtocolAdaptationTask task) {
        if (StringUtils.isNotBlank(task.getDslSnapshotPath())) {
            Path taskDslPath = Paths.get(task.getDslSnapshotPath().trim());
            if (Files.exists(taskDslPath)) {
                return taskDslPath;
            }
        }
        AiProtocolAdaptationArtifact artifact = selectLatestArtifact(task.getTaskId(), ARTIFACT_TYPE_AI_DSL_DRAFT);
        if (artifact == null) {
            throw new ServiceException(message("ai.protocol.adaptation.dsl.draft.required"));
        }
        return requireArtifactPath(artifact);
    }

    private AiProtocolAdaptationArtifact selectLatestArtifact(Long taskId, String artifactType) {
        List<AiProtocolAdaptationArtifact> artifacts = aiProtocolAdaptationArtifactMapper.selectList(
                Wrappers.<AiProtocolAdaptationArtifact>lambdaQuery()
                        .eq(AiProtocolAdaptationArtifact::getTaskId, taskId)
                        .eq(AiProtocolAdaptationArtifact::getArtifactType, artifactType)
                        .eq(AiProtocolAdaptationArtifact::getStatus, STATUS_ENABLED)
                        .orderByDesc(AiProtocolAdaptationArtifact::getCreateTime)
                        .orderByDesc(AiProtocolAdaptationArtifact::getArtifactId));
        return artifacts == null || artifacts.isEmpty() ? null : artifacts.get(0);
    }

    private Path requireArtifactPath(AiProtocolAdaptationArtifact artifact) {
        if (artifact == null || StringUtils.isBlank(artifact.getFilePath())) {
            throw new ServiceException(message("ai.protocol.artifact.file.path.required"));
        }
        Path path = Paths.get(artifact.getFilePath().trim());
        if (!Files.exists(path)) {
            throw new ServiceException(message("ai.protocol.artifact.file.not.exists", artifact.getFilePath()));
        }
        return path;
    }

    private ExtractedDocument extractDocument(AiProtocolAdaptationArtifact sourceArtifact, Path sourceFile, byte[] fileBytes) throws IOException {
        String fileName = StringUtils.isBlank(sourceArtifact.getArtifactName())
                ? sourceFile.getFileName().toString()
                : sourceArtifact.getArtifactName();
        String suffix = getFileSuffix(fileName).toLowerCase(Locale.ROOT);
        if (".xls".equals(suffix) || ".xlsx".equals(suffix)) {
            return extractWorkbook(fileName, suffix, fileBytes);
        }
        if (".docx".equals(suffix)) {
            return extractDocx(fileName, suffix, fileBytes);
        }
        if (".pdf".equals(suffix)) {
            return extractPdf(fileName, suffix, fileBytes);
        }
        if (".doc".equals(suffix)) {
            throw new ServiceException(message("ai.protocol.document.doc.extract.unsupported"));
        }
        if (SOURCE_DOCUMENT_SUFFIXES.contains(suffix)) {
            return extractPlainText(fileName, suffix, fileBytes, "TEXT");
        }
        throw new ServiceException(message("ai.protocol.document.type.unsupported", suffix));
    }

    private ExtractedDocument extractPlainText(String fileName, String suffix, byte[] fileBytes, String extractorType) {
        String text = decodeText(fileBytes);
        return buildExtractedDocument(fileName, suffix, extractorType, text, new JSONArray(), false);
    }

    private ExtractedDocument extractDocx(String fileName, String suffix, byte[] fileBytes) throws IOException {
        String documentXml = "";
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(fileBytes))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if ("word/document.xml".equals(entry.getName())) {
                    documentXml = new String(readAllBytes(zipInputStream), java.nio.charset.StandardCharsets.UTF_8);
                    break;
                }
            }
        }
        if (StringUtils.isBlank(documentXml)) {
            throw new ServiceException(message("ai.protocol.document.docx.empty"));
        }
        String text = documentXml
                .replaceAll("</w:p>", "\n")
                .replaceAll("</w:tr>", "\n")
                .replaceAll("<[^>]+>", "");
        text = unescapeXml(text).replaceAll("[\\t ]+\\n", "\n").trim();
        return buildExtractedDocument(fileName, suffix, "DOCX_XML", text, new JSONArray(), false);
    }

    private ExtractedDocument extractPdf(String fileName, String suffix, byte[] fileBytes) throws IOException {
        StringBuilder text = new StringBuilder();
        try (PDDocument document = Loader.loadPDF(fileBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            int pageCount = document.getNumberOfPages();
            for (int page = 1; page <= pageCount; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String pageText = stripper.getText(document);
                if (StringUtils.isBlank(pageText)) {
                    continue;
                }
                text.append("# PDF Page ").append(page).append('\n')
                        .append(pageText.trim()).append("\n\n");
            }
        }
        String contentText = text.toString().replaceAll("[\\t ]+\\n", "\n").trim();
        if (StringUtils.isBlank(contentText)) {
            throw new ServiceException(message("ai.protocol.document.pdf.text.empty"));
        }
        return buildExtractedDocument(fileName, suffix, "PDF_TEXT", contentText, new JSONArray(), false);
    }

    private ExtractedDocument extractWorkbook(String fileName, String suffix, byte[] fileBytes) throws IOException {
        JSONArray tables = new JSONArray();
        StringBuilder text = new StringBuilder();
        try (Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(new ByteArrayInputStream(fileBytes))) {
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                if (sheet == null) {
                    continue;
                }
                JSONObject table = new JSONObject();
                JSONArray rows = new JSONArray();
                int lastRowNum = sheet.getLastRowNum();
                int maxRow = Math.min(lastRowNum, MAX_EXCEL_ROWS_PER_SHEET);
                text.append("# Sheet: ").append(sheet.getSheetName()).append('\n');
                for (int rowIndex = 0; rowIndex <= maxRow; rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row == null) {
                        continue;
                    }
                    JSONArray rowValues = new JSONArray();
                    List<String> lineValues = new ArrayList<>();
                    short lastCellNum = row.getLastCellNum();
                    for (int columnIndex = 0; columnIndex < lastCellNum; columnIndex++) {
                        Cell cell = row.getCell(columnIndex);
                        String cellText = trimToEmpty(getCellText(cell));
                        rowValues.add(cellText);
                        lineValues.add(cellText);
                    }
                    rows.add(rowValues);
                    text.append(String.join("\t", lineValues)).append('\n');
                }
                table.put("sheetName", sheet.getSheetName());
                table.put("rowCount", Math.max(lastRowNum + 1, 0));
                table.put("extractedRowCount", rows.size());
                table.put("truncated", lastRowNum > MAX_EXCEL_ROWS_PER_SHEET);
                table.put("rows", rows);
                tables.add(table);
                text.append('\n');
            }
        } catch (Exception ex) {
            throw new IOException("解析 Excel 协议文档失败：" + ex.getMessage(), ex);
        }
        return buildExtractedDocument(fileName, suffix, "EXCEL_WORKBOOK", text.toString(), tables, false);
    }

    private ExtractedDocument buildExtractedDocument(String fileName, String suffix, String extractorType,
                                                     String rawText, JSONArray tables, boolean forceTruncated) {
        String text = rawText == null ? "" : rawText;
        boolean truncated = forceTruncated || text.length() > MAX_EXTRACTED_TEXT_LENGTH;
        String content = truncated ? text.substring(0, Math.min(text.length(), MAX_EXTRACTED_TEXT_LENGTH)) : text;
        ExtractedDocument document = new ExtractedDocument();
        document.fileName = fileName;
        document.suffix = suffix;
        document.extractorType = extractorType;
        document.contentText = content;
        document.textLength = text.length();
        document.lineCount = countLines(content);
        document.tables = tables == null ? new JSONArray() : tables;
        document.tableCount = document.tables.size();
        document.truncated = truncated;
        return document;
    }

    private JSONObject buildExtractedTextSnapshot(AiProtocolAdaptationTask task,
                                                  AiProtocolAdaptationArtifact sourceArtifact,
                                                  ExtractedDocument extractedDocument) {
        JSONObject snapshot = new JSONObject();
        snapshot.put("snapshotType", ARTIFACT_TYPE_EXTRACTED_TEXT);
        snapshot.put("generatedAt", formatNow());
        snapshot.put("taskId", task.getTaskId());
        snapshot.put("sourceArtifactId", sourceArtifact.getArtifactId());
        snapshot.put("sourceArtifactName", sourceArtifact.getArtifactName());
        snapshot.put("extractorType", extractedDocument.extractorType);
        snapshot.put("fileName", extractedDocument.fileName);
        snapshot.put("fileSuffix", extractedDocument.suffix);
        snapshot.put("textLength", extractedDocument.textLength);
        snapshot.put("lineCount", extractedDocument.lineCount);
        snapshot.put("tableCount", extractedDocument.tableCount);
        snapshot.put("truncated", extractedDocument.truncated);
        snapshot.put("contentText", extractedDocument.contentText);
        snapshot.put("tables", extractedDocument.tables);
        return snapshot;
    }

    private JSONObject buildDslDraftSnapshot(AiProtocolAdaptationTask task,
                                             AiProtocolAdaptationArtifact sourceArtifact,
                                             ExtractedDocument extractedDocument,
                                             Path extractedSnapshotPath) {
        JSONObject embeddedDsl = extractEmbeddedProtocolDsl(extractedDocument.contentText);
        if (embeddedDsl != null) {
            return buildEmbeddedDslDraftSnapshot(task, sourceArtifact, extractedDocument, extractedSnapshotPath, embeddedDsl);
        }
        String aiFailureReason = null;
        try {
            JSONObject aiDsl = aiProtocolDslAiParseService.parseToDsl(task, extractedDocument.fileName,
                    extractedDocument.contentText, extractedDocument.tables);
            if (aiDsl != null) {
                return buildAiDslDraftSnapshot(task, sourceArtifact, extractedDocument, extractedSnapshotPath, aiDsl);
            }
        } catch (Exception ex) {
            aiFailureReason = abbreviate(defaultIfBlank(ex.getMessage(), ex.getClass().getSimpleName()), 300);
        }
        JSONObject snapshot = new JSONObject();
        snapshot.put("snapshotType", ARTIFACT_TYPE_AI_DSL_DRAFT);
        snapshot.put("schemaVersion", "protocol-dsl-0.1");
        snapshot.put("generatedAt", formatNow());
        snapshot.put("generatedBy", "RULE_BASED_BOOTSTRAP");
        snapshot.put("taskId", task.getTaskId());
        snapshot.put("sourceArtifactId", sourceArtifact.getArtifactId());
        snapshot.put("extractedTextPath", extractedSnapshotPath.toString());
        snapshot.put("draftCompleteness", "SKELETON");

        JSONObject protocol = new JSONObject();
        protocol.put("protocolCode", trimToEmpty(task.getProtocolCode()));
        protocol.put("protocolName", trimToEmpty(task.getProtocolName()));
        protocol.put("protocolFamily", inferProtocolFamily(extractedDocument.contentText));
        protocol.put("messageFormat", inferMessageFormat(extractedDocument.contentText));
        protocol.put("transport", inferTransport(extractedDocument.contentText));
        protocol.put("sourceFileName", extractedDocument.fileName);
        protocol.put("textLength", extractedDocument.textLength);
        protocol.put("tableCount", extractedDocument.tableCount);
        snapshot.put("protocol", protocol);

        snapshot.put("messageTypes", new JSONArray());
        snapshot.put("fields", new JSONArray());
        snapshot.put("codecRules", new JSONArray());
        snapshot.put("thingModelMappings", new JSONArray());
        snapshot.put("sampleFrames", new JSONArray());
        snapshot.put("sourceTables", extractedDocument.tables);

        JSONArray qualityIssues = new JSONArray();
        qualityIssues.add(buildQualityIssue("BLOCKER", "MESSAGE_TYPES_REQUIRED", "需要确认上行、下行、心跳、事件等报文类型"));
        qualityIssues.add(buildQualityIssue("BLOCKER", "FIELD_RULES_REQUIRED", "需要确认字段顺序、长度、数据类型、字节序和缩放规则"));
        qualityIssues.add(buildQualityIssue("WARNING", "SAMPLE_FRAMES_RECOMMENDED",
                "建议补充至少一条可解析的样例报文，用于后续静态回归验证"));
        if (StringUtils.isNotBlank(aiFailureReason)) {
            qualityIssues.add(buildQualityIssue("WARNING", "AI_ENRICHMENT_FAILED",
                    "AI 增强解析未能生成可信 DSL，已降级为规则骨架：" + aiFailureReason));
        } else {
            qualityIssues.add(buildQualityIssue("WARNING", "AI_ENRICHMENT_PENDING",
                    "当前为规则抽取生成的 DSL 骨架，建议导出企业工作簿校对"));
        }
        snapshot.put("qualityIssues", qualityIssues);

        JSONObject generationStrategy = new JSONObject();
        generationStrategy.put("target", "fastbee-protocol");
        generationStrategy.put("requiresManualConfirmation", true);
        generationStrategy.put("allowCodeGeneration", false);
        generationStrategy.put("reason", "DSL 草稿仅完成骨架抽取，尚未通过质量门禁");
        snapshot.put("generationStrategy", generationStrategy);
        return snapshot;
    }

    private JSONObject buildEmbeddedDslDraftSnapshot(AiProtocolAdaptationTask task,
                                                     AiProtocolAdaptationArtifact sourceArtifact,
                                                     ExtractedDocument extractedDocument,
                                                     Path extractedSnapshotPath,
                                                     JSONObject embeddedDsl) {
        JSONObject snapshot = new JSONObject();
        snapshot.putAll(embeddedDsl);
        snapshot.put("snapshotType", ARTIFACT_TYPE_AI_DSL_DRAFT);
        snapshot.put("schemaVersion", defaultIfBlank(snapshot.getString("schemaVersion"), "protocol-dsl-0.1"));
        snapshot.put("generatedAt", formatNow());
        snapshot.put("generatedBy", "DOCUMENT_EMBEDDED_DSL");
        snapshot.put("taskId", task.getTaskId());
        snapshot.put("sourceArtifactId", sourceArtifact.getArtifactId());
        snapshot.put("extractedTextPath", extractedSnapshotPath.toString());
        snapshot.put("draftCompleteness", "STRUCTURED_DSL");

        JSONObject protocol = snapshot.getJSONObject("protocol");
        if (protocol == null) {
            protocol = new JSONObject();
        }
        protocol.put("protocolCode", defaultIfBlank(protocol.getString("protocolCode"), task.getProtocolCode()));
        protocol.put("protocolName", defaultIfBlank(protocol.getString("protocolName"), task.getProtocolName()));
        protocol.put("protocolFamily", defaultIfBlank(protocol.getString("protocolFamily"),
                inferProtocolFamily(extractedDocument.contentText)));
        protocol.put("messageFormat", defaultIfBlank(protocol.getString("messageFormat"),
                inferMessageFormat(extractedDocument.contentText)));
        protocol.put("transport", defaultIfBlank(protocol.getString("transport"), inferTransport(extractedDocument.contentText)));
        protocol.put("sourceFileName", extractedDocument.fileName);
        protocol.put("textLength", extractedDocument.textLength);
        protocol.put("tableCount", extractedDocument.tableCount);
        snapshot.put("protocol", protocol);

        ensureJsonArrayField(snapshot, "messageTypes");
        ensureJsonArrayField(snapshot, "fields");
        ensureJsonArrayField(snapshot, "codecRules");
        ensureJsonArrayField(snapshot, "thingModelMappings");
        ensureJsonArrayField(snapshot, "sampleFrames");
        ensureJsonArrayField(snapshot, "qualityIssues");
        snapshot.put("sourceTables", extractedDocument.tables);

        JSONObject generationStrategy = snapshot.getJSONObject("generationStrategy");
        if (generationStrategy == null) {
            generationStrategy = new JSONObject();
        }
        generationStrategy.put("target", defaultIfBlank(generationStrategy.getString("target"), "fastbee-protocol"));
        if (!generationStrategy.containsKey("requiresManualConfirmation")) {
            generationStrategy.put("requiresManualConfirmation", true);
        }
        snapshot.put("generationStrategy", generationStrategy);
        return snapshot;
    }

    private JSONObject buildAiDslDraftSnapshot(AiProtocolAdaptationTask task,
                                               AiProtocolAdaptationArtifact sourceArtifact,
                                               ExtractedDocument extractedDocument,
                                               Path extractedSnapshotPath,
                                               JSONObject aiDsl) {
        JSONObject snapshot = new JSONObject();
        snapshot.putAll(aiDsl);
        snapshot.put("snapshotType", ARTIFACT_TYPE_AI_DSL_DRAFT);
        snapshot.put("schemaVersion", defaultIfBlank(snapshot.getString("schemaVersion"), "protocol-dsl-0.1"));
        snapshot.put("generatedAt", formatNow());
        snapshot.put("generatedBy", "AI_ENRICHED_DSL");
        snapshot.put("taskId", task.getTaskId());
        snapshot.put("sourceArtifactId", sourceArtifact.getArtifactId());
        snapshot.put("extractedTextPath", extractedSnapshotPath.toString());
        snapshot.put("draftCompleteness", "AI_CANDIDATE");

        JSONObject protocol = snapshot.getJSONObject("protocol");
        if (protocol == null) {
            protocol = new JSONObject();
        }
        protocol.put("protocolCode", defaultIfBlank(protocol.getString("protocolCode"), task.getProtocolCode()));
        protocol.put("protocolName", defaultIfBlank(protocol.getString("protocolName"), task.getProtocolName()));
        protocol.put("protocolFamily", defaultIfBlank(protocol.getString("protocolFamily"),
                inferProtocolFamily(extractedDocument.contentText)));
        protocol.put("messageFormat", defaultIfBlank(protocol.getString("messageFormat"),
                inferMessageFormat(extractedDocument.contentText)));
        protocol.put("transport", defaultIfBlank(protocol.getString("transport"), inferTransport(extractedDocument.contentText)));
        protocol.put("sourceFileName", extractedDocument.fileName);
        protocol.put("textLength", extractedDocument.textLength);
        protocol.put("tableCount", extractedDocument.tableCount);
        snapshot.put("protocol", protocol);

        ensureJsonArrayField(snapshot, "messageTypes");
        ensureJsonArrayField(snapshot, "fields");
        ensureJsonArrayField(snapshot, "codecRules");
        ensureJsonArrayField(snapshot, "thingModelMappings");
        ensureJsonArrayField(snapshot, "sampleFrames");
        ensureJsonArrayField(snapshot, "qualityIssues");
        snapshot.put("sourceTables", extractedDocument.tables);

        JSONObject generationStrategy = snapshot.getJSONObject("generationStrategy");
        if (generationStrategy == null) {
            generationStrategy = new JSONObject();
        }
        generationStrategy.put("target", defaultIfBlank(generationStrategy.getString("target"), "fastbee-protocol"));
        if (!generationStrategy.containsKey("requiresManualConfirmation")) {
            generationStrategy.put("requiresManualConfirmation", true);
        }
        if (!generationStrategy.containsKey("allowCodeGeneration")) {
            generationStrategy.put("allowCodeGeneration", hasCodeGenerationMinimum(snapshot));
        }
        snapshot.put("generationStrategy", generationStrategy);
        return snapshot;
    }

    private String buildDslDraftArtifactSummary(JSONObject dslDraft) {
        if (dslDraft == null) {
            return "已生成协议 DSL 草稿骨架，等待质量门禁和人工复核";
        }
        String generatedBy = dslDraft.getString("generatedBy");
        if ("DOCUMENT_EMBEDDED_DSL".equals(generatedBy)) {
            return "已识别协议文档内嵌结构化 DSL，等待质量门禁和人工复核";
        }
        if ("AI_ENRICHED_DSL".equals(generatedBy)) {
            JSONObject aiMeta = dslDraft.getJSONObject("aiMeta");
            String confidenceText = aiMeta == null || aiMeta.get("confidence") == null
                    ? ""
                    : "，置信度=" + aiMeta.get("confidence");
            return "AI 增强解析已生成协议 DSL 候选" + confidenceText + "，等待质量门禁和人工复核";
        }
        return "已生成协议 DSL 草稿骨架，建议导出企业工作簿校对后执行质量门禁";
    }

    private void ensureJsonArrayField(JSONObject object, String key) {
        if (object == null) {
            return;
        }
        Object value = object.get(key);
        if (!(value instanceof JSONArray)) {
            object.put(key, new JSONArray());
        }
    }

    private boolean hasCodeGenerationMinimum(JSONObject snapshot) {
        if (snapshot == null || snapshot.isEmpty()) {
            return false;
        }
        return hasJsonValue(snapshot.getJSONObject("protocol"))
                && !readJsonArray(snapshot, "messageTypes").isEmpty()
                && !readJsonArray(snapshot, "fields").isEmpty()
                && !readJsonArray(snapshot, "codecRules").isEmpty();
    }

    private JSONObject extractEmbeddedProtocolDsl(String contentText) {
        if (StringUtils.isBlank(contentText)) {
            return null;
        }
        JSONObject wholeDocumentDsl = parseProtocolDslCandidate(contentText.trim());
        if (wholeDocumentDsl != null) {
            return wholeDocumentDsl;
        }
        JSONObject fencedDsl = extractProtocolDslFromCodeFence(contentText);
        if (fencedDsl != null) {
            return fencedDsl;
        }
        return extractProtocolDslNearSchemaMarker(contentText);
    }

    private JSONObject extractProtocolDslFromCodeFence(String contentText) {
        int searchIndex = 0;
        while (searchIndex >= 0 && searchIndex < contentText.length()) {
            int fenceStart = contentText.indexOf("```", searchIndex);
            if (fenceStart < 0) {
                return null;
            }
            int contentStart = contentText.indexOf('\n', fenceStart + 3);
            if (contentStart < 0) {
                return null;
            }
            String fenceHeader = contentText.substring(fenceStart + 3, contentStart).trim().toLowerCase(Locale.ROOT);
            int fenceEnd = contentText.indexOf("```", contentStart + 1);
            if (fenceEnd < 0) {
                return null;
            }
            String block = contentText.substring(contentStart + 1, fenceEnd).trim();
            if (fenceHeader.contains("json") || block.contains("protocol-dsl-0.1")) {
                JSONObject parsedDsl = parseProtocolDslCandidate(block);
                if (parsedDsl != null) {
                    return parsedDsl;
                }
            }
            searchIndex = fenceEnd + 3;
        }
        return null;
    }

    private JSONObject extractProtocolDslNearSchemaMarker(String contentText) {
        int markerIndex = contentText.indexOf("protocol-dsl-0.1");
        if (markerIndex < 0) {
            return null;
        }
        int objectStart = findJsonObjectStartBefore(contentText, markerIndex);
        while (objectStart >= 0) {
            String candidate = extractBalancedJsonObject(contentText, objectStart);
            JSONObject parsedDsl = parseProtocolDslCandidate(candidate);
            if (parsedDsl != null) {
                return parsedDsl;
            }
            objectStart = findJsonObjectStartBefore(contentText, objectStart - 1);
        }
        return null;
    }

    private int findJsonObjectStartBefore(String contentText, int markerIndex) {
        for (int index = Math.min(markerIndex, contentText.length() - 1); index >= 0; index--) {
            if (contentText.charAt(index) == '{') {
                return index;
            }
        }
        return -1;
    }

    private String extractBalancedJsonObject(String contentText, int objectStart) {
        if (objectStart < 0 || objectStart >= contentText.length() || contentText.charAt(objectStart) != '{') {
            return "";
        }
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int index = objectStart; index < contentText.length(); index++) {
            char current = contentText.charAt(index);
            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (current == '\\') {
                    escaped = true;
                } else if (current == '"') {
                    inString = false;
                }
                continue;
            }
            if (current == '"') {
                inString = true;
                continue;
            }
            if (current == '{') {
                depth++;
                continue;
            }
            if (current == '}') {
                depth--;
                if (depth == 0) {
                    return contentText.substring(objectStart, index + 1);
                }
            }
        }
        return "";
    }

    private JSONObject parseProtocolDslCandidate(String candidate) {
        if (StringUtils.isBlank(candidate)) {
            return null;
        }
        try {
            JSONObject parsed = JSON.parseObject(candidate.trim());
            return isProtocolDslCandidate(parsed) ? parsed : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private boolean isProtocolDslCandidate(JSONObject candidate) {
        if (candidate == null) {
            return false;
        }
        if ("protocol-dsl-0.1".equalsIgnoreCase(candidate.getString("schemaVersion"))) {
            return true;
        }
        return candidate.getJSONObject("protocol") != null
                && candidate.get("messageTypes") instanceof JSONArray
                && candidate.get("fields") instanceof JSONArray
                && candidate.get("codecRules") instanceof JSONArray;
    }

    private JSONObject buildQualityIssue(String level, String code, String message) {
        JSONObject issue = new JSONObject();
        issue.put("level", level);
        issue.put("code", code);
        issue.put("message", message);
        return issue;
    }

    private ValidationGateResult buildDslValidationReport(AiProtocolAdaptationTask task, Path dslSnapshotPath,
                                                          JSONObject dslSnapshot) {
        JSONObject actualSnapshot = dslSnapshot == null ? new JSONObject() : dslSnapshot;
        JSONObject protocol = actualSnapshot.getJSONObject("protocol");
        JSONArray messageTypes = readJsonArray(actualSnapshot, "messageTypes");
        JSONArray fields = readJsonArray(actualSnapshot, "fields");
        JSONArray codecRules = readJsonArray(actualSnapshot, "codecRules");
        JSONArray thingModelMappings = readJsonArray(actualSnapshot, "thingModelMappings");
        JSONArray sampleFrames = readJsonArray(actualSnapshot, "sampleFrames");
        JSONArray checks = new JSONArray();
        JSONArray issues = new JSONArray();
        boolean jsonMessageFormat = isJsonMessageFormat(protocol);

        checkProtocolMetadata(checks, issues, protocol);
        checkRequiredArray(checks, issues, messageTypes, "MESSAGE_TYPES", "报文类型",
                "dsl.messageTypes", "缺少报文类型定义，无法区分上行、下行、心跳、事件等处理入口");
        checkArrayRequiredFields(checks, issues, messageTypes, "MESSAGE_TYPE_FIELDS", "报文类型字段完整性",
                "dsl.messageTypes", new String[]{"code", "direction"});
        checkRequiredArray(checks, issues, fields, "FIELD_RULES", "字段规则",
                "dsl.fields", "缺少字段规则，无法生成稳定的 Encoder/Decoder 字段模型");
        checkArrayRequiredFields(checks, issues, fields, "FIELD_RULE_FIELDS", "字段规则完整性",
                "dsl.fields", new String[]{"fieldName", "messageType", "dataType"});
        checkJsonFieldPaths(checks, issues, fields, jsonMessageFormat);
        checkRequiredArray(checks, issues, codecRules, "CODEC_RULES", "编解码规则",
                "dsl.codecRules", "缺少编解码规则，无法确认帧边界、校验、转义、字节序或 JSON 路径规则");
        checkArrayRequiredFields(checks, issues, codecRules, "CODEC_RULE_FIELDS", "编解码规则完整性",
                "dsl.codecRules", new String[]{"ruleType", "target"});
        checkJsonCodecRules(checks, issues, codecRules, jsonMessageFormat);
        checkOptionalArray(checks, issues, thingModelMappings, "THING_MODEL_MAPPINGS", "物模型映射",
                "dsl.thingModelMappings", "未配置物模型映射，通用编解码代码仍可生成，后续可补充用于平台物模型自动绑定");
        checkArrayRequiredFields(checks, issues, thingModelMappings, "THING_MODEL_MAPPING_FIELDS", "物模型映射完整性",
                "dsl.thingModelMappings", new String[]{"identifier", "sourceField"}, "WARNING");
        checkOptionalArray(checks, issues, sampleFrames, "SAMPLE_FRAMES", "样例报文",
                "dsl.sampleFrames", "未配置可回放样例报文，通用编解码代码仍可生成，后续建议补充用于静态回归");
        checkSampleFrameRequiredFields(checks, issues, sampleFrames, jsonMessageFormat, "WARNING");
        checkGenerationStrategy(checks, issues, actualSnapshot.getJSONObject("generationStrategy"));
        appendExistingQualityIssues(checks, issues, readJsonArray(actualSnapshot, "qualityIssues"));

        int blockingIssueCount = countIssues(issues, "BLOCKER");
        int warningIssueCount = countIssues(issues, "WARNING");
        String validationStatus = blockingIssueCount > 0
                ? VALIDATION_STATUS_BLOCKED
                : warningIssueCount > 0 ? VALIDATION_STATUS_WARNING : VALIDATION_STATUS_PASSED;
        String riskLevel = blockingIssueCount > 0
                ? RISK_LEVEL_BLOCKER
                : warningIssueCount > 0 ? RISK_LEVEL_HIGH : DEFAULT_RISK_LEVEL;
        boolean canGenerateCode = blockingIssueCount == 0;
        String summary = buildValidationSummary(validationStatus, blockingIssueCount, warningIssueCount, issues);

        JSONObject report = new JSONObject();
        report.put("snapshotType", ARTIFACT_TYPE_VALIDATION_REPORT);
        report.put("schemaVersion", "protocol-validation-report-0.1");
        report.put("generatedAt", formatNow());
        report.put("taskId", task.getTaskId());
        report.put("dslSnapshotPath", dslSnapshotPath.toString());
        report.put("dslSummary", buildDslSummary(protocol, messageTypes, fields, codecRules, thingModelMappings, sampleFrames));

        JSONObject result = new JSONObject();
        result.put("validationStatus", validationStatus);
        result.put("riskLevel", riskLevel);
        result.put("canGenerateCode", canGenerateCode);
        result.put("blockingIssueCount", blockingIssueCount);
        result.put("warningIssueCount", warningIssueCount);
        result.put("checkedItemCount", checks.size());
        result.put("passedItemCount", countChecks(checks, "PASSED"));
        result.put("summary", summary);
        report.put("result", result);
        report.put("checks", checks);
        report.put("issues", issues);

        JSONObject productionGate = new JSONObject();
        productionGate.put("passed", canGenerateCode);
        productionGate.put("requiresManualConfirmation", true);
        productionGate.put("reason", summary);
        productionGate.put("nextActions", buildValidationNextActions(validationStatus));
        report.put("productionGate", productionGate);

        ValidationGateResult gateResult = new ValidationGateResult();
        gateResult.report = report;
        gateResult.validationStatus = validationStatus;
        gateResult.riskLevel = riskLevel;
        gateResult.summary = summary;
        return gateResult;
    }

    private void checkProtocolMetadata(JSONArray checks, JSONArray issues, JSONObject protocol) {
        int beginIndex = issues.size();
        if (protocol == null) {
            addIssue(issues, "BLOCKER", "PROTOCOL_META_REQUIRED", "缺少协议基础信息", "dsl.protocol");
            addCheck(checks, "PROTOCOL_META", "协议基础信息", resolveCheckStatus(issues, beginIndex),
                    "协议基础信息缺失");
            return;
        }
        if (StringUtils.isBlank(protocol.getString("protocolCode"))) {
            addIssue(issues, "BLOCKER", "PROTOCOL_CODE_REQUIRED", "缺少协议编码，无法生成稳定的协议类和包名",
                    "dsl.protocol.protocolCode");
        }
        if (StringUtils.isBlank(protocol.getString("protocolName"))) {
            addIssue(issues, "WARNING", "PROTOCOL_NAME_REQUIRED", "缺少协议名称，建议补齐便于企业审查和后续检索",
                    "dsl.protocol.protocolName");
        }
        warnIfUnknown(issues, "PROTOCOL_FAMILY_UNKNOWN", "协议族仍为 UNKNOWN，建议确认 TCP、UDP、MQTT、Modbus 或自定义协议族",
                "dsl.protocol.protocolFamily", protocol.getString("protocolFamily"));
        warnIfUnknown(issues, "MESSAGE_FORMAT_UNKNOWN", "报文格式仍为 UNKNOWN，建议确认 JSON、二进制帧或寄存器表",
                "dsl.protocol.messageFormat", protocol.getString("messageFormat"));
        warnIfUnknown(issues, "TRANSPORT_UNKNOWN", "传输方式仍为 UNKNOWN，建议确认 TCP、UDP、MQTT、串口或 HTTP",
                "dsl.protocol.transport", protocol.getString("transport"));
        addCheck(checks, "PROTOCOL_META", "协议基础信息", resolveCheckStatus(issues, beginIndex),
                "协议基础信息已完成基础校验");
    }

    private void warnIfUnknown(JSONArray issues, String code, String message, String source, String value) {
        if (StringUtils.isBlank(value) || "UNKNOWN".equalsIgnoreCase(value.trim())) {
            addIssue(issues, "WARNING", code, message, source);
        }
    }

    private boolean isJsonMessageFormat(JSONObject protocol) {
        if (protocol == null) {
            return false;
        }
        String messageFormat = trimToEmpty(protocol.getString("messageFormat")).toUpperCase(Locale.ROOT);
        return messageFormat.contains("JSON");
    }

    private boolean hasJsonValue(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof JSONObject) {
            return !((JSONObject) value).isEmpty();
        }
        if (value instanceof JSONArray) {
            return !((JSONArray) value).isEmpty();
        }
        return StringUtils.isNotBlank(String.valueOf(value));
    }

    private void checkRequiredArray(JSONArray checks, JSONArray issues, JSONArray rows, String checkCode,
                                    String checkName, String source, String blockerMessage) {
        int beginIndex = issues.size();
        if (rows == null || rows.isEmpty()) {
            addIssue(issues, "BLOCKER", checkCode + "_REQUIRED", blockerMessage, source);
        }
        addCheck(checks, checkCode, checkName, resolveCheckStatus(issues, beginIndex),
                rows == null || rows.isEmpty() ? blockerMessage : checkName + "已存在");
    }

    private void checkOptionalArray(JSONArray checks, JSONArray issues, JSONArray rows, String checkCode,
                                    String checkName, String source, String warningMessage) {
        int beginIndex = issues.size();
        if (rows == null || rows.isEmpty()) {
            addIssue(issues, "WARNING", checkCode + "_RECOMMENDED", warningMessage, source);
        }
        addCheck(checks, checkCode, checkName, resolveCheckStatus(issues, beginIndex),
                rows == null || rows.isEmpty() ? warningMessage : checkName + "已存在");
    }

    private void checkArrayRequiredFields(JSONArray checks, JSONArray issues, JSONArray rows, String checkCode,
                                          String checkName, String source, String[] requiredFields) {
        checkArrayRequiredFields(checks, issues, rows, checkCode, checkName, source, requiredFields, "BLOCKER");
    }

    private void checkArrayRequiredFields(JSONArray checks, JSONArray issues, JSONArray rows, String checkCode,
                                          String checkName, String source, String[] requiredFields,
                                          String issueLevel) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        int beginIndex = issues.size();
        for (int index = 0; index < rows.size(); index++) {
            Object row = rows.get(index);
            if (!(row instanceof JSONObject)) {
                addIssue(issues, issueLevel, checkCode + "_ROW_OBJECT_REQUIRED",
                        checkName + "第 " + (index + 1) + " 行不是结构化对象", source + "[" + index + "]");
                continue;
            }
            JSONObject rowObject = (JSONObject) row;
            for (String requiredField : requiredFields) {
                if (StringUtils.isBlank(rowObject.getString(requiredField))) {
                    addIssue(issues, issueLevel, checkCode + "_FIELD_REQUIRED",
                            checkName + "第 " + (index + 1) + " 行缺少必填字段 " + requiredField,
                            source + "[" + index + "]." + requiredField);
                }
            }
        }
        addCheck(checks, checkCode, checkName, resolveCheckStatus(issues, beginIndex),
                checkName + "已完成必填字段校验");
    }

    private void checkJsonFieldPaths(JSONArray checks, JSONArray issues, JSONArray fields,
                                     boolean jsonMessageFormat) {
        if (!jsonMessageFormat || fields == null || fields.isEmpty()) {
            return;
        }
        int beginIndex = issues.size();
        int missingPathCount = 0;
        for (int index = 0; index < fields.size(); index++) {
            Object row = fields.get(index);
            if (!(row instanceof JSONObject)) {
                continue;
            }
            JSONObject field = (JSONObject) row;
            if (StringUtils.isBlank(field.getString("jsonPath"))
                    && StringUtils.isBlank(field.getString("path"))
                    && StringUtils.isBlank(field.getString("sourceField"))) {
                missingPathCount++;
            }
        }
        if (missingPathCount > 0) {
            addIssue(issues, "WARNING", "JSON_FIELD_PATH_RECOMMENDED",
                    "JSON 报文字段中有 " + missingPathCount + " 行缺少 jsonPath，建议补齐以提升嵌套字段解析稳定性",
                    "dsl.fields");
        }
        addCheck(checks, "JSON_FIELD_PATHS", "JSON 字段路径", resolveCheckStatus(issues, beginIndex),
                missingPathCount == 0 ? "JSON 字段路径已覆盖" : "JSON 字段路径存在待补充项");
    }

    private void checkJsonCodecRules(JSONArray checks, JSONArray issues, JSONArray codecRules,
                                     boolean jsonMessageFormat) {
        if (!jsonMessageFormat || codecRules == null || codecRules.isEmpty()) {
            return;
        }
        int beginIndex = issues.size();
        boolean hasJsonCodecRule = false;
        for (int index = 0; index < codecRules.size(); index++) {
            Object row = codecRules.get(index);
            if (!(row instanceof JSONObject)) {
                continue;
            }
            JSONObject codecRule = (JSONObject) row;
            String ruleType = trimToEmpty(codecRule.getString("ruleType")).toUpperCase(Locale.ROOT);
            String target = trimToEmpty(codecRule.getString("target")).toUpperCase(Locale.ROOT);
            if (ruleType.contains("JSON")
                    || target.contains("JSON")
                    || StringUtils.isNotBlank(codecRule.getString("rootPath"))
                    || StringUtils.isNotBlank(codecRule.getString("deviceIdPath"))
                    || StringUtils.isNotBlank(codecRule.getString("timestampPath"))) {
                hasJsonCodecRule = true;
                break;
            }
        }
        if (!hasJsonCodecRule) {
            addIssue(issues, "WARNING", "JSON_CODEC_RULE_RECOMMENDED",
                    "协议报文格式为 JSON，但编解码规则未声明 JSON_PATH、JSON_BODY 或 rootPath，建议补齐用于企业复核",
                    "dsl.codecRules");
        }
        addCheck(checks, "JSON_CODEC_RULES", "JSON 编解码规则", resolveCheckStatus(issues, beginIndex),
                hasJsonCodecRule ? "JSON 编解码规则已声明" : "JSON 编解码规则建议补充");
    }

    private void checkSampleFrameRequiredFields(JSONArray checks, JSONArray issues, JSONArray sampleFrames,
                                                boolean jsonMessageFormat, String issueLevel) {
        if (sampleFrames == null || sampleFrames.isEmpty()) {
            return;
        }
        int beginIndex = issues.size();
        for (int index = 0; index < sampleFrames.size(); index++) {
            Object row = sampleFrames.get(index);
            if (!(row instanceof JSONObject)) {
                addIssue(issues, issueLevel, "SAMPLE_FRAME_FIELDS_ROW_OBJECT_REQUIRED",
                        "样例报文第 " + (index + 1) + " 行不是结构化对象", "dsl.sampleFrames[" + index + "]");
                continue;
            }
            JSONObject sampleFrame = (JSONObject) row;
            if (StringUtils.isBlank(sampleFrame.getString("messageType"))) {
                addIssue(issues, issueLevel, "SAMPLE_FRAME_FIELDS_FIELD_REQUIRED",
                        "样例报文第 " + (index + 1) + " 行缺少必填字段 messageType",
                        "dsl.sampleFrames[" + index + "].messageType");
            }
            if (StringUtils.isBlank(sampleFrame.getString("rawFrame"))
                    && (!jsonMessageFormat || !hasJsonValue(sampleFrame.get("expectedJson")))) {
                String message = jsonMessageFormat
                        ? "样例报文第 " + (index + 1) + " 行缺少 rawFrame 或 expectedJson，无法执行 JSON 样例回归"
                        : "样例报文第 " + (index + 1) + " 行缺少必填字段 rawFrame";
                addIssue(issues, issueLevel, "SAMPLE_FRAME_FIELDS_FIELD_REQUIRED",
                        message, "dsl.sampleFrames[" + index + "].rawFrame");
            }
        }
        addCheck(checks, "SAMPLE_FRAME_FIELDS", "样例报文完整性",
                resolveCheckStatus(issues, beginIndex), "样例报文已完成必填字段校验");
    }

    private void checkGenerationStrategy(JSONArray checks, JSONArray issues, JSONObject generationStrategy) {
        int beginIndex = issues.size();
        if (generationStrategy == null) {
            addIssue(issues, "WARNING", "GENERATION_STRATEGY_RECOMMENDED", "缺少生成策略，系统将按默认 fastbee-protocol 目标生成通用编解码代码包",
                    "dsl.generationStrategy");
        } else {
            Object allowCodeGenerationValue = generationStrategy.get("allowCodeGeneration");
            boolean allowCodeGeneration = Boolean.TRUE.equals(allowCodeGenerationValue)
                    || "true".equalsIgnoreCase(String.valueOf(allowCodeGenerationValue));
            if (!allowCodeGeneration) {
                addIssue(issues, "WARNING", "CODE_GENERATION_NOT_EXPLICITLY_ALLOWED_BY_DSL",
                        "DSL 生成策略未显式允许代码生成；若协议基础信息、报文类型、字段规则和编解码规则已通过，系统仍允许生成通用编解码代码包",
                        "dsl.generationStrategy.allowCodeGeneration");
            }
        }
        addCheck(checks, "GENERATION_STRATEGY", "生成策略", resolveCheckStatus(issues, beginIndex),
                "生成策略已完成质量门禁校验");
    }

    private void appendExistingQualityIssues(JSONArray checks, JSONArray issues, JSONArray qualityIssues) {
        int beginIndex = issues.size();
        if (qualityIssues == null || qualityIssues.isEmpty()) {
            addCheck(checks, "DSL_QUALITY_ISSUES", "DSL 内置风险项", "PASSED", "DSL 未声明额外风险项");
            return;
        }
        for (int index = 0; index < qualityIssues.size(); index++) {
            Object row = qualityIssues.get(index);
            if (!(row instanceof JSONObject)) {
                continue;
            }
            JSONObject issue = (JSONObject) row;
            String level = defaultIfBlank(issue.getString("level"), "").toUpperCase(Locale.ROOT);
            if (!"BLOCKER".equals(level) && !"WARNING".equals(level)) {
                continue;
            }
            if ("BLOCKER".equals(level)) {
                level = "WARNING";
            }
            addIssue(issues, level, defaultIfBlank(issue.getString("code"), "DSL_DECLARED_ISSUE"),
                    defaultIfBlank(issue.getString("message"), "DSL 声明了未命名风险项"),
                    "dsl.qualityIssues[" + index + "]");
        }
        addCheck(checks, "DSL_QUALITY_ISSUES", "DSL 内置风险项", resolveCheckStatus(issues, beginIndex),
                "已合并 DSL 内置风险项");
    }

    private JSONObject buildDslSummary(JSONObject protocol, JSONArray messageTypes, JSONArray fields,
                                       JSONArray codecRules, JSONArray thingModelMappings, JSONArray sampleFrames) {
        JSONObject summary = new JSONObject();
        summary.put("protocolCode", protocol == null ? "" : trimToEmpty(protocol.getString("protocolCode")));
        summary.put("protocolName", protocol == null ? "" : trimToEmpty(protocol.getString("protocolName")));
        summary.put("protocolFamily", protocol == null ? "" : trimToEmpty(protocol.getString("protocolFamily")));
        summary.put("messageFormat", protocol == null ? "" : trimToEmpty(protocol.getString("messageFormat")));
        summary.put("transport", protocol == null ? "" : trimToEmpty(protocol.getString("transport")));
        summary.put("messageTypeCount", messageTypes == null ? 0 : messageTypes.size());
        summary.put("fieldCount", fields == null ? 0 : fields.size());
        summary.put("codecRuleCount", codecRules == null ? 0 : codecRules.size());
        summary.put("thingModelMappingCount", thingModelMappings == null ? 0 : thingModelMappings.size());
        summary.put("sampleFrameCount", sampleFrames == null ? 0 : sampleFrames.size());
        return summary;
    }

    private JSONArray buildValidationNextActions(String validationStatus) {
        JSONArray actions = new JSONArray();
        if (VALIDATION_STATUS_BLOCKED.equals(validationStatus)) {
            actions.add("补齐协议基础信息、报文类型、字段规则和编解码规则");
            actions.add("通过企业工作簿或 AI 增强解析回灌可生成通用编解码代码的 DSL");
            actions.add("重新执行质量门禁，确认阻断项已消除");
            return actions;
        }
        if (VALIDATION_STATUS_WARNING.equals(validationStatus)) {
            actions.add("可先生成通用编解码代码包");
            actions.add("后续按需补充物模型映射、枚举、单位、字段范围、Topic 模板和样例报文");
            return actions;
        }
        actions.add("保留校验报告并进入协议代码生成前置确认");
        return actions;
    }

    private String buildValidationSummary(String validationStatus, int blockingIssueCount,
                                          int warningIssueCount, JSONArray issues) {
        if (VALIDATION_STATUS_BLOCKED.equals(validationStatus)) {
            return "DSL 质量门禁已阻断：" + blockingIssueCount + " 个阻断项、"
                    + warningIssueCount + " 个告警项；" + joinFirstIssueMessages(issues, "BLOCKER");
        }
        if (VALIDATION_STATUS_WARNING.equals(validationStatus)) {
            return "DSL 质量门禁有告警：" + warningIssueCount + " 个告警项；"
                    + joinFirstIssueMessages(issues, "WARNING");
        }
        return "DSL 质量门禁通过，可进入协议代码生成前置确认";
    }

    private String joinFirstIssueMessages(JSONArray issues, String level) {
        List<String> messages = new ArrayList<>();
        for (int index = 0; index < issues.size(); index++) {
            JSONObject issue = issues.getJSONObject(index);
            if (issue != null && level.equalsIgnoreCase(issue.getString("level"))) {
                messages.add(issue.getString("message"));
            }
            if (messages.size() >= 3) {
                break;
            }
        }
        return messages.isEmpty() ? "请查看校验报告详情" : String.join("；", messages);
    }

    private JSONArray readJsonArray(JSONObject object, String key) {
        Object value = object == null ? null : object.get(key);
        return value instanceof JSONArray ? (JSONArray) value : new JSONArray();
    }

    private void addCheck(JSONArray checks, String code, String name, String status, String message) {
        JSONObject check = new JSONObject();
        check.put("code", code);
        check.put("name", name);
        check.put("status", status);
        check.put("message", message);
        checks.add(check);
    }

    private void addIssue(JSONArray issues, String level, String code, String message, String source) {
        JSONObject issue = new JSONObject();
        issue.put("level", normalizeIssueLevel(level));
        issue.put("code", code);
        issue.put("message", message);
        issue.put("source", source);
        issues.add(issue);
    }

    private String resolveCheckStatus(JSONArray issues, int beginIndex) {
        boolean warning = false;
        for (int index = beginIndex; index < issues.size(); index++) {
            JSONObject issue = issues.getJSONObject(index);
            if (issue == null) {
                continue;
            }
            if ("BLOCKER".equalsIgnoreCase(issue.getString("level"))) {
                return "BLOCKED";
            }
            if ("WARNING".equalsIgnoreCase(issue.getString("level"))) {
                warning = true;
            }
        }
        return warning ? "WARNING" : "PASSED";
    }

    private int countIssues(JSONArray issues, String level) {
        int count = 0;
        for (int index = 0; index < issues.size(); index++) {
            JSONObject issue = issues.getJSONObject(index);
            if (issue != null && level.equalsIgnoreCase(issue.getString("level"))) {
                count++;
            }
        }
        return count;
    }

    private int countChecks(JSONArray checks, String status) {
        int count = 0;
        for (int index = 0; index < checks.size(); index++) {
            JSONObject check = checks.getJSONObject(index);
            if (check != null && status.equalsIgnoreCase(check.getString("status"))) {
                count++;
            }
        }
        return count;
    }

    private String normalizeIssueLevel(String level) {
        String actualLevel = defaultIfBlank(level, "WARNING").toUpperCase(Locale.ROOT);
        return "BLOCKER".equals(actualLevel) ? "BLOCKER" : "WARNING";
    }

    private void requireCodeGenerationAllowed(AiProtocolAdaptationTask task) {
        if (task == null) {
            throw new ServiceException(message("ai.protocol.adaptation.task.required"));
        }
        String validationStatus = defaultIfBlank(task.getValidationStatus(), DEFAULT_VALIDATION_STATUS);
        if (!VALIDATION_STATUS_PASSED.equals(validationStatus) && !VALIDATION_STATUS_WARNING.equals(validationStatus)) {
            throw new ServiceException(message("ai.protocol.adaptation.quality.gate.required"));
        }
        if (RISK_LEVEL_BLOCKER.equals(defaultIfBlank(task.getRiskLevel(), DEFAULT_RISK_LEVEL))) {
            throw new ServiceException(message("ai.protocol.adaptation.blocking.risk.denied"));
        }
    }

    private Path resolveGenerationFilePath(AiProtocolGenerationRecord record, String fileType) {
        String actualPath = selectGenerationFilePath(record, fileType);
        if (StringUtils.isBlank(actualPath)) {
            throw new ServiceException(message("ai.protocol.generation.record.file.path.required"));
        }
        try {
            Path filePath = Paths.get(actualPath).toAbsolutePath().normalize();
            Path allowedRoot = Paths.get(RuoYiConfig.getProfile(), "ai", "protocol", "adaptation")
                    .toAbsolutePath()
                    .normalize();
            if (!filePath.startsWith(allowedRoot)) {
                throw new ServiceException(message("ai.protocol.generation.file.path.outside.denied"));
            }
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                throw new ServiceException(message("ai.protocol.generation.file.not.downloadable", filePath));
            }
            return filePath;
        } catch (InvalidPathException ex) {
            throw new ServiceException(message("ai.protocol.generation.file.path.invalid", actualPath));
        }
    }

    private Path resolveArtifactFilePath(AiProtocolAdaptationArtifact artifact) {
        if (artifact == null || StringUtils.isBlank(artifact.getFilePath())) {
            throw new ServiceException(message("ai.protocol.artifact.file.path.not.configured"));
        }
        String actualPath = artifact.getFilePath().trim();
        try {
            Path filePath = Paths.get(actualPath).toAbsolutePath().normalize();
            Path allowedRoot = Paths.get(RuoYiConfig.getProfile(), "ai", "protocol", "adaptation")
                    .toAbsolutePath()
                    .normalize();
            if (!filePath.startsWith(allowedRoot)) {
                throw new ServiceException(message("ai.protocol.artifact.file.path.outside.denied"));
            }
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                throw new ServiceException(message("ai.protocol.artifact.file.not.downloadable", filePath));
            }
            return filePath;
        } catch (InvalidPathException ex) {
            throw new ServiceException(message("ai.protocol.artifact.file.path.invalid", actualPath));
        }
    }

    private boolean isPreviewableArtifactFile(Path filePath, AiProtocolAdaptationArtifact artifact) {
        String artifactType = artifact == null ? "" : defaultIfBlank(artifact.getArtifactType(), "");
        if (ARTIFACT_TYPE_VALIDATION_REPORT.equals(artifactType)
                || ARTIFACT_TYPE_AI_DSL_DRAFT.equals(artifactType)
                || ARTIFACT_TYPE_EXTRACTED_TEXT.equals(artifactType)) {
            return true;
        }
        String fileName = filePath == null || filePath.getFileName() == null ? "" : filePath.getFileName().toString();
        String suffix = getFileSuffix(fileName).toLowerCase(Locale.ROOT);
        return ".json".equals(suffix)
                || ".txt".equals(suffix)
                || ".md".equals(suffix)
                || ".csv".equals(suffix)
                || ".log".equals(suffix);
    }

    private String selectGenerationFilePath(AiProtocolGenerationRecord record, String fileType) {
        if (record == null) {
            throw new ServiceException(message("ai.protocol.generation.record.required"));
        }
        String actualType = defaultIfBlank(fileType, "").toLowerCase(Locale.ROOT);
        if ("package".equals(actualType) || "codepackage".equals(actualType) || "code_package".equals(actualType)) {
            return record.getCodePackagePath();
        }
        if ("manifest".equals(actualType) || "filemanifest".equals(actualType) || "file_manifest".equals(actualType)) {
            return record.getFileManifestPath();
        }
        if ("testreport".equals(actualType) || "test_report".equals(actualType) || "report".equals(actualType)) {
            return record.getTestReportPath();
        }
        throw new ServiceException(message("ai.protocol.generation.file.type.unsupported", fileType));
    }

    private String buildGenerationConfirmRemark(AiProtocolGenerationRecord record) {
        String confirmRemark = "人工已确认代码包交付，可进入编译回归与部署审批";
        String oldRemark = record == null ? "" : defaultIfBlank(record.getRemark(), "");
        if (oldRemark.contains(confirmRemark)) {
            return oldRemark;
        }
        return abbreviate(StringUtils.isBlank(oldRemark) ? confirmRemark : oldRemark + "；" + confirmRemark, 500);
    }

    private AiProtocolGenerationRecord buildGeneratingRecord(AiProtocolAdaptationTask task) {
        AiProtocolGenerationRecord record = new AiProtocolGenerationRecord();
        record.setTaskId(task.getTaskId());
        record.setDslSnapshotPath(trimToEmpty(task.getDslSnapshotPath()));
        record.setGenerationStrategy("");
        record.setCompileStatus(COMPILE_STATUS_PENDING);
        record.setValidationErrorCount(0);
        record.setValidationWarningCount(0);
        record.setGenerationStatus(GENERATION_STATUS_GENERATING);
        record.setFailureReason("");
        record.setStatus(STATUS_ENABLED);
        record.setCreateBy(AiSecuritySupport.resolveUsername());
        record.setCreateTime(AiSecuritySupport.now());
        record.setUpdateBy(AiSecuritySupport.resolveUsername());
        record.setUpdateTime(AiSecuritySupport.now());
        return record;
    }

    private void updateGenerationRecordSuccess(Long recordId, CodePackageBuildResult result) {
        AiProtocolGenerationRecord update = new AiProtocolGenerationRecord();
        update.setRecordId(recordId);
        update.setDslSnapshotPath(result.dslSnapshotPath);
        update.setGenerationStrategy(result.generationStrategy);
        update.setCodePackagePath(result.codePackagePath);
        update.setFileManifestPath(result.fileManifestPath);
        update.setTestReportPath(result.testReportPath);
        update.setCompileStatus(COMPILE_STATUS_PENDING);
        update.setValidationErrorCount(result.validationErrorCount);
        update.setValidationWarningCount(result.validationWarningCount);
        update.setGenerationStatus(GENERATION_STATUS_SUCCESS);
        update.setFailureReason("");
        update.setRemark("首轮生成代码包已落盘，编译与样例回归需在后续验证环境执行");
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        aiProtocolGenerationRecordMapper.updateById(update);
    }

    private void updateGenerationRecordFailure(Long recordId, String failureReason) {
        AiProtocolGenerationRecord update = new AiProtocolGenerationRecord();
        update.setRecordId(recordId);
        update.setCompileStatus(COMPILE_STATUS_PENDING);
        update.setGenerationStatus(GENERATION_STATUS_FAILED);
        update.setFailureReason(abbreviate(defaultIfBlank(failureReason, "协议代码包生成失败"), 1000));
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        aiProtocolGenerationRecordMapper.updateById(update);
    }

    private void updateGenerationRecordVerification(AiProtocolGenerationRecord record,
                                                    CodePackageVerificationResult result) {
        AiProtocolGenerationRecord update = new AiProtocolGenerationRecord();
        update.setRecordId(record.getRecordId());
        update.setCompileStatus(result.compileStatus);
        update.setFailureReason(COMPILE_STATUS_STATIC_FAILED.equals(result.compileStatus)
                ? abbreviate(result.summary, 1000) : "");
        update.setRemark(buildGenerationVerificationRemark(record, result));
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        aiProtocolGenerationRecordMapper.updateById(update);
    }

    private String buildGenerationVerificationRemark(AiProtocolGenerationRecord record,
                                                     CodePackageVerificationResult result) {
        String verifyRemark = "静态验证状态=" + result.compileStatus + "；样例回归状态=" + result.sampleReplayStatus;
        String oldRemark = record == null ? "" : defaultIfBlank(record.getRemark(), "");
        if (oldRemark.contains("静态验证状态=")) {
            return abbreviate(verifyRemark, 500);
        }
        return abbreviate(StringUtils.isBlank(oldRemark) ? verifyRemark : oldRemark + "；" + verifyRemark, 500);
    }

    private void requireGenerationVerificationPassed(AiProtocolGenerationRecord record) {
        String compileStatus = defaultIfBlank(record.getCompileStatus(), COMPILE_STATUS_PENDING);
        if (COMPILE_STATUS_PENDING.equals(compileStatus)) {
            throw new ServiceException(message("ai.protocol.generation.static.verify.required.confirm"));
        }
        if (COMPILE_STATUS_STATIC_FAILED.equals(compileStatus) || GENERATION_STATUS_FAILED.equals(compileStatus)) {
            throw new ServiceException(message("ai.protocol.generation.static.verify.failed.confirm"));
        }
        Set<String> allowedStatuses = Set.of(COMPILE_STATUS_STATIC_PASSED, COMPILE_STATUS_STATIC_WARNING,
                VALIDATION_STATUS_PASSED, VALIDATION_STATUS_WARNING);
        if (!allowedStatuses.contains(compileStatus)) {
            throw new ServiceException(message("ai.protocol.generation.confirm.status.denied", compileStatus));
        }
    }

    private CodePackageBuildResult buildCodePackage(AiProtocolAdaptationTask task, Path dslSnapshotPath,
                                                    JSONObject dslSnapshot) throws IOException {
        JSONObject actualSnapshot = dslSnapshot == null ? new JSONObject() : dslSnapshot;
        JSONObject protocol = actualSnapshot.getJSONObject("protocol");
        JSONObject generationStrategy = actualSnapshot.getJSONObject("generationStrategy");
        GeneratedCodeMeta meta = resolveGeneratedCodeMeta(task, protocol, generationStrategy);
        ValidationReportSummary validationSummary = readLatestValidationReportSummary(task.getTaskId());

        Map<String, byte[]> files = new LinkedHashMap<>();
        String javaPath = "src/main/java/" + meta.packageName.replace('.', '/') + "/" + meta.className + ".java";
        files.put(javaPath, buildProtocolServiceJava(meta, actualSnapshot).getBytes(StandardCharsets.UTF_8));
        files.put("src/main/resources/protocol-dsl.json", JSON.toJSONString(actualSnapshot).getBytes(StandardCharsets.UTF_8));
        files.put("README.md", buildCodePackageReadme(task, meta, actualSnapshot).getBytes(StandardCharsets.UTF_8));

        JSONObject manifest = buildFileManifest(task, meta, dslSnapshotPath, files, validationSummary);
        byte[] manifestBytes = JSON.toJSONString(manifest).getBytes(StandardCharsets.UTF_8);
        files.put("generation-manifest.json", manifestBytes);
        JSONObject testReport = buildInitialTestReport(task, meta, validationSummary);
        byte[] testReportBytes = JSON.toJSONString(testReport).getBytes(StandardCharsets.UTF_8);
        files.put("test-report.json", testReportBytes);

        byte[] zipBytes = buildZipBytes(files);
        String baseName = safePathSegment(meta.protocolCode) + "_protocol_package_" + System.currentTimeMillis();
        Path packagePath = saveGeneratedFile(task, "code_package", baseName + ".zip", zipBytes);
        Path manifestPath = saveGeneratedFile(task, "code_package", baseName + "_manifest.json", manifestBytes);
        Path testReportPath = saveGeneratedFile(task, "code_package", baseName + "_test_report.json", testReportBytes);

        CodePackageBuildResult result = new CodePackageBuildResult();
        result.dslSnapshotPath = dslSnapshotPath.toString();
        result.generationStrategy = JSON.toJSONString(generationStrategy == null ? new JSONObject() : generationStrategy);
        result.codePackagePath = packagePath.toString();
        result.fileManifestPath = manifestPath.toString();
        result.testReportPath = testReportPath.toString();
        result.validationErrorCount = validationSummary.blockingIssueCount;
        result.validationWarningCount = validationSummary.warningIssueCount;
        return result;
    }

    private GeneratedCodeMeta resolveGeneratedCodeMeta(AiProtocolAdaptationTask task, JSONObject protocol,
                                                       JSONObject generationStrategy) {
        JSONObject actualProtocol = protocol == null ? new JSONObject() : protocol;
        JSONObject actualStrategy = generationStrategy == null ? new JSONObject() : generationStrategy;
        String protocolCode = defaultIfBlank(actualProtocol.getString("protocolCode"),
                defaultIfBlank(task.getProtocolCode(), "TASK_" + task.getTaskId()));
        String protocolName = defaultIfBlank(actualProtocol.getString("protocolName"),
                defaultIfBlank(task.getProtocolName(), task.getTaskName()));
        String packageName = defaultIfBlank(actualStrategy.getString("packageName"),
                "com.fastbee.generated.protocol." + toJavaPackageSegment(protocolCode));
        String className = defaultIfBlank(actualStrategy.getString("className"),
                toJavaClassName(protocolCode) + "ProtocolService");
        GeneratedCodeMeta meta = new GeneratedCodeMeta();
        meta.protocolCode = protocolCode;
        meta.protocolName = protocolName;
        meta.packageName = normalizeJavaPackage(packageName);
        meta.className = toJavaClassName(className);
        meta.description = defaultIfBlank(actualStrategy.getString("reason"), "AI 协议适配生成的 FastBee 协议骨架");
        return meta;
    }

    private String buildProtocolServiceJava(GeneratedCodeMeta meta, JSONObject dslSnapshot) {
        JSONArray mappings = readJsonArray(dslSnapshot, "thingModelMappings");
        String sampleDecodePayload = resolveMainDecodePayload(dslSnapshot, mappings);
        String sampleEncodeParams = resolveMainEncodeParams(dslSnapshot, mappings);
        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(meta.packageName).append(";\n\n");
        builder.append("import com.alibaba.fastjson2.JSON;\n");
        builder.append("import com.alibaba.fastjson2.JSONArray;\n");
        builder.append("import com.alibaba.fastjson2.JSONObject;\n");
        builder.append("import com.fastbee.common.annotation.SysProtocol;\n");
        builder.append("import com.fastbee.common.extend.core.domin.mq.DeviceReport;\n");
        builder.append("import com.fastbee.common.extend.core.domin.mq.MQSendMessageBo;\n");
        builder.append("import com.fastbee.common.extend.core.domin.mq.message.DeviceData;\n");
        builder.append("import com.fastbee.common.extend.core.domin.mq.message.FunctionCallBackBo;\n");
        builder.append("import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;\n");
        builder.append("import com.fastbee.protocol.base.protocol.IProtocol;\n");
        builder.append("import lombok.extern.slf4j.Slf4j;\n");
        builder.append("import org.springframework.stereotype.Component;\n\n");
        builder.append("import java.nio.charset.StandardCharsets;\n");
        builder.append("import java.util.ArrayList;\n");
        builder.append("import java.util.Date;\n");
        builder.append("import java.util.List;\n");
        builder.append("import java.util.Map;\n\n");
        builder.append("/**\n");
        builder.append(" * AI 协议适配生成的 FastBee 协议服务。\n");
        builder.append(" * 生成时间：").append(formatNow()).append("\n");
        builder.append(" */\n");
        builder.append("@Slf4j\n");
        builder.append("@Component\n");
        builder.append("@SysProtocol(name = \"").append(escapeJava(meta.protocolName)).append("\", protocolCode = \"")
                .append(escapeJava(meta.protocolCode)).append("\", description = \"")
                .append(escapeJava(meta.description)).append("\")\n");
        builder.append("public class ").append(meta.className).append(" implements IProtocol {\n\n");
        builder.append("    private static final String[][] FIELD_MAPPINGS = new String[][] {\n");
        if (mappings.isEmpty()) {
            builder.append("            {\"\", \"\"}\n");
        } else {
            for (int index = 0; index < mappings.size(); index++) {
                JSONObject mapping = mappings.getJSONObject(index);
                String identifier = mapping == null ? "" : trimToEmpty(mapping.getString("identifier"));
                String sourceField = mapping == null ? "" : trimToEmpty(mapping.getString("sourceField"));
                builder.append("            {\"").append(escapeJava(identifier)).append("\", \"")
                        .append(escapeJava(sourceField)).append("\"}");
                builder.append(index + 1 < mappings.size() ? ",\n" : "\n");
            }
        }
        builder.append("    };\n\n");
        builder.append("    private static final String SAMPLE_DECODE_PAYLOAD = \"")
                .append(escapeJava(sampleDecodePayload)).append("\";\n");
        builder.append("    private static final String SAMPLE_ENCODE_PARAMS = \"")
                .append(escapeJava(sampleEncodeParams)).append("\";\n\n");
        builder.append("    @Override\n");
        builder.append("    public DeviceReport decode(DeviceData deviceData, String clientId) {\n");
        builder.append("        DeviceReport report = new DeviceReport();\n");
        builder.append("        String payload = readPayload(deviceData);\n");
        builder.append("        report.setSerialNumber(clientId);\n");
        builder.append("        report.setSources(payload);\n");
        builder.append("        List<ThingsModelSimpleItem> items = new ArrayList<>();\n");
        builder.append("        JSONObject root = parseJsonPayload(payload);\n");
        builder.append("        if (root != null && !root.isEmpty()) {\n");
        builder.append("            appendMappedItems(root, items);\n");
        builder.append("            if (items.isEmpty()) {\n");
        builder.append("                appendFlatJsonItems(root, items);\n");
        builder.append("            }\n");
        builder.append("        }\n");
        builder.append("        report.setThingsModelSimpleItem(items);\n");
        builder.append("        return report;\n");
        builder.append("    }\n\n");
        builder.append("    @Override\n");
        builder.append("    public FunctionCallBackBo encode(MQSendMessageBo message) {\n");
        builder.append("        FunctionCallBackBo callBack = new FunctionCallBackBo();\n");
        builder.append("        String payload = message == null || message.getParams() == null ? \"{}\" : JSON.toJSONString(message.getParams());\n");
        builder.append("        callBack.setSources(payload);\n");
        builder.append("        callBack.setMessage(payload.getBytes(StandardCharsets.UTF_8));\n");
        builder.append("        return callBack;\n");
        builder.append("    }\n\n");
        builder.append("    public static void main(String[] args) {\n");
        builder.append("        ").append(meta.className).append(" protocol = new ").append(meta.className).append("();\n");
        builder.append("        String demoSerialNumber = \"demo-device-001\";\n");
        builder.append("        DeviceData deviceData = DeviceData.builder()\n");
        builder.append("                .serialNumber(demoSerialNumber)\n");
        builder.append("                .data(SAMPLE_DECODE_PAYLOAD.getBytes(StandardCharsets.UTF_8))\n");
        builder.append("                .build();\n");
        builder.append("        DeviceReport report = protocol.decode(deviceData, demoSerialNumber);\n");
        builder.append("        System.out.println(\"=== Decode 示例 ===\");\n");
        builder.append("        System.out.println(\"原始报文: \" + SAMPLE_DECODE_PAYLOAD);\n");
        builder.append("        System.out.println(\"解析结果: \" + JSON.toJSONString(report));\n\n");
        builder.append("        MQSendMessageBo message = new MQSendMessageBo();\n");
        builder.append("        message.setSerialNumber(demoSerialNumber);\n");
        builder.append("        message.setIdentifier(\"demo-command\");\n");
        builder.append("        message.setParams(JSON.parseObject(SAMPLE_ENCODE_PARAMS));\n");
        builder.append("        FunctionCallBackBo encoded = protocol.encode(message);\n");
        builder.append("        System.out.println(\"=== Encode 示例 ===\");\n");
        builder.append("        System.out.println(\"下行参数: \" + SAMPLE_ENCODE_PARAMS);\n");
        builder.append("        System.out.println(\"编码原文: \" + encoded.getSources());\n");
        builder.append("        System.out.println(\"编码字节UTF-8: \" + new String(encoded.getMessage(), StandardCharsets.UTF_8));\n");
        builder.append("    }\n\n");
        builder.append("    private String readPayload(DeviceData deviceData) {\n");
        builder.append("        if (deviceData == null || deviceData.getData() == null) {\n");
        builder.append("            return \"\";\n");
        builder.append("        }\n");
        builder.append("        return new String(deviceData.getData(), StandardCharsets.UTF_8);\n");
        builder.append("    }\n\n");
        builder.append("    private JSONObject parseJsonPayload(String payload) {\n");
        builder.append("        try {\n");
        builder.append("            return JSON.parseObject(payload);\n");
        builder.append("        } catch (Exception ex) {\n");
        builder.append("            log.warn(\"协议 ").append(escapeJava(meta.protocolCode)).append(" 当前生成包未能按 JSON 解析报文，将仅保留原始 sources\", ex);\n");
        builder.append("            return new JSONObject();\n");
        builder.append("        }\n");
        builder.append("    }\n\n");
        builder.append("    private void appendMappedItems(JSONObject root, List<ThingsModelSimpleItem> items) {\n");
        builder.append("        for (String[] mapping : FIELD_MAPPINGS) {\n");
        builder.append("            if (mapping.length < 2 || mapping[0].isBlank() || mapping[1].isBlank()) {\n");
        builder.append("                continue;\n");
        builder.append("            }\n");
        builder.append("            Object value = readJsonPath(root, mapping[1]);\n");
        builder.append("            if (value != null) {\n");
        builder.append("                addItem(items, mapping[0], value);\n");
        builder.append("            }\n");
        builder.append("        }\n");
        builder.append("    }\n\n");
        builder.append("    private Object readJsonPath(JSONObject root, String path) {\n");
        builder.append("        String normalizedPath = normalizeJsonPath(path);\n");
        builder.append("        if (normalizedPath.isBlank()) {\n");
        builder.append("            return root;\n");
        builder.append("        }\n");
        builder.append("        Object current = root;\n");
        builder.append("        for (String part : normalizedPath.split(\"\\\\.\")) {\n");
        builder.append("            if (part.isBlank()) {\n");
        builder.append("                continue;\n");
        builder.append("            }\n");
        builder.append("            Object next = readJsonPathSegment(current, part);\n");
        builder.append("            if (next == null) {\n");
        builder.append("                return null;\n");
        builder.append("            }\n");
        builder.append("            current = next;\n");
        builder.append("        }\n");
        builder.append("        return current;\n");
        builder.append("    }\n\n");
        builder.append("    private Object readJsonPathSegment(Object current, String segment) {\n");
        builder.append("        String actual = segment == null ? \"\" : segment.trim();\n");
        builder.append("        if (actual.isBlank()) {\n");
        builder.append("            return current;\n");
        builder.append("        }\n");
        builder.append("        int bracketIndex = actual.indexOf('[');\n");
        builder.append("        String key = bracketIndex >= 0 ? actual.substring(0, bracketIndex) : actual;\n");
        builder.append("        Object value = current;\n");
        builder.append("        if (!key.isBlank()) {\n");
        builder.append("            if (!(value instanceof JSONObject jsonObject)) {\n");
        builder.append("                return null;\n");
        builder.append("            }\n");
        builder.append("            value = jsonObject.get(key);\n");
        builder.append("        }\n");
        builder.append("        int cursor = bracketIndex;\n");
        builder.append("        while (cursor >= 0 && cursor < actual.length()) {\n");
        builder.append("            int end = actual.indexOf(']', cursor);\n");
        builder.append("            if (end <= cursor + 1) {\n");
        builder.append("                return null;\n");
        builder.append("            }\n");
        builder.append("            int arrayIndex;\n");
        builder.append("            try {\n");
        builder.append("                arrayIndex = Integer.parseInt(actual.substring(cursor + 1, end));\n");
        builder.append("            } catch (NumberFormatException ex) {\n");
        builder.append("                return null;\n");
        builder.append("            }\n");
        builder.append("            if (!(value instanceof JSONArray array) || arrayIndex < 0 || arrayIndex >= array.size()) {\n");
        builder.append("                return null;\n");
        builder.append("            }\n");
        builder.append("            value = array.get(arrayIndex);\n");
        builder.append("            cursor = actual.indexOf('[', end + 1);\n");
        builder.append("        }\n");
        builder.append("        return value;\n");
        builder.append("    }\n\n");
        builder.append("    private String normalizeJsonPath(String path) {\n");
        builder.append("        if (path == null) {\n");
        builder.append("            return \"\";\n");
        builder.append("        }\n");
        builder.append("        String actual = path.trim();\n");
        builder.append("        if (actual.startsWith(\"$.\")) {\n");
        builder.append("            return actual.substring(2);\n");
        builder.append("        }\n");
        builder.append("        if (actual.startsWith(\"$\")) {\n");
        builder.append("            return actual.substring(1);\n");
        builder.append("        }\n");
        builder.append("        return actual;\n");
        builder.append("    }\n\n");
        builder.append("    private void appendFlatJsonItems(JSONObject root, List<ThingsModelSimpleItem> items) {\n");
        builder.append("        for (Map.Entry<String, Object> entry : root.entrySet()) {\n");
        builder.append("            if (entry.getValue() != null && !(entry.getValue() instanceof JSONObject)) {\n");
        builder.append("                addItem(items, entry.getKey(), entry.getValue());\n");
        builder.append("            }\n");
        builder.append("        }\n");
        builder.append("    }\n\n");
        builder.append("    private void addItem(List<ThingsModelSimpleItem> items, String identifier, Object value) {\n");
        builder.append("        ThingsModelSimpleItem item = new ThingsModelSimpleItem();\n");
        builder.append("        item.setId(identifier);\n");
        builder.append("        item.setValue(String.valueOf(value));\n");
        builder.append("        item.setTs(new Date());\n");
        builder.append("        items.add(item);\n");
        builder.append("    }\n");
        builder.append("}\n");
        return builder.toString();
    }

    private String resolveMainDecodePayload(JSONObject dslSnapshot, JSONArray mappings) {
        JSONArray sampleFrames = readJsonArray(dslSnapshot, "sampleFrames");
        for (int index = 0; index < sampleFrames.size(); index++) {
            Object row = sampleFrames.get(index);
            if (!(row instanceof JSONObject)) {
                continue;
            }
            JSONObject sampleFrame = (JSONObject) row;
            String rawFrame = trimToEmpty(sampleFrame.getString("rawFrame"));
            if (StringUtils.isBlank(rawFrame) && hasJsonValue(sampleFrame.get("expectedJson"))) {
                rawFrame = JSON.toJSONString(sampleFrame.get("expectedJson"));
            }
            if (isUsableMainSample(rawFrame)) {
                return rawFrame;
            }
        }
        return buildDefaultMainJsonPayload(mappings);
    }

    private String resolveMainEncodeParams(JSONObject dslSnapshot, JSONArray mappings) {
        JSONArray sampleFrames = readJsonArray(dslSnapshot, "sampleFrames");
        for (int index = 0; index < sampleFrames.size(); index++) {
            Object row = sampleFrames.get(index);
            if (!(row instanceof JSONObject)) {
                continue;
            }
            JSONObject sampleFrame = (JSONObject) row;
            Object expectedJson = sampleFrame.get("expectedJson");
            if (expectedJson instanceof JSONObject && isUsableMainSample(JSON.toJSONString(expectedJson))) {
                return JSON.toJSONString(expectedJson);
            }
            String rawFrame = trimToEmpty(sampleFrame.getString("rawFrame"));
            if (looksJsonObject(rawFrame) && isUsableMainSample(rawFrame)) {
                return rawFrame;
            }
        }
        return buildDefaultMainJsonPayload(mappings);
    }

    private boolean isUsableMainSample(String value) {
        return StringUtils.isNotBlank(value) && value.length() <= 8000;
    }

    private String buildDefaultMainJsonPayload(JSONArray mappings) {
        JSONObject payload = new JSONObject();
        for (int index = 0; index < mappings.size(); index++) {
            Object row = mappings.get(index);
            if (!(row instanceof JSONObject)) {
                continue;
            }
            JSONObject mapping = (JSONObject) row;
            String identifier = trimToEmpty(mapping.getString("identifier"));
            String sourceField = defaultIfBlank(mapping.getString("sourceField"), identifier);
            if (StringUtils.isBlank(sourceField)) {
                continue;
            }
            putMainSampleJsonValue(payload, sourceField, buildMainSampleValue(identifier, index));
        }
        if (payload.isEmpty()) {
            payload.put("sample", "value");
        }
        return JSON.toJSONString(payload);
    }

    private Object buildMainSampleValue(String identifier, int index) {
        String actual = trimToEmpty(identifier).toLowerCase(Locale.ROOT);
        if (actual.contains("switch") || actual.contains("status") || actual.contains("enable")
                || actual.contains("open") || actual.contains("close") || actual.contains("开关")
                || actual.contains("状态")) {
            return true;
        }
        if (actual.contains("temp") || actual.contains("humidity") || actual.contains("level")
                || actual.contains("pressure") || actual.contains("voltage") || actual.contains("current")
                || actual.contains("co2") || actual.contains("温") || actual.contains("湿")
                || actual.contains("电压") || actual.contains("电流")) {
            return 20 + index;
        }
        return "sample-" + defaultIfBlank(identifier, String.valueOf(index + 1));
    }

    private void putMainSampleJsonValue(JSONObject root, String path, Object value) {
        String normalizedPath = normalizeMainSampleJsonPath(path);
        if (StringUtils.isBlank(normalizedPath)) {
            root.put("value", value);
            return;
        }
        String[] segments = normalizedPath.split("\\.");
        JSONObject current = root;
        for (int index = 0; index < segments.length; index++) {
            String segment = trimToEmpty(segments[index]);
            if (StringUtils.isBlank(segment)) {
                continue;
            }
            boolean last = index == segments.length - 1;
            current = putMainSampleJsonSegment(current, segment, value, last);
        }
    }

    private JSONObject putMainSampleJsonSegment(JSONObject current, String segment, Object value, boolean last) {
        int bracketIndex = segment.indexOf('[');
        String key = bracketIndex >= 0 ? segment.substring(0, bracketIndex) : segment;
        if (StringUtils.isBlank(key)) {
            return current;
        }
        if (bracketIndex < 0) {
            if (last) {
                current.put(key, value);
                return current;
            }
            JSONObject next = current.getJSONObject(key);
            if (next == null) {
                next = new JSONObject();
                current.put(key, next);
            }
            return next;
        }
        JSONArray array = current.getJSONArray(key);
        if (array == null) {
            array = new JSONArray();
            current.put(key, array);
        }
        int arrayIndex = parseMainSampleArrayIndex(segment, bracketIndex);
        while (array.size() <= arrayIndex) {
            array.add(new JSONObject());
        }
        if (last) {
            array.set(arrayIndex, value);
            return current;
        }
        Object nextValue = array.get(arrayIndex);
        if (!(nextValue instanceof JSONObject)) {
            nextValue = new JSONObject();
            array.set(arrayIndex, nextValue);
        }
        return (JSONObject) nextValue;
    }

    private int parseMainSampleArrayIndex(String segment, int bracketIndex) {
        int end = segment.indexOf(']', bracketIndex);
        if (end <= bracketIndex + 1) {
            return 0;
        }
        try {
            return Math.max(Integer.parseInt(segment.substring(bracketIndex + 1, end)), 0);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String normalizeMainSampleJsonPath(String path) {
        String actual = trimToEmpty(path);
        if (actual.startsWith("$.")) {
            return actual.substring(2);
        }
        if (actual.startsWith("$")) {
            return actual.substring(1);
        }
        return actual;
    }

    private String buildCodePackageReadme(AiProtocolAdaptationTask task, GeneratedCodeMeta meta, JSONObject dslSnapshot) {
        JSONObject protocol = dslSnapshot == null ? new JSONObject() : dslSnapshot.getJSONObject("protocol");
        StringBuilder builder = new StringBuilder();
        builder.append("# ").append(meta.protocolName).append(" FastBee 协议代码包\n\n");
        builder.append("## 生成说明\n\n");
        builder.append("- 任务编号：").append(task.getTaskId()).append("\n");
        builder.append("- 协议编码：").append(meta.protocolCode).append("\n");
        builder.append("- 协议类：").append(meta.packageName).append(".").append(meta.className).append("\n");
        builder.append("- 生成时间：").append(formatNow()).append("\n");
        builder.append("- 协议族：").append(protocol == null ? "" : trimToEmpty(protocol.getString("protocolFamily"))).append("\n");
        builder.append("- 报文格式：").append(protocol == null ? "" : trimToEmpty(protocol.getString("messageFormat"))).append("\n\n");
        builder.append("## 文件清单\n\n");
        builder.append("- `src/main/java/.../").append(meta.className).append(".java`：FastBee `IProtocol` 协议服务类。\n");
        builder.append("- `src/main/resources/protocol-dsl.json`：本次生成使用的 DSL 快照。\n");
        builder.append("- `generation-manifest.json`：生成文件清单和来源信息。\n");
        builder.append("- `test-report.json`：首轮生成验证说明。\n\n");
        builder.append("## 接入边界\n\n");
        builder.append("当前代码包只生成可审查、可保存、可编译验证的协议骨架，不自动热部署、不自动绑定产品启用。");
        builder.append("上线前需要在目标工程执行 Maven 编译、样例报文回放和人工确认。\n\n");
        builder.append("## 本地样例\n\n");
        builder.append("生成的协议服务类内置 `main` 方法，会使用 DSL 样例报文或自动构造的默认 JSON 执行一次 `decode` 和 `encode`，");
        builder.append("可在导入目标工程后直接运行该类，快速检查编解码输入输出是否符合预期。\n");
        return builder.toString();
    }

    private JSONObject buildFileManifest(AiProtocolAdaptationTask task, GeneratedCodeMeta meta, Path dslSnapshotPath,
                                         Map<String, byte[]> files, ValidationReportSummary validationSummary) {
        JSONObject manifest = new JSONObject();
        manifest.put("generatedAt", formatNow());
        manifest.put("taskId", task.getTaskId());
        manifest.put("protocolCode", meta.protocolCode);
        manifest.put("protocolName", meta.protocolName);
        manifest.put("className", meta.packageName + "." + meta.className);
        manifest.put("dslSnapshotPath", dslSnapshotPath.toString());
        manifest.put("validationStatus", validationSummary.validationStatus);
        manifest.put("blockingIssueCount", validationSummary.blockingIssueCount);
        manifest.put("warningIssueCount", validationSummary.warningIssueCount);
        JSONArray fileArray = new JSONArray();
        for (Map.Entry<String, byte[]> entry : files.entrySet()) {
            JSONObject file = new JSONObject();
            file.put("path", entry.getKey());
            file.put("size", entry.getValue() == null ? 0 : entry.getValue().length);
            fileArray.add(file);
        }
        manifest.put("files", fileArray);
        return manifest;
    }

    private JSONObject buildInitialTestReport(AiProtocolAdaptationTask task, GeneratedCodeMeta meta,
                                              ValidationReportSummary validationSummary) {
        JSONObject report = new JSONObject();
        report.put("generatedAt", formatNow());
        report.put("taskId", task.getTaskId());
        report.put("protocolCode", meta.protocolCode);
        report.put("compileStatus", COMPILE_STATUS_PENDING);
        report.put("sampleReplayStatus", "PENDING");
        report.put("validationStatus", validationSummary.validationStatus);
        report.put("blockingIssueCount", validationSummary.blockingIssueCount);
        report.put("warningIssueCount", validationSummary.warningIssueCount);
        report.put("summary", "代码包已生成，当前环境未执行 Maven 编译与样例报文回放");
        return report;
    }

    private CodePackageVerificationResult buildCodePackageVerificationReport(AiProtocolGenerationRecord record,
                                                                             Path packagePath) throws IOException {
        Map<String, byte[]> entries = readZipEntries(packagePath);
        JSONArray checks = new JSONArray();
        JSONArray issues = new JSONArray();
        JSONArray sampleResults = new JSONArray();

        validateRequiredZipEntry(checks, issues, entries, "README.md", "README 文件");
        validateRequiredZipEntry(checks, issues, entries, "src/main/resources/protocol-dsl.json", "协议 DSL 快照");
        validateRequiredZipEntry(checks, issues, entries, "generation-manifest.json", "生成文件清单");
        validateRequiredZipEntry(checks, issues, entries, "test-report.json", "测试报告");

        String javaEntry = findGeneratedJavaEntry(entries);
        if (StringUtils.isBlank(javaEntry)) {
            addIssue(issues, "BLOCKER", "GENERATED_JAVA_REQUIRED",
                    "代码包缺少 FastBee 协议 Java 实现类", "zip.src.main.java");
            addCheck(checks, "GENERATED_JAVA", "协议 Java 实现类", "BLOCKED",
                    "代码包缺少协议 Java 实现类");
        } else {
            validateGeneratedJavaSource(checks, issues, javaEntry, getEntryText(entries, javaEntry));
        }

        JSONObject dslSnapshot = readDslSnapshotFromPackage(checks, issues, entries);
        if (!dslSnapshot.isEmpty()) {
            validateSampleFrames(checks, issues, sampleResults, dslSnapshot);
        }

        int blockingIssueCount = countIssues(issues, "BLOCKER");
        int warningIssueCount = countIssues(issues, "WARNING");
        String compileStatus = blockingIssueCount > 0
                ? COMPILE_STATUS_STATIC_FAILED
                : warningIssueCount > 0 ? COMPILE_STATUS_STATIC_WARNING : COMPILE_STATUS_STATIC_PASSED;
        String sampleReplayStatus = resolveSampleReplayStatus(blockingIssueCount, warningIssueCount, sampleResults);
        String summary = buildCodePackageVerificationSummary(compileStatus, sampleReplayStatus,
                blockingIssueCount, warningIssueCount, issues);

        JSONObject report = new JSONObject();
        report.put("schemaVersion", "protocol-generation-test-report-0.3");
        report.put("generatedAt", formatNow());
        report.put("verifiedAt", formatNow());
        report.put("recordId", record.getRecordId());
        report.put("taskId", record.getTaskId());
        report.put("protocolCode", readProtocolCode(dslSnapshot));
        report.put("compileStatus", compileStatus);
        report.put("sampleReplayStatus", sampleReplayStatus);
        report.put("blockingIssueCount", blockingIssueCount);
        report.put("warningIssueCount", warningIssueCount);
        report.put("summary", summary);
        report.put("checks", checks);
        report.put("issues", issues);
        report.put("sampleResults", sampleResults);
        report.put("note", "当前为首轮静态验证与 JSON/HEX 样例回归，不等同于目标工程 Maven 编译结果");

        CodePackageVerificationResult result = new CodePackageVerificationResult();
        result.report = report;
        result.compileStatus = compileStatus;
        result.sampleReplayStatus = sampleReplayStatus;
        result.summary = summary;
        return result;
    }

    private void validateRequiredZipEntry(JSONArray checks, JSONArray issues, Map<String, byte[]> entries,
                                          String entryName, String entryLabel) {
        int beginIndex = issues.size();
        byte[] content = entries.get(entryName);
        if (content == null || content.length == 0) {
            addIssue(issues, "BLOCKER", "ZIP_ENTRY_REQUIRED",
                    "代码包缺少" + entryLabel + "：" + entryName, "zip." + entryName);
        }
        addCheck(checks, "ZIP_ENTRY_" + safeCheckCode(entryName), entryLabel,
                resolveCheckStatus(issues, beginIndex),
                content == null || content.length == 0 ? "缺少必要文件" : "必要文件已存在");
    }

    private void validateGeneratedJavaSource(JSONArray checks, JSONArray issues, String javaEntry, String source) {
        int beginIndex = issues.size();
        if (StringUtils.isBlank(source)) {
            addIssue(issues, "BLOCKER", "GENERATED_JAVA_EMPTY", "协议 Java 实现类为空", "zip." + javaEntry);
        } else {
            if (!source.contains("implements IProtocol")) {
                addIssue(issues, "BLOCKER", "IPROTOCOL_IMPLEMENTATION_REQUIRED",
                        "协议 Java 实现类未实现 IProtocol", "zip." + javaEntry);
            }
            if (!source.contains("@SysProtocol")) {
                addIssue(issues, "BLOCKER", "SYSPROTOCOL_ANNOTATION_REQUIRED",
                        "协议 Java 实现类缺少 @SysProtocol 注册信息", "zip." + javaEntry);
            }
            if (!source.contains("DeviceReport decode(")) {
                addIssue(issues, "BLOCKER", "DECODE_METHOD_REQUIRED",
                        "协议 Java 实现类缺少 decode 方法", "zip." + javaEntry);
            }
            if (!source.contains("FunctionCallBackBo encode(")) {
                addIssue(issues, "BLOCKER", "ENCODE_METHOD_REQUIRED",
                        "协议 Java 实现类缺少 encode 方法", "zip." + javaEntry);
            }
        }
        addCheck(checks, "GENERATED_JAVA_STRUCTURE", "协议 Java 实现类结构",
                resolveCheckStatus(issues, beginIndex),
                "已完成 IProtocol、@SysProtocol、encode/decode 静态检查");
    }

    private JSONObject readDslSnapshotFromPackage(JSONArray checks, JSONArray issues, Map<String, byte[]> entries) {
        int beginIndex = issues.size();
        String dslText = getEntryText(entries, "src/main/resources/protocol-dsl.json");
        if (StringUtils.isBlank(dslText)) {
            addIssue(issues, "BLOCKER", "PACKAGE_DSL_EMPTY",
                    "代码包内协议 DSL 快照为空", "zip.src/main/resources/protocol-dsl.json");
            addCheck(checks, "PACKAGE_DSL_PARSE", "协议 DSL 快照解析",
                    resolveCheckStatus(issues, beginIndex), "协议 DSL 快照为空");
            return new JSONObject();
        }
        try {
            JSONObject dslSnapshot = JSON.parseObject(dslText);
            addCheck(checks, "PACKAGE_DSL_PARSE", "协议 DSL 快照解析",
                    resolveCheckStatus(issues, beginIndex), "协议 DSL 快照可解析");
            return dslSnapshot == null ? new JSONObject() : dslSnapshot;
        } catch (Exception ex) {
            addIssue(issues, "BLOCKER", "PACKAGE_DSL_PARSE_FAILED",
                    "代码包内协议 DSL 快照不是合法 JSON：" + ex.getMessage(),
                    "zip.src/main/resources/protocol-dsl.json");
            addCheck(checks, "PACKAGE_DSL_PARSE", "协议 DSL 快照解析",
                    resolveCheckStatus(issues, beginIndex), "协议 DSL 快照解析失败");
            return new JSONObject();
        }
    }

    private void validateSampleFrames(JSONArray checks, JSONArray issues, JSONArray sampleResults,
                                      JSONObject dslSnapshot) {
        int beginIndex = issues.size();
        JSONArray sampleFrames = readJsonArray(dslSnapshot, "sampleFrames");
        JSONArray fields = readJsonArray(dslSnapshot, "fields");
        JSONArray codecRules = readJsonArray(dslSnapshot, "codecRules");
        JSONArray mappings = readJsonArray(dslSnapshot, "thingModelMappings");
        Set<String> messageTypeCodes = collectMessageTypeCodes(readJsonArray(dslSnapshot, "messageTypes"));
        if (sampleFrames.isEmpty()) {
            addIssue(issues, "WARNING", "SAMPLE_FRAMES_RECOMMENDED",
                    "代码包验证缺少样例报文，已跳过 Decoder 静态回归；通用编解码代码包仍可生成",
                    "dsl.sampleFrames");
        }
        for (int index = 0; index < sampleFrames.size(); index++) {
            validateSampleFrame(issues, sampleResults, sampleFrames, fields, codecRules,
                    mappings, messageTypeCodes, index);
        }
        addCheck(checks, "SAMPLE_FRAME_REPLAY", "样例报文静态回归",
                resolveCheckStatus(issues, beginIndex),
                sampleFrames.isEmpty()
                        ? "缺少样例报文"
                        : "已完成样例报文 JSON/HEX 解析前静态检查与物模型映射检查");
    }

    private void validateSampleFrame(JSONArray issues, JSONArray sampleResults, JSONArray sampleFrames,
                                     JSONArray fields, JSONArray codecRules, JSONArray mappings,
                                     Set<String> messageTypeCodes, int index) {
        int beginIndex = issues.size();
        JSONObject sampleResult = new JSONObject();
        sampleResult.put("index", index + 1);
        Object row = sampleFrames.get(index);
        if (!(row instanceof JSONObject)) {
            addIssue(issues, "WARNING", "SAMPLE_FRAME_OBJECT_REQUIRED",
                    "样例报文第 " + (index + 1) + " 行不是结构化对象", "dsl.sampleFrames[" + index + "]");
            sampleResult.put("status", SAMPLE_REPLAY_STATUS_WARNING);
            sampleResult.put("message", "样例报文不是结构化对象");
            sampleResults.add(sampleResult);
            return;
        }
        JSONObject sampleFrame = (JSONObject) row;
        String messageType = trimToEmpty(sampleFrame.getString("messageType"));
        String rawFrame = trimToEmpty(sampleFrame.getString("rawFrame"));
        if (StringUtils.isBlank(rawFrame) && hasJsonValue(sampleFrame.get("expectedJson"))) {
            rawFrame = JSON.toJSONString(sampleFrame.get("expectedJson"));
        }
        sampleResult.put("messageType", messageType);
        sampleResult.put("rawFramePreview", abbreviate(rawFrame.replaceAll("\\R", " "), 160));
        if (StringUtils.isBlank(messageType)) {
            addIssue(issues, "WARNING", "SAMPLE_FRAME_MESSAGE_TYPE_REQUIRED",
                    "样例报文第 " + (index + 1) + " 行缺少 messageType", "dsl.sampleFrames[" + index + "].messageType");
        } else if (!messageTypeCodes.isEmpty() && !messageTypeCodes.contains(messageType)) {
            addIssue(issues, "WARNING", "SAMPLE_FRAME_MESSAGE_TYPE_UNKNOWN",
                    "样例报文第 " + (index + 1) + " 行的 messageType 未在报文类型中定义：" + messageType,
                    "dsl.sampleFrames[" + index + "].messageType");
        }
        if (StringUtils.isBlank(rawFrame)) {
            addIssue(issues, "WARNING", "SAMPLE_FRAME_RAW_REQUIRED",
                    "样例报文第 " + (index + 1) + " 行缺少 rawFrame，已跳过该样例回放，不阻断通用编解码代码生成",
                    "dsl.sampleFrames[" + index + "].rawFrame");
            sampleResult.put("status", resolveSampleStatus(issues, beginIndex));
            sampleResult.put("message", "样例报文为空");
            sampleResults.add(sampleResult);
            return;
        }
        if (looksJsonObject(rawFrame)) {
            validateJsonSampleFrame(issues, sampleResults, sampleResult, mappings,
                    beginIndex, index, messageType, rawFrame);
            return;
        }

        if (looksHexFrame(rawFrame)) {
            validateHexSampleFrame(issues, sampleResults, sampleResult, fields, codecRules,
                    mappings, beginIndex, index, messageType, rawFrame);
            return;
        }

        addIssue(issues, "WARNING", "SAMPLE_FRAME_FORMAT_UNSUPPORTED",
                "样例报文第 " + (index + 1) + " 行既不是 JSON 对象，也不是合法 HEX 字符串，已跳过该样例回放",
                "dsl.sampleFrames[" + index + "].rawFrame");
        sampleResult.put("status", resolveSampleStatus(issues, beginIndex));
        sampleResult.put("message", "样例报文格式无法执行首轮静态回归");
        sampleResults.add(sampleResult);
    }

    private void validateJsonSampleFrame(JSONArray issues, JSONArray sampleResults, JSONObject sampleResult,
                                         JSONArray mappings, int beginIndex, int index,
                                         String messageType, String rawFrame) {
        sampleResult.put("frameFormat", "JSON");
        JSONObject payload;
        try {
            payload = JSON.parseObject(rawFrame);
        } catch (Exception ex) {
            addIssue(issues, "WARNING", "SAMPLE_FRAME_JSON_PARSE_FAILED",
                    "样例报文第 " + (index + 1) + " 行 JSON 解析失败，已跳过该样例回放：" + ex.getMessage(),
                    "dsl.sampleFrames[" + index + "].rawFrame");
            sampleResult.put("status", resolveSampleStatus(issues, beginIndex));
            sampleResult.put("message", "JSON 解析失败");
            sampleResults.add(sampleResult);
            return;
        }
        JSONArray missingMappings = new JSONArray();
        int applicableMappingCount = countApplicableMappings(mappings, messageType);
        int hitCount = countMappingHits(payload, mappings, messageType, missingMappings);
        sampleResult.put("applicableMappingCount", applicableMappingCount);
        sampleResult.put("mappedItemCount", hitCount);
        sampleResult.put("missingMappings", missingMappings);
        appendSampleMappingIssues(issues, mappings, missingMappings, index,
                applicableMappingCount, hitCount);
        sampleResult.put("status", resolveSampleStatus(issues, beginIndex));
        sampleResult.put("message", "已完成 JSON 解析与物模型映射检查");
        sampleResults.add(sampleResult);
    }

    private void validateHexSampleFrame(JSONArray issues, JSONArray sampleResults, JSONObject sampleResult,
                                        JSONArray fields, JSONArray codecRules, JSONArray mappings,
                                        int beginIndex, int index, String messageType, String rawFrame) {
        sampleResult.put("frameFormat", "HEX");
        String normalizedFrame = normalizeHexFrame(rawFrame);
        int byteLength = normalizedFrame.length() / 2;
        sampleResult.put("normalizedFramePreview", abbreviate(normalizedFrame, 160));
        sampleResult.put("byteLength", byteLength);

        JSONObject codecRule = resolveCodecRuleForMessage(codecRules, messageType);
        if (codecRule == null) {
            addIssue(issues, "WARNING", "SAMPLE_FRAME_CODEC_RULE_REQUIRED",
                    "样例报文第 " + (index + 1) + " 行为 HEX 帧，但缺少可用于校验帧边界的编解码规则，已跳过该样例边界回归",
                    "dsl.codecRules");
        } else {
            sampleResult.put("codecRuleTarget", trimToEmpty(codecRule.getString("target")));
            validateHexFrameBoundary(issues, sampleResult, codecRule, normalizedFrame, index);
            String checksum = trimToEmpty(codecRule.getString("checksum"));
            if (hasEffectiveChecksum(checksum)) {
                addIssue(issues, "WARNING", "SAMPLE_FRAME_CHECKSUM_NOT_EXECUTED",
                        "样例报文第 " + (index + 1) + " 行声明校验算法 " + checksum
                                + "，当前首轮仅登记校验约束，尚未执行真实 checksum 计算",
                        "dsl.sampleFrames[" + index + "]");
                sampleResult.put("checksum", checksum);
                sampleResult.put("checksumVerified", false);
            }
        }

        List<JSONObject> applicableFields = collectApplicableFields(fields, messageType);
        sampleResult.put("fieldRuleCount", applicableFields.size());
        if (applicableFields.isEmpty()) {
            addIssue(issues, "WARNING", "SAMPLE_FRAME_FIELD_RULE_MISSING",
                    "样例报文第 " + (index + 1) + " 行未找到匹配 messageType 的字段定义，无法验证最小帧长度",
                    "dsl.fields");
        } else {
            int expectedMinByteLength = resolveExpectedMinByteLength(applicableFields);
            sampleResult.put("expectedMinByteLength", expectedMinByteLength);
            if (expectedMinByteLength <= 0) {
                addIssue(issues, "WARNING", "SAMPLE_FRAME_FIELD_LENGTH_UNKNOWN",
                        "样例报文第 " + (index + 1) + " 行字段定义缺少可计算的 offset/length，无法验证最小帧长度",
                        "dsl.fields");
            } else if (byteLength < expectedMinByteLength) {
                addIssue(issues, "WARNING", "SAMPLE_FRAME_LENGTH_TOO_SHORT",
                        "样例报文第 " + (index + 1) + " 行 HEX 长度为 " + byteLength
                                + " 字节，小于字段定义要求的最小 " + expectedMinByteLength + " 字节，建议复核样例或字段长度",
                        "dsl.sampleFrames[" + index + "].rawFrame");
            }
        }

        JSONArray missingMappings = new JSONArray();
        int applicableMappingCount = countApplicableMappings(mappings, messageType);
        int hitCount = countBinaryMappingHits(applicableFields, mappings, messageType, missingMappings);
        sampleResult.put("applicableMappingCount", applicableMappingCount);
        sampleResult.put("mappedItemCount", hitCount);
        sampleResult.put("missingMappings", missingMappings);
        appendSampleMappingIssues(issues, mappings, missingMappings, index,
                applicableMappingCount, hitCount);

        sampleResult.put("status", resolveSampleStatus(issues, beginIndex));
        sampleResult.put("message", "已完成 HEX 帧格式、边界、长度与物模型映射静态检查");
        sampleResults.add(sampleResult);
    }

    private void appendSampleMappingIssues(JSONArray issues, JSONArray mappings, JSONArray missingMappings,
                                           int index, int applicableMappingCount, int hitCount) {
        if (mappings.isEmpty()) {
            return;
        }
        if (applicableMappingCount == 0) {
            addIssue(issues, "WARNING", "SAMPLE_FRAME_MAPPING_NOT_APPLICABLE",
                    "样例报文第 " + (index + 1) + " 行没有匹配 messageType 的物模型映射，建议补充对应映射或样例",
                    "dsl.sampleFrames[" + index + "]");
        } else if (hitCount == 0) {
            addIssue(issues, "WARNING", "SAMPLE_FRAME_MAPPING_MISSED",
                    "样例报文第 " + (index + 1) + " 行未命中任何物模型映射；物模型映射仅作为平台绑定辅助，不阻断通用编解码代码生成",
                    "dsl.sampleFrames[" + index + "]");
        } else if (!missingMappings.isEmpty()) {
            addIssue(issues, "WARNING", "SAMPLE_FRAME_MAPPING_PARTIAL",
                    "样例报文第 " + (index + 1) + " 行存在未命中的物模型映射，建议补齐样例或调整 sourceField",
                    "dsl.sampleFrames[" + index + "]");
        }
    }

    private int countMappingHits(JSONObject payload, JSONArray mappings, String messageType,
                                 JSONArray missingMappings) {
        int hitCount = 0;
        for (int mappingIndex = 0; mappingIndex < mappings.size(); mappingIndex++) {
            Object row = mappings.get(mappingIndex);
            if (!(row instanceof JSONObject)) {
                continue;
            }
            JSONObject mapping = (JSONObject) row;
            String identifier = trimToEmpty(mapping.getString("identifier"));
            String sourceField = trimToEmpty(mapping.getString("sourceField"));
            if (StringUtils.isBlank(identifier)
                    || StringUtils.isBlank(sourceField)
                    || !matchesMessageType(mapping, messageType)) {
                continue;
            }
            Object value = readJsonPathValue(payload, sourceField);
            if (value == null) {
                JSONObject missing = new JSONObject();
                missing.put("identifier", identifier);
                missing.put("sourceField", sourceField);
                missingMappings.add(missing);
            } else {
                hitCount++;
            }
        }
        return hitCount;
    }

    private int countBinaryMappingHits(List<JSONObject> applicableFields, JSONArray mappings,
                                       String messageType, JSONArray missingMappings) {
        Set<String> fieldNames = collectFieldNameSet(applicableFields);
        int hitCount = 0;
        for (int mappingIndex = 0; mappingIndex < mappings.size(); mappingIndex++) {
            Object row = mappings.get(mappingIndex);
            if (!(row instanceof JSONObject)) {
                continue;
            }
            JSONObject mapping = (JSONObject) row;
            String identifier = trimToEmpty(mapping.getString("identifier"));
            String sourceField = trimToEmpty(mapping.getString("sourceField"));
            if (StringUtils.isBlank(identifier)
                    || StringUtils.isBlank(sourceField)
                    || !matchesMessageType(mapping, messageType)) {
                continue;
            }
            if (fieldNames.contains(normalizeFieldReference(sourceField))) {
                hitCount++;
            } else {
                JSONObject missing = new JSONObject();
                missing.put("identifier", identifier);
                missing.put("sourceField", sourceField);
                missingMappings.add(missing);
            }
        }
        return hitCount;
    }

    private int countApplicableMappings(JSONArray mappings, String messageType) {
        int count = 0;
        for (int mappingIndex = 0; mappingIndex < mappings.size(); mappingIndex++) {
            Object row = mappings.get(mappingIndex);
            if (!(row instanceof JSONObject)) {
                continue;
            }
            JSONObject mapping = (JSONObject) row;
            if (StringUtils.isNotBlank(mapping.getString("identifier"))
                    && StringUtils.isNotBlank(mapping.getString("sourceField"))
                    && matchesMessageType(mapping, messageType)) {
                count++;
            }
        }
        return count;
    }

    private JSONObject resolveCodecRuleForMessage(JSONArray codecRules, String messageType) {
        JSONObject globalRule = null;
        JSONObject fallbackRule = null;
        for (int index = 0; index < codecRules.size(); index++) {
            Object row = codecRules.get(index);
            if (!(row instanceof JSONObject)) {
                continue;
            }
            JSONObject codecRule = (JSONObject) row;
            String target = trimToEmpty(codecRule.getString("target"));
            if (StringUtils.isNotBlank(messageType)
                    && (target.equals(messageType) || target.equalsIgnoreCase(messageType))) {
                return codecRule;
            }
            if (globalRule == null && ("*".equals(target) || "ALL".equalsIgnoreCase(target))) {
                globalRule = codecRule;
            }
            if (fallbackRule == null && hasCodecRuleConstraint(codecRule)) {
                fallbackRule = codecRule;
            }
        }
        return globalRule != null ? globalRule : fallbackRule;
    }

    private boolean hasCodecRuleConstraint(JSONObject codecRule) {
        String ruleType = codecRule == null ? "" : trimToEmpty(codecRule.getString("ruleType")).toUpperCase(Locale.ROOT);
        String target = codecRule == null ? "" : trimToEmpty(codecRule.getString("target")).toUpperCase(Locale.ROOT);
        return codecRule != null
                && (StringUtils.isNotBlank(codecRule.getString("frameStart"))
                || StringUtils.isNotBlank(codecRule.getString("frameEnd"))
                || StringUtils.isNotBlank(codecRule.getString("checksum"))
                || StringUtils.isNotBlank(codecRule.getString("endian"))
                || StringUtils.isNotBlank(codecRule.getString("rootPath"))
                || StringUtils.isNotBlank(codecRule.getString("deviceIdPath"))
                || StringUtils.isNotBlank(codecRule.getString("timestampPath"))
                || ruleType.contains("JSON")
                || target.contains("JSON"));
    }

    private void validateHexFrameBoundary(JSONArray issues, JSONObject sampleResult, JSONObject codecRule,
                                          String normalizedFrame, int index) {
        String frameStart = trimToEmpty(codecRule.getString("frameStart"));
        if (StringUtils.isNotBlank(frameStart)) {
            String normalizedStart = normalizeHexFrame(frameStart);
            sampleResult.put("expectedFrameStart", normalizedStart);
            if (!isNormalizedHexBytes(normalizedStart)) {
                addIssue(issues, "BLOCKER", "CODEC_RULE_FRAME_START_INVALID",
                        "编解码规则 frameStart 不是合法 HEX 字节：" + frameStart, "dsl.codecRules.frameStart");
            } else if (!normalizedFrame.startsWith(normalizedStart)) {
                addIssue(issues, "WARNING", "SAMPLE_FRAME_START_MISMATCH",
                        "样例报文第 " + (index + 1) + " 行 HEX 帧头与编解码规则不一致，期望 "
                                + normalizedStart + "，建议复核样例或帧头规则",
                        "dsl.sampleFrames[" + index + "].rawFrame");
            }
        }

        String frameEnd = trimToEmpty(codecRule.getString("frameEnd"));
        if (StringUtils.isNotBlank(frameEnd)) {
            String normalizedEnd = normalizeHexFrame(frameEnd);
            sampleResult.put("expectedFrameEnd", normalizedEnd);
            if (!isNormalizedHexBytes(normalizedEnd)) {
                addIssue(issues, "BLOCKER", "CODEC_RULE_FRAME_END_INVALID",
                        "编解码规则 frameEnd 不是合法 HEX 字节：" + frameEnd, "dsl.codecRules.frameEnd");
            } else if (!normalizedFrame.endsWith(normalizedEnd)) {
                addIssue(issues, "WARNING", "SAMPLE_FRAME_END_MISMATCH",
                        "样例报文第 " + (index + 1) + " 行 HEX 帧尾与编解码规则不一致，期望 "
                                + normalizedEnd + "，建议复核样例或帧尾规则",
                        "dsl.sampleFrames[" + index + "].rawFrame");
            }
        }
    }

    private List<JSONObject> collectApplicableFields(JSONArray fields, String messageType) {
        List<JSONObject> applicableFields = new ArrayList<>();
        for (int index = 0; index < fields.size(); index++) {
            Object row = fields.get(index);
            if (row instanceof JSONObject && matchesMessageType((JSONObject) row, messageType)) {
                applicableFields.add((JSONObject) row);
            }
        }
        return applicableFields;
    }

    private Set<String> collectFieldNameSet(List<JSONObject> fields) {
        Set<String> fieldNames = new java.util.HashSet<>();
        for (JSONObject field : fields) {
            if (field == null) {
                continue;
            }
            String fieldName = normalizeFieldReference(field.getString("fieldName"));
            if (StringUtils.isNotBlank(fieldName)) {
                fieldNames.add(fieldName);
            }
        }
        return fieldNames;
    }

    private int resolveExpectedMinByteLength(List<JSONObject> fields) {
        int maxOffsetLength = 0;
        int sequentialLength = 0;
        for (JSONObject field : fields) {
            if (field == null) {
                continue;
            }
            Integer length = parseNonNegativeInteger(field.get("length"));
            if (length == null || length <= 0) {
                continue;
            }
            Integer offset = parseNonNegativeInteger(field.get("offset"));
            if (offset == null) {
                sequentialLength += length;
            } else {
                maxOffsetLength = Math.max(maxOffsetLength, offset + length);
            }
        }
        return maxOffsetLength > 0 ? maxOffsetLength : sequentialLength;
    }

    private Integer parseNonNegativeInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            int parsed = ((Number) value).intValue();
            return parsed < 0 ? null : parsed;
        }
        String actual = trimToEmpty(String.valueOf(value));
        if (StringUtils.isBlank(actual)) {
            return null;
        }
        try {
            int parsed = Integer.parseInt(actual);
            return parsed < 0 ? null : parsed;
        } catch (NumberFormatException ignored) {
            try {
                double parsedDouble = Double.parseDouble(actual);
                int parsed = (int) Math.round(parsedDouble);
                if (Math.abs(parsedDouble - parsed) > 0.000001D || parsed < 0) {
                    return null;
                }
                return parsed;
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }

    private boolean matchesMessageType(JSONObject row, String messageType) {
        String rowMessageType = trimToEmpty(row.getString("messageType"));
        return StringUtils.isBlank(rowMessageType)
                || StringUtils.isBlank(messageType)
                || rowMessageType.equals(messageType)
                || rowMessageType.equalsIgnoreCase(messageType);
    }

    private String normalizeFieldReference(String value) {
        String actual = trimToEmpty(value);
        int dotIndex = actual.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < actual.length() - 1) {
            actual = actual.substring(dotIndex + 1);
        }
        return actual.toLowerCase(Locale.ROOT);
    }

    private boolean looksHexFrame(String rawFrame) {
        return isNormalizedHexBytes(normalizeHexFrame(rawFrame));
    }

    private String normalizeHexFrame(String value) {
        String actual = trimToEmpty(value);
        if (StringUtils.isBlank(actual)) {
            return "";
        }
        actual = actual.replaceFirst("(?i)^hex\\s*[:=]\\s*", "");
        actual = actual.replaceAll("(?i)0x", "");
        return actual.replaceAll("[\\s,;:_-]", "").toUpperCase(Locale.ROOT);
    }

    private boolean isNormalizedHexBytes(String value) {
        return StringUtils.isNotBlank(value)
                && value.length() % 2 == 0
                && value.matches("[0-9A-F]+");
    }

    private boolean hasEffectiveChecksum(String checksum) {
        String actual = trimToEmpty(checksum);
        if (StringUtils.isBlank(actual)) {
            return false;
        }
        String normalized = actual.toUpperCase(Locale.ROOT);
        return !"-".equals(normalized)
                && !"NONE".equals(normalized)
                && !"NO".equals(normalized)
                && !"N/A".equals(normalized)
                && !"NA".equals(normalized)
                && !"NULL".equals(normalized)
                && !"无".equals(actual)
                && !"不校验".equals(actual);
    }

    private Object readJsonPathValue(JSONObject root, String path) {
        String normalizedPath = normalizeJsonPath(path);
        if (StringUtils.isBlank(normalizedPath)) {
            return root;
        }
        Object current = root;
        for (String part : normalizedPath.split("\\.")) {
            if (StringUtils.isBlank(part)) {
                continue;
            }
            current = readJsonPathSegment(current, part);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    private Object readJsonPathSegment(Object current, String segment) {
        String actual = trimToEmpty(segment);
        if (StringUtils.isBlank(actual)) {
            return current;
        }
        int bracketIndex = actual.indexOf('[');
        String key = bracketIndex >= 0 ? actual.substring(0, bracketIndex) : actual;
        Object value = current;
        if (StringUtils.isNotBlank(key)) {
            if (!(value instanceof JSONObject)) {
                return null;
            }
            value = ((JSONObject) value).get(key);
        }
        int cursor = bracketIndex;
        while (cursor >= 0 && cursor < actual.length()) {
            int end = actual.indexOf(']', cursor);
            if (end <= cursor + 1) {
                return null;
            }
            Integer arrayIndex = parseNonNegativeInteger(actual.substring(cursor + 1, end));
            if (arrayIndex == null || !(value instanceof JSONArray) || arrayIndex >= ((JSONArray) value).size()) {
                return null;
            }
            value = ((JSONArray) value).get(arrayIndex);
            cursor = actual.indexOf('[', end + 1);
        }
        return value;
    }

    private String normalizeJsonPath(String path) {
        String actual = trimToEmpty(path);
        if (actual.startsWith("$.")) {
            return actual.substring(2);
        }
        if (actual.startsWith("$")) {
            return actual.substring(1);
        }
        return actual;
    }

    private Set<String> collectMessageTypeCodes(JSONArray messageTypes) {
        Set<String> codes = new java.util.HashSet<>();
        for (int index = 0; index < messageTypes.size(); index++) {
            JSONObject messageType = messageTypes.getJSONObject(index);
            if (messageType != null && StringUtils.isNotBlank(messageType.getString("code"))) {
                codes.add(messageType.getString("code").trim());
            }
        }
        return codes;
    }

    private String resolveSampleReplayStatus(int blockingIssueCount, int warningIssueCount, JSONArray sampleResults) {
        if (blockingIssueCount > 0) {
            return SAMPLE_REPLAY_STATUS_FAILED;
        }
        if (warningIssueCount > 0 || sampleResults.isEmpty()) {
            return SAMPLE_REPLAY_STATUS_WARNING;
        }
        return SAMPLE_REPLAY_STATUS_PASSED;
    }

    private String resolveSampleStatus(JSONArray issues, int beginIndex) {
        String checkStatus = resolveCheckStatus(issues, beginIndex);
        if ("BLOCKED".equals(checkStatus)) {
            return SAMPLE_REPLAY_STATUS_FAILED;
        }
        if ("WARNING".equals(checkStatus)) {
            return SAMPLE_REPLAY_STATUS_WARNING;
        }
        return SAMPLE_REPLAY_STATUS_PASSED;
    }

    private String buildCodePackageVerificationSummary(String compileStatus, String sampleReplayStatus,
                                                       int blockingIssueCount, int warningIssueCount,
                                                       JSONArray issues) {
        if (COMPILE_STATUS_STATIC_FAILED.equals(compileStatus)) {
            return "代码包静态验证未通过：" + blockingIssueCount + " 个阻断项、"
                    + warningIssueCount + " 个告警项；" + joinFirstIssueMessages(issues, "BLOCKER");
        }
        if (COMPILE_STATUS_STATIC_WARNING.equals(compileStatus)) {
            return "代码包静态验证有告警：" + warningIssueCount + " 个告警项；样例回归状态="
                    + sampleReplayStatus + "；" + joinFirstIssueMessages(issues, "WARNING");
        }
        return "代码包静态验证通过，JSON/HEX 样例报文静态回归通过；仍需在目标工程执行 Maven 编译";
    }

    private String findGeneratedJavaEntry(Map<String, byte[]> entries) {
        for (String entryName : entries.keySet()) {
            if (entryName.startsWith("src/main/java/") && entryName.endsWith(".java")) {
                return entryName;
            }
        }
        return "";
    }

    private String getEntryText(Map<String, byte[]> entries, String entryName) {
        byte[] bytes = entries.get(entryName);
        return bytes == null ? "" : new String(bytes, StandardCharsets.UTF_8);
    }

    private boolean looksJsonObject(String rawFrame) {
        String actual = trimToEmpty(rawFrame);
        return actual.startsWith("{") && actual.endsWith("}");
    }

    private String safeCheckCode(String value) {
        return defaultIfBlank(value, "UNKNOWN").toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "_");
    }

    private String readProtocolCode(JSONObject dslSnapshot) {
        JSONObject protocol = dslSnapshot == null ? null : dslSnapshot.getJSONObject("protocol");
        return protocol == null ? "" : trimToEmpty(protocol.getString("protocolCode"));
    }

    private void replaceZipEntry(Path zipPath, String entryName, byte[] fileBytes) throws IOException {
        Map<String, byte[]> entries = readZipEntries(zipPath);
        entries.put(entryName, fileBytes == null ? new byte[0] : fileBytes);
        Files.write(zipPath, buildZipBytes(entries));
    }

    private Map<String, byte[]> readZipEntries(Path zipPath) throws IOException {
        Map<String, byte[]> entries = new LinkedHashMap<>();
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipPath))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    entries.put(zipEntry.getName(), readAllBytes(zipInputStream));
                }
                zipInputStream.closeEntry();
            }
        }
        return entries;
    }

    private byte[] buildZipBytes(Map<String, byte[]> files) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                ZipEntry zipEntry = new ZipEntry(entry.getKey());
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(entry.getValue() == null ? new byte[0] : entry.getValue());
                zipOutputStream.closeEntry();
            }
            zipOutputStream.finish();
            return outputStream.toByteArray();
        }
    }

    private ValidationReportSummary readLatestValidationReportSummary(Long taskId) {
        ValidationReportSummary summary = new ValidationReportSummary();
        summary.validationStatus = DEFAULT_VALIDATION_STATUS;
        AiProtocolAdaptationArtifact artifact = selectLatestArtifact(taskId, ARTIFACT_TYPE_VALIDATION_REPORT);
        if (artifact == null || StringUtils.isBlank(artifact.getFilePath())) {
            return summary;
        }
        try {
            JSONObject report = JSON.parseObject(Files.readString(Paths.get(artifact.getFilePath()), StandardCharsets.UTF_8));
            JSONObject result = report == null ? null : report.getJSONObject("result");
            if (result != null) {
                summary.validationStatus = defaultIfBlank(result.getString("validationStatus"), DEFAULT_VALIDATION_STATUS);
                summary.blockingIssueCount = result.getIntValue("blockingIssueCount");
                summary.warningIssueCount = result.getIntValue("warningIssueCount");
            }
        } catch (Exception ignored) {
            summary.validationStatus = DEFAULT_VALIDATION_STATUS;
        }
        return summary;
    }

    private Path saveGeneratedFile(AiProtocolAdaptationTask task, String folderName, String filename,
                                   byte[] fileBytes) throws IOException {
        Path targetDir = Paths.get(RuoYiConfig.getProfile(), "ai", "protocol", "adaptation",
                buildTaskDirectoryName(task), safePathSegment(folderName));
        Files.createDirectories(targetDir);
        Path targetFile = targetDir.resolve(safePathSegment(filename));
        Files.write(targetFile, fileBytes == null ? new byte[0] : fileBytes);
        return targetFile;
    }

    private String normalizeJavaPackage(String packageName) {
        String[] parts = defaultIfBlank(packageName, "com.fastbee.generated.protocol").split("\\.");
        List<String> normalizedParts = new ArrayList<>();
        for (String part : parts) {
            String normalized = toJavaPackageSegment(part);
            if (StringUtils.isNotBlank(normalized)) {
                normalizedParts.add(normalized);
            }
        }
        return normalizedParts.isEmpty() ? "com.fastbee.generated.protocol" : String.join(".", normalizedParts);
    }

    private String toJavaPackageSegment(String value) {
        String normalized = defaultIfBlank(value, "protocol").toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_]", "_")
                .replaceAll("_+", "_");
        if (normalized.matches("^[0-9].*")) {
            normalized = "p_" + normalized;
        }
        return StringUtils.isBlank(normalized) ? "protocol" : normalized;
    }

    private String toJavaClassName(String value) {
        String actualValue = defaultIfBlank(value, "GeneratedProtocolService");
        StringBuilder builder = new StringBuilder();
        boolean upperNext = true;
        for (int index = 0; index < actualValue.length(); index++) {
            char ch = actualValue.charAt(index);
            if (Character.isLetterOrDigit(ch)) {
                builder.append(upperNext ? Character.toUpperCase(ch) : ch);
                upperNext = false;
            } else {
                upperNext = true;
            }
        }
        String className = builder.length() == 0 ? "GeneratedProtocolService" : builder.toString();
        if (Character.isDigit(className.charAt(0))) {
            className = "P" + className;
        }
        return className;
    }

    private String escapeJava(String value) {
        return trimToEmpty(value)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    private byte[] buildEnterpriseWorkbookBytes(AiProtocolAdaptationTask task, Path dslSnapshotPath,
                                                JSONObject dslSnapshot) throws IOException {
        JSONObject actualSnapshot = dslSnapshot == null ? new JSONObject() : dslSnapshot;
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            appendWorkbookInstructionSheet(workbook, task, dslSnapshotPath);
            appendProtocolOverviewSheet(workbook, actualSnapshot.getJSONObject("protocol"));
            appendJsonArraySheet(workbook, SHEET_MESSAGE_TYPES,
                    List.of("code", "name", "direction", "trigger", "description", "confirmed", "remark"),
                    readJsonArray(actualSnapshot, "messageTypes"));
            appendJsonArraySheet(workbook, SHEET_FIELDS,
                    List.of("messageType", "fieldCode", "fieldName", "displayName", "jsonPath", "dataType", "length", "offset",
                            "byteOrder", "scale", "unit", "required", "description"),
                    readJsonArray(actualSnapshot, "fields"));
            appendJsonArraySheet(workbook, SHEET_CODEC_RULES,
                    List.of("ruleType", "target", "expression", "checksum", "endian", "frameStart",
                            "frameEnd", "description"),
                    readJsonArray(actualSnapshot, "codecRules"));
            aiThingModelWorkbookSupport.appendThingModelSheet(workbook, SHEET_THING_MODEL,
                    readJsonArray(actualSnapshot, "thingModelMappings"));
            appendJsonArraySheet(workbook, SHEET_SAMPLE_FRAMES,
                    List.of("messageType", "direction", "rawFrame", "expectedJson", "parsePassed", "description"),
                    readJsonArray(actualSnapshot, "sampleFrames"));
            appendGenerationStrategySheet(workbook, actualSnapshot.getJSONObject("generationStrategy"));
            appendJsonArraySheet(workbook, SHEET_QUALITY_ISSUES,
                    List.of("level", "code", "message", "source", "status", "owner", "remark"),
                    readJsonArray(actualSnapshot, "qualityIssues"));
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void appendWorkbookInstructionSheet(Workbook workbook, AiProtocolAdaptationTask task, Path dslSnapshotPath) {
        Sheet sheet = workbook.createSheet(SHEET_INSTRUCTION);
        sheet.createFreezePane(0, 1);
        sheet.setColumnWidth(0, 24 * 256);
        sheet.setColumnWidth(1, 96 * 256);
        CellStyle headerStyle = createWorkbookHeaderStyle(workbook);
        CellStyle contentStyle = createWorkbookContentStyle(workbook);
        Row headerRow = sheet.createRow(0);
        writeCell(headerRow, 0, "项目", headerStyle);
        writeCell(headerRow, 1, "说明", headerStyle);
        int rowIndex = 1;
        rowIndex = appendKeyValueRow(sheet, rowIndex, contentStyle, "工作簿定位",
                "本工作簿用于客户协议适配：承接 AI DSL 草稿、人工校对、重新导入、质量门禁和后续代码生成。");
        rowIndex = appendKeyValueRow(sheet, rowIndex, contentStyle, "任务编号", String.valueOf(task.getTaskId()));
        rowIndex = appendKeyValueRow(sheet, rowIndex, contentStyle, "任务名称", trimToEmpty(task.getTaskName()));
        rowIndex = appendKeyValueRow(sheet, rowIndex, contentStyle, "当前 DSL", dslSnapshotPath.toString());
        rowIndex = appendKeyValueRow(sheet, rowIndex, contentStyle, "填写约束",
                "不要修改 Sheet 名和第一行字段编码。允许补充、修改或删除业务行；空行导入时会被忽略。");
        rowIndex = appendKeyValueRow(sheet, rowIndex, contentStyle, "生产门禁",
                "导入后必须重新执行质量校验；存在阻断项时不会进入 FastBee 协议代码生成。");
        rowIndex = appendKeyValueRow(sheet, rowIndex, contentStyle, "关键 Sheet",
                "协议总览、报文类型、字段定义、编解码规则、物模型映射、样例报文、生成策略、质量问题。");
        appendKeyValueRow(sheet, rowIndex, contentStyle, "物模型映射",
                "与 FastBee 物模型导入模板保持一致，仅填写物模型名称、英文物模型名称、标识符、数据类型、计算公式、排序值、单位、有效值范围、模型类别、是否图表展示、是否实时监测、是否历史存储、是否只读数据、是否设备分享权限；协议字段来源由字段定义 Sheet 自动推断，未识别到物模型时保持空表。");
    }

    private void appendProtocolOverviewSheet(Workbook workbook, JSONObject protocol) {
        Sheet sheet = workbook.createSheet(SHEET_PROTOCOL);
        sheet.createFreezePane(0, 1);
        sheet.setColumnWidth(0, 24 * 256);
        sheet.setColumnWidth(1, 24 * 256);
        sheet.setColumnWidth(2, 40 * 256);
        sheet.setColumnWidth(3, 72 * 256);
        CellStyle headerStyle = createWorkbookHeaderStyle(workbook);
        CellStyle contentStyle = createWorkbookContentStyle(workbook);
        Row headerRow = sheet.createRow(0);
        writeCell(headerRow, 0, "key", headerStyle);
        writeCell(headerRow, 1, "name", headerStyle);
        writeCell(headerRow, 2, "value", headerStyle);
        writeCell(headerRow, 3, "description", headerStyle);
        int rowIndex = 1;
        rowIndex = appendProtocolRow(sheet, rowIndex, contentStyle, protocol,
                "protocolCode", "协议编码", "生成协议类、包名和知识资产标识的稳定编码");
        rowIndex = appendProtocolRow(sheet, rowIndex, contentStyle, protocol,
                "protocolName", "协议名称", "面向企业审查和检索的协议名称");
        rowIndex = appendProtocolRow(sheet, rowIndex, contentStyle, protocol,
                "protocolFamily", "协议族", "例如 TCP、UDP、MQTT、MODBUS、SERIAL 或自定义协议族");
        rowIndex = appendProtocolRow(sheet, rowIndex, contentStyle, protocol,
                "messageFormat", "报文格式", "例如 BINARY_FRAME、JSON、REGISTER_TABLE");
        rowIndex = appendProtocolRow(sheet, rowIndex, contentStyle, protocol,
                "transport", "传输方式", "例如 TCP、UDP、MQTT、SERIAL、HTTP");
        appendProtocolRow(sheet, rowIndex, contentStyle, protocol,
                "sourceFileName", "来源文件", "原始客户协议文档名称，仅用于审计追溯");
    }

    private int appendProtocolRow(Sheet sheet, int rowIndex, CellStyle style, JSONObject protocol,
                                  String key, String name, String description) {
        Row row = sheet.createRow(rowIndex);
        writeCell(row, 0, key, style);
        writeCell(row, 1, name, style);
        writeCell(row, 2, protocol == null ? "" : trimToEmpty(protocol.getString(key)), style);
        writeCell(row, 3, description, style);
        return rowIndex + 1;
    }

    private void appendGenerationStrategySheet(Workbook workbook, JSONObject generationStrategy) {
        JSONObject strategy = generationStrategy == null ? new JSONObject() : generationStrategy;
        JSONArray rows = new JSONArray();
        JSONObject row = new JSONObject();
        row.put("target", defaultIfBlank(strategy.getString("target"), "fastbee-protocol"));
        row.put("allowCodeGeneration", defaultIfBlank(strategy.getString("allowCodeGeneration"), "false"));
        row.put("requiresManualConfirmation", defaultIfBlank(strategy.getString("requiresManualConfirmation"), "true"));
        row.put("packageName", trimToEmpty(strategy.getString("packageName")));
        row.put("className", trimToEmpty(strategy.getString("className")));
        row.put("reason", trimToEmpty(strategy.getString("reason")));
        rows.add(row);
        appendJsonArraySheet(workbook, SHEET_GENERATION_STRATEGY,
                List.of("target", "allowCodeGeneration", "requiresManualConfirmation", "packageName", "className", "reason"),
                rows);
    }

    private JSONArray normalizeThingModelMappingsForWorkbook(JSONArray mappings) {
        JSONArray normalizedRows = new JSONArray();
        JSONArray actualMappings = mappings == null ? new JSONArray() : mappings;
        for (int index = 0; index < actualMappings.size(); index++) {
            Object item = actualMappings.get(index);
            if (!(item instanceof JSONObject)) {
                continue;
            }
            JSONObject source = (JSONObject) item;
            JSONObject row = new JSONObject();
            row.putAll(source);

            String identifier = getFirstNonBlankString(row, "identifier");
            String datatype = normalizeThingModelDataType(getFirstNonBlankString(row, "datatype", "dataType", "valueType"));
            if (StringUtils.isNotBlank(datatype)) {
                row.put("datatype", datatype);
            }
            putIfBlank(row, "modelName", getFirstNonBlankString(row, "displayName", "modelName_zh_CN", "fieldName", "identifier"));
            putIfBlank(row, "modelName_en_US", identifier);
            putIfBlank(row, "modelType", normalizeThingModelModelType(row));
            putIfBlank(row, "typeStr", normalizeThingModelTypeStr(row));
            putIfBlank(row, "unit", inferThingModelUnitFromSpecs(getFirstNonBlankString(row, "specs")));
            putIfBlank(row, "limitValue", inferThingModelLimitValueFromSpecs(getFirstNonBlankString(row, "specs"), datatype));
            putIfBlank(row, "isChartStr", normalizeThingModelBoolText(getFirstNonBlankString(row, "isChart"),
                    defaultThingModelFlag(row, "isChartStr")));
            putIfBlank(row, "isMonitorStr", normalizeThingModelBoolText(getFirstNonBlankString(row, "isMonitor"),
                    defaultThingModelFlag(row, "isMonitorStr")));
            putIfBlank(row, "isHistoryStr", normalizeThingModelBoolText(getFirstNonBlankString(row, "isHistory"),
                    defaultThingModelFlag(row, "isHistoryStr")));
            putIfBlank(row, "isReadonlyStr", normalizeThingModelBoolText(getFirstNonBlankString(row, "isReadonly"),
                    defaultIfBlank(inferReadonlyText(getFirstNonBlankString(row, "accessMode")),
                            defaultThingModelFlag(row, "isReadonlyStr"))));
            putIfBlank(row, "isSharePermStr", normalizeThingModelBoolText(getFirstNonBlankString(row, "isSharePerm"),
                    defaultThingModelFlag(row, "isSharePermStr")));
            putIfBlank(row, "specs", buildThingModelSpecs(row));
            normalizedRows.add(row);
        }
        return normalizedRows;
    }

    private JSONArray normalizeThingModelMappingsFromWorkbook(JSONArray rawRows, JSONArray fields) {
        JSONArray normalizedRows = new JSONArray();
        JSONArray actualRows = rawRows == null ? new JSONArray() : rawRows;
        for (int index = 0; index < actualRows.size(); index++) {
            Object item = actualRows.get(index);
            if (!(item instanceof JSONObject)) {
                continue;
            }
            JSONObject source = (JSONObject) item;
            String identifier = getFirstNonBlankString(source, "identifier", "标识符");
            String modelName = getFirstNonBlankString(source, "modelName", "物模型名称", "displayName", "modelName_zh_CN");
            if (StringUtils.isBlank(identifier) && StringUtils.isBlank(modelName)) {
                continue;
            }

            JSONObject matchedField = findMatchedProtocolField(fields, identifier, modelName);
            JSONObject row = new JSONObject();
            row.put("identifier", identifier);
            row.put("modelName", modelName);
            row.put("displayName", modelName);
            row.put("modelName_en_US", getFirstNonBlankString(source, "modelName_en_US", "英文物模型名称"));
            row.put("datatype", normalizeThingModelDataType(getFirstNonBlankString(source, "datatype", "dataType", "数据类型", "valueType")));
            row.put("formula", getFirstNonBlankString(source, "formula", "计算公式"));
            row.put("modelOrder", getFirstNonBlankString(source, "modelOrder", "排序值"));
            row.put("unit", getFirstNonBlankString(source, "unit", "单位"));
            row.put("limitValue", getFirstNonBlankString(source, "limitValue", "有效值范围"));
            row.put("typeStr", getFirstNonBlankString(source, "typeStr", "模型类别"));
            row.put("modelType", normalizeThingModelModelType(row));
            row.put("isChartStr", normalizeThingModelBoolText(getFirstNonBlankString(source, "isChartStr", "是否图表展示"), ""));
            row.put("isMonitorStr", normalizeThingModelBoolText(getFirstNonBlankString(source, "isMonitorStr", "是否实时监测"), ""));
            row.put("isHistoryStr", normalizeThingModelBoolText(getFirstNonBlankString(source, "isHistoryStr", "是否历史存储"), ""));
            row.put("isReadonlyStr", normalizeThingModelBoolText(getFirstNonBlankString(source, "isReadonlyStr", "是否只读数据"), ""));
            row.put("isSharePermStr", normalizeThingModelBoolText(getFirstNonBlankString(source, "isSharePermStr", "是否设备分享权限"), ""));

            String sourceField = getFirstNonBlankString(source, "sourceField", "来源字段", "协议字段", "报文字段", "jsonPath", "JSON路径");
            if (StringUtils.isBlank(sourceField) && matchedField != null) {
                sourceField = getFirstNonBlankString(matchedField, "jsonPath", "sourceField", "fieldCode", "fieldName");
            }
            row.put("sourceField", defaultIfBlank(sourceField, identifier));
            if (matchedField != null) {
                row.put("messageType", getFirstNonBlankString(matchedField, "messageType"));
            }
            putIfBlank(row, "modelName_en_US", identifier);
            putIfBlank(row, "typeStr", normalizeThingModelTypeStr(row));
            putIfBlank(row, "specs", buildThingModelSpecs(row));
            normalizedRows.add(row);
        }
        return normalizedRows;
    }

    private JSONObject findMatchedProtocolField(JSONArray fields, String identifier, String modelName) {
        JSONArray actualFields = fields == null ? new JSONArray() : fields;
        for (int index = 0; index < actualFields.size(); index++) {
            Object item = actualFields.get(index);
            if (!(item instanceof JSONObject)) {
                continue;
            }
            JSONObject field = (JSONObject) item;
            if (matchAnyIgnoreCase(identifier,
                    getFirstNonBlankString(field, "fieldName"),
                    getFirstNonBlankString(field, "fieldCode"),
                    getFirstNonBlankString(field, "identifier"),
                    getFirstNonBlankString(field, "sourceField"),
                    getFirstNonBlankString(field, "jsonPath"))
                    || matchAnyIgnoreCase(modelName,
                    getFirstNonBlankString(field, "displayName"),
                    getFirstNonBlankString(field, "fieldName"))) {
                return field;
            }
        }
        return null;
    }

    private boolean matchAnyIgnoreCase(String expected, String... candidates) {
        if (StringUtils.isBlank(expected) || candidates == null) {
            return false;
        }
        String normalizedExpected = expected.trim().toLowerCase(Locale.ROOT);
        for (String candidate : candidates) {
            if (StringUtils.isNotBlank(candidate) && normalizedExpected.equals(candidate.trim().toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private String defaultThingModelFlag(JSONObject row, String key) {
        String typeStr = normalizeThingModelTypeStr(row);
        boolean property = "属性".equals(typeStr);
        switch (key) {
            case "isChartStr":
            case "isMonitorStr":
                return property ? "是" : "否";
            case "isHistoryStr":
                return "是";
            case "isReadonlyStr":
                return property ? "是" : "否";
            case "isSharePermStr":
                return "否";
            default:
                return "";
        }
    }

    private void putIfBlank(JSONObject row, String key, String value) {
        if (row == null || StringUtils.isNotBlank(row.getString(key)) || StringUtils.isBlank(value)) {
            return;
        }
        row.put(key, value);
    }

    private String getFirstNonBlankString(JSONObject object, String... keys) {
        if (object == null || keys == null) {
            return "";
        }
        for (String key : keys) {
            Object value = object.get(key);
            if (value == null) {
                continue;
            }
            String text = value instanceof JSONObject || value instanceof JSONArray
                    ? JSON.toJSONString(value)
                    : String.valueOf(value);
            if (StringUtils.isNotBlank(text)) {
                return text.trim();
            }
        }
        return "";
    }

    private String normalizeThingModelDataType(String value) {
        String normalized = trimToEmpty(value).toLowerCase(Locale.ROOT);
        switch (normalized) {
            case "int":
            case "integer":
            case "long":
            case "short":
                return "integer";
            case "decimal":
            case "double":
            case "float":
            case "number":
            case "numeric":
                return "decimal";
            case "boolean":
            case "bool":
                return "bool";
            case "string":
            case "text":
            case "varchar":
                return "string";
            case "array":
                return "array";
            case "enum":
                return "enum";
            default:
                return StringUtils.isBlank(value) ? "" : value.trim();
        }
    }

    private String normalizeThingModelModelType(JSONObject row) {
        String modelType = getFirstNonBlankString(row, "modelType");
        if (StringUtils.isNotBlank(modelType)) {
            return modelType;
        }
        String typeText = normalizeText(getFirstNonBlankString(row, "typeStr", "type"));
        if (typeText.contains("属性") || typeText.contains("property") || "1".equals(typeText)) {
            return "PROPERTY";
        }
        if (typeText.contains("功能") || typeText.contains("服务") || typeText.contains("function")
                || typeText.contains("service") || "2".equals(typeText)) {
            return "FUNCTION";
        }
        if (typeText.contains("事件") || typeText.contains("event") || "3".equals(typeText)) {
            return "EVENT";
        }
        return "";
    }

    private String normalizeThingModelTypeStr(JSONObject row) {
        String typeText = normalizeText(getFirstNonBlankString(row, "typeStr", "modelType", "type"));
        if (typeText.contains("属性") || typeText.contains("property") || "1".equals(typeText)) {
            return "属性";
        }
        if (typeText.contains("功能") || typeText.contains("服务") || typeText.contains("function")
                || typeText.contains("service") || "2".equals(typeText)) {
            return "功能";
        }
        if (typeText.contains("事件") || typeText.contains("event") || "3".equals(typeText)) {
            return "事件";
        }
        return "";
    }

    private String normalizeThingModelBoolText(String value, String defaultValue) {
        String normalized = trimToEmpty(value).toLowerCase(Locale.ROOT);
        if (StringUtils.isBlank(normalized)) {
            return trimToEmpty(defaultValue);
        }
        if ("1".equals(normalized) || "true".equals(normalized) || "y".equals(normalized)
                || "yes".equals(normalized) || "是".equals(normalized)) {
            return "是";
        }
        if ("0".equals(normalized) || "false".equals(normalized) || "n".equals(normalized)
                || "no".equals(normalized) || "否".equals(normalized)) {
            return "否";
        }
        return value.trim();
    }

    private String inferReadonlyText(String accessMode) {
        String normalized = trimToEmpty(accessMode).toUpperCase(Locale.ROOT);
        if ("READ".equals(normalized) || "READONLY".equals(normalized) || "RO".equals(normalized)) {
            return "是";
        }
        if ("WRITE".equals(normalized) || "READ_WRITE".equals(normalized) || "RW".equals(normalized)) {
            return "否";
        }
        return "";
    }

    private String buildThingModelSpecs(JSONObject row) {
        String datatype = normalizeThingModelDataType(getFirstNonBlankString(row, "datatype", "dataType", "valueType"));
        if (StringUtils.isBlank(datatype)) {
            return "";
        }
        JSONObject specs = new JSONObject();
        specs.put("type", datatype);
        String unit = getFirstNonBlankString(row, "unit");
        String limitValue = getFirstNonBlankString(row, "limitValue");
        switch (datatype) {
            case "integer":
            case "decimal":
                if (StringUtils.isNotBlank(unit)) {
                    specs.put("unit", unit);
                }
                String[] range = splitLimitValue(limitValue);
                if (range.length >= 2) {
                    specs.put("min", range[0]);
                    specs.put("max", range[1]);
                }
                putJsonIfNotBlank(specs, "step", getFirstNonBlankString(row, "step"));
                break;
            case "bool":
                String[] boolTexts = splitLimitValue(limitValue);
                if (boolTexts.length >= 2) {
                    specs.put("falseText", boolTexts[0]);
                    specs.put("trueText", boolTexts[1]);
                }
                putJsonIfNotBlank(specs, "falseText", getFirstNonBlankString(row, "falseText"));
                putJsonIfNotBlank(specs, "trueText", getFirstNonBlankString(row, "trueText"));
                break;
            case "string":
                String maxLength = getFirstNonBlankString(row, "maxLength");
                if (StringUtils.isBlank(maxLength)) {
                    String[] stringRange = splitLimitValue(limitValue);
                    if (stringRange.length >= 2) {
                        maxLength = stringRange[1];
                    }
                }
                putJsonIfNotBlank(specs, "maxLength", maxLength);
                break;
            case "array":
                putJsonIfNotBlank(specs, "arrayType", getFirstNonBlankString(row, "arrayType"));
                break;
            case "enum":
                JSONArray enumList = parseThingModelEnumList(limitValue);
                if (!enumList.isEmpty()) {
                    specs.put("showWay", "select");
                    specs.put("enumList", enumList);
                }
                break;
            default:
                break;
        }
        return JSON.toJSONString(specs);
    }

    private void putJsonIfNotBlank(JSONObject object, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            object.put(key, value.trim());
        }
    }

    private String[] splitLimitValue(String limitValue) {
        if (StringUtils.isBlank(limitValue)) {
            return new String[0];
        }
        return limitValue.trim().split("/", -1);
    }

    private JSONArray parseThingModelEnumList(String limitValue) {
        JSONArray enumList = new JSONArray();
        if (StringUtils.isBlank(limitValue)) {
            return enumList;
        }
        String[] items = limitValue.trim().split("/");
        for (String item : items) {
            String[] pair = item.split(":", 2);
            if (pair.length < 2 || StringUtils.isBlank(pair[0]) || StringUtils.isBlank(pair[1])) {
                continue;
            }
            JSONObject enumItem = new JSONObject();
            enumItem.put("value", pair[0].trim());
            enumItem.put("text", pair[1].trim());
            enumList.add(enumItem);
        }
        return enumList;
    }

    private String inferThingModelUnitFromSpecs(String specsText) {
        JSONObject specs = parseThingModelSpecsObject(specsText);
        return specs == null ? "" : trimToEmpty(specs.getString("unit"));
    }

    private String inferThingModelLimitValueFromSpecs(String specsText, String datatype) {
        JSONObject specs = parseThingModelSpecsObject(specsText);
        if (specs == null) {
            return "";
        }
        String actualDatatype = normalizeThingModelDataType(defaultIfBlank(datatype, specs.getString("type")));
        switch (actualDatatype) {
            case "integer":
            case "decimal":
                String min = trimToEmpty(specs.getString("min"));
                String max = trimToEmpty(specs.getString("max"));
                return StringUtils.isNotBlank(min) && StringUtils.isNotBlank(max) ? min + "/" + max : "";
            case "bool":
                String falseText = trimToEmpty(specs.getString("falseText"));
                String trueText = trimToEmpty(specs.getString("trueText"));
                return StringUtils.isNotBlank(falseText) && StringUtils.isNotBlank(trueText) ? falseText + "/" + trueText : "";
            case "string":
                String maxLength = trimToEmpty(specs.getString("maxLength"));
                return StringUtils.isNotBlank(maxLength) ? "0/" + maxLength : "";
            case "enum":
                JSONArray enumList = specs.getJSONArray("enumList");
                if (enumList == null || enumList.isEmpty()) {
                    return "";
                }
                StringBuilder builder = new StringBuilder();
                for (int index = 0; index < enumList.size(); index++) {
                    JSONObject enumItem = enumList.getJSONObject(index);
                    if (enumItem == null) {
                        continue;
                    }
                    String value = trimToEmpty(enumItem.getString("value"));
                    String text = trimToEmpty(enumItem.getString("text"));
                    if (StringUtils.isBlank(value) || StringUtils.isBlank(text)) {
                        continue;
                    }
                    if (builder.length() > 0) {
                        builder.append("/");
                    }
                    builder.append(value).append(":").append(text);
                }
                return builder.toString();
            default:
                return "";
        }
    }

    private JSONObject parseThingModelSpecsObject(String specsText) {
        if (StringUtils.isBlank(specsText)) {
            return null;
        }
        try {
            return JSON.parseObject(specsText);
        } catch (Exception ignored) {
            return null;
        }
    }

    private void appendJsonArraySheetWithLabels(Workbook workbook, String sheetName, List<String> keys,
                                                List<String> headers, JSONArray rows) {
        Sheet sheet = workbook.createSheet(sheetName);
        sheet.createFreezePane(0, 1);
        CellStyle headerStyle = createWorkbookHeaderStyle(workbook);
        CellStyle contentStyle = createWorkbookContentStyle(workbook);
        Row headerRow = sheet.createRow(0);
        List<String> actualKeys = keys == null ? Collections.emptyList() : keys;
        List<String> actualHeaders = headers == null ? actualKeys : headers;
        for (int index = 0; index < actualHeaders.size(); index++) {
            writeCell(headerRow, index, actualHeaders.get(index), headerStyle);
            sheet.setColumnWidth(index, Math.min(72, Math.max(18, actualHeaders.get(index).length() + 14)) * 256);
        }
        JSONArray actualRows = rows == null ? new JSONArray() : rows;
        for (int rowIndex = 0; rowIndex < actualRows.size(); rowIndex++) {
            Object item = actualRows.get(rowIndex);
            JSONObject object = item instanceof JSONObject ? (JSONObject) item : new JSONObject();
            Row row = sheet.createRow(rowIndex + 1);
            for (int columnIndex = 0; columnIndex < actualHeaders.size(); columnIndex++) {
                String key = columnIndex < actualKeys.size() ? actualKeys.get(columnIndex) : actualHeaders.get(columnIndex);
                writeCell(row, columnIndex, formatWorkbookCellValue(object.get(key)), contentStyle);
            }
        }
    }

    private void appendJsonArraySheet(Workbook workbook, String sheetName, List<String> headers, JSONArray rows) {
        appendJsonArraySheetWithLabels(workbook, sheetName, headers, headers, rows);
    }

    private String formatWorkbookCellValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof JSONObject || value instanceof JSONArray) {
            return JSON.toJSONString(value);
        }
        return String.valueOf(value);
    }

    private int appendKeyValueRow(Sheet sheet, int rowIndex, CellStyle style, String key, String value) {
        Row row = sheet.createRow(rowIndex);
        writeCell(row, 0, key, style);
        writeCell(row, 1, value, style);
        return rowIndex + 1;
    }

    private CellStyle createWorkbookHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        style.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createWorkbookContentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.TOP);
        return style;
    }

    private void writeCell(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(trimToEmpty(value));
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private JSONObject buildDslSnapshotFromWorkbook(AiProtocolAdaptationTask task, Path workbookPath,
                                                    byte[] workbookBytes) throws IOException {
        try (Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(new ByteArrayInputStream(workbookBytes))) {
            JSONObject protocol = readProtocolOverviewFromWorkbook(workbook, task);
            JSONArray messageTypes = readWorkbookRows(workbook, SHEET_MESSAGE_TYPES);
            JSONArray fields = readWorkbookRows(workbook, SHEET_FIELDS);
            JSONArray codecRules = readWorkbookRows(workbook, SHEET_CODEC_RULES);
            JSONArray thingModelMappings = aiThingModelWorkbookSupport
                    .normalizeThingModelMappingsFromWorkbook(readWorkbookRows(workbook, SHEET_THING_MODEL), fields);
            JSONArray sampleFrames = readWorkbookRows(workbook, SHEET_SAMPLE_FRAMES);
            JSONArray qualityIssues = readQualityIssuesFromWorkbook(workbook);
            JSONObject generationStrategy = readGenerationStrategyFromWorkbook(workbook);

            JSONObject snapshot = new JSONObject();
            snapshot.put("snapshotType", ARTIFACT_TYPE_AI_DSL_DRAFT);
            snapshot.put("schemaVersion", "protocol-dsl-0.1");
            snapshot.put("generatedAt", formatNow());
            snapshot.put("generatedBy", "ENTERPRISE_WORKBOOK_IMPORT");
            snapshot.put("taskId", task.getTaskId());
            snapshot.put("sourceWorkbookPath", workbookPath.toString());
            snapshot.put("draftCompleteness", "REVIEWED_CANDIDATE");
            snapshot.put("protocol", protocol);
            snapshot.put("messageTypes", messageTypes);
            snapshot.put("fields", fields);
            snapshot.put("codecRules", codecRules);
            snapshot.put("thingModelMappings", thingModelMappings);
            snapshot.put("sampleFrames", sampleFrames);
            snapshot.put("sourceTables", new JSONArray());
            snapshot.put("qualityIssues", qualityIssues);
            snapshot.put("generationStrategy", generationStrategy);

            JSONObject importSummary = new JSONObject();
            importSummary.put("messageTypeCount", messageTypes.size());
            importSummary.put("fieldCount", fields.size());
            importSummary.put("codecRuleCount", codecRules.size());
            importSummary.put("thingModelMappingCount", thingModelMappings.size());
            importSummary.put("sampleFrameCount", sampleFrames.size());
            importSummary.put("qualityIssueCount", qualityIssues.size());
            snapshot.put("workbookImportSummary", importSummary);
            return snapshot;
        } catch (Exception ex) {
            throw new IOException("解析人工回填工作簿失败：" + ex.getMessage(), ex);
        }
    }

    private JSONObject readProtocolOverviewFromWorkbook(Workbook workbook, AiProtocolAdaptationTask task) {
        JSONObject protocol = new JSONObject();
        protocol.put("protocolCode", trimToEmpty(task.getProtocolCode()));
        protocol.put("protocolName", trimToEmpty(task.getProtocolName()));
        protocol.put("protocolFamily", "");
        Sheet sheet = workbook.getSheet(SHEET_PROTOCOL);
        if (sheet == null) {
            return protocol;
        }
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            String key = trimToEmpty(getCellText(row.getCell(0)));
            if (StringUtils.isBlank(key)) {
                continue;
            }
            protocol.put(key, trimToEmpty(getCellText(row.getCell(2))));
        }
        return protocol;
    }

    private JSONArray readWorkbookRows(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null || sheet.getLastRowNum() < 1) {
            return new JSONArray();
        }
        Map<Integer, String> headers = readHeaderMap(sheet.getRow(0));
        JSONArray rows = new JSONArray();
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null || isBlankWorkbookRow(row, headers)) {
                continue;
            }
            JSONObject object = new JSONObject();
            for (Map.Entry<Integer, String> entry : headers.entrySet()) {
                String value = trimToEmpty(getCellText(row.getCell(entry.getKey())));
                if (StringUtils.isNotBlank(value)) {
                    object.put(entry.getValue(), value);
                }
            }
            if (!object.isEmpty()) {
                rows.add(object);
            }
        }
        return rows;
    }

    private JSONArray readQualityIssuesFromWorkbook(Workbook workbook) {
        JSONArray rawRows = readWorkbookRows(workbook, SHEET_QUALITY_ISSUES);
        JSONArray issues = new JSONArray();
        for (int index = 0; index < rawRows.size(); index++) {
            JSONObject row = rawRows.getJSONObject(index);
            if (row == null) {
                continue;
            }
            String status = defaultIfBlank(row.getString("status"), "").toUpperCase(Locale.ROOT);
            if ("RESOLVED".equals(status) || "已处理".equals(status)) {
                continue;
            }
            if (StringUtils.isBlank(row.getString("level")) && StringUtils.isBlank(row.getString("code"))
                    && StringUtils.isBlank(row.getString("message"))) {
                continue;
            }
            issues.add(row);
        }
        return issues;
    }

    private JSONObject readGenerationStrategyFromWorkbook(Workbook workbook) {
        JSONArray rows = readWorkbookRows(workbook, SHEET_GENERATION_STRATEGY);
        JSONObject row = rows.isEmpty() ? new JSONObject() : rows.getJSONObject(0);
        JSONObject strategy = new JSONObject();
        strategy.put("target", defaultIfBlank(row.getString("target"), "fastbee-protocol"));
        strategy.put("allowCodeGeneration", parseWorkbookBoolean(row.getString("allowCodeGeneration"), false));
        strategy.put("requiresManualConfirmation", parseWorkbookBoolean(row.getString("requiresManualConfirmation"), true));
        strategy.put("packageName", trimToEmpty(row.getString("packageName")));
        strategy.put("className", trimToEmpty(row.getString("className")));
        strategy.put("reason", trimToEmpty(row.getString("reason")));
        return strategy;
    }

    private Map<Integer, String> readHeaderMap(Row headerRow) {
        if (headerRow == null) {
            return Collections.emptyMap();
        }
        Map<Integer, String> headers = new LinkedHashMap<>();
        short lastCellNum = headerRow.getLastCellNum();
        for (int columnIndex = 0; columnIndex < lastCellNum; columnIndex++) {
            String header = trimToEmpty(getCellText(headerRow.getCell(columnIndex)));
            if (StringUtils.isNotBlank(header)) {
                headers.put(columnIndex, header);
            }
        }
        return headers;
    }

    private boolean isBlankWorkbookRow(Row row, Map<Integer, String> headers) {
        for (Integer columnIndex : headers.keySet()) {
            if (StringUtils.isNotBlank(getCellText(row.getCell(columnIndex)))) {
                return false;
            }
        }
        return true;
    }

    private boolean parseWorkbookBoolean(String value, boolean defaultValue) {
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return "true".equals(normalized) || "1".equals(normalized) || "y".equals(normalized)
                || "yes".equals(normalized) || "是".equals(normalized) || "允许".equals(normalized);
    }

    private String buildWorkbookFilename(AiProtocolAdaptationTask task, String prefix) {
        return prefix + "_" + task.getTaskId() + ".xlsx";
    }

    private AiProtocolAdaptationArtifact buildGeneratedArtifact(AiProtocolAdaptationTask task, String artifactType,
                                                                String artifactName, Path artifactPath,
                                                                String sourceType, String summary) throws IOException {
        byte[] fileBytes = Files.readAllBytes(artifactPath);
        AiProtocolAdaptationArtifact artifact = new AiProtocolAdaptationArtifact();
        artifact.setTaskId(task.getTaskId());
        artifact.setArtifactType(artifactType);
        artifact.setArtifactName(artifactName);
        artifact.setFilePath(artifactPath.toString());
        artifact.setFileSize((long) fileBytes.length);
        artifact.setChecksum(DigestUtils.md5DigestAsHex(fileBytes));
        artifact.setSourceType(sourceType);
        artifact.setArtifactStatus(ARTIFACT_STATUS_READY);
        artifact.setSummary(summary);
        artifact.setStatus(STATUS_ENABLED);
        artifact.setCreateBy(AiSecuritySupport.resolveUsername());
        artifact.setCreateTime(AiSecuritySupport.now());
        artifact.setUpdateBy(AiSecuritySupport.resolveUsername());
        artifact.setUpdateTime(AiSecuritySupport.now());
        return artifact;
    }

    private Path saveJsonArtifactFile(AiProtocolAdaptationTask task, String artifactType,
                                      String filePrefix, JSONObject jsonObject) throws IOException {
        Path targetDir = Paths.get(RuoYiConfig.getProfile(), "ai", "protocol", "adaptation",
                buildTaskDirectoryName(task), artifactType.toLowerCase(Locale.ROOT));
        Files.createDirectories(targetDir);
        Path targetFile = targetDir.resolve(filePrefix + "_" + System.currentTimeMillis() + ".json");
        Files.writeString(targetFile, JSON.toJSONString(jsonObject), java.nio.charset.StandardCharsets.UTF_8);
        return targetFile;
    }

    private AiProtocolAdaptationTask requireTask(Long taskId) {
        if (taskId == null) {
            throw new ServiceException(message("ai.protocol.adaptation.task.id.required"));
        }
        AiProtocolAdaptationTask task = baseMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException(message("ai.protocol.adaptation.task.not.exists.or.deleted"));
        }
        return task;
    }

    private void validTaskBeforeSave(AiProtocolAdaptationTask task) {
        if (StringUtils.isBlank(task.getTaskName())) {
            throw new ServiceException(message("ai.protocol.adaptation.task.name.required"));
        }
    }

    private String normalizeUploadArtifactType(String artifactType) {
        String actualType = StringUtils.isBlank(artifactType)
                ? ARTIFACT_TYPE_SOURCE_DOCUMENT
                : artifactType.trim().toUpperCase(Locale.ROOT);
        if (!UPLOAD_ARTIFACT_TYPES.contains(actualType)) {
            throw new ServiceException(message("ai.protocol.adaptation.upload.type.unsupported"));
        }
        return actualType;
    }

    private void validateUploadFile(String artifactType, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException(message("ai.protocol.adaptation.upload.file.required"));
        }
        String originalFilename = StringUtils.isBlank(file.getOriginalFilename()) ? "" : file.getOriginalFilename().trim();
        String suffix = getFileSuffix(originalFilename).toLowerCase(Locale.ROOT);
        if (StringUtils.isBlank(suffix)) {
            throw new ServiceException(message("ai.protocol.adaptation.upload.extension.required"));
        }
        if ((ARTIFACT_TYPE_ENTERPRISE_WORKBOOK.equals(artifactType) || ARTIFACT_TYPE_REVIEW_WORKBOOK.equals(artifactType))
                && !WORKBOOK_SUFFIXES.contains(suffix)) {
            throw new ServiceException(message("ai.protocol.adaptation.enterprise.workbook.only"));
        }
        if (ARTIFACT_TYPE_SOURCE_DOCUMENT.equals(artifactType) && !SOURCE_DOCUMENT_SUFFIXES.contains(suffix)) {
            throw new ServiceException(message("ai.protocol.adaptation.original.document.only"));
        }
    }

    private String resolveOriginalFilename(String artifactType, String originalFilename) {
        if (StringUtils.isNotBlank(originalFilename)) {
            return originalFilename.trim();
        }
        if (ARTIFACT_TYPE_ENTERPRISE_WORKBOOK.equals(artifactType) || ARTIFACT_TYPE_REVIEW_WORKBOOK.equals(artifactType)) {
            return "protocol-workbook.xlsx";
        }
        return "protocol-document.txt";
    }

    private Path saveArtifactFile(AiProtocolAdaptationTask task, String artifactType,
                                  String originalFilename, byte[] fileBytes) throws IOException {
        String suffix = getFileSuffix(originalFilename);
        Path targetDir = Paths.get(RuoYiConfig.getProfile(), "ai", "protocol", "adaptation",
                buildTaskDirectoryName(task), artifactType.toLowerCase(Locale.ROOT));
        Files.createDirectories(targetDir);
        String targetName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().replace("-", "") + suffix;
        Path targetFile = targetDir.resolve(targetName);
        Files.write(targetFile, fileBytes);
        return targetFile;
    }

    private String safePathSegment(String value) {
        String safeValue = value == null ? "" : value.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9._-]", "_");
        return StringUtils.isBlank(safeValue) ? "task" : safeValue;
    }

    private String buildTaskDirectoryName(AiProtocolAdaptationTask task) {
        Long taskId = task == null ? null : task.getTaskId();
        return "task_" + (taskId == null ? "unknown" : taskId);
    }

    private String buildUploadSummary(String artifactType, String originalFilename, int fileSize) {
        return "上传产物类型=" + artifactType + "；原始文件名=" + originalFilename + "；文件大小=" + fileSize;
    }

    private String getFileSuffix(String filename) {
        if (StringUtils.isBlank(filename)) {
            return "";
        }
        int index = filename.lastIndexOf('.');
        return index >= 0 ? filename.substring(index) : "";
    }

    private String decodeText(byte[] fileBytes) {
        String utf8Text = new String(fileBytes, java.nio.charset.StandardCharsets.UTF_8);
        if (utf8Text.contains("\uFFFD")) {
            return new String(fileBytes, Charset.forName("GBK"));
        }
        return utf8Text;
    }

    private int countLines(String text) {
        if (StringUtils.isBlank(text)) {
            return 0;
        }
        return text.split("\\R", -1).length;
    }

    private String getCellText(Cell cell) {
        if (cell == null) {
            return "";
        }
        return new DataFormatter(Locale.getDefault()).formatCellValue(cell);
    }

    private byte[] readAllBytes(ZipInputStream zipInputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        while ((len = zipInputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
        }
        return outputStream.toByteArray();
    }

    private String unescapeXml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&apos;", "'");
    }

    private String inferProtocolFamily(String contentText) {
        String text = normalizeText(contentText);
        if (text.contains("modbus")) {
            return "MODBUS";
        }
        if (text.contains("mqtt")) {
            return "MQTT";
        }
        if (text.contains("tcp")) {
            return "TCP";
        }
        if (text.contains("udp")) {
            return "UDP";
        }
        return "UNKNOWN";
    }

    private String inferMessageFormat(String contentText) {
        String text = normalizeText(contentText);
        if (text.contains("{") && text.contains("}") && (text.contains("json") || text.contains("\""))) {
            return "JSON";
        }
        if (text.contains("寄存器") || text.contains("register") || text.contains("modbus")) {
            return "REGISTER_TABLE";
        }
        if (text.contains("crc") || text.contains("校验") || text.contains("字节") || text.contains("byte")
                || text.contains("offset") || text.contains("帧头") || text.contains("帧尾")) {
            return "BINARY_FRAME";
        }
        return "UNKNOWN";
    }

    private String inferTransport(String contentText) {
        String text = normalizeText(contentText);
        if (text.contains("mqtt")) {
            return "MQTT";
        }
        if (text.contains("tcp")) {
            return "TCP";
        }
        if (text.contains("udp")) {
            return "UDP";
        }
        if (text.contains("串口") || text.contains("rs485") || text.contains("serial")) {
            return "SERIAL";
        }
        return "UNKNOWN";
    }

    private String normalizeText(String text) {
        return text == null ? "" : text.toLowerCase(Locale.ROOT);
    }

    private String formatNow() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private String abbreviate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, Math.max(maxLength - 3, 0)) + "...";
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value.trim();
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private static class ValidationGateResult {
        private JSONObject report;
        private String validationStatus;
        private String riskLevel;
        private String summary;
    }

    private static class CodePackageBuildResult {
        private String dslSnapshotPath;
        private String generationStrategy;
        private String codePackagePath;
        private String fileManifestPath;
        private String testReportPath;
        private int validationErrorCount;
        private int validationWarningCount;
    }

    private static class CodePackageVerificationResult {
        private JSONObject report;
        private String compileStatus;
        private String sampleReplayStatus;
        private String summary;
    }

    private static class GeneratedCodeMeta {
        private String protocolCode;
        private String protocolName;
        private String packageName;
        private String className;
        private String description;
    }

    private static class ValidationReportSummary {
        private String validationStatus;
        private int blockingIssueCount;
        private int warningIssueCount;
    }

    private static class ExtractedDocument {
        private String fileName;
        private String suffix;
        private String extractorType;
        private String contentText;
        private int textLength;
        private int lineCount;
        private JSONArray tables;
        private int tableCount;
        private boolean truncated;
    }
}
