package com.fastbee.ai.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.ai.domain.AiProtocolAdaptationTask;
import com.fastbee.ai.model.vo.AiProtocolAdaptationTaskVO;
import com.fastbee.ai.service.IAiProtocolAdaptationTaskService;
import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.core.controller.BaseController;

/**
 * AI 协议适配任务控制器。
 */
@Api(tags = "AI 协议适配任务")
@RestController
@RequestMapping("/ai/protocol/adaptation")
public class AiProtocolAdaptationController extends BaseController {

    @Resource
    private IAiProtocolAdaptationTaskService aiProtocolAdaptationTaskService;

    /**
     * 查询协议适配任务列表。
     *
     * @param task 查询条件
     * @return 列表结果
     */
    @ApiOperation("查询协议适配任务列表")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiProtocolAdaptationTask task) {
        Page<AiProtocolAdaptationTaskVO> page = aiProtocolAdaptationTaskService.pageAiProtocolAdaptationTaskVO(task);
        return getDataTable(page.getRecords(), page.getTotal());
    }

    /**
     * 查询协议适配任务详情。
     *
     * @param taskId 任务 ID
     * @return 详情
     */
    @ApiOperation("查询协议适配任务详情")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:query')")
    @GetMapping("/{taskId}")
    public AjaxResult getInfo(@PathVariable("taskId") Long taskId) {
        return success(aiProtocolAdaptationTaskService.selectAiProtocolAdaptationTaskVO(taskId));
    }

    /**
     * 新增协议适配任务。
     *
     * @param task 任务信息
     * @return 操作结果
     */
    @ApiOperation("新增协议适配任务")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:add')")
    @Log(title = "AI 协议适配任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiProtocolAdaptationTask task) {
        return toAjax(aiProtocolAdaptationTaskService.insertAiProtocolAdaptationTask(task));
    }

    /**
     * 修改协议适配任务。
     *
     * @param task 任务信息
     * @return 操作结果
     */
    @ApiOperation("修改协议适配任务")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:edit')")
    @Log(title = "AI 协议适配任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiProtocolAdaptationTask task) {
        return toAjax(aiProtocolAdaptationTaskService.updateAiProtocolAdaptationTask(task));
    }

    /**
     * 修改协议适配任务状态。
     *
     * @param task 任务状态信息
     * @return 操作结果
     */
    @ApiOperation("修改协议适配任务状态")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:edit')")
    @Log(title = "AI 协议适配任务", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody AiProtocolAdaptationTask task) {
        return toAjax(aiProtocolAdaptationTaskService.updateAiProtocolAdaptationTaskStatus(task));
    }

    /**
     * 删除协议适配任务。
     *
     * @param taskIds 任务 ID 集合
     * @return 操作结果
     */
    @ApiOperation("删除协议适配任务")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:remove')")
    @Log(title = "AI 协议适配任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable Long[] taskIds) {
        return toAjax(aiProtocolAdaptationTaskService.deleteAiProtocolAdaptationTaskByIds(taskIds));
    }

    /**
     * 上传协议适配任务产物。
     *
     * @param taskId       任务 ID
     * @param artifactType 产物类型
     * @param file         文件
     * @return 产物信息
     * @throws Exception 上传异常
     */
    @ApiOperation("上传协议适配任务产物")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:edit')")
    @Log(title = "AI 协议适配产物", businessType = BusinessType.IMPORT)
    @PostMapping("/{taskId}/upload")
    public AjaxResult upload(@PathVariable("taskId") Long taskId,
                             @RequestParam(value = "artifactType", required = false) String artifactType,
                             @RequestParam("file") MultipartFile file) throws Exception {
        return success(aiProtocolAdaptationTaskService.uploadTaskArtifact(taskId, artifactType, file));
    }

    /**
     * 解析协议适配任务文档。
     *
     * @param taskId 任务 ID
     * @return 任务详情
     */
    @ApiOperation("解析协议适配任务文档")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:edit')")
    @Log(title = "AI 协议适配文档解析", businessType = BusinessType.UPDATE)
    @PostMapping("/{taskId}/parse")
    public AjaxResult parse(@PathVariable("taskId") Long taskId) {
        return success(aiProtocolAdaptationTaskService.parseTaskDocument(taskId));
    }

    /**
     * 校验协议适配任务 DSL。
     *
     * @param taskId 任务 ID
     * @return 任务详情
     */
    @ApiOperation("校验协议适配任务 DSL")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:validate')")
    @Log(title = "AI 协议适配质量校验", businessType = BusinessType.UPDATE)
    @PostMapping("/{taskId}/validate")
    public AjaxResult validate(@PathVariable("taskId") Long taskId) {
        return success(aiProtocolAdaptationTaskService.validateTaskDsl(taskId));
    }

    /**
     * 导出企业协议适配工作簿。
     *
     * @param taskId 任务 ID
     * @return 产物信息
     */
    @ApiOperation("导出企业协议适配工作簿")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:workbook:export')")
    @Log(title = "AI 协议适配工作簿导出", businessType = BusinessType.EXPORT)
    @PostMapping("/{taskId}/workbook/export")
    public AjaxResult exportWorkbook(@PathVariable("taskId") Long taskId) {
        return success(aiProtocolAdaptationTaskService.exportTaskWorkbook(taskId));
    }

    /**
     * 导入人工回填工作簿。
     *
     * @param taskId 任务 ID
     * @param file   人工回填工作簿
     * @return 任务详情
     * @throws Exception 导入异常
     */
    @ApiOperation("导入人工回填工作簿")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:workbook:import')")
    @Log(title = "AI 协议适配工作簿导入", businessType = BusinessType.IMPORT)
    @PostMapping("/{taskId}/workbook/import")
    public AjaxResult importWorkbook(@PathVariable("taskId") Long taskId,
                                     @RequestParam("file") MultipartFile file) throws Exception {
        return success(aiProtocolAdaptationTaskService.importTaskWorkbook(taskId, file));
    }

    /**
     * 生成 FastBee 协议代码包。
     *
     * @param taskId 任务 ID
     * @return 生成记录
     */
    @ApiOperation("生成 FastBee 协议代码包")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:generate')")
    @Log(title = "AI 协议代码生成", businessType = BusinessType.INSERT)
    @PostMapping("/{taskId}/generate")
    public AjaxResult generate(@PathVariable("taskId") Long taskId) {
        return success(aiProtocolAdaptationTaskService.generateCodePackage(taskId));
    }

    /**
     * 验证代码生成记录。
     *
     * @param recordId 生成记录 ID
     * @return 生成记录详情
     */
    @ApiOperation("验证代码生成记录")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:verify')")
    @Log(title = "AI 协议代码生成验证", businessType = BusinessType.UPDATE)
    @PostMapping("/generation/{recordId}/verify")
    public AjaxResult verifyGenerationRecord(@PathVariable("recordId") Long recordId) {
        return success(aiProtocolAdaptationTaskService.verifyGenerationRecord(recordId));
    }

    /**
     * 下载代码生成记录文件。
     *
     * @param recordId 生成记录 ID
     * @param fileType 文件类型
     * @param response 响应对象
     */
    @ApiOperation("下载代码生成记录文件")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:query')")
    @PostMapping("/generation/{recordId}/download/{fileType}")
    public void downloadGenerationFile(@PathVariable("recordId") Long recordId,
                                       @PathVariable("fileType") String fileType,
                                       HttpServletResponse response) {
        aiProtocolAdaptationTaskService.downloadGenerationFile(recordId, fileType, response);
    }

    /**
     * 人工确认代码生成记录。
     *
     * @param recordId 生成记录 ID
     * @return 生成记录详情
     */
    @ApiOperation("人工确认代码生成记录")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:confirm')")
    @Log(title = "AI 协议代码生成确认", businessType = BusinessType.UPDATE)
    @PostMapping("/generation/{recordId}/confirm")
    public AjaxResult confirmGenerationRecord(@PathVariable("recordId") Long recordId) {
        return success(aiProtocolAdaptationTaskService.confirmGenerationRecord(recordId));
    }

    /**
     * 自动编排协议适配主链路。
     *
     * @param taskId 任务 ID
     * @return 自动编排结果
     */
    @ApiOperation("自动编排协议适配主链路")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:auto-run')")
    @Log(title = "AI 协议适配自动编排", businessType = BusinessType.UPDATE)
    @PostMapping("/{taskId}/auto-run")
    public AjaxResult autoRun(@PathVariable("taskId") Long taskId) {
        return success(aiProtocolAdaptationTaskService.autoRunTask(taskId));
    }

    /**
     * 查询任务产物列表。
     *
     * @param taskId 任务 ID
     * @return 产物列表
     */
    @ApiOperation("查询任务产物列表")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:query')")
    @GetMapping("/{taskId}/artifacts")
    public AjaxResult artifacts(@PathVariable("taskId") Long taskId) {
        return success(aiProtocolAdaptationTaskService.listTaskArtifacts(taskId));
    }

    /**
     * 下载任务产物文件。
     *
     * @param artifactId 产物 ID
     * @param response   响应对象
     */
    @ApiOperation("下载任务产物文件")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:query')")
    @PostMapping("/artifact/{artifactId}/download")
    public void downloadArtifact(@PathVariable("artifactId") Long artifactId, HttpServletResponse response) {
        aiProtocolAdaptationTaskService.downloadTaskArtifact(artifactId, response);
    }

    /**
     * 预览任务产物文本内容。
     *
     * @param artifactId 产物 ID
     * @return 文本内容
     */
    @ApiOperation("预览任务产物文本内容")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:query')")
    @GetMapping("/artifact/{artifactId}/content")
    public AjaxResult artifactContent(@PathVariable("artifactId") Long artifactId) {
        return success(aiProtocolAdaptationTaskService.readTaskArtifactContent(artifactId));
    }

    /**
     * 查询任务生成记录列表。
     *
     * @param taskId 任务 ID
     * @return 生成记录列表
     */
    @ApiOperation("查询任务生成记录列表")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:query')")
    @GetMapping("/{taskId}/generationRecords")
    public AjaxResult generationRecords(@PathVariable("taskId") Long taskId) {
        return success(aiProtocolAdaptationTaskService.listTaskGenerationRecords(taskId));
    }

    /**
     * 查询协议代码生成记录详情。
     *
     * @param recordId 生成记录 ID
     * @return 生成记录详情
     */
    @ApiOperation("查询协议代码生成记录详情")
    @PreAuthorize("@ss.hasPermi('ai:protocol:adaptation:query')")
    @GetMapping("/generation/{recordId}")
    public AjaxResult generationRecord(@PathVariable("recordId") Long recordId) {
        return success(aiProtocolAdaptationTaskService.selectGenerationRecordVO(recordId));
    }
}
