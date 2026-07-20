package com.fastbee.iot.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.common.mybatis.LambdaQueryWrapperX;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.EventLog;
import com.fastbee.iot.mapper.EventLogMapper;
import com.fastbee.iot.model.HistoryModel;
import com.fastbee.iot.model.vo.EventLogVO;
import com.fastbee.iot.service.IEventLogService;

/**
 * 事件日志Service业务层处理
 *
 * @author kerwincui
 * @date 2024-08-16
 */
@Service
public class EventLogServiceImpl extends ServiceImpl<EventLogMapper,EventLog> implements IEventLogService {

    @Resource
    private EventLogMapper eventLogMapper;


    /**
     * 查询事件日志列表
     *
     * @param eventLog 事件日志
     * @return 事件日志
     */
    @Override
    public Page<EventLog> selectEventLogList(EventLog eventLog) {
        LambdaQueryWrapperX<EventLog> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eq(StringUtils.isNotEmpty(eventLog.getSerialNumber()),EventLog::getSerialNumber, eventLog.getSerialNumber());
        wrapper.eq(StringUtils.isNotEmpty(eventLog.getIdentify()),EventLog::getIdentify, eventLog.getIdentify());
        wrapper.eq(!Objects.isNull(eventLog.getLogType()),EventLog::getLogType, eventLog.getLogType());
        if (eventLog.getParams()!= null && eventLog.getParams().get("beginTime")!= null && eventLog.getParams().get("endTime")!= null) {
            wrapper.between(EventLog::getCreateTime, eventLog.getParams().get("beginTime"), eventLog.getParams().get("endTime"));
        }
        wrapper.orderByDesc(EventLog::getCreateTime);
        return eventLogMapper.selectPage(new Page<>(eventLog.getPageNum(), eventLog.getPageSize()), wrapper);
    }

    /**
     * 根据设备编号删除
     * @param serialNumber
     */
    @Override
    public void deleteEventLogBySerialNumber(String serialNumber){
        LambdaQueryWrapper<EventLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EventLog::getSerialNumber,serialNumber);
        eventLogMapper.delete(wrapper);
    }

    @Override
    public List<HistoryModel> listHistory(EventLogVO eventLogVO) {
        return eventLogMapper.listHistory(eventLogVO);
    }

    private LambdaQueryWrapper<EventLog> buildQueryWrapper(EventLog query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<EventLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(query.getIdentify()), EventLog::getIdentify, query.getIdentify());
        lqw.like(StringUtils.isNotBlank(query.getModelName()), EventLog::getModelName, query.getModelName());
        lqw.eq(query.getLogType() != null, EventLog::getLogType, query.getLogType());
        lqw.eq(StringUtils.isNotBlank(query.getLogValue()), EventLog::getLogValue, query.getLogValue());
        lqw.eq(query.getDeviceId() != null, EventLog::getDeviceId, query.getDeviceId());
        lqw.like(StringUtils.isNotBlank(query.getDeviceName()), EventLog::getDeviceName, query.getDeviceName());
        lqw.eq(StringUtils.isNotBlank(query.getSerialNumber()), EventLog::getSerialNumber, query.getSerialNumber());
        lqw.eq(query.getIsMonitor() != null, EventLog::getIsMonitor, query.getIsMonitor());
        lqw.eq(query.getMode() != null, EventLog::getMode, query.getMode());
        lqw.eq(query.getUserId() != null, EventLog::getUserId, query.getUserId());
        lqw.like(StringUtils.isNotBlank(query.getUserName()), EventLog::getUserName, query.getUserName());
        lqw.eq(query.getTenantId() != null, EventLog::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), EventLog::getTenantName, query.getTenantName());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(EventLog::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(EventLog entity){
        //TODO 做一些数据校验,如唯一约束
    }
}
