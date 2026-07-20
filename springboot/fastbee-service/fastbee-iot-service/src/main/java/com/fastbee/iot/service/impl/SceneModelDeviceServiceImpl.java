package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.enums.scenemodel.SceneModelVariableTypeEnum;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.SceneModelDeviceConvert;
import com.fastbee.iot.domain.*;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.mapper.SceneModelDataMapper;
import com.fastbee.iot.mapper.SceneModelDeviceMapper;
import com.fastbee.iot.mapper.ThingsModelMapper;
import com.fastbee.iot.model.ThingsModelItem.Datatype;
import com.fastbee.iot.model.vo.SceneModelDeviceVO;
import com.fastbee.iot.service.IDeviceJobService;
import com.fastbee.iot.service.ISceneModelDeviceService;

/**
 * 场景管理关联设备Service业务层处理
 *
 * @author kerwincui
 * @date 2024-05-21
 */
@Service
public class SceneModelDeviceServiceImpl extends ServiceImpl<SceneModelDeviceMapper,SceneModelDevice> implements ISceneModelDeviceService
{
    @Resource
    private SceneModelDeviceMapper sceneModelDeviceMapper;

    @Resource
    private SceneModelDataMapper sceneModelDataMapper;

    @Resource
    private DeviceMapper deviceMapper;

    @Resource
    private ThingsModelMapper thingsModelMapper;

    @Resource
    private IDeviceJobService jobService;


    /**
     * 查询场景管理关联设备
     *
     * @param id 场景管理关联设备主键
     * @return 场景管理关联设备
     */
    @Override
    public SceneModelDevice selectSceneModelDeviceById(Long id)
    {
        return sceneModelDeviceMapper.selectById(id);
    }

    /**
     * 查询场景管理关联设备分页列表
     *
     * @param sceneModelDevice 场景管理关联设备
     * @return 场景管理关联设备
     */
    @Override
    public Page<SceneModelDeviceVO> pageSceneModelDeviceVO(SceneModelDevice sceneModelDevice) {
        LambdaQueryWrapper<SceneModelDevice> lqw = buildQueryWrapper(sceneModelDevice);
        lqw.orderByAsc(SceneModelDevice::getSort).orderByDesc(SceneModelDevice::getCusDeviceId).orderByDesc(SceneModelDevice::getId);
        Page<SceneModelDevice> sceneModelDevicePage = baseMapper.selectPage(new Page<>(sceneModelDevice.getPageNum(), sceneModelDevice.getPageSize()), lqw);
        if (0 == sceneModelDevicePage.getTotal()) {
            return new Page<>();
        }
        Page<SceneModelDeviceVO> voPage = SceneModelDeviceConvert.INSTANCE.convertSceneModelDeviceVOPage(sceneModelDevicePage);
        List<SceneModelDeviceVO> sceneModelDeviceVOList = voPage.getRecords();
        this.getDeviceInfo(sceneModelDeviceVOList);
        return voPage;
    }

