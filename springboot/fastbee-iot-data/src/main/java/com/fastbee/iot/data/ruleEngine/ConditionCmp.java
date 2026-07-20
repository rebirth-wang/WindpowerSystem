package com.fastbee.iot.data.ruleEngine;

import static java.util.regex.Pattern.compile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;

import com.fastbee.common.extend.core.domin.thingsModel.SceneThingsModelItem;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.iot.cache.ITSLValueCache;
import com.fastbee.iot.model.ThingsModels.ValueItem;
import com.fastbee.rule.cmp.data.ConditionData;
import com.fastbee.rule.context.RuleContext;

@LiteflowComponent("condition")
public class ConditionCmp extends NodeBooleanComponent {
    @Resource
    private ITSLValueCache tSLValueCache;

    @Override
    public boolean processBoolean() throws Exception {
        ConditionData data = this.getCmpData(ConditionData.class);
        RuleContext cxt = this.getContextBean(RuleContext.class);
        List<SceneThingsModelItem> sceneThingsModelItems = cxt.getSceneThingsModelItems();
        List<ThingsModelSimpleItem> thingsModelSimpleItems = cxt.getThingsModelSimpleItems();
        boolean result = false;
        boolean isMatch = false;
        if (cxt.getTriggerType() < 4) {
            if (data.getType() == 1) {
                cxt.printDebugLog(this, "上报数据匹配，type=%d", data.getType());
                // 默认只有一个触发属性 if 判断按第一个条件执行
                for (ThingsModelSimpleItem item : thingsModelSimpleItems) {
                    if (!item.getId().equals(data.getModelId())) {
                        continue;
                    }
                    isMatch = true;
                    String value = item.getValue();
                    // 默认只判断第一个条件
                    result = checkItem(value, data.getExpressions().get(0));
                    if (result) {
                        SceneThingsModelItem sceneItem = new SceneThingsModelItem(item.getId(), item.getValue(), cxt.getType(),
                                this.getChainId(), cxt.getSceneId(), cxt.getProductId(), cxt.getSerialNumber(), item.getRemark(), item.getName(), data.getExpressions().get(0).getValue());
                        cxt.printDebugLog(this, "匹配项：%s", sceneItem.toString());
                        if (sceneThingsModelItems == null) {
                            sceneThingsModelItems = new ArrayList<>();
                        }
                        sceneThingsModelItems.add(sceneItem);
                    }
                }
                if (!isMatch) {
                    cxt.printDebugLog(this, "无匹配项，流程结束！");
                    this.setIsEnd(true);
                }
            } else {
                cxt.printDebugLog(this, "状态值数据匹配，type=%d", data.getType());
                ValueItem item = tSLValueCache.getCacheIdentifier(cxt.getProductId(), cxt.getSerialNumber(), data.getModelId());
                if (item != null) {
                    result = checkItem(item.getValue(), data.getExpressions().get(0));
                    if (result) {
                        SceneThingsModelItem sceneItem = new SceneThingsModelItem(item.getId(), item.getValue(), cxt.getType(),
                                this.getChainId(), cxt.getSceneId(), cxt.getProductId(), cxt.getSerialNumber(), item.getRemark(), item.getName(), data.getExpressions().get(0).getValue());
                        if (sceneThingsModelItems == null) {
                            sceneThingsModelItems = new ArrayList<>();
                        }
                        cxt.printDebugLog(this, "匹配项：%s", sceneItem.toString());
                        sceneThingsModelItems.add(sceneItem);
                    }
                }
            }
            // 记录结果
            cxt.setSceneThingsModelItems(sceneThingsModelItems);
        } else if (cxt.getTriggerType() == 5 || cxt.getTriggerType() == 6) {
            if (cxt.getTriggerType().equals(data.getTriggerType())) {
                SceneThingsModelItem sceneItem = new SceneThingsModelItem(data.getTriggerType() == 5 ? "online" : "offline", data.getTriggerType() == 5 ? "1" : "0", cxt.getType(),
                        this.getChainId(), cxt.getSceneId(), cxt.getProductId(), cxt.getSerialNumber(), "", "", data.getTriggerType() == 5 ? "1" : "0");
                if (sceneThingsModelItems == null) {
                    sceneThingsModelItems = new ArrayList<>();
                }
                cxt.printDebugLog(this, "匹配项：%s", sceneItem.toString());
                sceneThingsModelItems.add(sceneItem);
                result = true;
            }
        } else {
            cxt.printDebugLog(this, "触发类型不正确，流程结束！");
            this.setIsEnd(true);
        }

        return result;
    }

    private boolean checkItem(String value, ConditionData.Expression expr) {
        boolean result = false;
        String operator = expr.getOperator();
        String triggerValue = expr.getValue();
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
        return result;
    }

    private boolean isNumeric(String str) {
        Pattern pattern = compile("[0-9]*\\.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
