package com.fastbee.iot.model.varTemp;

import java.util.List;

import lombok.Data;

/**
 * @author bill
 */
@Data
public class SyncModel {

    private List<Long> productIds;
    private Long templateId;
}
