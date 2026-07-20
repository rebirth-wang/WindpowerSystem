package com.fastbee.rule.cmp.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConditionData extends BaseData {
    private String modelId;
    // 判断类型： 上报值：1 状态值：2
    private Integer type;
    private Integer triggerType;
    private List<Expression> expressions;

    @Data
    public static class Expression {
        private String from;
        private String to;
        private String operator;
        private String value;
    }
}
