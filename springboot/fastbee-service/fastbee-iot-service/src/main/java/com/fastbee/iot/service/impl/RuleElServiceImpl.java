package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import java.util.*;

import jakarta.annotation.Resource;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.internal.LinkedTreeMap;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.FlowBus;
import com.yomahub.liteflow.flow.LiteflowResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.Scene;
import com.fastbee.iot.enums.TriggerTypeEnum;
import com.fastbee.iot.service.*;
import com.fastbee.rule.context.BaseContext;
import com.fastbee.rule.context.RuleContext;
import com.fastbee.rule.convert.RuleElConvert;
import com.fastbee.rule.domain.RuleEl;
import com.fastbee.rule.domain.RuleLog;
import com.fastbee.rule.domain.vo.RuleElVO;
import com.fastbee.rule.mapper.RuleElMapper;
import com.fastbee.rule.parser.entity.node.Node;
import com.fastbee.rule.parser.enums.RuleEnums;
import com.fastbee.rule.parser.execption.LiteFlowELException;
import com.fastbee.rule.parser.graph.Graph;
import com.fastbee.rule.parser.graph.GraphInfo;

/**
 * 规则elService业务层处理
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@Slf4j
@Service
public class RuleElServiceImpl extends ServiceImpl<RuleElMapper, RuleEl> implements IRuleElService {

    @Resource
    private ISceneService sceneService;

    @Resource
    private FlowExecutor flowExecutor;

    @Resource
    private IRuleLogService ruleLogService;

    @Resource
    private IScriptService scriptService;

    @Resource
    private IDeviceService deviceService;

    /**
     * 查询规则el
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     *
     * @param ruleEl 规则el
     * @return 规则el
     */
    @Override
