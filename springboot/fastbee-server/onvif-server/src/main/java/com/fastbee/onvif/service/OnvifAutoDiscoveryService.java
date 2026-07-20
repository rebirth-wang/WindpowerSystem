package com.fastbee.onvif.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fastbee.iot.domain.Device;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.service.ICommonChannelService;
import com.fastbee.onvif.config.OnvifProperties;
import com.fastbee.onvif.domain.OnvifDeviceChannel;
import com.fastbee.onvif.domain.OnvifDiscoveryDevice;
import com.fastbee.onvif.util.OnvifClient;

/**
 * Discovers local ONVIF devices and syncs them into FastBee device/channel tables.
 */
@Slf4j
@Service
public class OnvifAutoDiscoveryService {

    private static final int STATUS_ONLINE = 3;
    private static final long ONVIF_CHANNEL_ONLINE = 1L;

    private final AtomicBoolean scanning = new AtomicBoolean(false);

    private final OnvifProperties properties;
    private final OnvifDiscoveryClient discoveryClient;
    private final IOnvifDeviceChannelService onvifDeviceChannelService;
    private final IDeviceService deviceService;
    private final ICommonChannelService commonChannelService;
    private final OnvifClient onvifClient;

    public OnvifAutoDiscoveryService(OnvifProperties properties,
                                     OnvifDiscoveryClient discoveryClient,
                                     IOnvifDeviceChannelService onvifDeviceChannelService,
                                     IDeviceService deviceService,
                                     ICommonChannelService commonChannelService,
                                     OnvifClient onvifClient) {
        this.properties = properties;
        this.discoveryClient = discoveryClient;
        this.onvifDeviceChannelService = onvifDeviceChannelService;
        this.deviceService = deviceService;
        this.commonChannelService = commonChannelService;
        this.onvifClient = onvifClient;
    }

    public void scanAndSync() {
        if (!scanning.compareAndSet(false, true)) {
            log.info("[ONVIF auto discovery] Previous scan is still running, skip this round");
            return;
        }
        try {
            List<OnvifDiscoveryDevice> devices = discoveryClient.discover(properties.getDiscoveryTimeout());
            log.info("[ONVIF auto discovery] Discovered {} device(s)", devices.size());
            for (OnvifDiscoveryDevice device : devices) {
                try {
                    syncDevice(device);
                } catch (Exception e) {
                    log.warn("[ONVIF auto discovery] Sync device {}:{} failed: {}",
                            device.getIp(), device.getPort(), e.getMessage());
                }
            }
        } finally {
            scanning.set(false);
        }
    }

    private void syncDevice(OnvifDiscoveryDevice discovered) {
        OnvifDeviceChannel channel = onvifDeviceChannelService.getChannelByIpAndPort(
                discovered.getIp(), discovered.getPort());
        boolean isNewChannel = channel == null;
        if (isNewChannel) {
            channel = new OnvifDeviceChannel();
            channel.setIp(discovered.getIp());
            channel.setPort(discovered.getPort());
            channel.setEnableAudio(0L);
            channel.setEnableMp4(0L);
        }
        channel.setStatus(ONVIF_CHANNEL_ONLINE);
        fillDefaultCredentials(channel);
        enrichDeviceInfo(channel);
        if (!StringUtils.hasText(channel.getName())) {
            channel.setName(buildChannelName(channel, discovered.getIp()));
        }
        channel.setRemark("WS-Discovery: " + nullToEmpty(discovered.getXaddr()));

        if (isNewChannel) {
            onvifDeviceChannelService.insertWithCache(channel);
        } else {
            onvifDeviceChannelService.updateWithCache(channel);
        }

        if (properties.isSyncToIotEnabled()) {
            syncExistingIotDeviceAndChannel(channel);
        }
    }

    private void fillDefaultCredentials(OnvifDeviceChannel channel) {
        if (!StringUtils.hasText(channel.getUsername()) && StringUtils.hasText(properties.getDefaultUsername())) {
            channel.setUsername(properties.getDefaultUsername());
        }
        if (!StringUtils.hasText(channel.getPassword()) && StringUtils.hasText(properties.getDefaultPassword())) {
            channel.setPassword(properties.getDefaultPassword());
        }
    }

