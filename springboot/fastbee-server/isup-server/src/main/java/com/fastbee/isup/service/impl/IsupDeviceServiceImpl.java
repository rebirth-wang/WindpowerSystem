package com.fastbee.isup.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fastbee.common.enums.DeviceStatus;
import com.fastbee.isup.model.ChanStatusObj;
import com.fastbee.isup.model.IsupDevInfo;
import com.fastbee.isup.model.xml.InputProxyChannelStatus;
import com.fastbee.isup.model.xml.InputProxyChannelStatusList;
import com.fastbee.isup.model.xml.PpvspMessage;
import com.fastbee.isup.sdk.isapi.ISAPIService;
import com.fastbee.isup.sdk.service.impl.CmsUtil;
import com.fastbee.isup.service.IIsupDeviceService;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.service.ICommonChannelService;

@Slf4j
@Profile("isup")
@Service
public class IsupDeviceServiceImpl implements IIsupDeviceService {
    @Resource
    private ISAPIService isapiService;
    @Resource
    private CmsUtil cmsUtil;
    @Resource
    private ICommonChannelService commonChannelService;

    public ChanStatusObj GetWorkingstatus(int lLoginID) {
        return isapiService.getWorkingstatus(lLoginID);
    }

    @Override
    public List<Integer> getDeviceChannels(int lLoginID, Integer deviceType) {
        try {
            switch (deviceType) {
                case 2:
                    // DVR/NVR设备：获取所有数字通道状态
                    InputProxyChannelStatusList channelStatusList = isapiService.getAllDigitalChannelStatus(lLoginID);
                    if (channelStatusList != null && channelStatusList.getChannels() != null) {
                        return channelStatusList.getChannels().stream()
                                .filter(ch -> ch.getOnline() != null && ch.getOnline())
                                .map(InputProxyChannelStatus::getId)
                                .collect(Collectors.toList());
                    }
                    break;
                case 3:
                    // IPCamera设备：通过远程控制获取通道号
                    PpvspMessage msg = cmsUtil.CMS_XMLRemoteControl(lLoginID);
                    if (msg != null && msg.getParams() != null &&
                            msg.getParams().getDeviceStatusXML() != null &&
                            msg.getParams().getDeviceStatusXML().getChStatus() != null) {

                        String channelStr = msg.getParams().getDeviceStatusXML().getChStatus().getCh();
                        if (channelStr != null && !channelStr.isEmpty()) {
                            try {
                                // 尝试直接解析为数字
                                Integer channelId = Integer.valueOf(channelStr.trim());
                                return Collections.singletonList(channelId);
                            } catch (NumberFormatException e) {
                                // 尝试取第一个字符
                                try {
                                    Integer channelId = Integer.valueOf(String.valueOf(channelStr.charAt(0)));
                                    return Collections.singletonList(channelId);
                                } catch (Exception ex) {
                                    log.error("解析IPCamera通道号失败，通道字符串: {}", channelStr);
                                }
                            }
                        }
                    }
                    break;
                case 1:
                case 30:
                case 31:
                case 40:
                    return Collections.singletonList(1);
                default:
                    log.warn("不支持的设备类型: {}", deviceType);
                    return Collections.singletonList(1);
            }
        } catch (Exception e) {
            log.error("获取设备通道失败，设备类型: {}", deviceType, e);
        }
        return Collections.emptyList();
    }

    @Override
    public void updateDeviceChannels(IsupDevInfo device, List<Integer> onlineChannelIds) {
        Map<Integer, IsupDevInfo.Channel> existingChannels = device.getChannels().stream()
                .collect(Collectors.toMap(IsupDevInfo.Channel::getChannelId, ch -> ch));
        // 创建新的通道列表
        List<IsupDevInfo.Channel> newChannels = new ArrayList<>();
        // 更新或创建在线通道
        for (Integer channelId : onlineChannelIds) {
            IsupDevInfo.Channel channel = existingChannels.get(channelId);
            if (channel != null) {
                // 已存在的通道，标记为在线
                channel.setIsOnline(1);
                newChannels.add(channel);
            } else {
                // 新通道
                newChannels.add(new IsupDevInfo.Channel(channelId, 1));
                log.info("发现新通道: {}_{}", device.getDeviceId(), channelId);
            }
        }
        // 将不在线的通道标记为离线（保留历史通道）
        existingChannels.values().stream()
                .filter(ch -> !onlineChannelIds.contains(ch.getChannelId()))
                .forEach(ch -> {
                    if (ch.getIsOnline() == 1) {
                        ch.setIsOnline(0);
                        newChannels.add(ch);
                        log.info("通道离线: {}_{}", device.getDeviceId(), ch.getChannelId());
                    } else {
                        newChannels.add(ch);
                    }
                });

        device.setChannels(newChannels);
    }

    public void updateDeviceChannels(IsupDevInfo device, ChanStatusObj chanStatus) {
        Map<Integer, IsupDevInfo.Channel> existingChannels = device.getChannels().stream()
                .collect(Collectors.toMap(IsupDevInfo.Channel::getChannelId, ch -> ch));
        // 创建新的通道列表
        List<IsupDevInfo.Channel> newChannels = new ArrayList<>();
        // 更新或创建在线通道
        for (ChanStatusObj.ChanStatus channelstatus : chanStatus.getChanStatusList().getChanStatus()) {
            IsupDevInfo.Channel channel = existingChannels.get(channelstatus.getChanNo());
            if (channel != null) {
                // 已存在的通道，标记为在线
                channel.setIsOnline(channelstatus.getOnline());
                newChannels.add(channel);
            } else {
                // 新通道
                newChannels.add(new IsupDevInfo.Channel(channelstatus.getChanNo(), channelstatus.getOnline()));
                log.info("发现新通道: {}_{}", device.getDeviceId(), channelstatus.getChanNo());
            }
            // 更新数据库
             CommonChannel commonChannel = commonChannelService.selectCommonChannelByDeviceChannelId(device.getDeviceId(), channelstatus.getChanNo().toString());
             if (commonChannel != null) {
                 commonChannel.setModel(device.getModel());
                 commonChannel.setManufacture(device.getManufacturer());
                 commonChannel.setStatus(channelstatus.getOnline() == 1 ? DeviceStatus.ONLINE.getType() : DeviceStatus.OFFLINE.getType());
                 commonChannel.setChannelName(channelstatus.getName());
                 commonChannelService.updateWithCache(commonChannel);
             } else {
                 commonChannel = new CommonChannel();
                 commonChannel.setDeviceId(device.getDeviceId());
                 commonChannel.setChannelId(channelstatus.getChanNo().toString());
                 commonChannel.setChannelName(channelstatus.getName());
                 commonChannel.setModel(device.getModel());
                 commonChannel.setManufacture(device.getManufacturer());
                 commonChannel.setDataType(ChannelStreamType.ISUP);
                 commonChannelService.insertWithCache(commonChannel);
             }
        }
        device.setChannels(newChannels);
    }
}
