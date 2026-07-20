package com.fastbee.iot.data.service;

import com.fastbee.common.extend.core.domin.mq.DeviceTestReportBo;

/**
 * @author bill
 */
public interface IDeviceTestService {

    public void messageHandler(DeviceTestReportBo testReportBo);
}
