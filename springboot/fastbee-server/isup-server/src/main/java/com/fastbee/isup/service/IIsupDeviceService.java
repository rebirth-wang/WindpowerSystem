package com.fastbee.isup.service;

import java.util.List;

import com.fastbee.isup.model.ChanStatusObj;
import com.fastbee.isup.model.IsupDevInfo;

public interface IIsupDeviceService {
    ChanStatusObj GetWorkingstatus(int lLoginID);
    List<Integer> getDeviceChannels(int lLoginID, Integer deviceType);
    void updateDeviceChannels(IsupDevInfo device, List<Integer> onlineChannelIds);
    void updateDeviceChannels(IsupDevInfo device, ChanStatusObj chanStatus);
}
