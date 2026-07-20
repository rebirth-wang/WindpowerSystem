package com.fastbee.iot.data.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import jakarta.annotation.Resource;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fastbee.common.enums.DeviceLogTypeEnum;
import com.fastbee.common.enums.FunctionReplyStatus;
import com.fastbee.common.enums.TopicType;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.mq.InvokeReqDto;
import com.fastbee.common.extend.core.domin.mq.SubDeviceBo;
import com.fastbee.common.extend.core.domin.mq.message.ReportDataBo;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelValuesInput;
import com.fastbee.common.extend.enums.scenemodel.SceneModelTagOpreationEnum;
import com.fastbee.common.utils.CaculateVariableAndNumberUtils;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.date.LocalDateTimeUtils;
import com.fastbee.common.utils.gateway.mq.TopicsUtils;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.cache.SceneModelTagCache;
import com.fastbee.iot.data.service.IDataHandler;
import com.fastbee.iot.data.service.IMqttMessagePublish;
import com.fastbee.iot.data.service.IRuleEngine;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceLog;
import com.fastbee.iot.domain.FunctionLog;
import com.fastbee.iot.domain.SceneTagPoints;
import com.fastbee.iot.mapper.SceneTagPointsMapper;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.scenemodel.JobCronCycleVO;
import com.fastbee.iot.model.scenemodel.SceneModelTagCacheVO;
import com.fastbee.iot.model.vo.DeviceVO;
import com.fastbee.iot.model.vo.SceneModelTagVO;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.iot.service.IFunctionLogService;
import com.fastbee.iot.service.ISceneModelTagService;
import com.fastbee.iot.tsdb.service.ILogService;
import com.fastbee.iot.util.JobCronUtils;
import com.fastbee.mqtt.manager.MqttRemoteManager;
import com.fastbee.mqtt.model.PushMessageBo;

/**
 * 上报数据处理方法集合
 *
 * @author bill
 */
@Service
@Slf4j
public class DataHandlerImpl implements IDataHandler {


    @Resource
    private IDeviceService deviceService;
    @Resource
    private IMqttMessagePublish messagePublish;
    @Resource
    private IRuleEngine ruleEngine;
    @Resource
    private MqttRemoteManager remoteManager;
    @Resource
    private TopicsUtils topicsUtils;

    @Resource
    private SceneTagPointsMapper sceneTagPointsMapper;

    @Resource
    private SceneModelTagCache sceneModelTagCache;

    @Resource
    private ILogService logService;

    @Resource
    private ISceneModelTagService sceneModelTagService;

    @Resource
    private IFunctionLogService functionLogService;

    @Resource
    private IDeviceCache deviceCache;

    /**
     * 上报属性或功能处理
     *
     * @param bo 上报数据模型
     */
    @Override
    public void reportData(ReportDataBo bo) {
            if (CollectionUtil.isEmpty(bo.getSubDeviceBoList())) {
                List<ThingsModelSimpleItem> thingsModelSimpleItems = bo.getDataList();
                if (CollectionUtils.isEmpty(bo.getDataList()) || bo.getDataList().size() == 0) {
                    thingsModelSimpleItems = JSON.parseArray(bo.getMessage(), ThingsModelSimpleItem.class);
                }
                if (CollectionUtils.isEmpty(thingsModelSimpleItems)) return;
                this.reportSingleData(bo, thingsModelSimpleItems, null);
            } else {
                for (SubDeviceBo subDeviceBo : bo.getSubDeviceBoList()) {
                    this.reportSingleData(bo, subDeviceBo.getThingsModelSimpleItem(), subDeviceBo);
                }
            }
    }

