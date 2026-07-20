package com.fastbee.iot.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.fastbee.common.enums.DeviceDistributeTypeEnum;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.mybatis.LambdaQueryWrapperX;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.DeviceRecordConvert;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceRecord;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.mapper.DeviceRecordMapper;
import com.fastbee.iot.mapper.ProductMapper;
import com.fastbee.iot.model.vo.DeviceRecordVO;
import com.fastbee.iot.service.IDeviceRecordService;
import com.fastbee.system.mapper.SysDeptMapper;

/**
 * 设备记录Service业务层处理
 *
 * @author zhangzhiyi
 * @date 2024-07-16
 */
@Service
public class DeviceRecordServiceImpl extends ServiceImpl<DeviceRecordMapper,DeviceRecord> implements IDeviceRecordService
{
    @Resource
    private DeviceRecordMapper deviceRecordMapper;
    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private DeviceMapper deviceMapper;

    /**
     * 查询设备记录
     *
     * @param id 设备记录主键
     * @return 设备记录
     */
    @Override
    public DeviceRecord selectDeviceRecordById(Long id)
    {
        return deviceRecordMapper.selectById(id);
    }

    @Override
    public Page<DeviceRecordVO> pageDeviceRecordVO(DeviceRecord deviceRecord) {
        LambdaQueryWrapper<DeviceRecord> queryWrapper = this.buildQueryWrapper(deviceRecord);
        queryWrapper.orderByDesc(DeviceRecord::getCreateTime);
        Page<DeviceRecord> deviceRecordPage = baseMapper.selectPage(new Page<>(deviceRecord.getPageNum(), deviceRecord.getPageSize()), queryWrapper);
        if (0 == deviceRecordPage.getTotal()) {
            return new Page<>();
        }
        Page<DeviceRecordVO> voPage = DeviceRecordConvert.INSTANCE.convertDeviceRecordVOPage(deviceRecordPage);
        List<DeviceRecordVO> deviceRecordVOList = voPage.getRecords();
        // 查租户、产品、设备信息
        List<Long> deptIdList = new ArrayList<>();
        deptIdList.addAll(deviceRecordVOList.stream().map(DeviceRecordVO::getOperateDeptId).distinct().collect(Collectors.toList()));
        deptIdList.addAll(deviceRecordVOList.stream().map(DeviceRecordVO::getTargetDeptId).distinct().collect(Collectors.toList()));
        Map<Long, SysDept> deptMap = new HashMap<>(2);
        if (CollectionUtils.isNotEmpty(deptIdList)) {
            List<SysDept> sysDeptList = sysDeptMapper.selectDeptByIds(deptIdList);
            deptMap = sysDeptList.stream().collect(Collectors.toMap(SysDept::getDeptId, Function.identity(), (o, n) -> n));
        }
        Map<Long, Product> productMap = new HashMap<>(2);
        List<Long> productIdList = deviceRecordVOList.stream().map(DeviceRecordVO::getProductId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(productIdList)) {
            List<Product> productList = productMapper.selectBatchIds(productIdList);
            productMap = productList.stream().collect(Collectors.toMap(Product::getProductId, Function.identity(), (o, n) -> n));
        }
        Map<Long, Device> deviceMap = new HashMap<>(2);
        List<Long> deviceIdList = deviceRecordVOList.stream().map(DeviceRecordVO::getDeviceId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deviceIdList)) {
            List<Device> deviceList = deviceMapper.selectDeviceListByDeviceIds(deviceIdList);
            deviceMap = deviceList.stream().collect(Collectors.toMap(Device::getDeviceId, Function.identity(), (o, n) -> n));
        }
        for (DeviceRecordVO deviceRecordVO : deviceRecordVOList) {
            SysDept operateDept = deptMap.get(deviceRecordVO.getOperateDeptId());
            if (null != operateDept) {
                deviceRecordVO.setOperateDeptName(operateDept.getDeptName());
            }
            SysDept targetDept = deptMap.get(deviceRecordVO.getTargetDeptId());
            if (null != targetDept) {
                deviceRecordVO.setTargetDeptName(targetDept.getDeptName());
            }
            Product product = productMap.get(deviceRecordVO.getProductId());
            if (null != product) {
                deviceRecordVO.setProductName(product.getProductName());
            }
            Device device = deviceMap.get(deviceRecordVO.getDeviceId());
            if (null != device) {
                deviceRecordVO.setDeviceName(device.getDeviceName());
            }
            if (null != deviceRecordVO.getDistributeType()) {
                deviceRecordVO.setDistributeTypeDesc(DeviceDistributeTypeEnum.getDesc(deviceRecordVO.getDistributeType()));
            }
        }
        return voPage;
    }

    /**
     * 查询设备记录列表
     *
     * @param deviceRecord 设备记录
     * @return 设备记录
     */
    @Override
    public List<DeviceRecordVO> selectDeviceRecordVOList(DeviceRecord deviceRecord)
    {
        LambdaQueryWrapper<DeviceRecord> queryWrapper = this.buildQueryWrapper(deviceRecord);
        List<DeviceRecord> deviceRecordList = deviceRecordMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(deviceRecordList)) {
            return new ArrayList<>();
        }
        return DeviceRecordConvert.INSTANCE.convertDeviceRecordVOList(deviceRecordList);
    }

    private LambdaQueryWrapperX<DeviceRecord> getListQueryWrapper(DeviceRecord deviceRecord) {
        LambdaQueryWrapperX<DeviceRecord> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eqIfPresent(DeviceRecord::getType, deviceRecord.getType());
        queryWrapperX.eqIfPresent(DeviceRecord::getProductId, deviceRecord.getProductId());
        queryWrapperX.eqIfPresent(DeviceRecord::getStatus, deviceRecord.getStatus());
        queryWrapperX.betweenIfPresent(DeviceRecord::getCreateTime, deviceRecord.getParams().get("beginTime"), deviceRecord.getParams().get("endTime"));
        queryWrapperX.eqIfPresent(DeviceRecord::getOperateDeptId, deviceRecord.getOperateDeptId());
        queryWrapperX.eqIfPresent(DeviceRecord::getSerialNumber, deviceRecord.getSerialNumber());
        return queryWrapperX;
    }

    /**
     * 新增设备记录
     *
     * @param deviceRecord 设备记录
     * @return 结果
     */
    @Override
    public int insertDeviceRecord(DeviceRecord deviceRecord)
    {
        deviceRecord.setCreateTime(DateUtils.getNowDate());
        return deviceRecordMapper.insert(deviceRecord);
    }

    /**
     * 修改设备记录
     *
     * @param deviceRecord 设备记录
     * @return 结果
     */
    @Override
    public int updateDeviceRecord(DeviceRecord deviceRecord)
    {
        return deviceRecordMapper.updateById(deviceRecord);
    }

    /**
     * 批量删除设备记录
     *
     * @param ids 需要删除的设备记录主键
     * @return 结果
     */
    @Override
    public int deleteDeviceRecordByIds(Long[] ids)
    {
        return deviceRecordMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除设备记录信息
     *
     * @param id 设备记录主键
     * @return 结果
     */
    @Override
    public int deleteDeviceRecordById(Long id)
    {
        return deviceRecordMapper.deleteById(id);
    }

    private LambdaQueryWrapper<DeviceRecord> buildQueryWrapper(DeviceRecord query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<DeviceRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getOperateDeptId() != null, DeviceRecord::getOperateDeptId, query.getOperateDeptId());
        lqw.eq(query.getTargetDeptId() != null, DeviceRecord::getTargetDeptId, query.getTargetDeptId());
        lqw.eq(query.getProductId() != null, DeviceRecord::getProductId, query.getProductId());
        lqw.eq(query.getDeviceId() != null, DeviceRecord::getDeviceId, query.getDeviceId());
        lqw.eq(StringUtils.isNotBlank(query.getSerialNumber()), DeviceRecord::getSerialNumber, query.getSerialNumber());
        lqw.eq(query.getParentId() != null, DeviceRecord::getParentId, query.getParentId());
        lqw.eq(query.getType() != null, DeviceRecord::getType, query.getType());
        lqw.eq(query.getDistributeType() != null, DeviceRecord::getDistributeType, query.getDistributeType());
        lqw.eq(query.getTotal() != null, DeviceRecord::getTotal, query.getTotal());
        lqw.eq(query.getSuccessQuantity() != null, DeviceRecord::getSuccessQuantity, query.getSuccessQuantity());
        lqw.eq(query.getFailQuantity() != null, DeviceRecord::getFailQuantity, query.getFailQuantity());
        lqw.eq(query.getStatus() != null, DeviceRecord::getStatus, query.getStatus());
        lqw.eq(query.getTenantId() != null, DeviceRecord::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), DeviceRecord::getTenantName, query.getTenantName());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(DeviceRecord::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DeviceRecord entity){
        //TODO 做一些数据校验,如唯一约束
    }
}
