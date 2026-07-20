package com.fastbee.rule.context;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.slot.DefaultContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.text.StringSubstitutor;

import com.fastbee.common.extend.enums.EnableEnum;
import com.fastbee.rule.cmp.data.BaseData;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseContext extends DefaultContext {
    private Long ruleId;
    /**
     * 设备编号
     */
    private String serialNumber;
    /**
     * 产品id
     */
    private Long productId;

    private Integer triggerType;
    /**
     * 调试上下文
     */
    private RuleDebugContext debugContext;
    /**
     * 是否启用调试模式
     */
    private Boolean debugEnabled = false;

    private Object result;

    public void printDebugLog(Integer debug, String nodeId, String format, Object... args) {
        if (Objects.equals(debug, EnableEnum.ENABLE.getType())) {
            if (this.debugContext.getDebugMsg() == null) {
                this.debugContext.setDebugMsg(String.format("[%d/%s] %s \r\n", ruleId, nodeId, String.format(format, args)));
            } else {
                this.debugContext.setDebugMsg(this.debugContext.getDebugMsg() + String.format("[%d/%s] %s \r\n", ruleId, nodeId, String.format(format, args)));
            }
        }
    }

    public void printDebugLog(NodeComponent node, String format, Object... args) {
        BaseData data = node.getCmpData(BaseData.class);
        this.printDebugLog(data.getDebug(), node.getTag(), format, args);
    }

    //"{ topic:${topic}, payload:${payload} }";
    // 自定义占位符，可在脚本中使用msgContext.setData("test":1);
    // 然后通过${test}调用，可在输出侧http body和 sql语句中传入该值
    public String placeholders(String str) {
        ConcurrentHashMap<String, Object> substitutorMap = new ConcurrentHashMap<>(this.dataMap);
        if(this.productId != null) {
            substitutorMap.put("productId", this.productId);
        }
        if(this.serialNumber != null) {
            substitutorMap.put("serialNumber", this.serialNumber);
        }
        if(this.ruleId != null) {
            substitutorMap.put("ruleId", ruleId);
        }
        if(this.triggerType != null) {
            substitutorMap.put("triggerType", triggerType);
        }
        StringSubstitutor substitutor = new StringSubstitutor(substitutorMap);
        return substitutor.replace(str);
    }

    // 上下文参数替换
    // 支持 baseContext.result,userList.get(0),dataMap.get('key'),nameArray[0]等表达式取值
    // 脚本中通过 baseContext.getContextValue(_meta.cmp,"baseContext.result"); 来取值
    public String getContextValue(NodeComponent node, String str) {
        return node.getContextValue(str);
    }
}
