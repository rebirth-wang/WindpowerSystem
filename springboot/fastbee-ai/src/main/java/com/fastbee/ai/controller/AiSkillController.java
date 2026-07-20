package com.fastbee.ai.controller;

import jakarta.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.ai.skill.IAiSkillCatalogService;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.core.controller.BaseController;

/**
 * AI 技能目录控制器。
 */
@RestController
@RequestMapping("/ai/skill")
public class AiSkillController extends BaseController {

    @Resource
    private IAiSkillCatalogService aiSkillCatalogService;

    /**
     * 查询 AI 会话可用技能列表。
     *
     * @return 技能列表
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:list')")
    @GetMapping("/list")
    public AjaxResult list() {
        return AjaxResult.success(aiSkillCatalogService.listSkillDefinitions());
    }
}
