package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.internal.LinkedTreeMap;
import com.yomahub.liteflow.builder.LiteFlowNodeBuilder;
import com.yomahub.liteflow.builder.el.LiteFlowChainELBuilder;
import com.yomahub.liteflow.flow.FlowBus;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.exception.job.TaskException;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.enums.DeviceJobTypeEnum;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.SceneConvert;
import com.fastbee.iot.convert.SceneScriptConvert;
import com.fastbee.iot.domain.*;
import com.fastbee.iot.mapper.AlertMapper;
import com.fastbee.iot.mapper.GroupMapper;
import com.fastbee.iot.mapper.SceneMapper;
import com.fastbee.iot.model.ScriptTemplate;
import com.fastbee.iot.model.vo.SceneScriptVO;
import com.fastbee.iot.model.vo.SceneTerminalUserVO;
import com.fastbee.iot.model.vo.SceneVO;
import com.fastbee.iot.service.*;
import com.fastbee.iot.util.LiteFlowConfig;
import com.fastbee.quartz.util.CronUtils;
import com.fastbee.rule.domain.RuleEl;
import com.fastbee.rule.parser.entity.node.Node;

/**
 * 场景联动Service业务层处理
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Service
public class SceneServiceImpl extends ServiceImpl<SceneMapper, Scene> implements ISceneService {

    private static final Logger log = LoggerFactory.getLogger(SceneServiceImpl.class);

    @Resource
    private RedisCache redisCache;

    @Resource
    private SceneMapper sceneMapper;

    @Resource
    private IDeviceJobService jobService;

    @Resource
    private IScriptService scriptService;

    @Resource
    private ISceneScriptService sceneScriptService;

    @Resource
    private ISceneDeviceService sceneDeviceService;

    @Resource
    private IAlertService alertService;

    @Resource
    private LiteFlowConfig liteFlowConfig;

    @Resource
    private GroupMapper groupMapper;

    @Value("${liteflow.rule-source-ext-data-map.applicationName}")
    private String applicationName;

    @Resource
    private AlertMapper alertMapper;

    @Override
    @Cacheable(cacheNames = "Scene", key = "#root.methodName + ':' + #sceneId", unless = "#result == null")
    public Scene selectScene(Long sceneId) {
        return sceneMapper.selectById(sceneId);
    }

    public Scene selectSceneByChainName(String chainName) {
        LambdaQueryWrapper<Scene> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(chainName), Scene::getChainName, chainName);
        return sceneMapper.selectOne(lqw);
    }

    /**
     * 修改场景联动
     *
     * @param sceneVO 场景联动
     * @return 结果
     */
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "Scene", key = "'selectScene:' + #sceneVO.getSceneId()")
    })
    @Transactional(rollbackFor = Exception.class)
    public int updateScene(SceneVO sceneVO) {
        if (sceneVO.getTriggers().isEmpty() || sceneVO.getActions().isEmpty()) {
            log.error("场景中至少包含一个触发器或者执行动作，否则规则启动报错！");
            return 0;
        }

        // 批量删除规则脚本
        scriptService.deleteRuleScriptBySceneIds(new Long[]{sceneVO.getSceneId()});
        // 批量删除场景脚本
        sceneScriptService.deleteSceneScriptBySceneIds(new Long[]{sceneVO.getSceneId()});
        // 批量删除场景设备
        sceneDeviceService.deleteSceneDeviceBySceneIds(new Long[]{sceneVO.getSceneId()});
        // 批量删除定时任务
        try {
            jobService.deleteJobBySceneIds(new Long[]{sceneVO.getSceneId()});
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        // 构建EL和场景数据
        List<Script> ruleScripts = new ArrayList<>();
        List<SceneScript> sceneScripts = new ArrayList<>();
        List<SceneDevice> sceneDevices = new ArrayList<>();
        String elData = buildElData(sceneVO, ruleScripts, sceneScripts, sceneDevices);

        sceneVO.setElData(elData);
        sceneVO.setUpdateTime(DateUtils.getNowDate());
        Scene scene = SceneConvert.INSTANCE.convertScene(sceneVO);
        sceneMapper.updateById(scene);
        // 保存规则脚本
        scriptService.insertRuleScriptList(ruleScripts);
        // 保存场景脚本
        sceneScriptService.insertSceneScriptList(sceneScripts);
        // 保存场景设备
        if (!sceneDevices.isEmpty()) {
            sceneDeviceService.insertSceneDeviceList(sceneDevices);
        }

        // 动态构建规则链和脚本组件到内存中
        dynamicBuildRule(ruleScripts, scene);
        return 1;
    }

    public void updateSceneStatusByView(Scene scene, RuleEl ruleEl, List<Node> triggerList) {
        scene.setUpdateBy("RuleEngineView");
        scene.setUpdateTime(DateUtils.getNowDate());
        if (!triggerList.isEmpty()) {
            try {
                jobService.deleteJobBySceneIds(new Long[]{ruleEl.getId()});
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }

        if (scene.getChainName() != null) {
            if (scene.getEnable() == 1) {
                LiteFlowChainELBuilder.createChain().setChainName(scene.getChainName()).setEL(scene.getElData()).build();
                processTriggerList(scene, ruleEl, triggerList);
            } else {
                if (FlowBus.getChain(scene.getChainName()) != null) {
                    FlowBus.removeChain(scene.getChainName());
                }
            }
        }
        sceneMapper.updateById(scene);
    }

    @Override
    public int updateSceneByView(Scene scene, RuleEl ruleEl, List<Node> triggerList) {
        scene.setUpdateBy("RuleEngineView");
        scene.setUpdateTime(DateUtils.getNowDate());
        sceneMapper.updateById(scene);

        // 批量删除场景设备
        sceneDeviceService.deleteSceneDeviceBySceneIds(new Long[]{scene.getSceneId()});

        try {
            jobService.deleteJobBySceneIds(new Long[]{scene.getSceneId()});
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        // 构建规则链
        LiteFlowChainELBuilder.createChain().setChainName(scene.getChainName()).setEL(scene.getElData()).build();
        // 处理触发条件
        processTriggerList(scene, ruleEl, triggerList);
        return 1;
    }

    private void processTriggerList(Scene scene, RuleEl ruleEl, List<Node> triggerList) {
        List<SceneDevice> sceneDevices = new ArrayList<>();
        for (Node node : triggerList) {
            switch (node.getType()) {
                case "customTrigger":
                    break;
                case "scheduledTrigger":
                    createSceneViewTask(scene, ruleEl, node);
                    break;
                case "productTrigger":
                case "devTrigger":
                    SceneDevice sceneDevice = new SceneDevice();
                    sceneDevice.setSceneId(scene.getSceneId());
                    LinkedTreeMap map = (LinkedTreeMap) node.getData();
                    // 产品触发无设备ID
                    if (map.containsKey("deviceId")) {
                        sceneDevice.setSerialNumber(map.get("deviceId").toString());
                    } else {
                        sceneDevice.setSerialNumber("");
                    }
                    // 产品触发和设备触发是插入触发表
                    if (map.containsKey("productId")) {
                        sceneDevice.setProductId(((Number) map.get("productId")).longValue());
                        sceneDevice.setScriptId("");
                        sceneDevice.setProductName("");
                        sceneDevice.setType(2);
                        sceneDevice.setSource(1);
                        sceneDevices.add(sceneDevice);
                    }
                    break;
                default:
                    break;
            }
        }
        if (!sceneDevices.isEmpty()) {
            sceneDeviceService.insertSceneDeviceList(sceneDevices);
        }
    }

    /**
     * 批量删除场景联动
     *
     * @param sceneIds 需要删除的场景联动主键
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = "Scene", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public int deleteSceneBySceneIds(Long[] sceneIds) {
        for (Long sceneId : sceneIds) {
            Scene scene = sceneMapper.selectById(sceneId);
            FlowBus.removeChain(scene.getChainName());
        }
        // 批量删除规则脚本
        scriptService.deleteRuleScriptBySceneIds(sceneIds);
        // 批量删除场景脚本
        sceneScriptService.deleteSceneScriptBySceneIds(sceneIds);
        // 批量删除场景设备
        sceneDeviceService.deleteSceneDeviceBySceneIds(sceneIds);
        // 批量删除定时任务
        try {
            jobService.deleteJobBySceneIds(sceneIds);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        // 删除场景关联的告警
        alertService.deleteAlertSceneBySceneIds(sceneIds);
        // 删除缓存静默时间
        for (Long sceneId : sceneIds) {
            String key = "silent:" + "scene_" + sceneId;
            redisCache.deleteObject(key);
        }
        return sceneMapper.deleteBatchIds(Arrays.asList(sceneIds));
    }

    /**
     * 删除场景联动信息
     *
     * @param sceneId 场景联动主键
     * @return 结果
     */
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "Scene", key = "'selectScene:' + #sceneId")
    })
    @Transactional(rollbackFor = Exception.class)
    public int deleteSceneBySceneId(Long sceneId) {
        Scene scene = sceneMapper.selectById(sceneId);
        if (scene != null) {
            FlowBus.removeChain(scene.getChainName());
        }
        // 批量删除规则脚本
        scriptService.deleteRuleScriptBySceneIds(new Long[]{sceneId});
        // 批量删除场景脚本
        sceneScriptService.deleteSceneScriptBySceneIds(new Long[]{sceneId});
        // 批量删除场景设备
        sceneDeviceService.deleteSceneDeviceBySceneIds(new Long[]{sceneId});
        // 批量删除定时任务
        try {
            jobService.deleteJobBySceneIds(new Long[]{sceneId});
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        // 删除告警关联的场景
        alertService.deleteAlertByAlertIds(new Long[]{sceneId});
        // 删除缓存静默时间
        String key = "silent:" + "scene_" + sceneId;
        redisCache.deleteObject(key);
        return sceneMapper.deleteById(sceneId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "Scene", key = "'selectScene:' + #scene.getSceneId()")
    })
    public int updateStatus(Scene scene) {
        Integer enable = scene.getEnable();
        // 更新定时任务状态
        List<DeviceJob> deviceJobList = jobService.listShortJobBySceneId(new Long[]{scene.getSceneId()});
        try {
            for (DeviceJob job : deviceJobList) {
                DeviceJob deviceJob = new DeviceJob();
                deviceJob.setJobId(job.getJobId());
                deviceJob.setJobGroup(job.getJobGroup());
                deviceJob.setStatus(enable == 1 ? 0 : 1);
                jobService.changeStatus(deviceJob);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Scene updateScene = new Scene();
        updateScene.setSceneId(scene.getSceneId());
        updateScene.setEnable(enable);
        return sceneMapper.updateById(updateScene);
    }

    /**
     * 查询场景联动
     *
     * @param param 场景联动
     * @return 场景联动
     */
    @Override
    @DataScope(fieldAlias = DataScopeAspect.DATA_SCOPE_FILED_ALIAS_USER_ID)
    public SceneVO selectSceneBySceneId(Scene param) {
        SysUser user = getLoginUser().getUser();
        LambdaQueryWrapper<Scene> queryWrapper = this.buildQueryWrapper(param);
        if (null != user.getDeptId()) {
            // 数据范围过滤
            if (ObjectUtil.isNotEmpty(param.getParams().get(DataScopeAspect.DATA_SCOPE))){
                queryWrapper.apply((String) param.getParams().get(DataScopeAspect.DATA_SCOPE));
            }
        } else {
            // 终端用户
            queryWrapper.eq(Scene::getUserId, user.getUserId());
        }
        // 查询场景
        Scene scene = this.getOne(queryWrapper);
        // 查询场景脚本
        SceneScript scriptParam = new SceneScript();
        scriptParam.setSceneId(scene.getSceneId());
        List<SceneScript> sceneScripts = sceneScriptService.selectSceneScriptList(scriptParam);
        List<SceneScriptVO> sceneScriptVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sceneScripts)) {
            sceneScriptVOList = SceneScriptConvert.INSTANCE.convertSceneScriptVOList(sceneScripts);
        }
        // 查询场景设备
        SceneDevice deviceParam = new SceneDevice();
        deviceParam.setSceneId(scene.getSceneId());
        List<SceneDevice> sceneDevices = sceneDeviceService.selectSceneDeviceList(deviceParam);
        // 构建场景设备列表，触发器和执行动作集合
        List<SceneScriptVO> triggers = new ArrayList<>();
        List<SceneScriptVO> actions = new ArrayList<>();
        for (SceneScriptVO sceneScriptVO : sceneScriptVOList) {
            // 构建设备列表
            List<String> deviceList = new ArrayList<>();
            for (SceneDevice sceneDevice : sceneDevices) {
                if (sceneDevice.getScriptId().equals(sceneScriptVO.getScriptId())) {
                    deviceList.add(sceneDevice.getSerialNumber());
                }
            }
            if (null != sceneScriptVO.getGroupId()) {
                Group group = groupMapper.selectById(sceneScriptVO.getGroupId());
                sceneScriptVO.setGroupName(group.getGroupName());
            }
            sceneScriptVO.setDeviceNums(deviceList.toArray(new String[deviceList.size()]));
            if (sceneScriptVO.getScriptPurpose() == 2) {
                triggers.add(sceneScriptVO);
            } else if (sceneScriptVO.getScriptPurpose() == 3) {
                actions.add(sceneScriptVO);
            }
        }
        SceneVO sceneVO = SceneConvert.INSTANCE.convertSceneVO(scene);
        sceneVO.setTriggers(triggers);
        sceneVO.setActions(actions);
        return sceneVO;
    }

    @Override
    @DataScope(fieldAlias = DataScopeAspect.DATA_SCOPE_FILED_ALIAS_USER_ID)
    public Page<SceneVO> pageSceneVO(Scene scene) {
        SysUser user = getLoginUser().getUser();
        LambdaQueryWrapper<Scene> queryWrapper = this.buildQueryWrapper(scene);
        if (null != user.getDeptId()) {
            // 数据范围过滤
            if (ObjectUtil.isNotEmpty(scene.getParams().get(DataScopeAspect.DATA_SCOPE))) {
                queryWrapper.apply((String) scene.getParams().get(DataScopeAspect.DATA_SCOPE));
            }
        } else {
            // 终端用户
            queryWrapper.eq(Scene::getUserId, user.getUserId());
        }
        queryWrapper.orderByDesc(Scene::getCreateTime);
        Page<Scene> scenePage = baseMapper.selectPage(new Page<>(scene.getPageNum(), scene.getPageSize()), queryWrapper);
        if (0 == scenePage.getTotal()) {
            return new Page<>();
        }
        Page<SceneVO> voPage = SceneConvert.INSTANCE.convertSceneVOPage(scenePage);
        // 过滤掉V开头的ChainName条目
        if (null == scene.getHasAlert()) {
            List<SceneVO> sceneVOList = voPage.getRecords().stream().filter(sceneVO -> sceneVO.getChainName().startsWith("C") && sceneVO.getChainName().length() == 20).collect(Collectors.toList());
            voPage.setRecords(sceneVOList);
        }
        List<Long> sceneIdList = voPage.getRecords().stream().map(SceneVO::getSceneId).collect(Collectors.toList());
        List<SceneScript> sceneScriptList = sceneScriptService.listSceneScriptByPurpose(sceneIdList, 3);
        Map<Long, List<SceneScript>> map = sceneScriptList.stream().collect(Collectors.groupingBy(SceneScript::getSceneId));
        for (SceneVO updateScene : voPage.getRecords()) {
            List<SceneScript> sceneScripts = map.get(updateScene.getSceneId());
            if (CollectionUtils.isNotEmpty(sceneScripts)) {
                updateScene.setActionCount(sceneScripts.size());
            }
        }
        return voPage;
    }

    /**
     * 查询场景联动列表
     *
     * @param scene 场景联动
     * @return 场景联动
     */
    @Override
    public List<SceneVO> selectSceneVOList(Scene scene) {
        SysUser user = getLoginUser().getUser();
        if (null != user.getDeptId()) {
            // 查询所属机构
            scene.setUserId(user.getDept().getDeptUserId());
        } else {
            // 终端用户
            scene.setUserId(user.getUserId());
        }
        LambdaQueryWrapper<Scene> queryWrapper = this.buildQueryWrapper(scene);
        List<Scene> scenes = baseMapper.selectList(queryWrapper);
        return SceneConvert.INSTANCE.convertSceneVOList(scenes);
    }

    /**
     * 新增场景联动
     *
     * @param sceneVO 场景联动
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertScene(SceneVO sceneVO) {
        if (sceneVO.getTriggers().isEmpty() || sceneVO.getActions().isEmpty()) {
            log.error("场景中至少包含一个触发器或者执行动作，否则规则启动报错！");
            return 0;
        }

        // 保存场景联动
        SysUser user = getLoginUser().getUser();
        sceneVO.setCreateBy(user.getUserName());
        sceneVO.setCreateTime(DateUtils.getNowDate());
        // 延时不超过90秒
        if (sceneVO.getExecuteDelay() > 90) {
            sceneVO.setExecuteDelay(90);
        }
        // 设置规则名称,D=数据流，A=执行动作，T=触发器,C=规则链 雪花算法生成唯一数
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        sceneVO.setChainName("C" + String.valueOf(snowflake.nextId()));
        Scene scene = SceneConvert.INSTANCE.convertScene(sceneVO);
        // 新增场景绑定告警
        int insert = sceneMapper.insert(scene);
        List<Long> alertIds = sceneVO.getAlertIds();
        List<AlertScene> alertScenes = new ArrayList<>();
        if (insert > 0 && alertIds != null && !alertIds.isEmpty()) {
            for (Long alertId : alertIds) {
                    Alert alert = alertService.getById(alertId);
                    if (alert != null) {
                        AlertScene alertScene = new AlertScene();
                        alertScene.setSceneId(scene.getSceneId());
                        alertScene.setAlertId(alertId);
                        alertScenes.add(alertScene);
                    }
            }
            alertMapper.insertAlertSceneList(alertScenes);
        }

        // 构建EL和场景数据
        List<Script> ruleScripts = new ArrayList<>();
        List<SceneScript> sceneScripts = new ArrayList<>();
        List<SceneDevice> sceneDevices = new ArrayList<>();
        sceneVO.setSceneId(scene.getSceneId());
        String elData = buildElData(sceneVO, ruleScripts, sceneScripts, sceneDevices);

        // 更新场景联动规则脚本
        scene.setElData(elData);
        sceneMapper.updateById(scene);
        // 保存规则脚本
        if (!ruleScripts.isEmpty()) {
            scriptService.insertRuleScriptList(ruleScripts);
        }
        // 保存场景脚本
        if (!sceneScripts.isEmpty()) {
            sceneScriptService.insertSceneScriptList(sceneScripts);
        }
        // 保存场景设备
        if (!sceneDevices.isEmpty()) {
            sceneDeviceService.insertSceneDeviceList(sceneDevices);
        }

        // 动态构建规则链和脚本组件到内存中
        dynamicBuildRule(ruleScripts, scene);
        return 1;
    }

    @Override
    public int insertSceneByView(Scene scene, RuleEl ruleEl, List<Node> triggerList) {
        scene.setCreateBy("RuleEngineView");
        scene.setApplicationName(applicationName);
        scene.setCreateTime(DateUtils.getNowDate());
        sceneMapper.insert(scene);
        // 构建规则链
        LiteFlowChainELBuilder.createChain().setChainName(scene.getChainName()).setEL(scene.getElData()).build();
        // 处理触发条件
        processTriggerList(scene, ruleEl, triggerList);
        return 1;
    }

    /**
     * 动态构建规则链和脚本组件
     *
     * @param ruleScripts
     */
    private void dynamicBuildRule(List<Script> ruleScripts, Scene scene) {
        for (Script ruleScript : ruleScripts) {
            // 规则引擎构建脚本组件
            if (ruleScript.getScriptPurpose() == 2) {
                //脚本条件组件
                LiteFlowNodeBuilder.createScriptBooleanNode().setId(ruleScript.getScriptId())
                        .setName(ruleScript.getScriptName())
                        .setScript(ruleScript.getScriptData())
                        .build();
            } else if (ruleScript.getScriptPurpose() == 3) {
                // 普通组件
                LiteFlowNodeBuilder.createScriptNode().setId(ruleScript.getScriptId())
                        .setName(ruleScript.getScriptName())
                        .setScript(ruleScript.getScriptData())
                        .build();
            }
        }
        // 构建规则链
        LiteFlowChainELBuilder.createChain().setChainName(scene.getChainName()).setEL(scene.getElData()).build();
    }

    /**
     * 构建EL数据
     *
     * @param sceneVO      场景
     * @param ruleScripts  规则脚本集合
     * @param sceneScripts 场景脚本集合
     * @param sceneDevices 场景设备集合
     * @return
     */
    private String buildElData(SceneVO sceneVO, List<Script> ruleScripts, List<SceneScript> sceneScripts, List<SceneDevice> sceneDevices) {
        // 排除定时后的触发器，等于0不生成规则数据，等于1移除AND和OR
        Long triggerNodeCount = sceneVO.getTriggers().stream().filter(x -> x.getSource() != 2 && x.getSource() != 4).count();
        Long triggerTimingCount = sceneVO.getTriggers().stream().filter(x -> x.getSource() == 2).count();
        Long selftriggerCount = sceneVO.getTriggers().stream().filter(x -> x.getSource() == 4).count();

        // 拼接规则数据，格式如：IF(AND(T1,T2,T3),THEN(A1,A2,A3))
        StringBuilder triggerBuilder = new StringBuilder();
        StringBuilder actionBuilder = new StringBuilder();
        if (0 == triggerNodeCount && (triggerTimingCount != 0 || selftriggerCount != 0)) {
            switch (sceneVO.getExecuteMode()) {
                case 1:
                    actionBuilder.append("THEN(");
                    break;
                case 2:
                    actionBuilder.append("WHEN(");
                    break;
                default:
                    break;
            }
        } else {
            switch (sceneVO.getCond()) {
                case 1:
                    triggerBuilder.append("IF(OR(");
                    break;
                case 2:
                    triggerBuilder.append("IF(AND(");
                    break;
                case 3:
                    triggerBuilder.append("IF(NOT(");
                    break;
                default:
                    break;
            }
            switch (sceneVO.getExecuteMode()) {
                case 1:
                    actionBuilder.append(",THEN(");
                    break;
                case 2:
                    actionBuilder.append(",WHEN(");
                    break;
                default:
                    break;
            }
        }

        for (int i = 0; i < sceneVO.getTriggers().size(); i++) {
            // 保存触发器和执行动作
            String scriptId = buildTriggerAction(sceneVO.getTriggers().get(i), 2, sceneVO, ruleScripts, sceneScripts, sceneDevices);
            // 构建触发器EL，排除定时触发器
            if (sceneVO.getTriggers().get(i).getSource() != 2 && sceneVO.getTriggers().get(i).getSource() != 4) {
                triggerBuilder.append(scriptId + ",");
            }
        }
        if (triggerNodeCount > 0) {
            triggerBuilder.deleteCharAt(triggerBuilder.lastIndexOf(","));
            triggerBuilder.append(")");
        }

        for (int i = 0; i < sceneVO.getActions().size(); i++) {
            // 保存触发器和执行动作
            String scriptId = buildTriggerAction(sceneVO.getActions().get(i), 3, sceneVO, ruleScripts, sceneScripts, sceneDevices);
            // 构建执行动作EL
            actionBuilder.append(scriptId + ",");
        }
        if (sceneVO.getActions().size() > 0) {
            actionBuilder.deleteCharAt(actionBuilder.lastIndexOf(","));
        }
        String elData;
        if (StringUtils.isEmpty(triggerBuilder)) {
            actionBuilder.append(")");
            elData = actionBuilder.toString();
        } else {
            actionBuilder.append("))");
            elData = triggerBuilder.append(actionBuilder).toString();
        }
        if (triggerNodeCount == 1) {
            // 移除AND和OR，它们必须包含两个以上组件
            if (elData.indexOf("AND(") != -1) {
                elData = elData.replace("AND(", "").replace("),", ",");
            } else if (elData.indexOf("OR(") != -1) {
                elData = elData.replace("OR(", "").replace("),", ",");
            }
        }
        return elData;
    }

    /**
     * 构建场景中的触发器和执行动作
     *
     * @param sceneScriptVO 场景脚本
     * @param scriptPurpose 脚本用途：1=数据流，2=触发器，3=执行动作
     * @param sceneVO       场景
     * @return 返回规则脚本ID
     */
    private String buildTriggerAction(SceneScriptVO sceneScriptVO, int scriptPurpose, SceneVO sceneVO, List<Script> ruleScripts, List<SceneScript> sceneScripts, List<SceneDevice> sceneDevices) {
        // 构建规则脚本
        Script ruleScript = new Script();
        // 设置脚本标识,D=数据流，A=执行动作，T=触发器,雪花算法生成唯一数
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        String scriptId = String.valueOf(snowflake.nextId());
        if (scriptPurpose == 2) {
            scriptId = "T" + scriptId;
            ruleScript.setScriptType("boolean_script");
        } else if (scriptPurpose == 3) {
            scriptId = "A" + scriptId;
            ruleScript.setScriptType("script");
        }
        ruleScript.setScriptId(scriptId);
        ruleScript.setScriptName(scriptId);
        ruleScript.setApplicationName(liteFlowConfig.getApplicationName());
        ruleScript.setScriptLanguage("groovy");
        SysUser user = getLoginUser().getUser();
        if (null != user.getDeptId()) {
            ruleScript.setUserId(user.getDept().getDeptUserId());
            ruleScript.setUserName(user.getDept().getDeptUserName());
        } else {
            ruleScript.setUserId(user.getUserId());
            ruleScript.setUserName(user.getUserName());
        }
        ruleScript.setScriptPurpose(scriptPurpose);
        ruleScript.setEnable(1);
        ruleScript.setScriptOrder(0);
        ruleScript.setScriptEvent(0);
        ruleScript.setScriptAction(0);
        ruleScript.setSceneId(sceneVO.getSceneId());
        ruleScript.setCreateBy(user.getUserName());
        ruleScript.setCreateTime(DateUtils.getNowDate());
        // 构建脚本内容
        ScriptTemplate template = new ScriptTemplate(
                sceneVO.getSceneId(), sceneVO.getChainName(), sceneVO.getRecoverId(), scriptId, sceneScriptVO.getType(), sceneScriptVO.getSource(), sceneVO.getSilentPeriod(),
                sceneVO.getExecuteDelay(), sceneVO.getCheckDelay(), sceneVO.getCond(), sceneScriptVO.getScriptPurpose(), sceneScriptVO.getId(), sceneScriptVO.getValue(),
                StringUtils.isEmpty(sceneScriptVO.getOperator()) ? "=" : sceneScriptVO.getOperator(), String.join(",", sceneScriptVO.getDeviceNums()), sceneScriptVO.getProductId(),
                sceneScriptVO.getNotifyCount()
        );
        String data = String.format("String json =\"%s\";\n" +
                "sceneContext.process(json);", StringEscapeUtils.escapeJava(JSONObject.toJSONString(template)));
        ruleScript.setScriptData(data);

        if (scriptPurpose == 2) {
            // 构建脚本集合,规则脚本不需要存储定时触发器
            if (sceneScriptVO.getSource() != 2) {
                ruleScripts.add(ruleScript);
            }
            // 触发器
            if (sceneScriptVO.getSource() == 1 || 5 == sceneScriptVO.getSource()) {
                // 构建场景设备集合
                for (int j = 0; j < sceneScriptVO.getDeviceNums().length; j++) {
                    SceneDevice sceneDevice = new SceneDevice();
                    sceneDevice.setSceneId(sceneVO.getSceneId());
                    sceneDevice.setScriptId(scriptId);
                    sceneDevice.setSource(sceneScriptVO.getSource());
                    sceneDevice.setSerialNumber(sceneScriptVO.getDeviceNums()[j]);
                    sceneDevice.setProductId(0L);
                    sceneDevice.setProductName("");
                    sceneDevice.setType(scriptPurpose);
                    sceneDevices.add(sceneDevice);
                }
            } else if (sceneScriptVO.getSource() == 2) {
                // 创建告警定时任务
                createSceneTask(sceneVO, sceneScriptVO);
            } else if (sceneScriptVO.getSource() == 3) {
                // 构建场景设备集合
                SceneDevice sceneDevice = new SceneDevice();
                sceneDevice.setSceneId(sceneVO.getSceneId());
                sceneDevice.setScriptId(scriptId);
                sceneDevice.setSource(sceneScriptVO.getSource());
                sceneDevice.setSerialNumber("");
                sceneDevice.setProductId(sceneScriptVO.getProductId());
                sceneDevice.setProductName(sceneScriptVO.getProductName());
                sceneDevice.setType(scriptPurpose);
                sceneDevices.add(sceneDevice);
            }
        } else if (scriptPurpose == 3) {
            ruleScripts.add(ruleScript);
            if (sceneScriptVO.getSource() == 1 || 6 == sceneScriptVO.getSource()) {
                // 构建场景设备集合
                for (int j = 0; j < sceneScriptVO.getDeviceNums().length; j++) {
                    SceneDevice sceneDevice = new SceneDevice();
                    sceneDevice.setSceneId(sceneVO.getSceneId());
                    sceneDevice.setScriptId(scriptId);
                    sceneDevice.setSource(sceneScriptVO.getSource());
                    sceneDevice.setSerialNumber(sceneScriptVO.getDeviceNums()[j]);
                    sceneDevice.setProductId(0L);
                    sceneDevice.setProductName("");
                    sceneDevice.setType(scriptPurpose);
                    sceneDevices.add(sceneDevice);
                }
            } else if (sceneScriptVO.getSource() == 3) {
                // 构建场景设备集合
                SceneDevice sceneDevice = new SceneDevice();
                sceneDevice.setSceneId(sceneVO.getSceneId());
                sceneDevice.setScriptId(scriptId);
                sceneDevice.setSource(sceneScriptVO.getSource());
                sceneDevice.setSerialNumber("");
                sceneDevice.setProductId(sceneScriptVO.getProductId());
                sceneDevice.setProductName(sceneScriptVO.getProductName());
                sceneDevice.setType(scriptPurpose);
                sceneDevices.add(sceneDevice);
            }
        }

        // 构建场景脚本集合
        sceneScriptVO.setSceneId(sceneVO.getSceneId());
        sceneScriptVO.setScriptId(scriptId);
        sceneScriptVO.setCreateTime(DateUtils.getNowDate());
        sceneScriptVO.setCreateBy(user.getUserName());
        sceneScriptVO.setOperator(StringUtils.isEmpty(sceneScriptVO.getOperator()) ? "=" : sceneScriptVO.getOperator());
        sceneScripts.add(SceneScriptConvert.INSTANCE.convertSceneScript(sceneScriptVO));
        // 返回脚本ID
        return scriptId;
    }

    /**
     * 创建场景定时任务
     *
     * @param sceneVO
     * @param sceneScriptVO
     */
    private void createSceneTask(SceneVO sceneVO, SceneScriptVO sceneScriptVO) {
        // 创建定时任务
        try {
            if (!CronUtils.isValid(sceneScriptVO.getCronExpression())) {
                log.error("新增场景联动定时任务失败，Cron表达式不正确");
                throw new Exception("新增场景联动定时任务失败，Cron表达式不正确");
            }
            DeviceJob deviceJob = new DeviceJob();
            deviceJob.setJobName(DeviceJobTypeEnum.SCENE.getDesc());
            deviceJob.setJobType(DeviceJobTypeEnum.SCENE.getType());
            deviceJob.setJobGroup("DEFAULT");
            deviceJob.setConcurrent("1");
            deviceJob.setMisfirePolicy("2");
            deviceJob.setStatus(sceneVO.getEnable() == 1 ? 0 : 1);
            deviceJob.setCronExpression(sceneScriptVO.getCronExpression());
            deviceJob.setIsAdvance(sceneScriptVO.getIsAdvance());
            deviceJob.setSceneId(sceneVO.getSceneId());
            deviceJob.setDeviceName("场景联动定时触发");
            deviceJob.setCreateBy(sceneVO.getCreateBy());
            jobService.insertJob(deviceJob);
        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (TaskException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSceneViewTask(Scene scene, RuleEl ruleEl, Node node) {
        // 创建定时任务
        try {
            LinkedTreeMap map = (LinkedTreeMap) node.getData();
            String cronExpression = map.get("cronExpression").toString();
            if (!CronUtils.isValid(cronExpression)) {
                log.error("新增场景联动定时任务失败，Cron表达式不正确");
                throw new Exception("新增场景联动定时任务失败，Cron表达式不正确");
            }
            DeviceJob deviceJob = new DeviceJob();
            deviceJob.setJobName(DeviceJobTypeEnum.RULE_VIEW.getDesc());
            deviceJob.setJobType(DeviceJobTypeEnum.RULE_VIEW.getType());
            deviceJob.setJobGroup("DEFAULT");
            deviceJob.setConcurrent("1");
            deviceJob.setMisfirePolicy("2");
            deviceJob.setStatus(scene.getEnable() == 1 ? 0 : 1);
            deviceJob.setCronExpression(cronExpression);
            if (map.containsKey("deviceId")) {
                Integer isAdvance = ((Number) map.get("isAdvance")).intValue();
                deviceJob.setIsAdvance(isAdvance);
            }
            deviceJob.setSceneId(ruleEl.getId());
            deviceJob.setDeviceName(DeviceJobTypeEnum.RULE_VIEW.getDesc());
            deviceJob.setCreateBy(scene.getCreateBy());
            jobService.insertJob(deviceJob);
        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (TaskException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<SceneTerminalUserVO> selectTerminalUserBySceneIds(Set<Long> sceneIdSet) {
        return sceneMapper.selectTerminalUserBySceneIds(sceneIdSet);
    }

    private LambdaQueryWrapper<Scene> buildQueryWrapper(Scene query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<Scene> lqw = Wrappers.lambdaQuery();
        lqw.eq(null != query.getSceneId(), Scene::getSceneId, query.getSceneId());
        lqw.like(StringUtils.isNotBlank(query.getSceneName()), Scene::getSceneName, query.getSceneName());
        lqw.like(StringUtils.isNotBlank(query.getChainName()), Scene::getChainName, query.getChainName());
        lqw.eq(query.getEnable() != null, Scene::getEnable, query.getEnable());
        lqw.eq(query.getUserId() != null, Scene::getUserId, query.getUserId());
        lqw.like(StringUtils.isNotBlank(query.getUserName()), Scene::getUserName, query.getUserName());
        lqw.eq(query.getSilentPeriod() != null, Scene::getSilentPeriod, query.getSilentPeriod());
        lqw.eq(query.getCond() != null, Scene::getCond, query.getCond());
        lqw.eq(query.getExecuteMode() != null, Scene::getExecuteMode, query.getExecuteMode());
        lqw.eq(query.getExecuteDelay() != null, Scene::getExecuteDelay, query.getExecuteDelay());
        lqw.eq(query.getHasAlert() != null, Scene::getHasAlert, query.getHasAlert());
        lqw.like(StringUtils.isNotBlank(query.getApplicationName()), Scene::getApplicationName, query.getApplicationName());
        lqw.eq(StringUtils.isNotBlank(query.getElData()), Scene::getElData, query.getElData());
        lqw.eq(query.getTerminalUser() != null, Scene::getTerminalUser, query.getTerminalUser());
        lqw.eq(query.getCheckDelay() != null, Scene::getCheckDelay, query.getCheckDelay());
        lqw.eq(query.getRecoverId() != null, Scene::getRecoverId, query.getRecoverId());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(Scene::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Scene entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 根据场景查询绑定的告警配置列表
     */
    @Override
    public Page<Alert> selectAlarmConfigsByScene(SceneVO scene) {
        return alertService.listAlertByScene(scene);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int bindAlertToScene(Long sceneId, List<Long> alertIds) {
        if (sceneId == null || alertIds == null || alertIds.isEmpty()) {
            return 0;
        }
        // 获取已存在的关联关系，避免重复插入
        List<AlertScene> existingAlertScenes = alertService.listAlertScene(sceneId);
        Set<Long> existingAlertIds = existingAlertScenes.stream()
                .map(AlertScene::getAlertId)
                .collect(Collectors.toSet());

        // 构建告警场景关联列表，排除已存在的关联
        List<AlertScene> alertScenes = new ArrayList<>();
        for (Long alertId : alertIds) {
            // 检查告警是否存在且不在当前关联列表中
            if (!existingAlertIds.contains(alertId)) {
                Alert alert = alertService.getById(alertId);
                if (alert != null) {
                    AlertScene alertScene = new AlertScene();
                    alertScene.setSceneId(sceneId);
                    alertScene.setAlertId(alertId);
                    alertScenes.add(alertScene);
                }
            }
        }

        if (alertScenes.isEmpty()) {
            return 0;
        }

        // 批量插入告警场景关联记录
        return alertMapper.insertAlertSceneList(alertScenes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unbindAlertFromScene(Long sceneId, List<Long> alertIds) {
        if (sceneId == null || alertIds == null || alertIds.isEmpty()) {
            return 0;
        }

        return alertMapper.deleteAlertSceneBySceneAndAlertIds(sceneId, alertIds);
    }

}
