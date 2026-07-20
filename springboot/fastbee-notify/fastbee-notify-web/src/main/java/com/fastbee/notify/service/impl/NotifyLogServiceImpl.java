package com.fastbee.notify.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.notify.convert.NotifyLogConvert;
import com.fastbee.notify.domain.NotifyChannel;
import com.fastbee.notify.domain.NotifyLog;
import com.fastbee.notify.domain.NotifyTemplate;
import com.fastbee.notify.mapper.NotifyChannelMapper;
import com.fastbee.notify.mapper.NotifyLogMapper;
import com.fastbee.notify.mapper.NotifyTemplateMapper;
import com.fastbee.notify.service.INotifyLogService;
import com.fastbee.notify.vo.NotifyLogVO;

/**
 * 通知日志Service业务层处理
 *
 * @author fastbee
 * @date 2023-12-16
 */
@Service
public class NotifyLogServiceImpl extends ServiceImpl<NotifyLogMapper,NotifyLog> implements INotifyLogService {
    @Resource
    private NotifyLogMapper notifyLogMapper;
    @Resource
    private NotifyChannelMapper notifyChannelMapper;
    @Resource
    private NotifyTemplateMapper notifyTemplateMapper;

    /**
     * 查询通知日志
     *
     * @param notifyLog 通知日志
     * @return 通知日志
     */
    @Override
    @DataScope
    public NotifyLog selectNotifyLogById(NotifyLog notifyLog)
    {
        LambdaQueryWrapper<NotifyLog> queryWrapper = this.buildQueryWrapper(notifyLog);
        return notifyLogMapper.selectOne(queryWrapper);
    }

    /**
     * 查询通知日志分页列表
     *
     * @param notifyLog 通知日志
     * @return 通知日志
     */
    @Override
    @DataScope()
    public Page<NotifyLogVO> pageNotifyLogVO(NotifyLog notifyLog) {
        LambdaQueryWrapper<NotifyLog> lqw = buildQueryWrapper(notifyLog);
        lqw.orderByDesc(NotifyLog::getCreateTime);
        Page<NotifyLog> notifyLogPage = baseMapper.selectPage(new Page<>(notifyLog.getPageNum(), notifyLog.getPageSize()), lqw);
        Page<NotifyLogVO> notifyLogVoPage = NotifyLogConvert.INSTANCE.convertNotifyLogVOPage(notifyLogPage);
        List<NotifyLogVO> notifyLogs = notifyLogVoPage.getRecords();
        if (CollectionUtils.isEmpty(notifyLogs)) {
            return notifyLogVoPage;
        }
        List<Long> channelIdList = notifyLogs.stream().map(NotifyLogVO::getChannelId).collect(Collectors.toList());
        List<NotifyChannel> notifyChannelList = notifyChannelMapper.selectBatchIds(channelIdList);
        Map<Long, NotifyChannel> notifyChannelMap = notifyChannelList.stream().collect(Collectors.toMap(NotifyChannel::getId, Function.identity()));
        List<Long> templateIdList = notifyLogs.stream().map(NotifyLogVO::getNotifyTemplateId).collect(Collectors.toList());
        List<NotifyTemplate> notifyTemplateList = notifyTemplateMapper.selectBatchIds(templateIdList);
        Map<Long, NotifyTemplate> notifyTemplateMap = notifyTemplateList.stream().collect(Collectors.toMap(NotifyTemplate::getId, Function.identity()));
        for (NotifyLogVO log : notifyLogs) {
            if (notifyChannelMap.containsKey(log.getChannelId())) {
                log.setChannelName(notifyChannelMap.get(log.getChannelId()).getName());
            }
            if (notifyTemplateMap.containsKey(log.getNotifyTemplateId())) {
                log.setTemplateName(notifyTemplateMap.get(log.getNotifyTemplateId()).getName());
            }
        }
        return notifyLogVoPage;
    }

    private LambdaQueryWrapper<NotifyLog> buildQueryWrapper(NotifyLog query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<NotifyLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, NotifyLog::getId, query.getId());
        lqw.eq(query.getChannelId() != null, NotifyLog::getChannelId, query.getChannelId());
        lqw.eq(query.getNotifyTemplateId() != null, NotifyLog::getNotifyTemplateId, query.getNotifyTemplateId());
        lqw.eq(StringUtils.isNotBlank(query.getMsgContent()), NotifyLog::getMsgContent, query.getMsgContent());
        lqw.eq(StringUtils.isNotBlank(query.getSendAccount()), NotifyLog::getSendAccount, query.getSendAccount());
        lqw.eq(query.getSendStatus() != null, NotifyLog::getSendStatus, query.getSendStatus());
        lqw.eq(StringUtils.isNotBlank(query.getResultContent()), NotifyLog::getResultContent, query.getResultContent());
        lqw.eq(StringUtils.isNotBlank(query.getServiceCode()), NotifyLog::getServiceCode, query.getServiceCode());
        lqw.eq(query.getTenantId() != null, NotifyLog::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), NotifyLog::getTenantName, query.getTenantName());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(NotifyLog::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }

        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }

        return lqw;
    }

    /**
     * 新增通知日志
     *
     * @param notifyLog 通知日志
     * @return 结果
     */
    @Override
    public int insertNotifyLog(NotifyLog notifyLog)
    {
        notifyLog.setCreateTime(DateUtils.getNowDate());
        return notifyLogMapper.insert(notifyLog);
    }

    /**
     * 修改通知日志
     *
     * @param notifyLog 通知日志
     * @return 结果
     */
    @Override
    public int updateNotifyLog(NotifyLog notifyLog)
    {
        notifyLog.setUpdateTime(DateUtils.getNowDate());
        return notifyLogMapper.updateById(notifyLog);
    }

    /**
     * 批量删除通知日志
     *
     * @param ids 需要删除的通知日志主键
     * @return 结果
     */
    @Override
    public int deleteNotifyLogByIds(Long[] ids)
    {
        return notifyLogMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除通知日志信息
     *
     * @param id 通知日志主键
     * @return 结果
     */
    @Override
    public int deleteNotifyLogById(Long id)
    {
        return notifyLogMapper.deleteById(id);
    }
}
