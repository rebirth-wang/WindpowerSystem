package com.fastbee.rule.parser.enums;

import com.fastbee.rule.parser.enums.core.IDictItem;
import com.fastbee.rule.parser.enums.core.StaticDictPool;

public interface RuleEnums {

    enum PATH_ENUM implements IDictItem {
        common_path("common","普通路径"),
        to_path("to","to(Switch路径)"),
        default_path("default","default(Switch路径)"),
        boolean_path("boolean","boolean(Boolean路径)"),
        true_path("true","true(If路径)"),
        false_path("false","false(If路径)"),
        do_path("do","do路径"),
        break_path("break","break路径"),
        ;

        private String value;
        private String label;

        PATH_ENUM(String value, String label){
            this.value = value;
            this.label = label;
            StaticDictPool.putDictItem(this,value,label);
        }

        public String getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }
    }

    enum NodeEnum implements IDictItem {

        COMMON("common","common", "普通组件"),
        SWITCH("switch","switch", "选择组件"),
        IF("if","if", "条件组件"),
        BOOLEAN("boolean","boolean", "布尔组件"),
        WHILE("while","while", "循环条件组件"),
        FOR("for","for", "循环次数组件"),
        ITERATOR("iterator","iterator", "循环迭代组件"),

        SCRIPT("script","script", "脚本组件"),
        SWITCH_SCRIPT("script","switch_script", "选择脚本组件"),
        IF_SCRIPT("script","if_script", "条件脚本组件"),
        BOOLEAN_SCRIPT("script","boolean_script", "布尔脚本组件"),
        WHILE_SCRIPT("script","while_script", "循环条件脚本组件"),
        FOR_SCRIPT("script","for_script", "循环次数脚本组件"),
        ITERATOR_SCRIPT("script","iterator_script", "循环迭代脚本组件"),

//        FALLBACK("fallback","fallback", "降级组件"),

        CHAIN("chain","chain", "链路组件"),
        CONTEXT("context","context", "数据上下文"),
        ROUTER("router","router", "策略路由"),
        SUB_FLOW("subflow","subflow", "子流程"),
        SUB_VAR("subvar","subvar", "子变量"),
        THEN("then","then", "串行"),
        WHEN("when","when", "并行"),
        NOTE("note","note", "注释"),

        AND("and","and", "与"),
        OR("or","or", "或"),
        NOT("not","not", "非"),

        //自定义规则组件
        START("start","start", "开始节点"),
        END("end","end", "结束节点"),

        DEV_TRIGGER("devTrigger","if", "设备触发"),
        PRODUCT_TRIGGER("productTrigger","if", "产品触发"),
        SCHEDULED_TRIGGER("scheduledTrigger","if", "定时触发"),
        CUSTOM_TRIGGER("customTrigger","if", "自定义触发"),
        DEV_EXECUTE("devExecute","common", "设备执行"),
        PRODUCT_EXECUTE("productExecute","common", "产品执行"),
        BRIDGE("bridge","common", "桥接组件"),
        CONDITION("condition","boolean", "条件判断"),
        CONDITION_SWITCH("conditionSwitch","switch", "多分支条件判断"),
        DELAY("delay","common", "延迟"),
        ALARM("alarm","common", "告警"),
        ;

        private String type;
        private String subType;
        private String label;

        NodeEnum(String type, String subType, String label){
            this.type = type;
            this.subType = subType;
            this.label = label;
            StaticDictPool.putDictItem(this,type,label,subType);
        }

        public String getType() {
            return type;
        }

        public String getSubType() {
            return subType;
        }

        public String getLabel() {
            return label;
        }
    }

}
