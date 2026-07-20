package com.fastbee.iot.data.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.fastbee.common.enums.DeviceStatus;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.mq.DeviceStatusBo;
import com.fastbee.common.extend.core.domin.notify.AlertPushParams;
import com.fastbee.common.extend.enums.AlertLogStatusEnum;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.framework.mybatis.helper.DataBaseHelper;
import com.fastbee.iot.domain.*;
import com.fastbee.iot.model.vo.AlertSceneSendVO;
import com.fastbee.iot.model.vo.DeviceAlertUserVO;
import com.fastbee.iot.service.*;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.service.ICommonChannelService;
import com.fastbee.mq.producer.MessageProducer;
import com.fastbee.notify.core.service.NotifySendService;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.enums.DeviceChannelStatus;
import com.fastbee.sip.mapper.SipDeviceMapper;

/**
 * @author gsb
 * @date 2023/2/20 17:13
 */
@Component
@Slf4j
public class DeviceJob {

    @Resource
    private IDeviceService deviceService;
    @Resource
    private SipDeviceMapper sipDeviceMapper;
    @Resource
    private ICommonChannelService commonChannelService;
    @Resource
    private IDeviceLogService deviceLogService;
    @Resource
    private IAlertService alertService;
    @Resource
    private NotifySendService notifySendService;
    @Resource
    private IAlertLogService alertLogService;
    @Resource
    private IDeviceAlertUserService deviceAlertUserService;
    @Resource
    private ISceneScriptService sceneScriptService;
    @Resource
    private IDeviceUpdateService deviceUpdateService;


    /**
     * 监控设备离线定时检查
     */
    public void updateSipDeviceOnlineStatus(Integer timeout) {
        String checkTimeCondition = DataBaseHelper.checkTime(timeout);
        List<SipDevice> devs = sipDeviceMapper.selectOfflineSipDevice(checkTimeCondition);
        devs.forEach(item -> {
            if (!Objects.equals(item.getDeviceSipId(), "")) {
                //更新iot设备状态
                Device dev = deviceService.selectDeviceBySerialNumber(item.getDeviceSipId());
                if (dev != null && dev.getStatus() == DeviceStatus.ONLINE.getType()) {
                    log.warn("定时任务：=>设备:{} 已经下线，设备超过{}秒没上线！", item.getDeviceSipId(), timeout);
                    dev.setStatus(DeviceStatus.OFFLINE.getType());
                    deviceUpdateService.updateDeviceStatusAndLocation(dev, "");
                    DeviceStatusBo bo = DeviceStatusBo.builder()
                            .serialNumber(dev.getSerialNumber())
                            .status(DeviceStatus.OFFLINE)
                            .build();
                    MessageProducer.sendStatusMsg(bo);
                }
                //更新通道状态
                List<CommonChannel> channels = commonChannelService
                        .lambdaQuery().eq(CommonChannel::getDeviceId, item.getDeviceSipId()).list();
                if (CollectionUtils.isEmpty(channels)) {
                    return;
                }
                channels.forEach(citem -> {
                    citem.setStatus(DeviceChannelStatus.offline.getValue());
                });
                commonChannelService.updateBatchById(channels);
            }
        });
    }

    /**
     * 检查设备上报日志
     *
     * @param cycle 设备上报日志周期（分钟）
     * @return void
     */

    public void checkDeviceReportData(String TriggerId, Integer cycle) {
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        calendar.setTime(now);
        calendar.add(Calendar.MINUTE, -cycle);
        //查询制定cycle周期（分钟）的设备上报日志
        deviceLogService.selectDeviceReportData(calendar.getTime(), now).forEach(item -> {
            // 获取无上报参数属性列表
            if (!item.getUnReportList().isEmpty()) {
                item.getUnReportList().forEach(s -> {
                    //传入入触发id 设备序列号 和 无上报的属性id，执行告警
                    alertPush(TriggerId, item.getSerialNumber(), s);
                });
            }
        });
    }

