package com.fastbee.iot.service.impl;

import static com.fastbee.common.core.domain.AjaxResult.success;
import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;
import static com.fastbee.common.extend.utils.SecurityUtils.getUsername;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import ch.qos.logback.classic.LoggerContext;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.internal.LinkedTreeMap;
import com.yomahub.liteflow.builder.LiteFlowNodeBuilder;
import com.yomahub.liteflow.flow.FlowBus;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.script.ScriptExecutorFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.enums.ScriptEventEnum;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.convert.ScriptConvert;
import com.fastbee.iot.domain.Script;
import com.fastbee.iot.enums.ScriptActionType;
import com.fastbee.iot.enums.ScriptPurposeType;
import com.fastbee.iot.mapper.ScriptMapper;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.ScriptCondition;
import com.fastbee.iot.model.vo.ScriptVO;
import com.fastbee.iot.service.IScriptService;
import com.fastbee.mqttclient.PubMqttClient;
import com.fastbee.rule.context.MsgContext;
import com.fastbee.rule.core.FlowLogExecutor;
import com.fastbee.rule.parser.entity.node.Node;
import com.fastbee.system.domain.SysConfig;
import com.fastbee.system.service.ISysConfigService;

/**
 * 规则引擎脚本Service业务层处理
 *
 * @author lizhuangpeng
 * @date 2023-07-01
 */
@Slf4j
@Service
public class ScriptServiceImpl extends ServiceImpl<ScriptMapper, Script> implements IScriptService {

    @Resource
    private ScriptMapper ruleScriptMapper;

    @Resource
    private FlowLogExecutor flowLogExecutor;

    @Resource
    private PubMqttClient mqttClient;

    private Set<String> allowPackages = new HashSet<>();

    @Value("${liteflow.rule-source-ext-data-map.applicationName}")
    private String applicationName;

    @Resource
    private IDeviceCache deviceCache;

    @Resource
    private ISysConfigService sysConfigService;

    @PostConstruct
    public void init() {
        String allowPackagesStr = sysConfigService.selectConfigByKey("sys.rule.allowPackages");
        if (null != allowPackagesStr) {
            String[] packages = allowPackagesStr.split("\r\n");
            Collections.addAll(allowPackages, packages);
        }
    }

    /**
     * 查询规则引擎脚本
     *
     * @param script 规则引擎脚本
     * @return 规则引擎脚本
     */
    @Override
    @DataScope(deptAlias = "s", userAlias = "s", fieldAlias = DataScopeAspect.DATA_SCOPE_FILED_ALIAS_USER_ID)
    public ScriptVO selectRuleScriptById(Script script) {
        return ruleScriptMapper.selectRuleScriptById(script);
    }

    /**
     * 查询规则引擎脚本日志
     *
     * @param id 规则引擎脚本主键
     * @return 规则引擎脚本
     */
    @Override
    public String selectRuleScriptLog(String type, String id) {
        // 获取日志存储路径
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        String path = loggerContext.getProperty("log.path");

        // 倒叙读取500条日志
        try {
            List<String> lines = new ArrayList<>();
            ReversedLinesFileReader reader = new ReversedLinesFileReader(new File(path + "/rule/" + type + ".log"));
            String line = "";
            while ((line = reader.readLine()) != null && lines.size() < 500) {
                String requestId = type + "/" + id;
                if (line.contains(requestId)) {
                    lines.add(line);
                }
            }
            Collections.reverse(lines);
            return String.join("\n", lines);
        } catch (IOException e) {
            return "暂无日志,详情如下：\n" + e.toString();
        }
    }

    /**
     * 查询规则引擎脚本列表
     *
     * @param ruleScript 规则引擎脚本
     * @return 规则引擎脚本
     */
    @Override
    @DataScope(deptAlias = "s", userAlias = "s", fieldAlias = DataScopeAspect.DATA_SCOPE_FILED_ALIAS_USER_ID)
    public Page<ScriptVO> selectRuleScriptList(Script ruleScript) {
        return ruleScriptMapper.selectRuleScriptList(new Page<>(ruleScript.getPageNum(), ruleScript.getPageSize()), ruleScript);
    }

    @Override
    public List<Script> selectExecRuleScriptList(ScriptCondition ruleScript) {
        return ruleScriptMapper.selectExecRuleScriptList(ruleScript);
    }

    /**
     * 查询规则引擎脚本标识数组（设备用户和租户的脚本）
     *
     * @return 规则引擎脚本
     */
    @Override
    public String[] selectRuleScriptIdArray(ScriptCondition scriptCondition) {
        return ruleScriptMapper.selectRuleScriptIdArray(scriptCondition);
    }