    private void getDeviceInfo(List<SceneModelDeviceVO> sceneModelDeviceVOList) {
        List<Long> deviceIdList = sceneModelDeviceVOList.stream().map(SceneModelDeviceVO::getCusDeviceId).filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(deviceIdList)) {
            return;
        }
        LambdaQueryWrapper<Device> deviceWrapper = new LambdaQueryWrapper<>();
        deviceWrapper.in(Device::getDeviceId, deviceIdList);
        deviceWrapper.select(Device::getDeviceId, Device::getProductId, Device::getSerialNumber, Device::getTenantId);
        List<Device> deviceList = deviceMapper.selectList(deviceWrapper);
        Map<Long, Device> deviceMap = deviceList.stream().collect(Collectors.toMap(Device::getDeviceId, Function.identity()));
        for (SceneModelDeviceVO sceneModelDeviceVO : sceneModelDeviceVOList) {
            Device device = deviceMap.get(sceneModelDeviceVO.getCusDeviceId());
            if (Objects.nonNull(device)) {
                sceneModelDeviceVO.setProductId(device.getProductId());
                sceneModelDeviceVO.setSerialNumber(device.getSerialNumber());
                sceneModelDeviceVO.setTenantId(device.getTenantId());
            }
        }
    }

    /**
     * 查询场景管理关联设备列表
     *
     * @param sceneModelDevice 场景管理关联设备
     * @return 场景管理关联设备
     */
    @Override
    public List<SceneModelDeviceVO> listSceneModelDeviceVO(SceneModelDevice sceneModelDevice) {
        LambdaQueryWrapper<SceneModelDevice> lqw = buildQueryWrapper(sceneModelDevice);
        List<SceneModelDevice> sceneModelDeviceList = baseMapper.selectList(lqw);
        if (CollectionUtils.isEmpty(sceneModelDeviceList)) {
            return new ArrayList<>();
        }
        List<SceneModelDeviceVO> sceneModelDeviceVOList = SceneModelDeviceConvert.INSTANCE.convertSceneModelDeviceVOList(sceneModelDeviceList);
        this.getDeviceInfo(sceneModelDeviceVOList);
        return sceneModelDeviceVOList;
    }

    /**
     * 新增场景管理关联设备
     *
     * @param sceneModelDevice 场景管理关联设备
     * @return 结果
     */
    @Override
    public int insertSceneModelDevice(SceneModelDevice sceneModelDevice)
    {
        SysUser user = getLoginUser().getUser();
        if (null != sceneModelDevice.getCusDeviceId()) {
            LambdaQueryWrapper<SceneModelDevice> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SceneModelDevice::getCusDeviceId, sceneModelDevice.getCusDeviceId());
            queryWrapper.eq(SceneModelDevice::getSceneModelId, sceneModelDevice.getSceneModelId());
            List<SceneModelDevice> sceneModelDeviceList = baseMapper.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(sceneModelDeviceList)) {
                throw new ServiceException(MessageUtils.message("sceneModel.scene.already.bind.device"));
            }
        }
        sceneModelDevice.setCreateTime(DateUtils.getNowDate());
        sceneModelDevice.setVariableType(SceneModelVariableTypeEnum.THINGS_MODEL.getType());
        sceneModelDevice.setAllEnable(1);
        sceneModelDevice.setSort(1);
        if (StringUtils.isEmpty(sceneModelDevice.getName())) {
            sceneModelDevice.setName(SceneModelVariableTypeEnum.THINGS_MODEL.getName());
        }
        sceneModelDevice.setCreateBy(user.getUserName());
        sceneModelDevice.setUpdateBy(user.getUserName());
        int result = sceneModelDeviceMapper.insert(sceneModelDevice);
        if (result <= 0) {
            return 0;
        }
        if (null != sceneModelDevice.getCusDeviceId()) {
            this.insertSceneModelData(sceneModelDevice);
        }
        return result;
    }

    /**
     * 修改场景管理关联设备
     *
     * @param sceneModelDevice 场景管理关联设备
     * @return 结果
     */
    @Override
    public int updateSceneModelDevice(SceneModelDevice sceneModelDevice)
    {
        // 校验是否有使用计算公式
        int count = sceneModelDeviceMapper.checkContainAliasFormule(sceneModelDevice.getId());
        if (count > 0) {
            throw new ServiceException(MessageUtils.message("sceneModel.update.fail.device.variable.has.quote.scene.variable.please.delete"));
        }
        SysUser user = getLoginUser().getUser();
        SceneModelDevice oldSceneModelDevice = this.selectSceneModelDeviceById(sceneModelDevice.getId());
        if (sceneModelDevice.getCusDeviceId().equals(oldSceneModelDevice.getCusDeviceId())) {
            sceneModelDevice.setUpdateTime(DateUtils.getNowDate());
            sceneModelDevice.setUpdateBy(user.getUserName());
            return sceneModelDeviceMapper.updateById(sceneModelDevice);
        }
        LambdaQueryWrapper<SceneModelDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SceneModelDevice::getCusDeviceId, sceneModelDevice.getCusDeviceId());
        queryWrapper.eq(SceneModelDevice::getSceneModelId, sceneModelDevice.getSceneModelId());
        List<SceneModelDevice> sceneModelDeviceList = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(sceneModelDeviceList)) {
            throw new ServiceException(MessageUtils.message("sceneModel.scene.already.bind.device"));
        }
        sceneModelDevice.setUpdateTime(DateUtils.getNowDate());
        int i = sceneModelDeviceMapper.updateById(sceneModelDevice);
        if (i <= 0) {
            return 0;
        }
        LambdaQueryWrapper<SceneModelData> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(SceneModelData::getSceneModelDeviceId, sceneModelDevice.getId());
        sceneModelDataMapper.delete(queryWrapper1);
        this.insertSceneModelData(sceneModelDevice);
        return i;
    }

    private void insertSceneModelData(SceneModelDevice sceneModelDevice) {
        Device device = deviceMapper.selectById(sceneModelDevice.getCusDeviceId());
        LambdaQueryWrapper<ThingsModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ThingsModel::getProductId, device.getProductId());
        List<ThingsModel> thingsModelList = thingsModelMapper.selectList(queryWrapper);
        List<SceneModelData> sceneModelDataList = new ArrayList<>();
        for (ThingsModel model : thingsModelList) {
            SceneModelData sceneModelData = new SceneModelData();
            sceneModelData.setEnable(1);
            sceneModelData.setSceneModelDeviceId(sceneModelDevice.getId());
            sceneModelData.setDatasourceId(model.getModelId());
            sceneModelData.setVariableType(SceneModelVariableTypeEnum.THINGS_MODEL.getType());
            sceneModelData.setSceneModelId(sceneModelDevice.getSceneModelId());
            sceneModelData.setSourceName(model.getModelName());
            sceneModelData.setIdentifier(model.getIdentifier());
            sceneModelData.setType(model.getType());
            Datatype datatype = JSON.parseObject(model.getSpecs(), Datatype.class);
            sceneModelData.setUnit(datatype.getUnit());
            sceneModelDataList.add(sceneModelData);
        }
        if (CollectionUtils.isNotEmpty(sceneModelDataList)) {
            sceneModelDataMapper.insertBatch(sceneModelDataList);
        }
    }

    /**
     * 批量删除场景管理关联设备
     *
     * @param ids 需要删除的场景管理关联设备主键
     * @return 结果
     */
    @Override
    public int deleteSceneModelDeviceByIds(Long[] ids)
    {
        // 校验是否有使用计算公式
        int count = sceneModelDeviceMapper.checkContainAliasFormule(ids[0]);
        if (count > 0) {
            throw new ServiceException(MessageUtils.message("sceneModel.delete.fail.device.variable.has.quote.scene.variable.please.delete"));
        }
        int i = sceneModelDeviceMapper.deleteBatchIds(Arrays.asList(ids));
        if (i > 0) {
            LambdaQueryWrapper<SceneModelData> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SceneModelData::getSceneModelDeviceId, Arrays.asList(ids));
            sceneModelDataMapper.delete(queryWrapper);
        }
        return i;
    }

    /**
     * 删除场景管理关联设备信息
     *
     * @param id 场景管理关联设备主键
     * @return 结果
     */
    @Override
    public int deleteSceneModelDeviceById(Long id)
    {
        return sceneModelDeviceMapper.deleteById(id);
    }

    @Override
    public int editEnable(SceneModelDevice sceneModelDevice) {
        LambdaUpdateWrapper<SceneModelDevice> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SceneModelDevice::getSceneModelId, sceneModelDevice.getSceneModelId());
        updateWrapper.eq(SceneModelDevice::getVariableType, sceneModelDevice.getVariableType());
        if (null != sceneModelDevice.getId()) {
            updateWrapper.eq(SceneModelDevice::getId, sceneModelDevice.getId());
        }
        updateWrapper.set(SceneModelDevice::getAllEnable, sceneModelDevice.getAllEnable());
        boolean update = this.update(updateWrapper);
        if (update) {
            LambdaUpdateWrapper<SceneModelData> sceneModelDataLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            sceneModelDataLambdaUpdateWrapper.set(SceneModelData::getEnable, sceneModelDevice.getAllEnable());
            sceneModelDataLambdaUpdateWrapper.eq(SceneModelData::getSceneModelId, sceneModelDevice.getSceneModelId());
            sceneModelDataLambdaUpdateWrapper.eq(SceneModelData::getVariableType, sceneModelDevice.getVariableType());
            if (null != sceneModelDevice.getId()) {
                sceneModelDataLambdaUpdateWrapper.eq(SceneModelData::getSceneModelDeviceId, sceneModelDevice.getId());
            }
            sceneModelDataMapper.update(null, sceneModelDataLambdaUpdateWrapper);
            if (SceneModelVariableTypeEnum.OPERATION_VARIABLE.getType().equals(sceneModelDevice.getVariableType())) {
                LambdaQueryWrapper<SceneModelData> sceneModelDataWrapper = new LambdaQueryWrapper<>();
                sceneModelDataWrapper.eq(SceneModelData::getSceneModelId, sceneModelDevice.getSceneModelId());
                sceneModelDataWrapper.eq(SceneModelData::getVariableType, sceneModelDevice.getVariableType());
                List<SceneModelData> sceneModelDataList = sceneModelDataMapper.selectList(sceneModelDataWrapper);
                Long[] datasourceId = sceneModelDataList.stream().map(SceneModelData::getDatasourceId).toArray(s -> new Long[s]);
                List<DeviceJob> deviceJobList = jobService.selectListByJobTypeAndDatasourceIds(datasourceId, 4);
                try {
                    for (DeviceJob job : deviceJobList) {
                        DeviceJob deviceJob = new DeviceJob();
                        deviceJob.setJobId(job.getJobId());
                        deviceJob.setJobGroup(job.getJobGroup());
                        deviceJob.setStatus(1 == sceneModelDevice.getAllEnable() ? 0 : 1);
                        jobService.changeStatus(deviceJob);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return update ? 1 : 0;
    }

    @Override
    public SceneModelDevice selectOneBySceneModelIdAndVariableType(Long sceneModelId, Integer variableType) {
        LambdaQueryWrapper<SceneModelDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SceneModelDevice::getSceneModelId, sceneModelId);
        queryWrapper.eq(SceneModelDevice::getVariableType, variableType);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public void deleteBySceneModelIds(List<Long> sceneModelIdList) {
        LambdaQueryWrapper<SceneModelDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SceneModelDevice::getSceneModelId, sceneModelIdList);
        baseMapper.delete(queryWrapper);
    }

    @Override
    public void phyDeleteBySceneModelIds(List<Long> sceneModelIds) {
        sceneModelDeviceMapper.phyDeleteBySceneModelIds(sceneModelIds);
    }

    @Override
    public int restoreBySceneModelId(Long sceneModelId) {
        return sceneModelDeviceMapper.restoreBySceneModelId(sceneModelId);
    }

    private LambdaQueryWrapper<SceneModelDevice> buildQueryWrapper(SceneModelDevice query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SceneModelDevice> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, SceneModelDevice::getId, query.getId());
        lqw.eq(query.getSceneModelId() != null, SceneModelDevice::getSceneModelId, query.getSceneModelId());
        lqw.eq(query.getCusDeviceId() != null, SceneModelDevice::getCusDeviceId, query.getCusDeviceId());
        lqw.eq(query.getSort() != null, SceneModelDevice::getSort, query.getSort());
        lqw.eq(StringUtils.isNotBlank(query.getDelFlag()), SceneModelDevice::getDelFlag, query.getDelFlag());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), SceneModelDevice::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, SceneModelDevice::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), SceneModelDevice::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, SceneModelDevice::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), SceneModelDevice::getRemark, query.getRemark());
        lqw.eq(query.getVariableType() != null, SceneModelDevice::getVariableType, query.getVariableType());
        lqw.eq(query.getAllEnable() != null, SceneModelDevice::getAllEnable, query.getAllEnable());
        lqw.like(StringUtils.isNotBlank(query.getName()), SceneModelDevice::getName, query.getName());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SceneModelDevice::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }
}
