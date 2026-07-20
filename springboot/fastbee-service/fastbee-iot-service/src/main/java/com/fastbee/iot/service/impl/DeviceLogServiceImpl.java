package com.fastbee.iot.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceLog;
import com.fastbee.iot.mapper.DeviceLogMapper;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.model.DeviceReport;
import com.fastbee.iot.model.HistoryModel;
import com.fastbee.iot.model.MonitorModel;
import com.fastbee.iot.model.param.DataCenterParam;
import com.fastbee.iot.model.vo.ThingsModelLogCountVO;
import com.fastbee.iot.model.vo.ThingsModelVO;
import com.fastbee.iot.service.IDeviceLogService;
import com.fastbee.iot.service.IThingsModelService;
import com.fastbee.iot.tsdb.service.ILogService;

/**
 * 设备日志Service业务层处理
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Service
@Slf4j
public class DeviceLogServiceImpl implements IDeviceLogService {
    @Autowired
    private DeviceLogMapper deviceLogMapper;

    @Autowired
    private ILogService logService;

    @Autowired
    private IThingsModelService thingsModelService;

    @Resource
    private DeviceMapper deviceMapper;

    /**
     * 查询设备日志
     *
     * @param logId 设备日志主键
     * @return 设备日志
     */
    @Override
    public DeviceLog selectDeviceLogByLogId(Long logId) {
        return deviceLogMapper.selectDeviceLogByLogId(logId);
    }

    /**
     * 查询设备日志列表
     *
     * @param deviceLog 设备日志
     * @return 设备日志
     */
    @Override
    public Page<DeviceLog> selectDeviceLogList(DeviceLog deviceLog) {
        if (deviceLog.getIsMonitor() == null) {
            byte isMonitor = 0;
            deviceLog.setIsMonitor(isMonitor);
        }
        return logService.selectDeviceLogList(deviceLog);
    }

    /**
     * 查询设备监测数据
     *
     * @param deviceLog 设备日志
     * @return 设备日志
     */
    @Override
    public List<MonitorModel> selectMonitorList(DeviceLog deviceLog) {
        return logService.selectMonitorList(deviceLog);
    }

    /**
     * 新增设备日志
     *
     * @param deviceLog 设备日志
     * @return 结果
     */
    @Override
    public int insertDeviceLog(DeviceLog deviceLog) {
        deviceLog.setCreateTime(DateUtils.getNowDate());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        deviceLog.setTenantId(loginUser.getDeptUserId());
        deviceLog.setUserId(loginUser.getUserId());
        deviceLog.setCreateBy(loginUser.getUsername());
        return logService.saveDeviceLog(deviceLog);
    }

    /**
     * 修改设备日志
     *
     * @param deviceLog 设备日志
     * @return 结果
     */
    @Override
    public int updateDeviceLog(DeviceLog deviceLog) {
        return deviceLogMapper.updateDeviceLog(deviceLog);
    }

    /**
     * 批量删除设备日志
     *
     * @param logIds 需要删除的设备日志主键
     * @return 结果
     */
    @Override
    public int deleteDeviceLogByLogIds(Long[] logIds) {
        return deviceLogMapper.deleteDeviceLogByLogIds(logIds);
    }

    /**
     * 根据设备Ids批量删除设备日志
     *
     * @param deviceNumber 需要删除数据的设备Ids
     * @return 结果
     */
    @Override
    public int deleteDeviceLogByDeviceNumber(String deviceNumber) {
        return deviceLogMapper.deleteDeviceLogByDeviceNumber(deviceNumber);
    }

    /**
     * 删除设备日志信息
     *
     * @param logId 设备日志主键
     * @return 结果
     */
    @Override
    public int deleteDeviceLogByLogId(Long logId) {
        return deviceLogMapper.deleteDeviceLogByLogId(logId);
    }

    /**
     * 查询设备历史数据
     *
     * @param deviceLog 设备日志
     * @return 设备日志集合
     */
    @Override
    public Map<String, List<HistoryModel>> selectHistoryList(DeviceLog deviceLog) {
        return logService.selectHistoryList(deviceLog);
    }

    @Override
    public List<DeviceReport> selectDeviceReportData(Date beginTime, Date endTime) {
        SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.YYYY_MM_DD_HH_MM_SS);
        String beginTimeString = formatter.format(beginTime);
        String endTimeString = formatter.format(endTime);
        LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Device::getStatus, 3);
        //查询在线设备
        List<Device> deviceList = deviceMapper.selectList(queryWrapper);
        List<DeviceReport> deviceReportList = new ArrayList<>();
        for (Device device : deviceList) {
            DeviceReport deviceReport = new DeviceReport();
            deviceReport.setSerialNumber(device.getSerialNumber());
            List<DeviceLog> reportList = new ArrayList<>();
            List<String> unReportList = new ArrayList<>();
            // 查询所有物模型id
            ThingsModelVO thingsModelVO = new ThingsModelVO();
            thingsModelVO.setType(1);
            thingsModelVO.setIsHistory(1);
            thingsModelVO.setProductId(device.getProductId());
            List<ThingsModelVO> list = thingsModelService.selectThingsModelList(thingsModelVO);
            for (ThingsModelVO item : list) {
                DeviceLog deviceLog = new DeviceLog();
                deviceLog.setSerialNumber(device.getSerialNumber());
                deviceLog.setIdentify(item.getIdentifier());
                deviceLog.setBeginTime(beginTimeString);
                deviceLog.setEndTime(endTimeString);
                DeviceLog lastdeviceLog = logService.selectLastReport(deviceLog);
                if (lastdeviceLog != null) {
                    reportList.add(lastdeviceLog);
                } else {
                    unReportList.add(item.getIdentifier());
                }
            }
            deviceReport.setReportList(reportList);
            deviceReport.setUnReportList(unReportList);
            deviceReportList.add(deviceReport);
        }
        //log.info("deviceReportList:{}", deviceReportList);
        return deviceReportList;
    }

    @Override
    public List<HistoryModel> listHistory(DeviceLog deviceLog) {
        return logService.listHistory(deviceLog);
    }

    @Override
    public List<JSONObject> listhistoryGroupByCreateTime(DeviceLog deviceLog) {
        Page<HistoryModel> modelList = logService.listhistoryGroupByCreateTime(deviceLog);
        List<JSONObject> resultList = new ArrayList<>();
        for (HistoryModel historyModel : modelList.getRecords()) {
            List<String> identityList = StringUtils.str2List(historyModel.getIdentify(), ",", true, true);
            List<String> valueList = StringUtils.str2List(historyModel.getValue(), ",", true, true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("time", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, historyModel.getTime()));
            for (int i = 0; i < identityList.size(); i++) {
                jsonObject.put("name" + (i + 1), identityList.get(i));
                jsonObject.put("value" + (i + 1), valueList.get(i));
            }
            resultList.add(jsonObject);
        }
        return resultList;
    }

    @Override
    public List<ThingsModelLogCountVO> countThingsModelInvoke(DataCenterParam dataCenterParam) {
        return logService.countThingsModelInvoke(dataCenterParam);
    }
}
