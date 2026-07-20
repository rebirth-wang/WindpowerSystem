package com.fastbee.iot.cache.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.cache.IModbusConfigCache;
import com.fastbee.iot.domain.ModbusConfig;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.enums.DeviceType;
import com.fastbee.iot.mapper.ProductMapper;
import com.fastbee.iot.service.IModbusConfigService;

/**
 * @author gsb
 * @date 2024/6/13 9:45
 */
@Service
public class ModbusConfigCacheImpl implements IModbusConfigCache {

    @Resource
    private IModbusConfigService modbusConfigService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private ProductMapper productMapper;

    /**
     * 获取modbus参数 --返回 Map<String, ModbusConfig>
     * @param productId
     * @return
     *  <p>
     *      返回key-value
     *      key: 物模型标识符
     *      value：ModbusConfig
     *  </p>
     */
    @Override
    public Map<Integer, List<ModbusConfig>> getModbusConfigCacheByProductId(Long productId, String directConnAddress){
        Map<Integer, List<ModbusConfig>> resultMap = new HashMap<>();
        String modbusKey;
        if (StringUtils.isNotEmpty(directConnAddress)) {
            modbusKey = RedisKeyBuilder.buildModbusKey(productId, directConnAddress);
        } else {
            modbusKey = RedisKeyBuilder.buildModbusKey(productId);
        }
        Map<String,String> map = redisCache.hashEntity(modbusKey);
        if (!CollectionUtils.isEmpty(map)){
            for (Map.Entry<String, String> entry : map.entrySet()) {
                List<ModbusConfig> modbusConfigList = JSON.parseArray(entry.getValue(), ModbusConfig.class);
                resultMap.put(Integer.parseInt(entry.getKey()), modbusConfigList);
            }
            return resultMap;
        }
        setModbusConfigCacheByProductId(productId);
        return redisCache.hashEntity(modbusKey);
    }

    /**
     * 缓存modbus参数-返回Map
     * @param productId
     *  <p>
     *      返回key-value
     *      key: 物模型标识符
     *      value：ModbusConfig
     *  </p>
     */
    @Override
    public void setModbusConfigCacheByProductId(Long productId){
        Product product = productMapper.selectById(productId);
        Map<String,String> resultMap = new HashMap<>(2);
        ModbusConfig modbusConfig = new ModbusConfig();
        modbusConfig.setProductId(productId);
        List<ModbusConfig> modbusConfigList = modbusConfigService.selectShortListByProductId(modbusConfig);
        if (DeviceType.DIRECT_DEVICE.getCode() == product.getDeviceType()){
            Map<String, List<ModbusConfig>> addressMap = modbusConfigList.stream().collect(Collectors.groupingBy(ModbusConfig::getAddress));
            for (Map.Entry<String, List<ModbusConfig>> entry : addressMap.entrySet()) {
                List<ModbusConfig> modbusConfigs = entry.getValue();
                Map<Integer, List<ModbusConfig>> registerMap = modbusConfigs.stream().collect(Collectors.groupingBy(ModbusConfig::getRegister));
                Map<String,String> resultRegisterMap = new HashMap<>(2);
                for (Map.Entry<Integer, List<ModbusConfig>> registerEntry : registerMap.entrySet()) {
                    List<ModbusConfig> value = registerEntry.getValue();
                    String valueStr = JSONObject.toJSONString(value);
                    resultRegisterMap.put(registerEntry.getKey() + "", valueStr);
                }
                String modbusKey = RedisKeyBuilder.buildModbusKey(productId, entry.getKey());
                redisCache.deleteStrHash(modbusKey);
                redisCache.hashPutAll(modbusKey, resultRegisterMap);
            }
        } else {
            Map<Integer, List<ModbusConfig>> listMap = modbusConfigList.stream().collect(Collectors.groupingBy(ModbusConfig::getRegister));
            for (Map.Entry<Integer, List<ModbusConfig>> entry : listMap.entrySet()) {
                List<ModbusConfig> value = entry.getValue();
                String valueStr = JSONObject.toJSONString(value);
                resultMap.put(entry.getKey()+"", valueStr);
            }
            String modbusKey = RedisKeyBuilder.buildModbusKey(productId);
            redisCache.deleteStrHash(modbusKey);
            redisCache.hashPutAll(modbusKey, resultMap);
        }
    }
    /**
     * 获取单个modbus参数缓存值
     * @param productId 产品id
     * @param register 标识符
     * @return ModbusConfig
     */
    @Override
    public List<ModbusConfig> getSingleModbusConfig(Long productId, Integer register, String directConnAddress){
        String modbusKey;
        if (StringUtils.isNotEmpty(directConnAddress)) {
            modbusKey = RedisKeyBuilder.buildModbusKey(productId, directConnAddress);
        } else {
            modbusKey = RedisKeyBuilder.buildModbusKey(productId);
        }
        String cacheMapValue = redisCache.getCacheMapValue(modbusKey, String.valueOf(register));
        if (!StringUtils.isEmpty(cacheMapValue)){
            return JSON.parseArray(cacheMapValue, ModbusConfig.class);
        }
        ModbusConfig modbusConfig = new ModbusConfig();
        modbusConfig.setProductId(productId);
        modbusConfig.setRegister(register);
        List<ModbusConfig> modbusConfigList = modbusConfigService.selectShortListByProductId(modbusConfig);
        if (!CollectionUtils.isEmpty(modbusConfigList)){
            setModbusConfigCacheByProductId(productId);
        }
        return modbusConfigList;
    }

}
