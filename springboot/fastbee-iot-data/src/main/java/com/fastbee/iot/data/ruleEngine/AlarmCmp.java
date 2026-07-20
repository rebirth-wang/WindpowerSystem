package com.fastbee.iot.data.ruleEngine;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSONObject;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import org.apache.commons.collections4.CollectionUtils;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.notify.AlertPushParams;
import com.fastbee.common.extend.core.domin.thingsModel.SceneThingsModelItem;
import com.fastbee.common.extend.enums.AlertLogStatusEnum;
import com.fastbee.common.extend.enums.EnableEnum;
import com.fastbee.common.extend.enums.SceneSourceEnum;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.data.service.IDataHandler;
import com.fastbee.iot.domain.AlertLog;
import com.fastbee.iot.domain.AlertNotifyTemplate;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceAlertUser;
import com.fastbee.iot.model.vo.AlertSceneSendVO;
import com.fastbee.iot.model.vo.DeviceAlertUserVO;
import com.fastbee.iot.model.vo.SceneTerminalUserVO;
import com.fastbee.iot.service.*;
import com.fastbee.notify.core.service.NotifySendService;
import com.fastbee.rule.cmp.data.AlarmData;
import com.fastbee.rule.context.RuleContext;

@LiteflowComponent("alarm")
public class AlarmCmp extends NodeComponent {
    @Resource
    private IAlertService alertService;

    @Resource
    private IDeviceService deviceService;

    @Resource
    private IDataHandler dataHandler;

    @Resource
    private IAlertLogService alertLogService;

    @Resource
    private NotifySendService notifySendService;

    @Resource
    private ISceneService sceneService;

    @Resource
    private IDeviceAlertUserService deviceAlertUserService;

    @Override
    public void process() throws Exception {
        AlarmData data = this.getCmpData(AlarmData.class);
        RuleContext cxt = this.getContextBean(RuleContext.class);
        List<SceneThingsModelItem> sceneThingsModelItems = cxt.getSceneThingsModelItems();
        String requestId = "scene/" + this.getChainId();
        if (SceneSourceEnum.ALARM.getSource().equals(data.getSource())) {
            Long sceneId = cxt.getSceneId();
            cxt.printDebugLog(this,"[%s]=====+>告警执行，sceneId=%d", requestId, sceneId);
            if (Objects.isNull(sceneThingsModelItems)) {
                return;
            }
            boolean isNodify = checkDeviceAlerting(cxt, requestId, data.getNotifyCount());
            cxt.printDebugLog(this,"[%s]=====+>是否发送告警，isNodify=%b", requestId, isNodify);
            Set<Long> sceneIdSet = sceneThingsModelItems.stream().map(SceneThingsModelItem::getSceneId).collect(Collectors.toSet());
            List<SceneTerminalUserVO> sceneTerminalUserVOList = sceneService.selectTerminalUserBySceneIds(sceneIdSet);
            Map<Long, SceneTerminalUserVO> sceneTerminalUserMap = sceneTerminalUserVOList.stream().collect(Collectors.toMap(SceneTerminalUserVO::getSceneId, Function.identity()));
            List<AlertLog> alertLogList = new ArrayList<>();
            for (SceneThingsModelItem sceneThingsModelItem : sceneThingsModelItems) {
                // 查询设备信息
                Device device = deviceService.selectDeviceBySerialNumber(sceneThingsModelItem.getDeviceNumber());
                Optional.ofNullable(device).orElseThrow(() -> new ServiceException(StringUtils.format(MessageUtils.message("alert.push.fail.device.not.exist"), sceneThingsModelItem.getDeviceNumber())));
                // 判断是否是终端用户的场景
                SceneTerminalUserVO sceneTerminalUserVO = sceneTerminalUserMap.get(sceneId);
                if (EnableEnum.ENABLE.getType().equals(sceneTerminalUserVO.getTerminalUser())) {
                    AlertLog alertLog = this.getTerminalUserAlertLog(sceneTerminalUserVO, device, sceneThingsModelItem);
                    alertLogList.add(alertLog);
                    continue;
                }
                // 获取场景相关的告警参数，告警必须要是启动状态
                List<AlertSceneSendVO> sceneSendVOList = alertService.listByAlertIds(sceneId);
                if (CollectionUtils.isEmpty(sceneSendVOList)) {
                    continue;
                }
                // 获取告警关联模版id
                for (AlertSceneSendVO alertSceneSendVO : sceneSendVOList) {
                    if (isNodify) {
                        AlertPushParams alertPushParams = this.buildAlertPushParams(device, sceneThingsModelItem);
                        List<AlertNotifyTemplate> alertNotifyTemplateList = alertService.listAlertNotifyTemplate(alertSceneSendVO.getAlertId());
                        alertPushParams.setAlertName(alertSceneSendVO.getAlertName());
                        for (AlertNotifyTemplate alertNotifyTemplate : alertNotifyTemplateList) {
                            alertPushParams.setNotifyTemplateId(alertNotifyTemplate.getNotifyTemplateId());
                            String mqttMsg = notifySendService.alertSend(alertPushParams);
                            if (StringUtils.isNotEmpty(mqttMsg)) {
                                dataHandler.notifyAlertWeb(mqttMsg, alertPushParams.getUserIdSet());
                            }
                        }
                    }
                    AlertLog alertLog = this.getAlertLog(alertSceneSendVO, device, sceneThingsModelItem);
                    alertLogList.add(alertLog);
                }
            }
            // 保存告警日志
            if (CollectionUtils.isNotEmpty(alertLogList)) {
                alertLogService.insertAlertLogBatch(alertLogList);
            }
        } else if (SceneSourceEnum.ALARM_RECOVER.getSource().equals(data.getSource())) {
            AlertLog alertLog = new AlertLog();
            alertLog.setSceneId(data.getId());
            alertLog.setSerialNumber(cxt.getSerialNumber());
            alertLog.setStatus(AlertLogStatusEnum.PROCESSED.getStatus());
            //自动设置告警处理状态
            alertLogService.updateAlertLogStatus(alertLog);
            // scene_logger.info("[{}]=====+>告警恢复！", requestId);
            cxt.printDebugLog(this,"[%s]=====+>告警恢复!", requestId);
        } else {
            // scene_logger.info("[{}]=====+>未知触发源！", data.getSource());
            cxt.printDebugLog(this,"[%s]=====+>未知触发源!", requestId);
        }
    }

