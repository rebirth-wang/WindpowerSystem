package com.fastbee.isup.sdk.service.impl;

import java.util.Optional;

import jakarta.annotation.Resource;

import com.sun.jna.Pointer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fastbee.common.enums.DeviceStatus;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.iot.service.IDeviceUpdateService;
import com.fastbee.isup.config.HikIsupProperties;
import com.fastbee.isup.model.ChanStatusObj;
import com.fastbee.isup.model.IsupDevInfo;
import com.fastbee.isup.sdk.service.DEVICE_REGISTER_CB;
import com.fastbee.isup.sdk.service.HCISUPCMS;
import com.fastbee.isup.sdk.service.IHikISUPAlarm;
import com.fastbee.isup.sdk.service.constant.EHOME_REGISTER_TYPE;
import com.fastbee.isup.sdk.structure.NET_EHOME_DEV_REG_INFO_V12;
import com.fastbee.isup.sdk.structure.NET_EHOME_DEV_SESSIONKEY;
import com.fastbee.isup.sdk.structure.NET_EHOME_SERVER_INFO_V50;
import com.fastbee.isup.service.DeviceCacheService;
import com.fastbee.isup.service.IIsupDeviceService;

@Slf4j
@Service
@Profile("isup")
@RequiredArgsConstructor
public class FRegisterCallBack implements DEVICE_REGISTER_CB {
    private final HikIsupProperties hikIsupProperties;
    private final HCISUPCMS hcIsupCms;
    private final DeviceCacheService deviceCacheService;
    private final IHikISUPAlarm hikISUPAlarm;
    private final IIsupDeviceService isupDeviceService;
    @Resource
    private IDeviceService deviceService;

    @Resource
    private IDeviceUpdateService deviceUpdateService;

