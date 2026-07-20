package com.fastbee.iot.service.impl;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.fastbee.common.constant.Constants;
import com.fastbee.common.enums.DeviceStatus;
import com.fastbee.common.extend.enums.DeviceLocationWayEnum;
import com.fastbee.common.extend.enums.DeviceStatusEnum;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.http.HttpUtils;
import com.fastbee.common.utils.ip.AddressUtils;
import com.fastbee.common.utils.ip.IpUtils;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.cache.ITSLValueCache;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceLog;
import com.fastbee.iot.enums.DeviceEventType;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.service.IDeviceUpdateService;
import com.fastbee.iot.tsdb.service.ILogService;

/**
 * 设备告警Service业务层处理
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Slf4j
@Service
public class DeviceUpdateServiceImpl extends ServiceImpl<DeviceMapper, Device> implements IDeviceUpdateService {

        @Resource
        private DeviceMapper deviceMapper;
        @Resource
        private IDeviceCache deviceCache;
        @Resource
        private ITSLValueCache itslValueCache;
        @Resource
        private ILogService logService;

        /**
         * 修改设备
         *
         * @param device 设备
         * @return 结果
         */
        @Caching(evict = {
                @CacheEvict(cacheNames = "Device", key = "'selectDeviceBySerialNumber:' + #device.getSerialNumber()"),
//            @CacheEvict(cacheNames = "Device", key = "'selectDeviceByDeviceId:' + #device.getSerialNumber()"),
                @CacheEvict(cacheNames = "Device", key = "'selectDeviceStatusAndTransportStatus:' + #device.getSerialNumber()"),
                @CacheEvict(cacheNames = "Device", key = "'selectProtocolBySerialNumber:' + #device.getSerialNumber()"),
                @CacheEvict(cacheNames = "Device", key = "'selectShortDeviceBySerialNumber:' + #device.getSerialNumber()")
        })
        @Override
        public int updateDevice(Device device) {
                // redis中删除设备协议缓存信息
                deviceCache.deleteDeviceProtocolDetailCache(device.getSerialNumber());
                device.setUpdateTime(DateUtils.getNowDate());
                // 未激活状态,可以修改产品以及物模型值
                if (DeviceStatusEnum.INACTIVE.getStatus().equals(device.getStatus())) {
                        // 缓存设备状态（物模型值）
                        itslValueCache.addCacheDeviceStatus(device.getProductId(), device.getSerialNumber());

                } else {
                        device.setProductId(null);
                        device.setProductName(null);
                }
                return deviceMapper.updateById(device);
        }

        @Caching(evict = {
                @CacheEvict(cacheNames = "Device", key = "'selectDeviceBySerialNumber:' + #device.getSerialNumber()"),
//            @CacheEvict(cacheNames = "Device", key = "'selectDeviceByDeviceId:' + #device.getSerialNumber()"),
                @CacheEvict(cacheNames = "Device", key = "'selectDeviceStatusAndTransportStatus:' + #device.getSerialNumber()"),
                @CacheEvict(cacheNames = "Device", key = "'selectProtocolBySerialNumber:' + #device.getSerialNumber()"),
                @CacheEvict(cacheNames = "Device", key = "'selectShortDeviceBySerialNumber:' + #device.getSerialNumber()")
        })
        @Override
        public int updateDeviceBySerialNumber(Device device) {
                // redis中删除设备协议缓存信息
                deviceCache.deleteDeviceProtocolDetailCache(device.getSerialNumber());
                LambdaUpdateWrapper<Device> wrapper = new LambdaUpdateWrapper<>();
                wrapper.eq(Device::getSerialNumber, device.getSerialNumber());
                return deviceMapper.update(device, wrapper);
        }

        /**
         * @param device 设备状态和定位更新
         * @return 结果
         */
        @Override
        @DSTransactional(rollbackFor = Exception.class)
        public int updateDeviceStatusAndLocation(Device device, String ipAddress) {
                // 设置自动定位和状态
                if (ipAddress != null && !Objects.equals(ipAddress, "")) {
                        if (device.getActiveTime() == null) {
                                device.setActiveTime(DateUtils.getNowDate());
                        }
                        // 定位方式(1=ip自动定位,2=设备定位,3=自定义)
                        if (DeviceLocationWayEnum.IP_AUTO.getWay().equals(device.getLocationWay())) {
                                device.setNetworkIp(ipAddress);
                                setLocation(ipAddress, device);
                        }
                }
                device.setUpdateTime(DateUtils.getNowDate());
                // redis中删除设备协议缓存信息
                deviceCache.deleteDeviceProtocolDetailCache(device.getSerialNumber());
                int result = deviceMapper.updateDeviceStatus(device);
                DeviceLog deviceLog = new DeviceLog();
                byte isMonitor = 0;
                byte mode = 3;
                deviceLog.setDeviceId(device.getDeviceId());
                deviceLog.setDeviceName(device.getDeviceName());
                deviceLog.setSerialNumber(device.getSerialNumber());
                deviceLog.setIsMonitor(isMonitor);
                deviceLog.setTenantId(device.getTenantId());
                deviceLog.setUserId(device.getTenantId());
                deviceLog.setUserName(device.getTenantName());
                deviceLog.setTenantName(device.getTenantName());
                deviceLog.setCreateTime(DateUtils.getNowDate());
                deviceLog.setCreateBy(device.getCreateBy());
                deviceLog.setMode(mode);
                if (DeviceStatusEnum.ONLINE.getStatus().equals(device.getStatus())) {
                        deviceLog.setLogValue("1");
                        deviceLog.setRemark("设备上线");
                        deviceLog.setIdentify("online");
                        deviceLog.setLogType(DeviceEventType.DEV_ONLINE.getCode().byteValue());
                        log.info("设备上线,sn:{}", device.getSerialNumber());
                } else if (DeviceStatusEnum.OFFLINE.getStatus().equals(device.getStatus())) {
                        deviceLog.setLogValue("0");
                        deviceLog.setRemark("设备离线");
                        deviceLog.setIdentify("offline");
                        deviceLog.setLogType(DeviceEventType.DEV_OFFLINE.getCode().byteValue());
                        log.info("设备离线,sn:{}", device.getSerialNumber());
                } else if (DeviceStatusEnum.FORBIDDEN.getStatus().equals(device.getStatus())) {
                        deviceLog.setLogValue("2");
                        deviceLog.setRemark("设备禁用");
                        deviceLog.setIdentify("disable");
                        byte logType = 8;
                        deviceLog.setLogType(logType);
                        log.info("设备禁用,sn:{}", device.getSerialNumber());
                }
                logService.saveDeviceLog(deviceLog);
                return result;
        }

        @Override
        public void updateProductNameByProductId(Long productId, String productName) {
                LambdaUpdateWrapper<Device> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.set(Device::getProductName, productName);
                updateWrapper.eq(Device::getProductId, productId);
                deviceMapper.update(null, updateWrapper);
        }

        /**
         * @param device 设备状态
         * @return 结果
         */
        @Override
        public int updateDeviceStatus(Device device) {
                // redis中删除设备协议缓存信息
                deviceCache.deleteDeviceProtocolDetailCache(device.getSerialNumber());
                if (device.getActiveTime() == null && device.getStatus() == DeviceStatus.ONLINE.getType()) {
                        device.setActiveTime(DateUtils.getNowDate());
                }
                device.setUpdateTime(DateUtils.getNowDate());
                return deviceMapper.updateDeviceStatus(device);
        }

        /**
         * 根据IP获取地址
         *
         * @param ip
         * @return
         */
        private void setLocation(String ip, Device device) {
                String address = "未知地址";
                if (IpUtils.internalIp(ip)) {
                        address = "内网IP";
                } else {
                        if (StringUtils.isEmpty(AddressUtils.apiConfig.getIpplus360Key())) {
                                log.warn("api.ipplus360Key值未配置，使用免费接口!");
                                JSONObject obj = AddressUtils.getResBywWhois(ip);
                                setLatitudeAndLongitude(obj.getString("city"), device);
                        } else {
                                JSONObject obj = AddressUtils.getResByIpplus360(ip);
                                if (Objects.equals(obj.getString("code"), "Success")) {
                                        JSONObject data = obj.getJSONObject("data");
                                        String region = data.getString("prov");
                                        String city = data.getString("city");
                                        address = String.format("%s %s", region, city);
                                        device.setLatitude(new BigDecimal(data.getString("lat")));
                                        device.setLongitude(new BigDecimal(data.getString("lng")));
                                }
                        }
                }
                device.setNetworkAddress(address);
        }

        /**
         * 设置经纬度
         *
         * @param city
         */
        private void setLatitudeAndLongitude(String city, Device device) {
                String BAIDU_URL = "https://api.map.baidu.com/geocoder";
                String baiduResponse = HttpUtils.sendGet(BAIDU_URL, "address=" + city + "&output=json", Constants.GBK);
                if (!StringUtils.isEmpty(baiduResponse)) {
                        JSONObject baiduObject = JSONObject.parseObject(baiduResponse);
                        JSONObject location = baiduObject.getJSONObject("result").getJSONObject("location");
                        device.setLongitude(location.getBigDecimal("lng"));
                        device.setLatitude(location.getBigDecimal("lat"));
                }
        }
}
