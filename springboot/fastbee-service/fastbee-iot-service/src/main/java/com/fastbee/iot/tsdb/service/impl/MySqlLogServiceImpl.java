package com.fastbee.iot.tsdb.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.fastbee.common.mybatis.LambdaQueryWrapperX;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceLog;
import com.fastbee.iot.domain.EventLog;
import com.fastbee.iot.mapper.DeviceLogMapper;
import com.fastbee.iot.mapper.EventLogMapper;
import com.fastbee.iot.model.DeviceStatistic;
import com.fastbee.iot.model.HistoryBo;
import com.fastbee.iot.model.HistoryModel;
import com.fastbee.iot.model.MonitorModel;
import com.fastbee.iot.model.param.DataCenterParam;
import com.fastbee.iot.model.vo.ThingsModelLogCountVO;
import com.fastbee.iot.tsdb.model.TdLogDto;
import com.fastbee.iot.tsdb.service.ILogService;

/**
 * 类名: MySqlLogServiceImpl
 * 描述: MySQL存储日志实现类
 * 时间: 2022/5/22,0022 13:37
 * 开发人: admin
 */
@Slf4j
@DS("master")
@ConditionalOnProperty(name = "spring.datasource.dynamic.datasource.taos.enabled", havingValue = "false")
@Service("MySql")
public class MySqlLogServiceImpl implements ILogService {
    @Resource
    private DeviceLogMapper deviceLogMapper;
    @Resource
    private EventLogMapper eventLogMapper;


    @Override
    public int createSTable(String database) {
        return 0;
    }

    /***
     * 新增设备日志
     * @return
     */
    @Override
    public int saveDeviceLog(DeviceLog deviceLog) {
        if (deviceLog.getLogType() == 3 || deviceLog.getLogType() == 5 || deviceLog.getLogType() == 6 || deviceLog.getLogType() == 8) {
            EventLog event = new EventLog();
            event.setDeviceId(deviceLog.getDeviceId());
            event.setDeviceName(deviceLog.getDeviceName());
            event.setSerialNumber(deviceLog.getSerialNumber());
            byte isMonitor = 0;
            event.setIsMonitor(isMonitor);
            event.setUserId(deviceLog.getTenantId());
            event.setUserName(deviceLog.getTenantName());
            event.setTenantId(deviceLog.getTenantId());
            event.setTenantName(deviceLog.getTenantName());
            event.setCreateTime(DateUtils.getNowDate());
            event.setCreateBy(deviceLog.getCreateBy());
            // 日志模式 1=影子模式，2=在线模式，3=其他
            byte mode = 3;
            event.setMode(mode);
            event.setLogValue(deviceLog.getLogValue());
            event.setRemark(deviceLog.getRemark());
            event.setIdentify(deviceLog.getIdentify());
            event.setLogType(deviceLog.getLogType());
            return eventLogMapper.insert(event);
        } else {
            return deviceLogMapper.insertDeviceLog(deviceLog);
        }
    }

    @Override
    public int saveBatch(TdLogDto dto) {
        int ret = 0;
        for (DeviceLog deviceLog : dto.getList()) {
            ret += this.saveDeviceLog(deviceLog);
        }
        return ret;
    }

