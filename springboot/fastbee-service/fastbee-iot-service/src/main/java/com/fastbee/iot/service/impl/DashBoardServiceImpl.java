package com.fastbee.iot.service.impl;

import java.lang.reflect.Field;
import java.util.*;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.DynamicBeanUtils;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceLog;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.model.HistoryBo;
import com.fastbee.iot.model.ThingsModels.ValueItem;
import com.fastbee.iot.model.dashBoard.DataCard;
import com.fastbee.iot.model.dashBoard.DeviceMatchBo;
import com.fastbee.iot.model.dashBoard.NameValue;
import com.fastbee.iot.model.dashBoard.Source;
import com.fastbee.iot.service.IDashBoardService;
import com.fastbee.iot.tsdb.service.ILogService;

/**
 * @author bill
 */
@Service
@Slf4j
public class DashBoardServiceImpl implements IDashBoardService {

    @Resource
    private RedisCache redisCache;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private ILogService logService;


    @Override
    public AjaxResult dashCombine(DeviceMatchBo bo) {
        Device device = deviceMapper.selectById(bo.getDeviceId());
        Optional.ofNullable(device).orElseThrow(() -> new ServiceException("设备id=" + bo.getDeviceId() + "不存在"));
        Integer type = bo.getType();
//        String identity = bo.getIdentifier();
        String[] identifiers = bo.getIdentifier();

        if (type == 5) {
            //查询实时状态值，从redis获取
            List<Map<String, Object>> results = new ArrayList<>();
            for (String identity : identifiers) {
                String cacheKey = RedisKeyBuilder.buildTSLVCacheKey(device.getProductId(), device.getSerialNumber());
                String cacheValue = redisCache.getCacheMapValue(cacheKey, identity);
                ValueItem valueItem = JSON.parseObject(cacheValue, ValueItem.class);

                Map<String, Object> item = new HashMap<>();
                item.put("identifier", identity);
                item.put("value", valueItem != null ? valueItem.getValue() : null);
                results.add(item);
            }
            return AjaxResult.success(results);
        } else {
            List<Map<String, Object>> historyResults = new ArrayList<>();
            String serialNumber = device.getSerialNumber();
            if (!Objects.isNull(bo.getSlaveId())) {
                serialNumber = serialNumber + "_" + bo.getSlaveId();
            }

            String beginTime = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, DateUtils.getDayBeginDate());
            String endTime = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, DateUtils.getDayEndDate());