    private void enrichDeviceInfo(OnvifDeviceChannel channel) {
        try {
            Map<String, String> info = onvifClient.getDeviceInformation(channel);
            setIfText(info.get("manufacturer"), channel::setManufacture);
            setIfText(info.get("model"), channel::setModel);
            setIfText(info.get("firmwareVersion"), channel::setFirmwareVersion);
            setIfText(info.get("serialNumber"), channel::setSerialNumber);
            setIfText(info.get("hardwareId"), channel::setHardwareId);
        } catch (Exception e) {
            log.debug("[ONVIF auto discovery] GetDeviceInformation failed for {}:{}: {}",
                    channel.getIp(), channel.getPort(), e.getMessage());
        }

        try {
            Map<String, String> capabilities = onvifClient.getCapabilities(channel);
            setIfText(capabilities.get("mediaServiceUrl"), channel::setMediaServiceUrl);
            setIfText(capabilities.get("ptzServiceUrl"), channel::setPtzServiceUrl);
            channel.setSupportPtz(StringUtils.hasText(channel.getPtzServiceUrl()) ? 1 : 0);
            channel.setPtzType(StringUtils.hasText(channel.getPtzServiceUrl()) ? 1L : 0L);
        } catch (Exception e) {
            log.debug("[ONVIF auto discovery] GetCapabilities failed for {}:{}: {}",
                    channel.getIp(), channel.getPort(), e.getMessage());
        }

        try {
            if (StringUtils.hasText(channel.getMediaServiceUrl())) {
                List<String> profiles = onvifClient.getProfiles(channel);
                if (!profiles.isEmpty()) {
                    channel.setProfileToken(profiles.get(0));
                }
            }
        } catch (Exception e) {
            log.debug("[ONVIF auto discovery] GetProfiles failed for {}:{}: {}",
                    channel.getIp(), channel.getPort(), e.getMessage());
        }

        if (!StringUtils.hasText(channel.getSerialNumber())) {
            channel.setSerialNumber(buildFallbackSerial(channel.getIp(), channel.getPort()));
        }
    }

    private void syncExistingIotDeviceAndChannel(OnvifDeviceChannel channel) {
        String serialNumber = channel.getSerialNumber();
        if (!StringUtils.hasText(serialNumber) || isFallbackSerial(channel, serialNumber)) {
            log.info("[ONVIF auto discovery] Skip iot/common sync for {}:{}, missing real serial number",
                    channel.getIp(), channel.getPort());
            return;
        }

        Device device = deviceService.selectDeviceBySerialNumber(serialNumber);
        if (device == null) {
            log.info("[ONVIF auto discovery] Skip iot/common sync for serial {}, iot_device not found", serialNumber);
            return;
        }

        Date now = new Date();
        device.setDeviceName(buildChannelName(channel, channel.getIp()));
        device.setStatus(STATUS_ONLINE);
        device.setNetworkIp(channel.getIp());
        device.setDeviceIp(channel.getIp());
        device.setDevicePort(channel.getPort());
        device.setActiveTime(now);
        device.setSummary(buildSummary(channel));
        device.setUpdateTime(now);
        deviceService.updateById(device);

        upsertCommonChannel(channel, device);
    }

    private void upsertCommonChannel(OnvifDeviceChannel channel, Device device) {
        String serialNumber = device.getSerialNumber();
        String commonChannelId = buildCommonChannelId(channel);
        CommonChannel commonChannel = findCommonChannel(channel);
        boolean isNew = commonChannel == null;
        if (isNew) {
            commonChannel = new CommonChannel();
            commonChannel.setRegisterTime(new Date());
            commonChannel.setDelFlag("0");
        }

        commonChannel.setDeviceId(serialNumber);
        commonChannel.setChannelId(commonChannelId);
        commonChannel.setTenantId(device.getTenantId());
        commonChannel.setProductId(device.getProductId());
        commonChannel.setChannelName(buildChannelName(channel, channel.getIp()));
        commonChannel.setDeviceType("ONVIF");
        commonChannel.setChannelType("ONVIF");
        commonChannel.setDataType(ChannelStreamType.ONVIF);
        commonChannel.setManufacture(nullToEmpty(channel.getManufacture()));
        commonChannel.setModel(nullToEmpty(channel.getModel()));
        commonChannel.setOwner(nullToEmpty(channel.getOwner()));
        commonChannel.setPassword(nullToEmpty(channel.getPassword()));
        commonChannel.setIpAddress(channel.getIp());
        commonChannel.setPort(channel.getPort());
        commonChannel.setPtzType(channel.getPtzType() != null ? channel.getPtzType() : 0L);
        commonChannel.setPtzTypeText(channel.getSupportPtz() != null && channel.getSupportPtz() == 1 ? "ONVIF PTZ" : "None");
        commonChannel.setStatus(STATUS_ONLINE);
        commonChannel.setStreamId(commonChannelId);
        commonChannel.setPlayUrl(channel.getLiveStreamTcp());
        commonChannel.setProxyUrl(channel.getMediaServiceUrl());
        commonChannel.setHasAudio(channel.getEnableAudio() != null ? channel.getEnableAudio().intValue() : 0);
        commonChannel.setHasBroadcast(0L);
        commonChannel.setCtrlConfig(buildCtrlConfig(channel));
        commonChannel.setExtendInfo(buildExtendInfo(channel));

        commonChannelService.upsertWithCache(commonChannel);
        if (commonChannel.getId() != null) {
            channel.setId(Math.toIntExact(commonChannel.getId()));
        }
    }

