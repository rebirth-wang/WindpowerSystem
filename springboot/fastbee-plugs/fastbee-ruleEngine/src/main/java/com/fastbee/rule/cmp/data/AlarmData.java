package com.fastbee.rule.cmp.data;

import lombok.Data;

@Data
public class AlarmData extends BaseData {
    private Integer cond;
    private Integer source;
    private Long id;
    private Integer notifyCount;
}
