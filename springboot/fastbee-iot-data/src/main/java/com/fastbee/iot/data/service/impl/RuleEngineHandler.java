package com.fastbee.iot.data.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fastbee.common.extend.core.domin.mq.SubDeviceBo;
import com.fastbee.common.extend.core.domin.mq.message.ReportDataBo;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.common.extend.enums.ScriptEventEnum;
import com.fastbee.iot.data.ruleEngine.SceneContext;
import com.fastbee.iot.data.service.IRuleEngine;
import com.fastbee.iot.domain.*;
import com.fastbee.iot.mapper.SceneMapper;
import com.fastbee.iot.model.MatchScenes;
import com.fastbee.iot.service.IRuleElService;
import com.fastbee.iot.service.ISceneDeviceService;
import com.fastbee.iot.service.IScriptService;
import com.fastbee.rule.context.BaseContext;
import com.fastbee.rule.context.MsgContext;
import com.fastbee.rule.core.FlowLogExecutor;

/**
 * 规则引擎处理数据方法
 *
 * @author bill
 */
@Component
@Slf4j
public class RuleEngineHandler implements IRuleEngine {

    @Resource
    private ISceneDeviceService sceneDeviceService;

    @Autowired
    private FlowLogExecutor flowExecutor;

    @Resource
    private SceneMapper sceneMapper;

    @Resource
    private IScriptService scriptService;

    @Resource
    private IRuleElService ruleElService;

    private static final Map<String, ScheduledExecutorService> CEHCK_CACHE = new ConcurrentHashMap<>();

    /**
     * 规则匹配(告警和场景联动)
     *
     * @param bo 上报数据模型bo
     * @see ReportDataBo
     */
    public void ruleMatch(ReportDataBo bo) {
        try {
            // 场景联动处理
            this.sceneProcess(bo);
            // 脚本规则处理  上报数据在编码前处理 这里不重新处理
            if(bo.getType() != 1) {
                scriptMatch(bo);
            }
            // 判断是否有匹配的可视化规则要执行
        } catch (Exception e) {
            log.error("场景联动规则执行异常 message={}", e, e.getMessage());
        }
    }

    public MsgContext scriptMatch(ReportDataBo bo) {
        try {
            // 判断是否有匹配的脚本要执行
            Integer event = 0;
            // ReportDataBo type:1=属性，2=功能，3=事件，4=设备升级，5=设备上线，6=设备下线
            switch (bo.getType()) {
                case 1:
                case 2:
                case 3:
                    event = ScriptEventEnum.DEVICE_REPORT.getType();
                    break;
                case 5:
                    event = ScriptEventEnum.DEVICE_ONLINE.getType();
                    break;
                case 6:
                    event = ScriptEventEnum.DEVICE_OFFLINE.getType();
                    break;
            }
            return scriptService.processRuleScript(bo.getSerialNumber(), event, bo.getTopic(), bo.getPayload());
        } catch (Exception e) {
            log.error("场景联动规则执行异常 message={}", e, e.getMessage());
        }
        return null;
    }

    /**
     * 场景规则处理
     */
    public void sceneProcess(ReportDataBo bo) {
        List<SubDeviceBo> subDeviceBoList = bo.getSubDeviceBoList();

        if (CollectionUtils.isEmpty(subDeviceBoList)) {
            List<ThingsModelSimpleItem> thingsModelSimpleItems = bo.getDataList();
            if (CollectionUtils.isEmpty(bo.getDataList())) {
                thingsModelSimpleItems = JSON.parseArray(bo.getMessage(), ThingsModelSimpleItem.class);
            }
            processScene(bo.getSerialNumber(), bo.getProductId(), thingsModelSimpleItems, bo.getType());
        } else {
            for (SubDeviceBo subDevice : subDeviceBoList) {
                processScene(subDevice.getSubClientId(), subDevice.getSubProductId(),
                        subDevice.getThingsModelSimpleItem(), bo.getType());
            }
        }
    }

    private void processScene(String serialNumber, Long productId, List<ThingsModelSimpleItem> thingsModelSimpleItems,
                               int type) {
        if (CollectionUtils.isEmpty(thingsModelSimpleItems) && type != 5 && type != 6) {
            return;
        }
        SceneDevice sceneDeviceParam = new SceneDevice();
        sceneDeviceParam.setProductId(productId);
        sceneDeviceParam.setSerialNumber(serialNumber);
        List<MatchScenes> sceneList = sceneDeviceService.selectTriggerDeviceRelateScenes(sceneDeviceParam);
        for (MatchScenes scene : sceneList) {
            SceneContext context = new SceneContext(serialNumber, productId, type, thingsModelSimpleItems, scene.getSceneId());
            if (scene.getChainName().startsWith("V")) {
                context.setRuleId(scene.getRuleId());
                ruleElService.execByid(scene.getRuleId(), context);
            } else {
                String requestId = "scene/" + scene.getChainName();
                flowExecutor.execute2FutureWithRid(scene.getChainName(), requestId, context);
            }
        }
    }

    public void addCheckTask(String key, ScheduledExecutorService task) {
        ScheduledExecutorService oldTask = CEHCK_CACHE.get(key);
        if (oldTask != null) {
            try {
                oldTask.shutdownNow();
            } catch (SecurityException e) {
                // 处理可能的权限异常
                log.error("Failed to shutdown old task for key: " + key);
            }
        }
        CEHCK_CACHE.put(key, task);
    }

    public void removeCheckTask(String key) {
        ScheduledExecutorService oldTask = CEHCK_CACHE.get(key);
        if (oldTask != null) {
            try {
                oldTask.shutdownNow();
                CEHCK_CACHE.remove(key);
            } catch (SecurityException e) {
                // 处理可能的权限异常
                log.error("Failed to shutdown task for key: " + key);
            }
        }
    }

    @Override
    public void ruleMatchBySceneId(Long sceneId) {
        Scene scene = sceneMapper.selectById(sceneId);
        if (Objects.isNull(scene)) {
            System.out.println("------------------[为查询到场景联动，场景联动id：" + sceneId + "]---------------------");
            return;
        }
        // 执行场景规则,异步非阻塞
        BaseContext context = new BaseContext();
        context.setRuleId(sceneId);
        String requestId = "scene/" + scene.getChainName();
        flowExecutor.execute2FutureWithRid(scene.getChainName(), requestId, context);
    }


}
