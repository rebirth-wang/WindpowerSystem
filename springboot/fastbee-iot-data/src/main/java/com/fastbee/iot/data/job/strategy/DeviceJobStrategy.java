package com.fastbee.iot.data.job.strategy;

import java.util.*;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Component;

import com.fastbee.common.enums.TopicType;
import com.fastbee.common.extend.core.domin.mq.InvokeReqDto;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.common.extend.enums.DeviceJobTypeEnum;
import com.fastbee.common.extend.enums.ScriptEventEnum;
import com.fastbee.common.utils.gateway.mq.TopicsUtils;
import com.fastbee.iot.data.service.IFunctionInvoke;
import com.fastbee.iot.data.service.IMqttMessagePublish;
import com.fastbee.iot.domain.DeviceJob;
import com.fastbee.iot.model.Action;
import com.fastbee.iot.service.IScriptService;
import com.fastbee.mqttclient.PubMqttClient;
import com.fastbee.rule.context.MsgContext;

/**
 * @author gsb
 * @date 2025/3/18 15:50
 */
@Component
public class DeviceJobStrategy implements JobInvokeStrategy {

    @Resource
    private IFunctionInvoke functionInvoke;
    @Resource
    private IMqttMessagePublish messagePublish;

    @Resource
    private IScriptService scriptService;

    @Resource
    private PubMqttClient mqttClient;
    @Resource
    private TopicsUtils topicsUtils;

    @Override
    public boolean supports(int jobType) {
        return DeviceJobTypeEnum.DEVICE_JOB.getType() == jobType;
    }

    @Override
    public void invoke(DeviceJob deviceJob) {
        System.out.println("------------------------执行定时任务-----------------------------");
        if ("MODBUS".equals(deviceJob.getJobGroup())) {
            List<Action> actions = JSON.parseArray(deviceJob.getActions(), Action.class);
            for (Action action : actions) {
                InvokeReqDto reqDto = new InvokeReqDto();
                reqDto.setProductId(deviceJob.getProductId());
                reqDto.setSerialNumber(deviceJob.getSerialNumber());
                reqDto.setModelName("");
                reqDto.setType(1);
                reqDto.setIdentifier(action.getId());
                Map<String, Object> params = new HashMap<>();
                params.put(action.getId(), action.getValue());
                reqDto.setRemoteCommand(params);
                reqDto.setParams(new JSONObject(reqDto.getRemoteCommand()));
                functionInvoke.invokeNoReply(reqDto);
            }
        } else {
             List<Action> actions = JSON.parseArray(deviceJob.getActions(), Action.class);
            List<ThingsModelSimpleItem> thingsModelSimpleItems = new ArrayList<>();
            for (Action action : actions) {
                ThingsModelSimpleItem model = new ThingsModelSimpleItem();
                model.setId(action.getId());
                model.setValue(action.getValue());
                model.setRemark("设备定时");
                thingsModelSimpleItems.add(model);
            }
            String topic = topicsUtils.buildTopic(deviceJob.getProductId(), deviceJob.getSerialNumber(), TopicType.FUNCTION_GET);
            // 调用 RuleProcess 处理规则脚本
            MsgContext msgContext = scriptService.processRuleScript(
                    deviceJob.getSerialNumber(),
                    ScriptEventEnum.DEVICE_INVOKE.getType(),
                    topic,
                    JSON.toJSONString(thingsModelSimpleItems)
            );

            // 使用转换后的内容发布属性
            if (!Objects.isNull(msgContext)) {
                mqttClient.publish(msgContext.getPayload().getBytes(), msgContext.getTopic(),  false,  1);
            }else {
                messagePublish.publishFunction(deviceJob.getProductId(), deviceJob.getSerialNumber(), thingsModelSimpleItems, 0);
            }
        }
    }
}