    private void reportSingleData(ReportDataBo bo, List<ThingsModelSimpleItem> thingsModelSimpleItems, SubDeviceBo subDeviceBo) {
        try {
            // 只有设备上报进入规则引擎流程
            if (bo.isRuleEngine() && !bo.getSerialNumber().startsWith("server-")) {
                ruleEngine.ruleMatch(bo);
            }
            ThingsModelValuesInput input = new ThingsModelValuesInput();
            if (Objects.isNull(subDeviceBo)) {
                input.setProductId(bo.getProductId());
                input.setDeviceNumber(bo.getSerialNumber());
            } else {
                input.setProductId(subDeviceBo.getSubProductId());
                input.setDeviceNumber(subDeviceBo.getSubClientId());
            }
            input.setThingsModelValueRemarkItem(thingsModelSimpleItems);
            List<ThingsModelSimpleItem> result = deviceService.reportDeviceThingsModelValue(input, bo.getType(), bo.isShadow());
            //发送至前端
            PushMessageBo messageBo = new PushMessageBo();
            messageBo.setTopic(topicsUtils.buildTopic(bo.getProductId(), bo.getSerialNumber(), TopicType.WS_SERVICE_INVOKE));
            JSONObject pushObj = new JSONObject();
            pushObj.put("message", result);
            pushObj.put("sources", bo.getSources());
            messageBo.setMessage(JSON.toJSONString(pushObj));
            remoteManager.pushCommon(messageBo);
            if (Objects.nonNull(subDeviceBo)) {
                messageBo.setTopic(topicsUtils.buildTopic(subDeviceBo.getSubProductId(), subDeviceBo.getSubClientId(), TopicType.WS_SERVICE_INVOKE));
                remoteManager.pushCommon(messageBo);
            }
            // 上报属性给小度音箱，接入小度音箱后可放开
//            try {
//                List<String> identifierList = thingsModelSimpleItems.stream().map(ThingsModelSimpleItem::getId).collect(Collectors.toList());
//                speakerService.reportDuerosAttribute(bo.getSerialNumber(), identifierList);
//            } catch (Exception e) {
//                log.error("=>上报属性信息给小度音箱异常", e);
//            }
        } catch (Exception e) {
            log.error("接收属性数据，解析数据时异常 message={},e={}", e.getMessage(), e);
        }
    }


    /**
     * 上报事件
     *
     * @param bo 上报数据模型
     */
    @Override
    public void reportEvent(ReportDataBo bo) {
        try {
            List<ThingsModelSimpleItem> thingsModelSimpleItems = JSON.parseArray(bo.getMessage(), ThingsModelSimpleItem.class);
            if (CollectionUtils.isEmpty(thingsModelSimpleItems)) {
                log.warn("reportEvent: thingsModelSimpleItems解析为空, serialNumber={}, message={}", bo.getSerialNumber(), bo.getMessage());
                return;
            }
            DeviceMetaData deviceMetaDataCache = deviceCache.getDeviceMetaDataCache(bo.getSerialNumber());
            if (deviceMetaDataCache == null || deviceMetaDataCache.getDevice() == null) {
                log.warn("reportEvent: 设备元数据为空, serialNumber={}", bo.getSerialNumber());
                return;
            }
            Device device = deviceMetaDataCache.getDevice();
            List<DeviceLog> results = new ArrayList<>();
            for (int i = 0; i < thingsModelSimpleItems.size(); i++) {
                byte logType = 3;
                byte isMonitor = 0;
                byte mode = 2;
                DeviceLog deviceLog = new DeviceLog();
                deviceLog.setDeviceId(device.getDeviceId());
                deviceLog.setDeviceName(device.getDeviceName());
                deviceLog.setLogValue(thingsModelSimpleItems.get(i).getValue());
                deviceLog.setRemark(thingsModelSimpleItems.get(i).getRemark());
                deviceLog.setSerialNumber(device.getSerialNumber());
                deviceLog.setIdentify(thingsModelSimpleItems.get(i).getId());
                deviceLog.setLogType(logType);
                deviceLog.setIsMonitor(isMonitor);
                deviceLog.setUserId(device.getTenantId());
                deviceLog.setUserName(device.getTenantName());
                deviceLog.setTenantId(device.getTenantId());
                deviceLog.setTenantName(device.getTenantName());
                deviceLog.setCreateBy(device.getCreateBy());
                deviceLog.setCreateTime(DateUtils.getNowDate());
                // 1=影子模式，2=在线模式，3=其他
                deviceLog.setMode(mode);
                results.add(deviceLog);
            }

            if (bo.isRuleEngine()) {
                ruleEngine.ruleMatch(bo);
            }

            for (DeviceLog deviceLog : results) {
                logService.saveDeviceLog(deviceLog);
            }
        } catch (Exception e) {
            log.error("接收事件，解析数据时异常 message={}", e.getMessage());
        }
    }

