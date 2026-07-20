package com.fastbee.iot.cache.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.enums.DeviceStatus;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.mq.DeviceStatusBo;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.ModbusParams;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.domain.SubGateway;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.mapper.ModbusParamsMapper;
import com.fastbee.iot.mapper.ProductMapper;
import com.fastbee.iot.mapper.SubGatewayMapper;
import com.fastbee.iot.model.DeviceMetaData;

/**
 * @author bill
 */
@Service
@Slf4j
public class DeviceCacheImpl implements IDeviceCache {

    @Resource
    private RedisCache redisCache;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private SubGatewayMapper subGatewayMapper;
    @Resource
    private ModbusParamsMapper modbusParamsMapper;


    /**
     * 更新设备状态
     * 如果设备状态保持不变，更新redis设备最新在线时间
     * 如果设备状态更改，更新redis同时，更新MySQL数据库设备状态
     *
     * @param dto dto
     */
    @Override
    public void updateDeviceStatusCache(DeviceStatusBo dto, Device device) {

        Optional.ofNullable(device).orElseThrow(() -> new ServiceException("设备不存在" + "[{" + dto.getSerialNumber() + "}]"));
        if (dto.getStatus() == DeviceStatus.ONLINE) {
            /*redis设备在线列表*/
            redisCache.zSetAdd(RedisKeyBuilder.buildDeviceOnlineListKey(), dto.getSerialNumber(), DateUtils.getTimestampSeconds());
            device.setStatus(DeviceStatus.ONLINE.getType());
        } else if (DeviceStatus.FORBIDDEN == dto.getStatus()) {
            /*在redis设备在线列表移除设备*/
            redisCache.zRem(RedisKeyBuilder.buildDeviceOnlineListKey(), dto.getSerialNumber());
            //更新一下mysql的设备状态为离线
            device.setStatus(DeviceStatus.FORBIDDEN.getType());
        } else {
            /*在redis设备在线列表移除设备*/
            redisCache.zRem(RedisKeyBuilder.buildDeviceOnlineListKey(), dto.getSerialNumber());
            //更新一下mysql的设备状态为离线
            device.setStatus(DeviceStatus.OFFLINE.getType());
        }
        device.setUpdateTime(DateUtils.getNowDate());

    }

    /**
     * 获取设备在线总数
     *
     * @return 设备在线总数
     */
    @Override
    public long deviceOnlineTotal() {
        return redisCache.zSize(RedisKeyBuilder.buildDeviceOnlineListKey());
    }


    /**
     * 批量更新redis缓存设备状态
     *
     * @param serialNumbers 设备列表
     * @param status        状态
     */
    @Override
    public void updateBatchDeviceStatusCache(List<String> serialNumbers, DeviceStatus status) {
        if (CollectionUtils.isEmpty(serialNumbers)) {
            return;
        }
        for (String serialNumber : serialNumbers) {
            DeviceStatusBo statusBo = new DeviceStatusBo();
            statusBo.setStatus(status);
            statusBo.setSerialNumber(serialNumber);
            //this.updateDeviceStatusCache(statusBo);
        }
    }

    @Override
    public DeviceMetaData getDeviceMetaDataCache(String serialNumber) {
        String cacheKey = "device:metadata:" + serialNumber;
        if (redisCache.containsKey(cacheKey)) {
            Object cacheObject = redisCache.getCacheObject(cacheKey);
            if (!Objects.isNull(cacheObject)) {
                return JSON.parseObject(cacheObject.toString(), DeviceMetaData.class);
            }
        }
        DeviceMetaData deviceMetaData = new DeviceMetaData();
        Device device = deviceMapper.selectDeviceBySerialNumber(serialNumber);
        if (Objects.isNull(device)) {
            return null;
        }
        deviceMetaData.setDevice(device);
        Product product = productMapper.selectById(device.getProductId());
        if (Objects.isNull(product)) {
            log.warn("getDeviceMetaDataCache: 产品不存在, productId={}, serialNumber={}", device.getProductId(), serialNumber);
            return null;
        }
        deviceMetaData.setProduct(product);
        LambdaQueryWrapper<SubGateway> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubGateway::getSubClientId, serialNumber);
        SubGateway subGateway = subGatewayMapper.selectOne(queryWrapper);
        deviceMetaData.setSubGateway(subGateway);
        LambdaQueryWrapper<SubGateway> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(SubGateway::getParentClientId, serialNumber);
        List<SubGateway> subGatewayList = subGatewayMapper.selectList(queryWrapper2);
        deviceMetaData.setSubGatewayList(subGatewayList);
        LambdaQueryWrapper<ModbusParams> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(ModbusParams::getProductId, device.getProductId());
        ModbusParams modbusParams = modbusParamsMapper.selectOne(queryWrapper1);
        deviceMetaData.setModbusParams(modbusParams);
        if (Objects.isNull(deviceMetaData.getModbusParams())) {
            deviceMetaData.setModbusParams(new ModbusParams());
        }
        if (Objects.isNull(deviceMetaData.getSubGateway())) {
            deviceMetaData.setSubGateway(new SubGateway());
        }
        String jsonString = JSON.toJSONString(deviceMetaData);
        redisCache.setCacheObject(cacheKey, jsonString);
        return deviceMetaData;
    }

    @Override
    public void deleteDeviceProtocolDetailCache(String serialNumber) {
        // redis中删除设备协议缓存信息
        redisCache.deleteObject("device:metadata:" + serialNumber);
    }

    /**
     * 系统停止时，更新所有设备为离线状态
     */
    @PreDestroy
    public void resetDeviceStatus(){
        // redis中删除设备协议缓存信息
        redisCache.deleteObject("device:msg");
        deviceMapper.reSetDeviceStatus();
    }


}
