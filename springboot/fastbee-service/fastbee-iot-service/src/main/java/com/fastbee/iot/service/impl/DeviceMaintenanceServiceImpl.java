package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import java.time.LocalDate;
import java.util.*;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.enums.MaintenanceStatusEnum;
import com.fastbee.common.extend.enums.MaintenanceTypeEnum;
import com.fastbee.common.extend.enums.MaintenanceUnitEnum;
import com.fastbee.common.extend.utils.DateConverterUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.DeviceMaintenanceConvert;
import com.fastbee.iot.domain.DeviceMaintenance;
import com.fastbee.iot.domain.WorkOrder;
import com.fastbee.iot.mapper.DeviceMaintenanceMapper;
import com.fastbee.iot.model.vo.DeviceMaintenanceVO;
import com.fastbee.iot.service.IDeviceMaintenanceService;
import com.fastbee.iot.service.IWorkOrderService;

/**
 * 设备维保Service业务层处理
 *
 * @author fastbee
 * @date 2025-12-25
 */
@Service
public class DeviceMaintenanceServiceImpl extends ServiceImpl<DeviceMaintenanceMapper,DeviceMaintenance> implements IDeviceMaintenanceService {

    @Resource
    private IWorkOrderService workOrderService;

    /**
     * 查询设备维保
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param deviceMaintenance 设备维保
     * @return 设备维保
     */
    @Override
    public DeviceMaintenance queryByIdWithCache(DeviceMaintenance deviceMaintenance){
        LambdaQueryWrapper<DeviceMaintenance> queryWrapper = this.buildQueryWrapper(deviceMaintenance);
        return this.getOne(queryWrapper);
    }

    /**
     * 查询设备维保
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 设备维保
     */
    @Override
    @Cacheable(cacheNames = "DeviceMaintenance", key = "#id")
    public DeviceMaintenance selectDeviceMaintenanceById(Long id){
        return this.getById(id);
    }

    /**
     * 查询设备维保分页列表
     *
     * @param deviceMaintenance 设备维保
     * @return 设备维保
     */
    @Override
    @DataScope
    public Page<DeviceMaintenanceVO> pageDeviceMaintenanceVO(DeviceMaintenance deviceMaintenance) {
        LambdaQueryWrapper<DeviceMaintenance> lqw = buildQueryWrapper(deviceMaintenance);
        lqw.orderByDesc(DeviceMaintenance::getCreateTime);
        Page<DeviceMaintenance> deviceMaintenancePage = baseMapper.selectPage(new Page<>(deviceMaintenance.getPageNum(), deviceMaintenance.getPageSize()), lqw);
        return DeviceMaintenanceConvert.INSTANCE.convertDeviceMaintenanceVOPage(deviceMaintenancePage);
    }

    /**
     * 查询设备维保列表
     *
     * @param deviceMaintenance 设备维保
     * @return 设备维保
     */
    @Override
    public List<DeviceMaintenanceVO> listDeviceMaintenanceVO(DeviceMaintenance deviceMaintenance) {
        LambdaQueryWrapper<DeviceMaintenance> lqw = buildQueryWrapper(deviceMaintenance);
        List<DeviceMaintenance> deviceMaintenanceList = baseMapper.selectList(lqw);
        return DeviceMaintenanceConvert.INSTANCE.convertDeviceMaintenanceVOList(deviceMaintenanceList);
    }

