package com.fastbee.sip.redisMsg;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.sip.domain.SipDevice;

public interface IRedisRpcService {

    void rtpSendStopped(String callId);

    void removeCallback(long key);

    void subscribeCatalog(int id, int cycle);

    void subscribeMobilePosition(int id, int cycle, int interval);

    AjaxResult devicesSync(String serverId, String deviceId);

    AjaxResult deviceConfigQuery(String serverId, SipDevice device, String channelId, String configType);

    AjaxResult deviceStatus(String serverId, SipDevice device);

    AjaxResult alarm(String serverId, SipDevice device, String startPriority, String endPriority, String alarmMethod, String alarmType, String startTime, String endTime);

    AjaxResult deviceInfo(String serverId, SipDevice device);

    AjaxResult queryPreset(String serverId, SipDevice device, String channelId);
}
