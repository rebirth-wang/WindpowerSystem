package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiSecuritySupport.getLoginUser;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.ai.convert.AiChatSessionConvert;
import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.domain.AiChatSession;
import com.fastbee.ai.mapper.AiChatMessageMapper;
import com.fastbee.ai.mapper.AiChatSessionMapper;
import com.fastbee.ai.model.enums.AiChatMode;
import com.fastbee.ai.model.vo.AiChatRecordDetailVO;
import com.fastbee.ai.model.vo.AiChatSessionVO;
import com.fastbee.ai.service.IAiChatMessageService;
import com.fastbee.ai.service.IAiChatSessionService;
import com.fastbee.ai.support.AiSecuritySupport;
import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 会话服务实现。
 */
@Service
public class AiChatSessionServiceImpl extends ServiceImpl<AiChatSessionMapper, AiChatSession> implements IAiChatSessionService {

    private static final String MODE_POLICY_AUTO = "AUTO";
    private static final String MODE_POLICY_PINNED = "PINNED";
    private static final String ADMIN_USERNAME = "admin";

    @Resource
    private IAiChatMessageService aiChatMessageService;

    @Resource
    private AiChatMessageMapper aiChatMessageMapper;

    @Override
    public Page<AiChatSession> pageCurrentUserSession(AiChatSession aiChatSession) {
        LambdaQueryWrapper<AiChatSession> lqw = buildQueryWrapper(aiChatSession);
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            lqw.eq(AiChatSession::getUserId, loginUser.getUserId());
        }
        lqw.orderByDesc(AiChatSession::getLastMessageTime).orderByDesc(AiChatSession::getCreateTime);
        return baseMapper.selectPage(new Page<>(aiChatSession.getPageNum(), aiChatSession.getPageSize()), lqw);
    }

    @Override
    public Page<AiChatSessionVO> pageCurrentUserSessionVO(AiChatSession aiChatSession) {
        Page<AiChatSession> sessionPage = pageCurrentUserSession(aiChatSession);
        Page<AiChatSessionVO> sessionVOPage = AiChatSessionConvert.INSTANCE.convertAiChatSessionVOPage(sessionPage);
        if (sessionVOPage != null && sessionVOPage.getRecords() != null) {
            fillSessionStrategyVO(sessionVOPage.getRecords(), sessionPage.getRecords());
            fillMessageCountVO(sessionVOPage.getRecords());
        }
        return sessionVOPage;
    }

    @Override
    @DataScope
    public Page<AiChatSession> pageChatRecord(AiChatSession aiChatSession) {
        LambdaQueryWrapper<AiChatSession> lqw = buildQueryWrapper(aiChatSession);
        excludeAdminSessionForNonAdmin(lqw);
        lqw.orderByDesc(AiChatSession::getLastMessageTime).orderByDesc(AiChatSession::getCreateTime);
        return baseMapper.selectPage(new Page<>(aiChatSession.getPageNum(), aiChatSession.getPageSize()), lqw);
    }

    @Override
    @DataScope
    public Page<AiChatSessionVO> pageChatRecordVO(AiChatSession aiChatSession) {
        Page<AiChatSession> sessionPage = pageChatRecord(aiChatSession);
        Page<AiChatSessionVO> sessionVOPage = AiChatSessionConvert.INSTANCE.convertAiChatSessionVOPage(sessionPage);
        if (sessionVOPage != null && sessionVOPage.getRecords() != null) {
            fillSessionStrategyVO(sessionVOPage.getRecords(), sessionPage.getRecords());
            fillMessageCountVO(sessionVOPage.getRecords());
        }
        return sessionVOPage;
    }

    @Override
    @DataScope
    public AiChatSession selectAiChatSession(AiChatSession aiChatSession) {
        return this.getOne(buildQueryWrapper(aiChatSession));
    }

    @Override
    @DataScope
    public AiChatSessionVO selectAiChatSessionVO(AiChatSession aiChatSession) {
        AiChatSession session = selectAiChatSession(aiChatSession);
        if (session == null) {
            return null;
        }
        AiChatSessionVO sessionVO = AiChatSessionConvert.INSTANCE.convertAiChatSessionVO(session);
        fillSessionStrategyVO(sessionVO, session);
        fillMessageCountVO(Arrays.asList(sessionVO));
        return sessionVO;
    }

    @Override
    public AiChatRecordDetailVO selectChatRecordDetail(Long sessionId) {
        AiChatSession query = new AiChatSession();
        query.setSessionId(sessionId);
        AiChatSessionVO sessionVO = this.selectAiChatSessionVO(query);
        if (sessionVO == null) {
            return null;
        }
        AiChatMessage messageQuery = new AiChatMessage();
        messageQuery.setSessionId(sessionId);
        AiChatRecordDetailVO detailVO = new AiChatRecordDetailVO();
        detailVO.setSession(sessionVO);
        detailVO.setMessages(aiChatMessageService.listAiChatMessage(messageQuery));
        return detailVO;
    }

    @Override
    public int deleteAiChatSessionByIds(Long[] sessionIds) {
        aiChatMessageMapper.delete(Wrappers.<AiChatMessage>lambdaQuery().in(AiChatMessage::getSessionId, Arrays.asList(sessionIds)));
        return baseMapper.deleteBatchIds(Arrays.asList(sessionIds));
    }

    @Override
    public int updateAiChatSessionArchived(Long sessionId, String isArchived) {
        AiChatSession update = new AiChatSession();
        update.setSessionId(sessionId);
        update.setIsArchived(isArchived);
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(new Date());
        return baseMapper.updateById(update);
    }

    @Override
    public int updateAiChatSessionTitle(Long sessionId, String sessionTitle) {
        AiChatSession update = new AiChatSession();
        update.setSessionId(sessionId);
        update.setSessionTitle(sessionTitle);
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(new Date());
        return baseMapper.updateById(update);
    }

    private LambdaQueryWrapper<AiChatSession> buildQueryWrapper(AiChatSession query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<AiChatSession> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getSessionId() != null, AiChatSession::getSessionId, query.getSessionId());
        lqw.like(StringUtils.isNotBlank(query.getSessionCode()), AiChatSession::getSessionCode, query.getSessionCode());
        lqw.like(StringUtils.isNotBlank(query.getSessionTitle()), AiChatSession::getSessionTitle, query.getSessionTitle());
        lqw.eq(query.getUserId() != null, AiChatSession::getUserId, query.getUserId());
        lqw.eq(query.getTenantId() != null, AiChatSession::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), AiChatSession::getTenantName, query.getTenantName());
        lqw.eq(StringUtils.isNotBlank(query.getProviderCode()), AiChatSession::getProviderCode, query.getProviderCode());
        lqw.eq(StringUtils.isNotBlank(query.getModelCode()), AiChatSession::getModelCode, query.getModelCode());
        lqw.eq(StringUtils.isNotBlank(query.getChatMode()), AiChatSession::getChatMode, query.getChatMode());
        lqw.eq(StringUtils.isNotBlank(query.getModePolicy()), AiChatSession::getModePolicy, query.getModePolicy());
        lqw.eq(StringUtils.isNotBlank(query.getPinnedMode()), AiChatSession::getPinnedMode, query.getPinnedMode());
        lqw.eq(StringUtils.isNotBlank(query.getLastEffectiveMode()), AiChatSession::getLastEffectiveMode, query.getLastEffectiveMode());
        lqw.eq(StringUtils.isNotBlank(query.getIsArchived()), AiChatSession::getIsArchived, query.getIsArchived());
        lqw.eq(StringUtils.isNotBlank(query.getStatus()), AiChatSession::getStatus, query.getStatus());

        if (Objects.nonNull(params.get("beginTime")) && Objects.nonNull(params.get("endTime"))) {
            lqw.between(AiChatSession::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))) {
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        return lqw;
    }

    private void excludeAdminSessionForNonAdmin(LambdaQueryWrapper<AiChatSession> lqw) {
        SysUser user = getLoginUser().getUser();
        if (!user.isAdmin()) {
            lqw.ne(AiChatSession::getCreateBy, ADMIN_USERNAME);
        }
    }

    private Map<Long, Integer> buildMessageCountMap(Set<Long> sessionIds) {
        if (sessionIds == null || sessionIds.isEmpty()) {
            return Map.of();
        }
        return aiChatMessageMapper.selectList(Wrappers.<AiChatMessage>lambdaQuery()
                        .select(AiChatMessage::getSessionId, AiChatMessage::getMessageId)
                        .in(AiChatMessage::getSessionId, sessionIds))
                .stream()
                .collect(Collectors.groupingBy(AiChatMessage::getSessionId, Collectors.summingInt(item -> 1)));
    }

    private void fillMessageCountVO(List<AiChatSessionVO> sessionVOList) {
        if (sessionVOList == null || sessionVOList.isEmpty()) {
            return;
        }
        Set<Long> sessionIds = sessionVOList.stream()
                .filter(Objects::nonNull)
                .map(AiChatSessionVO::getSessionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, Integer> countMap = buildMessageCountMap(sessionIds);
        for (AiChatSessionVO sessionVO : sessionVOList) {
            if (sessionVO != null) {
                sessionVO.setMessageCount(countMap.getOrDefault(sessionVO.getSessionId(), 0));
            }
        }
    }

    private void fillSessionStrategyVO(List<AiChatSessionVO> sessionVOList, List<AiChatSession> sessionList) {
        if (sessionVOList == null || sessionVOList.isEmpty() || sessionList == null || sessionList.isEmpty()) {
            return;
        }
        int size = Math.min(sessionVOList.size(), sessionList.size());
        for (int index = 0; index < size; index++) {
            fillSessionStrategyVO(sessionVOList.get(index), sessionList.get(index));
        }
    }

    private void fillSessionStrategyVO(AiChatSessionVO sessionVO, AiChatSession session) {
        if (sessionVO == null || session == null) {
            return;
        }
        String modePolicy = resolveSessionModePolicy(session);
        String pinnedMode = resolveSessionPinnedMode(session);
        sessionVO.setModePolicy(modePolicy);
        sessionVO.setPinnedMode(pinnedMode);
        sessionVO.setChatMode(resolveSessionChatMode(modePolicy, pinnedMode));
        sessionVO.setLastEffectiveMode(resolveSessionLastEffectiveMode(session));
    }

    private String resolveSessionModePolicy(AiChatSession session) {
        if (session == null) {
            return MODE_POLICY_AUTO;
        }
        String explicitPolicy = normalizeModePolicy(session.getModePolicy());
        if (StringUtils.isNotBlank(explicitPolicy)) {
            return explicitPolicy;
        }
        return AiChatMode.AUTO.name().equals(normalizeChatMode(session.getChatMode())) ? MODE_POLICY_AUTO : MODE_POLICY_PINNED;
    }

    private String resolveSessionPinnedMode(AiChatSession session) {
        if (session == null) {
            return null;
        }
        String explicitPinnedMode = normalizeChatMode(session.getPinnedMode());
        if (StringUtils.isNotBlank(explicitPinnedMode) && !AiChatMode.AUTO.name().equals(explicitPinnedMode)) {
            return explicitPinnedMode;
        }
        String chatMode = normalizeChatMode(session.getChatMode());
        return AiChatMode.AUTO.name().equals(chatMode) ? null : chatMode;
    }

    private String resolveSessionChatMode(String modePolicy, String pinnedMode) {
        return MODE_POLICY_PINNED.equals(modePolicy) && StringUtils.isNotBlank(pinnedMode)
                ? pinnedMode
                : AiChatMode.AUTO.name();
    }

    private String resolveSessionLastEffectiveMode(AiChatSession session) {
        if (session == null) {
            return null;
        }
        String normalizedMode = normalizeChatMode(session.getLastEffectiveMode());
        return AiChatMode.AUTO.name().equals(normalizedMode) ? null : normalizedMode;
    }

    private String normalizeModePolicy(String modePolicy) {
        if (StringUtils.isBlank(modePolicy)) {
            return null;
        }
        String normalized = modePolicy.trim().toUpperCase();
        return MODE_POLICY_PINNED.equals(normalized) ? MODE_POLICY_PINNED : MODE_POLICY_AUTO.equals(normalized) ? MODE_POLICY_AUTO : null;
    }

    private String normalizeChatMode(String chatMode) {
        if (StringUtils.isBlank(chatMode)) {
            return null;
        }
        String normalized = chatMode.trim().toUpperCase();
        if ("GENERAL_CHAT".equals(normalized)) {
            return AiChatMode.GENERAL.name();
        }
        for (AiChatMode item : AiChatMode.values()) {
            if (item.name().equals(normalized)) {
                return normalized;
            }
        }
        return null;
    }
}
