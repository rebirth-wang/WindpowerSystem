package com.fastbee.iot.data.job;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.fastbee.common.extend.enums.MaintenancePreTimeTypeEnum;
import com.fastbee.common.extend.enums.MaintenanceStatusEnum;
import com.fastbee.common.extend.enums.MaintenanceUnitEnum;
import com.fastbee.common.extend.enums.ObjectOperationTypeEnum;
import com.fastbee.common.extend.utils.DateConverterUtils;
import com.fastbee.iot.convert.WorkOrderConvert;
import com.fastbee.iot.domain.DeviceMaintenance;
import com.fastbee.iot.domain.ObjectOperationLog;
import com.fastbee.iot.domain.WorkOrder;
import com.fastbee.iot.model.vo.WorkOrderVO;
import com.fastbee.iot.service.IDeviceMaintenanceService;
import com.fastbee.iot.service.IObjectOperationLogService;
import com.fastbee.iot.service.IWorkOrderService;

/**
 * 设备维保定时任务
 *
 * @author fastbee
 * @date 2025/12/30
 */
@Slf4j
@Component
public class DeviceMaintenanceJob {

    @Resource
    private IDeviceMaintenanceService deviceMaintenanceService;
    @Resource
    private IWorkOrderService workOrderService;
    @Resource
    private IObjectOperationLogService objectOperationLogService;

