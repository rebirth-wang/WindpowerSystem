package com.fastbee.iot.data.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.common.enums.OTAUpgrade;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.cache.IOtaTaskCache;
import com.fastbee.iot.data.service.IFirmwareTaskDetailService;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.Firmware;
import com.fastbee.iot.domain.FirmwareTask;
import com.fastbee.iot.domain.FirmwareTaskDetail;
import com.fastbee.iot.mapper.FirmwareMapper;
import com.fastbee.iot.mapper.FirmwareTaskDetailMapper;
import com.fastbee.iot.mapper.FirmwareTaskMapper;
import com.fastbee.iot.model.FirmwareTaskDetailInput;
import com.fastbee.iot.model.FirmwareTaskDetailOutput;
import com.fastbee.iot.model.FirmwareTaskDeviceStatistic;
import com.fastbee.iot.service.IDeviceService;

/**
 * 固件升级任务详细对象Service业务层处理
 *
 * @author kerwincui
 * @date 2024-08-18
 */
@Service
public class FirmwareTaskDetailServiceImpl extends ServiceImpl<FirmwareTaskDetailMapper,FirmwareTaskDetail> implements IFirmwareTaskDetailService {

    @Resource
    private FirmwareTaskDetailMapper firmwareTaskDetailMapper;

    @Resource
    private IOtaTaskCache otaTaskCache;

    @Resource
    private FirmwareTaskMapper firmwareTaskMapper;

    @Resource
    private FirmwareMapper firmwareMapper;

    @Resource
    private IDeviceService deviceService;

    /**
     * 查询固件升级任务详细对象列表
     *
     * @param firmwareTaskDetail 固件升级任务详细对象
     * @return 固件升级任务详细对象
     */
    @Override
    public Page<FirmwareTaskDetail> selectFirmwareTaskDetailList(FirmwareTaskDetail firmwareTaskDetail) {
        LambdaQueryWrapper<FirmwareTaskDetail> lqw = buildQueryWrapper(firmwareTaskDetail);
        return baseMapper.selectPage(new Page<>(firmwareTaskDetail.getPageNum(), firmwareTaskDetail.getPageSize()), lqw);
    }

    private LambdaQueryWrapper<FirmwareTaskDetail> buildQueryWrapper(FirmwareTaskDetail query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<FirmwareTaskDetail> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getTaskId() != null, FirmwareTaskDetail::getTaskId, query.getTaskId());
        lqw.eq(StringUtils.isNotBlank(query.getSerialNumber()), FirmwareTaskDetail::getSerialNumber, query.getSerialNumber());
        lqw.eq(query.getUpgradeStatus() != null, FirmwareTaskDetail::getUpgradeStatus, query.getUpgradeStatus());
        lqw.eq(StringUtils.isNotBlank(query.getDetailMsg()), FirmwareTaskDetail::getDetailMsg, query.getDetailMsg());
        lqw.eq(StringUtils.isNotBlank(query.getMessageId()), FirmwareTaskDetail::getMessageId, query.getMessageId());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(FirmwareTaskDetail::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 查询固件升级任务详细
     * @param taskId
     * @param serialNumber
     * @return
     */
    public FirmwareTaskDetail selectFirmwareTaskDetailByTaskIdAndSerialNumber(Long taskId, String serialNumber){
        LambdaQueryWrapper<FirmwareTaskDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FirmwareTaskDetail::getId,taskId);
        wrapper.eq(FirmwareTaskDetail::getSerialNumber,serialNumber);
        return baseMapper.selectOne(wrapper);
    }

    /**
     * 更新升级状态
     * @param taskId
     * @param serialNumber
     * @param otaUpgrade
     */
    @Override
    public void updateStatus(Long taskId, String serialNumber, OTAUpgrade otaUpgrade) {
        FirmwareTaskDetail taskDetail = new FirmwareTaskDetail();
        taskDetail.setUpgradeStatus(otaUpgrade.getStatus());
        taskDetail.setDetailMsg(otaUpgrade.getDes());
        taskDetail.setUpdateTime(DateUtils.getNowDate());
        QueryWrapper<FirmwareTaskDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("task_id", taskId);
        wrapper.eq("serial_number", serialNumber);
        baseMapper.update(taskDetail, wrapper);
        // 当升级成功时，更新设备固件版本
        if (otaUpgrade.getStatus() == 3) {
            FirmwareTask task = firmwareTaskMapper.selectById(taskId);
            if (task != null) {
                Firmware firmware = firmwareMapper.selectById(task.getFirmwareId());
                if (firmware != null) {
                    Device device = deviceService.selectDeviceBySerialNumber(serialNumber);
                    if (device != null && firmware.getVersion() != null && device.getFirmwareVersion() != null) {
                        // 比较版本号
                        if (firmware.getVersion().compareTo(device.getFirmwareVersion()) > 0) {
                            // 更新设备固件版本
                            Device updateDevice = new Device();
                            updateDevice.setDeviceId(device.getDeviceId());
                            updateDevice.setSerialNumber(serialNumber);
                            updateDevice.setFirmwareVersion(firmware.getVersion());
                            updateDevice.setUpdateTime(DateUtils.getNowDate());
                            deviceService.updateById(updateDevice);
                        }
                    }
                }
            }
        }
    }

    /**
     * 根据固件id查询下属设备列表
     * @param firmwareTaskDetailInput
     * @return
     */
    @Override
    public Page<FirmwareTaskDetailOutput> selectFirmwareTaskDetailListByFirmwareId(FirmwareTaskDetailInput firmwareTaskDetailInput) {
        Page<FirmwareTaskDetailOutput> resultPage = firmwareTaskDetailMapper.selectFirmwareTaskDetailListByFirmwareId(new Page<>(firmwareTaskDetailInput.getPageNum(),firmwareTaskDetailInput.getPageSize()), firmwareTaskDetailInput);
        if (0 == resultPage.getTotal()) {
            return new Page<>();
        }
        // 传入升级进度
        for (FirmwareTaskDetailOutput output : resultPage.getRecords()) {
            String statusCache = otaTaskCache.getOtaCacheValue(output.getSerialNumber(), "status");
            if (output.getUpgradeStatus() == 1 && StringUtils.isNotEmpty(statusCache)) {
                output.setProgress(Integer.parseInt(statusCache));
            }
            else if (output.getUpgradeStatus() == 2 || output.getUpgradeStatus() == 3) {
                output.setProgress(100);
            } else {
                output.setProgress(0);
            }
        }
        return resultPage;
    }

    /**
     * 固件升级设备统计
     * @param firmwareTaskDetailInput
     * @return
     */
    @Override
    public List<FirmwareTaskDeviceStatistic> deviceStatistic(FirmwareTaskDetailInput firmwareTaskDetailInput) {
        return firmwareTaskDetailMapper.deviceStatistic(firmwareTaskDetailInput);
    }

}
