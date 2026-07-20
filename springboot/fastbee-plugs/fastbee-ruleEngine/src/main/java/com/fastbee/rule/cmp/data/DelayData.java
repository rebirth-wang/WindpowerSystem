package com.fastbee.rule.cmp.data;

import lombok.Data;

@Data
public class DelayData extends BaseData {
    private Integer cond;
    private Integer delay;
    private Integer delayType;
}
