package com.fastbee.sip.service.impl;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.common.extend.config.SysSipConfig;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.model.*;
import com.fastbee.media.service.IPTZService;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.redisMsg.IRedisRpcPlayService;
import com.fastbee.sip.service.ISipDeviceService;
import com.fastbee.sip.service.ISipPtzCmdService;

@Slf4j
@Service(ChannelStreamType.PTZ_SERVICE + ChannelStreamType.GB28181)
public class PtzServiceForGBImpl implements IPTZService {
    @Autowired
    private ISipPtzCmdService ptzCmdService;
    @Autowired
    private ISipDeviceService sipDeviceService;
    @Autowired
    private SysSipConfig sysSipConfig;
    @Autowired
    private IRedisRpcPlayService redisRpcPlayService;

    @Override
    public Boolean ptz(CommonChannel channel, PtzInput imput) {
        SipDevice device = sipDeviceService.selectSipDeviceBySipId(channel.getDeviceId());
        if (device == null) {
            log.error("stopPlayback dev is null,deviceId:{},channelId:{}", channel.getDeviceId(), channel.getChannelId());
            return null;
        }
        // 判断设备是否属于当前平台, 如果不属于则发起自动调用
        if (!sysSipConfig.getServerId().equals(device.getServerId())) {
            return redisRpcPlayService.ptz(device.getServerId(), channel.getChannelId(), imput);
        }
        return ptzCmdService.ptz(device, channel.getChannelId(), imput);
    }

    @Override
    public Boolean frontEndCommand(CommonChannel channel, Integer cmdCode, Integer parameter1, Integer parameter2, Integer combindCode) {
        SipDevice device = sipDeviceService.selectSipDeviceBySipId(channel.getDeviceId());
        if (device == null) {
            log.error("stopPlayback dev is null,deviceId:{},channelId:{}", channel.getDeviceId(), channel.getChannelId());
            return null;
        }
        // 判断设备是否属于当前平台, 如果不属于则发起自动调用
        if (!sysSipConfig.getServerId().equals(device.getServerId())) {
            return redisRpcPlayService.frontEndCommand(device.getServerId(), channel.getChannelId(), cmdCode, parameter1, parameter2, combindCode);
        }
        return ptzCmdService.frontEndCommand(device.getDeviceSipId(), channel.getChannelId(), cmdCode, parameter1, parameter2, combindCode);
    }

    @Override
    public Boolean ptz(CommonChannel channel, FrontEndControlCodeForPTZ frontEndControlCode) {
        int cmdCode = 0;
        int panSpeed = 0;
        int titleSpeed = 0;
        int zoomSpeed = 0;
        if (frontEndControlCode != null) {
            if (frontEndControlCode.getPan() != null) {
                if (frontEndControlCode.getPan() == 0) {
                    cmdCode = cmdCode | 1 << 1;
                } else if (frontEndControlCode.getPan() == 1) {
                    cmdCode = cmdCode | 1;
                }
            }
            if (frontEndControlCode.getTilt() != null) {
                if (frontEndControlCode.getTilt() == 0) {
                    cmdCode = cmdCode | 1 << 3;
                } else if (frontEndControlCode.getTilt() == 1) {
                    cmdCode = cmdCode | 1 << 2;
                }
            }

            if (frontEndControlCode.getZoom() != null) {
                if (frontEndControlCode.getZoom() == 0) {
                    cmdCode = cmdCode | 1 << 5;
                } else if (frontEndControlCode.getZoom() == 1) {
                    cmdCode = cmdCode | 1 << 4;
                }
            }
            panSpeed = (int) (frontEndControlCode.getPanSpeed() / 100D * 255);
            titleSpeed = (int) (frontEndControlCode.getTiltSpeed() / 100D * 255);
            ;
            zoomSpeed = (int) (frontEndControlCode.getZoomSpeed() / 100D * 16);
        }
        this.frontEndCommand(channel, cmdCode, panSpeed, titleSpeed, zoomSpeed);
        return true;
    }

    @Override
    public Boolean preset(CommonChannel channel, FrontEndControlCodeForPreset frontEndControlCode) {
        int cmdCode = 0;
        int parameter1 = 0;
        int parameter2 = 0;
        int parameter3 = 0;
        if (frontEndControlCode != null) {
            if (frontEndControlCode.getCode() != null) {
                if (frontEndControlCode.getCode() == 1) {
                    cmdCode = 0x81;
                } else if (frontEndControlCode.getCode() == 2) {
                    cmdCode = 0x82;
                } else if (frontEndControlCode.getCode() == 3) {
                    cmdCode = 0x83;
                }
            }
            parameter2 = frontEndControlCode.getPresetId();
        }
        this.frontEndCommand(channel, cmdCode, parameter1, parameter2, parameter3);
        return true;
    }

