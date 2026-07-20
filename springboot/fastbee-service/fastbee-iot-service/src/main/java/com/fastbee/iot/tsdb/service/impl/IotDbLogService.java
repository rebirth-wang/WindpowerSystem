package com.fastbee.iot.tsdb.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceLog;
import com.fastbee.iot.mapper.IotDbLogMapper;
import com.fastbee.iot.model.DeviceStatistic;
import com.fastbee.iot.model.HistoryBo;
import com.fastbee.iot.model.HistoryModel;
import com.fastbee.iot.model.MonitorModel;
import com.fastbee.iot.model.param.DataCenterParam;
import com.fastbee.iot.model.vo.ThingsModelLogCountVO;
import com.fastbee.iot.tsdb.model.TdLogDto;
import com.fastbee.iot.tsdb.service.ILogService;

@Slf4j
@Primary
@ConditionalOnProperty(name = "spring.datasource.dynamic.datasource.iotdb.enabled", havingValue = "true")
@DS("iotdb")
@Service("IotDB")
public class IotDbLogService implements ILogService {

    @Resource
    private IotDbLogMapper iotDbLogMapper;


    @Override
    public int createSTable(String database) {
        Long count = iotDbLogMapper.countDB(database);
        if (count == 0) {
            iotDbLogMapper.createDB(database);
        }
        return 1;
    }

    @Override
    public int saveDeviceLog(DeviceLog deviceLog) {
        return iotDbLogMapper.save(deviceLog);
    }

    @Override
    public int saveBatch(TdLogDto dto) {
        int ret = 0;
        for (DeviceLog deviceLog : dto.getList()) {
            ret += this.saveDeviceLog(deviceLog);
        }
        return ret;
    }

    @Override
    public int deleteDeviceLogByDeviceNumber(String deviceNumber) {
        return iotDbLogMapper.deleteDeviceLogByDeviceNumber(deviceNumber);
    }

    @Override
    public DeviceStatistic selectCategoryLogCount(Device device) {
        DeviceStatistic statistic = new DeviceStatistic();
        Long property = iotDbLogMapper.selectPropertyLogCount(device);
        Long event = iotDbLogMapper.selectEventLogCount(device);
        Long monitor = iotDbLogMapper.selectMonitorLogCount(device);
        statistic.setPropertyCount(property == null ? 0 : property);
        statistic.setEventCount(event == null ? 0 : event);
        statistic.setMonitorCount(monitor == null ? 0 : monitor);
        return statistic;
    }

    @Override
    public Page<DeviceLog> selectDeviceLogList(DeviceLog deviceLog) {
        if (deviceLog.getIdentify() != null) {
            deviceLog.setIdentify("%" + deviceLog.getIdentify() + "%");
        }
        return iotDbLogMapper.selectDeviceLogList(new Page<>(deviceLog.getPageNum(), deviceLog.getPageSize()), deviceLog);
    }

    @Override
    public Page<DeviceLog> selectEventLogList(DeviceLog deviceLog) {
        if (deviceLog.getParams().get("beginTime") != null && deviceLog.getParams().get("beginTime") != "" && deviceLog.getParams().get("endTime") != null && deviceLog.getParams().get("endTime") != "") {
            String beginTime = deviceLog.getParams().get("beginTime").toString();
            String endTime = deviceLog.getParams().get("endTime").toString();
            beginTime = beginTime + " 00:00:00";
            endTime = endTime + " 23:59:59";
            deviceLog.setBeginTime(beginTime);
            deviceLog.setEndTime(endTime);
        }
        if (deviceLog.getIdentify() != null) {
            deviceLog.setIdentify("%" + deviceLog.getIdentify() + "%");
        }
        // 获取全量数据
        List<DeviceLog> allLogs = iotDbLogMapper.selectEventLogList(deviceLog);

        // 手动分页处理
        int pageSize = deviceLog.getPageSize();
        int pageNum = deviceLog.getPageNum();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allLogs.size());

        // 构建MyBatis-Plus分页对象
        Page<DeviceLog> page = new Page<>(pageNum, pageSize);
        page.setRecords(allLogs.subList(start, end));
        page.setTotal(allLogs.size());

        return page;
    }

    @Override
    public List<MonitorModel> selectMonitorList(DeviceLog deviceLog) {
        if (deviceLog.getIdentify() != null) {
            deviceLog.setIdentify("%" + deviceLog.getIdentify() + "%");
        }
        return iotDbLogMapper.selectMonitorList(deviceLog);
    }

    @Override
    public Map<String, List<HistoryModel>> selectHistoryList(DeviceLog deviceLog) {
        List<HistoryModel> historyList = iotDbLogMapper.selectHistoryList(deviceLog);
        return historyList.stream().collect(Collectors.groupingBy(HistoryModel::getIdentify));
    }

    @Override
    public List<HistoryModel> listHistory(DeviceLog deviceLog) {
        return iotDbLogMapper.listHistory(deviceLog);
    }

    @Override
    public Page<HistoryModel> listhistoryGroupByCreateTime(DeviceLog deviceLog) {
        return iotDbLogMapper.listhistoryGroupByCreateTime(new Page<>(deviceLog.getPageNum(), deviceLog.getPageSize()), deviceLog);
    }

    @Override
    public List<String> selectStatsValue(DeviceLog deviceLog) {
        return iotDbLogMapper.selectStatsValue(deviceLog);
    }

    @Override
    public List<ThingsModelLogCountVO> countThingsModelInvoke(DataCenterParam dataCenterParam) {
        List<ThingsModelLogCountVO> thingsModelLogCountVOS = iotDbLogMapper.countThingsModelInvoke(dataCenterParam);
        thingsModelLogCountVOS.forEach(vo -> {vo.setCounts(thingsModelLogCountVOS.size());});
        return thingsModelLogCountVOS;
    }

    @Override
    public DeviceLog selectLastReport(DeviceLog deviceLog) {
        return null;
    }

    @Override
    public List<HistoryBo> selectHistorySingleBo(DeviceLog deviceLog) {
        return iotDbLogMapper.selectHistoryListBo(deviceLog);
    }
}