    /**
     * 新增规则引擎脚本
     *
     * @param scriptVO 规则引擎脚本
     * @return 结果
     */
    @Override
    public int insertRuleScript(ScriptVO scriptVO) {
        // 脚本中引用包替换为许可的包
        scriptVO.setScriptData(replaceAllowPackage(scriptVO.getScriptData()));
        // 设置脚本标识,D=数据流，A=执行动作，T=触发器,雪花算法生成唯一数
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        scriptVO.setScriptId("D" + snowflake.nextId());
        SysUser sysUser = getLoginUser().getUser();
        // 归属为机构管理员
        if (null != sysUser.getDeptId()) {
            scriptVO.setUserId(sysUser.getDept().getDeptUserId());
            scriptVO.setUserName(sysUser.getDept().getDeptName());
        } else {
            scriptVO.setUserId(sysUser.getUserId());
            scriptVO.setUserName(sysUser.getUserName());
        }
        scriptVO.setCreateBy(sysUser.getUserName());
        scriptVO.setCreateTime(DateUtils.getNowDate());
        Script script = ScriptConvert.INSTANCE.convertScript(scriptVO);
        int result = ruleScriptMapper.insert(script);
        // 动态刷新脚本
        if (result == 1) {
            LiteFlowNodeBuilder builder = null;
            if (scriptVO.getScriptType().equals("script")) {
                builder = LiteFlowNodeBuilder.createScriptNode();
            } else if (scriptVO.getScriptType().equals("switch_script")) {
                builder = LiteFlowNodeBuilder.createScriptSwitchNode();
            } else if (scriptVO.getScriptType().equals("boolean_script")) {
                builder = LiteFlowNodeBuilder.createScriptBooleanNode();
            } else if (scriptVO.getScriptType().equals("for_script")) {
                builder = LiteFlowNodeBuilder.createScriptForNode();
            }
            if (builder != null) {
                builder.setId(scriptVO.getScriptId())
                        .setName(scriptVO.getScriptName())
                        .setScript(scriptVO.getScriptData())
                        .build();
            }
        }
        if ((scriptVO.getScriptEvent() == 5 || scriptVO.getScriptEvent() == 6) && scriptVO.getBridgeId() != 0) {
            ruleScriptMapper.insertScriptBridge(scriptVO.getScriptId(), scriptVO.getBridgeId());
        }
        return result;
    }

    /**
     * 脚本中引用包替换为许可的包
     *
     * @return
     */
    private String replaceAllowPackage(String scriptData) {
        // 初始化白名单（应只执行一次，建议移到构造函数或静态初始化块中）
        if (allowPackages.isEmpty()) {
            allowPackages.add("import cn.hutool.json.JSONArray;");
            allowPackages.add("import cn.hutool.json.JSONObject;");
            allowPackages.add("import cn.hutool.json.JSONUtil;");
            allowPackages.add("import cn.hutool.core.util.NumberUtil;");
            allowPackages.add("import cn.hutool.core.util.HexUtil;");
        }

        // 分割脚本数据为行
        String[] lines = scriptData.split("\n");
        StringBuilder filteredScript = new StringBuilder();
        StringBuilder header = new StringBuilder();

        // 过滤掉不在白名单中的import语句
        for (String line : lines) {
            if (line.trim().startsWith("import ")) {
                // 只保留白名单中的import语句
                if (allowPackages.contains(line.trim())) {
                    header.append(line).append("\n");
                }
            } else {
                filteredScript.append(line).append("\n");
            }
        }
        return header + filteredScript.toString();
    }

    @Override
    public Set<String> addAllowPackage(String allowPackage) {
        String allowPackagesStr = sysConfigService.selectConfigByKey("sys.rule.allowPackages");
        allowPackagesStr += "\r\n" + allowPackage;
        SysConfig config = new SysConfig();
        config.setConfigKey("sys.rule.allowPackages");
        config.setConfigValue(allowPackagesStr);
        sysConfigService.updateConfigByKey(config);
        allowPackages.add(allowPackage);
        return allowPackages;
    }