    private CommonChannel findCommonChannel(OnvifDeviceChannel channel) {
        if (channel.getId() != null) {
            CommonChannel commonChannel = commonChannelService.getById(Long.valueOf(channel.getId()));
            if (commonChannel != null && Objects.equals(commonChannel.getDataType(), ChannelStreamType.ONVIF)) {
                return commonChannel;
            }
        }
        LambdaQueryWrapper<CommonChannel> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommonChannel::getDataType, ChannelStreamType.ONVIF);
        lqw.eq(StringUtils.hasText(channel.getIp()), CommonChannel::getIpAddress, channel.getIp());
        lqw.eq(channel.getPort() != null, CommonChannel::getPort, channel.getPort());
        lqw.last("limit 1");
        return commonChannelService.getOne(lqw);
    }

    private String buildCommonChannelId(OnvifDeviceChannel channel) {
        if (StringUtils.hasText(channel.getIp()) && channel.getPort() != null) {
            return "ONVIF-" + channel.getIp() + "-" + channel.getPort();
        }
        if (StringUtils.hasText(channel.getSerialNumber())) {
            return "ONVIF-" + channel.getSerialNumber();
        }
        return channel.getId() == null ? "ONVIF-" + System.currentTimeMillis() : String.valueOf(channel.getId());
    }

    private String buildFallbackSerial(String ip, Integer port) {
        return "ONVIF-" + nullToEmpty(ip).replace(".", "") + "-" + (port != null ? port : 80);
    }

    private boolean isFallbackSerial(OnvifDeviceChannel channel, String serialNumber) {
        return buildFallbackSerial(channel.getIp(), channel.getPort()).equals(serialNumber);
    }

    private String buildChannelName(OnvifDeviceChannel channel, String fallbackIp) {
        if (StringUtils.hasText(channel.getName())) {
            return channel.getName();
        }
        String manufacture = nullToEmpty(channel.getManufacture()).trim();
        String model = nullToEmpty(channel.getModel()).trim();
        String name = (manufacture + " " + model).trim();
        return StringUtils.hasText(name) ? name : "ONVIF-" + fallbackIp;
    }

    private String buildSummary(OnvifDeviceChannel channel) {
        return "[{\"name\":\"protocol\",\"value\":\"ONVIF\"},"
                + "{\"name\":\"ip\",\"value\":\"" + escapeJson(channel.getIp()) + "\"},"
                + "{\"name\":\"port\",\"value\":\"" + channel.getPort() + "\"},"
                + "{\"name\":\"manufacturer\",\"value\":\"" + escapeJson(channel.getManufacture()) + "\"},"
                + "{\"name\":\"model\",\"value\":\"" + escapeJson(channel.getModel()) + "\"},"
                + "{\"name\":\"firmware\",\"value\":\"" + escapeJson(channel.getFirmwareVersion()) + "\"}]";
    }

    private String buildCtrlConfig(OnvifDeviceChannel channel) {
        return "{\"profileToken\":\"" + escapeJson(channel.getProfileToken()) + "\","
                + "\"supportPtz\":" + (channel.getSupportPtz() != null ? channel.getSupportPtz() : 0) + "}";
    }

    private String buildExtendInfo(OnvifDeviceChannel channel) {
        return "{\"onvifChannelId\":\"" + channel.getId() + "\","
                + "\"serialNumber\":\"" + escapeJson(channel.getSerialNumber()) + "\","
                + "\"mediaServiceUrl\":\"" + escapeJson(channel.getMediaServiceUrl()) + "\","
                + "\"ptzServiceUrl\":\"" + escapeJson(channel.getPtzServiceUrl()) + "\","
                + "\"profileToken\":\"" + escapeJson(channel.getProfileToken()) + "\"}";
    }

    private void setIfText(String value, java.util.function.Consumer<String> setter) {
        if (StringUtils.hasText(value)) {
            setter.accept(value);
        }
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String escapeJson(String value) {
        return nullToEmpty(value).replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
