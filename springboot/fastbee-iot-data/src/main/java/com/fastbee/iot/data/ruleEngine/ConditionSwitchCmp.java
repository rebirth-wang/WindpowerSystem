package com.fastbee.iot.data.ruleEngine;

import static java.util.regex.Pattern.compile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeSwitchComponent;

import com.fastbee.common.extend.core.domin.thingsModel.SceneThingsModelItem;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.iot.cache.ITSLValueCache;
import com.fastbee.iot.model.ThingsModels.ValueItem;
import com.fastbee.rule.cmp.data.ConditionData;
import com.fastbee.rule.context.RuleContext;

@LiteflowComponent("conditionSwitch")
public class ConditionSwitchCmp extends NodeSwitchComponent {
    @Resource
    private ITSLValueCache tSLValueCache;

    @Override
    public String processSwitch() throws Exception {
        ConditionData data = this.getCmpData(ConditionData.class);
        RuleContext cxt = this.getContextBean(RuleContext.class);
        List<SceneThingsModelItem> sceneThingsModelItems = cxt.getSceneThingsModelItems();
        List<ThingsModelSimpleItem> thingsModelSimpleItems = cxt.getThingsModelSimpleItems();
        int index = 0;
        boolean result = false;
        boolean isMatch = false;
        if (cxt.getTriggerType() < 4) {
            for (ConditionData.Expression expr : data.getExpressions()) {
                if (data.getType() == 1) {
                    cxt.printDebugLog(this, "上报数据匹配，type=%d", data.getType());
                    for (ThingsModelSimpleItem item : thingsModelSimpleItems) {
                        if (!item.getId().equals(data.getModelId())) {
                            continue;
                        }
                        // 默认只判断第一个条件
                        String value = item.getValue();
                        // 默认只判断第一个条件
                        result = checkItem(value, expr);
                        index++;
                        if (result) {
                            SceneThingsModelItem sceneItem = new SceneThingsModelItem(item.getId(), item.getValue(), cxt.getType(),
                                    this.getChainId(), cxt.getSceneId(), cxt.getProductId(), cxt.getSerialNumber(), item.getRemark(), item.getName(), expr.getValue());
                            if (sceneThingsModelItems == null) {
                                sceneThingsModelItems = new ArrayList<>();
                            }
                            cxt.printDebugLog(this, "匹配项：%s \r\n tag:%d", sceneItem.toString(), index);
                            sceneThingsModelItems.add(sceneItem);
                            cxt.setSceneThingsModelItems(sceneThingsModelItems);
                            return "tag:" + index;
                        }
                    }
                } else {
                    cxt.printDebugLog(this, "上报数据匹配，type=%d", data.getType());
                    ValueItem item = tSLValueCache.getCacheIdentifier(cxt.getProductId(), cxt.getSerialNumber(), data.getModelId());
                    if (item != null) {
                        result = checkItem(item.getValue(), expr);
                        index++;
                        if (result) {
                            SceneThingsModelItem sceneItem = new SceneThingsModelItem(item.getId(), item.getValue(), cxt.getType(),
                                    this.getChainId(), cxt.getSceneId(), cxt.getProductId(), cxt.getSerialNumber(), item.getRemark(), item.getName(), data.getExpressions().get(0).getValue());
                            if (sceneThingsModelItems == null) {
                                sceneThingsModelItems = new ArrayList<>();
                            }
                            cxt.printDebugLog(this, "匹配项：%s \r\n tag:%d", sceneItem.toString(), index);
                            sceneThingsModelItems.add(sceneItem);
                            return "tag:" + index;
                        }
                    }
                }

            }
            if (!isMatch) {
                cxt.printDebugLog(this, "无匹配项，流程结束！");
                this.setIsEnd(true);
            }
        } else if (cxt.getTriggerType() == 5 || cxt.getTriggerType() == 6) {
            if (cxt.getTriggerType().equals(data.getTriggerType())) {
                SceneThingsModelItem sceneItem = new SceneThingsModelItem(data.getTriggerType() == 5 ? "online" : "offline", data.getTriggerType() == 5 ? "1" : "0", cxt.getType(),
                        this.getChainId(), cxt.getSceneId(), cxt.getProductId(), cxt.getSerialNumber(), "", "", data.getTriggerType() == 5 ? "1" : "0");
                if (sceneThingsModelItems == null) {
                    sceneThingsModelItems = new ArrayList<>();
                }
                cxt.printDebugLog(this, "匹配项：%s", sceneItem.toString());
                sceneThingsModelItems.add(sceneItem);
                return "tag:" + index;
            }
        } else {
            cxt.printDebugLog(this, "触发类型不正确，流程结束！");
            this.setIsEnd(true);
        }
        return "tag:" + index;
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