    @Override
    public Boolean fi(CommonChannel channel, FrontEndControlCodeForFI frontEndControlCode) {
        int cmdCode = 1 << 6;
        int focusSpeed = 0;
        int irisSpeed = 0;
        int parameter3 = 0;
        if (frontEndControlCode != null) {
            if (frontEndControlCode.getFocus() != null) {
                if (frontEndControlCode.getFocus() == 0) {
                    cmdCode = cmdCode | 1 << 1;
                } else if (frontEndControlCode.getFocus() == 1) {
                    cmdCode = cmdCode | 1;
                } else {
                    log.error("[FI失败] 未知的聚焦指令 {}", frontEndControlCode.getFocus());
                }
            }
            if (frontEndControlCode.getIris() != null) {
                if (frontEndControlCode.getIris() == 0) {
                    cmdCode = cmdCode | 1 << 3;
                } else if (frontEndControlCode.getIris() == 1) {
                    cmdCode = cmdCode | 1 << 2;
                } else {
                    log.error("[FI失败] 未知的光圈指令 {}", frontEndControlCode.getIris());
                }
            }
            if (frontEndControlCode.getFocusSpeed() == null) {
                log.error("[FI失败] getFocusSpeed 为 null  {}", frontEndControlCode.getIris());
                return false;
            }
            if (frontEndControlCode.getIrisSpeed() == null) {
                log.error("[FI失败] getIrisSpeed 为 null  {}", frontEndControlCode.getIris());
                return false;
            }
            focusSpeed = frontEndControlCode.getFocusSpeed();
            irisSpeed = frontEndControlCode.getIrisSpeed();
        }
        this.frontEndCommand(channel, cmdCode, focusSpeed, irisSpeed, parameter3);
        return true;
    }

    @Override
    public Boolean tour(CommonChannel channel, FrontEndControlCodeForTour frontEndControlCode) {
        int cmdCode = 0;
        int parameter1 = 0;
        int parameter2 = 0;
        int parameter3 = 0;
        if (frontEndControlCode != null) {
            if (frontEndControlCode.getCode() != null) {
                if (frontEndControlCode.getCode() == 1) {
                    cmdCode = 0x84;
                    if (frontEndControlCode.getPresetId() == null) {
                        log.error("[巡航控制失败] getPresetId 为 null  {}", frontEndControlCode.getCode());
                        return false;
                    }
                    parameter2 = frontEndControlCode.getPresetId();
                } else if (frontEndControlCode.getCode() == 2) {
                    cmdCode = 0x85;
                    if (frontEndControlCode.getPresetId() == null) {
                        log.error("[巡航控制失败] getPresetId 为 null  {}", frontEndControlCode.getCode());
                        return false;
                    }
                    parameter2 = frontEndControlCode.getPresetId();
                } else if (frontEndControlCode.getCode() == 3) {
                    cmdCode = 0x86;
                    if (frontEndControlCode.getPresetId() == null) {
                        log.error("[巡航控制失败] getPresetId 为 null  {}", frontEndControlCode.getCode());
                        return false;
                    }
                    parameter2 = frontEndControlCode.getPresetId();
                    if (frontEndControlCode.getTourSpeed() == null) {
                        log.error("[巡航控制失败] getTourSpeed 为 null  {}", frontEndControlCode.getCode());
                        return false;
                    }
                    parameter3 = frontEndControlCode.getTourSpeed();
                } else if (frontEndControlCode.getCode() == 4) {
                    cmdCode = 0x87;
                    if (frontEndControlCode.getPresetId() == null) {
                        log.error("[巡航控制失败] getPresetId 为 null  {}", frontEndControlCode.getCode());
                        return false;
                    }
                    parameter2 = frontEndControlCode.getPresetId();
                    if (frontEndControlCode.getTourTime() == null) {
                        log.error("[巡航控制失败] getTourTime 为 null  {}", frontEndControlCode.getCode());
                        return false;
                    }
                    parameter3 = frontEndControlCode.getTourTime();
                } else if (frontEndControlCode.getCode() == 5) {
                    cmdCode = 0x88;
                } else if (frontEndControlCode.getCode() == 6) {
                } else {
                    log.error("[巡航控制失败] 未知的指令 {}", frontEndControlCode.getCode());
                    return false;
                }
                if (frontEndControlCode.getTourId() == null) {
                    log.error("[巡航控制失败] 参数异常 {}", frontEndControlCode.getCode());
                    return false;
                }
                parameter1 = frontEndControlCode.getTourId();
            }
        }
        this.frontEndCommand(channel, cmdCode, parameter1, parameter2, parameter3);
        return true;
    }

