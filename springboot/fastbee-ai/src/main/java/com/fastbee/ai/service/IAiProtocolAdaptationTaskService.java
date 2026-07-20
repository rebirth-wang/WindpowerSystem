package com.fastbee.ai.service;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.ai.domain.AiProtocolAdaptationTask;
import com.fastbee.ai.model.vo.AiProtocolAdaptationArtifactVO;
import com.fastbee.ai.model.vo.AiProtocolAdaptationAutoRunResultVO;
import com.fastbee.ai.model.vo.AiProtocolAdaptationTaskVO;
import com.fastbee.ai.model.vo.AiProtocolGenerationRecordVO;

/**
 * AI 协议适配任务服务接口。
 */
public interface IAiProtocolAdaptationTaskService extends IService<AiProtocolAdaptationTask> {

    /**
     * 查询协议适配任务列表。
     *
     * @param task 查询条件
     * @return 任务列表
     */
    List<AiProtocolAdaptationTask> listAiProtocolAdaptationTask(AiProtocolAdaptationTask task);

    /**
     * 分页查询协议适配任务展示列表。
     *
     * @param task 查询条件
     * @return 分页结果
     */
    Page<AiProtocolAdaptationTaskVO> pageAiProtocolAdaptationTaskVO(AiProtocolAdaptationTask task);

    /**
     * 查询协议适配任务详情。
     *
     * @param taskId 任务 ID
     * @return 任务展示对象
     */
    AiProtocolAdaptationTaskVO selectAiProtocolAdaptationTaskVO(Long taskId);

    /**
     * 新增协议适配任务。
     *
     * @param task 任务信息
     * @return 影响行数
     */
    int insertAiProtocolAdaptationTask(AiProtocolAdaptationTask task);

    /**
     * 修改协议适配任务。
     *
     * @param task 任务信息
     * @return 影响行数
     */
    int updateAiProtocolAdaptationTask(AiProtocolAdaptationTask task);

    /**
     * 修改协议适配任务状态。
     *
     * @param task 任务状态信息
     * @return 影响行数
     */
    int updateAiProtocolAdaptationTaskStatus(AiProtocolAdaptationTask task);

    /**
     * 删除协议适配任务。
     *
     * @param taskIds 任务 ID 集合
     * @return 影响行数
     */
    int deleteAiProtocolAdaptationTaskByIds(Long[] taskIds);

    /**
     * 上传协议适配任务产物。
     *
     * @param taskId       任务 ID
     * @param artifactType 产物类型
     * @param file         文件
     * @return 产物展示对象
     * @throws Exception 上传异常
     */
    AiProtocolAdaptationArtifactVO uploadTaskArtifact(Long taskId, String artifactType, MultipartFile file) throws Exception;

    /**
     * 解析任务最新原始协议文档并生成 DSL 草稿骨架。
     *
     * @param taskId 任务 ID
     * @return 任务展示对象
     */
    AiProtocolAdaptationTaskVO parseTaskDocument(Long taskId);

    /**
     * 校验任务当前协议 DSL 快照并生成质量门禁报告。
     *
     * @param taskId 任务 ID
     * @return 任务展示对象
     */
    AiProtocolAdaptationTaskVO validateTaskDsl(Long taskId);

    /**
     * 导出任务当前 DSL 对应的企业协议适配工作簿。
     *
     * @param taskId 任务 ID
     * @return 产物展示对象
     */
    AiProtocolAdaptationArtifactVO exportTaskWorkbook(Long taskId);

    /**
     * 导入人工回填工作簿并生成 DSL 候选快照。
     *
     * @param taskId 任务 ID
     * @param file   人工回填工作簿
     * @return 任务展示对象
     * @throws Exception 导入异常
     */
    AiProtocolAdaptationTaskVO importTaskWorkbook(Long taskId, MultipartFile file) throws Exception;

    /**
     * 基于已通过质量门禁的 DSL 生成 FastBee 协议代码包。
     *
     * @param taskId 任务 ID
     * @return 生成记录展示对象
     */
    AiProtocolGenerationRecordVO generateCodePackage(Long taskId);

    /**
     * 对代码生成记录执行首轮静态验证与样例报文回归。
     *
     * @param recordId 生成记录 ID
     * @return 生成记录展示对象
     */
    AiProtocolGenerationRecordVO verifyGenerationRecord(Long recordId);

    /**
     * 下载代码生成记录关联的落盘文件。
     *
     * @param recordId 生成记录 ID
     * @param fileType 文件类型：package、manifest、testReport
     * @param response 响应对象
     */
    void downloadGenerationFile(Long recordId, String fileType, HttpServletResponse response);

    /**
     * 人工确认代码生成记录。
     *
     * @param recordId 生成记录 ID
     * @return 生成记录展示对象
     */
    AiProtocolGenerationRecordVO confirmGenerationRecord(Long recordId);

    /**
     * 自动编排协议适配主链路。
     *
     * @param taskId 任务 ID
     * @return 自动编排结果
     */
    AiProtocolAdaptationAutoRunResultVO autoRunTask(Long taskId);

    /**
     * 查询任务产物列表。
     *
     * @param taskId 任务 ID
     * @return 产物列表
     */
    List<AiProtocolAdaptationArtifactVO> listTaskArtifacts(Long taskId);

    /**
     * 下载协议适配任务产物文件。
     *
     * @param artifactId 产物 ID
     * @param response   响应对象
     */
    void downloadTaskArtifact(Long artifactId, HttpServletResponse response);

    /**
     * 读取协议适配任务产物文本内容。
     *
     * @param artifactId 产物 ID
     * @return 产物文本内容
     */
    Map<String, Object> readTaskArtifactContent(Long artifactId);

    /**
     * 查询任务生成记录列表。
     *
     * @param taskId 任务 ID
     * @return 生成记录列表
     */
    List<AiProtocolGenerationRecordVO> listTaskGenerationRecords(Long taskId);

    /**
     * 查询生成记录详情。
     *
     * @param recordId 生成记录 ID
     * @return 生成记录详情
     */
    AiProtocolGenerationRecordVO selectGenerationRecordVO(Long recordId);
}
