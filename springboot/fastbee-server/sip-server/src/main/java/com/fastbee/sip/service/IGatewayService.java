package com.fastbee.sip.service;

import java.util.List;

import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;

public interface IGatewayService {
    void sendFunction(String deviceID,List<ThingsModelSimpleItem> functinos);
    void sendFunction(String deviceID,String identifier,String value);
}