            if (type == 4) {
                // type=4 合并处理
                Map<String, List<HistoryBo>> allDataMap = new HashMap<>();

                for (String identity : identifiers) {
                    DeviceLog deviceLog = new DeviceLog();
                    deviceLog.setSerialNumber(serialNumber);
                    deviceLog.setIdentify(identity);
                    deviceLog.setBeginTime(beginTime);
                    deviceLog.setEndTime(endTime);

                    List<HistoryBo> historyBoList = logService.selectHistorySingleBo(deviceLog);
                    allDataMap.put(identity, historyBoList);
                }

                // 统一处理 type=4 数据
                return dataChange(allDataMap);
            } else {
                // 其他类型仍按单属性处理
                for (String identity : identifiers) {
                    DeviceLog deviceLog = new DeviceLog();
                    deviceLog.setSerialNumber(serialNumber);
                    deviceLog.setIdentify(identity);
                    deviceLog.setBeginTime(beginTime);
                    deviceLog.setEndTime(endTime);

                    List<HistoryBo> historyBoList = logService.selectHistorySingleBo(deviceLog);
                    Map<String, Object> resultItem = new HashMap<>();
                    AjaxResult ajaxResult = dataChange(historyBoList, identity, bo.getType());
                    resultItem.put("data", ajaxResult.get("data"));
                    historyResults.add(resultItem);
                }
                if (type == 3) {
                    //单属性
                    return AjaxResult.success(historyResults.get(0).get("data"));
                } else if (type == 2) {
                    // 多属性 -> 合并为 NameValue 列表
                    Map<String, List<HistoryBo>> multiAttrData = new HashMap<>();
                    for (String identity : identifiers) {
                        DeviceLog deviceLog = new DeviceLog();
                        deviceLog.setSerialNumber(serialNumber);
                        deviceLog.setIdentify(identity);
                        deviceLog.setBeginTime(beginTime);
                        deviceLog.setEndTime(endTime);

                        List<HistoryBo> historyBoList = logService.selectHistorySingleBo(deviceLog);
                        multiAttrData.put(identity, historyBoList);
                    }

                    List<NameValue> mergedList = mergeMultiAttributeDataToNameValueList(multiAttrData);
                    return AjaxResult.success(mergedList);
                } else {
                    List<List<String>> mergedData = new ArrayList<>();

                    for (Map<String, Object> item : historyResults) {
                        @SuppressWarnings("unchecked")
                        List<List<String>> data = (List<List<String>>) item.get("data");
                        mergedData.addAll(data);
                    }

                    return AjaxResult.success(mergedData);
                }
            }
        }
    }


    private List<NameValue> mergeMultiAttributeDataToNameValueList(Map<String, List<HistoryBo>> allDataMap) {
        List<NameValue> result = new ArrayList<>();

        for (Map.Entry<String, List<HistoryBo>> entry : allDataMap.entrySet()) {
            String identify = entry.getKey();
            List<HistoryBo> list = entry.getValue();

            for (HistoryBo model : list) {
                NameValue nameValue = new NameValue();
                nameValue.setName(model.getIdentify());
                nameValue.setValue(model.getValue());
                result.add(nameValue);
            }
        }

        return result;
    }

    /**
     * 支持 type=4 的双属性图表
     */
    private static AjaxResult dataChange(Map<String, List<HistoryBo>> allDataMap) {
        DataCard dualDataCard = new DataCard();
        List<String> dimensions = new ArrayList<>();
        dimensions.add(DataCard.PRODUCT);
        dimensions.addAll(allDataMap.keySet());

        // 使用 String 时间作为 key，避免毫秒差异影响合并
        Map<String, Map<String, String>> mergedMap = new TreeMap<>();

        Set<String> identifiers = allDataMap.keySet();

        for (String identify : identifiers) {
            List<HistoryBo> list = allDataMap.get(identify);

            for (HistoryBo model : list) {
                // 统一格式："yy-MM-dd HH:mm:ss"
                String timeStr = DateUtils.dateTimeYY(model.getTimeField());

                mergedMap.computeIfAbsent(timeStr, k -> new HashMap<>())
                        .put(identify, model.getValue());
            }
        }

        // 构造 source 列表
        List<Map<String, Object>> sourceList = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> entry : mergedMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put(DataCard.PRODUCT, entry.getKey());

            for (String identifier : identifiers) {
                item.put(identifier, entry.getValue().getOrDefault(identifier, null));
            }

            sourceList.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dimensions", dimensions);
        result.put("source", sourceList);
        return AjaxResult.success(result);
    }

    /**
     * 数据转换
     *
     * @param list
     * @return
     */
    private static AjaxResult dataChange(List<HistoryBo> list, String identity, Integer type) {
        //    type : 1. 轮播列表  2.滚动排名列表  3.折线图/柱状图/饼状图-非环形（单个属性）  4.折线图/柱状图（两个属性）
        switch (type) {
            case 1:
                //1. 轮播列表
                List<List<String>> results = new ArrayList<>();
                for (HistoryBo model : list) {
                    List<String> subList = new ArrayList<>();
                    reflect(model, subList);
                    results.add(subList);
                }
                return AjaxResult.success(results);
            case 2:
                //2.滚动排名列表
                List<NameValue> nameValueList = new ArrayList<>();
                list.forEach(model -> {
                    NameValue nameValue = new NameValue();
                    nameValue.setName(model.getIdentify());
                    nameValue.setValue(model.getValue());
                    nameValueList.add(nameValue);
                });
                return AjaxResult.success(nameValueList);
            case 3:
                // 3.折线图/柱状图/饼状图-非环形（单个属性）
                DataCard dataCard = new DataCard();
                List<String> dimensions = new ArrayList<>();
                dimensions.add(DataCard.PRODUCT);
                dimensions.add(identity);
                dataCard.setDimensions(dimensions);
                List<Object> sourceList = new ArrayList<>();
                for (HistoryBo model : list) {
                    Source source = new Source();
                    source.setProduct(model.getTime());
                    Map<String,Object> map = new HashMap<>(2);
                    map.put(identity,model.getValue());
                    Object target = DynamicBeanUtils.getTarget(source, map);
                    sourceList.add(target);
                }
                dataCard.setSource(sourceList);
                return AjaxResult.success(dataCard);
            default:
                return AjaxResult.success();
        }
    }

    public static void reflect(HistoryBo e,List<String> result) {
        try {
            Class cls = e.getClass();
            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                Object o = f.get(e);
                if (o instanceof Date){
                    o = DateUtils.dateTimeYY((Date) o);
                }
                result.add(o + "");
            }
        }catch (Exception ex){
            throw new ServiceException(ex.getMessage());
        }
    }

}
