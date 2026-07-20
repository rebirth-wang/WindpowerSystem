package com.fastbee.ai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.ai.domain.AiChatSession;
import com.fastbee.ai.model.vo.AiChatRecordDetailVO;
import com.fastbee.ai.model.vo.AiChatSessionVO;

/**
 * AI 会话服务接口。
 */
public interface IAiChatSessionService extends IService<AiChatSession> {

    /**
     * 查询当前用户会话列表。
     *
     * @param aiChatSession 查询条件
     * @return 会话分页
     */
    Page<AiChatSession> pageCurrentUserSession(AiChatSession aiChatSession);

    /**
     * 查询当前用户会话展示列表。
     *
     * @param aiChatSession 查询条件
     * @return 会话展示分页
     */
    Page<AiChatSessionVO> pageCurrentUserSessionVO(AiChatSession aiChatSession);

    /**
     * 查询会话记录列表。
     *
     * @param aiChatSession 查询条件
     * @return 会话分页
     */
    Page<AiChatSession> pageChatRecord(AiChatSession aiChatSession);

    /**
     * 查询会话记录展示列表。
     *
     * @param aiChatSession 查询条件
     * @return 会话展示分页
     */
    Page<AiChatSessionVO> pageChatRecordVO(AiChatSession aiChatSession);

    /**
     * 查询会话详情。
     *
     * @param aiChatSession 查询条件
     * @return 会话信息
     */
    AiChatSession selectAiChatSession(AiChatSession aiChatSession);

    /**
     * 查询会话展示详情。
     *
     * @param aiChatSession 查询条件
     * @return 会话展示信息
     */
    AiChatSessionVO selectAiChatSessionVO(AiChatSession aiChatSession);

    /**
     * 查询会话记录详情。
     *
     * @param sessionId 会话 ID
     * @return 会话详情
     */
    AiChatRecordDetailVO selectChatRecordDetail(Long sessionId);

    /**
     * 删除会话及消息。
     *
     * @param sessionIds 会话 ID 集合
     * @return 影响行数
     */
    int deleteAiChatSessionByIds(Long[] sessionIds);

    /**
     * 更新会话归档状态。
     *
     * @param sessionId   会话 ID
     * @param isArchived 是否归档
     * @return 影响行数
     */
    int updateAiChatSessionArchived(Long sessionId, String isArchived);

    /**
     * 更新会话标题。
     *
     * @param sessionId     会话 ID
     * @param sessionTitle 会话标题
     * @return 影响行数
     */
    int updateAiChatSessionTitle(Long sessionId, String sessionTitle);
}
