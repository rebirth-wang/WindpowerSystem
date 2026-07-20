package com.fastbee.rule.cmp.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DevExecuteCData extends BaseData {
    private Integer cond;
    private String deviceId;
    private Long productId;
    private Integer type;
    private String modelId;
    private String value;
}