    /***
     * 根据设备ID删除设备日志
     * @return
     */
    @Override
    public int deleteDeviceLogByDeviceNumber(String deviceNumber) {
        LambdaQueryWrapper<EventLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EventLog::getSerialNumber,deviceNumber);
        eventLogMapper.delete(wrapper);
        return deviceLogMapper.deleteDeviceLogByDeviceNumber(deviceNumber);
    }

    /***
     * 设备属性、功能、事件和监测数据总数
     * @return
     */
    @Override
    public DeviceStatistic selectCategoryLogCount(Device device) {
        DeviceStatistic statistic = deviceLogMapper.selectDeviceLogCount(device);
        statistic.setEventCount(eventLogMapper.selectEventLogCount(device));
//        statistic.setFunctionCount(functionLogMapper.selectFunctionLogCount(device.getTenantId()));
        return statistic;
    }

    /***
     * 日志列表
     * @return
     */
    @Override
    public Page<DeviceLog> selectDeviceLogList(DeviceLog deviceLog) {
        if (deviceLog.getLogType() != null && (deviceLog.getLogType() == 3 || deviceLog.getLogType() == 5 || deviceLog.getLogType() == 6)) {
                EventLog event = new EventLog();
                byte isMonitor = 0;
                byte mode = 3;
                event.setDeviceId(deviceLog.getDeviceId());
                event.setDeviceName(deviceLog.getDeviceName());
                event.setSerialNumber(deviceLog.getSerialNumber());
                event.setIsMonitor(isMonitor);
                event.setUserId(deviceLog.getTenantId());
                event.setUserName(deviceLog.getTenantName());
                event.setCreateTime(DateUtils.getNowDate());
                // 日志模式 1=影子模式，2=在线模式，3=其他
                event.setMode(mode);
                event.setLogValue(deviceLog.getLogValue());
                event.setRemark(deviceLog.getRemark());
                event.setIdentify(deviceLog.getIdentify());
                event.setLogType(deviceLog.getLogType());
                LambdaQueryWrapperX<EventLog> wrapper = new LambdaQueryWrapperX<>();
                wrapper.eq(StringUtils.isNotEmpty(event.getSerialNumber()),EventLog::getSerialNumber, event.getSerialNumber());
                wrapper.eq(StringUtils.isNotEmpty(event.getIdentify()),EventLog::getIdentify, event.getIdentify());
                wrapper.eq(!Objects.isNull(event.getLogType()),EventLog::getLogType, event.getLogType());
                if (event.getParams()!= null && event.getParams().get("beginTime")!= null && event.getParams().get("endTime")!= null) {
                    wrapper.between(EventLog::getCreateTime, event.getParams().get("beginTime"), event.getParams().get("endTime"));
                }
                wrapper.orderByDesc(EventLog::getCreateTime);
                Page<EventLog> ret = eventLogMapper.selectPage(new Page<>(event.getPageNum(), event.getPageSize()), wrapper);
                return eventtoDevice(ret);

        } else {
            return deviceLogMapper.selectDeviceLogList(new Page<>(deviceLog.getPageNum(), deviceLog.getPageSize()), deviceLog);
        }
    }

    @Override
    public Page<DeviceLog> selectEventLogList(DeviceLog deviceLog) {
        EventLog event = new EventLog();
        event.setDeviceId(deviceLog.getDeviceId());
        event.setDeviceName(deviceLog.getDeviceName());
        event.setSerialNumber(deviceLog.getSerialNumber());
        byte isMonitor = 0;
        event.setIsMonitor(isMonitor);
        event.setUserId(deviceLog.getTenantId());
        event.setUserName(deviceLog.getTenantName());
        event.setCreateTime(DateUtils.getNowDate());
        // 日志模式 1=影子模式，2=在线模式，3=其他
        byte mode = 3;
        event.setMode(mode);
        event.setLogValue(deviceLog.getLogValue());
        event.setRemark(deviceLog.getRemark());
        event.setIdentify(deviceLog.getIdentify());
        event.setLogType(deviceLog.getLogType());
        LambdaQueryWrapperX<EventLog> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eq(StringUtils.isNotEmpty(event.getSerialNumber()),EventLog::getSerialNumber, event.getSerialNumber());
        wrapper.eq(StringUtils.isNotEmpty(event.getIdentify()),EventLog::getIdentify, event.getIdentify());
        wrapper.eq(!Objects.isNull(event.getLogType()),EventLog::getLogType, event.getLogType());
        if (event.getParams()!= null && event.getParams().get("beginTime")!= null && event.getParams().get("endTime")!= null) {
            wrapper.between(EventLog::getCreateTime, event.getParams().get("beginTime"), event.getParams().get("endTime"));
        }
        wrapper.orderByDesc(EventLog::getCreateTime);
        Page<EventLog> ret = eventLogMapper.selectPage(new Page<>(event.getPageNum(), event.getPageSize()), wrapper);
        return eventtoDevice(ret);
    }

    public Page<DeviceLog> eventtoDevice(Page<EventLog> deviceLog) {
        Page<DeviceLog> page = new Page<>();
        List<DeviceLog> deviceLogs = deviceLog.getRecords().stream()
                .map(eventLog -> {
                    DeviceLog log = new DeviceLog();
                    // 这里需要根据 EventLog 和 DeviceLog 的属性进行映射
                    log.setLogId(eventLog.getLogId());
                    log.setDeviceId(eventLog.getDeviceId());
                    log.setCreateTime(eventLog.getCreateTime());
                    log.setLogType(eventLog.getLogType());
                    log.setLogValue(eventLog.getLogValue());
                    log.setIdentify(eventLog.getIdentify());
                    log.setRemark(eventLog.getRemark());
                    log.setSerialNumber(eventLog.getSerialNumber());
                    log.setMode(eventLog.getMode());
                    return log;
                })
                .collect(Collectors.toList());
        page.setRecords(deviceLogs);
        page.setTotal(deviceLog.getTotal());
        page.setSize(deviceLog.getSize());
        page.setCurrent(deviceLog.getCurrent());
        return page;
    }
    /***
     * 监测数据列表
     * @return
     */
    @Override
    public List<MonitorModel> selectMonitorList(DeviceLog deviceLog) {
        LambdaQueryWrapperX<DeviceLog> wrapper = new LambdaQueryWrapperX<>();
        Date beginTime = null;
        Date endTime = null;
        if (deviceLog.getBeginTime() != null && deviceLog.getEndTime() != null) {
            beginTime = parseTime(deviceLog.getBeginTime());
            endTime = parseTime(deviceLog.getEndTime());
        }
        wrapper.select(DeviceLog::getLogValue, DeviceLog::getCreateTime)
                .eq(DeviceLog::getIsMonitor, 1)
                .eq(deviceLog.getIdentify() != null && !deviceLog.getIdentify().isEmpty(),
                        DeviceLog::getIdentify, deviceLog.getIdentify())
                .eq(deviceLog.getDeviceId() != null && deviceLog.getDeviceId() != 0,
                        DeviceLog::getDeviceId, deviceLog.getDeviceId())
                .eq(deviceLog.getSerialNumber() != null && !deviceLog.getSerialNumber().isEmpty(),
                        DeviceLog::getSerialNumber, deviceLog.getSerialNumber())
                .between(deviceLog.getBeginTime() != null && deviceLog.getEndTime() != null,
                        DeviceLog::getCreateTime, beginTime, endTime)
                .orderByDesc(DeviceLog::getCreateTime)
                .last("limit " + deviceLog.getTotal());

        return deviceLogMapper.selectList(wrapper).stream()
                .map(log -> new MonitorModel(log.getLogValue(), log.getCreateTime()))
                .collect(Collectors.toList());
    }

    /**
     * 查询历史数据  is_Montor=1 或 is_history=1
     */
    @Override
    public Map<String, List<HistoryModel>> selectHistoryList(DeviceLog deviceLog) {
        LambdaQueryWrapperX<DeviceLog> wrapper = new LambdaQueryWrapperX<>();
        wrapper.select(DeviceLog::getLogValue,
                DeviceLog::getCreateTime,
                DeviceLog::getIdentify);
        Date beginTime = null;
        Date endTime = null;
        if (deviceLog.getBeginTime() != null && deviceLog.getEndTime() != null) {
            beginTime = parseTime(deviceLog.getBeginTime());
            endTime = parseTime(deviceLog.getEndTime());
        }
        wrapper.eq(StringUtils.isNotBlank(deviceLog.getSerialNumber()),
                        DeviceLog::getSerialNumber,
                        deviceLog.getSerialNumber())
                .between(deviceLog.getBeginTime() != null && deviceLog.getEndTime() != null,
                        DeviceLog::getCreateTime,
                        beginTime,
                        endTime)
                .orderByDesc(DeviceLog::getCreateTime);

        // 执行查询并转换结果
        List<HistoryModel> historyList = deviceLogMapper.selectList(wrapper)
                .stream()
                .map(log -> new HistoryModel(
                        log.getCreateTime(),
                        log.getLogValue(),
                        log.getIdentify(),
                        null, null, null, null))
                .collect(Collectors.toList());

        return historyList.stream()
                .collect(Collectors.groupingBy(HistoryModel::getIdentify));
    }

    private Date parseTime(String time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.parse(time);
        } catch (ParseException e) {
            throw new IllegalArgumentException("时间格式错误: " + time, e);
        }
    }

    @Override
    public List<HistoryModel> listHistory(DeviceLog deviceLog) {
        LambdaQueryWrapperX<DeviceLog> wrapper = new LambdaQueryWrapperX<>();
        Date beginTime = null;
        Date endTime = null;
        if (deviceLog.getBeginTime() != null && deviceLog.getEndTime() != null) {
            beginTime = parseTime(deviceLog.getBeginTime());
            endTime = parseTime(deviceLog.getEndTime());
        }
        wrapper.select(DeviceLog::getLogValue,
                        DeviceLog::getCreateTime,
                        DeviceLog::getIdentify)
                .eq(StringUtils.isNotBlank(deviceLog.getSerialNumber()),
                        DeviceLog::getSerialNumber,
                        deviceLog.getSerialNumber())
                .between(deviceLog.getBeginTime() != null && deviceLog.getEndTime() != null,
                        DeviceLog::getCreateTime,
                        beginTime,
                        endTime)
                .in(CollectionUtils.isNotEmpty(deviceLog.getIdentityList()),
                        DeviceLog::getIdentify,
                        deviceLog.getIdentityList())
                .eq(deviceLog.getLogType() != null,
                        DeviceLog::getLogType,
                        deviceLog.getLogType())
                .orderByDesc(DeviceLog::getCreateTime);

        return deviceLogMapper.selectList(wrapper).stream()
                .map(log -> new HistoryModel(
                        log.getCreateTime(),
                        log.getLogValue(),
                        log.getIdentify(),
                        null, null, null, null))
                .collect(Collectors.toList());
    }

    @Override
    public Page<HistoryModel> listhistoryGroupByCreateTime(DeviceLog deviceLog) {
        Date beginTime = null;
        Date endTime = null;
        if (deviceLog.getBeginTime() != null && deviceLog.getEndTime() != null) {
            beginTime = parseTime(deviceLog.getBeginTime());
            endTime = parseTime(deviceLog.getEndTime());
        }
        return deviceLogMapper.listhistoryGroupByCreateTime(new Page<>(deviceLog.getPageNum(), deviceLog.getPageSize()), deviceLog, beginTime, endTime);
    }

    /**
     * 场景统计变量历史值
     *
     * @param deviceLog 实体类
     * @return java.lang.String
     */
    @Override
    public List<String> selectStatsValue(DeviceLog deviceLog) {
        LambdaQueryWrapperX<DeviceLog> wrapper = new LambdaQueryWrapperX<>();
        Date beginTime = null;
        Date endTime = null;
        if (deviceLog.getBeginTime() != null && deviceLog.getEndTime() != null) {
            beginTime = parseTime(deviceLog.getBeginTime());
            endTime = parseTime(deviceLog.getEndTime());
        }

        wrapper.select(DeviceLog::getLogValue)
                .eq(deviceLog.getLogType() != null,
                        DeviceLog::getLogType,
                        deviceLog.getLogType())
                .between(deviceLog.getBeginTime() != null && deviceLog.getEndTime() != null,
                        DeviceLog::getCreateTime,
                        beginTime,
                        endTime)
                .eq(StringUtils.isNotBlank(deviceLog.getIdentify()),
                        DeviceLog::getIdentify,
                        deviceLog.getIdentify())
                .eq(StringUtils.isNotBlank(deviceLog.getSerialNumber()),
                        DeviceLog::getSerialNumber,
                        deviceLog.getSerialNumber());

        return deviceLogMapper.selectList(wrapper)
                .stream()
                .map(DeviceLog::getLogValue)
                .collect(Collectors.toList());
    }

    @Override
    public List<ThingsModelLogCountVO> countThingsModelInvoke(DataCenterParam dataCenterParam) {
        Date beginTime = null;
        Date endTime = null;
        if (dataCenterParam.getBeginTime() != null && dataCenterParam.getEndTime() != null) {
            beginTime = parseTime(dataCenterParam.getBeginTime());
            endTime = parseTime(dataCenterParam.getEndTime());
        }
        return deviceLogMapper.countThingsModelInvoke(dataCenterParam, beginTime, endTime);
    }

    @Override
    public DeviceLog selectLastReport(DeviceLog deviceLog) {
        Date beginTime = null;
        Date endTime = null;
        if (deviceLog.getBeginTime() != null && deviceLog.getEndTime() != null) {
            beginTime = parseTime(deviceLog.getBeginTime());
            endTime = parseTime(deviceLog.getEndTime());
        }
        IPage<DeviceLog> deviceLogPage = new Page<>(1, 1);
        IPage<DeviceLog> ret = deviceLogMapper.selectLastReport(deviceLogPage, deviceLog, beginTime, endTime);
        if (ret.getTotal() > 0) {
            return ret.getRecords().get(0);
        } else {
            return null;
        }
    }

    /**
     * 查询单个属性历史数据
     */
    @Override
    public   List<HistoryBo> selectHistorySingleBo(DeviceLog deviceLog){
        return deviceLogMapper.selectHistorySingleBo(deviceLog);
    }
}