    private AlertLog getTerminalUserAlertLog(SceneTerminalUserVO sceneTerminalUserVO, Device device, SceneThingsModelItem sceneThingsModelItem) {
        AlertLog alertLog = new AlertLog();
        alertLog.setAlertName("设备告警");
        alertLog.setAlertLevel(1L);
        alertLog.setSerialNumber(sceneThingsModelItem.getDeviceNumber());
        alertLog.setProductId(sceneThingsModelItem.getProductId());
        alertLog.setDeviceName(device.getDeviceName());
        alertLog.setUserId(sceneTerminalUserVO.getUserId());
        // 统一未处理(处理中状态)
        alertLog.setStatus(AlertLogStatusEnum.PROCESSING.getStatus());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", sceneThingsModelItem.getId());
        jsonObject.put("name", sceneThingsModelItem.getName());
        jsonObject.put("value", sceneThingsModelItem.getValue());
        jsonObject.put("remark", sceneThingsModelItem.getRemark());
        alertLog.setDetail(jsonObject.toJSONString());
        alertLog.setCreateTime(new Date());
        alertLog.setCreateBy(device.getCreateBy());
        alertLog.setSceneId(sceneThingsModelItem.getSceneId());
        return alertLog;
    }

    private AlertLog getAlertLog(AlertSceneSendVO alertSceneSendVO, Device device, SceneThingsModelItem sceneThingsModelItem) {
        AlertLog alertLog = new AlertLog();
        alertLog.setAlertName(alertSceneSendVO.getAlertName());
        alertLog.setAlertLevel(alertSceneSendVO.getAlertLevel());
        alertLog.setSerialNumber(sceneThingsModelItem.getDeviceNumber());
        alertLog.setProductId(sceneThingsModelItem.getProductId());
        alertLog.setDeviceName(device.getDeviceName());
        alertLog.setUserId(device.getTenantId());
        // 统一未处理(处理中状态)
        alertLog.setStatus(AlertLogStatusEnum.PROCESSING.getStatus());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", sceneThingsModelItem.getId());
        jsonObject.put("name", sceneThingsModelItem.getName());
        jsonObject.put("value", sceneThingsModelItem.getValue());
        jsonObject.put("remark", sceneThingsModelItem.getRemark());
        alertLog.setDetail(jsonObject.toJSONString());
        alertLog.setCreateTime(new Date());
        alertLog.setCreateBy(device.getCreateBy());
        alertLog.setSceneId(sceneThingsModelItem.getSceneId());
        return alertLog;
    }

    private AlertPushParams buildAlertPushParams(Device device, SceneThingsModelItem sceneThingsModelItem) {
        // 获取告警推送参数
        AlertPushParams alertPushParams = new AlertPushParams();
        alertPushParams.setDeviceName(device.getDeviceName());
        alertPushParams.setSerialNumber(sceneThingsModelItem.getDeviceNumber());
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
        alertPushParams.setValue(sceneThingsModelItem.getValue());
        alertPushParams.setMatchValue(sceneThingsModelItem.getMatchValue());
        return alertPushParams;
    }

    private boolean checkDeviceAlerting(RuleContext cxt, String requestId, Integer notifyCount) {
        AlertLog alertLog = new AlertLog();
        alertLog.setSerialNumber(cxt.getSerialNumber());
        alertLog.setStatus(AlertLogStatusEnum.PROCESSING.getStatus());
        alertLog.setSceneId(cxt.getSceneId());
        Long count = alertLogService.selectAlertLogListCount(alertLog);
        // 查询设备告警对应的场景是否有未处理告警
        if (count >= notifyCount) {
            // scene_logger.info("[{}]=====+>只记录日志不发送告警！", requestId);
            cxt.printDebugLog(this,"[%s]=====+>只记录日志不发送告警！", requestId);
            return false;
        } else {
            // scene_logger.info("[{}]=====+>发送告警！", requestId);
            cxt.printDebugLog(this,"[%s]=====+>发送告警！", requestId);
            return true;
        }
    }
}