    @Override
    public Boolean scan(CommonChannel channel, FrontEndControlCodeForScan frontEndControlCode) {
        int cmdCode = 0;
        int parameter1 = 0;
        int parameter2 = 0;
        int parameter3 = 0;
        if (frontEndControlCode != null) {
            if (frontEndControlCode.getCode() != null) {
                if (frontEndControlCode.getCode() == 1) {
                    cmdCode = 0x89;
                    if (frontEndControlCode.getScanId() == null) {
                        log.error("[巡航控制失败] getScanId 为 null  {}", frontEndControlCode.getCode());
                        return false;
                    }
                    parameter1 = frontEndControlCode.getScanId();
                } else if (frontEndControlCode.getCode() == 2) {
                    cmdCode = 0x89;
                    if (frontEndControlCode.getScanId() == null) {
                        log.error("[巡航控制失败] getScanId 为 null  {}", frontEndControlCode.getCode());
                        return false;
                    }
                    parameter1 = frontEndControlCode.getScanId();
                    parameter2 = 1;
                } else if (frontEndControlCode.getCode() == 3) {
                    cmdCode = 0x89;
                    if (frontEndControlCode.getScanId() == null) {
                        log.error("[巡航控制失败] getScanId 为 null  {}", frontEndControlCode.getCode());
                        return false;
                    }
                    parameter1 = frontEndControlCode.getScanId();
                    parameter2 = 2;
                } else if (frontEndControlCode.getCode() == 4) {
                    cmdCode = 0x8A;
                    if (frontEndControlCode.getScanId() == null) {
                        log.error("[巡航控制失败] getScanId 为 null  {}", frontEndControlCode.getCode());
                        return false;
                    }
                    if (frontEndControlCode.getScanSpeed() == null) {
                        log.error("[巡航控制失败] getScanSpeed 为 null  {}", frontEndControlCode.getCode());
                        return false;
                    }
                    parameter1 = frontEndControlCode.getScanId();
                    parameter2 = frontEndControlCode.getScanSpeed();
                } else if (frontEndControlCode.getCode() == 5) {

                } else {
                    log.error("[巡航控制失败] 未知的指令 {}", frontEndControlCode.getCode());
                }
            }
        }
        this.frontEndCommand(channel, cmdCode, parameter1, parameter2, parameter3);
        return true;
    }

    @Override
    public Boolean auxiliary(CommonChannel channel, FrontEndControlCodeForAuxiliary frontEndControlCode) {
        int cmdCode = 0;
        int parameter1 = 0;
        int parameter2 = 0;
        int parameter3 = 0;
        if (frontEndControlCode != null) {
            if (frontEndControlCode.getCode() != null) {
                if (frontEndControlCode.getCode() == 1) {
                    cmdCode = 0x8C;
                    if (frontEndControlCode.getAuxiliaryId() == null) {
                        log.error("[辅助开关失败] getAuxiliaryId 为 null  {}", frontEndControlCode.getCode());
                        return false;
                    }
                    parameter1 = frontEndControlCode.getAuxiliaryId();
                } else if (frontEndControlCode.getCode() == 2) {
                    cmdCode = 0x8D;
                    if (frontEndControlCode.getAuxiliaryId() == null) {
                        log.error("[辅助开关失败] getAuxiliaryId 为 null  {}", frontEndControlCode.getCode());
                        return false;
                    }
                    parameter1 = frontEndControlCode.getAuxiliaryId();
                }else {
                    log.error("[辅助开关失败] 未知的指令 {}", frontEndControlCode.getCode());
                    return false;
                }
            }
        }
        this.frontEndCommand(channel, cmdCode, parameter1, parameter2, parameter3);
        return true;
    }

    @Override
    public Boolean wiper(CommonChannel channel, FrontEndControlCodeForWiper frontEndControlCode) {
        int cmdCode = 0;
        int parameter1 = 1;
        int parameter2 = 0;
        int parameter3 = 0;
        if (frontEndControlCode != null) {
            if (frontEndControlCode.getCode() != null) {
                if (frontEndControlCode.getCode() == 1) {
                    cmdCode = 0x8C;
                } else if (frontEndControlCode.getCode() == 2) {
                    cmdCode = 0x8D;
                }else {
                    log.error("[雨刷开关失败] 未知的指令 {}", frontEndControlCode.getCode());
                    return false;
                }
            }
        }
        this.frontEndCommand(channel, cmdCode, parameter1, parameter2, parameter3);
        return true;
    }

    @Override
    public List<Preset> queryPresetList(CommonChannel channel) {
        SipDevice device = sipDeviceService.selectSipDeviceBySipId(channel.getDeviceId());
        if (device == null) {
            log.error("stopPlayback dev is null,deviceId:{},channelId:{}", channel.getDeviceId(), channel.getChannelId());
            return null;
        }
        // 判断设备是否属于当前平台, 如果不属于则发起自动调用
        if (!sysSipConfig.getServerId().equals(device.getServerId())) {
            return redisRpcPlayService.queryPresetList(device.getServerId(), channel.getChannelId(), null);
        }
        ptzCmdService.presetQuery(device, channel.getChannelId());
        return null;
    }
}