    private LambdaQueryWrapper<DeviceMaintenance> buildQueryWrapper(DeviceMaintenance query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<DeviceMaintenance> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, DeviceMaintenance::getId, query.getId());
                    lqw.like(StringUtils.isNotBlank(query.getName()), DeviceMaintenance::getName, query.getName());
                    lqw.like(StringUtils.isNotBlank(query.getDeviceName()), DeviceMaintenance::getDeviceName, query.getDeviceName());
                    lqw.eq(query.getStatus() != null, DeviceMaintenance::getStatus, query.getStatus());
                    lqw.eq(query.getMaintenanceType() != null, DeviceMaintenance::getMaintenanceType, query.getMaintenanceType());
                    lqw.eq(query.getDeviceId() != null, DeviceMaintenance::getDeviceId, query.getDeviceId());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(DeviceMaintenance::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))) {
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        return lqw;
    }

    /**
     * 新增设备维保
     *
     * @param add 设备维保
     * @return 是否新增成功
     */
    @Override
    public AjaxResult insertWithCache(DeviceMaintenance add) {
        String errorMsg = validEntityBeforeSave(add);
        if (StringUtils.isNotEmpty(errorMsg)) {
            return AjaxResult.error(errorMsg);
        }
        LoginUser loginUser = getLoginUser();
        add.setCreateBy(loginUser.getUsername());
        add.setTenantId(loginUser.getDeptUserId());
        if (loginUser.getUser().getDept() != null) {
            add.setTenantName(loginUser.getUser().getDept().getDeptName());
        }
        if (add.getStartMaintenanceTime().before(new Date())) {
            add.setStatus(MaintenanceStatusEnum.IN_PROGRESS.getStatus());
        } else {
            add.setStatus(MaintenanceStatusEnum.NO_STARTED.getStatus());
        }
        add.setMaintenanceType(MaintenanceTypeEnum.WITHIN_WARRANTY.getType());
        // 计算下次维保时间
        add.setNextMaintenanceTime(this.calculateNextMaintenanceTime(add, add.getStartMaintenanceTime()));
        boolean result = this.save(add);
        if (!result) {
            return  AjaxResult.error();
        }
        if (MaintenanceStatusEnum.IN_PROGRESS.getStatus().equals(add.getStatus())) {
            WorkOrder workOrder = workOrderService.generateDeviceMaintenanceWorkOrder(add);
            workOrderService.save(workOrder);
        }
        return AjaxResult.success();
    }

    /**
     * 修改设备维保
     *
     * @param update 设备维保
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "DeviceMaintenance", key = "#update.id")
    public AjaxResult updateWithCache(DeviceMaintenance update) {
        String errorMsg = validEntityBeforeSave(update);
        if (StringUtils.isNotEmpty(errorMsg)) {
            return AjaxResult.error(errorMsg);
        }
        LoginUser loginUser = getLoginUser();
        update.setUpdateBy(loginUser.getUsername());
        update.setNextMaintenanceTime(this.calculateNextMaintenanceTime(update, update.getStartMaintenanceTime()));
        if (MaintenanceStatusEnum.NO_STARTED.getStatus().equals(update.getStatus()) && update.getStartMaintenanceTime().before(new Date())) {
            update.setStatus(MaintenanceStatusEnum.IN_PROGRESS.getStatus());
        }
        boolean result = this.updateById(update);
        if (!result) {
            return  AjaxResult.error();
        }
        if (MaintenanceStatusEnum.IN_PROGRESS.getStatus().equals(update.getStatus())) {
            WorkOrder workOrder = workOrderService.generateDeviceMaintenanceWorkOrder(update);
            workOrderService.save(workOrder);
        }
        return AjaxResult.success();
    }

    /**
     * 保存前的数据校验
     */
    private String validEntityBeforeSave(DeviceMaintenance deviceMaintenance){
        // 做一些数据校验,如唯一约束
        Boolean result = this.compareTimeAndPeriodUnit(deviceMaintenance);
        if (result) {
            return "维保周期必须小于保内维保时间";
        }
        return "";
    }

    /**
     * 校验并批量删除设备维保信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "DeviceMaintenance", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        boolean result = this.removeByIds(Arrays.asList(ids));
        if (result) {
            workOrderService.removeDeviceMaintenanceWorkOrder(Arrays.asList(ids));
        }
        return result;
    }

    @Override
    public List<DeviceMaintenance> selectAllStratList() {
        LambdaQueryWrapper<DeviceMaintenance> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.le(DeviceMaintenance::getStartMaintenanceTime, new Date());
        queryWrapper.ne(DeviceMaintenance::getStatus, MaintenanceStatusEnum.STOPPED.getStatus());
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public void updateStatusByIds(List<Long> updateIdList, Integer status) {
        LambdaUpdateWrapper<DeviceMaintenance> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(DeviceMaintenance::getStatus, status);
        updateWrapper.in(DeviceMaintenance::getId, updateIdList);
        baseMapper.update(null, updateWrapper);
    }

    private Boolean compareTimeAndPeriodUnit(DeviceMaintenance deviceMaintenance) {
        Integer timeDays = MaintenanceUnitEnum.changeDays(deviceMaintenance.getMaintenanceTimeUnit(), deviceMaintenance.getMaintenanceTime());
        Integer periodDays = MaintenanceUnitEnum.changeDays(deviceMaintenance.getMaintenancePeriodUnit(), deviceMaintenance.getMaintenancePeriod());
        return periodDays >= timeDays;
    }

    /**
     * 计算下次维护时间
     *
     * @param add 添加
     * @return {@link Date }
     */
    @Override
    public Date calculateNextMaintenanceTime(DeviceMaintenance add, Date time) {
        LocalDate localDate = DateConverterUtils.convertToLocalDate(time);
        MaintenanceUnitEnum maintenanceUnitEnum = MaintenanceUnitEnum.getEnum(add.getMaintenancePeriodUnit());
        switch (Objects.requireNonNull(maintenanceUnitEnum)) {
            case DAY:
                localDate = localDate.plusDays(add.getMaintenancePeriod());
                break;
            case WEEK:
                localDate = localDate.plusWeeks(add.getMaintenancePeriod());
                break;
            case MONTH:
                localDate = localDate.plusMonths(add.getMaintenancePeriod());
                break;
            case YEAR:
                localDate = localDate.plusYears(add.getMaintenancePeriod());
                break;
            default:
                break;
        }
        return DateConverterUtils.convertToDate(localDate);
    }

}
