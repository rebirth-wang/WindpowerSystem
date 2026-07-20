package com.fastbee.iot.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import com.fastbee.common.enums.FunctionReplyStatus;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.cache.ITSLCache;
import com.fastbee.iot.cache.ITSLValueCache;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.iot.model.ThingsModels.ValueItem;
import com.fastbee.iot.model.vo.FunctionLogVO;
import com.fastbee.iot.service.IDeviceRuntimeService;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.iot.service.IFunctionLogService;

/**
 * 设备运行时数据
 *
 * @author gsb
 * @date 2023/2/1 15:11
 */
@Service
public class DeviceRuntimeServiceImpl implements IDeviceRuntimeService {

    @Resource
    private IFunctionLogService logService;
    @Resource
    private IDeviceService deviceService;
    @Resource
    private ITSLCache itslCache;
    @Resource
    private ITSLValueCache itslValueCache;



    /**
     * 根据设备编号查询设备实时运行状态
     *
     * @return 设备实时数据
     */
    @Override
    public List<ThingsModelValueItem> runtimeBySerialNumber(String serialNumber) {
        if (StringUtils.isEmpty(serialNumber)) {
            return new ArrayList<>();
        }
        Device device = deviceService.selectDeviceBySerialNumber(serialNumber);
        if (device == null) {
            return new ArrayList<>();
        }
        Long productId = device.getProductId();
        List<ThingsModelValueItem> thingsModelList = itslCache.getThingsModelList(productId);
        if (thingsModelList == null || thingsModelList.isEmpty()) {
            return new ArrayList<>();
        }
        List<ValueItem> valueItemList = itslValueCache.getCacheDeviceStatus(productId, serialNumber);
        if (valueItemList == null) {
            valueItemList = new ArrayList<>();
        }
        for (ThingsModelValueItem modelValueItem : thingsModelList) {
            //组装数据
            ValueItem valueItem = valueItemList.stream()
                    .filter(v -> v != null && Objects.equals(v.getId(), modelValueItem.getId()))
                    .findFirst()
                    .orElse(new ValueItem());
            modelValueItem.setValue(valueItem.getValue());
            modelValueItem.setShadow(valueItem.getShadow());
            modelValueItem.setTs(valueItem.getTs());
        }
        return thingsModelList;
    }


    /**
     * 根据设备编号查询设备服务调用日志情况
     *
     * @param serialNumber 设备编号
     * @return 服务下发日志
     */
    @Override
    public Page<FunctionLogVO> runtimeReply(String serialNumber) {
        FunctionLogVO log = new FunctionLogVO();
        log.setSerialNumber(serialNumber);
        Page<FunctionLogVO> logList = logService.selectFunctionLogList(log);
        for (FunctionLogVO l : logList.getRecords()) {
            if (l.getReplyTime() == null) {
                l.setReplyTime(DateUtils.getNowDate());
            }

            l.setShowValue(l.getResultMsg());
            //设备超过10s未回复，则认为下发失败
            if (!Objects.isNull(l.getResultCode())) {
                if (l.getResultCode().equals(FunctionReplyStatus.NORELY.getCode()) && DateUtils.getTimestamp() - l.getCreateTime().getTime() > 10000) {
                    l.setShowValue(FunctionReplyStatus.FAIl.getMessage());
                    l.setResultCode(FunctionReplyStatus.FAIl.getCode());
                }
            }
        }
        return logList;
    }
}
