package com.fastbee.ai.controller;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.ai.domain.AiChatSession;
import com.fastbee.ai.model.vo.AiChatRecordDetailVO;
import com.fastbee.ai.model.vo.AiChatSessionVO;
import com.fastbee.ai.service.IAiChatSessionService;
import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 会话记录控制器。
 */
@Api(tags = "AI 会话记录")
@RestController
@RequestMapping("/ai/chatRecord")
public class AiChatRecordController extends BaseController {

    @Resource
    private IAiChatSessionService aiChatSessionService;

    /**
     * 查询会话记录列表。
     *
     * @param aiChatSession 查询条件
     * @return 列表结果
     */
    @ApiOperation("查询会话记录列表")
    @PreAuthorize("@ss.hasPermi('ai:chatRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiChatSession aiChatSession) {
        Page<AiChatSessionVO> page = aiChatSessionService.pageChatRecordVO(aiChatSession);
        return getDataTable(page.getRecords(), page.getTotal());
    }

    /**
     * 查询会话记录详情。
     *
     * @param sessionId 会话 ID
     * @return 详情
     */
    @ApiOperation("查询会话记录详情")
    @PreAuthorize("@ss.hasPermi('ai:chatRecord:query')")
    @GetMapping("/{sessionId}")
    public AjaxResult getInfo(@PathVariable("sessionId") Long sessionId) {
        AiChatRecordDetailVO detailVO = aiChatSessionService.selectChatRecordDetail(sessionId);
        return success(detailVO);
    }

    /**
     * 归档会话记录。
     *
     * @param sessionId 会话 ID
     * @return 操作结果
     */
    @ApiOperation("归档会话记录")
    @PreAuthorize("@ss.hasPermi('ai:chatRecord:remove')")
    @Log(title = "AI 会话记录", businessType = BusinessType.UPDATE)
    @PutMapping("/{sessionId}/archive")
    public AjaxResult archive(@PathVariable("sessionId") Long sessionId) {
        AiChatSession session = aiChatSessionService.getById(sessionId);
        if (session == null) {
            return AjaxResult.error(message("ai.chat.session.not.exists.or.deleted"));
        }
        SecurityUtils.checkUserOperatePermission(session.getTenantId(), session.getCreateBy());
        return toAjax(aiChatSessionService.updateAiChatSessionArchived(sessionId, "1"));
    }

    /**
     * 取消归档会话记录。
     *
     * @param sessionId 会话 ID
     * @return 操作结果
     */
    @ApiOperation("取消归档会话记录")
    @PreAuthorize("@ss.hasPermi('ai:chatRecord:remove')")
    @Log(title = "AI 会话记录", businessType = BusinessType.UPDATE)
    @PutMapping("/{sessionId}/unarchive")
    public AjaxResult unarchive(@PathVariable("sessionId") Long sessionId) {
        AiChatSession session = aiChatSessionService.getById(sessionId);
        if (session == null) {
            return AjaxResult.error(message("ai.chat.session.not.exists.or.deleted"));
        }
        SecurityUtils.checkUserOperatePermission(session.getTenantId(), session.getCreateBy());
        return toAjax(aiChatSessionService.updateAiChatSessionArchived(sessionId, "0"));
    }

    /**
     * 重命名会话记录。
     *
     * @param sessionId 会话 ID
     * @param request   会话标题
     * @return 操作结果
     */
    @ApiOperation("重命名会话记录")
    @PreAuthorize("@ss.hasPermi('ai:chatRecord:remove')")
    @Log(title = "AI 会话记录", businessType = BusinessType.UPDATE)
    @PutMapping("/{sessionId}/rename")
    public AjaxResult rename(@PathVariable("sessionId") Long sessionId, @RequestBody AiChatSession request) {
        AiChatSession session = aiChatSessionService.getById(sessionId);
        if (session == null) {
            return AjaxResult.error(message("ai.chat.session.not.exists.or.deleted"));
        }
        SecurityUtils.checkUserOperatePermission(session.getTenantId(), session.getCreateBy());
        String sessionTitle = request == null || request.getSessionTitle() == null ? "" : request.getSessionTitle().trim();
        if (sessionTitle.length() > 255) {
            sessionTitle = sessionTitle.substring(0, 255);
        }
        if (StringUtils.isBlank(sessionTitle)) {
            return AjaxResult.error(message("ai.chat.session.title.required"));
        }
        return toAjax(aiChatSessionService.updateAiChatSessionTitle(sessionId, sessionTitle));
    }

    /**
     * 删除会话记录。
     *
     * @param sessionIds 会话 ID 集合
     * @return 操作结果
     */
    @ApiOperation("删除会话记录")
    @PreAuthorize("@ss.hasPermi('ai:chatRecord:remove')")
    @Log(title = "AI 会话记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{sessionIds}")
    public AjaxResult remove(@PathVariable Long[] sessionIds) {
        List<AiChatSession> sessionList = aiChatSessionService.listByIds(Arrays.asList(sessionIds));
        for (AiChatSession session : sessionList) {
            SecurityUtils.checkUserOperatePermission(session.getTenantId(), session.getCreateBy());
        }
        return toAjax(aiChatSessionService.deleteAiChatSessionByIds(sessionIds));
    }
}
