package com.fastbee.rule.cmp.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DevTriggerData extends BaseData {
    private Long productId;
    private String deviceId;
    private Integer type;
    private String modelId;
}