    /**
     * 上报设备信息
     */
    @Override
    public void reportDevice(ReportDataBo bo) {
        try {
            // 设备实体
            Device deviceEntity = deviceService.selectDeviceBySerialNumber(bo.getSerialNumber());
            if (deviceEntity != null) {
                if (!deviceEntity.getProductId().equals(bo.getProductId())) {
                    throw new ServiceException("接收设备信息，设备上报主题产品编号错误");
                }
                // 上报设备信息
                DeviceVO deviceVO = JSON.parseObject(bo.getMessage(), DeviceVO.class);
                deviceVO.setProductId(bo.getProductId());
                deviceVO.setSerialNumber(bo.getSerialNumber());
                deviceService.reportDevice(deviceVO, deviceEntity);
                // 发布设备状态
                messagePublish.publishStatus(bo.getProductId(), bo.getSerialNumber(), 3, deviceEntity.getIsShadow(), deviceVO.getRssi());
            }

        } catch (Exception e) {
            log.error("接收设备信息，解析数据时异常 message={}", e.getMessage());
            throw new ServiceException(e.getMessage(), 1);
        }
    }

    @Override
    public String calculateSceneModelTagValue(Long id) {
        LocalDateTime now = LocalDateTime.now();
        SceneModelTagVO sceneModelTagVO = sceneModelTagService.selectSceneModelTagAndTenantById(id);
        if (null == sceneModelTagVO) {
            return "场景运算型变量计算错误：变量为空";
        }
        if ((StringUtils.isEmpty(sceneModelTagVO.getAliasFormule()) && org.apache.commons.collections4.CollectionUtils.isEmpty(sceneModelTagVO.getTagPointsVOList()))) {
            return "场景运算型变量计算错误：没有计算公式";
        }
        String checkMsg = sceneModelTagService.checkAliasFormule(sceneModelTagVO);
        if (StringUtils.isNotEmpty(checkMsg)) {
            return "场景运算型变量计算错误：" + checkMsg;
        }
        LambdaQueryWrapper<SceneTagPoints> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SceneTagPoints::getTagId, sceneModelTagVO.getId());
        List<SceneTagPoints> sceneTagPointsList = sceneTagPointsMapper.selectList(queryWrapper);
        Map<String, String> replaceMap = new HashMap<>(2);
        // 计算周期
        JobCronCycleVO jobCronCycleVO = new JobCronCycleVO();
        boolean b = sceneTagPointsList.stream().anyMatch(s -> !SceneModelTagOpreationEnum.ORIGINAL_VALUE.getCode().equals(s.getOperation()));
        if (b) {
            jobCronCycleVO = JobCronUtils.handleTimeCycle(sceneModelTagVO.getCycleType(), sceneModelTagVO.getCycle(), now);
        }
        // 需不需要判断每个变量启用了没有 todo
        for (SceneTagPoints sceneTagPoints : sceneTagPointsList) {
            String value;
            value = sceneModelTagService.getSceneModelDataValue(sceneTagPoints, jobCronCycleVO);
            // value没值先兜底0
            if (StringUtils.isEmpty(value)) {
                value = "0";
            }
            replaceMap.put(sceneTagPoints.getAlias(), value);
        }
        BigDecimal execute = CaculateVariableAndNumberUtils.execute(sceneModelTagVO.getAliasFormule(), replaceMap);
        String resultValue = execute.toPlainString();
        this.saveSceneModelTagValue(sceneModelTagVO, resultValue, now);
        return "";
    }

    @Override
    public void invokeSceneModelTagValue(InvokeReqDto reqDto, String messageId) {
        LocalDateTime now = LocalDateTime.now();
        String sceneModelTagId = reqDto.getIdentifier();
        Map<String, Object> remoteCommand = reqDto.getRemoteCommand();
        String value = remoteCommand.get(sceneModelTagId).toString();
        FunctionLog functionLog = new FunctionLog();
        functionLog.setIdentify(reqDto.getIdentifier());
        functionLog.setFunType(4);
        functionLog.setFunValue(value);
        functionLog.setMessageId(messageId);
        functionLog.setSerialNumber(reqDto.getSceneModelId().toString());
        functionLog.setMode(3);
        functionLog.setModelName(reqDto.getModelName());
        SceneModelTagVO sceneModelTagVO = sceneModelTagService.selectSceneModelTagAndTenantById(Long.valueOf(sceneModelTagId));
        if (null == sceneModelTagVO) {
            functionLog.setResultCode(FunctionReplyStatus.FAIl.getCode());
            functionLog.setResultMsg(FunctionReplyStatus.FAIl.getMessage());
            functionLogService.insertFunctionLog(functionLog);
            return;
        }
        functionLog.setUserId(sceneModelTagVO.getTenantId());
        functionLog.setCreateBy(sceneModelTagVO.getCreateBy());
        this.saveSceneModelTagValue(sceneModelTagVO, value, now);
        functionLog.setResultCode(FunctionReplyStatus.SUCCESS.getCode());
        functionLog.setResultMsg(FunctionReplyStatus.SUCCESS.getMessage());
        functionLogService.insertFunctionLog(functionLog);
    }

    @Override
    public void notifyAlertWeb(String mqttMsg, Set<Long> userIdSet) {
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(userIdSet)) {
            for (Long userId : userIdSet) {
                //发送至前端
                PushMessageBo messageBo = new PushMessageBo();
                // /场景id/变量id/scene/report（对应变量标识）
                messageBo.setTopic("/notify/alert/web/push/" + userId);
                messageBo.setMessage(mqttMsg);
                remoteManager.pushCommon(messageBo);
            }
        }
    }

    @Override
    public void addCacheSceneModelTagValue(Long sceneModelId, Long id, String value, LocalDateTime now) {
        SceneModelTagCacheVO sceneModelTagCacheVO = new SceneModelTagCacheVO();
        sceneModelTagCacheVO.setId(id.toString());
        sceneModelTagCacheVO.setTs(LocalDateTimeUtils.localDateTimeToStr(now, LocalDateTimeUtils.YYYY_MM_DD_HH_MM_SS));
        sceneModelTagCacheVO.setValue(value);
        sceneModelTagCache.addSceneModelTagValue(sceneModelId, sceneModelTagCacheVO);
    }

    /**
     * 保存场景变量值
     *
     * @param sceneModelTagVO 变量类
     * @return void
     * @param: value 值
     * @param: now 执行时间
     */
    private void saveSceneModelTagValue(SceneModelTagVO sceneModelTagVO, String value, LocalDateTime now) {
        // 保存运算型变量值,存缓存
        SceneModelTagCacheVO sceneModelTagCacheVO = new SceneModelTagCacheVO();
        sceneModelTagCacheVO.setId(sceneModelTagVO.getId().toString());
        sceneModelTagCacheVO.setTs(LocalDateTimeUtils.localDateTimeToStr(now, LocalDateTimeUtils.YYYY_MM_DD_HH_MM_SS));
        sceneModelTagCacheVO.setValue(value);
        sceneModelTagCache.addSceneModelTagValue(sceneModelTagVO.getSceneModelId(), sceneModelTagCacheVO);
        // 是否历史存储
        if (1 == sceneModelTagVO.getStorage()) {
            byte isMonitor = 0;
            byte mode = 3;
            DeviceLog deviceLog = new DeviceLog();
            deviceLog.setIdentify(sceneModelTagVO.getId().toString());
            deviceLog.setModelName(sceneModelTagVO.getName());
            deviceLog.setLogType(DeviceLogTypeEnum.SCENE_VARIABLE_REPORT.getType().byteValue());
            deviceLog.setLogValue(value);
            deviceLog.setIsMonitor(isMonitor);
            deviceLog.setMode(mode);
            deviceLog.setCreateTime(new Date());
            deviceLog.setCreateBy(sceneModelTagVO.getCreateBy());
            deviceLog.setTenantId(sceneModelTagVO.getTenantId());
            deviceLog.setUserId(sceneModelTagVO.getTenantId());
            logService.saveDeviceLog(deviceLog);
        }
        //发送至前端
        List<SceneModelTagCacheVO> sendMsg = new ArrayList<>();
        sendMsg.add(sceneModelTagCacheVO);
        PushMessageBo messageBo = new PushMessageBo();
        // /场景id/变量id/scene/report（对应变量标识）
        messageBo.setTopic(TopicsUtils.buildSceneReportTopic(sceneModelTagVO.getSceneModelId(), sceneModelTagVO.getSceneModelDeviceId()));
        messageBo.setMessage(JSON.toJSONString(sendMsg));
        remoteManager.pushCommon(messageBo);
    }


}