    @Override
    public Set<String> removeAllowPackage(String allowPackage) {
        String allowPackagesStr = sysConfigService.selectConfigByKey("sys.rule.allowPackages");
        //删除allowPackage 判断是否在第一行
        if (allowPackagesStr.startsWith(allowPackage)) {
            allowPackagesStr = allowPackagesStr.replace(allowPackage + "\r\n", "");
        } else {
            allowPackagesStr = allowPackagesStr.replace("\r\n" + allowPackage, "");
        }
        SysConfig config = new SysConfig();
        config.setConfigKey("sys.rule.allowPackages");
        config.setConfigValue(allowPackagesStr);
        sysConfigService.updateConfigByKey(config);
        allowPackages.remove(allowPackage);
        return allowPackages;
    }

    @Override
    public Set<String> getAllowPackage() {
        return allowPackages;
    }

    @Override
    public int publishScriptByView(List<Node> scriptList) {
        int ret = 0;
        for (Node node : scriptList) {
            LinkedTreeMap map = (LinkedTreeMap) node.getData();
            Script script = JSONUtil.toBean(JSONUtil.toJsonStr(map), Script.class);
            ret += updateRuleScript(script);
        }
        return ret;
    }

    private int updateRuleScript(Script script) {
        SysUser sysUser = getLoginUser().getUser();
        LambdaQueryWrapper<Script> lqw = Wrappers.lambdaQuery();
        lqw.eq(script.getScriptId() != null, Script::getScriptId, script.getScriptId());
        Script old = ruleScriptMapper.selectOne(lqw);
        if (old != null) {
            script.setUpdateBy("ruleView");
            script.setUpdateTime(DateUtils.getNowDate());
            return ruleScriptMapper.update(script, lqw);
        } else {
            // 归属为机构管理员
            if (null != sysUser.getDeptId()) {
                script.setUserId(sysUser.getDept().getDeptUserId());
                script.setUserName(sysUser.getDept().getDeptName());
            } else {
                script.setUserId(sysUser.getUserId());
                script.setUserName(sysUser.getUserName());
            }
            script.setApplicationName(applicationName);
            script.setScriptEvent(7);
            script.setScriptAction(6);
            script.setScriptPurpose(3);
            script.setCreateBy("ruleView");
            script.setCreateTime(DateUtils.getNowDate());
            return ruleScriptMapper.insert(script);
        }
    }

    /**
     * 修改规则引擎脚本
     *
     * @param scriptVO 规则引擎脚本
     * @return 结果
     */
    @Override
    public int updateRuleScript(ScriptVO scriptVO) {
        // 脚本中引用包替换为许可的包
        scriptVO.setScriptData(replaceAllowPackage(scriptVO.getScriptData()));
        scriptVO.setUpdateTime(DateUtils.getNowDate());
        if ((Objects.equals(scriptVO.getScriptEvent(), ScriptEventEnum.HTTP_BRIDGE.getType()) || Objects.equals(scriptVO.getScriptEvent(), ScriptEventEnum.MQTT_BRIDGE.getType())) && scriptVO.getBridgeId() != 0) {
            ruleScriptMapper.updateScriptBridge(scriptVO.getScriptId(), scriptVO.getBridgeId());
        }
        Script script = ScriptConvert.INSTANCE.convertScript(scriptVO);
        script.setUpdateBy(getUsername());
        int result = ruleScriptMapper.updateById(script);
        // 动态刷新脚本
        if (result == 1) {
            if (scriptVO.getEnable() == 1) {
                LiteFlowNodeBuilder.createScriptNode().setId(scriptVO.getScriptId())
                        .setName(scriptVO.getScriptName())
                        .setScript(scriptVO.getScriptData())
                        .build();
                log.info("重新加载脚本：{}", scriptVO.getScriptId());
            } else {
                FlowBus.unloadScriptNode(scriptVO.getScriptId());
                log.info("删除脚本：{}", scriptVO.getScriptId());
            }
        }
        return result;
    }