    public void batchGenerateWorkOrder() {
        log.info("设备维保定时任务执行...");
        List<DeviceMaintenance> deviceMaintenanceList = deviceMaintenanceService.list();
        if (CollectionUtils.isEmpty(deviceMaintenanceList)) {
            log.info("设备维保定时任务执行未查询到任务");
            return;
        }
        CompletableFuture.runAsync(() -> {
            Date date = new Date();
            LocalDateTime now = LocalDateTime.now();
            List<DeviceMaintenance> editList = new ArrayList<>();
            List<WorkOrder> workOrderList = new ArrayList<>();
            List<ObjectOperationLog> objectOperationLogList = new ArrayList<>();
            for (DeviceMaintenance deviceMaintenance : deviceMaintenanceList) {
                if (MaintenanceStatusEnum.STOPPED.getStatus().equals(deviceMaintenance.getStatus())) {
                    continue;
                }

                // 未开始
                if (MaintenanceStatusEnum.NO_STARTED.getStatus().equals(deviceMaintenance.getStatus())) {
                    boolean generateWorkOrder = deviceMaintenance.getStartMaintenanceTime().before(date)
                            || (deviceMaintenance.getStartMaintenanceTime().after(date) && this.judgePreTime(deviceMaintenance, now, 1));
                    if (generateWorkOrder) {
                        deviceMaintenance.setStatus(MaintenanceStatusEnum.IN_PROGRESS.getStatus());
                        editList.add(deviceMaintenance);
                        WorkOrder workOrder = workOrderService.generateDeviceMaintenanceWorkOrder(deviceMaintenance);
                        workOrderList.add(workOrder);
                        WorkOrderVO workOrderVO = WorkOrderConvert.INSTANCE.convertWorkOrderVO(workOrder);
                        objectOperationLogList.add(objectOperationLogService.generateObjectOperationLog(null, workOrderVO, workOrder.getId(), ObjectOperationTypeEnum.WORK_ORDER.getType(), "设备维保"));
                    }
                }

                // 维保中
                if (MaintenanceStatusEnum.IN_PROGRESS.getStatus().equals(deviceMaintenance.getStatus())) {
                    if (1 == deviceMaintenance.getExpireStopFlag()) {
                        // 计算维保结束时间
                        Date stopTime = this.calculateStopMaintenanceTime(deviceMaintenance);
                        if (stopTime.before(date)) {
                            deviceMaintenance.setStatus(MaintenanceStatusEnum.STOPPED.getStatus());
                            editList.add(deviceMaintenance);
                            continue;
                        }
                    }

                    boolean generateWorkOrder = deviceMaintenance.getNextMaintenanceTime().before(date)
                            || (deviceMaintenance.getNextMaintenanceTime().after(date) && this.judgePreTime(deviceMaintenance, now, 2));
                    if (generateWorkOrder) {
                        // 计算下次维保时间
                        Date nextMaintenanceTime = deviceMaintenanceService.calculateNextMaintenanceTime(deviceMaintenance, deviceMaintenance.getNextMaintenanceTime());
                        deviceMaintenance.setNextMaintenanceTime(nextMaintenanceTime);
                        editList.add(deviceMaintenance);
                        WorkOrder workOrder = workOrderService.generateDeviceMaintenanceWorkOrder(deviceMaintenance);
                        workOrderList.add(workOrder);
                        WorkOrderVO workOrderVO = WorkOrderConvert.INSTANCE.convertWorkOrderVO(workOrder);
                        objectOperationLogList.add(objectOperationLogService.generateObjectOperationLog(null, workOrderVO, workOrder.getId(), ObjectOperationTypeEnum.WORK_ORDER.getType(), "设备维保"));
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(editList)) {
                deviceMaintenanceService.updateBatchById(editList);
            }
            if (CollectionUtils.isNotEmpty(workOrderList)) {
                workOrderService.saveBatch(workOrderList);
            }
            if (CollectionUtils.isNotEmpty(objectOperationLogList)) {
                objectOperationLogService.saveBatch(objectOperationLogList);
            }

        });
    }

    private Date calculateStopMaintenanceTime(DeviceMaintenance deviceMaintenance) {
        LocalDateTime stopTime = null;
        LocalDateTime startTime = DateConverterUtils.convertToLocalDateTime(deviceMaintenance.getStartMaintenanceTime());
        MaintenanceUnitEnum maintenanceUnitEnum = MaintenanceUnitEnum.getEnum(deviceMaintenance.getMaintenanceTimeUnit());
        switch (Objects.requireNonNull(maintenanceUnitEnum)) {
            case DAY:
                stopTime = startTime.plusDays(deviceMaintenance.getMaintenanceTime());
                break;
            case WEEK:
                stopTime = startTime.plusWeeks(deviceMaintenance.getMaintenanceTime());
                break;
            case MONTH:
                stopTime = startTime.plusMonths(deviceMaintenance.getMaintenanceTime());
                break;
            case YEAR:
                stopTime = startTime.plusYears(deviceMaintenance.getMaintenanceTime());
                break;
            default:
                break;
        }
        return DateConverterUtils.convertToDate(stopTime);
    }

    private Boolean judgePreTime(DeviceMaintenance deviceMaintenance, LocalDateTime now, Integer judgeType) {
        MaintenancePreTimeTypeEnum preTimeTypeEnum = MaintenancePreTimeTypeEnum.getByType(deviceMaintenance.getPreWorkTimeType());
        if (Objects.isNull(preTimeTypeEnum)) {
            return false;
        }
        LocalDateTime startTime;
        if (1 == judgeType) {
            startTime = DateConverterUtils.convertToLocalDateTime(deviceMaintenance.getStartMaintenanceTime());
        } else {
            startTime = DateConverterUtils.convertToLocalDateTime(deviceMaintenance.getNextMaintenanceTime());
        }
        switch (preTimeTypeEnum) {
            case OND_DAY:
                return startTime.minusDays(1).isBefore(now);
            case TWO_DAY:
                return startTime.minusDays(2).isBefore(now);
            case THREE_DAY:
                return startTime.minusDays(3).isBefore(now);
            case ONE_WEEK:
                return startTime.minusWeeks(1).isBefore(now);
            case TWO_WEEK:
                return startTime.minusWeeks(2).isBefore(now);
            case ONE_MONTH:
                return startTime.minusMonths(1).isBefore(now);
            default:
                return false;
        }
    }
}
