package com.fastbee.iot.data.ruleEngine;

import static java.util.regex.Pattern.compile;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.mq.InvokeReqDto;
import com.fastbee.common.extend.core.domin.notify.AlertPushParams;
import com.fastbee.common.extend.core.domin.thingsModel.SceneThingsModelItem;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.common.extend.enums.AlertLogStatusEnum;
import com.fastbee.common.extend.enums.EnableEnum;
import com.fastbee.common.extend.enums.SceneCondEnum;
import com.fastbee.common.extend.enums.ScenePurposeEnum;
import com.fastbee.common.extend.enums.SceneSourceEnum;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.spring.SpringUtils;
import com.fastbee.iot.data.service.IDataHandler;
import com.fastbee.iot.data.service.IFunctionInvoke;
import com.fastbee.iot.data.service.IRuleEngine;
import com.fastbee.iot.domain.*;
import com.fastbee.iot.enums.DeviceEventType;
import com.fastbee.iot.model.ScriptTemplate;
import com.fastbee.iot.model.ThingsModels.ValueItem;
import com.fastbee.iot.model.vo.AlertSceneSendVO;
import com.fastbee.iot.model.vo.DeviceAlertUserVO;
import com.fastbee.iot.model.vo.SceneTerminalUserVO;
import com.fastbee.iot.service.*;
import com.fastbee.notify.core.service.NotifySendService;
import com.fastbee.rule.context.RuleContext;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class SceneContext extends RuleContext {
    private static Logger scene_logger = LoggerFactory.getLogger("scene");

    private static IFunctionInvoke functionInvoke = SpringUtils.getBean(IFunctionInvoke.class);

    private static IDeviceService deviceService = SpringUtils.getBean(IDeviceService.class);

    private static IAlertService alertService = SpringUtils.getBean(IAlertService.class);

    private static NotifySendService notifySendService = SpringUtils.getBean(NotifySendService.class);

    private static RedisCache redisCache = SpringUtils.getBean(RedisCache.class);

    private static IAlertLogService alertLogService = SpringUtils.getBean(IAlertLogService.class);

    private static IDeviceUserService deviceUserService = SpringUtils.getBean(IDeviceUserService.class);

    private static ISceneService sceneService = SpringUtils.getBean(ISceneService.class);

    private static IRuleEngine ruleService = SpringUtils.getBean(IRuleEngine.class);

    private static IDeviceAlertUserService deviceAlertUserService = SpringUtils.getBean(IDeviceAlertUserService.class);

    private static IDataHandler dataHandler = SpringUtils.getBean(IDataHandler.class);

    private static IWorkOrderService workOrderService = SpringUtils.getBean(IWorkOrderService.class);

    public SceneContext(String deviceNum, Long productId, int type, List<ThingsModelSimpleItem> thingsModelSimpleItems, Long sceneId) {
        this.setProductId(productId);
        this.setSerialNumber(deviceNum);
        this.setType(type);
        this.setSceneId(sceneId);
        this.setThingsModelSimpleItems(thingsModelSimpleItems);
        this.setTriggerType(type);
    }

    /**
     * 处理规则脚本
     *
     * @return
     */
    private boolean process(String json) throws InterruptedException {
        ScriptTemplate scriptTemplate = JSON.parseObject(json, ScriptTemplate.class);
        String requestId = "scene/" + scriptTemplate.getChainName();
        scene_logger.info("[{}]=====+>场景联动规则开始执行！", requestId);
        scene_logger.info("[{}]=====+>规则引擎脚本配置：{}，规则引擎上下文：{}", requestId, scriptTemplate, this);
        //MDC.put("id", scriptTemplate.getChainName());
        //触发器
        if (ScenePurposeEnum.TRIGGER.getPurpose().equals(scriptTemplate.getPurpose())) {
            // 触发器，检查静默时间
            if (!checkSilent(scriptTemplate.getSilent(), scriptTemplate.getSceneId(), requestId, this.getSerialNumber())) {
                // 触发条件为不满足时，返回true
                if (scriptTemplate.getCond() == SceneCondEnum.NOT.getCond()) {
                    return true;
                }
                return false;
            }
            if (SceneSourceEnum.DEVICE.getSource().equals(scriptTemplate.getSource()) || SceneSourceEnum.ALARM_RECOVER.getSource().equals(scriptTemplate.getSource())) {
                // 设备触发
                return deviceTrigger(scriptTemplate);
            } else if (SceneSourceEnum.PRODUCT.getSource().equals(scriptTemplate.getSource())) {
                // 产品触发
                return productTrigger(scriptTemplate);
            } else if (SceneSourceEnum.DEVICE_OFFLINE.getSource().equals(scriptTemplate.getSource())) {
                return true;
            }
            //执行动作
        } else if (ScenePurposeEnum.ACTION.getPurpose().equals(scriptTemplate.getPurpose())) {
            scene_logger.info("[{}]=====+>符合触发条件！", requestId);
            if (scriptTemplate.getCheckdelay() != 0) {
                // 告警恢复规则 设备上线触发 不加载
                if (scriptTemplate.getSource() != SceneSourceEnum.ALARM_RECOVER.getSource() && this.getType() != DeviceEventType.DEV_ONLINE.getCode()) {
                    loadDelayTrigger(scriptTemplate);
                }
            } else {
                // 执行动作，延迟执行，线程休眠 delay x 1000毫秒
                Thread.sleep(scriptTemplate.getDelay() * 1000);
                if (SceneSourceEnum.ALARM.getSource().equals(scriptTemplate.getSource())) {
                    //存在相同未处理告警只记录 不触发告警通知和动作
                    boolean isNodify = checkDeviceAlerting(scriptTemplate.getSceneId(), requestId, this.getSerialNumber(), scriptTemplate.getNotifyCount());
                    this.alert(scriptTemplate.getSceneId(), isNodify);
                } else if (SceneSourceEnum.ALARM_RECOVER.getSource().equals(scriptTemplate.getSource())) {
                    this.alertRecover(scriptTemplate);
                } else if (SceneSourceEnum.DEVICE.getSource().equals(scriptTemplate.getSource()) || SceneSourceEnum.PRODUCT.getSource().equals(scriptTemplate.getSource()) || SceneSourceEnum.DEVICE_OFFLINE.getSource().equals(scriptTemplate.getSource())) {
                    // 下发指令
                    this.send(scriptTemplate);
                }
                // 更新静默时间
                this.updateSilent(scriptTemplate.getSilent(), scriptTemplate.getSceneId(), this.getSerialNumber());
            }
        }
        return false;
    }

    /***
     * 设备触发脚本处理
     * @param scriptTemplate 解析后的Json脚本数据
     * @return
     */
    private boolean deviceTrigger(ScriptTemplate scriptTemplate) {
        String requestId = "scene/" + scriptTemplate.getChainName();
        // 判断定制触发（执行一次）或设备上报
        boolean isDeviceReport = !StringUtils.isEmpty(this.getSerialNumber());
        if (isDeviceReport) {
            // 1. 匹配设备编号
            scene_logger.info("[{}]=====+>设备触发！", requestId);
            boolean matchDeviceNum = Arrays.asList(scriptTemplate.getDeviceNums().split(",")).contains(this.getSerialNumber());
            if (this.getType() < DeviceEventType.DEV_UPGRADE.getCode()) {
                // 2.匹配物模型标识符
                if (matchDeviceNum) {
                    return matchValue(requestId, scriptTemplate);
                } else if (scriptTemplate.getCond() == SceneCondEnum.ALL.getCond()) {
                    // 所有条件下 不同设备 取缓存值来判断
                    return matchCacheValue(requestId, scriptTemplate);
                }
            } else {
                if (matchDeviceNum) {
                    return matchDeviceStatus(requestId, scriptTemplate);
                }
            }
        } else {
            scene_logger.info("[{}]=====+>定时触发/执行一次！", requestId);
            // 定时触发/执行一次
            String[] deviceNums = scriptTemplate.getDeviceNums().split(",");
            // 任意设备匹配成功返回true
            return matchTimedTrigger(requestId, scriptTemplate, deviceNums);
        }
        return false;
    }

    /***
     * 产品触发脚本处理
     * @param scriptTemplate
     * @return
     */
    private boolean productTrigger(ScriptTemplate scriptTemplate) {
        String requestId = "scene/" + scriptTemplate.getChainName();
        // 判断定制触发（执行一次）或设备上报
        boolean isDeviceReport = !StringUtils.isEmpty(this.getSerialNumber());
        if (isDeviceReport) {
            scene_logger.info("[{}]=====+>产品触发！", requestId);
            // 匹配产品编号
            boolean matchProductId = scriptTemplate.getProductId().equals(this.getProductId());
            if (this.getType() < DeviceEventType.DEV_UPGRADE.getCode()) {
                // 匹配物模型标识符
                if (matchProductId) {
                    return matchValue(requestId, scriptTemplate);
                } else if (scriptTemplate.getCond() == SceneCondEnum.ALL.getCond()) {
                    // 所条件下 不同设备 取缓存值来判断
                    return matchCacheValue(requestId, scriptTemplate);
                }
            } else {
                if (matchProductId) {
                    return matchDeviceStatus(requestId, scriptTemplate);
                }
            }
        } else {
            scene_logger.info("[{}]=====+>定时触发/执行一次！", requestId);
            // 定时触发/执行一次
            // 查询设备最新上报值去匹配
            String[] deviceNums = deviceService.getDeviceNumsByProductId(scriptTemplate.getProductId());
            return matchTimedTrigger(requestId, scriptTemplate, deviceNums);
        }
        return false;
    }

    private boolean matchCacheValue(String requestId, ScriptTemplate scriptTemplate) {
        String[] deviceNums = scriptTemplate.getDeviceNums().split(",");
        for (String deviceNum : deviceNums) {
            ValueItem item = getTSLVCacheValue(scriptTemplate.getProductId(), deviceNum, scriptTemplate.getId());
            if (item != null) {
                scene_logger.info("[{}]=====+>获取缓存值:{}！", requestId, item);
                if (this.getSceneThingsModelItems() == null) {
                    this.setSceneThingsModelItems(new ArrayList<>());
                }
                // 3.设备上报值匹配
                boolean isMatch = matchValue(requestId, scriptTemplate.getOperator(), scriptTemplate.getValue(), item.getValue());
                SceneThingsModelItem sceneItem = new SceneThingsModelItem(scriptTemplate.getId(), item.getValue(), this.getType(),
                        scriptTemplate.getScriptId(), scriptTemplate.getSceneId(), scriptTemplate.getProductId(), deviceNum, item.getRemark(), item.getName(), scriptTemplate.getValue());

                if (isMatch) {
                    this.getSceneThingsModelItems().add(sceneItem);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matchValue(String requestId, ScriptTemplate scriptTemplate) {
        ThingsModelSimpleItem matchItem = null;
        if (this.getThingsModelSimpleItems() != null) {
            for (ThingsModelSimpleItem item : this.getThingsModelSimpleItems()) {
                if (item.getId().equals(scriptTemplate.getId())) {
                    matchItem = item;
                    scene_logger.info("[{}]=====+>上报值匹配:{}！", requestId, matchItem.toString());
                    break;
                }
            }
        }

        if (matchItem != null) {
            // 记录结果
            if (this.getSceneThingsModelItems() == null) {
                this.setSceneThingsModelItems(new ArrayList<>());
            }
            // 3.设备上报值匹配
            boolean isMatch = matchValue(requestId, scriptTemplate.getOperator(), scriptTemplate.getValue(), matchItem.getValue());
            SceneThingsModelItem sceneItem = new SceneThingsModelItem(scriptTemplate.getId(), matchItem.getValue(), this.getType(),
                    scriptTemplate.getScriptId(), scriptTemplate.getSceneId(), scriptTemplate.getProductId(), this.getSerialNumber(), matchItem.getRemark(), matchItem.getName(), scriptTemplate.getValue());

            if (isMatch) {
                this.getSceneThingsModelItems().add(sceneItem);
                return true;
            }
        }
        return false;
    }

    private boolean matchDeviceStatus(String requestId, ScriptTemplate scriptTemplate) {
        // 延迟匹配只处理设备上下线  todo: 设备其他状态的延迟匹配
        if (this.getType() == DeviceEventType.DEV_ONLINE.getCode() && scriptTemplate.getCheckdelay() != 0) {
            String key = "CHECK_" + scriptTemplate.getSceneId() + this.getSerialNumber();
            //设备上线删除告警任务
            ruleService.removeCheckTask(key);
            scene_logger.info("[{}]=====+>设备上线清理 离线延时匹配任务：{}", requestId, key);
        }
        // 上线，下线
        if (scriptTemplate.getType() == this.getType()) {
            // 记录结果
            if (this.getSceneThingsModelItems() == null) {
                this.setSceneThingsModelItems(new ArrayList<>());
            }
            SceneThingsModelItem sceneItem = new SceneThingsModelItem(
                    this.getType() == DeviceEventType.DEV_ONLINE.getCode() ? "online" : "offline",
                    this.getType() == DeviceEventType.DEV_ONLINE.getCode() ? "1" : "0",
                    this.getType(),
                    scriptTemplate.getScriptId(), scriptTemplate.getSceneId(), scriptTemplate.getProductId(),
                    this.getSerialNumber(), "", "",
                    this.getType() == DeviceEventType.DEV_ONLINE.getCode() ? "1" : "0");
            this.getSceneThingsModelItems().add(sceneItem);
            // 记录结果
            return true;
        }
        return false;
    }

    private boolean matchTimedTrigger(String requestId, ScriptTemplate scriptTemplate, String[] deviceNums) {
        int resultCount = 0;
        // 查询设备最新上报值去匹配
        for (String num : deviceNums) {
            // 数组类型，key去除前缀，值从逗号分隔的字符串获取
            String id = "";
            if (DeviceEventType.DEV_ONLINE.getCode() == scriptTemplate.getType() || DeviceEventType.DEV_OFFLINE.getCode() == scriptTemplate.getType()) {
                return true;
            } else {
                id = scriptTemplate.getId();
            }
            String value = Objects.requireNonNull(getTSLVCacheValue(scriptTemplate.getProductId(), num, id)).getValue();
            boolean isMatch = matchValue(requestId, scriptTemplate.getOperator(), scriptTemplate.getValue(), value);
            if (isMatch) {
                // 记录结果
                if (this.getSceneThingsModelItems() == null) {
                    this.setSceneThingsModelItems(new ArrayList<>());
                }
                SceneThingsModelItem sceneItem = new SceneThingsModelItem(scriptTemplate.getId(), value, this.getType(),
                        scriptTemplate.getScriptId(), scriptTemplate.getSceneId(), scriptTemplate.getProductId(), num, "", "", scriptTemplate.getValue());
                this.getSceneThingsModelItems().add(sceneItem);
                resultCount++;
            }
        }
        return resultCount > 0;
    }

    private ValueItem getTSLVCacheValue(Long productId, String serialNumber, String id) {
        String key = RedisKeyBuilder.buildTSLVCacheKey(productId, serialNumber);
        String cacheValue = redisCache.getCacheMapValue(key, id);
        if (StringUtils.isEmpty(cacheValue)) {
            return null;
        }
        return JSON.parseObject(cacheValue, ValueItem.class);
    }

    private void loadDelayTrigger(ScriptTemplate scriptTemplate) {
        // 延迟匹配只处理设备下线状态  todo: 设备其他状态的延迟匹配
        String requestId = "scene/" + scriptTemplate.getChainName();
        String key = "CHECK_" + scriptTemplate.getSceneId() + this.getSerialNumber();
        //默认上线执行告警恢复 在延迟匹配周期内不告警
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            scene_logger.info("[{}]=====+>延时执行告警任务：{} 执行！", requestId, key);
            //设备未上线，则执行告警任务
            if (SceneSourceEnum.ALARM.getSource().equals(scriptTemplate.getSource())) {
                this.alert(scriptTemplate.getSceneId(), true);
            } else if (SceneSourceEnum.DEVICE.getSource().equals(scriptTemplate.getSource()) || SceneSourceEnum.PRODUCT.getSource().equals(scriptTemplate.getSource())) {
                // 下发指令
                this.send(scriptTemplate);
            }
        }, scriptTemplate.getCheckdelay(), TimeUnit.SECONDS);
        scene_logger.info("[{}]=====+>新增延时执行告警任务：{}", requestId, key);
        ruleService.addCheckTask(key, executor);
    }

    private boolean checkDeviceAlerting(Long sceneId, String requestId, String deviceNum, Integer notifyCount) {
        AlertLog alertLog = new AlertLog();
        alertLog.setSerialNumber(deviceNum);
        alertLog.setStatus(AlertLogStatusEnum.PROCESSING.getStatus());
        alertLog.setSceneId(sceneId);
        Long count = alertLogService.selectAlertLogListCount(alertLog);
        // 查询设备告警对应的场景是否有未处理告警
        if (count >= notifyCount) {
            scene_logger.info("[{}]=====+>只记录日志不发送告警！", requestId);
            return false;
        } else {
            scene_logger.info("[{}]=====+>发送告警！", requestId);
            return true;
        }
    }

    /**
     * 执行动作，下发指令
     *
     * @param scriptTemplate
     */
    private void send(ScriptTemplate scriptTemplate) {
        String[] deviceNumbers = null;
        if (SceneSourceEnum.DEVICE.getSource().equals(scriptTemplate.getSource()) || SceneSourceEnum.DEVICE_OFFLINE.getSource().equals(scriptTemplate.getSource())) {
            // 下发给指定设备
            deviceNumbers = scriptTemplate.getDeviceNums().split(",");
        } else if (SceneSourceEnum.PRODUCT.getSource().equals(scriptTemplate.getSource())) {
            deviceNumbers = deviceService.getDeviceNumsByProductId(scriptTemplate.getProductId());
        }

        if (deviceNumbers != null) {
            for (String deviceNum : deviceNumbers) {
                InvokeReqDto reqDto = new InvokeReqDto();
                reqDto.setProductId(scriptTemplate.getProductId());
                reqDto.setSerialNumber(deviceNum);
                reqDto.setModelName("");
                reqDto.setType(1);
                reqDto.setIdentifier(scriptTemplate.getId());
                Map<String, Object> params = new HashMap<>();
                params.put(scriptTemplate.getId(), scriptTemplate.getValue());
                reqDto.setRemoteCommand(params);
                reqDto.setParams(new JSONObject(reqDto.getRemoteCommand()));
                functionInvoke.invokeNoReply(reqDto);
            }
        }
    }

    private void alertRecover(ScriptTemplate scriptTemplate) {
        String requestId = "scene/" + scriptTemplate.getChainName();
        AlertLog alertLog = new AlertLog();
        alertLog.setSceneId(Long.valueOf(scriptTemplate.getId()));
        Scene scene = sceneService.getById(Long.valueOf(scriptTemplate.getId()));
        // 所有条件的触发器 恢复所有告警  任意条件的触发器只恢复对应设备的告警
        if (scene != null && scene.getCond() == SceneCondEnum.ANY.getCond()) {
            alertLog.setSerialNumber(this.getSerialNumber());
        }
        alertLog.setStatus(AlertLogStatusEnum.PROCESSED.getStatus());
        alertLog.setRemark(scriptTemplate.getChainName() + "的告警恢复");
        //自动设置告警处理状态
        alertLogService.updateAlertLogStatus(alertLog);
        //如果存在延时确认任务，则删除
        String key = "CHECK_" + scriptTemplate.getId() + this.getSerialNumber();
        //设备上线删除告警任务
        ruleService.removeCheckTask(key);
    }

    /**
     * 执行动作，告警处理
     *
     * @param sceneId 场景ID
     */
    private void alert(Long sceneId, boolean isNodify) {
        if (Objects.isNull( this.getSceneThingsModelItems())) {
            return;
        }
        Set<Long> sceneIdSet = this.getSceneThingsModelItems().stream().map(SceneThingsModelItem::getSceneId).collect(Collectors.toSet());
        List<SceneTerminalUserVO> sceneTerminalUserVOList = sceneService.selectTerminalUserBySceneIds(sceneIdSet);
        Map<Long, SceneTerminalUserVO> sceneTerminalUserMap = sceneTerminalUserVOList.stream().collect(Collectors.toMap(SceneTerminalUserVO::getSceneId, Function.identity()));
        List<AlertLog> alertLogList = new ArrayList<>();
        for (SceneThingsModelItem sceneThingsModelItem : this.getSceneThingsModelItems()) {
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
                // 开启通知发送
                if (isNodify) {
                    AlertPushParams alertPushParams = buildAlertPushParams(device, sceneThingsModelItem);
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
                if (EnableEnum.ENABLE.getType().equals(alertSceneSendVO.getGenerateWorkOrder())) {
                    workOrderService.alertGenerateWorkOrder(alertSceneSendVO, device);
                }
                AlertLog alertLog = getAlertLog(alertSceneSendVO, device, sceneThingsModelItem);
                alertLogList.add(alertLog);
            }
        }
        // 保存告警日志
        if (CollectionUtils.isNotEmpty(alertLogList)) {
            alertLogService.insertAlertLogBatch(alertLogList);
        }
    }

    private AlertPushParams buildAlertPushParams(Device device, SceneThingsModelItem sceneThingsModelItem) {
        // 获取告警推送参数
        AlertPushParams alertPushParams = new AlertPushParams();
        alertPushParams.setDeviceName(device.getDeviceName());
        alertPushParams.setSerialNumber(sceneThingsModelItem.getDeviceNumber());
//            List<DeviceUser> deviceUserList = deviceUserService.getDeviceUserAndShare(device.getDeviceId());
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
        alertPushParams.setIdentify(sceneThingsModelItem.getId());
        return alertPushParams;
    }

    private AlertLog getTerminalUserAlertLog(SceneTerminalUserVO sceneTerminalUserVO, Device device, SceneThingsModelItem sceneThingsModelItem) {
        AlertLog alertLog = new AlertLog();
        alertLog.setAlertName("设备告警");
        alertLog.setAlertLevel(1L);
        alertLog.setSerialNumber(sceneThingsModelItem.getDeviceNumber());
        alertLog.setProductId(sceneThingsModelItem.getProductId());
        alertLog.setDeviceName(device.getDeviceName());
        alertLog.setUserId(sceneTerminalUserVO.getUserId());
        // 统一未处理(处理中)
        alertLog.setStatus(AlertLogStatusEnum.PROCESSING.getStatus());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", sceneThingsModelItem.getId());
        jsonObject.put("name", sceneThingsModelItem.getName());
        jsonObject.put("value", sceneThingsModelItem.getValue());
        jsonObject.put("remark", sceneThingsModelItem.getRemark());
        alertLog.setDetail(jsonObject.toJSONString());
        alertLog.setCreateTime(new Date());
        alertLog.setCreateBy(sceneTerminalUserVO.getCreateBy());
        alertLog.setSceneId(sceneThingsModelItem.getSceneId());
        return alertLog;
    }

    /**
     * 组装告警日志
     *
     * @param alertSceneSendVO
     * @return com.fastbee.iot.domain.AlertLog
     * @param: device
     */
    private AlertLog getAlertLog(AlertSceneSendVO alertSceneSendVO, Device device, SceneThingsModelItem sceneThingsModelItem) {
        AlertLog alertLog = new AlertLog();
        alertLog.setAlertName(alertSceneSendVO.getAlertName());
        alertLog.setAlertLevel(alertSceneSendVO.getAlertLevel());
        alertLog.setSerialNumber(sceneThingsModelItem.getDeviceNumber());
        alertLog.setProductId(sceneThingsModelItem.getProductId());
        alertLog.setDeviceName(device.getDeviceName());
        alertLog.setUserId(device.getTenantId());
        // 统一未处理(处理中)
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

    /**
     * 检查静默周期物模型值是否匹配
     *
     * @param operator     操作符
     * @param triggerValue 触发值
     * @param value        上报的值
     * @return
     */
    private boolean matchValue(String requestId, String operator, String triggerValue, String value) {
        boolean result = false;
        // 操作符比较
        switch (operator) {
            case "=":
                result = value.equals(triggerValue);
                break;
            case "!=":
                result = !value.equals(triggerValue);
                break;
            case ">":
                if (isNumeric(value) && isNumeric(triggerValue)) {
                    result = Double.parseDouble(value) > Double.parseDouble(triggerValue);
                }
                break;
            case "<":
                if (isNumeric(value) && isNumeric(triggerValue)) {
                    result = Double.parseDouble(value) < Double.parseDouble(triggerValue);
                }
                break;
            case ">=":
                if (isNumeric(value) && isNumeric(triggerValue)) {
                    result = Double.parseDouble(value) >= Double.parseDouble(triggerValue);
                }
                break;
            case "<=":
                if (isNumeric(value) && isNumeric(triggerValue)) {
                    result = Double.parseDouble(value) <= Double.parseDouble(triggerValue);
                }
                break;
            case "between":
                // 比较值用英文中划线分割 -
                String[] triggerValues = triggerValue.split("~");
                if (isNumeric(value) && isNumeric(triggerValues[0]) && isNumeric(triggerValues[1])) {
                    result = Double.parseDouble(value) >= Double.parseDouble(triggerValues[0]) && Double.parseDouble(value) <= Double.parseDouble(triggerValues[1]);
                }
                break;
            case "notBetween":
                // 比较值用英文中划线分割 -
                String[] trigValues = triggerValue.split("~");
                if (isNumeric(value) && isNumeric(trigValues[0]) && isNumeric(trigValues[1])) {
                    result = Double.parseDouble(value) <= Double.parseDouble(trigValues[0]) || Double.parseDouble(value) >= Double.parseDouble(trigValues[1]);
                }
                break;
            case "contain":
                result = value.contains(triggerValue);
                break;
            case "notContain":
                result = !value.contains(triggerValue);
                break;
            default:
                break;
        }
        scene_logger.info("[{}]=====+>条件匹配结果：{}！", requestId, result);
        return result;
    }

    /**
     * 检查静默时间
     *
     * @param silent
     * @param sceneId
     * @return
     */
    private boolean checkSilent(int silent, Long sceneId, String requestId, String serialNumber) {
        if (silent == 0 || sceneId == 0) {
            return true;
        }
        // silent:scene_场景编号
        String key = "silent:" + "scene_" + sceneId + ":" + serialNumber;
        Calendar calendar = Calendar.getInstance();
        // 查询静默截止时间
        Long expireTime = redisCache.getCacheObject(key);
        if (expireTime == null) {
            // 添加场景静默时间
            calendar.add(Calendar.MINUTE, silent);
            redisCache.setCacheObject(key, calendar.getTimeInMillis());
            scene_logger.info("[{}]=====+>检查静默时间！", requestId);
            return true;
        } else {
            Long NowTimestamp = Calendar.getInstance().getTimeInMillis();
            if (NowTimestamp > expireTime) {
                scene_logger.info("[{}]=====+>检查静默时间！", requestId);
                return true;
            }
            scene_logger.info("[{}]=====+>检查静默时间！", requestId);
            return false;
        }
    }

    /**
     * 更新静默时间
     *
     * @param sceneId
     * @param silent
     */
    private void updateSilent(int silent, Long sceneId, String serialNumber) {
        if (silent == 0 || sceneId == 0) {
            return;
        }
        // 更新场景静默时间
        String key = "silent:" + "scene_" + sceneId + ":" + serialNumber;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, silent);
        redisCache.setCacheObject(key, calendar.getTimeInMillis());
    }

    /**
     * 判断字符串是否为整数或小数
     */
    private boolean isNumeric(String str) {
        Pattern pattern = compile("-?[0-9]+(\\.[0-9]+)?");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

}
