package com.fastbee.ai.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.ai.model.dto.AiNl2SqlGenerateRequest;
import com.fastbee.ai.model.dto.AiNl2SqlQueryRequest;
import com.fastbee.ai.model.vo.AiModelRouteVO;
import com.fastbee.ai.service.AiModelRoutingService;
import com.fastbee.ai.service.IAiHybridQueryService;
import com.fastbee.ai.service.IAiNl2SqlGenerateService;
import com.fastbee.ai.service.IAiNl2SqlQueryService;
import com.fastbee.ai.service.IAiNl2SqlWorkflowService;
import com.fastbee.ai.service.IAiQueryPlanService;
import com.fastbee.ai.service.IAiRedisRealtimeQueryService;
import com.fastbee.ai.service.IAiTsdbQueryService;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 智能问数控制器。
 */
@RestController
@RequestMapping("/ai/nl2sql")
public class AiNl2SqlController extends BaseController {

    @Resource
    private IAiNl2SqlQueryService aiNl2SqlQueryService;

    @Resource
    private IAiNl2SqlGenerateService aiNl2SqlGenerateService;

    @Resource
    private IAiNl2SqlWorkflowService aiNl2SqlWorkflowService;

    @Resource
    private AiModelRoutingService aiModelRoutingService;

    @Resource
    private IAiQueryPlanService aiQueryPlanService;

    @Resource
    private IAiRedisRealtimeQueryService aiRedisRealtimeQueryService;

    @Resource
    private IAiTsdbQueryService aiTsdbQueryService;

    @Resource
    private IAiHybridQueryService aiHybridQueryService;

    /**
     * 预览执行受控只读 SQL。
     *
     * @param request 查询请求
     * @return 查询结果
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:edit')")
    @PostMapping("/preview")
    public AjaxResult preview(@Valid @RequestBody AiNl2SqlQueryRequest request) {
        return AjaxResult.success(aiNl2SqlQueryService.executeReadOnlyQuery(request));
    }

    /**
     * 根据自然语言生成结构化 SQL 结果。
     *
     * @param request 生成请求
     * @return 生成结果
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:edit')")
    @PostMapping("/structured")
    public AjaxResult structured(@Valid @RequestBody AiNl2SqlGenerateRequest request) {
        AiModelRouteVO route = StringUtils.isNotBlank(request.getModelCode())
                ? aiModelRoutingService.resolveByModelCode(request.getModelCode())
                : aiModelRoutingService.resolveDefaultRoute();
        return AjaxResult.success(aiNl2SqlGenerateService.generateStructured(
                request.getQuestion(),
                route
        ));
    }

    /**
     * 预览多源问数执行计划。
     *
     * @param request 生成请求
     * @return 执行计划
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:edit')")
    @PostMapping("/plan")
    public AjaxResult plan(@Valid @RequestBody AiNl2SqlGenerateRequest request) {
        return AjaxResult.success(aiQueryPlanService.buildPlan(request.getQuestion()));
    }

    /**
     * 执行 Redis 实时值问数。
     *
     * @param request 生成请求
     * @return 实时值结果
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:edit')")
    @PostMapping("/realtime/current")
    public AjaxResult current(@Valid @RequestBody AiNl2SqlGenerateRequest request) {
        return AjaxResult.success(aiRedisRealtimeQueryService.queryCurrentValue(request.getQuestion()));
    }

    /**
     * 执行 TSDB 时序问数。
     *
     * @param request 生成请求
     * @return 时序问数结果
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:edit')")
    @PostMapping("/tsdb/query")
    public AjaxResult tsdb(@Valid @RequestBody AiNl2SqlGenerateRequest request) {
        return AjaxResult.success(aiTsdbQueryService.query(request.getQuestion()));
    }

    /**
     * 执行 Hybrid 多源问数。
     *
     * @param request 生成请求
     * @return 多源问数结果
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:edit')")
    @PostMapping("/hybrid/query")
    public AjaxResult hybrid(@Valid @RequestBody AiNl2SqlGenerateRequest request) {
        return AjaxResult.success(aiHybridQueryService.query(request.getQuestion()));
    }

    /**
     * 根据自然语言生成受控 SQL 并执行。
     *
     * @param request 生成请求
     * @return 生成结果
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:edit')")
    @PostMapping("/generate")
    public AjaxResult generate(@Valid @RequestBody AiNl2SqlGenerateRequest request) {
        AiModelRouteVO route = StringUtils.isNotBlank(request.getModelCode())
                ? aiModelRoutingService.resolveByModelCode(request.getModelCode())
                : aiModelRoutingService.resolveDefaultRoute();
        return AjaxResult.success(aiNl2SqlWorkflowService.generateAndExecute(
                request.getQuestion(),
                request.getRowLimit(),
                route
        ));
    }
}