//    @Cacheable(cacheNames = "RuleEl", key = "#id")
    @DataScope
    public RuleEl queryByIdWithCache(RuleEl ruleEl) {
        LambdaQueryWrapper<RuleEl> lambdaQueryWrapper = this.buildQueryWrapper(ruleEl);
        return this.getOne(lambdaQueryWrapper);
    }

    /**
     * 查询规则el
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     *
     * @param id 主键
     * @return 规则el
     */
    @Override
    @Cacheable(cacheNames = "RuleEl", key = "#id")
    public RuleEl selectRuleElById(Long id) {
        return this.getById(id);
    }

    /**
     * 查询规则el分页列表
     *
     * @param ruleEl 规则el
     * @return 规则el
     */
    @Override
    @DataScope()
    public Page<RuleElVO> pageRuleElVO(RuleEl ruleEl) {
        LambdaQueryWrapper<RuleEl> lqw = buildQueryWrapper(ruleEl);
        Page<RuleEl> ruleElPage = baseMapper.selectPage(new Page<>(ruleEl.getPageNum(), ruleEl.getPageSize()), lqw);
        return RuleElConvert.INSTANCE.convertRuleElVOPage(ruleElPage);
    }

    /**
     * 查询规则el列表
     *
     * @param ruleEl 规则el
     * @return 规则el
     */
    @Override
    public List<RuleElVO> listRuleElVO(RuleEl ruleEl) {
        LambdaQueryWrapper<RuleEl> lqw = buildQueryWrapper(ruleEl);
        List<RuleEl> ruleElList = baseMapper.selectList(lqw);
        return RuleElConvert.INSTANCE.convertRuleElVOList(ruleElList);
    }

    private LambdaQueryWrapper<RuleEl> buildQueryWrapper(RuleEl query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<RuleEl> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getTenantId() != null, RuleEl::getTenantId, query.getTenantId());
        lqw.eq(query.getId() != null, RuleEl::getId, query.getId());
        lqw.eq(StringUtils.isNotBlank(query.getElId()), RuleEl::getElId, query.getElId());
        lqw.like(StringUtils.isNotBlank(query.getElName()), RuleEl::getElName, query.getElName());
        lqw.eq(StringUtils.isNotBlank(query.getEl()), RuleEl::getEl, query.getEl());
        lqw.eq(StringUtils.isNotBlank(query.getFlowJson()), RuleEl::getFlowJson, query.getFlowJson());
        lqw.eq(StringUtils.isNotBlank(query.getSourceJson()), RuleEl::getSourceJson, query.getSourceJson());
        lqw.eq(query.getExecutorId() != null, RuleEl::getExecutorId, query.getExecutorId());
        lqw.eq(query.getSceneId() != null, RuleEl::getSceneId, query.getSceneId());
        lqw.eq(query.getEnable() != null, RuleEl::getEnable, query.getEnable());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), RuleEl::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, RuleEl::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), RuleEl::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, RuleEl::getUpdateTime, query.getUpdateTime());
        lqw.eq(query.getDelFlag() != null, RuleEl::getDelFlag, query.getDelFlag());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), RuleEl::getRemark, query.getRemark());
        lqw.eq(StringUtils.isNotBlank(query.getRuleParams()), RuleEl::getRuleParams, query.getRuleParams());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(RuleEl::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))) {
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        lqw.orderByAsc(RuleEl::getUpdateTime);
        return lqw;
    }

    /**
     * 新增规则el
     *
     * @param add 规则el
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(RuleEl add) {
        SysUser user = getLoginUser().getUser();
        if (null == user.getDeptId()) {
            throw new ServiceException(MessageUtils.message("only.allow.tenant.config"));
        }
        validEntityBeforeSave(add);
        if (add.getEl() == null && add.getSourceJson() != null) {
            try {
                Graph graph = new Graph(add.getSourceJson());
                GraphInfo graphInfo = graph.toELInfo();
                add.setEl(graphInfo.toString());
            } catch (LiteFlowELException e) {
                throw new RuntimeException(e);
            }
        }
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        String id = "V" + snowflake.nextId();
        add.setElId(id);
        add.setTenantId(user.getDept().getDeptUserId());
        add.setCreateTime(DateUtils.getNowDate());
        add.setCreateBy(user.getUserName());
        return this.save(add);
    }

    /**
     * 修改规则el
     *
     * @param update 规则el
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "RuleEl", key = "#update.id")
    public RuleEl updateWithCache(RuleEl update) {
        SysUser user = getLoginUser().getUser();
        if (null == user.getDeptId()) {
            throw new ServiceException(MessageUtils.message("only.allow.tenant.config"));
        }
        validEntityBeforeSave(update);
        List<Node> list = new ArrayList<>();
        try {
            if (StringUtils.isNotBlank(update.getSourceJson())) {
                Graph graph = new Graph(update.getSourceJson());
                GraphInfo graphInfo = graph.toELInfo();
                update.setEl(graphInfo.toString());
                list = graph.getNodes(RuleEnums.NodeEnum.SCHEDULED_TRIGGER.getType());
            }
        } catch (LiteFlowELException e) {
            throw new RuntimeException(e);
        }
        // 更新场景表
        Scene scene = sceneService.selectScene(update.getSceneId());
        if (scene != null) {
            scene.setUserId(user.getDept().getDeptUserId());
            scene.setUserName(user.getUserName());
            scene.setSceneId(update.getSceneId());
            scene.setSceneName(update.getElName());
            scene.setElData(update.getEl());
            scene.setEnable(update.getEnable());
            sceneService.updateSceneStatusByView(scene, update, list);
        }
        // 更新el表
        update.setUpdateTime(DateUtils.getNowDate());
        update.setUpdateBy(user.getUserName());
        this.updateById(update);
        return update;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(RuleEl entity) {
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除规则el信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "RuleEl", keyGenerator = "deleteKeyGenerator")
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
        }
        for (Long id : ids) {
            RuleEl ruleEl = this.getById(id);
            if (null != ruleEl.getSceneId() && ruleEl.getSceneId() > 0) {
                // 删除场景联动规则
                sceneService.deleteSceneBySceneId(ruleEl.getSceneId());
            }
        }
        return this.removeByIds(Arrays.asList(ids));
    }


    /** 代码生成区域 可直接覆盖END**/

    /**
     * 自定义代码区域
     **/
    @Override
    public Boolean exec(RuleEl ruleEl) {
        if (ruleEl.getElId() != null) {
            return this.execByElid(ruleEl.getElId());
        }
        return false;
    }

    @Override
    public Boolean execByid(Long id, Object... contextBeanArray) {
        RuleEl ruleEl = this.getById(id);
        if (ruleEl != null) {
            String requestId = "ruleview/" + id;
            if (contextBeanArray.length > 0) {
                BaseContext context = (BaseContext) contextBeanArray[0];
                // json 转map
                if (ruleEl.getRuleParams() != null) {
                    Map<String, Object> values = JSON.parseObject(ruleEl.getRuleParams(), Map.class);
                    context.getDataMap().putAll(values);
                }
            }
            return executeRuleWithDebug(ruleEl, requestId, contextBeanArray);
        }
        return false;
    }

    public Boolean execByElid(String id) {
        LambdaQueryWrapper<RuleEl> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(id), RuleEl::getElId, id);
        RuleEl ruleEl = this.getOne(lqw);
        if (ruleEl != null) {
            String requestId = "ruleview/" + id;
            RuleContext context = buildContext(ruleEl);
            // json 转map
            if (ruleEl.getRuleParams() != null) {
                Map<String, Object> values = JSON.parseObject(ruleEl.getRuleParams(), Map.class);
                context.getDataMap().putAll(values);
            }
            return executeRuleWithDebug(ruleEl, requestId, context);
        }
        return false;
    }

    public RuleContext buildContext(RuleEl ruleEl) {
        Graph graph = new Graph(ruleEl.getSourceJson());
        // 获取触发节点
        List<Node> devTriggerList = graph.getNodes(RuleEnums.NodeEnum.DEV_TRIGGER.getType());
        List<Node> proTriggerList = graph.getNodes(RuleEnums.NodeEnum.PRODUCT_TRIGGER.getType());
        List<Node> schTriggerList = graph.getNodes(RuleEnums.NodeEnum.SCHEDULED_TRIGGER.getType());
        List<Node> customTriggerList = graph.getNodes(RuleEnums.NodeEnum.CUSTOM_TRIGGER.getType());
        RuleContext context = new RuleContext();
        if (!devTriggerList.isEmpty()) {
            LinkedTreeMap map = (LinkedTreeMap) devTriggerList.get(0).getData();
            // 安全获取设备ID
            Object deviceIdObj = map.get("deviceId");
            if (deviceIdObj != null) {
                context.setSerialNumber(deviceIdObj.toString());
            }
            // 安全获取并解析类型
            Object typeObj = map.get("type");
            if (typeObj != null) {
                try {
                    context.setTriggerType(Integer.parseInt(typeObj.toString()));
                } catch (NumberFormatException e) {
                    // 记录日志或设置默认值
                    context.setTriggerType(0); // 或其他默认值
                }
            }
            // 安全获取并解析产品ID
            Object productIdObj = map.get("productId");
            if (productIdObj != null) {
                try {
                    context.setProductId(Long.parseLong(productIdObj.toString()));
                } catch (NumberFormatException e) {
                    // 记录日志或设置默认值
                    context.setProductId(0L); // 或其他默认值
                }
            }

            if (map.get("modelId") != null) {
                List<ThingsModelSimpleItem> thingsModelSimpleItems = createThingsModelSimpleItems(map.get("modelId").toString());
                context.setThingsModelSimpleItems(thingsModelSimpleItems);
            }
        } else if (!proTriggerList.isEmpty()) {
            LinkedTreeMap map = (LinkedTreeMap) proTriggerList.get(0).getData();
            // 安全获取并解析类型
            Object typeObj = map.get("type");
            if (typeObj != null) {
                try {
                    context.setTriggerType(Integer.parseInt(typeObj.toString()));
                } catch (NumberFormatException e) {
                    context.setTriggerType(0); // 或其他默认值
                }
            }
            Long productId = 0L;
            Object productIdObj = map.get("productId");
            if (productIdObj != null) {
                try {
                    productId = Long.parseLong(productIdObj.toString());
                    context.setProductId(productId);
                } catch (NumberFormatException e) {
                    context.setProductId(0L); // 或其他默认值
                }
            }
            String[] serialNumbers = deviceService.getDeviceNumsByProductId(productId);
            if (serialNumbers != null && serialNumbers.length > 0) {
                context.setSerialNumber(serialNumbers[0]);
            }

            if (map.get("modelId") != null) {
                List<ThingsModelSimpleItem> thingsModelSimpleItems = createThingsModelSimpleItems(map.get("modelId").toString());
                context.setThingsModelSimpleItems(thingsModelSimpleItems);
            }
        } else if (!schTriggerList.isEmpty()) {
            context.setTriggerType(TriggerTypeEnum.SCHEDULED_TRIGGER.getCode());
        } else if (!customTriggerList.isEmpty()) {
            context.setTriggerType(TriggerTypeEnum.CUSTOM_TRIGGER.getCode());
        }
        context.setRuleId(ruleEl.getId());
        return context;
    }

    private List<ThingsModelSimpleItem> createThingsModelSimpleItems(String modelId) {
        List<ThingsModelSimpleItem> thingsModelSimpleItems = new ArrayList<>();
        ThingsModelSimpleItem thingsModelSimpleItem = new ThingsModelSimpleItem();
        thingsModelSimpleItem.setId(modelId);
        thingsModelSimpleItem.setTs(new Date());
        // 0-100随机值 保留两位小数
        double randomValue = Math.random() * 100;
        thingsModelSimpleItem.setValue(String.format("%.2f", randomValue));
        thingsModelSimpleItems.add(thingsModelSimpleItem);
        return thingsModelSimpleItems;
    }

    @Override
    public Boolean publish(RuleEl ruleEl) {
        try {
            SysUser user = getLoginUser().getUser();
            // 创建场景联动规则
            Graph graph = new Graph(ruleEl.getSourceJson());
            log.debug("publish:{}", ruleEl.getSourceJson());
            GraphInfo graphInfo = graph.toELInfo();
            ruleEl.setEl(graphInfo.toString());
            // 获取触发节点
            List<Node> triggerList = new ArrayList<>();
            triggerList.addAll(graph.getNodes(RuleEnums.NodeEnum.DEV_TRIGGER.getType()));
            triggerList.addAll(graph.getNodes(RuleEnums.NodeEnum.PRODUCT_TRIGGER.getType()));
            triggerList.addAll(graph.getNodes(RuleEnums.NodeEnum.SCHEDULED_TRIGGER.getType()));
            List<Node> customTriggerList = graph.getNodes(RuleEnums.NodeEnum.CUSTOM_TRIGGER.getType());

            List<Node> scriptList = graph.getNodes(RuleEnums.NodeEnum.SCRIPT.getType());
            if (!scriptList.isEmpty()) {
                scriptService.publishScriptByView(scriptList);
            }
            if (customTriggerList.size() > 1) {
                throw new ServiceException("只支持一个自定义触发！");
            }
            triggerList.addAll(customTriggerList);
            // 获取告警节点
            List<Node> alarmList = graph.getNodes(RuleEnums.NodeEnum.ALARM.getType());
            // 同步到规则引擎规则
            Scene oldscene = sceneService.selectSceneByChainName(ruleEl.getElId());
            if (ruleEl.getSceneId() == null && oldscene == null) {
                Scene scene = new Scene();
                if (StringUtils.isNotBlank(ruleEl.getElId())) {
                    scene.setChainName(ruleEl.getElId());
                } else {
                    Snowflake snowflake = IdUtil.getSnowflake(1, 1);
                    String id = "V" + snowflake.nextId();
                    scene.setChainName(id);
                }
                // 使用自定义触发组件的触发id
                if (!customTriggerList.isEmpty()) {
                    Node node = customTriggerList.get(0);
                    LinkedTreeMap map = (LinkedTreeMap) node.getData();
                    String customId = map.get("customId").toString();
                    if (FlowBus.getChain(customId) != null) {
                        throw new ServiceException("自定义触发ID已经存在！");
                    }
                    String value = map.get("customValue").toString();
                    ruleEl.setRuleParams(value);
                    ruleEl.setElId(customId);
                    scene.setChainName(customId);
                    if (FlowBus.getChain(customId) != null) {
                        throw new ServiceException("自定义触发ID已经存在！！");
                    }
                }
                scene.setUserId(user.getDept().getDeptUserId());
                scene.setUserName(user.getUserName());
                scene.setSceneName(ruleEl.getElName());
                scene.setElData(ruleEl.getEl());
                scene.setEnable(1);
                scene.setRemark(ruleEl.getRemark());
                if (!alarmList.isEmpty()) {
                    scene.setHasAlert(1);
                }
                sceneService.insertSceneByView(scene, ruleEl, triggerList);
                ruleEl.setSceneId(scene.getSceneId());
            } else {
                Scene scene;
                if (ruleEl.getSceneId() != null) {
                    scene = sceneService.selectScene(ruleEl.getSceneId());
                } else {
                    scene = oldscene;
                }
                if (!customTriggerList.isEmpty()) {
                    Node node = customTriggerList.get(0);
                    LinkedTreeMap map = (LinkedTreeMap) node.getData();
                    String customId = map.get("customId").toString();
                    String value = map.get("customValue").toString();
                    ruleEl.setRuleParams(value);
                    ruleEl.setElId(customId);
                    scene.setChainName(customId);
                    if (FlowBus.getChain(customId) != null) {
                        throw new ServiceException("自定义触发ID已经存在！！");
                    }
                }
                scene.setSceneName(ruleEl.getElName());
                scene.setElData(ruleEl.getEl());
                scene.setEnable(1);
                scene.setRemark(ruleEl.getRemark());
                if (!alarmList.isEmpty()) {
                    scene.setHasAlert(1);
                }
                sceneService.updateSceneByView(scene, ruleEl, triggerList);
            }
            // 更新el状态
            ruleEl.setEnable(1);
            return this.updateById(ruleEl);
        } catch (LiteFlowELException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean executeRuleWithDebug(RuleEl ruleEl, String requestId, Object... contextBeanArray) {
        boolean success = false;
        String errorMsg = null;
        try {
            // 执行规则
            LiteflowResponse response;
            if (ruleEl.getElId() != null) {
                response = flowExecutor.execute2RespWithRid(
                        ruleEl.getElId(), null, requestId, contextBeanArray);
            } else if (ruleEl.getEl() != null) {
                response = flowExecutor.execute2RespWithEL(
                        ruleEl.getEl(), null, requestId, contextBeanArray);
            } else {
                log.error("规则执行异常，规则: {}", ruleEl);
                return success;
            }

            success = response.isSuccess();
            if (!success) {
                errorMsg = response.getMessage();
            }
            BaseContext context = response.getContextBean(BaseContext.class);
            logExecutionResult(ruleEl, success, context,
                    success ? "执行成功:" + response.getExecuteStepStrWithTime() : "执行失败:" + response.getMessage());
        } catch (Exception e) {
            success = false;
            errorMsg = "执行异常: " + e.getMessage();
            log.error("规则执行异常，规则ID: {}", ruleEl.getElId(), e);
            logExecutionResult(ruleEl, false, null, errorMsg);
        }
        return success;
    }

    private void logExecutionResult(RuleEl ruleEl, boolean success, BaseContext context, String message) {
        try {
            RuleLog log = new RuleLog();
            log.setRuleId(ruleEl.getId());
            log.setElId(ruleEl.getElId());
            log.setStatus(success ? 1 : 0);
            log.setRuleParams(ruleEl.getRuleParams());
            log.setTriggerType(context.getTriggerType());
            if (context.getDebugEnabled()) {
                // 列表转json字符串
                log.setStepMsg(JSON.toJSONString(context.getDebugContext().getSteps()));
                log.setResultMsg(context.getDebugContext().getDebugMsg());
            } else {
                log.setResultMsg(message);
            }
            log.setCreateTime(DateUtils.getNowDate());
            ruleLogService.insertWithCache(log);
        } catch (Exception e) {
            log.error("记录规则执行日志失败，规则ID: {}", ruleEl, e);
        }
    }

}
