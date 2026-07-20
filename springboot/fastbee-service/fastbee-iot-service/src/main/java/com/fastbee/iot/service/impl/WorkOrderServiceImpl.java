package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.enums.ObjectOperationTypeEnum;
import com.fastbee.common.extend.enums.WorkOrderStatusEnum;
import com.fastbee.common.extend.enums.WorkOrderTypeEnum;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.WorkOrderConvert;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceMaintenance;
import com.fastbee.iot.domain.ObjectOperationLog;
import com.fastbee.iot.domain.WorkOrder;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.mapper.WorkOrderMapper;
import com.fastbee.iot.model.dto.WorkOrderDetailsDto;
import com.fastbee.iot.model.dto.WorkOrderResultDto;
import com.fastbee.iot.model.vo.AlertSceneSendVO;
import com.fastbee.iot.model.vo.WorkOrderVO;
import com.fastbee.iot.service.IObjectOperationLogService;
import com.fastbee.iot.service.IWorkOrderService;
import com.fastbee.system.mapper.SysUserMapper;

/**
 * 工单管理Service业务层处理
 *
 * @author fastbee
 * @date 2025-08-18
 */
@Service
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper,WorkOrder> implements IWorkOrderService {

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private IObjectOperationLogService objectOperationLogService;
    @Resource
    private WorkOrderMapper workOrderMapper;

    /**
     * 查询工单管理
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param workOrder 工单
     * @return 工单管理
     */
    @Override
    @Cacheable(cacheNames = "WorkOrder", key = "#id")
    @DataScope
    public WorkOrder queryByIdWithCache(WorkOrder workOrder){
        LambdaQueryWrapper<WorkOrder> queryWrapper = this.buildQueryWrapper(workOrder);
        return this.getOne(queryWrapper);
    }

    /**
     * 查询工单管理
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 工单管理
     */
    @Override
    @Cacheable(cacheNames = "WorkOrder", key = "#id")
    public WorkOrder selectWorkOrderById(Long id){
        return this.getById(id);
    }

    /**
     * 查询工单管理分页列表
     *
     * @param workOrderVO 工单管理
     * @return 工单管理
     */
    @Override
    @DataScope()
    public Page<WorkOrderVO> pageWorkOrderVO(WorkOrderVO workOrderVO) {
        Long userId = getUserId();
        WorkOrder workOrder = WorkOrderConvert.INSTANCE.convertWorkOrder(workOrderVO);
        if (MapUtils.isNotEmpty(workOrderVO.getParams())) {
            workOrder.setParams(workOrderVO.getParams());
        }
        LambdaQueryWrapper<WorkOrder> lqw = buildQueryWrapper(workOrder);
        if (Boolean.TRUE.equals(workOrderVO.getShowOwner())) {
            lqw.eq(WorkOrder::getUserId, userId);
        }
        lqw.orderByDesc(WorkOrder::getCreateTime);
        Page<WorkOrder> workOrderPage = baseMapper.selectPage(new Page<>(workOrder.getPageNum(), workOrder.getPageSize()), lqw);
        Page<WorkOrderVO> voPage = WorkOrderConvert.INSTANCE.convertWorkOrderVOPage(workOrderPage);
        if (0 == voPage.getTotal()) {
            return voPage;
        }
        List<WorkOrderVO> workOrderVOList = voPage.getRecords();
        List<Long> userIdList = workOrderVOList.stream().map(WorkOrderVO::getUserId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, SysUser> userMap = new HashMap<>(2);
        if (CollectionUtils.isNotEmpty(userIdList)) {
            LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(SysUser::getUserId, SysUser::getUserName, SysUser::getPhonenumber);
            queryWrapper.in(SysUser::getUserId, userIdList);
            List<SysUser> sysUserList = sysUserMapper.selectList(queryWrapper);
            userMap = sysUserList.stream().collect(Collectors.toMap(SysUser::getUserId, Function.identity()));
        }

        List<Long> deviceIdList = workOrderVOList.stream().map(WorkOrderVO::getDeviceId).distinct().collect(Collectors.toList());
        List<Device> deviceList = deviceMapper.selectDeviceListByDeviceIds(deviceIdList);
        Map<Long, Device> deviceMap = deviceList.stream().collect(Collectors.toMap(Device::getDeviceId, Function.identity()));

        for (WorkOrderVO vo : workOrderVOList) {
            SysUser sysUser = userMap.get(vo.getUserId());
            if (Objects.nonNull(sysUser)) {
                vo.setUserName(sysUser.getUserName());
                vo.setUserPhone(sysUser.getPhonenumber());
            }
            Device device = deviceMap.get(vo.getDeviceId());
            if (Objects.nonNull(device)) {
                vo.setDeviceName(device.getDeviceName());
                vo.setProductName(device.getProductName());
            }
            if (Objects.nonNull(vo.getUserId())) {
                vo.setCanReceived(vo.getUserId().equals(userId) && WorkOrderStatusEnum.ORDER_DISPATCHED.getStatus().equals(vo.getStatus()));
            } else {
                vo.setCanReceived(false);
            }
            vo.setCanEdit(!WorkOrderTypeEnum.USER_FEEDBACK.getType().equals(vo.getType())
                    && !WorkOrderTypeEnum.DEVICE_ALERT.getType().equals(vo.getType()) && WorkOrderStatusEnum.checkUpdate(vo.getStatus()));
            vo.setCanDelete(WorkOrderStatusEnum.checkDelete(vo.getStatus()));
        }
        return voPage;
    }

    /**
     * 查询工单管理列表
     *
     * @param workOrder 工单管理
     * @return 工单管理
     */
    @Override
    public List<WorkOrderVO> listWorkOrderVO(WorkOrder workOrder) {
        LambdaQueryWrapper<WorkOrder> lqw = buildQueryWrapper(workOrder);
        List<WorkOrder> workOrderList = baseMapper.selectList(lqw);
        return WorkOrderConvert.INSTANCE.convertWorkOrderVOList(workOrderList);
    }

    private LambdaQueryWrapper<WorkOrder> buildQueryWrapper(WorkOrder query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<WorkOrder> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getId() != null, WorkOrder::getId, query.getId());
                    lqw.like(StringUtils.isNotBlank(query.getName()), WorkOrder::getName, query.getName());
                    lqw.eq(StringUtils.isNotBlank(query.getDetails()), WorkOrder::getDetails, query.getDetails());
                    lqw.eq(query.getStatus() != null, WorkOrder::getStatus, query.getStatus());
                    lqw.eq(query.getType() != null, WorkOrder::getType, query.getType());
                    lqw.eq(StringUtils.isNotBlank(query.getNumber()), WorkOrder::getNumber, query.getNumber());
                    lqw.eq(query.getUserId() != null, WorkOrder::getUserId, query.getUserId());
                    lqw.eq(query.getDeviceId() != null, WorkOrder::getDeviceId, query.getDeviceId());
                    lqw.eq(query.getTenantId() != null, WorkOrder::getTenantId, query.getTenantId());
                    lqw.like(StringUtils.isNotBlank(query.getTenantName()), WorkOrder::getTenantName, query.getTenantName());
                    lqw.eq(query.getDelFlag() != null, WorkOrder::getDelFlag, query.getDelFlag());
                    lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), WorkOrder::getCreateBy, query.getCreateBy());
                    lqw.eq(query.getCreateTime() != null, WorkOrder::getCreateTime, query.getCreateTime());
                    lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), WorkOrder::getUpdateBy, query.getUpdateBy());
                    lqw.eq(query.getUpdateTime() != null, WorkOrder::getUpdateTime, query.getUpdateTime());
                    lqw.eq(StringUtils.isNotBlank(query.getRemark()), WorkOrder::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(WorkOrder::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        return lqw;
    }

    /**
     * 新增工单管理
     *
     * @param add 工单管理
     * @return 是否新增成功
     */
    @Override
    public WorkOrderVO insertWithCache(WorkOrder add) {
        validEntityBeforeSave(add);
        SysUser sysUser = getLoginUser().getUser();
        add.setTenantId(sysUser.getDept().getDeptUserId());
        add.setTenantName(sysUser.getDept().getDeptName());
        add.setCreateBy(sysUser.getUserName());
        add.setStatus(WorkOrderStatusEnum.ORDER_DISPATCHED.getStatus());
        add.setNumber(String.valueOf(System.currentTimeMillis()));
        boolean save = this.save(add);
        if (!save) {
            return null;
        }
        WorkOrderVO workOrderVO = baseMapper.selectVoById(add.getId());
        objectOperationLogService.insert(null, workOrderVO, add.getId(), ObjectOperationTypeEnum.WORK_ORDER.getType(), sysUser.getUserName());
        workOrderVO.setNotifyFlag(true);
        return workOrderVO;
    }

    /**
     * 修改工单管理
     *
     * @param workOrderVO 工单管理
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "WorkOrder", key = "#update.id")
    public WorkOrderVO updateWithCache(WorkOrderVO workOrderVO) {
        String username = getUsername();
        WorkOrderVO oldVo = baseMapper.selectVoById(workOrderVO.getId());
        WorkOrder workOrder = WorkOrderConvert.INSTANCE.convertWorkOrder(workOrderVO);
        workOrder.setUpdateBy(username);
        boolean b = this.updateById(workOrder);
        if (!b) {
            return null;
        }
        WorkOrderVO newVO = baseMapper.selectVoById(workOrderVO.getId());
        objectOperationLogService.insert(oldVo, newVO, workOrderVO.getId(), ObjectOperationTypeEnum.WORK_ORDER.getType(), username);
        workOrderVO.setNotifyFlag(!oldVo.getUserId().equals(newVO.getUserId()));
        return newVO;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(WorkOrder entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除工单管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "WorkOrder", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        boolean b = this.removeByIds(Arrays.asList(ids));
        if (b) {
            objectOperationLogService.deleteByObjectIdAndType(Arrays.asList(ids), ObjectOperationTypeEnum.WORK_ORDER.getType());
        }
        return b;
    }

    @Override
    public WorkOrderVO changeStatus(WorkOrderVO workOrderVO) {
        String username = getUsername();
        WorkOrderVO oldVo = baseMapper.selectVoById(workOrderVO.getId());
        WorkOrder workOrder = WorkOrderConvert.INSTANCE.convertWorkOrder(workOrderVO);
        workOrder.setUpdateBy(username);
        boolean b = this.updateById(workOrder);
        if (!b) {
            return null;
        }
        WorkOrderVO updateVo = baseMapper.selectVoById(workOrderVO.getId());
        objectOperationLogService.insert(oldVo, updateVo, workOrderVO.getId(), ObjectOperationTypeEnum.WORK_ORDER.getType(), username);
        updateVo.setNotifyFlag(WorkOrderStatusEnum.ORDER_DISPATCHED.getStatus().equals(updateVo.getStatus()));
        return updateVo;
    }

    @Override
    public AjaxResult endUserAdd(WorkOrderVO workOrderVO) {
        String username = getUsername();
        workOrderVO.setStatus(WorkOrderStatusEnum.PENDING.getStatus());
        workOrderVO.setType(WorkOrderTypeEnum.USER_FEEDBACK.getType());
        Device device = deviceMapper.selectById(workOrderVO.getDeviceId());
        workOrderVO.setTenantId(device.getTenantId());
        workOrderVO.setTenantName(device.getTenantName());
        WorkOrder workOrder = WorkOrderConvert.INSTANCE.convertWorkOrder(workOrderVO);
        workOrder.setNumber(String.valueOf(System.currentTimeMillis()));
        workOrder.setCreateBy(username);
        int insert = baseMapper.insert(workOrder);
        if (insert > 0) {
            objectOperationLogService.insert(null, workOrderVO, workOrder.getId(), ObjectOperationTypeEnum.WORK_ORDER.getType(), username);
        }
        return insert > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    @Override
    public WorkOrder endUserQuery(Long id) {
        LambdaQueryWrapper<WorkOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WorkOrder::getId, id);
        queryWrapper.select(WorkOrder::getName, WorkOrder::getStatus, WorkOrder::getResult);
        WorkOrder workOrder = baseMapper.selectOne(queryWrapper);
        if (Objects.isNull(workOrder)) {
            return null;
        }
        if (!workOrder.getCreateBy().equals(getUsername())) {
            return null;
        }
        return workOrder;
    }

    @Override
    public WorkOrder generateDeviceMaintenanceWorkOrder(DeviceMaintenance deviceMaintenance) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setDeviceMaintenanceId(deviceMaintenance.getId());
        workOrder.setName(deviceMaintenance.getName() + "工单");
        workOrder.setDeviceId(deviceMaintenance.getDeviceId());
        WorkOrderDetailsDto workOrderDetailsDto = new WorkOrderDetailsDto();
        workOrderDetailsDto.setDescription("设备维保自动生成工单");
        workOrder.setDetails(JSON.toJSONString(workOrderDetailsDto));
        workOrder.setStatus(WorkOrderStatusEnum.PENDING.getStatus());
        workOrder.setType(WorkOrderTypeEnum.ROUTINE_MAINTENANCE.getType());
        workOrder.setNumber(String.valueOf(System.currentTimeMillis()));
        workOrder.setTenantId(deviceMaintenance.getTenantId());
        workOrder.setTenantName(deviceMaintenance.getTenantName());
        workOrder.setCreateBy(deviceMaintenance.getCreateBy());
        return workOrder;
    }

    @Override
    public void removeDeviceMaintenanceWorkOrder(List<Long> deviceMaintenanceIds) {
        LambdaQueryWrapper<WorkOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(WorkOrder::getDeviceMaintenanceId, deviceMaintenanceIds);
        workOrderMapper.delete(queryWrapper);
    }

    @Override
    public void alertGenerateWorkOrder(AlertSceneSendVO alertSceneSendVO, Device device) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setName(alertSceneSendVO.getAlertName() + "工单");
        workOrder.setDeviceId(device.getDeviceId());
        WorkOrderDetailsDto workOrderDetailsDto = new WorkOrderDetailsDto();
        workOrderDetailsDto.setDescription("设备告警自动生成工单");
        workOrder.setDetails(JSON.toJSONString(workOrderDetailsDto));
        workOrder.setStatus(WorkOrderStatusEnum.PENDING.getStatus());
        workOrder.setType(WorkOrderTypeEnum.DEVICE_ALERT.getType());
        workOrder.setNumber(String.valueOf(System.currentTimeMillis()));
        workOrder.setTenantId(device.getTenantId());
        workOrder.setTenantName(device.getTenantName());
        workOrder.setCreateBy(alertSceneSendVO.getAlertCreateBy());
        WorkOrderVO workOrderVO = WorkOrderConvert.INSTANCE.convertWorkOrderVO(workOrder);
        workOrderMapper.insert(workOrder);
        objectOperationLogService.insert(null, workOrderVO, workOrder.getId(), ObjectOperationTypeEnum.WORK_ORDER.getType(), "设备告警");
    }

    /**
     * @description: 定时强制结单
     * @author zzy
     * @date 2025-08-26 15:36
     */
    public void timingMandatoryBilling() {
        LambdaQueryWrapper<WorkOrder> queryWrapper = new LambdaQueryWrapper<>();
        List<Integer> list = Arrays.asList(WorkOrderStatusEnum.ORDER_RECEIVED.getStatus(), WorkOrderStatusEnum.ORDER_DISPATCHED.getStatus());
        queryWrapper.in(WorkOrder::getStatus, list);
        queryWrapper.lt(WorkOrder::getEndTime, new Date());
        List<WorkOrder> workOrders = workOrderMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(workOrders)) {
            return;
        }
        List<ObjectOperationLog> logs = new ArrayList<>();
        for (WorkOrder workOrder : workOrders) {
            WorkOrderStatusEnum statusEnum = WorkOrderStatusEnum.getByStatus(workOrder.getStatus());
            ObjectOperationLog objectOperationLog = new ObjectOperationLog();
            objectOperationLog.setObjectId(workOrder.getId());
            objectOperationLog.setType(ObjectOperationTypeEnum.WORK_ORDER.getType());
            WorkOrderResultDto workOrderResultDto = new WorkOrderResultDto();
            if (WorkOrderTypeEnum.USER_FEEDBACK.getType().equals(workOrder.getType())) {
                workOrderResultDto.setDescription("用户反馈超时未处理，系统强制结单，请联系设备厂家处理");
            } else {
                workOrderResultDto.setDescription("超时未接单或者未结单，系统强制结单");
            }
            String resultJson = JSON.toJSONString(workOrderResultDto);
            assert statusEnum != null;
            objectOperationLog.setContent("工单状态：由 " + statusEnum.getDescription() + " 变更为 \"强制结单\"，处理结果：由 空 变更为 " + resultJson);
            objectOperationLog.setCreateBy("工单系统定时");
            logs.add(objectOperationLog);
            workOrder.setStatus(WorkOrderStatusEnum.MANDATORY_BILLING.getStatus());
            workOrder.setResult(resultJson);
        }
        boolean b = baseMapper.updateBatch(workOrders, workOrders.size());
        if (b) {
            objectOperationLogService.saveBatch(logs);
        }
    }

}