    @Override
    public boolean invoke(int lUserID, int dwDataType, Pointer pOutBuffer, int dwOutLen, Pointer pInBuffer, int dwInLen, Pointer pUser) {
       // log.info("设备注册状态回调FRegisterCallBack, dwDataType: {}, lUserID: {}", dwDataType, lUserID);
        NET_EHOME_DEV_REG_INFO_V12 strDevRegInfo = new NET_EHOME_DEV_REG_INFO_V12();
        Pointer pDevRegInfo = strDevRegInfo.getPointer();
        switch (dwDataType) {
            case EHOME_REGISTER_TYPE.ENUM_DEV_ON:
                // 设备上线回调
                strDevRegInfo.write();
                pDevRegInfo.write(0, pOutBuffer.getByteArray(0, strDevRegInfo.size()), 0, strDevRegInfo.size());
                strDevRegInfo.read();
                log.info("设备注册地址: {}:{}", new String(strDevRegInfo.struRegInfo.struDevAdd.szIP).trim(), strDevRegInfo.struRegInfo.struDevAdd.wPort);
                Integer deviceType = strDevRegInfo.struRegInfo.dwDevType;
                NET_EHOME_SERVER_INFO_V50 strEhomeServerInfo = new NET_EHOME_SERVER_INFO_V50();
                strEhomeServerInfo.read();
                // 设置报警服务器地址、端口、类型
                byte[] byCmsIP = hikIsupProperties.getExtendIp().getBytes();
                System.arraycopy(byCmsIP, 0, strEhomeServerInfo.struUDPAlarmSever.szIP, 0, byCmsIP.length);
                System.arraycopy(byCmsIP, 0, strEhomeServerInfo.struTCPAlarmSever.szIP, 0, byCmsIP.length);
                // 报警服务器类型：0- 只支持UDP协议上报，1- 支持UDP、TCP两种协议上报 2-MQTT
                strEhomeServerInfo.dwAlarmServerType = Integer.parseInt(hikIsupProperties.getAlarmServer().getType());
                strEhomeServerInfo.struTCPAlarmSever.wPort = Short.parseShort(hikIsupProperties.getAlarmServer().getListenTcpPort());
                strEhomeServerInfo.struUDPAlarmSever.wPort = Short.parseShort(hikIsupProperties.getAlarmServer().getListenTcpPort());
                byte[] byClouldAccessKey = hikIsupProperties.getPicServer().getAccessKey().getBytes();
                System.arraycopy(byClouldAccessKey, 0, strEhomeServerInfo.byClouldAccessKey, 0, byClouldAccessKey.length);
                byte[] byClouldSecretKey = hikIsupProperties.getPicServer().getSecretKey().getBytes();
                System.arraycopy(byClouldSecretKey, 0, strEhomeServerInfo.byClouldSecretKey, 0, byClouldSecretKey.length);
                strEhomeServerInfo.dwClouldPoolId = 1;
                // 设置图片存储服务器地址、端口、类型
                byte[] bySSIP = hikIsupProperties.getExtendIp().getBytes();
                System.arraycopy(bySSIP, 0, strEhomeServerInfo.struPictureSever.szIP, 0, bySSIP.length);
                strEhomeServerInfo.struPictureSever.wPort = Short.parseShort(hikIsupProperties.getPicServer().getListenPort());
                strEhomeServerInfo.dwPicServerType = Integer.parseInt(hikIsupProperties.getPicServer().getType());    //存储服务器（SS）类型：0-Tomcat，1-VRB，2-云存储，3-KMS，4-ISUP5.0。
                strEhomeServerInfo.write();
                dwInLen = strEhomeServerInfo.size();
                pInBuffer.write(0, strEhomeServerInfo.getPointer().getByteArray(0, dwInLen), 0, dwInLen);
                String deviceId = new String(strDevRegInfo.struRegInfo.byDeviceID).trim();
                log.info("设备上线, DeviceID: {}, LoginID: {} 设备类型: {}", deviceId, lUserID, deviceType);
                Device iotdev = deviceService.selectDeviceBySerialNumber(deviceId);
                if(iotdev != null) {
                    if (iotdev.getStatus() != DeviceStatus.ONLINE.getType() && iotdev.getStatus() != DeviceStatus.FORBIDDEN.getType()) {
                        iotdev.setStatus(DeviceStatus.ONLINE.getType());
                        deviceUpdateService.updateDeviceStatusAndLocation(iotdev, new String(strDevRegInfo.struRegInfo.struDevAdd.szIP).trim());
                    }
                }
                // 注册登录句柄映射
                deviceCacheService.registerLoginId(lUserID, deviceId);
                // 获取或创建设备对象
                IsupDevInfo device = deviceCacheService.getByDeviceId(deviceId).orElse(new IsupDevInfo());
                device.setDeviceId(deviceId);
                // 设备类型：1-数字视频录像机，3-数字视频服务器，30-网络摄像机，40-网络球机。
                device.setDeviceType(deviceType);
                device.setIsOnline(1);
                device.setLoginId(lUserID);
                // 获取通道列表
                ChanStatusObj chanStatus = isupDeviceService.GetWorkingstatus(lUserID);
                if (chanStatus == null || chanStatus.getChanStatusList() == null || chanStatus.getChanStatusList().getChanStatus().isEmpty()) {
                    log.warn("未能获取设备通道信息，设备ID: {}, 类型: {}", deviceId, deviceType);
                } else {
                    // 更新通道列表
                    isupDeviceService.updateDeviceChannels(device, chanStatus);
                }
                deviceCacheService.saveOrUpdate(device);
                break;
            case EHOME_REGISTER_TYPE.ENUM_DEV_OFF:
                log.info("设备下线回调 Device off, lUserID is: {}", lUserID);
                Optional<IsupDevInfo> deviceOpt = deviceCacheService.getByLoginId(lUserID);
                if (!deviceOpt.isPresent()) {
                    log.warn("未找到登录句柄{}对应的设备", lUserID);
                } else {
                    IsupDevInfo deviceOffline = deviceOpt.get();
                    deviceCacheService.removeByLoginId(lUserID);
                    log.info("设备下线 {}，影响{}个通道", deviceOffline.getDeviceId(), deviceOffline.getChannels().size());
                    Device devOffline = deviceService.selectDeviceBySerialNumber(deviceOffline.getDeviceId());
                    if(devOffline != null) {
                        devOffline.setStatus(DeviceStatus.OFFLINE.getType());
                        deviceUpdateService.updateDeviceStatusAndLocation(devOffline, null);
                    }
                }
                break;
            case EHOME_REGISTER_TYPE.ENUM_DEV_AUTH:
                // Ehome5.0设备认证回调
                strDevRegInfo.write();
                pDevRegInfo.write(0, pOutBuffer.getByteArray(0, strDevRegInfo.size()), 0, strDevRegInfo.size());
                strDevRegInfo.read();
                // ISUP5.0登录校验值
                // todo: 产品可配置接入秘钥
                String szEHomeKey = hikIsupProperties.getIsupKey();
                byte[] bs = szEHomeKey.getBytes();
                pInBuffer.write(0, bs, 0, szEHomeKey.length());
                log.info("Ehome5.0设备认证回调 Device auth, DeviceID is: {}", new String(strDevRegInfo.struRegInfo.byDeviceID).trim());
                break;
            case EHOME_REGISTER_TYPE.ENUM_DEV_SESSIONKEY:
                // Ehome5.0设备Sessionkey回调
                strDevRegInfo.write();
                pDevRegInfo.write(0, pOutBuffer.getByteArray(0, strDevRegInfo.size()), 0, strDevRegInfo.size());
                strDevRegInfo.read();
                NET_EHOME_DEV_SESSIONKEY struSessionKey = new NET_EHOME_DEV_SESSIONKEY();
                System.arraycopy(strDevRegInfo.struRegInfo.byDeviceID, 0, struSessionKey.sDeviceID, 0, strDevRegInfo.struRegInfo.byDeviceID.length);
                System.arraycopy(strDevRegInfo.struRegInfo.bySessionKey, 0, struSessionKey.sSessionKey, 0, strDevRegInfo.struRegInfo.bySessionKey.length);
                struSessionKey.write();
                Pointer pSessionKey = struSessionKey.getPointer();
                hcIsupCms.NET_ECMS_SetDeviceSessionKey(pSessionKey);
                log.info("Ehome5.0设备 Sessionkey回调 Device session key, DeviceID is: {}", new String(strDevRegInfo.struRegInfo.byDeviceID).trim());
                hikISUPAlarm.NET_EALARM_SetDeviceSessionKey(pSessionKey);
                break;
            case EHOME_REGISTER_TYPE.ENUM_DEV_DAS_REQ:
                String dasInfo = "{\n" +
                        "    \"Type\":\"DAS\",\n" +
                        "    \"DasInfo\": {\n" +
                        "        \"Address\":\"" + hikIsupProperties.getExtendIp() + "\",\n" +
                        "        \"Domain\":\"\",\n" +
                        "        \"ServerID\":\"\",\n" +
                        "        \"Port\":" + hikIsupProperties.getDasPort() + ",\n" +
                        "        \"UdpPort\":\n" +
                        "    }\n" +
                        "}";
                byte[] bs1 = dasInfo.getBytes();
                pInBuffer.write(0, bs1, 0, dasInfo.length());
                log.info("Ehome5.0设备DAS请求回调 Device DAS request: {}", dasInfo);
                break;
            default:
                log.info("FRegisterCallBack default type: {}", dwDataType);
                break;
        }
        return true;
    }
}