    void alertPush(String TriggerId, String serialNumber, String identity) {
        // 获取告警场景id
        SceneScript triggerScript = new SceneScript();
        SceneScript alertlog = new SceneScript();
        // 设置筛选的触发id
        triggerScript.setId(TriggerId);
        // 获取脚本列表
        List<SceneScript> list = sceneScriptService.selectSceneScriptList(triggerScript);
        if (!list.isEmpty()) {
            triggerScript = list.get(0);
            alertlog.setId(identity);
            alertlog.setValue("无数据上报");
            // 获取脚本对应场景id
            alertlog.setSceneId(triggerScript.getSceneId());
        } else {
            return;
        }
        // 自定义触发动作逻辑，或者获取对应场景动作 执行对应的动作
        // 执行告警动作
        List<AlertLog> alertLogList = new ArrayList<>();
        // 查询设备信息
        Device device = deviceService.selectDeviceBySerialNumber(serialNumber);
        Optional.ofNullable(device).orElseThrow(() -> new ServiceException(StringUtils.format(MessageUtils.message("alert.push.fail.device.not.exist"), serialNumber)));
        // 获取场景相关的告警参数，告警必须要是启动状态
        List<AlertSceneSendVO> sceneSendVOList = alertService.listByAlertIds(alertlog.getSceneId());
        if (CollectionUtils.isEmpty(sceneSendVOList)) {
            return;
        }
        // 获取告警推送参数
        AlertPushParams alertPushParams = new AlertPushParams();
        alertPushParams.setDeviceName(device.getDeviceName());
        alertPushParams.setSerialNumber(serialNumber);
        // 多租户改版查询自己配置的告警用户
        DeviceAlertUser deviceAlertUser = new DeviceAlertUser();
        deviceAlertUser.setDeviceId(device.getDeviceId());
        List<DeviceAlertUserVO> deviceAlertUserVOList = deviceAlertUserService.selectDeviceAlertUserList(deviceAlertUser).getRecords();
        if (CollectionUtils.isNotEmpty(deviceAlertUserVOList)) {
            alertPushParams.setUserPhoneSet(deviceAlertUserVOList.stream().map(DeviceAlertUserVO::getPhoneNumber).filter(StringUtils::isNotEmpty).collect(Collectors.toSet()));
            alertPushParams.setUserIdSet(deviceAlertUserVOList.stream().map(DeviceAlertUserVO::getUserId).collect(Collectors.toSet()));
        }
        String address;
        if (StringUtils.isNotEmpty(device.getNetworkAddress())) {
            address = device.getNetworkAddress();
        } else if (StringUtils.isNotEmpty(device.getNetworkIp())) {
            address = device.getNetworkIp();
        } else if (Objects.nonNull(device.getLongitude()) && Objects.nonNull(device.getLatitude())) {
            address = device.getLongitude() + "," + device.getLatitude();
        } else {
            address = "未知地点";
        }
        alertPushParams.setAddress(address);
        alertPushParams.setAlertTime(DateUtils.parseDateToStr(DateUtils.YY_MM_DD_HH_MM_SS, new Date()));
        // 获取告警关联模版id
        for (AlertSceneSendVO alertSceneSendVO : sceneSendVOList) {
            List<AlertNotifyTemplate> alertNotifyTemplateList = alertService.listAlertNotifyTemplate(alertSceneSendVO.getAlertId());
            alertPushParams.setAlertName(alertSceneSendVO.getAlertName());
            for (AlertNotifyTemplate alertNotifyTemplate : alertNotifyTemplateList) {
                alertPushParams.setNotifyTemplateId(alertNotifyTemplate.getNotifyTemplateId());
                notifySendService.alertSend(alertPushParams);
            }
            List<AlertLog> alist = alertLogService.selectAlertLogListByCreateBy(alertlog.getSceneId(), alertlog.getId(), AlertLogStatusEnum.PROCESSING.getStatus());
            if (alist.isEmpty()) {
                AlertLog alertLog = buildAlertLog(alertSceneSendVO, device, alertlog);
                alertLogList.add(alertLog);
            } else {
                //重复未处理告警，只更新告警发生时间
                for (AlertLog alertLog : alist) {
                    alertLog.setCreateTime(new Date());
                    alertLogService.updateAlertLog(alertLog);
                }
            }
        }
        // 保存告警日志
        alertLogService.insertAlertLogBatch(alertLogList);
    }

    private AlertLog buildAlertLog(AlertSceneSendVO alertSceneSendVO, Device device, SceneScript Item) {
        AlertLog alertLog = new AlertLog();
        alertLog.setAlertName(alertSceneSendVO.getAlertName());
        alertLog.setAlertLevel(alertSceneSendVO.getAlertLevel());
        alertLog.setSerialNumber(device.getSerialNumber());
        alertLog.setProductId(device.getProductId());
        alertLog.setDeviceName(device.getDeviceName());
        alertLog.setUserId(device.getTenantId());
        alertLog.setStatus(AlertLogStatusEnum.PROCESSING.getStatus());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", Item.getId());
        jsonObject.put("value", Item.getValue());
        jsonObject.put("remark", "");
        alertLog.setDetail(jsonObject.toJSONString());
        alertLog.setCreateBy(device.getCreateBy());
        alertLog.setSceneId(Item.getSceneId());
        alertLog.setRemark(Item.getId());
        alertLog.setCreateTime(new Date());
        return alertLog;
    }
}