    /**
     * 批量删除规则引擎脚本
     *
     * @param ids 需要删除的规则引擎脚本主键
     * @return 结果
     */
    @Override
    public int deleteRuleScriptByIds(String[] ids) {
        for (String id : ids) {
            FlowBus.unloadScriptNode(id);
            log.info("删除脚本：{}", id);
            ruleScriptMapper.deleteScriptBridge(id);
        }
        return ruleScriptMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除规则引擎脚本信息
     *
     * @param id 规则引擎脚本主键
     * @return 结果
     */
    @Override
    public int deleteRuleScriptById(String id) {
        FlowBus.unloadScriptNode(id);
        log.info("删除脚本：{}", id);
        ruleScriptMapper.deleteScriptBridge(id);
        return ruleScriptMapper.deleteById(id);
    }

    /**
     * 验证脚本
     * ruleScript.scriptData 脚本数据
     *
     * @return
     */
    @Override
    public AjaxResult validateScript(Script ruleScript) {
        // 检查安全性检查
        String pattern = ".*while|for\\s*\\(|InputStream|OutputStream|Reader|Writer|File|Socket.*";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(ruleScript.getScriptData());
        if (m.find()) {
            return success("验证失败，错误信息：" + "不能包含关键词for、while、Reader、Write、File、Socket等", false);
        }
        // 热刷新脚本
        try {
            ScriptExecutorFactory.loadInstance().getScriptExecutor("groovy").load("validateScript", ruleScript.getScriptData());
        } catch (Exception e) {
            return success("验证失败，错误信息：" + e.getMessage(), false);
        }
        return success("验证成功，脚本的实际执行情况可以查看后端日志文件", true);
    }

    @Override
    public int deleteRuleScriptBySceneIds(Long[] sceneIds) {
        for (Long id : sceneIds) {
            Script script = new Script();
            script.setSceneId(id);
            List<ScriptVO> list = ruleScriptMapper.selectRuleScriptList(script);
            list.forEach(item -> {
                FlowBus.unloadScriptNode(item.getScriptId());
            });
        }
        LambdaQueryWrapper<Script> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Script::getSceneId, Arrays.asList(sceneIds));
        return ruleScriptMapper.delete(queryWrapper);
    }

    @Override
    public int insertRuleScriptList(List<Script> ruleScriptList) {
        return ruleScriptMapper.insertBatch(ruleScriptList) ? 1 : 0;
    }

    @Override
    public MsgContext execRuleScript(ScriptCondition scriptCondition, Object... contextBeanArray) {
        List<Script> scripts = selectExecRuleScriptList(scriptCondition);
        //如果查询不到脚本，则认为是不用处理
        if (Objects.isNull(scripts) || scripts.isEmpty()) {
            return null;
        }
        LiteflowResponse response = null;
        for (Script script : scripts) {
            String el;
            String requestId = "script/" + script.getScriptId();
            if (script.getScriptAction() == ScriptActionType.HTTP_PUSH.getCode()) {
                el = "THEN(" + script.getScriptId() + ",httpBridge" + ")";
            } else if (script.getScriptAction() == ScriptActionType.MQTT_BRIDGE.getCode()) {
                el = "THEN(" + script.getScriptId() + ",mqttBridge" + ")";
            } else if (script.getScriptAction() == ScriptActionType.DB_STORE.getCode()) {
                el = "THEN(" + script.getScriptId() + ",databaseBridge" + ")";
            } else {
                el = "THEN(" + script.getScriptId() + ")";
            }
            // 判断脚本节点是否存在
            if (FlowBus.getNode(script.getScriptId()) == null) {
                LiteFlowNodeBuilder.createScriptNode().setId(script.getScriptId())
                        .setName(script.getScriptName())
                        .setScript(script.getScriptData())
                        .build();
                log.info("重新加载脚本：{}", script.getScriptId());
            }
            // 直接执行el规则，不创建规则链
            response = flowLogExecutor.execute2RespWithEL(el, requestId, contextBeanArray);
            if (response.isSuccess() && script.getScriptAction() == 1) {
                // http接入 && mqtt桥接接入默认开启重发布
                if (script.getScriptEvent() == 5 || script.getScriptEvent() == 6) {
                    MsgContext cxt = response.getContextBean(MsgContext.class);
                    mqttClient.publish(0, false, cxt.getTopic(), cxt.getPayload());
                }
            }
        }
        return response.getContextBean(MsgContext.class);
    }

    public MsgContext processRuleScript(String serialNumber, Integer event, String topic, String payload) {
        DeviceMetaData deviceMetaData = deviceCache.getDeviceMetaDataCache(serialNumber);
        if (Objects.isNull(deviceMetaData)) {
            return new MsgContext();
        }
        // 查询数据流脚本组件
        ScriptCondition scriptCondition = ScriptCondition.builder()
                .scriptPurpose(ScriptPurposeType.DATA_STREAM.getCode())
                .scriptEvent(event)
                .productId(deviceMetaData.getDevice().getProductId())
                .build();
        MsgContext context = MsgContext.builder()
                .protocolCode(deviceMetaData.getProduct().getProtocolCode())
                .payload(payload)
                .topic(topic)
                .build();
        context.setSerialNumber(serialNumber);
        context.setProductId(deviceMetaData.getDevice().getProductId());
        //返回处理完的消息上下文
        return this.execRuleScript(scriptCondition, context);
    }
}
